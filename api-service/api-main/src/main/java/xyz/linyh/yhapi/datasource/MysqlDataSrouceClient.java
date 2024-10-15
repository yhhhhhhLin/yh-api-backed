package xyz.linyh.yhapi.datasource;

import org.springframework.transaction.annotation.Transactional;
import xyz.linyh.ducommon.common.ErrorCodeEnum;
import xyz.linyh.ducommon.exception.BusinessException;
import xyz.linyh.model.datasource.entitys.DscInfo;
import xyz.linyh.model.datasource.vos.ColumnBriefVO;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MysqlDataSrouceClient implements DataSourceClient {

    private String url;
    private String username;
    private String password;

    public MysqlDataSrouceClient(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }


    @Override
    public Boolean testConnection() {
        Connection connection = getConnection(url, username, password);
        return connection != null;
    }

    @Override
    public List<ColumnBriefVO> listColumns(String schemaName, String tableName) {
        Connection connection = getConnection(url, username, password);
        if (connection == null) {
            return new ArrayList<>();
        }

        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, schemaName, tableName);

            List<ColumnBriefVO> columnBriefVos = new ArrayList<>();
            while (columns.next()) {
                ColumnBriefVO columnBriefVO = new ColumnBriefVO();
                String columnName = columns.getString("COLUMN_NAME");
                String columnType = columns.getString("TYPE_NAME");
                columnBriefVO.setColumnName(columnName);
                columnBriefVO.setColumnType(columnType);
                columnBriefVO.setSchemaName(schemaName);
                columnBriefVO.setTableName(tableName);
                columnBriefVos.add(columnBriefVO);
            }

            return columnBriefVos;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>(0);
        }
    }

    private Connection getConnection(String url, String username, String password) {

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void executeSql(DscInfo dscInfo, String sql) {
        Connection connection = getConnection(dscInfo.getUrl(), dscInfo.getUsername(), dscInfo.getPassword());

        String[] sqls = sql.split(";");
        if (connection != null) {
            Statement statement = null;
            try {
                statement = connection.createStatement();
                for (String stmt : sqls) {
                    if (!stmt.trim().isEmpty()) {
                        statement.execute(stmt.trim());
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Map<String, Object> executeSqlReturn(DscInfo dscInfo, String sql) {
        Map<String, Object> resultMap = new HashMap<>();
        if (sql == null || sql.trim().isEmpty()) {
            // 返回空的结果，避免执行空的SQL
            return resultMap;
        }

        try (Connection connection = getConnection(dscInfo.getUrl(), dscInfo.getUsername(), dscInfo.getPassword());
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql.trim())) {

            // 获取元数据
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // 遍历结果集并填充Map
            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnLabel(i), resultSet.getObject(i));
                }
                // 假设你需要将每一行的数据放到Map中，行号作为key
                resultMap.put("row" + resultSet.getRow(), row);
            }

        } catch (SQLException e) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "SQL执行失败");
        }

        return resultMap;
    }

    @Override
    public Map<String, String> selectTableByNamePrefix(DscInfo dscInfo, String schemaName, String tableNameExp) {
        Map<String, String> resultMap = new HashMap<>();

        // 判断schemaName 或 tableNameExp 为空的情况
        if (schemaName == null || schemaName.trim().isEmpty() || tableNameExp == null || tableNameExp.trim().isEmpty()) {
            return resultMap;
        }

        // 生成 SQL 语句
        String sql = String.format(
                "SELECT table_name FROM information_schema.tables WHERE table_schema = '%s' AND table_name LIKE '%%%s%%'",
                schemaName.trim(), tableNameExp.trim()
        );

        // 执行 SQL 语句
        try (Connection connection = getConnection(dscInfo.getUrl(), dscInfo.getUsername(), dscInfo.getPassword());
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            // 将结果集中的表名添加到 Map
            int index = 1;
            while (resultSet.next()) {
                String tableName = resultSet.getString("table_name");
                resultMap.put("table" + index, tableName);
                index++;
            }

        } catch (SQLException e) {
            throw new BusinessException(ErrorCodeEnum.PARAMS_ERROR, "查询表名失败");
        }

        return resultMap;
    }
}
