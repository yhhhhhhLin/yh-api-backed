package xyz.linyh.model.interfaceinfo.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

/**
 * @author lin
 */
@Data
@ToString
@EqualsAndHashCode
public class InterfaceInfoVO {

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
     * 请求参数
     */
    private String requestParams;


    /**
     * get请求参数
     */
    private String getRequestParams;

    /**
     * 需要花费的积分
     */
    private Integer pointsRequired;

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
     * 接口状态 1为可用 0为不可用
     */
    private Integer status;

    /**
     * 接口创建者id
     */
    private Long userId;


    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 接口被调用总数
     */
    private Integer allNum;

    /**
     * 剩余次数接口
     */
    private Integer remNum;




}
