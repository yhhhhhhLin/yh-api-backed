package xyz.linyh.pay.utils;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class UserUtils {


    public static Long getLoginUserId(HttpServletRequest request) {
        return Long.valueOf(request.getHeader("userId"));
    }
}
