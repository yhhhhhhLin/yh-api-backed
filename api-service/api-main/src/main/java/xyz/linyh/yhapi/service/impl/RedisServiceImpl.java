package xyz.linyh.yhapi.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import xyz.linyh.ducommon.common.ErrorCodeEnum;
import xyz.linyh.ducommon.constant.RedisConstant;
import xyz.linyh.ducommon.exception.BusinessException;
import xyz.linyh.yhapi.service.RedisService;

import java.util.HashMap;
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
    public boolean addHash(String hashKey, Map<Long, Integer> idAndCount) {
        redisTemplate.opsForHash().putAll(hashKey, idAndCount);
        return true;
    }

    @Override
    public boolean addHash(String hashKey, String field, Integer value) {
        redisTemplate.opsForHash().put(hashKey, field, value);
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

    /**
     * 查出在redis中，某一个用户对某一个接口调用了多少次，
     * 在更新数据库的时候，通过接口id和userId，直接更新某一个用户新增调用了多少次
     */
    @Override
    public Map getAllInterfaceCallCount() {
        Map<Map<Long, Long>, Integer> interfaceUserAddCount = new HashMap<>();
        ScanOptions option = ScanOptions.scanOptions().match(RedisConstant.INTERFACE_ID_AND_CALL_COUNT_KEY + "*").build();
//        可以使所有操作都使用同一个连接，减少网络开销
        try (Cursor<byte[]> cursor = redisTemplate.executeWithStickyConnection(
                redisConnection -> redisConnection.scan(option)
        )) {
            if (cursor == null) {
                return new HashMap();
            }
            while (cursor.hasNext()) {
                String key = new String(cursor.next());
                Map<Object, Object> userAndCount = redisTemplate.opsForHash().entries(key);
                userAndCount.forEach((k, v) -> {
//                    如果为-1，那么就是原先就有的,那么就直接循环下一次
                    if (userAndCount.get(k).equals(-1)) {
                        return;
                    } else {
                        HashMap<Long, Long> interfaceAndId = new HashMap<Long, Long>() {{
                            put(1L, Long.parseLong(k.toString()));
                        }};
                        interfaceUserAddCount.put(interfaceAndId, Integer.parseInt(v.toString()));
                    }
                });

            }
            return interfaceUserAddCount;
        } catch (Exception e) {
            System.out.println(e);
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "持久化用户调用次数失败查询失败");
        }
    }


}
