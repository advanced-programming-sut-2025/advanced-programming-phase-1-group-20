package org.example.common.network.responses;

/**
 * Response for chat message operations.
 */
public class ChatResponse extends Response {
    private final String messageId;
    private final String sender;
    private final String message;
    private final String recipient;
    private final long timestamp;
    private final boolean delivered;
    
    public ChatResponse(String requestId, String sourceId, ResponseStatus status, 
                       String message, String messageId, String sender, String messageText, 
                       String recipient, long timestamp, boolean delivered) {
        super(requestId, sourceId, status, message);
        this.messageId = messageId;
        this.sender = sender;
        this.message = messageText;
        this.recipient = recipient;
        this.timestamp = timestamp;
        this.delivered = delivered;
    }
    
    public String getMessageId() {
        return messageId;
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
        return timestamp;
    }
    
    public boolean isDelivered() {
        return delivered;
    }
    
    public static ChatResponse success(String requestId, String sourceId, String messageId, 
                                     String sender, String message, String recipient) {
        return new ChatResponse(requestId, sourceId, ResponseStatus.SUCCESS, 
                              "Message sent successfully", messageId, sender, message, 
                              recipient, System.currentTimeMillis(), true);
    }
    
    public static ChatResponse error(String requestId, String sourceId, String errorMessage) {
        return new ChatResponse(requestId, sourceId, ResponseStatus.ERROR, 
                              errorMessage, null, null, null, null, 0, false);
    }
} 