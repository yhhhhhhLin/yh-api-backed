package xyz.linyh.model.interfaceinfo.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建请求
 *
 * @TableName product
 */
@Data
public class InterfaceInfoInvokeRequest implements Serializable {

    /**
     * 接口id
     */
    private Long id;


    /**
     * 请求体参数参数
     */
    private String requestParams;


    /**
     * get请求参数
     */
    private List<GRequestParamsDto> getRequestParams;


    /**
     * 请求头
     */
    private String requestHeader;

    /**
     * 响应信息
     */
    private String responseHeader;


    private static final long serialVersionUID = 1L;
}