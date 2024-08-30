package xyz.linyh.yhapi.scheduling;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class InterfaceDispatchTask {

    @Scheduled(cron = "0 */1 * * * ?")
    public void createTableTask(){
//        TODO 监控所有调度信息，如果到达指定时间就执行调度信息内容

    }
}
