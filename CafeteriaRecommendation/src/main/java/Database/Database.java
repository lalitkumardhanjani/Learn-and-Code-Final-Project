package Database;

import java.sql.Connection;
import java.sql.SQLException;

public interface Database {
    Connection getConnection() throws SQLException;
    boolean checkCredentials(int userId, String password);
    void saveLoginAttempt(int userId, boolean success);
    void saveLogoutAttempt(int userId, boolean success);
}
