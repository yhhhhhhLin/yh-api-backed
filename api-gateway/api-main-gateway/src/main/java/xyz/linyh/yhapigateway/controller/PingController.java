package xyz.linyh.yhapigateway.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.linyh.ducommon.common.BaseResponse;
import xyz.linyh.ducommon.common.ResultUtils;

@RestController
public class PingController {

    @RequestMapping("/")
    public BaseResponse ping(){
        return ResultUtils.success(null);
    }
}
