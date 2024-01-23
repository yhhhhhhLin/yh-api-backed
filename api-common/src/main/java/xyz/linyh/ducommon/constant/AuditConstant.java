package xyz.linyh.ducommon.constant;

/**
 * 审核相关的常量
 *
 * @author lin
 */
public interface AuditConstant {

    /**
     * 审核类型：接口
     */
    String API_INTERFACE_AUDIT_TYPE = "API_INTERFACE_AUDIT_TYPE";

    /**
     * 审核提交(还没审核)
     */
    Integer AUDIT_STATUS_SUMMIT = 1;

    /**
     * gpt审核未通过
     */
    Integer AUDIT_STATUS_GPT_FAIL = 2;

    /**
     * gpt审核通过
     */
    Integer AUDIT_STATUS_GPT_SUCCESS = 3;

    /**
     * 人工审核失败
     */
    Integer AUDIT_STATUS_PROPLE_FAIL = 4;

    /**
     * 人工审核通过(待发布)
     */
    Integer AUDIT_STATUS_PROPLE_SUCCESS = 5;

    /**
     * 已经发布
     */
    Integer AUDIT_STATUS_PUBLISH = 9;

    String audit_type_interface = null;


}
