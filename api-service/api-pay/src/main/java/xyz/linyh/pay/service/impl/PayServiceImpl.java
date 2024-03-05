package xyz.linyh.pay.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.linyh.ducommon.common.ErrorCode;
import xyz.linyh.ducommon.constant.PayConstant;
import xyz.linyh.ducommon.exception.BusinessException;
import xyz.linyh.model.pay.eneity.CreditOrder;
import xyz.linyh.pay.config.AlipayProperties;
import xyz.linyh.pay.service.CreditOrderService;
import xyz.linyh.pay.service.PayService;

@Service
@Slf4j
public class PayServiceImpl implements PayService {


    @Autowired
    private AlipayClient myAlipayClient;

    @Autowired
    private CreditOrderService creditOrderService;

    @Override
    public String PayCreditByAliSandBox(String orderId) {

//        0. 参数校验
        if (StrUtil.isBlank(orderId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "订单号不能为空");
        }

//        1. 查询订单是否存在，并且订单状态需要为待支付
        CreditOrder creditOrder = creditOrderService.getOne(Wrappers.<CreditOrder>lambdaQuery().eq(CreditOrder::getOrderNo, orderId));
        if (creditOrder == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "订单不存在");
        }

        if (PayConstant.ORDER_STATIC_UNPAID.equals(creditOrder.getStatus())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "订单不是待支付订单");
        }

//        2. 发送付款请求
        //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
//        TODO 配置
        alipayRequest.setReturnUrl(AlipayProperties.return_url);
        alipayRequest.setNotifyUrl(AlipayProperties.notify_url);

//        商户订单号，必须唯一
        String out_trade_no = orderId;

        // 付款金额
        String total_amount = creditOrder.getTotal().toString();
        //订单名称
        String subject = creditOrder.getOrderName();
        //商品描述
        String body = creditOrder.getOrderName();

        alipayRequest.setBizContent("{\"out_trade_no\":\"" + out_trade_no + "\","
                + "\"total_amount\":\"" + total_amount + "\","
                + "\"subject\":\"" + subject + "\","
                + "\"body\":\"" + body + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        //请求
        try {
            return myAlipayClient.pageExecute(alipayRequest).getBody();
        } catch (AlipayApiException e) {
            log.error("支付宝支付失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "支付宝支付失败");
        }

    }
}
