package xyz.linyh.yhapi.datasource;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            return connection != null;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
