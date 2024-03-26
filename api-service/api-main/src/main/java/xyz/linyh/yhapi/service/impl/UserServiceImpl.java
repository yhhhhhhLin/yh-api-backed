package xyz.linyh.yhapi.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import xyz.linyh.ducommon.common.ErrorCode;
import xyz.linyh.ducommon.constant.RedisConstant;
import xyz.linyh.ducommon.constant.UserConstant;
import xyz.linyh.ducommon.exception.BusinessException;
import xyz.linyh.model.interfaceinfo.InterfaceAllCountAndCallCount;
import xyz.linyh.model.user.dto.AnyUserUpdateRequest;
import xyz.linyh.model.user.entitys.User;
import xyz.linyh.model.user.vo.UserProfileVo;
import xyz.linyh.yhapi.mapper.InterfaceinfoMapper;
import xyz.linyh.yhapi.mapper.UserMapper;
import xyz.linyh.yhapi.service.OssService;
import xyz.linyh.yhapi.service.RedisService;
import xyz.linyh.yhapi.service.UserService;
import xyz.linyh.yhapi.utils.NonCollidingAccessKeyGenerator;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static xyz.linyh.ducommon.constant.RedisConstant.USER_ID_PREFIX;
import static xyz.linyh.ducommon.constant.UserConstant.*;


/**
 * 用户服务实现类
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private InterfaceinfoMapper interfaceinfoMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private OssService ossService;
    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "yh";

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            user.setUserName("平台用户");
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
//            重置密钥

            return user.getId();
        }
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 记录用户的登录态 改为生成token返回给前端
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return user;
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {

        String userId = request.getHeader(USER_Id);
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
//        查询数据库获取对应用户
        User user = getUserByToken(USER_ID_PREFIX + userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        return user;
    }

    /**
     * 根据用户id获取当前登录用户
     *
     * @param userId
     * @return
     */
    @Override
    public User getLoginUser(Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        String json = redisService.get(USER_ID_PREFIX + userId);
        return JSONUtil.toBean(json, User.class);
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && ADMIN_ROLE.equals(user.getUserRole());
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
//        删除对应用户redis中的信息
        String userId = request.getHeader(USER_Id);
        redisService.delete(userId);
        return true;
    }


    /**
     * 根据用户ak获取用户信息
     *
     * @param accessKey
     * @return
     */
    @Override
    public User getUserByAk(String accessKey) {
        if (accessKey == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = this.getOne(Wrappers.<User>lambdaQuery().eq(User::getAccessKey, accessKey));
        return user;
    }

    /**
     * 把用户信息保存到redis中
     *
     * @param userId
     */
    @Override
    public void saveUserToRedis(Long userId) {
        log.info("------------------id为:{}用户更新用户缓存--------------",userId);
        User user = this.getById(userId);
        if (user == null || userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //        保存到redis中
        redisService.saveStringToRedis(USER_ID_PREFIX + userId, JSONUtil.toJsonStr(user));
    }

    /**
     * 根据用户token获取对应token信息
     *
     * @param token
     * @return
     */
    @Override
    public User getUserByToken(String token) {
        if (token == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String json = redisService.get(token);
        User user = JSONUtil.toBean(json, User.class);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return user;
    }

    @Override
    public Boolean removeUserById(Long id) {

        boolean b = this.removeById(id);
//        删除对应redis数据
        redisService.delete(USER_ID_PREFIX + id);
        return b;
    }

    /**
     * 管理员修改用户信息
     *
     * @param user
     * @return
     */
    @Override
    public Boolean updateUserById(User user) {
        boolean result = this.updateById(user);

        if (result) {
            redisService.update(String.valueOf(user.getId()), JSONUtil.toJsonStr(user));
        }
        return result;
    }

    /**
     * 用户自己更新个人信息
     *
     * @param anyUserUpdateRequest
     * @return
     */
    @Override
    public Boolean updateUserBySelf(Long userId, AnyUserUpdateRequest anyUserUpdateRequest) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, userId)
                .set(StringUtils.isNotBlank(anyUserUpdateRequest.getUserName()),User::getUserName, anyUserUpdateRequest.getUserName())
                .set(anyUserUpdateRequest.getGender()!=null,User::getGender, anyUserUpdateRequest.getGender());
        boolean result = this.update(wrapper);

        if (!result) {
            return false;
        }
        this.saveUserToRedis(userId);


        return true;
    }

    /**
     * 更新用户ak和sk
     *
     * @param id
     * @return
     */
    @Override
    public Boolean updateUserAkSk(Long id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Map map = null;
        try {
            map = NonCollidingAccessKeyGenerator.generAkAndSk();
        } catch (Exception e) {
            log.error("生成随机ak和sk失败");
            return false;

        }

        boolean update = this.update(Wrappers.<User>lambdaUpdate().eq(User::getId, id)
                .set(User::getAccessKey, map.get("accessKey"))
                .set(User::getSecretKey, map.get("secretKey")));


        this.saveUserToRedis(id);
        return update;

    }

    @Override
    public UserProfileVo getUserProfile(User user, String account) {
        UserProfileVo userProfileVo = new UserProfileVo();

        User profileUserInfo = this.getOne(Wrappers.<User>lambdaQuery().eq(User::getUserAccount, account));
        if (profileUserInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }

        if (profileUserInfo.getId().equals(user.getId())) {
            userProfileVo.setSelf(true);
        }

        userProfileVo = setUserInfo(userProfileVo, profileUserInfo);

        userProfileVo = getUserInterfaceInfo(userProfileVo, profileUserInfo);

        return userProfileVo;
    }

//    @Override
//    @Transactional
//    public boolean saveUserAvatars(MultipartFile file, User user) {
////        为头像生成一个唯一文件名
//        String fileNamePre = IdUtil.simpleUUID();
//        String fileName = file.getOriginalFilename();
//        String suffix = fileName.substring(fileName.lastIndexOf("."));
//        String avatarPath = fileNamePre + suffix;
//
////        把旧的覆盖掉
//        if (user.getUserAvatar() != null) {
//            File avatarAbsPath = getAvatarAbsPath(user.getUserAvatar());
//            if (avatarAbsPath.exists() && avatarAbsPath.isFile()) {
//                avatarAbsPath.delete();
//            }
//        }
//
//        String result = saveFileToDisk(avatarPath, file);
//        if (!StringUtils.isNotBlank(result)) {
//            log.info("文件保存失败，抛出异常，使用事务");
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件保存失败");
//        }
//
//        return this.update(Wrappers.<User>lambdaUpdate()
//                .eq(User::getId, user.getId())
//                .set(User::getUserAvatar, avatarPath));
//    }
//


    @Override
    @Transactional
    public boolean saveUserAvatars(MultipartFile file, User user) {

        String avatarPath = ossService.uploadImage(file, user.getId(), USER_AVATAR);

        return this.update(Wrappers.<User>lambdaUpdate()
                .eq(User::getId, user.getId())
                .set(User::getUserAvatar, avatarPath));
    }
//    @Override
//    public File getLoginUserAvatar(User user) {
//
//        String userAvatar = user.getUserAvatar();
//        return getAvatarAbsPath(userAvatar);
//    }


    @Override
    public InputStream getLoginUserAvatar(User user) {
        String userAvatar = user.getUserAvatar();
        return ossService.getImage(userAvatar);

    }

    @Override
    public boolean addUserCredit(Long userId, Integer addCredit) {
        if(userId == null || addCredit == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户id和要增加的积分不能为空");
        }
        User user = JSONUtil.toBean(redisService.get(USER_ID_PREFIX + userId), User.class);
        Integer oldCredits = user.getCredits();
        this.update(Wrappers.<User>lambdaUpdate().eq(User::getId, userId).set(User::getCredits, oldCredits + addCredit));
//        更新Redis缓存
        this.saveUserToRedis(userId);

        return true;
    }

    /**
     * 根据不同的系统获取头像的绝对路径
     * @param userAvatar
     * @return
     */
    private File getAvatarAbsPath(String userAvatar) {

        String path = null;
//        获取当前操作系统
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            path = UserConstant.SAVE_AVATAR_WIN_PRE_PATH;
        } else {
            path = UserConstant.SAVE_AVATAR_LINUX_PRE_PATH;
        }

        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        return new File(path + "/" + userAvatar);
    }

    /**
     * 保存文件到磁盘 TODO（后面升级为保存到oss中）
     *
     * @param fileNameNew
     * @param file
     * @return
     */
    private String saveFileToDisk(String fileNameNew, MultipartFile file) {
        File avaFile = getAvatarAbsPath(fileNameNew);
        try {
            file.transferTo(avaFile);
            return fileNameNew;
        } catch (IOException e) {
            log.error("文件保存失败", e);
            return null;
        }

    }

    private UserProfileVo getUserInterfaceInfo(UserProfileVo userProfileVo, User profileUserInfo) {
//        查询数据库获取可用接口数量和接口被调用总次数
        InterfaceAllCountAndCallCount result = interfaceinfoMapper.getAllInterCountAndCallCount(profileUserInfo.getId());

        if (result != null) {
            userProfileVo.setCanUseInterfaceNum(result.getCanUseInterfaceNum());
            userProfileVo.setInterfaceTransferNum(result.getInterfaceTransferNum());
        } else {
            userProfileVo.setCanUseInterfaceNum(0);
            userProfileVo.setInterfaceTransferNum(0);
        }

        return userProfileVo;
    }

    /**
     * 赋值
     *
     * @param userProfileVo
     * @param profileUserInfo
     * @return
     */
    private UserProfileVo setUserInfo(UserProfileVo userProfileVo, User profileUserInfo) {
        BeanUtils.copyProperties(profileUserInfo, userProfileVo);
        return userProfileVo;
    }

}




