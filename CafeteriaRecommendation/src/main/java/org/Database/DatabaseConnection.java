package org.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.Constant.Constant;

public class DatabaseConnection {
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(Constant.DB_URL, Constant.DB_USER, Constant.DB_PASSWORD);
    }
}
