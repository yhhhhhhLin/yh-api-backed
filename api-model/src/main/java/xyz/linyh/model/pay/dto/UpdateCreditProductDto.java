package xyz.linyh.model.pay.dto;

import lombok.Data;

@Data
public class UpdateCreditProductDto {

    private Long creditId;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 多少价格
     */
    private Integer price;

    /**
     * 多少积分
     */
    private Integer integral;

    /**
     * 商品图片地址
     */
    private String picture;

    /**
     * 折扣价格
     */
    private Integer discountPrice;

}
