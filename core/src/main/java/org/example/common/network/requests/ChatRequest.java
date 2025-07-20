package org.example.common.network.requests;

import org.example.common.network.responses.ChatResponse;
import org.example.common.network.responses.Response;
import org.example.common.network.routes.Route;

/**
 * Request for chat message operations.
 */
public class ChatRequest extends Request {
    private final String sender;
    private final String message;
    private final String recipient; // null for global chat
    private final long messageTimestamp;
    
    public ChatRequest(String sender, String message, String recipient, String sourceId) {
        super(new Route("/chat", Route.RouteType.COMMUNICATION, "ChatHandler"), sourceId);
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
    
    @Override
    public Class<? extends Response> getExpectedResponseType() {
        return ChatResponse.class;
    }
} 