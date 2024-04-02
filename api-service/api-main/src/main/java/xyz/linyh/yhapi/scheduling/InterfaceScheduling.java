package xyz.linyh.yhapi.scheduling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import xyz.linyh.yhapi.service.RedisService;
import xyz.linyh.yhapi.service.UserinterfaceinfoService;

import java.util.Map;

/**
 * 接口定时任务
 */
@Component
@Slf4j
public class InterfaceScheduling {

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserinterfaceinfoService userinterfaceinfoService;

//    @Scheduled(cron = "0 5 * * * ?")
//    public void interfaceCountTask(){
//        log.info("将redis里面接口调用次数持久化到redis");
////        获取redis中所有接口对应的hash
//        Map allInterfaceUserAddCount = redisService.getAllInterfaceCallCount();
//        boolean b = userinterfaceinfoService.batchAddUserInterfaceCallCount(allInterfaceUserAddCount);
//
//    }
}
