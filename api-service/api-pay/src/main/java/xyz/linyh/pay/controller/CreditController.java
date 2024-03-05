package xyz.linyh.pay.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.linyh.ducommon.common.BaseResponse;
import xyz.linyh.ducommon.common.ErrorCode;
import xyz.linyh.ducommon.common.ResultUtils;
import xyz.linyh.ducommon.constant.UserConstant;
import xyz.linyh.model.pay.eneity.UserCredits;
import xyz.linyh.pay.service.UserCreditsService;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
@RestController
public class CreditController {

    @Autowired
    private UserCreditsService userCreditsService;

    @GetMapping("/getCharge")
    public BaseResponse getUserCharge(HttpServletRequest request) {
        String userId = request.getHeader(UserConstant.USER_Id);
        if (userId == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }

        UserCredits userCredit = userCreditsService.getById(String.valueOf(userId));
        if (userCredit != null) {
            return ResultUtils.success(userCredit);
        }

        return ResultUtils.error(ErrorCode.SYSTEM_ERROR);
    }
}
