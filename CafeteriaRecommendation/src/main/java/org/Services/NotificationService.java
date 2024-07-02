package org.Services;

import org.Database.NotificationDatabase;

import java.io.PrintWriter;
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
}
