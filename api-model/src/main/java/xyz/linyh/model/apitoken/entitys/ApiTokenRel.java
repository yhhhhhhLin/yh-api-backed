package xyz.linyh.model.apitoken.entitys;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import xyz.linyh.model.base.entitys.BaseEntity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author linzz
 */
@TableName(value = "apiTokenRel")
@Data
public class ApiTokenRel extends BaseEntity implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long interfaceId;

    private String token;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
