package xyz.linyh.model.pay.dto;

import lombok.Data;

@Data
public class CreateCreditOrderDto {

    /**
     * 产品id
     */
    private Long productId;

    /**
     * 产品数量
     */
    private Integer num;

    /**
     * 支付类型
     */
    private Integer payType;
}
