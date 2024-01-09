package xyz.linyh.pay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.linyh.model.pay.eneity.UserCredits;

/**
 * @author lin
 * @description 针对表【usercredits(用户剩余积分表)】的数据库操作Service
 * @createDate 2023-12-26 13:51:29
 */
public interface UserCreditsService extends IService<UserCredits> {

    void saveOneUserCredit(Long userId, Integer credit);
}
