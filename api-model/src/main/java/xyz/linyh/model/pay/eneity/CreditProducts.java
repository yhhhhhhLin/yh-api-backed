package xyz.linyh.model.pay.eneity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName creditproducts
 */
@TableName(value ="creditProducts")
@Data
public class CreditProducts implements Serializable {
    private Long id;

    private String description;

    private double price;

    private Integer integral;

    private String picture;

    private double discountPrice;

    private Date createTime;

    private Date updateTime;

    private Integer isDelete;

    private static final long serialVersionUID = 1L;

}