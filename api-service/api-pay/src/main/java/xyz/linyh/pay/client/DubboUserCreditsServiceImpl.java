package xyz.linyh.pay.client;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.linyh.pay.service.UserCreditsService;
import xyz.linyh.dubboapi.service.DubboUserCreditsService;

@DubboService
public class DubboUserCreditsServiceImpl implements DubboUserCreditsService {

    @Autowired
    private UserCreditsService userCreditsService;

    public void CreateUserCredit(Long userId,Integer credit){
        userCreditsService.saveOneUserCredit(userId,credit);

    }
}
