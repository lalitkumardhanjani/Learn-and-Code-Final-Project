package Database;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqlServerDatabase implements Database {
    private static final String DB_URL = "jdbc:sqlserver://ITT-LALIT-K;databaseName=Cafeteria";
    private static final String DB_USER = "CafeteriaLogin";
    private static final String DB_PASSWORD = "root";

    private static final String USER_CREDENTIAL_TABLE = "UserCredential";
    private static final String LOGIN_ATTEMPTS_TABLE = "LoginAttempts";
    private static final String LOGOUT_ATTEMPTS_TABLE = "LogoutAttempts";
    private static final String USER_ROLE_TABLE = "UserRole";
    private static final String USER_TABLE = "[User]";
    private static final String MENU_TABLE = "Menu";

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    @Override
    public boolean checkCredentials(int userId, String password, int role) {
        String query = "SELECT * FROM UserCredential JOIN [User] ON UserCredential.UserId = [User].Id WHERE [User].Id = ? and UserCredential.Password=? and [User].RoleId=?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setString(2, password);
            statement.setInt(3,role);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public int insertSelectedFoodItemsInDB(BufferedReader in, PrintWriter out, List<Integer> ids) {
        int breakfastMenuItemId = ids.get(0);
        int lunchMenuItemId = ids.get(1);
        int dinnerMenuItemId = ids.get(2);
        int userId = ids.get(3);

        String query = "INSERT INTO EmployeeFoodVotes (FoodItemId, UserId, DateTime) VALUES (?, ?, GETDATE())";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // Insert breakfast item
            statement.setInt(1, breakfastMenuItemId);
            statement.setInt(2, userId);
            statement.addBatch();

            // Insert lunch item
            statement.setInt(1, lunchMenuItemId);
            statement.setInt(2, userId);
            statement.addBatch();

            // Insert dinner item
            statement.setInt(1, dinnerMenuItemId);
            statement.setInt(2, userId);
            statement.addBatch();

            int[] results = statement.executeBatch();

            // Check if all rows were inserted successfully
            for (int result : results) {
                if (result != PreparedStatement.SUCCESS_NO_INFO && result != 1) {
                    return 0; // Indicate failure if any row wasn't inserted successfully
                }
            }
            return 1; // Indicate success if all rows were inserted successfully

        } catch (SQLException e) {
            e.printStackTrace();
            return 0; // Indicate failure
        }
    }

    @Override
    public  int rolloutFinalizedMenusStatusUpdate(){
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE RecommendedMenu SET Status = 1 WHERE Status = 0")) {
            stmt.executeUpdate();
            return 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    @Override
    public int giveFoodFeedback(int FoodItemId,int Rating,String comment,int userId){
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO FoodFeedbackHistory (userId, FoodItemId, Rating, Comments,DateTime) VALUES (?, ?, ?, ?,GETDATE())")) {
            stmt.setInt(1, userId);
            stmt.setInt(2, FoodItemId);
            stmt.setInt(3, Rating);
            stmt.setString(4, comment);
            int rowAffected=stmt.executeUpdate();
            if(rowAffected > 0 ){
                return  1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public  List<String> getFoodFeedbackHistory() throws  SQLException{
        Connection conn = getConnection();
        List<String> selectedFoodItemsByEmployees = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement("SELECT ffh.Id, ffh.UserId, u.Name AS UserName, ffh.FoodItemId, m.Name AS FoodItemName, mt.MealType, ffh.Rating, ffh.Comments, ffh.DateTime FROM FoodFeedbackHistory ffh JOIN [User] u ON ffh.UserId = u.Id JOIN Menu m ON ffh.FoodItemId = m.Id JOIN MealType mt ON m.MealId = mt.Id;\n")) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("Id");
                    int UserId = rs.getInt("UserId");
                    String UserName = rs.getString("UserName");
                    int foodItemId = rs.getInt("FoodItemId");
                    String foodItemName = rs.getString("FoodItemName");
                    String MealType = rs.getString("MealType");
                    int rating = rs.getInt("Rating");
                    String comment = rs.getString("Comments");
                    String date = rs.getString("DateTime");
                    String formattedItem = String.format("%-15d %-15d %-20s %-20d %-20s %-20s %-20d %-20s %-20s ", id, UserId, UserName,foodItemId,foodItemName,MealType,rating,comment,date);
                    selectedFoodItemsByEmployees.add(formattedItem);
                }
            }
        }
        return selectedFoodItemsByEmployees;
    }
    @Override
    public  List<String> getSelectedFoodItemsEmployees() throws SQLException {
        Connection conn = getConnection();
        List<String> selectedFoodItemsByEmployees = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement("SELECT efv.FoodItemId, m.Name, COUNT(efv.Id) AS VoteCount FROM EmployeeFoodVotes efv JOIN Menu m ON efv.FoodItemId = m.Id GROUP BY efv.FoodItemId, m.Name ORDER BY VoteCount DESC;")) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("FoodItemId");
                    String name = rs.getString("Name");
                    int voteCount = rs.getInt("VoteCount");
                    String formattedItem = String.format("%-15d %-20s %-20d ", id, name, voteCount);
                    selectedFoodItemsByEmployees.add(formattedItem);
                }
            }
        }
        return selectedFoodItemsByEmployees;
    }
    @Override
    public List<String> getFinalizedMenu() throws SQLException {
        Connection conn = getConnection();
        List<String> finalizedMenuItems = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement("SELECT Id, FoodItemId, Name FROM FinalizedMenu WHERE CONVERT(DATE, DateTime) = CONVERT(DATE, GETDATE());")) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("Id");
                    int foodItemId = rs.getInt("foodItemId");
                    String name = rs.getString("Name");
                    String formattedItem = String.format("%-15d %-15d %-20s", id,foodItemId, name);
                    finalizedMenuItems.add(formattedItem);
                }
            }
        }
        return finalizedMenuItems;
    }

    @Override
    public List<String> getFinalizedMenu(int breakfastMenuItemId, int lunchMenuItemId, int dinnerMenuItemId) throws SQLException {
        List<String> finalizedMenuItems = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT Menu.Id, Menu.Name as Name, Menu.Price as Price, MealType.MealType as MealType FROM Menu JOIN MealType ON Menu.MealId=MealType.Id WHERE Menu.Id IN (?, ?, ?)")) {
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

                    // Insert into FinalizedMenu table
                    try (PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO FinalizedMenu (FoodItemId, Name, DateTime) VALUES (?, ?, GETDATE())")) {
                        insertStmt.setInt(1, id);
                        insertStmt.setString(2, name); // Use setString for String values
                        insertStmt.executeUpdate();
                    }
                }
            }
        }
        return finalizedMenuItems;
    }



    @Override
    public void logLoginAttempt(int userId, boolean success) {
        String query = "INSERT INTO " + LOGIN_ATTEMPTS_TABLE + " (UserId, AttemptTime, Success) VALUES (?, GETDATE(), ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setBoolean(2, success);
            statement.executeUpdate();
        } catch (SQLException e) {
            // Log or throw custom exception
            e.printStackTrace();
        }
    }

    @Override
    public int rolloutRecommendation(){
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE RecommendedMenu SET Status = 0 WHERE Status = -1")) {
             stmt.executeUpdate();
             return 1;
        } catch (SQLException e) {
             e.printStackTrace();
             return 0;
        }
    }

    @Override
    public List<String> generateRecommendedMenu(){
        List<String> recommendedMenuItems = new ArrayList<>();
        try (Connection conn = getConnection()) {
            recommendedMenuItems.addAll(getMenuItems(conn, "Breakfast"));
            recommendedMenuItems.addAll(getMenuItems(conn, "Lunch"));
            recommendedMenuItems.addAll(getMenuItems(conn, "Dinner"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recommendedMenuItems;
    }

    public List<String> getRecommendedMenu() throws SQLException {
        List<String> menuItems = new ArrayList<>();
        String query = "SELECT FoodItemId, Name, Price, MealType, Status FROM RecommendedMenu WHERE Status = 0";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int foodItemIdid = rs.getInt("FoodItemId");
                String name = rs.getString("Name");
                double price = rs.getDouble("Price");
                String mealType = rs.getString("MealType");
                String formattedItem = String.format("%-15d %-20s ₹%-9.2f %-20s", foodItemIdid, name, price, mealType);
                menuItems.add(formattedItem);
            }
        }
        return menuItems;
    }

    @Override
    public List<String> getMenuItems(Connection conn, String mealType) throws SQLException {
        List<String> menuItems = new ArrayList<>();
        try (Statement stmt = conn.createStatement()) {
            try (ResultSet rs = stmt.executeQuery("SELECT DISTINCT TOP 3 Menu.Id as Id,Menu.Name as Name,Menu.Price as Price,MealType.MealType as MealType FROM FoodFeedbackHistory JOIN [User] " +
                    "ON FoodFeedbackHistory.UserId=[User].Id JOIN Menu ON Menu.Id = FoodFeedbackHistory.FoodItemId JOIN MealType ON " +
                    "Menu.MealId=MealType.Id WHERE Menu.IsAvailable = 1 AND MealType.MealType='" + mealType + "'")) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    double price = rs.getDouble("price");
                    String formattedItem = String.format("%-15d %-20s ₹%-9.2f %-20s ", id, name, price, mealType);
                    menuItems.add(formattedItem);

                    try (PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO RecommendedMenu (FoodItemId,Name, price, mealtype,status) VALUES (?,?, ?, ?,-1)")) {
                        insertStmt.setInt(1,id);
                        insertStmt.setString(2, name);
                        insertStmt.setDouble(3, price);
                        insertStmt.setString(4, mealType);
                        insertStmt.executeUpdate();
                    }

                    try (PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO Notification (Message,DateTime) VALUES ('Recommendation Menu is Ready',GETDATE())")) {
                        insertStmt.executeUpdate();
                    }
                }
            }
        }
        return menuItems;
    }


    @Override
    public void logLogoutAttempt(int userId, boolean success) {
        String query = "INSERT INTO " + LOGOUT_ATTEMPTS_TABLE + " (UserId, AttemptTime, Success) VALUES (?, GETDATE(), ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setBoolean(2, success);
            statement.executeUpdate();
        } catch (SQLException e) {
            // Log or throw custom exception
            e.printStackTrace();
        }
    }

    @Override
    public String getUserRole(int userId) {
        String query = "SELECT Role FROM UserRole JOIN [User] ON UserRole.Id =[User].RoleId WHERE [User].Id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("Role");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void createMenuItem(String name, double price, Integer mealType, int availability) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO Menu (name, price, mealId, isavailable) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, name);
            stmt.setDouble(2, price);
            stmt.setInt(3, mealType);
            stmt.setInt(4, availability);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (Connection conn = getConnection();
             PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO Notification (Message,DateTime) VALUES ('New Food Item is added into the Menu',GETDATE())")) {
            insertStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getNotifications(){
        List<String> notifications = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Message FROM Notification")) {
            while (rs.next()) {
                String message = rs.getString("Message");
                String formattedItem = String.format("%-20s",message);
                notifications.add(formattedItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }
    @Override
    public List<String> getMenuItems() {
        List<String> menuItems = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Id,name, price, mealId,isAvailable FROM Menu")) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                int mealId = rs.getInt("mealId");
                int isAvailable =rs.getInt("isAvailable");

                // Format the string
                String formattedItem = String.format("%-15d %-20s ₹%-9.2f %-15d %-15d", id,name, price, mealId,isAvailable);
                menuItems.add(formattedItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return menuItems;
    }
    @Override
    public boolean isValidMenuId(int menuId) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) AS count FROM Menu WHERE menuId = ?")) {
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

    @Override
    public boolean deleteMenuItem(int menuId){
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM Menu WHERE id = ?")) {
            stmt.setInt(1, menuId);
            int rowAffected=stmt.executeUpdate();
            if(rowAffected>0){
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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

        // Remove the last comma and space
        queryBuilder.delete(queryBuilder.length() - 2, queryBuilder.length());
        queryBuilder.append(" WHERE Id = ?");
        params.add(menuId);

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            int rowsUpdated = stmt.executeUpdate();
            System.out.println("Rows updated: " + rowsUpdated);

            // Insert the notification with the menuId included in the message
            String notificationMessage = "Food item with Menu Id " + menuId + " is updated";
            try (PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO Notification (Message,DateTime) VALUES (?,GETDATE())")) {
                insertStmt.setString(1, notificationMessage);
                insertStmt.executeUpdate();  // Use executeUpdate instead of executeQuery
            }

        } catch (SQLException e) {
            System.err.println("SQL error while updating menu item: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error while updating menu item: " + e.getMessage());
            e.printStackTrace();
        }
    }




}
