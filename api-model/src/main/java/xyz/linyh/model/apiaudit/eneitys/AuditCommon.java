package xyz.linyh.model.apiaudit.eneitys;

import lombok.Data;

import java.util.Date;

@Data
public class AuditCommon {

    /**
     * 审核内容id
     */
    private Long auditId;

    /**
     * 审核要求
     */
    private String auditDescription;

    /**
     * 审核的信息类型（有一个constant）
     */
    private String type;

    /**
     * 要审核的内容
     */
    private Object content;

    /**
     * 审核结果码（有一个constant）
     */
    private String auditResultCode;

    /**
     * 审核结果信息
     */
    private String auditResultMsg;

    /**
     * 审核创建时间
     */
    private Date auditCreateTime;

    /**
     * 审核时间
     */
    private Date auditResultTime;


}
