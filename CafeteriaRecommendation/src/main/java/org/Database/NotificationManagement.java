package org.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationManagement {
    public List<String> getNotifications() {
        List<String> notifications = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT Message FROM Notification")) {
            while (rs.next()) {
                String message = rs.getString("Message");
                String formattedItem = String.format("%-20s", message);
                notifications.add(formattedItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }
}
