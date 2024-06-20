package Notification;

public class SocketNotificationHandler implements Notification {
    private int id;
    private String message;

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void sendNotification() {
        // Implementation for sending a notification
    }
}