package xyz.linyh.pay.controller;

import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.linyh.ducommon.common.BaseResponse;
import xyz.linyh.ducommon.common.ErrorCode;
import xyz.linyh.ducommon.common.ResultUtils;
import xyz.linyh.ducommon.constant.PayConstant;
import xyz.linyh.ducommon.exception.BusinessException;
import xyz.linyh.model.pay.dto.CreateCreditOrderDto;
import xyz.linyh.model.pay.eneity.CreditOrder;
import xyz.linyh.pay.service.CreditOrderService;
import xyz.linyh.pay.service.PayService;
import xyz.linyh.pay.utils.UserUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 订单模块（创建订单可以，支付订单...）
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private CreditOrderService creditOrderService;

    @Autowired
    private PayService payService;

    /**
     * 创建购买积分的订单
     * @param dto 相关参数
     * @param request
     * @return
     */
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
     * 对积分订单进行支付
     * @param orderNum
     * @return
     */
    @GetMapping("/pay")
    public String payOrder(@RequestParam("orderNum") String orderNum,String type) {
        if(StrUtil.isBlank(orderNum) || StrUtil.isBlank(type)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "订单号和支付方式不能为空");
        }

        switch (type) {
            case PayConstant.PAY_TYPE_ALIPAY:
                return payService.PayCreditByAliSandBox(orderNum);
            case PayConstant.PAY_TYPE_WECHATPAY:
                break;
        }
        return null;
    }
}
