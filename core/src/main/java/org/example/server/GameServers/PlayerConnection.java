package org.example.server.GameServers;

import io.javalin.websocket.WsContext;
import org.example.common.models.Message;
import org.example.common.models.Player.Player;
import org.example.common.models.entities.User;
import com.google.gson.Gson;
import java.lang.reflect.Method;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Queue;

public class PlayerConnection {
    private String username;
    private WsContext wsContext;
    private User user;
    private Player player;
    private Object gameSession; // Will be GameSession when available
    private final Queue<String> outgoingMessages;
    private final Gson gson;
    private long lastHeartbeat;
    private ConnectionState state;
    
    public enum ConnectionState {
        CONNECTING,
        AUTHENTICATED,
        IN_GAME,
        DISCONNECTED
    }
    
    public PlayerConnection(WsContext wsContext) {
        this.wsContext = wsContext;
        this.outgoingMessages = new ConcurrentLinkedQueue<>();
        this.gson = new Gson();
        this.lastHeartbeat = System.currentTimeMillis();
        this.state = ConnectionState.CONNECTING;
    }
    
    // Getters and Setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public WsContext getWsContext() {
        return wsContext;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
        this.username = user.getUsername();
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public void setPlayer(Player player) {
        this.player = player;
    }
    
    public Object getGameSession() {
        return gameSession;
    }
    
    public void setGameSession(Object gameSession) {
        this.gameSession = gameSession;
        this.state = ConnectionState.IN_GAME;
    }
    
    public ConnectionState getState() {
        return state;
    }
    
    public void setState(ConnectionState state) {
        this.state = state;
    }
    
    // Message handling methods
    public void sendMessage(String messageJson) {
        if (wsContext == null || !wsContext.session.isOpen()) {
            System.err.println("DEBUG: Cannot send message - WebSocket context is null or closed");
            return;
        }
        
        try {
            System.out.println("DEBUG: Sending WebSocket message to " + username + ": " + messageJson);
            wsContext.send(messageJson);
            System.out.println("DEBUG: WebSocket message sent successfully");
        } catch (Exception e) {
            System.err.println("DEBUG: Failed to send WebSocket message to " + username + ": " + e.getMessage());
            e.printStackTrace();
            // Add to queue for retry
            outgoingMessages.offer(messageJson);
        }
    }
    
    public void sendMessage(Message message) {
        try {
            String messageJson = gson.toJson(message);
            System.out.println("DEBUG: Sending message JSON: " + messageJson);
            sendMessage(messageJson);
        } catch (Exception e) {
            System.err.println("DEBUG: Failed to serialize message to JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void processIncomingMessage(String messageJson) {
        try {
            Message message = gson.fromJson(messageJson, Message.class);
            
            switch (message.getType()) {
                case AUTH_LOGIN:
                    handleAuthentication(message);
                    break;
                case PING:
                    handlePing(message);
                    break;
                case HEARTBEAT:
                    updateHeartbeat();
                    break;
                default:
                    // Forward to game session if authenticated and in game
                    if (state == ConnectionState.IN_GAME && gameSession != null) {
                        // Use reflection to call processMessage on GameSession
                        try {
                            java.lang.reflect.Method method = gameSession.getClass().getMethod("processMessage", String.class, Message.class);
                            method.invoke(gameSession, username, message);
                        } catch (Exception e) {
                            System.err.println("Failed to forward message to game session: " + e.getMessage());
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            System.err.println("Failed to process message from " + username + ": " + e.getMessage());
            sendErrorMessage("Invalid message format");
        }
    }
    
    private void handleAuthentication(Message message) {
        String token = message.getFromBody("token");
        String requestedUsername = message.getFromBody("username");
        
        // TODO: Validate JWT token and get user
        // For now, simple validation
        if (token != null && requestedUsername != null) {
            // This would normally validate the JWT token
            // For now, we'll create a basic user object
            this.user = new User(requestedUsername, "", "", "", null);
            this.username = requestedUsername;
            this.state = ConnectionState.AUTHENTICATED;
            
            // Send success response
            Message response = new Message();
            response.setType(Message.Type.SUCCESS);
            response.putInBody("message", "Authentication successful");
            response.putInBody("username", username);
            sendMessage(response);
            
            System.out.println("Player " + username + " authenticated successfully");
        } else {
            sendErrorMessage("Authentication failed: Invalid token or username");
        }
    }
    
    private void handlePing(Message message) {
        Message pong = new Message();
        pong.setType(Message.Type.PONG);
        pong.putInBody("timestamp", System.currentTimeMillis());
        sendMessage(pong);
    }
    
    private void updateHeartbeat() {
        this.lastHeartbeat = System.currentTimeMillis();
    }
    
    public boolean isAlive() {
        long currentTime = System.currentTimeMillis();
        long heartbeatTimeout = 60000; // 60 seconds timeout
        return (currentTime - lastHeartbeat) < heartbeatTimeout;
    }
    
    public void sendErrorMessage(String errorMessage) {
        Message error = new Message();
        error.setType(Message.Type.ERROR);
        error.putInBody("message", errorMessage);
        error.putInBody("timestamp", System.currentTimeMillis());
        sendMessage(error);
    }
    
    public void disconnect() {
        this.state = ConnectionState.DISCONNECTED;
        
        // Remove from game session if in one
        if (gameSession != null && username != null) {
            // Use reflection to call removePlayer on GameSession
            try {
                java.lang.reflect.Method method = gameSession.getClass().getMethod("removePlayer", String.class);
                method.invoke(gameSession, username);
            } catch (Exception e) {
                System.err.println("Failed to remove player from game session: " + e.getMessage());
            }
        }
        
        // Close WebSocket connection
        if (wsContext != null && wsContext.session.isOpen()) {
            try {
                wsContext.closeSession();
            } catch (Exception e) {
                System.err.println("Error closing WebSocket for " + username + ": " + e.getMessage());
            }
        }
        
        System.out.println("Player " + username + " disconnected");
    }
    
    // Process any queued outgoing messages
    public void processOutgoingQueue() {
        while (!outgoingMessages.isEmpty() && wsContext != null && wsContext.session.isOpen()) {
            String message = outgoingMessages.poll();
            try {
                wsContext.send(message);
            } catch (Exception e) {
                // If still failing, put back in queue and break
                outgoingMessages.offer(message);
                break;
            }
        }
    }
}
