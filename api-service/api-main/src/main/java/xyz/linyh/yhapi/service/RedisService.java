package xyz.linyh.yhapi.service;

import xyz.linyh.model.user.entitys.User;

import java.util.List;
import java.util.Map;

/**
 * 用来操作redis的服务
 *
 * @author lin
 */
public interface RedisService {

    /**
     * 将用户信息保存到redis中
     *
     * @param key
     * @param value
     */
    void saveStringToRedis(String key, String value);


    /**
     * 获取用户信息
     *
     * @param key
     * @return
     */
    String get(String key);

    /**
     * 删除redis中对应的key和value
     *
     * @param key
     * @return
     */
    Boolean delete(String key);

    /**
     * 根据用户id修改对应的value
     *
     * @param key
     * @param value
     * @return
     */
    Boolean update(String key, String value);

    boolean addHash(Map<Long, Integer> idAndCount);

    /**
     * 增加redis中hash的value
     * @param interfaceIdAndCallCountKey
     * @param interfaceInfoId
     * @return
     */
    boolean addHashFieldValueNum(String interfaceIdAndCallCountKey, Long interfaceInfoId);

    String getHashValue(String key, String field);
}
