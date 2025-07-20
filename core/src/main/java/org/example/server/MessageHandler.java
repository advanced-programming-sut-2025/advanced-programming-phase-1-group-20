package org.example.server;

import org.example.common.models.App;
import org.example.common.models.Message;
import org.example.common.models.entities.User;
import org.example.server.GameServers.PlayerConnection;
import org.example.utils.auth.JWTUtils;
import com.google.gson.Gson;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageHandler {
    private final Map<String, GameSession> gameSessions;
    private final Map<String, PlayerConnection> playerConnections;
    private final Gson gson;
    private final ServerConfig config;

    public MessageHandler() {
        this.gameSessions = new ConcurrentHashMap<>();
        this.playerConnections = new ConcurrentHashMap<>();
        this.gson = new Gson();
        this.config = ServerConfig.getInstance();
    }

    public void addPlayerConnection(String username, PlayerConnection connection) {
        playerConnections.put(username, connection);
        System.out.println("Added player connection: " + username);
    }

    public void removePlayerConnection(String username) {
        PlayerConnection connection = playerConnections.remove(username);
        if (connection != null) {
            // Remove player from any game session
            for (GameSession session : gameSessions.values()) {
                session.removePlayer(username);
            }
            System.out.println("Removed player connection: " + username);
        }
    }

    public void processMessage(String username, Message message) {
        PlayerConnection connection = playerConnections.get(username);
        if (connection == null) {
            System.err.println("No connection found for user: " + username);
            return;
        }

        switch (message.getType()) {
            case CREATE_GAME:
                handleCreateGame(connection, message);
                break;
            case JOIN_GAME:
                handleJoinGame(connection, message);
                break;
            case LEAVE_GAME:
                handleLeaveGame(connection, message);
                break;
            case AUTH_LOGIN:
                handleAuthentication(connection, message);
                break;
            default:
                // Forward to game session if player is in one
                Object gameSessionObj = connection.getGameSession();
                if (gameSessionObj instanceof GameSession) {
                    GameSession gameSession = (GameSession) gameSessionObj;
                    gameSession.processMessage(username, message);
                } else {
                    sendErrorMessage(connection, "Player not in a game session");
                }
                break;
        }
    }

    private void handleCreateGame(PlayerConnection connection, Message message) {
        User user = connection.getUser();
        if (user == null) {
            sendErrorMessage(connection, "User not authenticated");
            return;
        }

        // Create new game session
        GameSession gameSession = new GameSession(user);
        gameSessions.put(gameSession.getSessionId(), gameSession);

        // Add creator to the session
        if (gameSession.addPlayer(connection, user)) {
            // Send success response
            Message response = new Message();
            response.setType(Message.Type.SUCCESS);
            response.putInBody("message", "Game created successfully");
            response.putInBody("gameSessionId", gameSession.getSessionId());
            response.putInBody("playerCount", gameSession.getPlayerCount());
            connection.sendMessage(response);

            System.out.println("Game session created: " + gameSession.getSessionId() + " by " + user.getUsername());
        } else {
            sendErrorMessage(connection, "Failed to create game session");
        }
    }

    private void handleJoinGame(PlayerConnection connection, Message message) {
        User user = connection.getUser();
        if (user == null) {
            sendErrorMessage(connection, "User not authenticated");
            return;
        }

        String gameSessionId = message.getFromBody("gameSessionId");
        if (gameSessionId == null) {
            sendErrorMessage(connection, "Game session ID required");
            return;
        }

        GameSession gameSession = gameSessions.get(gameSessionId);
        if (gameSession == null) {
            sendErrorMessage(connection, "Game session not found");
            return;
        }

        if (gameSession.isFull()) {
            sendErrorMessage(connection, "Game session is full");
            return;
        }

        if (gameSession.addPlayer(connection, user)) {
            // Send success response
            Message response = new Message();
            response.setType(Message.Type.SUCCESS);
            response.putInBody("message", "Joined game successfully");
            response.putInBody("gameSessionId", gameSessionId);
            response.putInBody("playerCount", gameSession.getPlayerCount());
            connection.sendMessage(response);

            System.out.println("Player " + user.getUsername() + " joined game session: " + gameSessionId);
        } else {
            sendErrorMessage(connection, "Failed to join game session");
        }
    }

    private void handleLeaveGame(PlayerConnection connection, Message message) {
        User user = connection.getUser();
        if (user == null) {
            sendErrorMessage(connection, "User not authenticated");
            return;
        }

        Object gameSessionObj = connection.getGameSession();
        if (gameSessionObj instanceof GameSession) {
            GameSession gameSession = (GameSession) gameSessionObj;
            gameSession.removePlayer(user.getUsername());

            // Send success response
            Message response = new Message();
            response.setType(Message.Type.SUCCESS);
            response.putInBody("message", "Left game successfully");
            connection.sendMessage(response);

            // Remove session if empty
            if (gameSession.getPlayerCount() == 0) {
                gameSessions.remove(gameSession.getSessionId());
                System.out.println("Removed empty game session: " + gameSession.getSessionId());
            }
        } else {
            sendErrorMessage(connection, "Player not in a game session");
        }
    }

    private void handleAuthentication(PlayerConnection connection, Message message) {
        String token = message.getFromBody("token");
        String username = message.getFromBody("username");

        if (token == null || username == null) {
            sendErrorMessage(connection, "Token and username required");
            return;
        }

        // Validate JWT token
        String tokenStatus = JWTUtils.validateToken(token);
        if (!tokenStatus.equals(JWTUtils.TOKEN_VALID)) {
            sendErrorMessage(connection, "Invalid or expired token");
            return;
        }

        // Get user from App
        User user = App.getUser(username);
        if (user == null) {
            sendErrorMessage(connection, "User not found");
            return;
        }

        // Set user in connection
        connection.setUser(user);
        connection.setState(PlayerConnection.ConnectionState.AUTHENTICATED);

        // Add to player connections
        addPlayerConnection(username, connection);

        // Send success response
        Message response = new Message();
        response.setType(Message.Type.SUCCESS);
        response.putInBody("message", "Authentication successful");
        response.putInBody("username", username);
        connection.sendMessage(response);

        System.out.println("Player " + username + " authenticated successfully");
    }

    private void sendErrorMessage(PlayerConnection connection, String errorMessage) {
        Message error = new Message();
        error.setType(Message.Type.ERROR);
        error.putInBody("message", errorMessage);
        error.putInBody("timestamp", System.currentTimeMillis());
        connection.sendMessage(error);
    }

    public Map<String, GameSession> getGameSessions() {
        return gameSessions;
    }

    public Map<String, PlayerConnection> getPlayerConnections() {
        return playerConnections;
    }

    public void shutdown() {
        // Stop all game sessions
        for (GameSession session : gameSessions.values()) {
            session.stopSession();
        }
        gameSessions.clear();

        // Disconnect all players
        for (PlayerConnection connection : playerConnections.values()) {
            connection.disconnect();
        }
        playerConnections.clear();

        System.out.println("MessageHandler shutdown completed");
    }
}
