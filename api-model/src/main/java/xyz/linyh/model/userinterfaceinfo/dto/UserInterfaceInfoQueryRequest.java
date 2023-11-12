package xyz.linyh.model.userinterfaceinfo.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import xyz.linyh.ducommon.common.PageRequest;

import java.io.Serializable;

/**
 * 查询请求
 *
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserInterfaceInfoQueryRequest extends PageRequest implements Serializable {

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
     * 用户是否能调用这个接口 0为可用 1为不可用
     */
    private Integer status;


    private static final long serialVersionUID = 1L;
}