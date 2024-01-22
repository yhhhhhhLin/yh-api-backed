package xyz.linyh.ducommon.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
public class  InterfaceInfoConstant{

//    接口可用状态
    public static Integer STATIC_USE = 1;
//    接口不可用状态
    public static Integer STATIC_NOT_USE = 0;
    public static String GATEWAY_PATH = "http://localhost:8081/yhapi/routes";

//    @Value("${gateway.url}")
//    public static String GATEWAY_INTERFACE_PATH;

    public static String url = "http://gateway:8081/interface";

}
