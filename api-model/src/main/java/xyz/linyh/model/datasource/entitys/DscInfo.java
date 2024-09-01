package xyz.linyh.model.datasource.entitys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import xyz.linyh.model.base.entitys.BaseEntity;

import java.io.Serializable;

/**
 * 数据库连接表
 * @TableName dscinfo
 */
@TableName(value ="dscInfo")
@Data
public class DscInfo extends BaseEntity implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 数据库名称
     */
    private String schemaName;

    /**
     * 数据库类型
     */
    private Integer dscType;

    /**
     * 连接地址
     */
    private String url;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 状态（0不可用，1可用)
     */
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}