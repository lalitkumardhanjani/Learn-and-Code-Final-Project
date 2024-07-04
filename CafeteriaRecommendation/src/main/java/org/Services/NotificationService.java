package org.Services;

import org.Database.DatabaseConnection;
import org.Database.NotificationDatabase;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class NotificationService {
    private final NotificationDatabase database;

    public NotificationService(NotificationDatabase database) {
        this.database = database;
    }

    public void viewNotifications(PrintWriter out) {
        try {
            List<String> notifications = database.getNotifications();
            if (notifications.isEmpty()) {
                out.println("No notifications are present.");
            } else {
                out.println("-------------- Notifications -------------");
                out.println("------------------------------------------");

                for (String notification : notifications) {
                    out.println(notification);
                }
                out.println("------------------------------------------");
            }
            out.println("END");
        } catch (Exception e) {
            out.println("Error retrieving notifications: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void addNotification(String message) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO Notification (Message, DateTime) VALUES (?, GETDATE())")) {
            stmt.setString(1, message);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error while inserting notification: " + e.getMessage());
        }
    }
}
