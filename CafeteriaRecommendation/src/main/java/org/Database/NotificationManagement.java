package org.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationManagement {

    public static void addNotification(String message) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO Notification (Message, DateTime) VALUES (?, GETDATE())")) {
            stmt.setString(1, message);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error while inserting notification: " + e.getMessage());
        }
    }

    public List<String> getNotifications() {
        List<String> notifications = new ArrayList<>();
        String query = "SELECT Message FROM Notification";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String message = rs.getString("Message");
                String formattedItem = String.format("%-20s", message);
                notifications.add(formattedItem);
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
