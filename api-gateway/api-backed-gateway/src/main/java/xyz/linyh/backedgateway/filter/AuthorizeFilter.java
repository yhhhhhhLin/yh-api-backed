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

/**
 * 用来判断用户是否登录的校验
 */
@Component
@Slf4j
public class AuthorizeFilter implements Ordered, GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpResponse response = exchange.getResponse();
//        判断是否是登录页面，如果是登录页面，那么不用鉴权
        String path = exchange.getRequest().getURI().getPath();
        if(path!=null && (path.contains("login") && !path.contains("get") || path.contains("register"))){
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
