package xyz.linyh.yhapi.service;


import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import xyz.linyh.model.user.dto.AnyUserUpdateRequest;
import xyz.linyh.model.user.entitys.User;
import xyz.linyh.model.user.vo.UserProfileVo;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.InputStream;

/**
 * 用户服务
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);


    /**
     * 根据用户id获取当前登录用户
     *
     * @param userId
     * @return
     */
    User getLoginUser(Long userId);

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 根据用户ak获取用户信息
     *
     * @param accessKey
     * @return
     */
    User getUserByAk(String accessKey);

    /**
     * 把用户信息保存到redis中
     *
     * @param userId
     */
    void saveUserToRedis(Long userId);

    /**
     * 根据用户token获取对应token信息
     *
     * @param token
     * @return
     */
    User getUserByToken(String token);

    Boolean removeUserById(Long id);

    /**
     * 管理员修改用户信息
     *
     * @param user
     * @return
     */
    Boolean updateUserById(User user);

    /**
     * 用户自己更新个人信息
     *
     * @param anyUserUpdateRequest
     * @return
     */
    Boolean updateUserBySelf(Long userId, AnyUserUpdateRequest anyUserUpdateRequest);

    /**
     * 更新用户ak和sk
     *
     * @param id
     * @return
     */
    Boolean updateUserAkSk(Long id);

    /**
     * 获取某一个用户的主页信息
     * @param user
     * @param account
     * @return
     */
    UserProfileVo getUserProfile(User user, String account);

    /**
     * 保存用户头像
     * @param file
     * @param user
     * @return
     */
    boolean saveUserAvatars(MultipartFile file, User user);

    /**
     * 获取用户头像
     * @param user
     * @return
     */
    InputStream getLoginUserAvatar(User user);

    /**
     * 增加用户积分
     * @param userId 用户id
     * @param addCredit 要增加的积分
     */
    boolean addUserCredit(Long userId, Integer addCredit);
}
