package org.example.common.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatMessage {
    private String sender;
    private String content;
    private String timestamp;
    private ChatType type;
    private String recipient; // For private messages
    private String roomId; // For room-based messages

    public enum ChatType {
        PUBLIC,
        PRIVATE,
        ROOM
    }

    public ChatMessage() {
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public ChatMessage(String sender, String content, ChatType type) {
        this();
        this.sender = sender;
        this.content = content;
        this.type = type;
    }

    public ChatMessage(String sender, String content, ChatType type, String recipient) {
        this(sender, content, type);
        this.recipient = recipient;
    }

    public ChatMessage(String sender, String content, ChatType type, String recipient, String roomId) {
        this(sender, content, type, recipient);
        this.roomId = roomId;
    }

    // Getters and Setters
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public ChatType getType() {
        return type;
    }

    public void setType(ChatType type) {
        this.type = type;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    @Override
    public String toString() {
        switch (type) {
            case PRIVATE:
                return String.format("[%s] %s -> %s: %s", timestamp, sender, recipient, content);
            case ROOM:
                return String.format("[%s] [%s] %s: %s", timestamp, roomId, sender, content);
            default:
                return String.format("[%s] %s: %s", timestamp, sender, content);
        }
    }
}

