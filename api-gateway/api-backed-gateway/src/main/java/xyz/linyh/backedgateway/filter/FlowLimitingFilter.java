package xyz.linyh.backedgateway.filter;

import com.alibaba.nacos.api.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import xyz.linyh.backedgateway.utils.RedisUtil;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class FlowLimitingFilter implements Ordered, GlobalFilter {

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 用来保存每一个ip访问的次数
     */
    private final ConcurrentHashMap<String, VisitInfo> requestCounts = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 获取用户ip地址
        String ipAddress = getClientIpAddress(exchange.getRequest());
//        如果不是通过nginx转发的，直接不能访问
        if(ipAddress==null){
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            return response.setComplete();
        }
//        log.info("{} ip请求请求了网站",ipAddress);

        // 2. 如果有这个对应的ip就直接获取，如果没有就初始化
        VisitInfo visitInfo = requestCounts.computeIfAbsent(ipAddress, k -> new VisitInfo());

        // 每一个ip一秒只能请求10次
        if (visitInfo.incrementAndCheckLimit()) {
            log.info("{}请求次数超过了限制--------",ipAddress);
            System.out.println("有超出次数的"+ipAddress);
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            return response.setComplete();
        }
        log.info("{}请求次数没有超过限制",ipAddress);

//        增加redis的请求次数
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        redisUtil.addDailyVisitCount(LocalDate.now().format(format),ipAddress);


        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private String getClientIpAddress(ServerHttpRequest request) {
        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");

//        log.info("用户请求的地址为{}", xForwardedFor);
        if (!StringUtils.isBlank(xForwardedFor)) {
            int index = xForwardedFor.indexOf(',');
            if (index != -1) {
                return xForwardedFor.substring(0, index);
            }
        }
        return xForwardedFor;
    }

    private static class VisitInfo {
        private AtomicInteger count = new AtomicInteger(0);
        private Instant lastVisitTime = Instant.now();

        public synchronized boolean incrementAndCheckLimit() {
            // 如果距离上次访问时间超过一秒，重置计数
            if (Duration.between(lastVisitTime, Instant.now()).getSeconds() >= 1) {
                count.set(0);
                lastVisitTime = Instant.now();
            }

            // 执行自增并检查限制
            return count.incrementAndGet() > 10;
        }
    }
}
