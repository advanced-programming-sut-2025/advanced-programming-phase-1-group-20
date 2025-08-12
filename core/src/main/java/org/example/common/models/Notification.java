package org.example.common.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Notification {
    private String id;
    private String title;
    private String message;
    private NotificationType type;
    private NotificationPriority priority;
    private String timestamp;
    private boolean isRead;
    private String recipient;
    private String sender;

    public enum NotificationType {
        CHAT_MESSAGE,
        PRIVATE_MESSAGE,
        SYSTEM_MESSAGE,
        TRADE_REQUEST,
        GIFT_RECEIVED,
        QUEST_UPDATE,
        WEATHER_ALERT,
        FARM_UPDATE
    }

    public enum NotificationPriority {
        LOW,
        NORMAL,
        HIGH,
        URGENT
    }

    public Notification() {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        this.isRead = false;
    }

    public Notification(String title, String message, NotificationType type) {
        this();
        this.title = title;
        this.message = message;
        this.type = type;
        this.priority = NotificationPriority.NORMAL;
    }

    public Notification(String title, String message, NotificationType type, NotificationPriority priority) {
        this(title, message, type);
        this.priority = priority;
    }

    public Notification(String title, String message, NotificationType type, NotificationPriority priority, String recipient) {
        this(title, message, type, priority);
        this.recipient = recipient;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public NotificationPriority getPriority() {
        return priority;
    }

    public void setPriority(NotificationPriority priority) {
        this.priority = priority;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s", timestamp, title, message);
    }
}

