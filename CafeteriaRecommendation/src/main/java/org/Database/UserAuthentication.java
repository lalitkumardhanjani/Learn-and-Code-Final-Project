package org.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserAuthentication {

    public boolean isValidUser(int userId, String password, int roleId) {
        String query = "SELECT * FROM UserCredential JOIN [User] ON UserCredential.UserId = [User].Id WHERE [User].Id = ? AND UserCredential.Password = ? AND [User].RoleId = ?";
        try (Connection dbConnection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = dbConnection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, password);
            preparedStatement.setInt(3, roleId);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException sqlException) {
            System.err.println("SQL Error while checking credentials: " + sqlException.getMessage());
        } catch (NullPointerException nullPointerException) {
            System.err.println("Null value encountered while checking credentials: " + nullPointerException.getMessage());
        } catch (IllegalArgumentException illegalArgumentException) {
            System.err.println("Illegal argument provided while checking credentials: " + illegalArgumentException.getMessage());
        } catch (Exception exception) {
            System.err.println("Unexpected error while checking credentials: " + exception.getMessage());
        }
        return false;
    }

    public void logLoginAttempt(int userId, boolean success) {
        String query = "INSERT INTO LoginAttempts (UserId, AttemptTime, Success) VALUES (?, GETDATE(), ?)";
        try (Connection dbConnection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = dbConnection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setBoolean(2, success);
            preparedStatement.executeUpdate();
        } catch (SQLException sqlException) {
            System.err.println("SQL Error while logging login attempt: " + sqlException.getMessage());
        } catch (NullPointerException nullPointerException) {
            System.err.println("Null value encountered while logging login attempt: " + nullPointerException.getMessage());
        } catch (Exception exception) {
            System.err.println("Unexpected error while logging login attempt: " + exception.getMessage());
        }
    }

    public void logLogoutAttempt(int userId, boolean success) {
        String query = "INSERT INTO LogoutAttempts (UserId, AttemptTime, Success) VALUES (?, GETDATE(), ?)";
        try (Connection dbConnection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = dbConnection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setBoolean(2, success);
            preparedStatement.executeUpdate();
        } catch (SQLException sqlException) {
            System.err.println("SQL Error while logging logout attempt: " + sqlException.getMessage());
        } catch (NullPointerException nullPointerException) {
            System.err.println("Null value encountered while logging logout attempt: " + nullPointerException.getMessage());
        } catch (Exception exception) {
            System.err.println("Unexpected error while logging logout attempt: " + exception.getMessage());
        }
    }

    public String getUserRole(int userId) {
        String query = "SELECT Role FROM UserRole JOIN [User] ON UserRole.Id = [User].RoleId WHERE [User].Id = ?";
        try (Connection dbConnection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = dbConnection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("Role");
            }
        } catch (SQLException sqlException) {
            System.err.println("SQL Error while retrieving user role: " + sqlException.getMessage());
        } catch (NullPointerException nullPointerException) {
            System.err.println("Null value encountered while retrieving user role: " + nullPointerException.getMessage());
        } catch (Exception exception) {
            System.err.println("Unexpected error while retrieving user role: " + exception.getMessage());
        }
        return null;
    }
}
