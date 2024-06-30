package org.Database;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SqlServerDatabase implements AuthenticationDatabase, MenuManagementDatabase, FeedbackDatabase, NotificationDatabase {
    private UserAuthentication userAuth = new UserAuthentication();
    private MenuManagement menuManagement = new MenuManagement();
    private FeedbackManagement feedbackManagement = new FeedbackManagement();
    private NotificationManagement notificationManagement = new NotificationManagement();

    @Override
    public Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    // AuthenticationDatabase methods
    @Override
    public boolean checkCredentials(int userId, String password, int role) {
        return userAuth.checkCredentials(userId, password, role);
    }

    @Override
    public void logLoginAttempt(int userId, boolean success) {
        userAuth.logLoginAttempt(userId, success);
    }

    @Override
    public void logLogoutAttempt(int userId, boolean success) {
        userAuth.logLogoutAttempt(userId, success);
    }

    @Override
    public String getUserRole(int userId) {
        return userAuth.getUserRole(userId);
    }

    // MenuManagementDatabase methods
    @Override
    public void createMenuItem(String name, double price, Integer mealType, int availability) {
        menuManagement.createMenuItem(name, price, mealType, availability);
    }

    @Override
    public List<String> getMenuItems() {
        return menuManagement.getMenuItems();
    }

    @Override
    public void updateMenuItem(int menuId, String newName, double newPrice, int newAvailability) {
        menuManagement.updateMenuItem(menuId, newName, newPrice, newAvailability);
    }

    @Override
    public boolean isValidMenuId(int menuId) {
        return menuManagement.isValidMenuId(menuId);
    }

    @Override
    public boolean deleteMenuItem(int menuId) {
        return menuManagement.deleteMenuItem(menuId);
    }

    @Override
    public List<String> generateRecommendedMenu() {
        return menuManagement.generateRecommendedMenu();
    }

    @Override
    public List<String> getRecommendedMenu() throws SQLException {
        return menuManagement.getRecommendedMenu();
    }

    @Override
    public List<String> getMenuItems(Connection conn, String mealType) throws SQLException {
        return menuManagement.getMenuItems(conn, mealType);
    }

    @Override
    public int rolloutRecommendation() {
        return menuManagement.rolloutRecommendation();
    }

    @Override
    public List<String> getFinalizedMenu(int breakfastMenuItemId, int lunchMenuItemId, int dinnerMenuItemId) throws SQLException {
        return menuManagement.getFinalizedMenu(breakfastMenuItemId, lunchMenuItemId, dinnerMenuItemId);
    }

    @Override
    public int rolloutFinalizedMenusStatusUpdate() {
        return menuManagement.rolloutFinalizedMenusStatusUpdate();
    }

    @Override
    public List<String> getFinalizedMenu() throws SQLException {
        return menuManagement.getFinalizedMenu();
    }

    // FeedbackDatabase methods
    @Override
    public int insertSelectedFoodItemsInDB(List<Integer> ids) {
        return feedbackManagement.insertSelectedFoodItemsInDB(ids);
    }

    @Override
    public List<String> getSelectedFoodItemsEmployees() throws SQLException {
        return feedbackManagement.getSelectedFoodItemsEmployees();
    }

    @Override
    public int giveFoodFeedback(int foodItemId, int rating, String comment, int userId) {
        return feedbackManagement.giveFoodFeedback(foodItemId, rating, comment, userId);
    }

    @Override
    public List<String> getFoodFeedbackHistory() throws SQLException {
        return feedbackManagement.getFoodFeedbackHistory();
    }

    // NotificationDatabase methods
    @Override
    public List<String> getNotifications() {
        return notificationManagement.getNotifications();
    }
}
