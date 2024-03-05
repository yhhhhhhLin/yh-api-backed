package xyz.linyh.backedgateway.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import xyz.linyh.backedgateway.utils.JwtUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 用来判断用户是否登录的校验
 */
@Component
@Slf4j
public class AuthorizeFilter implements Ordered, GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpResponse response = exchange.getResponse();
//        支付宝的回调不需要认证，后面限流可能还需要判断支付宝的地址
        List<String> whiteList = new ArrayList<>(Arrays.asList("/pay/order/url/notify"));
//        判断是否是登录页面，注册页面..，如果是登录页面，那么不用鉴权
        String path = exchange.getRequest().getURI().getPath();
        System.out.println("----------------------------------------"+path+"-----------------------------------------");
        if(path!=null && (path.contains("login") && !path.contains("get") || path.contains("register"))){
            return chain.filter(exchange);
        }
//        如果是白名单的路径，那么可以直接访问
        if(whiteList.contains(path)){
            return chain.filter(exchange);
        }

        if(path!=null && (path.contains("gpt/connect") )){
            return chain.filter(exchange);
        }


//        获取传递的token，然后解析出来保存到session中
        String token = exchange.getRequest().getHeaders().getFirst("token");

        boolean isValidate = JwtUtils.validateToken(token);

        if(!isValidate){
            return unauthorizedResponse(response);
        }
        DecodedJWT decodedJWT = JwtUtils.parseToken(token);

        String userId = decodedJWT.getSubject();
        if(userId == null ){
            return unauthorizedResponse(response);
        }

//        将用户id保存到请求头中
        ServerHttpRequest newRequest = exchange.getRequest().mutate().header("userId", userId.toString()).build();
        log.info("{} id通过了请求token认证",userId);

        exchange.mutate().request(newRequest);
//        验证成功，执行下一个filter
        return chain.filter(exchange);

    }

    public Mono<Void> unauthorizedResponse(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

    /**
     * 用来指定这个filter优先级，越小优先级越高
     * @return
     */
    @Override
    public int getOrder() {
        return 1;
    }
}
