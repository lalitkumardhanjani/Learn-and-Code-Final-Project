package org.Database;

import java.util.List;

public interface INotificationDatabase {
    List<String> getNotifications();

    void addNotification(String message);
}
