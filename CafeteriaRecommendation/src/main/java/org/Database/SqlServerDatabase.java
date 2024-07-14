package org.Database;

import javax.swing.plaf.PanelUI;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SqlServerDatabase implements IAuthenticationDatabase, IMenuManagementDatabase, IFeedbackDatabase, INotificationDatabase {

    private final UserAuthentication userAuth = new UserAuthentication();
    private final MenuManagement menuManagement = new MenuManagement();
    private final FeedbackManagement feedbackManagement = new FeedbackManagement();
    private final NotificationManagement notificationManagement = new NotificationManagement();

    @Override
    public Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    @Override
    public boolean isValidUser(int userId, String password, int role) {
        try {
            return userAuth.isValidUser(userId, password, role);
        } catch (Exception e) {
            System.err.println("Error checking credentials for userId " + userId + ": " + e.getMessage());
            return false;
        }
    }

    @Override
    public String getDiscardFoodItemIds() throws SQLException{
        try {
            return menuManagement.getDiscardFoodItemIds();
        } catch (Exception e) {
            System.err.println("Facing issues while getting the Discard Food item Ids");
            return "Facing issues while getting the Discard Food item Ids";
        }
    }

    @Override
    public void logLoginAttempt(int userId, boolean success) {
        try {
            userAuth.logLoginAttempt(userId, success);
        } catch (Exception e) {
            System.err.println("Error logging login attempt for userId " + userId + ": " + e.getMessage());
        }
    }

    @Override
    public void makeEmployeeProfile(BufferedReader in, PrintWriter out, String[] parts){
        try {
            menuManagement.makeEmployeeProfile(in,out,parts);
        }  catch (Exception e) {
            System.err.println("Error while getting the Improvement Questions and Answers " + e.getMessage());
        }
    }
    @Override
    public List<String> getImprovementQuestionsandAnswers(){
        try {
            return menuManagement.getImprovementQuestionsandAnswers();
        }  catch (Exception e) {
            System.err.println("Error while getting the Improvement Questions and Answers " + e.getMessage());
            return List.of();
        }
    }
    @Override
    public void insertImprovementAnswersinDB(BufferedReader in, PrintWriter out, String[] parts){
        try {
            menuManagement.insertImprovementAnswersinDB(in,out,parts);
        }  catch (Exception e) {
            System.err.println("Error while inserting the Improvement Questions and Answers " + e.getMessage());
        }
    }
    @Override
    public void logLogoutAttempt(int userId, boolean success) {
        try {
            userAuth.logLogoutAttempt(userId, success);
        } catch (Exception e) {
            System.err.println("Error logging logout attempt for userId " + userId + ": " + e.getMessage());
        }
    }

    @Override
    public String getUserRole(int userId) {
        try {
            return userAuth.getUserRole(userId);
        } catch (Exception e) {
            System.err.println("Error retrieving role for userId " + userId + ": " + e.getMessage());
            return null;
        }
    }

    // MenuManagementDatabase methods
    @Override
    public void createMenuItem(String name, double price, Integer mealType, int availability) {
        try {
            menuManagement.createMenuItem(name, price, mealType, availability);
        } catch (Exception e) {
            System.err.println("Error creating menu item: " + name + ": " + e.getMessage());
        }
    }

    @Override
    public List<String> generateDiscardMenuItems() {
        try {
            return  menuManagement.generateDiscardMenuItems();
        } catch (Exception e) {
            System.err.println("Error While Generating Discarded Menu Items " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public boolean updateStatusOfDiscardItem(){
        try {
            return  menuManagement.updateStatusOfDiscardItem();
        } catch (Exception e) {
            System.err.println("Error While Generating Discarded Menu Items " + e.getMessage());
            return false;
        }
    }
    @Override
    public List<String> getMenuItems() {
        try {
            return menuManagement.getMenuItems();
        } catch (Exception e) {
            System.err.println("Error retrieving menu items: " + e.getMessage());
            return List.of(); // Return empty list on failure
        }
    }

    @Override
    public void updateMenuItem(int menuId, String newName, double newPrice, int newAvailability) {
        try {
            menuManagement.updateMenuItem(menuId, newName, newPrice, newAvailability);
        } catch (Exception e) {
            System.err.println("Error updating menu item with id " + menuId + ": " + e.getMessage());
        }
    }

    @Override
    public boolean isValidMenuId(int menuId) {
        try {
            return menuManagement.isValidMenuId(menuId);
        } catch (Exception e) {
            System.err.println("Error checking validity of menu item id " + menuId + ": " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteMenuItem(int menuId) {
        try {
            return menuManagement.deleteMenuItem(menuId);
        } catch (Exception e) {
            System.err.println("Error deleting menu item with id " + menuId + ": " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<String> generateRecommendedMenu() throws SQLException, IOException {
        try {
            return menuManagement.generateRecommendedMenu();
        } catch (Exception e) {
            System.err.println("Error generating recommended menu: " + e.getMessage());
            throw e; // Re-throw exception to handle it at the caller level
        }
    }

    @Override
    public List<String> getRecommendedMenu(int userId) throws SQLException {
        try {
            return menuManagement.getRecommendedMenu(userId);
        } catch (Exception e) {
            System.err.println("Error retrieving recommended menu: " + e.getMessage());
            throw e; // Re-throw exception to handle it at the caller level
        }
    }

    @Override
    public List<String> getMenuItems(Connection conn, String mealType) throws SQLException {
        try {
            return menuManagement.getMenuItems(conn, mealType);
        } catch (Exception e) {
            System.err.println("Error retrieving menu items for meal type " + mealType + ": " + e.getMessage());
            throw e; // Re-throw exception to handle it at the caller level
        }
    }

    @Override
    public int rolloutRecommendation() {
        try {
            return menuManagement.rolloutRecommendation();
        } catch (Exception e) {
            System.err.println("Error rolling out recommendation: " + e.getMessage());
            return 0; // Indicate failure
        }
    }

    @Override
    public List<String> getFinalizedMenu(int breakfastMenuItemId, int lunchMenuItemId, int dinnerMenuItemId) throws SQLException {
        try {
            return menuManagement.getFinalizedMenu(breakfastMenuItemId, lunchMenuItemId, dinnerMenuItemId);
        } catch (Exception e) {
            System.err.println("Error retrieving finalized menu: " + e.getMessage());
            throw e; // Re-throw exception to handle it at the caller level
        }
    }

    @Override
    public int rolloutFinalizedMenusStatusUpdate() {
        try {
            return menuManagement.rolloutFinalizedMenusStatusUpdate();
        } catch (Exception e) {
            System.err.println("Error rolling out finalized menu status: " + e.getMessage());
            return 0; // Indicate failure
        }
    }

    @Override
    public void addNotification(String message) {
        try {
            notificationManagement.addNotification(message);
        }  catch (Exception e) {
            System.err.println("Error retrieving finalized menu: " + e.getMessage());
        }
    }

    @Override
    public List<String> getFinalizedMenu() throws SQLException {
        try {
            return menuManagement.getFinalizedMenu();
        } catch (Exception e) {
            System.err.println("Error retrieving finalized menu: " + e.getMessage());
            throw e; // Re-throw exception to handle it at the caller level
        }
    }

    // FeedbackDatabase methods
    @Override
    public int insertSelectedFoodItemsInDB(List<Integer> ids) {
        try {
            return feedbackManagement.insertSelectedFoodItemsInDB(ids);
        } catch (Exception e) {
            System.err.println("Error inserting selected food items: " + e.getMessage());
            return 0; // Indicate failure
        }
    }

    @Override
    public List<String> getSelectedFoodItemsEmployees() throws SQLException {
        try {
            return feedbackManagement.getSelectedFoodItemsEmployees();
        } catch (Exception e) {
            System.err.println("Error retrieving selected food items by employees: " + e.getMessage());
            throw e; // Re-throw exception to handle it at the caller level
        }
    }

    @Override
    public int giveFoodFeedback(int foodItemId, int rating, String comment, int userId) {
        try {
            return feedbackManagement.giveFoodFeedback(foodItemId, rating, comment, userId);
        } catch (Exception e) {
            System.err.println("Error giving food feedback for food item id " + foodItemId + ": " + e.getMessage());
            return 0; // Indicate failure
        }
    }

    @Override
    public List<String> getFoodFeedbackHistory() throws SQLException {
        try {
            return feedbackManagement.getFoodFeedbackHistory();
        } catch (Exception e) {
            System.err.println("Error retrieving food feedback history: " + e.getMessage());
            throw e; // Re-throw exception to handle it at the caller level
        }
    }

    // NotificationDatabase methods
    @Override
    public List<String> getNotifications() {
        try {
            return notificationManagement.getNotifications();
        } catch (Exception e) {
            System.err.println("Error retrieving notifications: " + e.getMessage());
            return List.of(); // Return empty list on failure
        }
    }
}
