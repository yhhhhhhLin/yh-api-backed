package xyz.linyh.model.base.entitys;

import lombok.Data;

import java.util.Date;

@Data
public class BaseEntity {
    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

}
