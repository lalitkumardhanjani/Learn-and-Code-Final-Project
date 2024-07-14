package org.Database;

import org.Recommendation.ParseSentimentWords;
import org.Recommendation.SentimentAnalyzer;

import javax.persistence.Id;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;

public class MenuManagement implements IMenuManagementDatabase {

    @Override
    public void createMenuItem(String name, double price, Integer mealType, int availability) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO Menu (name, price, mealId, isavailable) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, name);
            stmt.setDouble(2, price);
            stmt.setInt(3, mealType);
            stmt.setInt(4, availability);
            stmt.executeUpdate();

            NotificationManagement.addNotification("New Food Item is added into the Menu");
        } catch (SQLException e) {
            System.err.println("Error while inserting menu item: " + e.getMessage());
        }
    }

    @Override
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
        }
        return menuItems;
    }

    public void generateFinalizedMenu(Scanner scanner, BufferedReader in, PrintWriter out) throws IOException {
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
        }
    }

    public static void viewRecommendationMenu(BufferedReader in, PrintWriter out) throws IOException {
        try {
            out.println("viewRecommendationMenu");
            String response;
            while (!(response = in.readLine()).equals("END")) {
                System.out.println(response);
            }
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        }
    }

    @Override
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

            NotificationManagement.addNotification("Food item with Menu Id " + menuId + " is updated");
        } catch (SQLException e) {
            System.err.println("Error while updating menu item: " + e.getMessage());
        }
    }

    @Override
    public List<String> getImprovementQuestionsandAnswers() {
        List<String> improvementAnswers = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            retrieveImprovementQuestionsAndAnswers(conn, improvementAnswers);
        } catch (SQLException e) {
            System.err.println("Error while fetching improvement questions and answers: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error while fetching improvement questions and answers: " + e.getMessage());
        }
        return improvementAnswers;
    }

    @Override
    public boolean deleteMenuItem(int menuId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM Menu WHERE id = ?")) {
            stmt.setInt(1, menuId);
            int rowAffected = stmt.executeUpdate();
            return rowAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error while deleting menu item: " + e.getMessage());
            return false;
        }
    }

    @Override
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
        }
    }

    @Override
    public void makeEmployeeProfile(BufferedReader in, PrintWriter out, String[] parts){
        String dietary = parts[1];
        String spiceLevel = parts[2];
        String cousine = parts[3];
        Integer isSweethTooth = Integer.parseInt(parts[4]);
        Integer userId = Integer.parseInt(parts[5]);

        System.out.println(dietary+":"+spiceLevel+":"+cousine+":"+isSweethTooth+":"+userId);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO UserPreference (UserId,DietaryPreference, SpiceLevel, Cuisine, HasSweetTooth) VALUES (?,?, ?, ?, ?)")) {
            stmt.setInt(1, userId);
            stmt.setString(2, dietary);
            stmt.setString(3, spiceLevel);
            stmt.setString(4, cousine);
            stmt.setInt(5, isSweethTooth);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error while inserting employee profile item: " + e.getMessage());
        }

    }

    public List<String> generateRecommendedMenu() throws IOException {
        List<String> recommendedMenuItems = new ArrayList<>();
        Map<Integer, Double> foodSentimentRatings = new HashMap<>();
        Map<Integer, List<Integer>> foodRatings = new HashMap<>();
        SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer(new ParseSentimentWords());

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (!hasExistingRecommendations(conn)) {
                generateNewRecommendations(conn, sentimentAnalyzer, foodSentimentRatings, foodRatings, recommendedMenuItems);
            } else {
                recommendedMenuItems.add("Recommendation Menu already generated.");
                System.out.println("Recommendation Menu already generated.");
            }
        } catch (SQLException e) {
            System.err.println("Error while generating recommended menu: " + e.getMessage());
        }
        return recommendedMenuItems;
    }


    private boolean hasExistingRecommendations(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS count FROM RecommendedMenu")) {
            rs.next();
            return rs.getInt("count") > 0;
        }
    }

    @Override
    public List<String> generateDiscardMenuItems() {
        List<String> discardMenuItems = new ArrayList<>();
        Map<Integer, List<Integer>> foodRatings = new HashMap<>();
        Map<Integer, String> foodComments = new HashMap<>();
        SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer(new ParseSentimentWords());

        try (Connection conn = DatabaseConnection.getConnection()) {
            retrieveFeedbackData(conn, sentimentAnalyzer, foodRatings, foodComments);
            calculateDiscardItems(conn, sentimentAnalyzer, foodRatings, foodComments, discardMenuItems);
        } catch (SQLException e) {
            System.err.println("Error while generating discard menu items: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error while generating discard menu items: " + e.getMessage());
        }

        return discardMenuItems;
    }

    private void retrieveFeedbackData(Connection conn, SentimentAnalyzer sentimentAnalyzer,
                                      Map<Integer, List<Integer>> foodRatings,
                                      Map<Integer, String> foodComments) throws SQLException {
        String feedbackQuery = "SELECT FoodItemId, Rating, Comments FROM FoodFeedbackHistory";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(feedbackQuery)) {
            while (rs.next()) {
                int foodItemId = rs.getInt("FoodItemId");
                int rating = rs.getInt("Rating");
                String comments = rs.getString("Comments");

                sentimentAnalyzer.analyzeComments(foodItemId, comments);

                foodRatings.putIfAbsent(foodItemId, new ArrayList<>());
                foodRatings.get(foodItemId).add(rating);

                foodComments.putIfAbsent(foodItemId, "");
                foodComments.put(foodItemId, foodComments.get(foodItemId) + " " + comments);
            }
        }
    }

    private void calculateDiscardItems(Connection conn, SentimentAnalyzer sentimentAnalyzer,
                                       Map<Integer, List<Integer>> foodRatings,
                                       Map<Integer, String> foodComments,
                                       List<String> discardMenuItems) throws SQLException {
        for (Map.Entry<Integer, List<Integer>> entry : foodRatings.entrySet()) {
            int foodItemId = entry.getKey();
            List<Integer> ratings = entry.getValue();
            double averageRating = sentimentAnalyzer.calculateAverageRating(foodItemId, ratings);
            double sentimentScore = SentimentAnalyzer.calculateSentimentScore(foodComments.get(foodItemId));

            if (averageRating < 2 && sentimentScore < 2) {
                addDiscardItem(conn, foodItemId, discardMenuItems);
            }
        }
    }

    private void addDiscardItem(Connection conn, int foodItemId, List<String> discardMenuItems) throws SQLException {
        String itemDetailsQuery = "SELECT Menu.Id, Menu.Name, Menu.Price, Menu.IsAvailable, MealType.MealType " +
                "FROM Menu " +
                "JOIN MealType ON Menu.MealId = MealType.Id " +
                "WHERE Menu.Id = ?";
        try (PreparedStatement itemStmt = conn.prepareStatement(itemDetailsQuery)) {
            itemStmt.setInt(1, foodItemId);
            try (ResultSet itemRs = itemStmt.executeQuery()) {
                if (itemRs.next()) {
                    insertDiscardItem(conn, foodItemId);

                    int id = itemRs.getInt("Id");
                    String name = itemRs.getString("Name");
                    double price = itemRs.getDouble("Price");
                    boolean isAvailable = itemRs.getBoolean("IsAvailable");
                    String mealType = itemRs.getString("MealType");
                    String formattedItem = String.format("%d^%s^%.2f^%b^%s", id, name, price, isAvailable, mealType);
                    discardMenuItems.add(formattedItem);
                }
            }
        }
    }

    private void insertDiscardItem(Connection conn, int foodItemId) throws SQLException {
        String insertDiscardQuery = "INSERT INTO DiscardItem (FoodItemId, DateTime) VALUES (?, GETDATE())";
        try (PreparedStatement insertStmt = conn.prepareStatement(insertDiscardQuery)) {
            insertStmt.setInt(1, foodItemId);
            insertStmt.executeUpdate();
        }
    }



    @Override
    public boolean updateStatusOfDiscardItem() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            updateDiscardItemStatus(conn);
            NotificationManagement.addNotification("Take a look into the Improvement Questions!!!");
            return true;
        } catch (SQLException e) {
            System.err.println("Error while updating DiscardItem status: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error while updating DiscardItem status: " + e.getMessage());
        }
        return false;
    }

    private void updateDiscardItemStatus(Connection conn) throws SQLException {
        String updateDiscardQuery = "UPDATE DiscardItem SET Status = 1 WHERE Status = 0";
        try (PreparedStatement stmt = conn.prepareStatement(updateDiscardQuery)) {
            stmt.executeUpdate();
        }
    }



    private void insertImprovementAnswers(Connection conn, int foodItemId, int userId, String answer1,
                                          String answer2, String answer3) throws SQLException {
        String insertImprovementQuery = "INSERT INTO ImprovementFeedback (FoodItemId, UserId, Question1, Question2, Question3) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertImprovementQuery)) {
            stmt.setInt(1, foodItemId);
            stmt.setInt(2, userId);
            stmt.setString(3, answer1);
            stmt.setString(4, answer2);
            stmt.setString(5, answer3);
            stmt.executeUpdate();
        }
    }
    @Override
    public String getDiscardFoodItemIds() {
        StringBuilder discardedItemIds = new StringBuilder();
        try (Connection conn = DatabaseConnection.getConnection()) {
            retrieveDiscardFoodItemIds(conn, discardedItemIds);
        } catch (SQLException e) {
            System.err.println("Error while fetching discard food item IDs: " + e.getMessage());
        }
        return discardedItemIds.toString();
    }


    private void retrieveDiscardFoodItemIds(Connection conn, StringBuilder discardedItemIds) throws SQLException {
        String selectDiscardQuery = "SELECT FoodItemId FROM DiscardItem";
        try (PreparedStatement stmt = conn.prepareStatement(selectDiscardQuery); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                if (discardedItemIds.length() > 0) {
                    discardedItemIds.append(",");
                }
                discardedItemIds.append(rs.getInt("FoodItemId"));
            }
        }
    }

    private void retrieveImprovementQuestionsAndAnswers(Connection conn, List<String> improvementAnswers) throws SQLException {
        String improvementQuery = "SELECT FoodItemId, UserId, Question1, Question2, Question3 FROM ImprovementFeedback";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(improvementQuery)) {
            while (rs.next()) {
                int foodItemId = rs.getInt("FoodItemId");
                int userId = rs.getInt("UserId");
                String answer1 = rs.getString("Question1");
                String answer2 = rs.getString("Question2");
                String answer3 = rs.getString("Question3");

                String formattedItem = String.format("%d^%d^%s^%s^%s", foodItemId, userId, answer1, answer2, answer3);
                improvementAnswers.add(formattedItem);
            }
        }
    }


    private void generateNewRecommendations(Connection conn, SentimentAnalyzer sentimentAnalyzer,
                                            Map<Integer, Double> foodSentimentRatings,
                                            Map<Integer, List<Integer>> foodRatings,
                                            List<String> recommendedMenuItems) throws IOException, SQLException {
        // Step 1: Retrieve feedback data
        retrieveFeedbackData(conn, sentimentAnalyzer, foodRatings);

        // Step 2: Calculate average ratings and generate recommendations
        calculateAverageRatings(sentimentAnalyzer, foodRatings, foodSentimentRatings);

        // Get top-rated items for each meal type (only top 3)
        recommendedMenuItems.addAll(getTopRatedItems(foodSentimentRatings, "Breakfast", 3, conn));
        recommendedMenuItems.addAll(getTopRatedItems(foodSentimentRatings, "Lunch", 3, conn));
        recommendedMenuItems.addAll(getTopRatedItems(foodSentimentRatings, "Dinner", 3, conn));

        // Insert recommendations
        insertRecommendedMenuItems(recommendedMenuItems);
    }

    private void retrieveFeedbackData(Connection conn, SentimentAnalyzer sentimentAnalyzer,
                                      Map<Integer, List<Integer>> foodRatings) throws SQLException {
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
    }

    private void calculateAverageRatings(SentimentAnalyzer sentimentAnalyzer,
                                         Map<Integer, List<Integer>> foodRatings,
                                         Map<Integer, Double> foodSentimentRatings) {
        for (Map.Entry<Integer, List<Integer>> entry : foodRatings.entrySet()) {
            int foodItemId = entry.getKey();
            List<Integer> ratings = entry.getValue();
            double averageRating = sentimentAnalyzer.calculateAverageRating(foodItemId, ratings);
            foodSentimentRatings.put(foodItemId, averageRating);
        }
    }

    public static List<String> getTopRatedItems(Map<Integer, Double> averageRatings, String mealType, int limit, Connection conn) {
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

                    // Get top comment
                    String topComment = SentimentAnalyzer.getTopComment(id);

                    // Calculate average rating
                    double averageRating = averageRatings.getOrDefault(id, 0.0);

                    // Format item with delimiter ^
                    String formattedItem = String.format("%d^%s^%.2f^%s^%.2f^%s",
                            id, name, price, mealTypeStr, averageRating, topComment);

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
                    continue;
                }

                int foodItemId = parseInteger(parts[0], "FoodItemId");
                String name = parts[1].trim();
                double price = parseDouble(parts[2], "price");
                String mealType = parts[3].trim();
                double avgRating = parseDouble(parts[4], "average rating");
                String avgComments = parts[5].trim();

                if (foodItemId == -1 || price == -1 || avgRating == -1) {
                    continue;
                }

                stmt.setInt(1, foodItemId);
                stmt.setString(2, name);
                stmt.setDouble(3, price);
                stmt.setString(4, mealType);
                stmt.setDouble(5, avgRating);
                stmt.setString(6, avgComments);

                stmt.addBatch();
            }

            stmt.executeBatch();
            NotificationManagement.addNotification("Recommendation Menu is Ready");
        } catch (SQLException e) {
            System.err.println("Error while inserting recommended menu items: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error while inserting recommended menu items: " + e.getMessage());
        }
    }

    private int parseInteger(String value, String fieldName) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            System.err.println("Error parsing " + fieldName + ": " + value);
            return -1;
        }
    }

    private double parseDouble(String value, String fieldName) {
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            System.err.println("Error parsing " + fieldName + ": " + value);
            return -1;
        }
    }

    public List<String> getMenuItems(Connection conn, String mealType) {
        List<String> menuItems = new ArrayList<>();
        String query = "SELECT DISTINCT TOP 3 Menu.Id, Menu.Name, Menu.Price, MealType.MealType " +
                "FROM FoodFeedbackHistory " +
                "JOIN [User] ON FoodFeedbackHistory.UserId = [User].Id " +
                "JOIN Menu ON Menu.Id = FoodFeedbackHistory.FoodItemId " +
                "JOIN MealType ON Menu.MealId = MealType.Id " +
                "WHERE Menu.IsAvailable = 1 AND MealType.MealType = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, mealType);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("Id");
                    String name = rs.getString("Name");
                    double price = rs.getDouble("Price");

                    String formattedItem = String.format("%-15d %-20s ₹%-9.2f %-20s ",
                            id, name, price, mealType);

                    menuItems.add(formattedItem);

                    insertRecommendedMenuItem(conn, id, name, price, mealType);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error while fetching menu items: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error while fetching menu items: " + e.getMessage());
        }
        return menuItems;
    }

    private void insertRecommendedMenuItem(Connection conn, int id, String name, double price, String mealType) {
        String insertSql = "INSERT INTO RecommendedMenu (FoodItemId, Name, Price, Mealtype, Status) " +
                "VALUES (?, ?, ?, ?, -1)";

        try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            stmt.setInt(1, id);
            stmt.setString(2, name);
            stmt.setDouble(3, price);
            stmt.setString(4, mealType);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error while inserting recommended menu item: " + e.getMessage());
        }
    }

    public List<String> getRecommendedMenu(int userId) {
        List<String> menuItems = new ArrayList<>();
        String query = "SELECT rm.Id, rm.FoodItemId, rm.Name, rm.Price, rm.Mealtype, rm.Status, rm.[Avg Rating], rm.[Avg Comments] FROM [dbo].[RecommendedMenu] rm JOIN [dbo].[Menu] m ON rm.FoodItemId = m.Id JOIN [dbo].[UserPreference] up ON up.UserId = 3 WHERE rm.Status = 0 ORDER BY CASE WHEN m.MealId = 1 THEN 1 WHEN m.MealId = 2 THEN 2 WHEN m.MealId = 3 THEN 3 ELSE 4 END, CASE WHEN up.DietaryPreference = m.DietaryPreference THEN 1 ELSE 0 END DESC, CASE WHEN up.Cuisine = m.Cuisine THEN 1 ELSE 0 END DESC, CASE WHEN up.SpiceLevel = m.SpiceLevel THEN 1 ELSE 0 END DESC, CASE WHEN up.HasSweetTooth = m.HasSweetTooth THEN 1 ELSE 0 END DESC;";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int foodItemId = rs.getInt("FoodItemId");
                String name = rs.getString("Name");
                double price = rs.getDouble("Price");
                String mealType = rs.getString("MealType");
                double avgRating = rs.getDouble("Avg Rating");
                String comment = rs.getString("Avg Comments");

                String formattedItem = String.format("%-15d %-20s ₹%-9.2f %-20s %-9.2f %-20s",
                        foodItemId, name, price, mealType, avgRating, comment);

                menuItems.add(formattedItem);
            }
        } catch (SQLException e) {
            System.err.println("Error while fetching recommended menu: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error while fetching recommended menu: " + e.getMessage());
        }
        return menuItems;
    }


    public int rolloutRecommendation() {
        String updateSql = "UPDATE RecommendedMenu SET Status = 0 WHERE Status = -1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateSql)) {
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
        String query = "SELECT Menu.Id, Menu.Name, Menu.Price, MealType.MealType " +
                "FROM Menu " +
                "JOIN MealType ON Menu.MealId = MealType.Id " +
                "WHERE Menu.Id IN (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, breakfastMenuItemId);
            stmt.setInt(2, lunchMenuItemId);
            stmt.setInt(3, dinnerMenuItemId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("Id");
                    String name = rs.getString("Name");
                    double price = rs.getDouble("Price");
                    String mealType = rs.getString("MealType");

                    String formattedItem = String.format("%-15d %-20s ₹%-9.2f %-20s ",
                            id, name, price, mealType);

                    finalizedMenuItems.add(formattedItem);

                    insertFinalizedMenuItem(conn, id, name);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error while fetching finalized menu: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error while fetching finalized menu: " + e.getMessage());
        }
        return finalizedMenuItems;
    }

    private void insertFinalizedMenuItem(Connection conn, int id, String name) {
        String insertSql = "INSERT INTO FinalizedMenu (FoodItemId, Name, DateTime) VALUES (?, ?, GETDATE())";

        try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            stmt.setInt(1, id);
            stmt.setString(2, name);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error while inserting finalized menu item: " + e.getMessage());
        }
    }

    public int rolloutFinalizedMenusStatusUpdate() {
        String updateSql = "UPDATE RecommendedMenu SET Status = 1 WHERE Status = 0";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateSql)) {
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
        List<String> finalizedMenuItems = new ArrayList<>();
        String query = "SELECT Id, FoodItemId, Name FROM FinalizedMenu WHERE CONVERT(DATE, DateTime) = CONVERT(DATE, GETDATE())";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("Id");
                int foodItemId = rs.getInt("FoodItemId");
                String name = rs.getString("Name");

                String formattedItem = String.format("%-15d %-15d %-20s", id, foodItemId, name);
                finalizedMenuItems.add(formattedItem);
            }
        } catch (SQLException e) {
            System.err.println("Error while fetching today's finalized menu: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error while fetching today's finalized menu: " + e.getMessage());
        }
        return finalizedMenuItems;
    }

    @Override
    public void insertImprovementAnswersinDB(BufferedReader in, PrintWriter out, String[] parts) {
        int foodItemId = Integer.parseInt(parts[1]);
        int userId = Integer.parseInt(parts[2]);
        String answer1 = parts[3];
        String answer2 = parts[4];
        String answer3 = parts[5];
        try (Connection conn = DatabaseConnection.getConnection()) {
            insertImprovementAnswers(conn, foodItemId, userId, answer1, answer2, answer3);
        } catch (SQLException e) {
            System.err.println("Error while inserting improvement answers: " + e.getMessage());
        } catch (NullPointerException e) {
            System.err.println("Null value encountered while inserting improvement answers: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Illegal argument provided: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error while inserting improvement answers: " + e.getMessage());
        }
    }
}
