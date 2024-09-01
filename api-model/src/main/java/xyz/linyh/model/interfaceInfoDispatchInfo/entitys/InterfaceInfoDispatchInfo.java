package xyz.linyh.model.interfaceInfoDispatchInfo.entitys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 数据源接口调度信息
 * @TableName interfaceinfodispatchinfo
 */
@TableName(value ="interfaceInfoDispatchInfo")
@Data
public class InterfaceInfoDispatchInfo implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private Long interfaceInfoId;

    /**
     * 调度周期code
     */
    private Integer dispatchPeriod;

    /**
     * 执行成功时间 yyyy-MM-dd HH:mm
     */
    private String successTime;

    /**
     * 具体时间 HH:mm
     */
    private String specTime;

    /**
     * 状态
     */
    private Integer status;

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