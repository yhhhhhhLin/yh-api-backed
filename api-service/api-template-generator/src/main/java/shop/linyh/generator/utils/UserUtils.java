package shop.linyh.generator.utils;

import javax.servlet.http.HttpServletRequest;

public class UserUtils {


    public static Long getLoginUserId(HttpServletRequest request) {
        return Long.valueOf(request.getHeader("userId"));
    }
}
