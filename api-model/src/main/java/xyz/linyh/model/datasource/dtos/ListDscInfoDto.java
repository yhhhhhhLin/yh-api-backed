package xyz.linyh.model.datasource.dtos;

import lombok.Data;
import xyz.linyh.model.base.dtos.PageBaseDto;

@Data
public class ListDscInfoDto extends PageBaseDto {
    /**
     * 数据源类型
     */
    private Integer dscType;

    /**
     * 状态（0-不可用）
     */
    private Integer status;

//    TODO 一些查询条件
}
