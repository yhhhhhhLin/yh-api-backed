package xyz.linyh.model.apiaudit.dto;

import lombok.Data;
import xyz.linyh.model.base.dtos.PageBaseDto;

/**
 * 可以用来模糊查询的获取待审核接口数据的类
 * @author lin
 */
@Data
public class ListAuditDto extends PageBaseDto {

    /**
     * 待审核的接口名称
     */
    private String name;

    /**
     * 审核状态
     */
    private String status;

    /**
     * 接口描述
     */
    private String apiDescription;

    /**
     * 接口地址
     */
    private String host;

    /**
     * 接口uri
     */
    private String uri;

    /**
     * 接口请求方法
     */
    private String method;




}
