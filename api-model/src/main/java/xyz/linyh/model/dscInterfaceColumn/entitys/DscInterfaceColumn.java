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
    private Integer id;

    /**
     * 接口id
     */
    private Integer interfaceInfoId;

    /**
     * schemaName
     */
    private String schemaName;

    /**
     * tableName
     */
    private String tableName;

    /**
     * 
     */
    private String columnName;

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