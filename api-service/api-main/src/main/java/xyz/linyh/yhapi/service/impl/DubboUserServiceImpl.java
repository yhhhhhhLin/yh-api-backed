package xyz.linyh.yhapi.service.impl;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.linyh.dubboapi.service.DubboUserService;
import xyz.linyh.model.user.entitys.User;
import xyz.linyh.yhapi.service.UserService;

@DubboService
public class DubboUserServiceImpl implements DubboUserService {

    @Autowired
    private UserService userService;

    @Override
    public User getUserByAk(String accessKey) {
        return userService.getUserByAk(accessKey);
    }
}
