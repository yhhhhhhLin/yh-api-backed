package xyz.linyh.yhapi.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import xyz.linyh.ducommon.constant.RedisConstant;
import xyz.linyh.yhapi.MyApplication;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = MyApplication.class)
public class WebVisitControllerTest {


    @Autowired
    private RedisTemplate redisTemplate;



    @Test
    public void testGetVisitData() {

//        最近7天的日期
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate now = LocalDate.now();

        List<String> daysKeys = new ArrayList<>(7);
        for(int i = 6;i>=0;i--){
            String keySuf = now.minusDays(i).format(formatter);
            daysKeys.add(RedisConstant.DAILY_VISIT_KEY_PRE.concat(keySuf));
        }

        List result = redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                HashOperations<String, Object, Object> hashOps = (HashOperations<String, Object, Object>) operations.opsForHash();
                for (int i = 0; i < 7; i++) {
                    hashOps.entries(daysKeys.get(i));
                }

                return null;
            }
        });
        System.out.println(result);
//
    }
}
