package Database;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface Database {
    Connection getConnection() throws SQLException;
    boolean checkCredentials(int userId, String password, int role);
    void logLoginAttempt(int userId, boolean success);
    void logLogoutAttempt(int userId, boolean success);
    String getUserRole(int userId);
    void createMenuItem(String name, double price, Integer mealType, int availability);
    List<String> getMenuItems();
    void updateMenuItem(int menuId, String newName, double newPrice, int newAvailability);
    boolean isValidMenuId(int menuId);
    boolean deleteMenuItem(int menuId);
    List<String> generateRecommendedMenu();
    List<String> getRecommendedMenu() throws SQLException;
    List<String> getMenuItems(Connection conn, String mealType) throws SQLException;
    int rolloutRecommendation();
    List<String> getFinalizedMenu(int breakfastMenuItemId, int lunchMenuItemId, int dinnerMenuItemId) throws SQLException;
    int rolloutFinalizedMenusStatusUpdate();
    int insertSelectedFoodItemsInDB(BufferedReader in, PrintWriter out, List<Integer> ids);
    List<String> getSelectedFoodItemsEmployees() throws SQLException;
    int giveFoodFeedback(int FoodItemId, int Rating, String comment, int userId);
    List<String> getFoodFeedbackHistory() throws SQLException;
    List<String> getFinalizedMenu() throws SQLException;
    List<String> getNotifications();
}
