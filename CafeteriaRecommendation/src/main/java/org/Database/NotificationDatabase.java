package org.Database;

import java.util.List;

public interface NotificationDatabase {
    List<String> getNotifications();

    void addNotification(String message);
}
