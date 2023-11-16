package xyz.linyh.yapiclientsdk.entitys;

import lombok.Data;

import java.util.Map;

/**
 * @author lin
 */
@Data
public class InterfaceParams {

    /**
     *     请求参数
     */
    private Map<String,Object> requestParams;

    /**
     *      请求体参数
     */
    private Map<String,Object> requestBody;

    /**
     *     请求头
     */
    private Map<String,Object> requestHeader;

    /**
     *     请求方法
     */
    private String requestMethod;

    /**
     *     请求uri
     */
    private String requestURI;

}
