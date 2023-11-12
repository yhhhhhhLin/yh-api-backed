package xyz.linyh.model.userinterfaceinfo.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 更新请求
 *
 * @TableName product
 */
@Data
public class UserInterfaceInfoUpdateRequest implements Serializable {

    /**
     * id
     */
    @TableId
    private Long id;

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
     * 共调用多少次
     */
    private Integer allNum;

    /**
     * 用户是否能调用这个接口 0为可用 1为不可用
     */
    private Integer status;


    private static final long serialVersionUID = 1L;
}