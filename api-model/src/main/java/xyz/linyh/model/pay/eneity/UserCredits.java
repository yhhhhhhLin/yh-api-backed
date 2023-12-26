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
 * @TableName usercredits
 */
@TableName(value ="UserCredits")
@EqualsAndHashCode
@Data
public class UserCredits implements Serializable {
    private Long id;

    private Long userid;

    private Integer credit;

    private Date createTime;

    private Date updateTime;

    private Integer isDelete;

    private static final long serialVersionUID = 1L;

}