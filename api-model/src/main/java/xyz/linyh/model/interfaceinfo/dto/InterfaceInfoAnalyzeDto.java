package xyz.linyh.model.interfaceinfo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.linyh.ducommon.constant.CommonConstant;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterfaceInfoAnalyzeDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 一次展示多少个
     */
    private Integer total;

    /**
     * 当前是第几页
     */
    private Integer current;

    /**
     * 排序顺序（默认降序）
     */
    private String sortOrder = CommonConstant.SORT_ORDER_DESC;

}
