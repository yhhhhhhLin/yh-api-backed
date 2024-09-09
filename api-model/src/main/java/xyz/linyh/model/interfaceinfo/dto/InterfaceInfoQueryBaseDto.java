package xyz.linyh.model.interfaceinfo.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import xyz.linyh.model.base.dtos.PageBaseDto;

import java.io.Serializable;

/**
 * 查询请求
 *
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InterfaceInfoQueryBaseDto extends PageBaseDto implements Serializable {

    /**
     * 接口id
     */
    private Long id;

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
     * 需要花费的积分
     */
    private Integer pointsRequired;

    /**
     * 响应信息
     */
    private String responseHeader;

    /**
     * 请求参数
     */
    private String requestParams;


    /**
     * 接口状态 0为可用 1为不可用
     */
    private Integer status;

    /**
     * 数据源类型 --> interfaceTypeEnum
     */
    private Integer interfaceType;

    /**
     * 接口创建者id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}