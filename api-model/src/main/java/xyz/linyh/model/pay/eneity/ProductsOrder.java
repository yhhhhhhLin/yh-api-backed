package xyz.linyh.model.pay.eneity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @TableName productsorder
 */
@TableName(value ="ProductsOrder")
@Data
@EqualsAndHashCode
public class ProductsOrder implements Serializable {
    private Long id;

    private String orderNo;

    private String codeUrl;

    private Long userId;

    private Long productId;

    private String orderName;

    private Long total;

    private String status;

    private String payType;

    private String productInfo;

    private String formData;

    private Long addPoints;

    private Date expirationTime;

    private Date createTime;

    private Date updateTime;

    private Integer isDelete;

    private static final long serialVersionUID = 1L;

}