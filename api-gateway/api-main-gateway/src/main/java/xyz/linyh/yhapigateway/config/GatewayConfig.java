package xyz.linyh.yhapigateway.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import xyz.linyh.yhapigateway.service.RouteService;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

/**
 * gateway相关配置
 * @author lin
 */
@Configuration
public class GatewayConfig {

    @Autowired
    private RouteService routeService;

    /**
     * 项目启动自动初始化所有路由信息到hashmap缓存中
     * @return
     */
    @Bean
    public CommandLineRunner getRoutesBeforeStartup(){
        return (String... args)->{
            routeService.getRouteDefinitions();
        };
    }

    /**
     * 访问这些路由就会调用里面的方法
     * @return
     */
    @Bean
    public RouterFunction<ServerResponse> gwRoute() {
        return RouterFunctions
                .route(GET("/yhapi/routes").and(accept(APPLICATION_JSON)), routeService::updateInterfaceCache)
//                .andRoute(GET("/yhapi/routes").and(accept(APPLICATION_JSON)), routeService::queryAll)
//                .andRoute(GET("/yhapi/routes/{routeId}").and(accept(APPLICATION_JSON)), routeService::queryOne)
//                .andRoute(DELETE("/yhapi/routes/{routeId}").and(accept(APPLICATION_JSON)), routeService::delete)
////                .andRoute(GET("/gw-mgr/routes-all").and(accept(APPLICATION_JSON)), routeService::queryALL)
                ;
    }

    /**
     * 最基础的路由拦截规则
     * @param builder
     * @return
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("route_1", r -> r.path("/test").uri("https://localhost:8848/nacos"))
                .build();
    }
}
