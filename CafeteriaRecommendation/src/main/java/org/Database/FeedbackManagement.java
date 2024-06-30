package org.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeedbackManagement {
    public int giveFoodFeedback(int foodItemId, int rating, String comment, int userId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO FoodFeedbackHistory (userId, FoodItemId, Rating, Comments, DateTime) VALUES (?, ?, ?, ?, GETDATE())")) {
            stmt.setInt(1, userId);
            stmt.setInt(2, foodItemId);
            stmt.setInt(3, rating);
            stmt.setString(4, comment);
            int rowAffected = stmt.executeUpdate();
            return rowAffected > 0 ? 1 : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<String> getFoodFeedbackHistory() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        List<String> feedbackHistory = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement("SELECT ffh.Id, ffh.UserId, u.Name AS UserName, ffh.FoodItemId, m.Name AS FoodItemName, mt.MealType, ffh.Rating, ffh.Comments, ffh.DateTime FROM FoodFeedbackHistory ffh JOIN [User] u ON ffh.UserId = u.Id JOIN Menu m ON ffh.FoodItemId = m.Id JOIN MealType mt ON m.MealId = mt.Id")) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("Id");
                    int userId = rs.getInt("UserId");
                    String userName = rs.getString("UserName");
                    int foodItemId = rs.getInt("FoodItemId");
                    String foodItemName = rs.getString("FoodItemName");
                    String mealType = rs.getString("MealType");
                    int rating = rs.getInt("Rating");
                    String comment = rs.getString("Comments");
                    String dateTime = rs.getString("DateTime");
                    String formattedItem = String.format("%-15d %-15d %-20s %-20d %-20s %-20s %-20d %-20s %-20s", id, userId, userName, foodItemId, foodItemName, mealType, rating, comment, dateTime);
                    feedbackHistory.add(formattedItem);
                }
            }
        }
        return feedbackHistory;
    }

    public List<String> getSelectedFoodItemsEmployees() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        List<String> selectedFoodItemsByEmployees = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement("SELECT efv.FoodItemId, m.Name, COUNT(efv.Id) AS VoteCount FROM EmployeeFoodVotes efv JOIN Menu m ON efv.FoodItemId = m.Id GROUP BY efv.FoodItemId, m.Name ORDER BY VoteCount DESC")) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("FoodItemId");
                    String name = rs.getString("Name");
                    int voteCount = rs.getInt("VoteCount");
                    String formattedItem = String.format("%-15d %-20s %-20d", id, name, voteCount);
                    selectedFoodItemsByEmployees.add(formattedItem);
                }
            }
        }
        return selectedFoodItemsByEmployees;
    }

    public int insertSelectedFoodItemsInDB(List<Integer> ids) {
        int breakfastMenuItemId = ids.get(0);
        int lunchMenuItemId = ids.get(1);
        int dinnerMenuItemId = ids.get(2);
        int userId = ids.get(3);

        String query = "INSERT INTO EmployeeFoodVotes (FoodItemId, UserId, DateTime) VALUES (?, ?, GETDATE())";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, breakfastMenuItemId);
            statement.setInt(2, userId);
            statement.addBatch();

            statement.setInt(1, lunchMenuItemId);
            statement.setInt(2, userId);
            statement.addBatch();

            statement.setInt(1, dinnerMenuItemId);
            statement.setInt(2, userId);
            statement.addBatch();

            int[] results = statement.executeBatch();

            for (int result : results) {
                if (result != PreparedStatement.SUCCESS_NO_INFO && result != 1) {
                    return 0;
                }
            }
            return 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
