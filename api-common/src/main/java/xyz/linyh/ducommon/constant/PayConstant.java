package xyz.linyh.ducommon.constant;

public interface PayConstant {

    public Integer DEFAULT_CREDIT = 20;

    /**
     * 订单状态之未支付
     */
    public String ORDER_STATIC_UNPAID = "0";

    /**
     * 订单状态之已支付
      */
    public String ORDER_STATIC_PAID = "1";

    /**
     * 订单状态之已取消
     */
    public String ORDER_STATIC_CANCEL = "2";

    /**
     * 订单状态之已删除
     */
    public String ORDER_STATIC_DELETED = "3";

    /**
     * 支付状态阿里支付
     */
    public String PAY_TYPE_ALIPAY = "1";

    /**
     * 支付状态之微信支付
     */
    public String PAY_TYPE_WECHATPAY = "2";
}
