package xyz.linyh.model.dscInterfaceColumn.entitys;

import com.baomidou.mybatisplus.annotation.*;

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
     * 原先表的字段名称
     */
    private String columnName;

    /**
     * comment
     */
    private String columnComment;

    /**
     * 创建结果表的字段名称 t0_columnName t1_columnName
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
     * 逻辑删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}