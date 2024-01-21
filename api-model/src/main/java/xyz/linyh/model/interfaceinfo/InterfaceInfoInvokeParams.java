package xyz.linyh.model.interfaceinfo;

import lombok.Data;
import xyz.linyh.model.interfaceinfo.dto.InterfaceInfoInvokeRequest;

@Data
public class InterfaceInfoInvokeParams extends InterfaceInfoInvokeRequest {
    /**
     * 支付类型(1:免费体验次数，2：积分兑换)
     */
    private InterfaceInfoInvokePayType payType;
}
