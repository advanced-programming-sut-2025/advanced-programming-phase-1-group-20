package org.example.server.GameServers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.javalin.Javalin;
import io.javalin.websocket.WsContext;
import org.example.common.models.Message;
import org.example.server.MessageHandler;
import org.example.server.ServerConfig;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import org.example.common.models.entities.User;

public class AppWebSocket {
    private static ConcurrentHashMap<String, PlayerConnection> connectedPlayers = new ConcurrentHashMap<>();
    private final Javalin app;
    private final MessageHandler messageHandler;
    private final ServerConfig config;
    private final static Gson gson = new GsonBuilder()
        .serializeSpecialFloatingPointValues()
        .registerTypeAdapter(Message.Type.class, new TypeAdapter<Message.Type>() {
            @Override
            public void write(JsonWriter out, Message.Type value) throws IOException {
                out.value(value.name());
            }

            @Override
            public Message.Type read(JsonReader in) throws IOException {
                String value = in.nextString();
                try {
                    return Message.Type.valueOf(value);
                } catch (IllegalArgumentException e) {
                    System.err.println("Unknown message type: " + value);
                    return null;
                }
            }
        })
        .create();

    public AppWebSocket(Javalin app, MessageHandler messageHandler) {
        this.app = app;
        this.messageHandler = messageHandler;
        this.config = ServerConfig.getInstance();
        configureWebSocketRoutes();
    }
    
    private void configureWebSocketRoutes() {
        String wsPath = config.getWebSocketPath();
        
        app.ws(wsPath, ws -> {
            ws.onConnect(this::handleConnect);
            ws.onMessage(ctx -> {
                // Get the message content from the context
                String messageJson = ctx.message();
                handleMessageWithContent(ctx, messageJson);
            });
            ws.onClose(this::handleClose);
            ws.onError(this::handleError);
        });
        
        System.out.println("WebSocket configured on path: " + wsPath);
    }
    
    private void handleConnect(WsContext ctx) {
        String sessionId = ctx.sessionId();
        System.out.println("New WebSocket connection: " + sessionId);
        
        // Create player connection
        PlayerConnection connection = new PlayerConnection(ctx);
        connectedPlayers.put(sessionId, connection);
        
        // Send welcome message
        Message welcomeMessage = new Message();
        welcomeMessage.setType(Message.Type.SUCCESS);
        welcomeMessage.putInBody("message", "Connected to server");
        welcomeMessage.putInBody("sessionId", sessionId);
        
        String messageJson = gson.toJson(welcomeMessage);
        ctx.send(messageJson);
    }
    
    private void handleMessage(WsContext ctx) {
        String sessionId = ctx.sessionId();
        // Fix: Javalin WebSocket API - message content is passed as parameter
        // We need to modify the WebSocket configuration to pass the message
        
        PlayerConnection connection = connectedPlayers.get(sessionId);
        if (connection == null) {
            System.err.println("No connection found for session: " + sessionId);
            return;
        }
        
        // For now, we'll create a simple test message to verify connection
        // The actual message handling will be fixed in the WebSocket configuration
        Message testMessage = new Message();
        testMessage.setType(Message.Type.SUCCESS);
        testMessage.putInBody("message", "Message received");
        
        String responseJson = gson.toJson(testMessage);
        ctx.send(responseJson);
        
        System.out.println("WebSocket message handler called for session: " + sessionId);
    }
    
    private void handleMessageWithContent(WsContext ctx, String messageJson) {
        String sessionId = ctx.sessionId();
        
        System.out.println("Received message from " + sessionId + ": " + messageJson);
        
        PlayerConnection connection = connectedPlayers.get(sessionId);
        if (connection == null) {
            System.err.println("No connection found for session: " + sessionId);
            return;
        }
        
        try {
            System.out.println("DEBUG: Parsing message JSON: " + messageJson);
            Message message = gson.fromJson(messageJson, Message.class);
            System.out.println("DEBUG: Parsed message type: " + message.getType());
            
            // Handle authentication first
            if (message.getType() == Message.Type.AUTH_LOGIN) {
                System.out.println("DEBUG: Handling AUTH_LOGIN message");
                handleAuthentication(connection, message);
            } else if (connection.getUser() != null) {
                // User is authenticated, forward to message handler
                System.out.println("DEBUG: Forwarding message to MessageHandler: " + message.getType());
                messageHandler.processMessage(connection.getUsername(), message);
            } else {
                // User not authenticated
                System.out.println("DEBUG: User not authenticated");
                sendErrorMessage(ctx, "Authentication required");
            }
            
        } catch (Exception e) {
            System.err.println("Error processing message from " + sessionId + ": " + e.getMessage());
            e.printStackTrace();
            sendErrorMessage(ctx, "Invalid message format");
        }
    }
    
    private void handleMessageFixed(WsContext ctx) {
        String sessionId = ctx.sessionId();
        
        System.out.println("WebSocket message received for session: " + sessionId);
        
        PlayerConnection connection = connectedPlayers.get(sessionId);
        if (connection == null) {
            System.err.println("No connection found for session: " + sessionId);
            return;
        }
        
        // For testing purposes, let's auto-authenticate users when they send any message
        if (connection.getUser() == null) {
            // Auto-authenticate for testing
            User testUser = new User();
            testUser.setUsername("test_user_" + sessionId.substring(0, 8));
            connection.setUser(testUser);
            connection.setState(PlayerConnection.ConnectionState.AUTHENTICATED);
            
            // Add to message handler
            messageHandler.addPlayerConnection(testUser.getUsername(), connection);
            
            // Send success response
            Message response = new Message();
            response.setType(Message.Type.SUCCESS);
            response.putInBody("message", "Auto-authenticated for testing");
            response.putInBody("username", testUser.getUsername());
            connection.sendMessage(response);
            
            System.out.println("Auto-authenticated user: " + testUser.getUsername());
        }
        
        // Send a test response
        Message testResponse = new Message();
        testResponse.setType(Message.Type.SUCCESS);
        testResponse.putInBody("message", "Message processed");
        connection.sendMessage(testResponse);
    }
    
    private void handleAuthentication(PlayerConnection connection, Message message) {
        String token = message.getFromBody("token");
        String username = message.getFromBody("username");
        
        System.out.println("Authentication attempt for user: " + username);
        
        if (token == null || username == null) {
            sendErrorMessage(connection.getWsContext(), "Token and username required");
            return;
        }
        
        // For now, we'll do simple validation - in production, validate JWT properly
        if (token.startsWith("temp_token_") || token.length() > 10) {
            // Set user in connection
            User user = new User();
            user.setUsername(username);
            connection.setUser(user);
            connection.setState(PlayerConnection.ConnectionState.AUTHENTICATED);
            
            // Add to message handler's player connections
            messageHandler.addPlayerConnection(username, connection);
            
            // Send success response
            Message response = new Message();
            response.setType(Message.Type.SUCCESS);
            response.putInBody("message", "Authentication successful");
            response.putInBody("username", username);
            connection.sendMessage(response);
            
            System.out.println("Player " + username + " authenticated successfully");
        } else {
            sendErrorMessage(connection.getWsContext(), "Invalid token");
        }
    }
    
    private void handleClose(WsContext ctx) {
        String sessionId = ctx.sessionId();
        System.out.println("WebSocket connection closed: " + sessionId);
        
        PlayerConnection connection = connectedPlayers.remove(sessionId);
        if (connection != null) {
            String username = connection.getUsername();
            if (username != null) {
                messageHandler.removePlayerConnection(username);
            }
            connection.disconnect();
        }
    }
    
    private void handleError(WsContext ctx) {
        String sessionId = ctx.sessionId();
        System.err.println("WebSocket error for session " + sessionId + ": " + "Connection error");
        
        // Clean up connection on error
        PlayerConnection connection = connectedPlayers.remove(sessionId);
        if (connection != null) {
            String username = connection.getUsername();
            if (username != null) {
                messageHandler.removePlayerConnection(username);
            }
            connection.disconnect();
        }
    }
    
    private void sendErrorMessage(WsContext ctx, String errorMessage) {
        Message error = new Message();
        error.setType(Message.Type.ERROR);
        error.putInBody("message", errorMessage);
        error.putInBody("timestamp", System.currentTimeMillis());
        
        String errorJson = gson.toJson(error);
        try {
            ctx.send(errorJson);
        } catch (Exception e) {
            System.err.println("Failed to send error message: " + e.getMessage());
        }
    }
    
    public static ConcurrentHashMap<String, PlayerConnection> getConnectedPlayers() {
        return connectedPlayers;
    }
    
    public static Gson getGson() {
        return gson;
    }
    
    public void shutdown() {
        // Disconnect all players
        for (PlayerConnection connection : connectedPlayers.values()) {
            connection.disconnect();
        }
        connectedPlayers.clear();
        System.out.println("AppWebSocket shutdown completed");
    }
}
