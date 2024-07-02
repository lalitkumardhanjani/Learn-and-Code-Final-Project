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
            System.err.println("Error while inserting menu item: " + e.getMessage());
        } catch (NullPointerException e) {
            System.err.println("Null value encountered while inserting menu item: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Illegal argument provided: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error while inserting menu item: " + e.getMessage());
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO Notification (Message, DateTime) VALUES ('New Food Item is added into the Menu', GETDATE())")) {
            insertStmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error while inserting notification: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error while inserting notification: " + e.getMessage());
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
            System.err.println("Error while fetching menu items: " + e.getMessage());
        } catch (NullPointerException e) {
            System.err.println("Null value encountered while fetching menu items: " + e.getMessage());
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Array index out of bounds: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error while fetching menu items: " + e.getMessage());
        }
        return menuItems;
    }

    private void generateFinalizedMenu(Scanner scanner, BufferedReader in, PrintWriter out) throws IOException {
        try {
            System.out.println("Enter the Menu Item Id for the Finalized Breakfast");
            int breakfastMenuItemId = scanner.nextInt();
            System.out.println("Enter the Menu Item Id for the Finalized Lunch");
            int lunchMenuItemId = scanner.nextInt();
            System.out.println("Enter the Menu Item Id for the Finalized Dinner");
            int dinnerMenuItemId = scanner.nextInt();
            out.println("generateFinalizedMenu:" + breakfastMenuItemId + ":" + lunchMenuItemId + ":" + dinnerMenuItemId);
            String response;
            while (!(response = in.readLine()).equals("END")) {
                System.out.println(response);
            }
        } catch (InputMismatchException e) {
            System.err.println("Invalid input: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    private static void viewRecommendationMenu(BufferedReader in, PrintWriter out) throws IOException {
        try {
            out.println("viewRecommendationMenu");
            String response;
            while (!(response = in.readLine()).equals("END")) {
                System.out.println(response);
            }
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    public void updateMenuItem(int menuId, String newName, double newPrice, int newAvailability) {
        StringBuilder queryBuilder = new StringBuilder("UPDATE Menu SET ");
        List<Object> params = new ArrayList<>();
        boolean updateRequired = false;

        try {
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
                System.err.println("Error while updating menu item: " + e.getMessage());
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid argument: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    public boolean deleteMenuItem(int menuId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM Menu WHERE id = ?")) {
            stmt.setInt(1, menuId);
            int rowAffected = stmt.executeUpdate();
            return rowAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error while deleting menu item: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error while deleting menu item: " + e.getMessage());
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
            System.err.println("Error while validating menu item ID: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error while validating menu item ID: " + e.getMessage());
            return false;
        }
    }

    public List<String> generateRecommendedMenu() throws IOException {
        List<String> recommendedMenuItems = new ArrayList<>();
        Map<Integer, Double> foodSentimentRatings = new HashMap<>();
        Map<Integer, List<Integer>> foodRatings = new HashMap<>(); // Store ratings for average calculation
        SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer(new ParseSentimentWords());

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Step 1: Retrieve feedback data
            String feedbackQuery = "SELECT FoodItemId, Rating, Comments FROM FoodFeedbackHistory";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(feedbackQuery)) {

                while (rs.next()) {
                    int foodItemId = rs.getInt("FoodItemId");
                    int rating = rs.getInt("Rating");
                    String comments = rs.getString("Comments");

                    // Analyze comments and store data
                    sentimentAnalyzer.analyzeComments(foodItemId, comments);

                    // Update ratings
                    foodRatings.putIfAbsent(foodItemId, new ArrayList<>());
                    foodRatings.get(foodItemId).add(rating);
                }
            }

            // Step 2: Calculate average ratings and generate recommendations
            for (Map.Entry<Integer, List<Integer>> entry : foodRatings.entrySet()) {
                int foodItemId = entry.getKey();
                List<Integer> ratings = entry.getValue();
                double averageRating = sentimentAnalyzer.calculateAverageRating(foodItemId, ratings);
                foodSentimentRatings.put(foodItemId, averageRating);
            }

            // Get top-rated items for each meal type (only top 3)
            recommendedMenuItems.addAll(getTopRatedItems(foodSentimentRatings, "Breakfast", 3, conn));
            recommendedMenuItems.addAll(getTopRatedItems(foodSentimentRatings, "Lunch", 3, conn));
            recommendedMenuItems.addAll(getTopRatedItems(foodSentimentRatings, "Dinner", 3, conn));
        } catch (SQLException e) {
            System.err.println("Error while generating recommended menu: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error while generating recommended menu: " + e.getMessage());
        }
        insertRecommendedMenuItems(recommendedMenuItems); // Inserting with delimiter ^ for easy parsing
        return recommendedMenuItems;
    }

    private List<String> getTopRatedItems(Map<Integer, Double> averageRatings, String mealType, int limit, Connection conn) {
        List<String> topRatedItems = new ArrayList<>();
        String query = "SELECT TOP " + limit + " Menu.Id, Menu.Name, Menu.Price, MealType.MealType " +
                "FROM Menu JOIN MealType ON Menu.MealId = MealType.Id " +
                "WHERE Menu.IsAvailable = 1 AND MealType.MealType = ? " +
                "ORDER BY (SELECT AVG(Rating) FROM FoodFeedbackHistory WHERE FoodItemId = Menu.Id) DESC";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, mealType);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("Id");
                    String name = rs.getString("Name");
                    double price = rs.getDouble("Price");
                    String mealTypeStr = rs.getString("MealType");

                    // Get top comments
                    List<String> topComments = SentimentAnalyzer.getTopComments(id);

                    // Calculate average rating
                    double averageRating = averageRatings.getOrDefault(id, 0.0);

                    // Format item with delimiter ^
                    String formattedItem = String.format("%d^%s^%.2f^%s^%.2f^%s",
                            id, name, price, mealTypeStr, averageRating, String.join(" ", topComments));

                    topRatedItems.add(formattedItem);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error while fetching top-rated items: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error while fetching top-rated items: " + e.getMessage());
        }
        return topRatedItems;
    }

    public void insertRecommendedMenuItems(List<String> recommendedMenuItems) {
        String insertSql = "INSERT INTO RecommendedMenu (FoodItemId, Name, Price, Mealtype, Status, [Avg Rating], [Avg Comments]) " +
                "VALUES (?, ?, ?, ?,-1, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertSql)) {

            for (String menuItem : recommendedMenuItems) {
                // Split using delimiter ^
                String[] parts = menuItem.split("\\^");

                if (parts.length < 6) {
                    System.err.println("Invalid format for menu item: " + menuItem);
                    continue; // Skip this item or handle the error appropriately
                }

                int foodItemId;
                try {
                    foodItemId = Integer.parseInt(parts[0].trim());
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing FoodItemId: " + parts[0]);
                    continue; // Skip this item or handle the error appropriately
                }

                String name = parts[1].trim();
                double price;
                try {
                    price = Double.parseDouble(parts[2].trim());
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing price: " + parts[2]);
                    continue; // Skip this item or handle the error appropriately
                }

                String mealType = parts[3].trim();
                double avgRating;
                try {
                    avgRating = Double.parseDouble(parts[4].trim());
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing average rating: " + parts[4]);
                    continue; // Skip this item or handle the error appropriately
                }

                String avgComments = parts[5].trim();

                stmt.setInt(1, foodItemId);
                stmt.setString(2, name);
                stmt.setDouble(3, price);
                stmt.setString(4, mealType);
                stmt.setDouble(5, avgRating);
                stmt.setString(6, avgComments);

                stmt.addBatch(); // Add the statement to the batch for batch processing
            }

            int[] result = stmt.executeBatch(); // Execute batch insert

            // Handle success or failure based on result array if needed

        } catch (SQLException e) {
            System.err.println("Error while inserting recommended menu items: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error while inserting recommended menu items: " + e.getMessage());
        }
    }

    public List<String> getMenuItems(Connection conn, String mealType) {
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
        } catch (SQLException e) {
            System.err.println("Error while fetching menu items: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error while fetching menu items: " + e.getMessage());
        }
        return menuItems;
    }

    public List<String> getRecommendedMenu() {
        List<String> menuItems = new ArrayList<>();
        String query = "SELECT FoodItemId, Name, Price, MealType, [Avg Rating], [Avg Comments], Status FROM RecommendedMenu WHERE Status = 0";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int foodItemId = rs.getInt("FoodItemId");
                String name = rs.getString("Name");
                double price = rs.getDouble("Price");
                String mealType = rs.getString("MealType");
                Double avgRating = rs.getDouble("Avg Rating");
                String comment = rs.getString("Avg Comments");
                String formattedItem = String.format("%-15d %-20s ₹%-9.2f %-20s %-9.2f %-20s", foodItemId, name, price, mealType, avgRating, comment);
                menuItems.add(formattedItem);
            }
        } catch (SQLException e) {
            System.err.println("Error while fetching recommended menu: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error while fetching recommended menu: " + e.getMessage());
        }
        System.out.println(menuItems);
        return menuItems;
    }

    public int rolloutRecommendation() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE RecommendedMenu SET Status = 0 WHERE Status = -1")) {
            stmt.executeUpdate();
            return 1;
        } catch (SQLException e) {
            System.err.println("Error while rolling out recommendation: " + e.getMessage());
            return 0;
        } catch (Exception e) {
            System.err.println("Unexpected error while rolling out recommendation: " + e.getMessage());
            return 0;
        }
    }

    public List<String> getFinalizedMenu(int breakfastMenuItemId, int lunchMenuItemId, int dinnerMenuItemId) {
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
        } catch (SQLException e) {
            System.err.println("Error while fetching finalized menu: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error while fetching finalized menu: " + e.getMessage());
        }
        return finalizedMenuItems;
    }

    public int rolloutFinalizedMenusStatusUpdate() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE RecommendedMenu SET Status = 1 WHERE Status = 0")) {
            stmt.executeUpdate();
            return 1;
        } catch (SQLException e) {
            System.err.println("Error while updating finalized menu status: " + e.getMessage());
            return 0;
        } catch (Exception e) {
            System.err.println("Unexpected error while updating finalized menu status: " + e.getMessage());
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
        } catch (SQLException e) {
            System.err.println("Error while fetching today's finalized menu: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error while fetching today's finalized menu: " + e.getMessage());
        }
        return finalizedMenuItems;
    }

}
