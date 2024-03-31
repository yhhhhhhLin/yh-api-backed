package xyz.linyh.yhapi.service.impl;

import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import xyz.linyh.ducommon.constant.RedisConstant;
import xyz.linyh.model.user.entitys.User;
import xyz.linyh.yhapi.service.RedisService;

import java.util.List;
import java.util.Map;

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

    @Override
    public boolean addHash(Map<Long, Integer> idAndCount) {
        redisTemplate.opsForHash().putAll(RedisConstant.INTERFACE_ID_AND_CALL_COUNT_KEY, idAndCount);
        return true;
    }

    @Override
    public boolean addHashFieldValueNum(String interfaceIdAndCallCountKey, Long interfaceInfoId) {
        redisTemplate.opsForHash().increment(interfaceIdAndCallCountKey, interfaceInfoId, 1);
        return true;
    }

    @Override
    public int getIntHashValue(String key, String field) {
        Object result = redisTemplate.opsForHash().get(key, field);
        if (result == null) {
            return 0;
        }
        if (result instanceof String) {
            return Integer.parseInt((String) result);
        } else if (result instanceof Integer) {
            return (int) result;
        }
        return 0;
    }


}
