package xyz.linyh.dubboapi.service;


import xyz.linyh.model.user.entitys.User;

/**
 * 用户服务
 *
 *
 */
public interface DubboUserService{


    /**
     * 根据用户ak获取用户信息
     * @param accessKey
     * @return
     */
    User getUserByAk(String accessKey);

}
