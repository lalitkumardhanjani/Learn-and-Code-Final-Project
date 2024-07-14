package org.Database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IMenuManagementDatabase {
    void createMenuItem(String name, double price, Integer mealType, int availability);
    List<String> getMenuItems();
    void updateMenuItem(int menuId, String newName, double newPrice, int newAvailability);
    boolean isValidMenuId(int menuId);
    boolean deleteMenuItem(int menuId);
    List<String> generateRecommendedMenu() throws SQLException, IOException;
    List<String> getRecommendedMenu(int userId) throws SQLException;

    List<String> getImprovementQuestionsandAnswers();

    List<String> getMenuItems(Connection conn, String mealType) throws SQLException;
    int rolloutRecommendation();
    List<String> getFinalizedMenu(int breakfastMenuItemId, int lunchMenuItemId, int dinnerMenuItemId) throws SQLException;
    int rolloutFinalizedMenusStatusUpdate();
    List<String> getFinalizedMenu() throws SQLException;

    List<String> generateDiscardMenuItems();
    boolean updateStatusOfDiscardItem();

    void insertImprovementAnswersinDB(BufferedReader in, PrintWriter out, String[] parts);

    String getDiscardFoodItemIds() throws SQLException;


    void makeEmployeeProfile(BufferedReader in, PrintWriter out, String[] parts);
}
