package services;

import models.User;

public class NotificationService {
    public boolean sendNotification(User user, String message) {
        System.out.println("Notification to " + user.getUsername() + ": " + message);
        return true;
    }
}