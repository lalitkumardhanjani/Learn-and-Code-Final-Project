package Database;

import javax.persistence.criteria.CriteriaBuilder;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface Database {
    Connection getConnection() throws SQLException;
    boolean checkCredentials(int userId, String password, int role);
    void logLoginAttempt(int userId, boolean success);
    void logLogoutAttempt(int userId, boolean success);
    String getUserRole(int userId);
    //boolean createMenuItem(String name, double price, Integer MealType,int availability);

    void createMenuItem(String name, double price, Integer mealType, int availability);
    List<String> getMenuItems();
    public void updateMenuItem(int menuId, String newName, double newPrice, int newAvailability);

    boolean isValidMenuId(int menuId);

    public boolean deleteMenuItem(int menuId);

}