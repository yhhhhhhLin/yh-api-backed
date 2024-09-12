package xyz.linyh.yhapigateway.filters;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import xyz.linyh.ducommon.common.InterfaceTypeEnum;
import xyz.linyh.model.interfaceinfo.entitys.Interfaceinfo;
import xyz.linyh.yhapigateway.service.RouteService;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_PREDICATE_MATCHED_PATH_ROUTE_ID_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

/**
 * 用来修改跳转到真正的uri地址
 */
@Component
@Slf4j
public class UriHostPlaceholderFilter extends AbstractGatewayFilterFactory<UriHostPlaceholderFilter.Config> {
    @Autowired
    private RouteService routeService;

    public UriHostPlaceholderFilter() {
        super(Config.class);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Collections.singletonList("order");
    }

    String PREFIX = "/interface";

    String INTERFACE_TYPE_HEADER = "interface_type";


    @Override
    public GatewayFilter apply(Config config) {
        return new OrderedGatewayFilter((exchange, chain) -> {
//            TODO sdk和interfaceController里增加请求头，可以用来判断是那种类型的接口
            String interfaceType = exchange.getRequest().getHeaders().getFirst(INTERFACE_TYPE_HEADER);
//            if(InterfaceTypeEnum.DATABASE_INTERFACE.equals(interfaceType)){
//
//            }else{
//
//            }
//            TODO 改为普通api需要修改uri，数据源不需要修改uri，直接生成查询sql查询对应数据库就可以了
            String routeId = exchange.getAttribute(GATEWAY_PREDICATE_MATCHED_PATH_ROUTE_ID_ATTR);
            String uri = exchange.getRequest().getHeaders().getFirst("uri");
            String method = exchange.getRequest().getMethod().toString();

            if (uri == null) {
                return chain.filter(exchange);
            }
//            获取缓存里面所有的路由信息
            Map<String, Interfaceinfo> routes = routeService.getRoutes();

            if (routes == null || routes.isEmpty()) {
                return chain.filter(exchange);
            }

            Interfaceinfo interfaceinfo = routes.get(uri);
            if (interfaceinfo == null) {
                return chain.filter(exchange);
            }

//            获取真正要跳转的地址
            String host = interfaceinfo.getHost();
            log.info("真正要跳转的地址为:{}", host);
            ;

            System.out.println(exchange.getRequest().getQueryParams());

//            todo 可能还需要对地址格式进行判断
            URI newUrl = null;
            String modUrl = host + uri;
            try {
                String query = exchange.getRequest().getURI().getQuery();
                if (!StrUtil.isBlank(query)) {
                    modUrl = modUrl + "?" + query;

                }
//                需要补get请求参数到后面
                newUrl = new URI(modUrl);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }

            exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, newUrl);

            log.info("修改后的请求地址为:{}", newUrl);
            return chain.filter(exchange);
        }, config.getOrder());
    }

    @Data
    @NoArgsConstructor
    public static class Config {
        private int order;

        public Config(int order) {
            this.order = order;
        }
    }
}
