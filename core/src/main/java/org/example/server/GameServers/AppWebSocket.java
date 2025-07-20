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

public class AppWebSocket {
    private static ConcurrentHashMap<String, PlayerConnection> connectedPlayers = new ConcurrentHashMap<>();
    private final Javalin app;
    private final MessageHandler messageHandler;
    private final ServerConfig config;
    private final static Gson gson = new GsonBuilder()
        .registerTypeAdapter(LocalDateTime.class, new TypeAdapter<LocalDateTime>() {
            @Override
            public void write(JsonWriter out, LocalDateTime value) throws IOException {
                out.value(value.toString());
            }

            @Override
            public LocalDateTime read(JsonReader in) throws IOException {
                return LocalDateTime.parse(in.nextString());
            }
        })
        .serializeSpecialFloatingPointValues()
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
            ws.onMessage(this::handleMessage);
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
        // TODO: Fix WebSocket API call
        String messageJson = "{}"; // ctx.message();
        
        System.out.println("Received message from " + sessionId + ": " + messageJson);
        
        PlayerConnection connection = connectedPlayers.get(sessionId);
        if (connection == null) {
            System.err.println("No connection found for session: " + sessionId);
            return;
        }
        
        try {
            Message message = gson.fromJson(messageJson, Message.class);
            
            // Handle authentication first
            if (message.getType() == Message.Type.AUTH_LOGIN) {
                connection.processIncomingMessage(messageJson);
            } else if (connection.getUser() != null) {
                // User is authenticated, forward to message handler
                messageHandler.processMessage(connection.getUser().getUsername(), message);
            } else {
                // User not authenticated
                sendErrorMessage(ctx, "Authentication required");
            }
            
        } catch (Exception e) {
            System.err.println("Error processing message from " + sessionId + ": " + e.getMessage());
            sendErrorMessage(ctx, "Invalid message format");
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
