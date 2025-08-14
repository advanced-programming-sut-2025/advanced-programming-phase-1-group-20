package org.example.server.GameServers;

import io.javalin.websocket.WsContext;
import org.example.common.models.Message;
import org.example.common.models.Player.Player;
import org.example.common.models.entities.User;
import org.example.common.models.App;
import org.example.utils.auth.JWTUtils;
import com.google.gson.Gson;

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

    public void setWsContext(WsContext wsContext) {
        this.wsContext = wsContext;
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
// System.err.println("DEBUG: Cannot send message - WebSocket context is null or closed");
            return;
        }

        try {
            wsContext.send(messageJson);
        } catch (Exception e) {
// System.err.println("DEBUG: Failed to send WebSocket message to " + username + ": " + e.getMessage());
            e.printStackTrace();
            // Add to queue for retry
            outgoingMessages.offer(messageJson);
        }
    }

    public void sendMessage(Message message) {
        try {
            String messageJson = gson.toJson(message);
            if (message != null && message.getType() != null &&
                (message.getType().name().startsWith("TRADE_") || message.getType() == Message.Type.CHAT_PUBLIC)) {
            }
            sendMessage(messageJson);
        } catch (Exception e) {
// System.err.println("DEBUG: Failed to serialize message to JSON: " + e.getMessage());
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

        // Require both token and username
        if (token == null || requestedUsername == null) {
            sendErrorMessage("Token and username required");
            return;
        }

        // Allow temporary tokens for testing flows
        if (token.startsWith("temp_token_")) {
            User tempUser = new User();
            tempUser.setUsername(requestedUsername);
            this.user = tempUser;
            this.username = requestedUsername;
            this.state = ConnectionState.AUTHENTICATED;

            Message response = new Message();
            response.setType(Message.Type.SUCCESS);
            response.putInBody("message", "Authentication successful");
            response.putInBody("username", username);
            sendMessage(response);
            return;
        }

        // Validate JWT token
        String tokenStatus = JWTUtils.getTokenStatus(token);
        if (!JWTUtils.TOKEN_VALID.equals(tokenStatus)) {
            sendErrorMessage("Invalid or expired token: " + JWTUtils.getTokenStatusMessage(tokenStatus));
            return;
        }

        // Ensure the token subject matches the requested username
        String tokenUsername = JWTUtils.extractUsername(token);
        if (tokenUsername == null || !tokenUsername.equals(requestedUsername)) {
            sendErrorMessage("Token username mismatch");
            return;
        }

        // Load or create the user
        User existingUser = App.getUser(requestedUsername);
        if (existingUser == null) {
            existingUser = new User();
            existingUser.setUsername(requestedUsername);
            App.addUser(existingUser);
        }

        this.user = existingUser;
        this.username = requestedUsername;
        this.state = ConnectionState.AUTHENTICATED;

        // Send success response
        Message response = new Message();
        response.setType(Message.Type.SUCCESS);
        response.putInBody("message", "Authentication successful");
        response.putInBody("username", username);
        sendMessage(response);
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

        // we don't immediately remove from game session - let the delayed removal handle it
        // This allows for reconnection without losing game state

        // Close WebSocket connection
        if (wsContext != null && wsContext.session.isOpen()) {
            try {
                wsContext.closeSession();
            } catch (Exception e) {
                System.err.println("Error closing WebSocket for " + username + ": " + e.getMessage());
            }
        }
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
