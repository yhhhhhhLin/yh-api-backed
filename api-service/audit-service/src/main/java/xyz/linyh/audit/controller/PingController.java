package xyz.linyh.audit.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

    @RequestMapping("/ping")
    public void ping() {
        System.out.println("ping通auditService");
    }
}
