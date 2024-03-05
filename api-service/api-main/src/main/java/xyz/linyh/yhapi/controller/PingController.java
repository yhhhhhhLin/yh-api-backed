package xyz.linyh.yhapi.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.linyh.ducommon.common.BaseResponse;
import xyz.linyh.ducommon.common.ResultUtils;

/**
 * 模块ping（可以用来测试模块是否可用）
 */
@RestController
public class PingController {

    /**
     * ping
     * @return
     */
    @GetMapping("/")
    public BaseResponse ping() {
        return ResultUtils.success(null);
    }
}
