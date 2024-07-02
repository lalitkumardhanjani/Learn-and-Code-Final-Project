package org.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeedbackManagement {

    public int giveFoodFeedback(int foodItemId, int rating, String comment, int userId) {
        String query = "INSERT INTO FoodFeedbackHistory (userId, FoodItemId, Rating, Comments, DateTime) VALUES (?, ?, ?, ?, GETDATE())";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, foodItemId);
            stmt.setInt(3, rating);
            stmt.setString(4, comment);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0 ? 1 : 0;
        } catch (SQLException e) {
            System.err.println("Error while giving food feedback: " + e.getMessage());
            return 0;
        } catch (NullPointerException e) {
            System.err.println("Null value encountered: " + e.getMessage());
            return 0;
        } catch (IllegalArgumentException e) {
            System.err.println("Illegal argument provided: " + e.getMessage());
            return 0;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            return 0;
        }
    }

    public List<String> getFoodFeedbackHistory() {
        List<String> feedbackHistory = new ArrayList<>();
        String query = "SELECT ffh.Id, ffh.UserId, u.Name AS UserName, ffh.FoodItemId, m.Name AS FoodItemName, mt.MealType, ffh.Rating, ffh.Comments, ffh.DateTime " +
                "FROM FoodFeedbackHistory ffh " +
                "JOIN [User] u ON ffh.UserId = u.Id " +
                "JOIN Menu m ON ffh.FoodItemId = m.Id " +
                "JOIN MealType mt ON m.MealId = mt.Id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

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
                String formattedItem = String.format("%-15d %-15d %-20s %-20d %-20s %-20s %-20d %-20s %-20s",
                        id, userId, userName, foodItemId, foodItemName, mealType, rating, comment, dateTime);
                feedbackHistory.add(formattedItem);
            }
        } catch (SQLException e) {
            System.err.println("Error while retrieving food feedback history: " + e.getMessage());
        } catch (NullPointerException e) {
            System.err.println("Null value encountered: " + e.getMessage());
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Array index out of bounds: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Illegal argument provided: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
        return feedbackHistory;
    }

    public List<String> getSelectedFoodItemsEmployees() {
        List<String> selectedFoodItemsByEmployees = new ArrayList<>();
        String query = "SELECT efv.FoodItemId, m.Name, COUNT(efv.Id) AS VoteCount " +
                "FROM EmployeeFoodVotes efv " +
                "JOIN Menu m ON efv.FoodItemId = m.Id " +
                "GROUP BY efv.FoodItemId, m.Name " +
                "ORDER BY VoteCount DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("FoodItemId");
                String name = rs.getString("Name");
                int voteCount = rs.getInt("VoteCount");
                String formattedItem = String.format("%-15d %-20s %-20d", id, name, voteCount);
                selectedFoodItemsByEmployees.add(formattedItem);
            }
        } catch (SQLException e) {
            System.err.println("Error while retrieving selected food items by employees: " + e.getMessage());
        } catch (NullPointerException e) {
            System.err.println("Null value encountered: " + e.getMessage());
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Array index out of bounds: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Illegal argument provided: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
        return selectedFoodItemsByEmployees;
    }

    public int insertSelectedFoodItemsInDB(List<Integer> ids) {
        if (ids.size() < 4) {
            System.err.println("Insufficient IDs provided for inserting selected food items");
            return 0;
        }

        String query = "INSERT INTO EmployeeFoodVotes (FoodItemId, UserId, DateTime) VALUES (?, ?, GETDATE())";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            for (int i = 0; i < 3; i++) {
                statement.setInt(1, ids.get(i));
                statement.setInt(2, ids.get(3));
                statement.addBatch();
            }

            int[] results = statement.executeBatch();
            for (int result : results) {
                if (result != PreparedStatement.SUCCESS_NO_INFO && result != 1) {
                    return 0;
                }
            }
            return 1;
        } catch (SQLException e) {
            System.err.println("Error while inserting selected food items: " + e.getMessage());
            return 0;
        } catch (NullPointerException e) {
            System.err.println("Null value encountered: " + e.getMessage());
            return 0;
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Array index out of bounds: " + e.getMessage());
            return 0;
        } catch (IllegalArgumentException e) {
            System.err.println("Illegal argument provided: " + e.getMessage());
            return 0;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            return 0;
        }
    }
}
