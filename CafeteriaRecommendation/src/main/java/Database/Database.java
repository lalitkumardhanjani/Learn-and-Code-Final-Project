package Database;

import javax.persistence.criteria.CriteriaBuilder;
import java.sql.Connection;
import java.sql.SQLException;

public interface Database {
    Connection getConnection() throws SQLException;
    boolean checkCredentials(int userId, String password);
    void LogLoginAttempt(int userId, boolean success);
    void LogLogoutAttempt(int userId, boolean success);
    String getUserRole(int userId);
    boolean createMenuItem(String name, double price, Integer MealType);

    boolean isMenuItemExist(String name);
}