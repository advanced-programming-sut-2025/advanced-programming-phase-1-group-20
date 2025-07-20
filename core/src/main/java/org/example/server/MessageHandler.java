package org.example.server;

import org.example.common.Lobby.Lobby;
import org.example.common.Lobby.LobbyPlayer;
import org.example.common.Lobby.LobbySettings;
import org.example.common.models.*;
import org.example.common.models.entities.User;
import org.example.server.GameServers.PlayerConnection;
import org.example.utils.auth.JWTUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageHandler {
    private final Map<String, GameSession> gameSessions;
    private final Map<String, PlayerConnection> playerConnections;
    private final LobbyManager lobbyManager;
    private final OnlinePlayersManager onlinePlayersManager;
    private final Gson gson;
    private final ServerConfig config;

    public MessageHandler() {
        this.gameSessions = new ConcurrentHashMap<>();
        this.playerConnections = new ConcurrentHashMap<>();
        this.lobbyManager = LobbyManager.getInstance();
        this.onlinePlayersManager = OnlinePlayersManager.getInstance();
        this.gson = new Gson();
        this.config = ServerConfig.getInstance();
    }

    public void addPlayerConnection(String username, PlayerConnection connection) {
        playerConnections.put(username, connection);
        onlinePlayersManager.playerConnected(username, connection);
        System.out.println("Added player connection: " + username);
    }

    public void removePlayerConnection(String username) {
        PlayerConnection connection = playerConnections.remove(username);
        if (connection != null) {
            // Remove player from lobby
            lobbyManager.leaveLobby(username);

            // Remove player from online players
            onlinePlayersManager.playerDisconnected(username);

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
            // Game session messages
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

            // Lobby messages
            case CREATE_LOBBY:
                handleCreateLobby(connection, message);
                break;
            case JOIN_LOBBY:
                handleJoinLobby(connection, message);
                break;
            case LEAVE_LOBBY:
                handleLeaveLobby(connection, message);
                break;
            case LIST_LOBBIES:
                handleListLobbies(connection, message);
                break;
            case SEARCH_LOBBY:
                handleSearchLobby(connection, message);
                break;
            case START_LOBBY_GAME:
                handleStartLobbyGame(connection, message);
                break;
            case PLAYER_READY:
                handlePlayerReady(connection, message);
                break;

            // Online players messages
            case REQUEST_PLAYERS_LIST:
                handleRequestPlayersList(connection, message);
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

    // =====================
    // LOBBY MESSAGE HANDLERS
    // =====================

    private void handleCreateLobby(PlayerConnection connection, Message message) {
        User user = connection.getUser();
        if (user == null) {
            sendErrorMessage(connection, "User not authenticated");
            return;
        }

        String lobbyName = message.getFromBody("lobbyName");
        Boolean isPrivate = message.getFromBody("isPrivate");
        Boolean isVisible = message.getFromBody("isVisible");
        String password = message.getFromBody("password");

        if (lobbyName == null || lobbyName.trim().isEmpty()) {
            sendErrorMessage(connection, "Lobby name is required");
            return;
        }

        // Set defaults if null
        isPrivate = isPrivate != null ? isPrivate : false;
        isVisible = isVisible != null ? isVisible : true;

        // Create lobby settings
        LobbySettings settings = new LobbySettings(isPrivate, isVisible, password);

        try {
            Lobby lobby = lobbyManager.createLobby(lobbyName, user.getUsername(), settings);

            // Update online players manager (creator is automatically in lobby)
            onlinePlayersManager.playerJoinedLobby(user.getUsername(), lobby.getId(), lobby.getName());

            // Send success response
            Message response = new Message();
            response.setType(Message.Type.SUCCESS);
            response.putInBody("message", "Lobby created successfully");
            response.putInBody("lobby", lobby);
            connection.sendMessage(response);

            // Update lobby activity
            lobbyManager.updateLobbyActivity(lobby.getId());

            System.out.println("Lobby created: " + lobby.getId() + " by " + user.getUsername());
        } catch (Exception e) {
            System.err.println("Failed to create lobby: " + e.getMessage());
            sendErrorMessage(connection, "Failed to create lobby");
        }
    }

    private void handleJoinLobby(PlayerConnection connection, Message message) {
        User user = connection.getUser();
        if (user == null) {
            sendErrorMessage(connection, "User not authenticated");
            return;
        }

        String lobbyId = message.getFromBody("lobbyId");
        String password = message.getFromBody("password");

        if (lobbyId == null || lobbyId.trim().isEmpty()) {
            sendErrorMessage(connection, "Lobby ID is required");
            return;
        }

        try {
            boolean success = lobbyManager.joinLobby(lobbyId, user.getUsername(), password);
            if (success) {
                Lobby lobby = lobbyManager.getLobbyById(lobbyId);

                // Update online players manager
                onlinePlayersManager.playerJoinedLobby(user.getUsername(), lobby.getId(), lobby.getName());

                // Send success response
                Message response = new Message();
                response.setType(Message.Type.SUCCESS);
                response.putInBody("message", "Joined lobby successfully");
                response.putInBody("lobby", lobby);
                connection.sendMessage(response);

                // Broadcast to all lobby members
                broadcastLobbyUpdate(lobby);

                System.out.println("Player " + user.getUsername() + " joined lobby " + lobbyId);
            } else {
                Lobby lobby = lobbyManager.getLobbyById(lobbyId);
                String reason = "Failed to join lobby";

                if (lobby == null) {
                    reason = "Lobby not found";
                } else if (!lobby.canJoin()) {
                    reason = "Lobby is full or not accepting players";
                } else if (lobby.getSettings().requiresPassword()) {
                    reason = "Invalid password";
                }

                sendErrorMessage(connection, reason);
            }
        } catch (Exception e) {
            System.err.println("Failed to join lobby: " + e.getMessage());
            sendErrorMessage(connection, "Failed to join lobby");
        }
    }

    private void handleLeaveLobby(PlayerConnection connection, Message message) {
        User user = connection.getUser();
        if (user == null) {
            sendErrorMessage(connection, "User not authenticated");
            return;
        }

        try {
            Lobby lobby = lobbyManager.getLobbyByPlayerId(user.getUsername());
            if (lobby == null) {
                sendErrorMessage(connection, "Not in any lobby");
                return;
            }

            String lobbyId = lobby.getId();
            boolean success = lobbyManager.leaveLobby(user.getUsername());

            if (success) {
                // Update online players manager
                onlinePlayersManager.playerLeftLobby(user.getUsername());

                // Send success response
                Message response = new Message();
                response.setType(Message.Type.SUCCESS);
                response.putInBody("message", "Left lobby successfully");
                connection.sendMessage(response);

                // Broadcast to remaining lobby members (if any)
                Lobby updatedLobby = lobbyManager.getLobbyById(lobbyId);
                if (updatedLobby != null && !updatedLobby.getPlayers().isEmpty()) {
                    broadcastLobbyUpdate(updatedLobby);
                }

                System.out.println("Player " + user.getUsername() + " left lobby " + lobbyId);
            } else {
                sendErrorMessage(connection, "Failed to leave lobby");
            }
        } catch (Exception e) {
            System.err.println("Failed to leave lobby: " + e.getMessage());
            sendErrorMessage(connection, "Failed to leave lobby");
        }
    }

    private void handleListLobbies(PlayerConnection connection, Message message) {
        User user = connection.getUser();
        if (user == null) {
            sendErrorMessage(connection, "User not authenticated");
            return;
        }

        try {
            List<Lobby> visibleLobbies = lobbyManager.getVisibleLobbies();

            // Send lobby list response
            Message response = new Message();
            response.setType(Message.Type.SUCCESS);
            response.putInBody("message", "Lobby list retrieved");
            response.putInBody("lobbies", visibleLobbies);
            connection.sendMessage(response);

            System.out.println("Sent lobby list to " + user.getUsername() + " (" + visibleLobbies.size() + " lobbies)");
        } catch (Exception e) {
            System.err.println("Failed to get lobby list: " + e.getMessage());
            sendErrorMessage(connection, "Failed to retrieve lobby list");
        }
    }

    private void handleSearchLobby(PlayerConnection connection, Message message) {
        User user = connection.getUser();
        if (user == null) {
            sendErrorMessage(connection, "User not authenticated");
            return;
        }

        String searchTerm = message.getFromBody("searchTerm");
        String lobbyId = message.getFromBody("lobbyId");

        try {
            List<Lobby> foundLobbies = new ArrayList<>();

            if (lobbyId != null && !lobbyId.trim().isEmpty()) {
                // Search by lobby ID (for invisible lobbies)
                Lobby lobby = lobbyManager.getLobbyById(lobbyId.trim());
                if (lobby != null) {
                    foundLobbies.add(lobby);
                }
            } else if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                // Search by name
                foundLobbies = lobbyManager.searchLobbiesByName(searchTerm.trim());
            } else {
                // No search criteria, return visible lobbies
                foundLobbies = lobbyManager.getVisibleLobbies();
            }

            // Send search results
            Message response = new Message();
            response.setType(Message.Type.SUCCESS);
            response.putInBody("message", "Search completed");
            response.putInBody("lobbies", foundLobbies);
            connection.sendMessage(response);

            System.out.println("Search results for " + user.getUsername() + ": " + foundLobbies.size() + " lobbies");
        } catch (Exception e) {
            System.err.println("Failed to search lobbies: " + e.getMessage());
            sendErrorMessage(connection, "Failed to search lobbies");
        }
    }

    private void handleStartLobbyGame(PlayerConnection connection, Message message) {
        User user = connection.getUser();
        if (user == null) {
            sendErrorMessage(connection, "User not authenticated");
            return;
        }

        try {
            Lobby lobby = lobbyManager.getLobbyByPlayerId(user.getUsername());
            if (lobby == null) {
                sendErrorMessage(connection, "Not in any lobby");
                return;
            }

            boolean canStart = lobbyManager.canStartGame(lobby.getId(), user.getUsername());
            if (!canStart) {
                String reason = "Cannot start game";
                if (!lobby.isAdmin(user.getUsername())) {
                    reason = "Only lobby admin can start the game";
                } else if (lobby.getPlayers().size() < 2) {
                    reason = "Need at least 2 players to start game";
                }
                sendErrorMessage(connection, reason);
                return;
            }

            boolean success = lobbyManager.startGame(lobby.getId(), user.getUsername());
            if (success) {
                // Create game session from lobby
                GameSession gameSession = createGameSessionFromLobby(lobby);
                if (gameSession != null) {
                    // Update online players manager for all lobby players
                    for (LobbyPlayer lobbyPlayer : lobby.getPlayers()) {
                        onlinePlayersManager.playerInGame(lobbyPlayer.getId(), gameSession.getSessionId());
                    }

                    // Send success to all lobby members
                    broadcastGameStarted(lobby, gameSession);
                    System.out.println("Game started in lobby " + lobby.getId());
                } else {
                    sendErrorMessage(connection, "Failed to create game session");
                }
            } else {
                sendErrorMessage(connection, "Failed to start game");
            }
        } catch (Exception e) {
            System.err.println("Failed to start lobby game: " + e.getMessage());
            sendErrorMessage(connection, "Failed to start game");
        }
    }

    private void handlePlayerReady(PlayerConnection connection, Message message) {
        User user = connection.getUser();
        if (user == null) {
            sendErrorMessage(connection, "User not authenticated");
            return;
        }

        Boolean ready = message.getFromBody("ready");
        if (ready == null) {
            ready = true; // Default to ready
        }

        try {
            boolean success = lobbyManager.setPlayerReady(user.getUsername(), ready);
            if (success) {
                Lobby lobby = lobbyManager.getLobbyByPlayerId(user.getUsername());

                // Send success response
                Message response = new Message();
                response.setType(Message.Type.SUCCESS);
                response.putInBody("message", ready ? "Player ready" : "Player not ready");
                response.putInBody("ready", ready);
                connection.sendMessage(response);

                // Broadcast to all lobby members
                broadcastLobbyUpdate(lobby);

                System.out.println("Player " + user.getUsername() + " is " + (ready ? "ready" : "not ready"));
            } else {
                sendErrorMessage(connection, "Failed to update ready status");
            }
        } catch (Exception e) {
            System.err.println("Failed to update player ready status: " + e.getMessage());
            sendErrorMessage(connection, "Failed to update ready status");
        }
    }

    // =====================
    // ONLINE PLAYERS HANDLERS
    // =====================

    private void handleRequestPlayersList(PlayerConnection connection, Message message) {
        User user = connection.getUser();
        if (user == null) {
            sendErrorMessage(connection, "User not authenticated");
            return;
        }

        try {
            onlinePlayersManager.sendPlayerListTo(user.getUsername());
            System.out.println("Sent online players list to " + user.getUsername());
        } catch (Exception e) {
            System.err.println("Failed to send online players list: " + e.getMessage());
            sendErrorMessage(connection, "Failed to get online players list");
        }
    }

    // =====================
    // HELPER METHODS
    // =====================

    private void broadcastLobbyUpdate(Lobby lobby) {
        if (lobby == null) return;

        Message updateMessage = new Message();
        updateMessage.setType(Message.Type.SUCCESS);
        updateMessage.putInBody("message", "Lobby updated");
        updateMessage.putInBody("lobby", lobby);

        // Send to all players in the lobby
        for (LobbyPlayer player : lobby.getPlayers()) {
            PlayerConnection connection = playerConnections.get(player.getId());
            if (connection != null) {
                connection.sendMessage(updateMessage);
            }
        }
    }

    private void broadcastGameStarted(Lobby lobby, GameSession gameSession) {
        if (lobby == null || gameSession == null) return;

        Message startMessage = new Message();
        startMessage.setType(Message.Type.SUCCESS);
        startMessage.putInBody("message", "Game starting");
        startMessage.putInBody("gameSessionId", gameSession.getSessionId());

        // Send to all players in the lobby
        for (LobbyPlayer lobbyPlayer : lobby.getPlayers()) {
            PlayerConnection connection = playerConnections.get(lobbyPlayer.getId());
            if (connection != null) {
                connection.sendMessage(startMessage);
            }
        }
    }

    private GameSession createGameSessionFromLobby(Lobby lobby) {
        try {
            // Get first player as creator
            if (lobby.getPlayers().isEmpty()) {
                return null;
            }

            LobbyPlayer admin = lobby.getPlayers().stream()
                    .filter(LobbyPlayer::isAdmin)
                    .findFirst()
                    .orElse(lobby.getPlayers().get(0));

            User adminUser = App.getUser(admin.getId());
            if (adminUser == null) {
                return null;
            }

            // Create game session
            GameSession gameSession = new GameSession(adminUser);
            gameSessions.put(gameSession.getSessionId(), gameSession);

            // Add all lobby players to game session
            for (LobbyPlayer lobbyPlayer : lobby.getPlayers()) {
                User user = App.getUser(lobbyPlayer.getId());
                PlayerConnection connection = playerConnections.get(lobbyPlayer.getId());
                if (user != null && connection != null) {
                    gameSession.addPlayer(connection, user);
                }
            }

            return gameSession;
        } catch (Exception e) {
            System.err.println("Failed to create game session from lobby: " + e.getMessage());
            return null;
        }
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
