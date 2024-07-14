package org.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NotificationManagement {

    public static void addNotification(String message) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO Notification (Message, DateTime) VALUES (?, GETDATE())")) {
            statement.setString(1, message);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error while inserting notification: " + e.getMessage());
        }
    }

    public List<String> getNotifications() {
        List<String> notifications = new ArrayList<>();
        String query = "SELECT Message FROM Notification";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String message = resultSet.getString("Message");
                String formattedMessage = String.format("%-20s", message);
                notifications.add(formattedMessage);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving notifications: " + e.getMessage());
        } catch (NullPointerException e) {
            System.err.println("Null value encountered: " + e.getMessage());
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Array index out of bounds: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Illegal argument provided: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }

        return notifications;
    }
}
