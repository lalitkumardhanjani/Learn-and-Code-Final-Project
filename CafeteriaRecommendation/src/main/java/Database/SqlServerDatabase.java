package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlServerDatabase implements Database {
    private static final String DB_URL = "jdbc:sqlserver://ITT-LALIT-K;databaseName=Cafeteria";
    private static final String DB_USER = "CafeteriaLogin";
    private static final String DB_PASSWORD = "root";

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    @Override
    public boolean checkCredentials(int userId, String password) {
        String query = "SELECT * FROM UserCredential WHERE UserId = ? AND Password = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void LogLoginAttempt(int userId, boolean success) {
        String query = "INSERT INTO LoginAttempts (UserId, AttemptTime, Success) VALUES (?, GETDATE(), ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setBoolean(2, success);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void LogLogoutAttempt(int userId, boolean success) {
        String query = "INSERT INTO LogoutAttempts (UserId, AttemptTime, Success) VALUES (?, GETDATE(), ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setBoolean(2, success);
            statement.executeUpdate();
        } catch (SQLException e) {
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
    public  boolean isMenuItemExists(String name){
        String menuItemExist = "IF EXISTS (SELECT Name FROM Menu WHERE Name = 'Pohe') SELECT 1 ELSE SELECT 0";

        try(Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(menuItemExist);
            statement.setString(1,name);
            int
        ) catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean createMenuItem(String name, double price,Integer MealType) {
        String query = "INSERT INTO Menu (Name, Price, IsAvailable,MealId) VALUES (?, ?, 1,?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setDouble(2, price);
            statement.setInt(3,MealType);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
