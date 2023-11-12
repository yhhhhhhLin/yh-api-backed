package xyz.linyh.yhapigateway.service.Impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import xyz.linyh.dubboapi.service.DubboInterfaceinfoService;
import xyz.linyh.model.interfaceinfo.entitys.Interfaceinfo;
import xyz.linyh.yhapigateway.service.RouteService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

/**
 * 用来对数据库里面的路由进行操作
 * @author lin
 */
@Component
@Slf4j
public class RouteServiceImpl implements RouteService {

    @DubboReference
    private DubboInterfaceinfoService dubboInterfaceinfoService;

    /**
     * 本地缓存，获取到的路由信息会保存到这个hashmap中
     * key为路由id（对应接口id）value为路由的详细
     */
    private final Map<String, Interfaceinfo> routes = new ConcurrentHashMap<>(256);



    /**
     * 获取数据库里面的所有路由信息
     */
    @Override
    public Map<String,Interfaceinfo> getRouteDefinitions() {
        if(routes.size()>0){
            return routes;

        }
        log.info("开始从数据库初始化interface路由信息");
        List<Interfaceinfo> interfaceinfos = dubboInterfaceinfoService.getAllInterface();
//        将路由信息保存到hashmap中
        for (Interfaceinfo interfaceinfo : interfaceinfos) {
            routes.put(String.valueOf(interfaceinfo.getUri()),interfaceinfo);
        }
        return routes;
    }


    @Override
    public Map<String,Interfaceinfo> getRoutes(){
        return routes;
    }

//    如果后端服务对路由进行修改，调用这里面的接口实现修改路由中缓存数据
    /**
     * 后端新增了接口，gateway需要重新刷新缓存中的接口
     *
     * @param serverRequest
     * @return
     */
    @Override
    public Mono<ServerResponse> updateInterfaceCache(ServerRequest serverRequest) {
//        刷新的缓存中的接口数据
        log.info("刷新的缓存中的接口数据");
        routes.clear();
        List<Interfaceinfo> interfaceinfos = dubboInterfaceinfoService.getAllInterface();
//        将路由信息保存到hashmap中
        for (Interfaceinfo interfaceinfo : interfaceinfos) {
            routes.put(String.valueOf(interfaceinfo.getUri()),interfaceinfo);
        }
        log.info("刷新的缓存中的接口数据成功");
        String responseData = "{ \"key\": \"value\" }";
        return ok().contentType(APPLICATION_JSON).body(Mono.just(responseData), String.class);
    }

}
