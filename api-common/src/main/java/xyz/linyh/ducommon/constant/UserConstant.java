package xyz.linyh.ducommon.constant;

/**
 * 用户常量
 *
 *
 */
public interface UserConstant {

    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "userLoginState";

    String USER_Id = "userId";

    /**
     * 系统用户 id（虚拟用户）
     */
    long SYSTEM_USER_ID = 0;

    //  region 权限

    /**
     * 默认权限
     */
    String DEFAULT_ROLE = "user";

    /**
     * 管理员权限
     */
    String ADMIN_ROLE = "admin";

    String SAVE_AVATAR_WIN_PRE_PATH = "D:\\img\\apiAvatar";

    String SAVE_AVATAR_LINUX_PRE_PATH = "/home/img/apiAvatar";

    String USER_AVATAR = "userAvatar";

    // endregion
}
