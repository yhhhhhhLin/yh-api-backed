package xyz.linyh.yhapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.linyh.ducommon.common.BaseResponse;
import xyz.linyh.ducommon.common.ResultUtils;

@RestController
public class PingController {

    @GetMapping("/")
    public BaseResponse ping() {
        return ResultUtils.success(null);
    }
}
