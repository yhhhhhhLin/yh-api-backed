package xyz.linyh.dubboapi.service;


import xyz.linyh.model.user.entitys.User;

/**
 * 用户服务
 * @author lin
 */
public interface DubboUserService{


    /**
     * 根据用户ak获取用户信息
     * @param accessKey
     * @return
     */
    User getUserByAk(String accessKey);

    /**
     * 根据id获取当前登录用户
     * @param userId
     * @return
     */
    User getLoginUser(String userId);

    /**
     * 给某一个用户增加积分
     * @param userId
     * @param addCredit
     */
    boolean addUserCredit(Long userId,Integer addCredit);
}
