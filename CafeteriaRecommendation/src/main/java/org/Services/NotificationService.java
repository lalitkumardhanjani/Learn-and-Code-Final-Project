package org.Services;

import org.Database.*;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class NotificationService {
    private final INotificationDatabase notificationDatabase;

    public NotificationService(INotificationDatabase notificationDatabase) {
        this.notificationDatabase = notificationDatabase;
    }

    public void viewNotifications(PrintWriter outputWriter) {
        try {
            List<String> notificationList = notificationDatabase.getNotifications();
            if (notificationList.isEmpty()) {
                outputWriter.println("No notifications are present.");
            } else {
                outputWriter.println("-------------- Notifications -------------");
                outputWriter.println("------------------------------------------");

                for (String notification : notificationList) {
                    outputWriter.println(notification);
                }
                outputWriter.println("------------------------------------------");
            }
            outputWriter.println("END");
        } catch (Exception exception) {
            outputWriter.println("Error retrieving notifications: " + exception.getMessage());
            exception.printStackTrace();
        }
    }

}
