package xyz.linyh.yhapi.datasource;

import xyz.linyh.model.datasource.entitys.DscInfo;
import xyz.linyh.model.datasource.vos.ColumnBriefVO;

import java.util.List;

public interface DataSourceClient {

    Boolean testConnection();

    /**
     * 获取所有列名
     * @param schemaName
     * @param tableName
     * @return
     */
    List<ColumnBriefVO> listColumns(String schemaName, String tableName);

    void executeSql(DscInfo dscInfo, String sql);
}
