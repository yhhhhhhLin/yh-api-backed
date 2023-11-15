package xyz.linyh.yhapigateway.filters;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.nacos.shaded.com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import xyz.linyh.dubboapi.service.DubboInterfaceinfoService;
import xyz.linyh.dubboapi.service.DubboUserService;
import xyz.linyh.dubboapi.service.DubboUserinterfaceinfoService;
import xyz.linyh.ducommon.common.ErrorCode;
import xyz.linyh.ducommon.exception.BusinessException;
import xyz.linyh.model.interfaceinfo.entitys.Interfaceinfo;
import xyz.linyh.model.user.entitys.User;
import xyz.linyh.yapiclientsdk.utils.MyDigestUtils;
import xyz.linyh.yhapigateway.service.Impl.RouteServiceImpl;
import xyz.linyh.yhapigateway.utils.RedissonLockUtil;

import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR;

/**
 * @author lin
 */
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE)
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    @DubboReference
    private DubboUserinterfaceinfoService dubboUserinterfaceinfoService;

    @DubboReference
    private DubboInterfaceinfoService dubboInterfaceinfoService;

    @Resource
    private RouteServiceImpl routeServiceImpl;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpResponse response = exchange.getResponse();

//        3. 统一日志
        ServerHttpRequest request = exchange.getRequest();

        HttpHeaders headers = request.getHeaders();
        String uri = headers.getFirst("uri");

        String path = request.getPath().value();
        String method = request.getMethod().toString();

        log.info("请求地址:{}",path);
        log.info("请求参数:{}",request.getQueryParams());
        log.info("请求方法:{}",request.getMethod());
        log.info("请求用户地址:{}",request.getRemoteAddress());
        log.info("请求体:{}",request.getBody());


//        4. todo 设置请求黑白名单

//        5. 统一鉴权
        String sign = headers.getFirst("sign");
        String timeS = headers.getFirst("timeS");
        String accessKey = headers.getFirst("accessKey");
        String randomNum = headers.getFirst("randomNum");
        if(StrUtil.hasBlank(sign,timeS,accessKey,randomNum)){
            log.info("缺少认证参数");
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.setComplete();
        }

        User user = null;
        try {
            user = dubboUserService.getUserByAk(accessKey);
        } catch (Exception e) {
            throw new RuntimeException("gateway获取用户失败");
        }
        if(user == null){
            throw new RuntimeException("gateway获取用户失败");
        }
        try {
//            api签名认证
            signAuth(sign,timeS,user);
        } catch (Exception e) {
            response.setStatusCode(HttpStatus.FORBIDDEN);
            log.info("签名认证失败");
            return response.setComplete();
        }

//        获取所有接口
        Map<String, Interfaceinfo> URIAndInterface = routeServiceImpl.getRoutes();

        if(URIAndInterface==null || URIAndInterface.size()<=0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }

//        6. 判断请求接口是否存在
        Interfaceinfo mapInterface = URIAndInterface.get(uri);

        if(mapInterface==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"无法获取接口");
        }


//         todo 判断是否还有调用次数 因为可以直接通过sdk调用方法，可以绕过前面的backed


//        7. 转发到对应的接口
        Mono<Void> filter = chain.filter(exchange);

        return handleResponse(exchange,chain,mapInterface,user);
//        这个是异步的方法，需要全部过滤都结束才会转发到对应的服务上 所以无法通过filter来获取响应结果


    }




    @Autowired
    private RedissonLockUtil redissonLockUtil;
    private static Joiner joiner = Joiner.on("");
    /**
     * 处理异步发送请求无法获取响应值
     * @param exchange
     * @param chain
     * @return
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, Interfaceinfo interfaceinfo, User user) {
        ServerHttpResponse originalResponse = exchange.getResponse();
        // 保存数据的工厂
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();
        // 增强响应对象
        ServerHttpResponseDecorator response = new ServerHttpResponseDecorator(originalResponse) {
            // 响应调用完才会执行
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (getStatusCode().equals(HttpStatus.OK) && body instanceof Flux) {
                    // 获取ContentType，判断是否返回JSON格式数据
                    String originalResponseContentType = exchange.getAttribute(ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR);
                    if (StrUtil.isNotBlank(originalResponseContentType) ) {
                        Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                        //（返回数据内如果字符串过大，默认会切割）解决返回体分段传输
                        // 往返回值里面写数据
                        return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                            List<String> list = new ArrayList<>();
                            dataBuffers.forEach(dataBuffer -> {
                                try {
                                    byte[] content = new byte[dataBuffer.readableByteCount()];
                                    dataBuffer.read(content);
                                    DataBufferUtils.release(dataBuffer);
                                    list.add(new String(content, "utf-8"));
                                } catch (Exception e) {
                                    log.info("加强response出现错误------------");
                                }
                            });

                            String responseData = joiner.join(list);

//                            8. 响应日志
                            log.info("调用成功次数加1");

//                            9. 次数++(加锁)
                            try {
//                                redis分布式锁上锁
                                redissonLockUtil.redissonDistributedLocks(("gateway_" + user.getUserAccount()).intern(), () -> {
                                    dubboUserinterfaceinfoService.invokeOk(interfaceinfo.getId(), user.getId());
                                }, "接口调用失败");
//                                dubboUserinterfaceinfoService.invokeOk(interfaceInfoId,userId);
                            } catch (Exception e) {
                                throw new RuntimeException("gateway调用次数修改失败");
                            }

                            System.out.println("responseData："+responseData);


                            byte[] uppedContent = new String(responseData.getBytes(), Charset.forName("UTF-8")).getBytes();
                            originalResponse.getHeaders().setContentLength(uppedContent.length);
                            return bufferFactory.wrap(uppedContent);
                        }));
                    }
                }
                return super.writeWith(body);
            }

            @Override
            public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
                return writeWith(Flux.from(body).flatMapSequential(p -> p));
            }
        };
        return chain.filter(exchange.mutate().response(response).build());
    }

    @DubboReference
    private DubboUserService dubboUserService;

    /**
     * api签名认证
     * @param sign
     * @param timeS
     * @param user
     * @return
     */
    Boolean signAuth(String sign,String timeS,User user){

        if(user==null){
            throw new RuntimeException(ErrorCode.NO_AUTH_ERROR.getMessage());
        }
        String secretKey = user.getSecretKey();
//        通过加密算法加密生成然后和sign比较
//        认证生成签名
        if(!sign.equals(MyDigestUtils.getDigest(secretKey))){
            throw new RuntimeException("签名认证不通过");
        }

//        判断时间是否超出 获取5分钟后的时间戳
        Long nowTime = DateUtil.date().toTimestamp().getTime()/1000;
        Long time = Long.valueOf(timeS);
        if((nowTime-time)>5*30){
            throw new RuntimeException("超出时间");
        }
        return true;
    }

    @Override
    public int getOrder() {
        return -1;
    }
}