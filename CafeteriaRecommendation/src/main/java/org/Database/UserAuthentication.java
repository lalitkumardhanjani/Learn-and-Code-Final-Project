package org.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserAuthentication {

    public boolean checkCredentials(int userId, String password, int role) {
        String query = "SELECT * FROM UserCredential JOIN [User] ON UserCredential.UserId = [User].Id WHERE [User].Id = ? and UserCredential.Password=? and [User].RoleId=?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setString(2, password);
            statement.setInt(3, role);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            System.err.println("SQL Error while checking credentials: " + e.getMessage());
        } catch (NullPointerException e) {
            System.err.println("Null value encountered while checking credentials: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Illegal argument provided while checking credentials: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error while checking credentials: " + e.getMessage());
        }
        return false;
    }

    public void logLoginAttempt(int userId, boolean success) {
        String query = "INSERT INTO LoginAttempts (UserId, AttemptTime, Success) VALUES (?, GETDATE(), ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setBoolean(2, success);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("SQL Error while logging login attempt: " + e.getMessage());
        } catch (NullPointerException e) {
            System.err.println("Null value encountered while logging login attempt: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error while logging login attempt: " + e.getMessage());
        }
    }

    public void logLogoutAttempt(int userId, boolean success) {
        String query = "INSERT INTO LogoutAttempts (UserId, AttemptTime, Success) VALUES (?, GETDATE(), ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setBoolean(2, success);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("SQL Error while logging logout attempt: " + e.getMessage());
        } catch (NullPointerException e) {
            System.err.println("Null value encountered while logging logout attempt: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error while logging logout attempt: " + e.getMessage());
        }
    }

    public String getUserRole(int userId) {
        String query = "SELECT Role FROM UserRole JOIN [User] ON UserRole.Id =[User].RoleId WHERE [User].Id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("Role");
            }
        } catch (SQLException e) {
            System.err.println("SQL Error while retrieving user role: " + e.getMessage());
        } catch (NullPointerException e) {
            System.err.println("Null value encountered while retrieving user role: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error while retrieving user role: " + e.getMessage());
        }
        return null;
    }

}
