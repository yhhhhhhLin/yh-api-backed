package xyz.linyh.audit.service.impl;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.linyh.audit.service.ApiinterfaceauditService;
import xyz.linyh.dubboapi.service.audit.DubboAuditInterfaceService;

/**
 * 提供远程调用接口给其他的微服务
 */
@DubboService
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
        apiinterfaceauditService.updateAuditInterfaceCodeAndMsg(auditId, code, msg);
    }
}
