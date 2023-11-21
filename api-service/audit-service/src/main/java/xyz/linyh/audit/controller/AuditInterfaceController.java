package xyz.linyh.audit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.linyh.audit.service.ApiinterfaceauditService;
import xyz.linyh.ducommon.common.BaseResponse;
import xyz.linyh.ducommon.common.ErrorCode;
import xyz.linyh.ducommon.common.ResultUtils;
import xyz.linyh.model.apiaudit.eneitys.ApiInterfaceAudit;

import javax.servlet.http.HttpServletRequest;

/**
 * api平台接口对应增删改查的controller
 * @author lin
 */
@RestController
@RequestMapping("/interface/audit")
public class AuditInterfaceController {

    @Autowired
    private ApiinterfaceauditService apiinterfaceauditService;

    /**
     * 新增新的审核
     * @return
     */
    @PostMapping("/")
    public BaseResponse addAudit(@RequestBody ApiInterfaceAudit audit, HttpServletRequest request){
        if(audit==null){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR,"audit参数不能为空");
        }
        Long userId = getLoginUserId(request);
        audit.setUserId(userId);
//        创建对应的审核表

        apiinterfaceauditService.sendAuditInterfaceMsgToGpt(audit);
        return ResultUtils.success("发送成功，等待审核");
    }

    private Long getLoginUserId(HttpServletRequest request) {
        return Long.valueOf(request.getHeader("userId"));
    }

    /**
     * 查看所有审核的接口
     * @return
     */

    @GetMapping("/list")
    public BaseResponse listAudit(){
        return null;
    }

    /**
     * 获取某一个待审核接口的详细内容
     * @return
     */
    @GetMapping("/{id}")
    public BaseResponse getAudit(@PathVariable Long id){
        return null;
    }

    /**
     * 删除某一个审核接口
     * @return
     */
    @DeleteMapping("/{id}")
    public BaseResponse deleteAudit(@PathVariable Long id){
        return null;
    }

    /**
     * 更新某一个审核接口
     * @return
     */
    @PutMapping("/{id}")
    public BaseResponse updateAudit(@PathVariable Long id){
        return null;
    }
}
