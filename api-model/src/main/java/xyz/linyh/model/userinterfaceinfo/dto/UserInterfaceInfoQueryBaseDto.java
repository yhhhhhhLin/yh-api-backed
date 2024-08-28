package xyz.linyh.model.userinterfaceinfo.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import xyz.linyh.model.apiweatherInterface.dtos.PageBaseDto;

import java.io.Serializable;

/**
 * 查询请求
 *
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserInterfaceInfoQueryBaseDto extends PageBaseDto implements Serializable {

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