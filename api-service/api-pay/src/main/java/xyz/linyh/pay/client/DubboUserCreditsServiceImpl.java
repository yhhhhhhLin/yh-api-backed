package xyz.linyh.pay.client;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.linyh.dubboapi.service.DubboUserCreditsService;
import xyz.linyh.pay.service.UserCreditsService;

@DubboService
public class DubboUserCreditsServiceImpl implements DubboUserCreditsService {

    @Autowired
    private UserCreditsService userCreditsService;

    public void CreateUserCredit(Long userId, Integer credit) {
        userCreditsService.saveOneUserCredit(userId, credit);

    }
}
