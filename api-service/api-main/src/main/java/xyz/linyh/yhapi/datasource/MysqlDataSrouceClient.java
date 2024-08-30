package xyz.linyh.yhapi.datasource;

import xyz.linyh.model.datasource.entitys.DscInfo;
import xyz.linyh.model.datasource.vos.ColumnBriefVO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
}
