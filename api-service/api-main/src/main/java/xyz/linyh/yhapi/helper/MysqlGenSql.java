package xyz.linyh.yhapi.helper;

import xyz.linyh.model.datasource.vos.ColumnBriefVO;

import java.util.List;

public class MysqlGenSql extends GenSql{

    @Override
    protected String createDropTableSql(String tableName) {
        return "";
    }

    @Override
    protected String createSelectSql(List<ColumnBriefVO> columns) {
        return "";
    }

    @Override
    protected String createCreateTableSql(String tableName, List<ColumnBriefVO> columns) {
        return "";
    }

    @Override
    protected String createInsertSql(String tableName, String selectSql) {
        return "";
    }
}
