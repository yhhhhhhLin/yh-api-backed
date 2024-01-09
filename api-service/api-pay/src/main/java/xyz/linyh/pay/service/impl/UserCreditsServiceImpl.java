package xyz.linyh.pay.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Service;
import xyz.linyh.pay.mapper.UserCreditsMapper;
import xyz.linyh.pay.service.UserCreditsService;
import xyz.linyh.model.pay.eneity.UserCredits;

/**
 * @author lin
 * @description 针对表【usercredits(用户剩余积分表)】的数据库操作Service实现
 * @createDate 2023-12-26 13:51:29
 */
@Service
public class UserCreditsServiceImpl extends ServiceImpl<UserCreditsMapper, UserCredits>
        implements UserCreditsService {

    @Override
    public void saveOneUserCredit(Long userId, Integer credit) {
        UserCredits userCredits = new UserCredits();
        userCredits.setUserid(userId);
        userCredits.setCredit(credit);

        this.save(userCredits);
    }
}




