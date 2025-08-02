package org.example.common.network.events;


public class ChatNotification extends Notification {
    private final String sender;
    private final String message;
    private final String recipient; // null for global chat
    private final long messageTimestamp;

    public ChatNotification(String sender, String message, String recipient, String sourceId) {
        super(NotificationType.CHAT_MESSAGE, sourceId, recipient); // Target specific recipient or broadcast
        this.sender = sender;
        this.message = message;
        this.recipient = recipient;
        this.messageTimestamp = System.currentTimeMillis();
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public String getRecipient() {
        return recipient;
    }

    public long getMessageTimestamp() {
        return messageTimestamp;
    }

    public boolean isPrivateMessage() {
        return recipient != null && !recipient.isEmpty();
    }
}
