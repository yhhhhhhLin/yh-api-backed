package xyz.linyh.model.interfaceinfo.dto;

import lombok.Data;
import xyz.linyh.model.datasource.dtos.AddDataSourceApiDto;

import java.io.Serializable;

/**
 * 创建请求
 *
 * @TableName product
 */
@Data
public class InterfaceInfoAddRequest implements Serializable {

    /**
     * 接口名称
     */
    private String name;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 接口描述
     */
    private String description;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * get请求参数
     */
    private String getRequestParams;


    /**
     * 接口uri
     */
    private String uri;

    /**
     * 接口host
     */
    private String host;

    /**
     * 需要花费的积分
     */
    private Integer pointsRequired;

    /**
     * 请求头
     */
    private String requestHeader;

    /**
     * 响应信息
     */
    private String responseHeader;

    /**
     * 如果是数据源api 数据源api信息
     */
    private AddDataSourceApiDto dataSourceApiParams;

    /**
     * 接口类型-->InterfaceTypeEnum
     */
    private Integer interfaceType = 1;

    private Long userId;



    private static final long serialVersionUID = 1L;
}