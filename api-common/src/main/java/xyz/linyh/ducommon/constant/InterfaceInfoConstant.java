package xyz.linyh.ducommon.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
@ConfigurationProperties(prefix = "gateway")
public class  InterfaceInfoConstant{

    public static String STATIC_USE = "1";
    public static String STATIC_NOT_USE = "0";
    public static String GATEWAY_PATH = "http://localhost:8081/yhapi/routes";

//    @Value("${gateway.url}")
//    public static String GATEWAY_INTERFACE_PATH;

    public static String url = "http://yhapi-gateway:8081/interface";

}
