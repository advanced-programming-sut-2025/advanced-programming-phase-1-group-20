package org.example.common.network.handlers;

import org.example.common.network.requests.ChatRequest;
import org.example.common.network.requests.Request;
import org.example.common.network.responses.ChatResponse;
import org.example.common.network.responses.Response;
import org.example.common.network.routing.RequestHandler;

import java.util.concurrent.CompletableFuture;

/**
 * Handler for chat message requests.
 */
public class ChatHandler implements RequestHandler {
    
    @Override
    public CompletableFuture<Response> handle(Request request) {
        if (!(request instanceof ChatRequest)) {
            CompletableFuture<Response> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalArgumentException("Invalid request type"));
            return future;
        }
        
        ChatRequest chatRequest = (ChatRequest) request;
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                return processChatMessage(chatRequest);
            } catch (Exception e) {
                return ChatResponse.error(chatRequest.getRequestId(), 
                                        chatRequest.getSourceId(), e.getMessage());
            }
        });
    }
    
    private ChatResponse processChatMessage(ChatRequest request) {
        String sender = request.getSender();
        String message = request.getMessage();
        String recipient = request.getRecipient();
        
        // Validate message
        if (message == null || message.trim().isEmpty()) {
            return ChatResponse.error(request.getRequestId(), 
                                    request.getSourceId(), "Message cannot be empty");
        }
        
        if (message.length() > 500) { // Example max length
            return ChatResponse.error(request.getRequestId(), 
                                    request.getSourceId(), "Message too long");
        }
        
        // Check if recipient exists (for private messages)
        if (request.isPrivateMessage() && !isPlayerOnline(recipient)) {
            return ChatResponse.error(request.getRequestId(), 
                                    request.getSourceId(), "Player not online");
        }
        
        // Generate message ID
        String messageId = generateMessageId();
        
        // Store message in chat history
        storeChatMessage(messageId, sender, message, recipient);
        
        return ChatResponse.success(request.getRequestId(), 
                                  request.getSourceId(), messageId, sender, message, recipient);
    }
    
    private boolean isPlayerOnline(String username) {
        // Check if player is online
        // This would integrate with your existing online players management
        return true; // Placeholder
    }
    
    private String generateMessageId() {
        return "msg_" + System.currentTimeMillis() + "_" + 
               java.util.UUID.randomUUID().toString().substring(0, 8);
    }
    
    private void storeChatMessage(String messageId, String sender, String message, String recipient) {
        // Store chat message in history
        // This would integrate with your existing chat system
        System.out.println("Chat message stored: " + messageId + " from " + sender + " to " + 
                          (recipient != null ? recipient : "all"));
    }
} 