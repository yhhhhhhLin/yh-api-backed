package xyz.linyh.pay.controller;

import cn.hutool.core.util.StrUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.linyh.ducommon.common.BaseResponse;
import xyz.linyh.ducommon.common.ErrorCode;
import xyz.linyh.ducommon.common.ResultUtils;
import xyz.linyh.ducommon.constant.PayConstant;
import xyz.linyh.ducommon.exception.BusinessException;
import xyz.linyh.model.pay.dto.CreateCreditOrderDto;
import xyz.linyh.model.pay.eneity.CreditOrder;
import xyz.linyh.pay.config.AliPayClientConfig;
import xyz.linyh.pay.service.CreditOrderService;
import xyz.linyh.pay.service.PayService;
import xyz.linyh.pay.utils.UserUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

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
     * 查询所有订单,只能获取到自己的订单
     * @param current 当前页
     * @param pageSize 页面大小
     * @param orderType 订单类型
     * @return
     */
    @GetMapping("/list")
    public BaseResponse listAllOrder(@RequestParam("current") Integer current,
                                     @RequestParam("pageSize") Integer pageSize,
                                     @RequestParam("orderType") String orderType,
                                     HttpServletRequest request) {
        if (StrUtil.isEmpty(orderType)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        if (current <= 0 || pageSize <= 0) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数错误");
        }

        Long userId = UserUtils.getLoginUserId(request);
        switch (orderType) {
            case "credit":
                Page<CreditOrder> creditOrderPage = creditOrderService.listAllCreditOrderByPage(current, pageSize,userId);
                return ResultUtils.success(creditOrderPage);
            case "goods":
//                TODO 还有其他产品没写
                List<Object> objects = null;
                return ResultUtils.success(objects);
            default:
                return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数错误");
        }


    }

    /**
     * 获取订单详情,只能获取到自己的订单
     * @param id 订单id
     * @param orderType 订单类型
     * @return
     */
    @GetMapping
    public BaseResponse getOrderById(@RequestParam("id") String id,
                                     @RequestParam("orderType") String orderType,
                                     HttpServletRequest request) {
        if (StrUtil.isEmpty(orderType) || StrUtil.isEmpty(id)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数错误");
        }

        Long userId = UserUtils.getLoginUserId(request);
        switch (orderType) {
            case "credit":
                CreditOrder creditOrder = creditOrderService.getCreditOrderById(id,userId);
                return ResultUtils.success(creditOrder);
            default:
                return null;
        }
    }

    /**
     * 删除订单,只能删除自己的订单
     * @param id
     * @return
     */
    @DeleteMapping
    public BaseResponse deleteOrderById(@RequestParam("id") String id,
                                        HttpServletRequest request) {
        if (StrUtil.isEmpty(id)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        Long userId = UserUtils.getLoginUserId(request);
        creditOrderService.deleteCreditOrderByUserIdAndId(id,userId);
        return ResultUtils.success("删除成功");
    }

    /**
     * 创建购买积分的订单
     *
     * @param dto     相关参数
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
     * 对积分订单进行支付 todo 还需要添加对应订单类型的前缀，以便后续支付成功后修改数据库可以修改订单状态和对应后续操作
     *
     * @param orderNum
     * @return
     */
    @GetMapping("/credit/pay")
    public String payOrder(@RequestParam("orderNum") String orderNum, String type) {
        if (StrUtil.isBlank(orderNum) || StrUtil.isBlank(type)) {
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


    @PostMapping("/url/notify")
    public void notifyUrl(@RequestParam Map<String, String> params) throws AlipayApiException {
//        1. 进行参数校验判断是否合法
        boolean checkResult = AlipaySignature.rsaCheckV1(params, AliPayClientConfig.static_alipay_public_key, AliPayClientConfig.static_charset, AliPayClientConfig.static_sign_type);
        if (!checkResult) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "验签失败");
        }
        System.out.println(params);
        creditOrderService.updateOrderStatusAndOpt(params);

    }

    @GetMapping("/url/return")
    public void returnUrl(String out_trade_no) {
        System.out.println("接收到return返回的，订单号为：" + out_trade_no);
    }


}
