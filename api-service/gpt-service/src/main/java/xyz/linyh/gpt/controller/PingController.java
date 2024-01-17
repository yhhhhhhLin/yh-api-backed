package xyz.linyh.gpt.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class PingController {

    @RequestMapping("/ping")
    public void ping() {
        log.info("ping通gptService");
    }
}
