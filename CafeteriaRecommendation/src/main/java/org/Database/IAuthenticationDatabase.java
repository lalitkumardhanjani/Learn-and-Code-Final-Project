package org.Database;

import java.sql.Connection;
import java.sql.SQLException;

public interface IAuthenticationDatabase {
    Connection getConnection() throws SQLException;
    boolean isValidUser(int userId, String password, int role);
    void logLoginAttempt(int userId, boolean success);
    void logLogoutAttempt(int userId, boolean success);
    String getUserRole(int userId);
}
