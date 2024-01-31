package xyz.linyh.model.visit.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
public class VisitsResultVO {

    /**
     * 每一天的数据
     */
    private List<VisitResultVo> visitResultVos;

    /**
     * 访问总数
     */
    public Integer total;

    public VisitsResultVO(){
        this.visitResultVos = new ArrayList<>();
    }

    public void add(String date, Integer pv){
        this.visitResultVos.add(new VisitResultVo(date, pv));
    }

}

@Data
@AllArgsConstructor
@NoArgsConstructor
class VisitResultVo {

    /**
     * 访问日期
     */
    private String date;

    /**
     * 访问次数
     */
    private Integer pv;
}