package org.Database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface MenuManagementDatabase {
    void createMenuItem(String name, double price, Integer mealType, int availability);
    List<String> getMenuItems();
    void updateMenuItem(int menuId, String newName, double newPrice, int newAvailability);
    boolean isValidMenuId(int menuId);
    boolean deleteMenuItem(int menuId);
    List<String> generateRecommendedMenu() throws SQLException, IOException;
    List<String> getRecommendedMenu() throws SQLException;
    List<String> getMenuItems(Connection conn, String mealType) throws SQLException;
    int rolloutRecommendation();
    List<String> getFinalizedMenu(int breakfastMenuItemId, int lunchMenuItemId, int dinnerMenuItemId) throws SQLException;
    int rolloutFinalizedMenusStatusUpdate();
    List<String> getFinalizedMenu() throws SQLException;
}
