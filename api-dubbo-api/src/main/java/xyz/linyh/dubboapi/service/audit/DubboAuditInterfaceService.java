package xyz.linyh.dubboapi.service.audit;


public interface DubboAuditInterfaceService {

    /**
     * 更新interface审核表的code和msg信息
     * @param auditId
     * @param code
     * @param msg
     */
    public void updateAuditInterfaceCodeAndMsg(Long auditId, Integer code, String msg);

}
