package xyz.linyh.model.apiaudit.dto;

import lombok.Data;

/**
 * 用来接收前端创建修改审核数据的状态
 * @author lin
 */
@Data
public class AuditStatusDto {
    /**
     * 审核表数据对应id
     */
    private Long auditId;

    /**
     * 修改后的字段
     */
    private Integer status;

    /**
     * 审核建议
     */
    private String description;
}
