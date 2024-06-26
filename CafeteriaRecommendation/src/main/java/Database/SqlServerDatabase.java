package Database;

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
    public List<String> getFinalizedMenu(int breakfastMenuItemId, int lunchMenuItemId, int dinnerMenuItemId) throws SQLException {
        Connection conn = getConnection();
        List<String> finalizedMenuItems = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement("SELECT Menu.Id, Menu.Name as Name, Menu.Price as Price, MealType.MealType as MealType FROM Menu JOIN MealType ON Menu.MealId=MealType.Id WHERE Menu.Id IN (?, ?, ?)")) {
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
    public List<String> getRecommendedMenu(){
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

                    // Insert into RecommendedMenu table
                    try (PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO RecommendedMenu (Name, price, mealtype,status) VALUES (?, ?, ?,-1)")) {
                        insertStmt.setString(1, name);
                        insertStmt.setDouble(2, price);
                        insertStmt.setString(3, mealType);
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
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO Menu (name, price, mealType, availability) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, name);
            stmt.setDouble(2, price);
            stmt.setInt(3, mealType);
            stmt.setInt(4, availability);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        if (newPrice >= 0 && newName != null) {
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
        } catch (SQLException e) {
            System.err.println("SQL error while updating menu item: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error while updating menu item: " + e.getMessage());
            e.printStackTrace();
        }
    }



}
