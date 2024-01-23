package xyz.linyh.audit.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.linyh.audit.service.ApiinterfaceauditService;
import xyz.linyh.dubboapi.service.DubboAuditInterfaceService;
import xyz.linyh.dubboapi.service.DubboInterfaceinfoService;
import xyz.linyh.ducommon.constant.AuditConstant;
import xyz.linyh.ducommon.constant.AuditMQTopicConstant;
import xyz.linyh.model.apiaudit.eneitys.ApiInterfaceAudit;

import javax.print.DocFlavor;

/**
 * 提供远程调用接口给其他的微服务
 */
@DubboService
@Slf4j
public class DubboAuditInterfaceServiceImpl implements DubboAuditInterfaceService {

    @Autowired
    private ApiinterfaceauditService apiinterfaceauditService;


    /**
     * 更新interface审核表的code和msg信息
     *
     * @param auditId
     * @param code
     * @param msg
     */
    @Override
    public void updateAuditInterfaceCodeAndMsg(Long auditId, Integer code, String msg) {
        log.info("接收到要更新数据库的请求，更新的id为:{},code为:{},msg为:{}", auditId, code, msg);

//        判断他原先的状态码是否是通过还是未通过，如果是这些状态，那么就不需要更新了，因为审核后的结果可能比管理员同意慢
        ApiInterfaceAudit apiInterfaceAudit = apiinterfaceauditService.getById(auditId);
        String auditStatus = String.valueOf(apiInterfaceAudit.getStatus());
        if (auditStatus.equals(AuditConstant.AUDIT_STATUS_PUBLISH) || auditStatus.equals(AuditConstant.AUDIT_STATUS_PROPLE_FAIL) || auditStatus.equals(AuditConstant.AUDIT_STATUS_PROPLE_SUCCESS)) {
            return;
        }

        apiinterfaceauditService.updateAuditInterfaceCodeAndMsg(auditId, code, msg);

    }
}
