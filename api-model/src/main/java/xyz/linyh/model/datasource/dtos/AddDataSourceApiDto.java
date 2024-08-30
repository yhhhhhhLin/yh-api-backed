package xyz.linyh.model.datasource.dtos;

import lombok.Data;
import xyz.linyh.model.datasource.vos.ColumnBriefVO;
import xyz.linyh.model.interfaceInfoDispatchInfo.dtos.DispatchInfoDto;

import java.util.List;

/**
 * TODO
 */
@Data
public class AddDataSourceApiDto {

    /**
     * 数据源id
     */
    private Long dscId;

    private String schemaName;

    private String tableName;

    /**
     * 要查询的列信息
     */
    private List<ColumnBriefVO> searchColumns;

    /**
     * 调度配置
     */
    private DispatchInfoDto dispatchInfo;


}
