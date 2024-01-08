package xyz.linyh.audit.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import xyz.linyh.audit.service.ApiinterfaceauditService;
import xyz.linyh.ducommon.annotation.AuthCheck;
import xyz.linyh.ducommon.common.BaseResponse;
import xyz.linyh.ducommon.common.ErrorCode;
import xyz.linyh.ducommon.common.ResultUtils;
import xyz.linyh.ducommon.constant.AuditConstant;
import xyz.linyh.model.apiaudit.dto.AuditStatusDto;
import xyz.linyh.model.apiaudit.dto.ListAuditDto;
import xyz.linyh.model.apiaudit.eneitys.ApiInterfaceAudit;

import javax.servlet.http.HttpServletRequest;

/**
 * api平台接口对应增删改查的controller
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
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "audit参数不能为空");
        }
        Long userId = getLoginUserId(request);
        audit.setUserId(userId);

//        创建对应审核表
        audit = apiinterfaceauditService.saveAuditInterface(audit);

        log.info("添加到数据库成功:{}", audit);
        apiinterfaceauditService.sendAuditInterfaceMsgToGpt(audit);
        return ResultUtils.success("发送成功，等待审核");
    }

    private Long getLoginUserId(HttpServletRequest request) {
        return Long.valueOf(request.getHeader("userId"));
    }

    /**
     * TODO 需要管理员才能干
     * 分页查看所有审核的接口
     *
     * @return 返回所有审核的接口
     */
    @AuthCheck(mustRole = "admin")
    @PostMapping("/list")
    public BaseResponse<Page<ApiInterfaceAudit>> listAudit(@RequestBody ListAuditDto dto, HttpServletRequest request) {
//        参数校验
        if (dto == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "dto参数不能为空");
        }

        dto.check();
        Page<ApiInterfaceAudit> page = new Page<>(dto.getCurrent(), dto.getPageSize());
        Page<ApiInterfaceAudit> pageList = apiinterfaceauditService.page(page, Wrappers.<ApiInterfaceAudit>lambdaQuery()
                .like(dto.getName() != null, ApiInterfaceAudit::getName, dto.getName())
                .eq(dto.getStatus() != null, ApiInterfaceAudit::getStatus, dto.getStatus()).orderByDesc(ApiInterfaceAudit::getCreateTime));
        return ResultUtils.success(pageList);
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
    public BaseResponse<Boolean> auditInterface(@RequestBody AuditStatusDto dto) {
        if (dto == null || dto.getStatus() == null || dto.getAuditId() == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "status或auditId参数不能为空");
        }
        apiinterfaceauditService.updateAuditInterfaceCodeAndMsg(dto.getAuditId(), dto.getStatus(), dto.getDescription());
//        判断状态，如果是通过，那么才通过审核
        if (dto.getStatus().equals(Integer.valueOf(AuditConstant.AUDIT_STATUS_PROPLE_SUCCESS))) {
            apiinterfaceauditService.passInterfaceAudit(dto.getAuditId(), dto.getStatus());
        }
//        TODO 如果是不通过，可能还需要去修改对应主服务的状态

//        将数据保存到api接口的数据库中


        return ResultUtils.success(true);
    }

    /**
     * 更新某一个审核接口
     *
     * @return
     */
    @PutMapping("/{id}")
    public BaseResponse<Object> updateAudit(@PathVariable Long id) {
        return null;
    }
}
