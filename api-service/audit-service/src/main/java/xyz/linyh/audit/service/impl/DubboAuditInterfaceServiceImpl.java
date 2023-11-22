package xyz.linyh.audit.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.linyh.audit.service.ApiinterfaceauditService;
import xyz.linyh.dubboapi.service.DubboAuditInterfaceService;

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
        apiinterfaceauditService.updateAuditInterfaceCodeAndMsg(auditId, code, msg);
    }
}
