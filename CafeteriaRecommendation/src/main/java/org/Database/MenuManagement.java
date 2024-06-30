package org.Database;
import org.Recommendation.ParseSentimentWords;
import org.Recommendation.SentimentAnalyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;

public class MenuManagement {

    public void createMenuItem(String name, double price, Integer mealType, int availability) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO Menu (name, price, mealId, isavailable) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, name);
            stmt.setDouble(2, price);
            stmt.setInt(3, mealType);
            stmt.setInt(4, availability);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO Notification (Message,DateTime) VALUES ('New Food Item is added into the Menu',GETDATE())")) {
            insertStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getMenuItems() {
        List<String> menuItems = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Id, name, price, mealId, isAvailable FROM Menu")) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                int mealId = rs.getInt("mealId");
                int isAvailable = rs.getInt("isAvailable");

                String formattedItem = String.format("%-15d %-20s ₹%-9.2f %-15d %-15d", id, name, price, mealId, isAvailable);
                menuItems.add(formattedItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return menuItems;
    }

    private void generateFinalizedMenu(Scanner scanner, BufferedReader in, PrintWriter out) throws IOException {

        System.out.println("Enter the Menu Item Id for the Finalized Breakfast");
        int breakfastMenuItemId=scanner.nextInt();
        System.out.println("Enter the Menu Item Id for the Finalized Lunch");
        int lunchMenuItemId = scanner.nextInt();
        System.out.println("Enter the Menu Item Id for the Finalized Dinner");
        int dinnerMenuItemId = scanner.nextInt();
        out.println("generateFinalizedMenu:"+breakfastMenuItemId+":"+lunchMenuItemId+":"+dinnerMenuItemId);
        String response;
        while (!(response = in.readLine()).equals("END")) {
            System.out.println(response);
        }
    }

    private static void viewRecommendationMenu(BufferedReader in, PrintWriter out) throws IOException {
        out.println("viewRecommendationMenu");
        String response;
        while (!(response = in.readLine()).equals("END")) {
            System.out.println(response);
        }
    }
    public void updateMenuItem(int menuId, String newName, double newPrice, int newAvailability) {
        StringBuilder queryBuilder = new StringBuilder("UPDATE Menu SET ");
        List<Object> params = new ArrayList<>();

        boolean updateRequired = false;

        if (newName != null && !newName.isEmpty()) {
            queryBuilder.append("name = ?, ");
            params.add(newName);
            updateRequired = true;
        }
        if (newPrice >= 0) {
            queryBuilder.append("price = ?, ");
            params.add(newPrice);
            updateRequired = true;
        }
        if (newAvailability == 0 || newAvailability == 1) {
            queryBuilder.append("isavailable = ?, ");
            params.add(newAvailability);
            updateRequired = true;
        }

        if (!updateRequired) {
            System.out.println("No fields to update. Update request ignored.");
            return;
        }

        queryBuilder.delete(queryBuilder.length() - 2, queryBuilder.length());
        queryBuilder.append(" WHERE Id = ?");
        params.add(menuId);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            int rowsUpdated = stmt.executeUpdate();
            System.out.println("Rows updated: " + rowsUpdated);

            String notificationMessage = "Food item with Menu Id " + menuId + " is updated";
            try (PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO Notification (Message, DateTime) VALUES (?, GETDATE())")) {
                insertStmt.setString(1, notificationMessage);
                insertStmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean deleteMenuItem(int menuId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM Menu WHERE id = ?")) {
            stmt.setInt(1, menuId);
            int rowAffected = stmt.executeUpdate();
            return rowAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isValidMenuId(int menuId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) AS count FROM Menu WHERE id = ?")) {
            stmt.setInt(1, menuId);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int count = rs.getInt("count");
            return count > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> generateRecommendedMenu() {
        List<String> recommendedMenuItems = new ArrayList<>();
        Map<Integer, Double> foodSentimentRatings = new HashMap<>();

        try {
            // Initialize the parser and sentiment analyzer
            ParseSentimentWords parser = new ParseSentimentWords();
            SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer(parser);

            // Step 1: Retrieve feedback data
            String feedbackQuery = "SELECT FoodItemId, Rating, Comments FROM FoodFeedbackHistory";
            try (Connection conn = DatabaseConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(feedbackQuery)) {

                while (rs.next()) {
                    int foodItemId = rs.getInt("FoodItemId");
                    int rating = rs.getInt("Rating");
                    String comments = rs.getString("Comments");

                    // Step 2: Perform sentiment analysis on comments
                    double sentimentScore = sentimentAnalyzer.calculateSentimentScore(comments);

                    // Step 3: Calculate weighted rating
                    double weightedRating = (rating + sentimentScore) / 2.0;

                    // Step 4: Accumulate sentiment scores for each food item
                    foodSentimentRatings.put(foodItemId,
                            foodSentimentRatings.getOrDefault(foodItemId, 0.0) + weightedRating);
                }

                // Step 5: Calculate average ratings
                Map<Integer, Double> averageRatings = new HashMap<>();
                for (Map.Entry<Integer, Double> entry : foodSentimentRatings.entrySet()) {
                    int foodItemId = entry.getKey();
                    double totalRating = entry.getValue();
                    averageRatings.put(foodItemId, totalRating); // Assuming each item has a single feedback entry for simplicity
                }

                // Step 6: Generate recommendations for each meal type
                recommendedMenuItems.addAll(getTopRatedItems(averageRatings, "Breakfast", conn));
                recommendedMenuItems.addAll(getTopRatedItems(averageRatings, "Lunch", conn));
                recommendedMenuItems.addAll(getTopRatedItems(averageRatings, "Dinner", conn));

            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return recommendedMenuItems;
    }

    private List<String> getTopRatedItems(Map<Integer, Double> averageRatings, String mealType, Connection conn) throws SQLException {
        List<String> topRatedItems = new ArrayList<>();
        String query = "SELECT TOP 3 Menu.Id, Menu.Name, Menu.Price FROM Menu " +
                "JOIN MealType ON Menu.MealId = MealType.Id " +
                "WHERE Menu.IsAvailable = 1 AND MealType.MealType = ? " +
                "ORDER BY averageRatings DESC";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, mealType);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("Id");
                    String name = rs.getString("Name");
                    double price = rs.getDouble("Price");
                    String formattedItem = String.format("%-15d %-20s ₹%-9.2f %-20s", id, name, price, mealType);
                    topRatedItems.add(formattedItem);
                }
            }
        }
        return topRatedItems;
    }
    public List<String> getMenuItems(Connection conn, String mealType) throws SQLException {
        List<String> menuItems = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DISTINCT TOP 3 Menu.Id as Id, Menu.Name as Name, Menu.Price as Price, MealType.MealType as MealType FROM FoodFeedbackHistory JOIN [User] ON FoodFeedbackHistory.UserId = [User].Id JOIN Menu ON Menu.Id = FoodFeedbackHistory.FoodItemId JOIN MealType ON Menu.MealId = MealType.Id WHERE Menu.IsAvailable = 1 AND MealType.MealType = '" + mealType + "'")) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                String formattedItem = String.format("%-15d %-20s ₹%-9.2f %-20s ", id, name, price, mealType);
                menuItems.add(formattedItem);

                try (PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO RecommendedMenu (FoodItemId, Name, price, mealtype, status) VALUES (?, ?, ?, ?, -1)")) {
                    insertStmt.setInt(1, id);
                    insertStmt.setString(2, name);
                    insertStmt.setDouble(3, price);
                    insertStmt.setString(4, mealType);
                    insertStmt.executeUpdate();
                }

                try (PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO Notification (Message, DateTime) VALUES ('Recommendation Menu is Ready', GETDATE())")) {
                    insertStmt.executeUpdate();
                }
            }
        }
        return menuItems;
    }

    public List<String> getRecommendedMenu() throws SQLException {
        List<String> menuItems = new ArrayList<>();
        String query = "SELECT FoodItemId, Name, Price, MealType, Status FROM RecommendedMenu WHERE Status = 0";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int foodItemId = rs.getInt("FoodItemId");
                String name = rs.getString("Name");
                double price = rs.getDouble("Price");
                String mealType = rs.getString("MealType");
                String formattedItem = String.format("%-15d %-20s ₹%-9.2f %-20s", foodItemId, name, price, mealType);
                menuItems.add(formattedItem);
            }
        }
        return menuItems;
    }

    public int rolloutRecommendation() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE RecommendedMenu SET Status = 0 WHERE Status = -1")) {
            stmt.executeUpdate();
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<String> getFinalizedMenu(int breakfastMenuItemId, int lunchMenuItemId, int dinnerMenuItemId) throws SQLException {
        List<String> finalizedMenuItems = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT Menu.Id, Menu.Name as Name, Menu.Price as Price, MealType.MealType as MealType FROM Menu JOIN MealType ON Menu.MealId = MealType.Id WHERE Menu.Id IN (?, ?, ?)")) {
            stmt.setInt(1, breakfastMenuItemId);
            stmt.setInt(2, lunchMenuItemId);
            stmt.setInt(3, dinnerMenuItemId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("Id");
                    String name = rs.getString("Name");
                    double price = rs.getDouble("Price");
                    String mealType = rs.getString("MealType");
                    String formattedItem = String.format("%-15d %-20s ₹%-9.2f %-20s ", id, name, price, mealType);
                    finalizedMenuItems.add(formattedItem);

                    try (PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO FinalizedMenu (FoodItemId, Name, DateTime) VALUES (?, ?, GETDATE())")) {
                        insertStmt.setInt(1, id);
                        insertStmt.setString(2, name);
                        insertStmt.executeUpdate();
                    }
                }
            }
        }
        return finalizedMenuItems;
    }

    public int rolloutFinalizedMenusStatusUpdate() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE RecommendedMenu SET Status = 1 WHERE Status = 0")) {
            stmt.executeUpdate();
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<String> getFinalizedMenu() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        List<String> finalizedMenuItems = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement("SELECT Id, FoodItemId, Name FROM FinalizedMenu WHERE CONVERT(DATE, DateTime) = CONVERT(DATE, GETDATE())")) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("Id");
                    int foodItemId = rs.getInt("foodItemId");
                    String name = rs.getString("Name");
                    String formattedItem = String.format("%-15d %-15d %-20s", id, foodItemId, name);
                    finalizedMenuItems.add(formattedItem);
                }
            }
        }
        return finalizedMenuItems;
    }

}