package xyz.linyh.yhapi.scheduling;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import xyz.linyh.model.userinterfaceinfo.entitys.UserInterfaceinfo;
import xyz.linyh.yhapi.service.RedisService;
import xyz.linyh.yhapi.service.UserinterfaceinfoService;

import java.util.List;
import java.util.Map;

/**
 * 在项目启动的时候执行
 */
@Component
@Slf4j
public class StartingJob implements ApplicationRunner {

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserinterfaceinfoService userinterfaceinfoService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        cacheInterfaceCallCount();

    }

    /**
     * 将每个接口调用次数缓存到redis中
     */
    private void cacheInterfaceCallCount() {
        LambdaQueryWrapper<UserInterfaceinfo> wrapper = new LambdaQueryWrapper<>();
        Map<Long,Integer> idAndCount = userinterfaceinfoService.listAllInterfaceCallCount();
//        存到redis的hash中
        boolean result = redisService.addHash(idAndCount);
        if (result) {
            log.info(">>>>>>>>>>>>>>缓存接口调用次数成功<<<<<<<<<<<<<<<");
        }else{
            log.error(">>>>>>>>>>>>>>缓存接口调用次数失败<<<<<<<<<<<<<<<");
        }

    }
}
