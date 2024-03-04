package xyz.linyh.model.pay.eneity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName CreditOrder
 */
@TableName(value ="CreditOrder")
@Data
@EqualsAndHashCode
public class CreditOrder implements Serializable {

    /**
     * 数据对应的id
     */
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 产品id
     */
    private Long productId;

    /**+
     * 产品名称
     */
    private String orderName;

    /**
     * 价格
     */
    private Long total;

    /**
     * 状态
     */
    private String status;

    /**
     * 支付类型
     */
    private String payType;

    /**
     * 产品信息
     */
    private String productInfo;

    /**
     * 增加的积分
     */
    private Long addPoints;

    /**
     * 订单过期时间
     */
    private Long expirationTime;

    /**
     * 订单创建时间
     */
    private Long createTime;

    /**
     * 订单更新时间
     */
    private Long updateTime;

    private Integer isDelete;

    private static final long serialVersionUID = 1L;

}