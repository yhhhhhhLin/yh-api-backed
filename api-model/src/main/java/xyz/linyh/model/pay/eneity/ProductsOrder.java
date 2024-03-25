package xyz.linyh.model.pay.eneity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName productsorder
 */
@TableName(value ="productsOrder")
@Data
@EqualsAndHashCode
public class ProductsOrder implements Serializable {

    /**
     * 数据对应的id
     */
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 没用
     */
    private String codeUrl;

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
     * 产品类型
     */
    private String productInfo;

    private String formData;

    private Long addPoints;

    private Date expirationTime;

    private Date createTime;

    private Date updateTime;

    private Integer isDelete;

    private static final long serialVersionUID = 1L;

}