package xyz.linyh.model.interfaceinfo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新请求
 *
 * @TableName product
 */
@Data
public class InterfaceInfoUpdateRequest implements Serializable {

    /**
     * 接口id
     */
    private Long id;

    /**
     * 接口名称
     */
    private String name;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * g请求参数
     */
//    todo 接收不到
    private String getRequestParams;



    /**
     * 请求方法
     */
    private String method;

    /**
     * 接口描述
     */
    private String description;

    /**
     * 接口uri
     */
    private String uri;

    /**
     * 接口host
     */
    private String host;

    /**
     * 请求头
     */
    private String requestHeader;

    /**
     * 响应信息
     */
    private String responseHeader;

    /**
     * 接口状态 0为可用 1为不可用
     */
    private Integer status;


    private static final long serialVersionUID = 1L;
}