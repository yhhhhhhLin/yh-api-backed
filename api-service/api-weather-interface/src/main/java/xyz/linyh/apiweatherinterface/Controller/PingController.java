package xyz.linyh.apiweatherinterface.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试模块是否可用
 */
@RestController
@Slf4j
public class PingController {

    /**
     * 测试模块是否可用
     */
    @RequestMapping("/ping")
    public void ping() {
        log.info("ping");
    }
}
