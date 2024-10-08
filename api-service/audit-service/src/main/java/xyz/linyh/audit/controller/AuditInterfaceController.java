package xyz.linyh.audit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.linyh.audit.service.ApiinterfaceauditService;
import xyz.linyh.ducommon.annotation.AuthCheck;
import xyz.linyh.ducommon.common.BaseResponse;
import xyz.linyh.ducommon.common.ErrorCodeEnum;
import xyz.linyh.ducommon.common.ResultUtils;
import xyz.linyh.ducommon.constant.AuditConstant;
import xyz.linyh.model.apiaudit.dto.AuditStatusDto;
import xyz.linyh.model.apiaudit.dto.ListAuditDto;
import xyz.linyh.model.apiaudit.dto.UpdateInterfaceAuditDto;
import xyz.linyh.model.apiaudit.eneitys.ApiInterfaceAudit;

import javax.servlet.http.HttpServletRequest;

/**
 * 审核接口数据的增删改查
 *
 * @author lin
 */
@RestController
@RequestMapping("/audit/interface")
@Slf4j
public class AuditInterfaceController {

    @Autowired
    private ApiinterfaceauditService apiinterfaceauditService;

    /**
     * 新增新的审核
     *
     * @return 返回是否成功
     */
    @PostMapping
    public BaseResponse addAudit(@RequestBody ApiInterfaceAudit audit, HttpServletRequest request) {
        if (audit == null) {
            return ResultUtils.error(ErrorCodeEnum.PARAMS_ERROR, "audit参数不能为空");
        }

        Long userId = getLoginUserId(request);
        audit.setUserId(userId);

//        创建对应审核表
        audit = apiinterfaceauditService.saveAuditInterface(audit);
        log.info("添加到数据库成功:{}", audit);

        apiinterfaceauditService.sendAuditInterfaceMsgToGpt(audit);
        return ResultUtils.success("发送成功，等待审核");
    }

    /**
     * 分页查看所有审核的接口
     *
     * @return 返回所有审核的接口
     */
    @AuthCheck(mustRole = "admin")
    @GetMapping
    public BaseResponse<Page<ApiInterfaceAudit>> listAudit(ListAuditDto dto, HttpServletRequest request) {
//        参数校验
        if (dto == null) {
            return ResultUtils.error(ErrorCodeEnum.PARAMS_ERROR, "dto参数不能为空");
        }
        dto.check();

        Page<ApiInterfaceAudit> pageList = apiinterfaceauditService.listAuditPage(dto);

        return ResultUtils.success(pageList);
    }

    /**
     * 如果修改了接口内容，那么需要重新审核接口
     *
     * @param dto 修改的接口内容
     * @return
     */
    @PutMapping
    public BaseResponse<Object> updateAudit(@RequestBody UpdateInterfaceAuditDto dto, HttpServletRequest request) {
        if (dto == null || dto.getId() == null) {
            return ResultUtils.error(ErrorCodeEnum.PARAMS_ERROR, "请求参数或id不能为空");
        }

        ApiInterfaceAudit apiInterfaceAudit = new ApiInterfaceAudit();
        BeanUtils.copyProperties(dto, apiInterfaceAudit);
        apiInterfaceAudit.setApiId(dto.getId());
        apiInterfaceAudit.setId(null);
        apiInterfaceAudit.setStatus(AuditConstant.AUDIT_STATUS_SUMMIT);

        Long userId = getLoginUserId(request);
        boolean result = apiinterfaceauditService.updateAuditInterface(apiInterfaceAudit, userId);

        return ResultUtils.success(result);
    }

    /**
     * 获取某一个待审核接口的详细内容
     *
     * @return 返回某一个审核接口
     */
    @GetMapping("/{id}")
    public BaseResponse<Object> getAudit(@PathVariable Long id) {
        return null;
    }

    /**
     * 删除某一个审核接口
     *
     * @return
     */
    @DeleteMapping("/{id}")
    public BaseResponse<Object> deleteAudit(@PathVariable Long id) {
        return null;
    }

    /**
     * 修改某一个接口的状态
     *
     * @return
     */
    @PostMapping("/status")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> auditInterface(@RequestBody AuditStatusDto dto,HttpServletRequest request) {
        if (dto == null || dto.getStatus() == null || dto.getAuditId() == null) {
            return ResultUtils.error(ErrorCodeEnum.PARAMS_ERROR, "status或auditId参数不能为空");
        }

        Long userId = getLoginUserId(request);

        if (AuditConstant.AUDIT_STATUS_PROPLE_SUCCESS.equals(dto.getStatus())) {
            apiinterfaceauditService.passInterfaceAudit(dto.getAuditId(), dto.getStatus(),userId);
        } else if (AuditConstant.AUDIT_STATUS_PROPLE_FAIL.equals(dto.getStatus())) {
            apiinterfaceauditService.rejectInterfaceAudit(dto.getAuditId(), dto.getStatus(), dto.getDescription(),userId);
        } else {
            return ResultUtils.error(ErrorCodeEnum.PARAMS_ERROR, "status参数错误");
        }

        return ResultUtils.success(true);
    }


    /**
     * TODO 改为去redis中获取也可以 或直接在gateway中去redis中获取然后保存起来？
     *
     * @param request
     * @return
     */
    private Long getLoginUserId(HttpServletRequest request) {
        return Long.valueOf(request.getHeader("userId"));
    }

}
