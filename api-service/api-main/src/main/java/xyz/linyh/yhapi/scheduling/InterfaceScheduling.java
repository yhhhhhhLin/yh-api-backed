package xyz.linyh.yhapi.scheduling;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 接口定时任务
 */
@Component
public class InterfaceScheduling {

    @Scheduled(cron = "0 5 * * * ?")
    public void interfaceCountTask(){
        System.out.println("将redis里面接口调用次数持久化到redis");

    }
}
