package xyz.linyh.yhapi.helper;

import xyz.linyh.model.datasource.vos.ColumnBriefVO;
import xyz.linyh.model.dscInterfaceColumn.entitys.DscInterfaceColumn;

import java.util.List;

public class MysqlGenSql extends GenSql{

    private final  String DROP_TABLE_SQL_TEMPLATE = "DROP TABLE IF EXISTS `%s`;";

    @Override
    protected String createDropTableSql(String tableName) {
        return String.format(DROP_TABLE_SQL_TEMPLATE, tableName);
    }

    @Override
    protected String createSelectSql(List<DscInterfaceColumn> columns) {
        return "";
    }

    @Override
    protected String createCreateTableSql(String tableName, List<DscInterfaceColumn> columns) {
        return "";
    }

    @Override
    protected String createInsertSql(String tableName, String selectSql) {
        return "";
    }
}
