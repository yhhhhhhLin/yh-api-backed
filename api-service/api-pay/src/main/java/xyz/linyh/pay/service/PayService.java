package xyz.linyh.pay.service;

/**
 * 支付接口
 */
public interface PayService {

    /**
     * 支付宝沙箱支付
     * @param orderId
     * @return
     */
    public String PayCreditByAliSandBox(String orderId);
}
