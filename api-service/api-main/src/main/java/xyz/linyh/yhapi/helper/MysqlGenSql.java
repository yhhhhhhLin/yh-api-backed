package xyz.linyh.yhapi.helper;

import org.apache.commons.lang3.StringUtils;
import xyz.linyh.model.datasource.vos.ColumnBriefVO;
import xyz.linyh.model.dscInterfaceColumn.entitys.DscInterfaceColumn;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MysqlGenSql extends GenSql{

    private final  String DROP_TABLE_SQL_TEMPLATE = "DROP TABLE IF EXISTS `%s`";

    private final String SELECT_SQL_TEMPLATE = "SELECT %s FROM `%s`";

    private String CREATE_TABLE_SQL_TEMPLATE = "create table if not exists %s(%s) ";

    private String INSERT_TABLE_SQL_TEMPLATE = "insert into %s values(%s)";

    @Override
    protected String createDropTableSql(String tableName) {
        return String.format(DROP_TABLE_SQL_TEMPLATE, tableName);
    }

    @Override
    protected String createSelectSql(List<DscInterfaceColumn> columns) {
        DscInterfaceColumn dscInterfaceColumn = columns.get(0);
        String tableName = dscInterfaceColumn.getTableName();
        List<String> columnsFields = getAllColumnsField(columns);
        String columnsString = StringUtils.join(columnsFields, ",");

        return String.format(SELECT_SQL_TEMPLATE, columnsString, tableName);
    }

    private List<String> getAllColumnsField(List<DscInterfaceColumn> columns) {
        return columns.stream()
                .map(DscInterfaceColumn::getColumnName)
                .collect(Collectors.toList());
    }

    @Override
    protected String createCreateTableSql(String tableName, List<DscInterfaceColumn> columns) {
        List<DscInterfaceColumn> allColumns = supplyBasicColumns(columns);
        String columnsSql = genColumnSqlFragment(allColumns);
        return String.format(CREATE_TABLE_SQL_TEMPLATE, columnsSql, tableName);
    }

    private String genColumnSqlFragment(List<DscInterfaceColumn> columns) {
        return columns.stream().map(column->{
            StringBuilder sb = new StringBuilder();
            String columnName = column.getColumnName();
            String columnType = column.getColumnType();
            sb.append(columnName);
            sb.append(" ");
            sb.append(columnType);
            return sb.toString();
        }).collect(Collectors.joining(","));
    }

    private List<DscInterfaceColumn> supplyBasicColumns(List<DscInterfaceColumn> columns) {
        List<DscInterfaceColumn> allColumns = new ArrayList<>(columns);
        DscInterfaceColumn pk = new DscInterfaceColumn();
        pk.setTableName(columns.get(0).getTableName());
        pk.setColumnName("pk");
        pk.setColumnType("varchar(255)");
        allColumns.add(pk);
        return allColumns;
    }

    @Override
    protected String createInsertSql(String tableName, List<DscInterfaceColumn> columns) {
        List<DscInterfaceColumn> allColumns = supplyBasicColumns(columns);

//        return String.join(INSERT_TABLE_SQL_TEMPLATE, tableName, selectSql);
    }
}
