package xyz.linyh.audit.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试模块是否可用
 */
@RestController
public class PingController {


    /**
     * 测试模块是否可用
     */
    @RequestMapping("/ping")
    public void ping() {
        System.out.println("ping通auditService");
    }
}
