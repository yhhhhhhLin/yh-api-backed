package xyz.linyh.model.apiaudit.dto;

import lombok.Data;
import xyz.linyh.ducommon.common.PageRequest;

/**
 * 可以用来模糊查询的获取待审核接口数据的类
 * @author lin
 */
@Data
public class ListAuditDto extends PageRequest {

    /**
     * 待审核的接口名称
     */
    private String name;

    /**
     * 审核的内容类型
     */
    private String auditType;

    /**
     * 审核状态
     */
    private String status;




}
