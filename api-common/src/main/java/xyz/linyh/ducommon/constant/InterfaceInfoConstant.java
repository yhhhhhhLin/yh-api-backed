package xyz.linyh.ducommon.constant;

public interface InterfaceInfoConstant {

    /**
     * 接口可用状态
     */
    Integer STATIC_USE = 1;

    /**
     * 接口不可用状态
     */
    Integer STATIC_NOT_USE = 0;

    /**
     * 接口审核状态
     */
    Integer STATIC_AUDITING = 2;

    /**
     * 需要重新修改信息重新审核状态
     */
    Integer STATIC_SHOULD_RE_AUDIT = 3;


    String GATEWAY_PATH = "http://localhost:8081/yhapi/routes";

//    @Value("${gateway.url}")
//    public static String GATEWAY_INTERFACE_PATH;

    String url = "http://gateway:8081/interface";

}
