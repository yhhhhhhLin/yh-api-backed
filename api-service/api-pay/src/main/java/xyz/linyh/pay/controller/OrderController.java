package xyz.linyh.pay.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.linyh.ducommon.common.BaseResponse;
import xyz.linyh.ducommon.common.ErrorCode;
import xyz.linyh.ducommon.common.ResultUtils;
import xyz.linyh.model.pay.dto.CreateCreditOrderDto;
import xyz.linyh.model.pay.eneity.CreditOrder;
import xyz.linyh.pay.service.CreditOrderService;
import xyz.linyh.pay.utils.UserUtils;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private CreditOrderService creditOrderService;

    @PostMapping("/credit/create")
    public BaseResponse createCreditOrder(@RequestBody CreateCreditOrderDto dto, HttpServletRequest request) {

        Long userId = UserUtils.getLoginUserId(request);
        if (userId <= 0L) {
            return ResultUtils.error(ErrorCode.NOT_LOGIN_ERROR, "用户未登录,获取不到用户id");
        }

        CreditOrder creditOrder = creditOrderService.createCreditOrder(dto, userId);
        return ResultUtils.success(creditOrder);
    }

    /**
     * 对订单进行支付
     * @param orderNum
     * @return
     */
    @GetMapping("/pay")
    public BaseResponse payOrder(@RequestParam("orderNum") String orderNum,String type) {

        return null;
    }
}
