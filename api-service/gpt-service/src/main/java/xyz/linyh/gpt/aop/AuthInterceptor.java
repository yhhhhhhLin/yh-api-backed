package xyz.linyh.gpt.aop;

import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import xyz.linyh.dubboapi.service.DubboUserService;
import xyz.linyh.ducommon.annotation.AuthCheck;
import xyz.linyh.ducommon.common.ErrorCodeEnum;
import xyz.linyh.ducommon.constant.UserConstant;
import xyz.linyh.ducommon.exception.BusinessException;
import xyz.linyh.model.enums.UserRoleEnum;
import xyz.linyh.model.user.entitys.User;

import javax.servlet.http.HttpServletRequest;

/**
 * 权限校验 AOP
 *
 * @author lin
 */
@Aspect
@Component
public class AuthInterceptor {

    @DubboReference
    private DubboUserService userService;

    /**
     * 执行拦截，用来判断是否是管理员或是否被封号 aop
     *
     * @param joinPoint 1
     * @param authCheck 1
     * @return 1
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        String mustRole = authCheck.mustRole();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        String userId = request.getHeader(UserConstant.USER_Id);
        // 当前登录用户
        User loginUser = userService.getLoginUser(userId);

        // 必须有该权限才通过
        if (StringUtils.isNotBlank(mustRole)) {
            UserRoleEnum mustUserRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
            if (mustUserRoleEnum == null) {
                throw new BusinessException(ErrorCodeEnum.NO_AUTH_ERROR);
            }

            String userRole = loginUser.getUserRole();
            // 如果被封号，直接拒绝
            if (UserRoleEnum.BAN.equals(mustUserRoleEnum)) {
                throw new BusinessException(ErrorCodeEnum.NO_AUTH_ERROR);
            }

            // 必须有管理员权限
            if (UserRoleEnum.ADMIN.equals(mustUserRoleEnum)) {
                if (!mustRole.equals(userRole)) {
                    throw new BusinessException(ErrorCodeEnum.NO_AUTH_ERROR);
                }
            }
        }

        // 通过权限校验，放行
        return joinPoint.proceed();
    }
}

