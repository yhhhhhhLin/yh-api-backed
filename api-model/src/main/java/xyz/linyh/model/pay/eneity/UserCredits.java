package xyz.linyh.model.pay.eneity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * @TableName usercredits
 */
@TableName(value ="userCredits")
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