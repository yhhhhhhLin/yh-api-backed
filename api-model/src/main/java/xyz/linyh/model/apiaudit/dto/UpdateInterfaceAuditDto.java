package xyz.linyh.model.apiaudit.dto;

import lombok.Data;

@Data
public class UpdateInterfaceAuditDto {

    /**
     * 接口id
     */
    private Long id;

    /**
     * 接口名称
     */
    private String name;
    /**
     * 接口描述信息
     */
    private String apiDescription;

    /**
     * 接口请求方法
     */
    private String method;

    /**
     * 接口host
     */
    private String host;

    /**
     * 接口uri
     */
    private String uri;

    /**
     * 接口get请求参数
     */
    private String getRequestParams;

    /**
     * 接口post请求参数
     */
    private String requestParams;

    /**
     * 调用接口所需要的积分
     */
    private Integer pointsRequired;

    /**
     * requestHeader
     */
    private String requestHeader;

    /**
     * responseHeader
     */
    private String responseHeader;


}
