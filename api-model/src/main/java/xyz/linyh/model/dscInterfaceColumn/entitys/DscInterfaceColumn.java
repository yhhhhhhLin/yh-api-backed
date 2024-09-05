package xyz.linyh.model.dscInterfaceColumn.entitys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 数据源接口和对应查询表字段关系表
 * @TableName dscinterfacecolumn
 */
@TableName(value ="dscInterfaceColumn")
@Data
public class DscInterfaceColumn implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 接口id
     */
    private Long interfaceInfoId;

    /**
     * schemaName
     */
    private String schemaName;

    /**
     * tableName
     */
    private String tableName;

    /**
     * 表别名 t0,t1...
     */
    private String tableAlias;

    /**
     * 
     */
    private String columnName;

    /**
     * comment
     */
    private String columnComment;

    /**
     * 字段别名 to_columnName t1_columnName
     */
    private String columnAlias;

    /**
     * 
     */
    private String columnType;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

    /**
     * 
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}