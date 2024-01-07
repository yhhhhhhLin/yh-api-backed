package xyz.linyh.yhapi.service.impl;

import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import xyz.linyh.yhapi.service.RedisService;

/**
 * @author lin
 */
@Service
public class RedisServiceImpl implements RedisService {


    @Autowired
    private StringRedisTemplate redisTemplate;


    @Override
    public void saveStringToRedis(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public String get(String key) {
//        用json转换为对应类型
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除redis中对应的key和value
     *
     * @param key
     */
    @Override
    public Boolean delete(String key) {
        if (key == null) {
            return false;
        }
        return redisTemplate.delete(key);
    }

    /**
     * 根据用户id修改对应的value
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    public Boolean update(String key, String value) {
        return null;
    }

}
