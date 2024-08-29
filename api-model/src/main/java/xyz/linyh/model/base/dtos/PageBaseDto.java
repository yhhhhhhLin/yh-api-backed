package xyz.linyh.model.base.dtos;

import lombok.Data;
import xyz.linyh.ducommon.constant.CommonConstant;

/**
 * 分页请求
 *
 *
 */
@Data
public class PageBaseDto {

    /**
     * 当前页号
     */
    private long current = 1;

    /**
     * 页面大小
     */
    private long pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序（默认升序）
     */
    private String sortOrder = CommonConstant.SORT_ORDER_ASC;


    /**
     * 用来校验传入的请求参数是否合法
     */
    public void check(){
        if (current < 1) {
            current = 1;
        }
        if (pageSize < 1) {
            pageSize = 10;
        }
    }
}
