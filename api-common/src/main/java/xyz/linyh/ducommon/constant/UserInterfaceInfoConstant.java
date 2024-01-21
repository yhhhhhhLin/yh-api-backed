package xyz.linyh.ducommon.constant;

public interface UserInterfaceInfoConstant {

//    对应用户可以用这个接口
    Integer CAN_USE = 1;

//    对应用户不可以用这个接口
    Integer NOT_CAN_USE = 0;

    /**
     * 支付类型：免费体验次数
     */
    String INTERFACE_INVOKE_PAY_TYPE_EXPERIENCE = "1";

    /**
     * 支付类型：积分
     */
    String INTERFACE_INVOKE_PAY_TYPE_CREDITS = "2";
}
