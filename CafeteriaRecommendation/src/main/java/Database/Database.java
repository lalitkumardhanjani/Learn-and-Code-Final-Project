package Database;

import java.sql.Connection;
import java.sql.SQLException;

public interface Database {
    Connection getConnection() throws SQLException;
    boolean checkCredentials(int userId, String password);
    void LogLoginAttempt(int userId, boolean success);
    void LogLogoutAttempt(int userId, boolean success);
}
