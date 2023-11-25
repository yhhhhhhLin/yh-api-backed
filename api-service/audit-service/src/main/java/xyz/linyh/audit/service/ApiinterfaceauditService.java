package xyz.linyh.audit.service;


import com.baomidou.mybatisplus.extension.service.IService;
import xyz.linyh.model.apiaudit.dto.AuditStatusDto;
import xyz.linyh.model.apiaudit.eneitys.ApiInterfaceAudit;

/**
* @author lin
* @description 针对表【apiinterfaceaudit(api接口审核表)】的数据库操作Service
* @createDate 2023-11-20 09:33:12
*/
public interface ApiinterfaceauditService extends IService<ApiInterfaceAudit> {

    /**
     * 发送一个审核请求到gpt
     * @param audit
     */
    public void sendAuditInterfaceMsgToGpt(ApiInterfaceAudit audit);

    /**
     * 更新审核的接口的code和msg
     * @param
     */
    public void updateAuditInterfaceCodeAndMsg(Long auditId, Integer code, String msg);

    /**
     * 保存对应接口审核的数据到数据库
     * @param audit
     * @return
     */
    ApiInterfaceAudit saveAuditInterface(ApiInterfaceAudit audit);

    /**
     * 修改审核的状态和审核建议
     * @param auditId 要修改的审核data的id
     * @param status 修改后的状态
     * @param description 修改建议
     * @return 返回是否修改成功
     */
    Boolean updateStatusAndDescription(Long auditId,Integer status,String description);

    /**
     * 接口审核通过
     * @param auditId
     * @param status
     */
    void passInterfaceAudit(Long auditId, Integer status);
}

