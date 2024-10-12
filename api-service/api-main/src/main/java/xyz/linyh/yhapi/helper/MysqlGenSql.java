package xyz.linyh.yhapi.helper;

import org.apache.commons.lang3.StringUtils;
import xyz.linyh.model.dscInterfaceColumn.entitys.DscInterfaceColumn;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MysqlGenSql extends GenSql {

    private final String DROP_TABLE_SQL_TEMPLATE = "DROP TABLE IF EXISTS `%s`";

    private final String SELECT_SQL_TEMPLATE = "SELECT %s FROM `%s`";

    private String CREATE_TABLE_SQL_TEMPLATE = "create table if not exists %s(%s) ";

    private String SELECT_INSERT_TABLE_SQL_TEMPLATE = "insert into %s %s";

    private final String RENAME_TABLE_SQL_TEMPLATE = "rename %s to %s";

    @Override
    protected String createDropTableSql(String tableName) {
        return String.format(DROP_TABLE_SQL_TEMPLATE, tableName);
    }

    @Override
    protected String createSelectSqlWithPtAndPk(List<DscInterfaceColumn> columns) {
//        TODO 目前只有一个表
        DscInterfaceColumn dscInterfaceColumn = columns.get(0);
        String tableName = dscInterfaceColumn.getTableName();
        String tableNameAndAlias = String.format("%s %s", tableName, dscInterfaceColumn.getTableAlias());
//        t0.columnName columnAlias
        List<String> columnsFields = columns.stream().filter(this::notPkAndPt)
                .map(t -> String.format("%s.%s %s", t.getTableAlias(), t.getColumnName(), t.getColumnAlias()))
                .collect(Collectors.toList());

        String columnsStringExpectPkAndPt = StringUtils.join(columnsFields, ",");
        String columnsString = String.format("%s,%s as pk,%s as pt", columnsStringExpectPkAndPt,
                UUID.randomUUID().toString(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        return String.format(SELECT_SQL_TEMPLATE, columnsString, tableNameAndAlias);
    }

    private Boolean notPkAndPt(DscInterfaceColumn column) {
        return !"pk".equals(column.getColumnAlias()) && "pt".equals(column.getColumnAlias());
    }

    @Override
    protected String createCreateTableSql(String tableName, List<DscInterfaceColumn> columns) {
//        TODO 列名为每个原先表的列的别名
        List<DscInterfaceColumn> allColumns = supplyBasicColumns(columns);
        String columnsSql = genColumnSqlFragment(allColumns);
        return String.format(CREATE_TABLE_SQL_TEMPLATE, tableName, columnsSql);
    }

    private String genColumnSqlFragment(List<DscInterfaceColumn> columns) {
        return columns.stream().map(column -> {
            StringBuilder sb = new StringBuilder();
            String columnType = column.getColumnType();
            String columnAlias = column.getColumnAlias();
            sb.append(columnAlias);
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
        pk.setColumnAlias("主键");
        pk.setColumnType("varchar(255)");
        allColumns.add(pk);
        DscInterfaceColumn pt = new DscInterfaceColumn();
        pt.setTableName(columns.get(0).getTableName());
        pt.setColumnName("pt");
        pt.setColumnAlias("业务日期");
        pt.setColumnType("varchar(255)");
        allColumns.add(pt);
        return allColumns;
    }

    @Override
    protected String createInsertSql(String tableName, List<DscInterfaceColumn> columns) {
        List<DscInterfaceColumn> allColumns = supplyBasicColumns(columns);
//        还需要添加uuid和时间字段
        String selectSql = createSelectSqlWithPtAndPk(allColumns);
//        insert into %s
//        select table t0.columnName1 columnAlas1, t0.columnName2 columnAlas2,uuid as pk, time as pt
//        from srcTable
        return String.join(SELECT_INSERT_TABLE_SQL_TEMPLATE, tableName, selectSql);
    }

    @Override
    public String renameTable(String srcTableName, String destTableName) {
        return String.format(RENAME_TABLE_SQL_TEMPLATE, srcTableName, destTableName);
    }
}
