package xyz.linyh.audit.controller;

import org.springframework.web.bind.annotation.*;
import xyz.linyh.ducommon.common.BaseResponse;

/**
 * api平台接口对应增删改查的controller
 */
@RestController
@RequestMapping("/interface/audit")
public class AuditController {

    /**
     * 新增新的审核
     * @return
     */
    @PostMapping("/")
    public BaseResponse addAudit(){
        return null;
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
