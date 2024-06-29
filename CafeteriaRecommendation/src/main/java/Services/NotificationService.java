package Services;

import Database.Database;

import java.io.PrintWriter;
import java.util.List;

public class NotificationService {
    private Database database;

    public NotificationService(Database database) {
        this.database = database;
    }

    public void viewNotifications(PrintWriter out) {
        List<String> notifications = database.getNotifications();
        if (notifications.isEmpty()) {
            out.println("No notifications are present");
        } else {
            out.println("-------------- Notifications -------------");
            out.println("------------------------------------------");

            for (String notification : notifications) {
                out.println(notification);
            }
            out.println("--------------------------------------------");
            out.println("END");
        }
    }
}
