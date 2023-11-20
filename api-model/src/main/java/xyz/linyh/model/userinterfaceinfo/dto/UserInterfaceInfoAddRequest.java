package xyz.linyh.model.userinterfaceinfo.dto;

import lombok.Data;
import xyz.linyh.model.user.entitys.User;

import java.io.Serializable;

/**
 * 创建请求
 *
 * @TableName product
 */
@Data
public class UserInterfaceInfoAddRequest implements Serializable {



    /**
     * 用户id
     */
    private Long userId;

    /**
     * 接口id
     */
    private Long interfaceId;

    /**
     * 剩余调用次数
     */
    private Integer remNum;



    /**
     * 用户是否能调用这个接口 0为可用 1为不可用
     */
    private Integer status;

    User user;




    private static final long serialVersionUID = 1L;
}