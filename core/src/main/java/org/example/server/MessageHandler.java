package org.example.server;

import org.example.client.Main;
import org.example.client.views.GameView;
import org.example.common.Lobby.Lobby;
import org.example.common.Lobby.LobbyPlayer;
import org.example.common.Lobby.LobbySettings;
import org.example.common.models.*;
import org.example.common.models.entities.User;
import org.example.common.models.Player.Player;
import org.example.server.GameServers.PlayerConnection;
import org.example.utils.auth.JWTUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
        
        // Register player with ChatManager
        ChatManager chatManager = ChatManager.getInstance(this);
        chatManager.registerPlayer(username, connection);
        
// System.out.println("DEBUG: Added player connection: " + username);
// System.out.println("DEBUG: Current player connections: " + playerConnections.keySet());
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
            
            // Unregister player from ChatManager
            ChatManager chatManager = ChatManager.getInstance(this);
            chatManager.unregisterPlayer(username);
            
// System.out.println("Removed player connection: " + username);
        }
    }

    public void processMessage(String username, Message message) {
// System.out.println("DEBUG: Processing message from " + username + " with type: " + message.getType());
// System.out.println("DEBUG: Available player connections: " + playerConnections.keySet());
        PlayerConnection connection = playerConnections.get(username);
        if (connection == null) {
            System.err.println("No connection found for user: " + username);
            return;
        }

        // Log movement messages specifically
        if (message.getType() == Message.Type.PLAYER_MOVE) {
            float x = message.getFloatFromBody("x");
            float y = message.getFloatFromBody("y");
// System.out.println("🚀 SERVER: Received PLAYER_MOVE from " + username + " - Position: (" + x + ", " + y + ")");
        }

        switch (message.getType()) {
            // Authentication messages
            case AUTH_LOGIN:
// System.out.println("DEBUG: Handling AUTH_LOGIN message");
                handleAuthentication(connection, message);
                break;

            // Game session messages
            case CREATE_GAME:
// System.out.println("DEBUG: Handling CREATE_GAME message");
                handleCreateGame(connection, message);
                break;
            case JOIN_GAME:
                handleJoinGame(connection, message);
                break;
            case LEAVE_GAME:
                handleLeaveGame(connection, message);
                break;
            case REJOIN_GAME:
                handleRejoinGame(connection, message);
                break;

            // Lobby messages
            case CREATE_LOBBY:
// System.out.println("DEBUG: Handling CREATE_LOBBY message");
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

            // Reaction system messages
            case REACTION_SEND:
                handleReactionSend(connection, message);
                break;

            // Chat messages
            case CHAT:
                handleChat(connection, message);
                break;
            case CHAT_PRIVATE:
                handlePrivateChat(connection, message);
                break;
            case CHAT_PUBLIC:
                handlePublicChat(connection, message);
                break;
            case CHAT_ROOM_CREATE:
                handleCreateChatRoom(connection, message);
                break;
            case CHAT_ROOM_JOIN:
                handleJoinChatRoom(connection, message);
                break;
            case CHAT_ROOM_LEAVE:
                handleLeaveChatRoom(connection, message);
                break;
            case CHAT_HISTORY_REQUEST:
                handleChatHistoryRequest(connection, message);
                break;

            default:
                // Forward to game session if player is in one
                Object gameSessionObj = connection.getGameSession();
                if (gameSessionObj instanceof GameSession) {
                    GameSession gameSession = (GameSession) gameSessionObj;
// System.out.println("DEBUG: Forwarding message to game session: " + message.getType());
                    gameSession.processMessage(username, message);
                } else {
// System.err.println("DEBUG: Player not in a game session, cannot process: " + message.getType());
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

// System.out.println("Game session created: " + gameSession.getSessionId() + " by " + user.getUsername());
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

// System.out.println("Player " + user.getUsername() + " joined game session: " + gameSessionId);
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
// System.out.println("Removed empty game session: " + gameSession.getSessionId());
            }
        } else {
            sendErrorMessage(connection, "Player not in a game session");
        }
    }

    private void handleRejoinGame(PlayerConnection connection, Message message) {
        User user = connection.getUser();
        if (user == null) {
            sendErrorMessage(connection, "User not authenticated");
            return;
        }

        String gameSessionId = message.getFromBody("gameSessionId");
        String username = message.getFromBody("username");

        if (gameSessionId == null) {
            sendErrorMessage(connection, "Game session ID required for rejoin");
            return;
        }

// System.out.println("🔄 SERVER: Player " + username + " attempting to rejoin game session: " + gameSessionId);

        GameSession gameSession = gameSessions.get(gameSessionId);
        if (gameSession == null) {
            sendErrorMessage(connection, "Game session not found");
            return;
        }

        // Check if the game session is still active
        if (!gameSession.isActive()) {
            sendErrorMessage(connection, "Game session is no longer active");
            return;
        }

        // Check if player was previously in this game session
        if (!gameSession.hasPlayer(username)) {
            sendErrorMessage(connection, "Player was not in this game session");
            return;
        }

        // Re-add player to the game session
        if (gameSession.addPlayer(connection, user)) {
            // Send success response with current game state
            Message response = new Message();
            response.setType(Message.Type.SUCCESS);
            response.putInBody("message", "Successfully rejoined game");
            response.putInBody("gameSessionId", gameSessionId);
            response.putInBody("playerCount", gameSession.getPlayerCount());

            // Send current game state
            response.putInBody("gameData", gameSession.getGameInstance().getGameState());
            response.putInBody("playersData", gameSession.getGameInstance().getPlayersData());

            connection.sendMessage(response);

// System.out.println("✅ SERVER: Player " + username + " successfully rejoined game session: " + gameSessionId);

            // Notify other players about the rejoin
            gameSession.broadcastPlayerRejoined(username);
        } else {
            sendErrorMessage(connection, "Failed to rejoin game session");
        }
    }

    private void handleAuthentication(PlayerConnection connection, Message message) {
        String token = message.getFromBody("token");
        String username = message.getFromBody("username");

// System.out.println("DEBUG: Authentication attempt - username: " + username + ", token: " + (token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null"));

        if (token == null || username == null) {
// System.out.println("DEBUG: Authentication failed - missing token or username");
            sendErrorMessage(connection, "Token and username required");
            return;
        }

        // For testing purposes, accept temp tokens
        if (token.startsWith("temp_token_")) {
// System.out.println("DEBUG: Processing temp token authentication for user: " + username);
            // Create a basic user for testing
            User user = new User();
            user.setUsername(username);
            connection.setUser(user);
            connection.setState(PlayerConnection.ConnectionState.AUTHENTICATED);
            addPlayerConnection(username, connection);

            // Send success response
            Message response = new Message();
            response.setType(Message.Type.SUCCESS);
            response.putInBody("message", "Authentication successful");
            response.putInBody("username", username);
            connection.sendMessage(response);

// System.out.println("Player " + username + " authenticated successfully with temp token");
            return;
        }

        // Validate JWT token properly
// System.out.println("DEBUG: Validating JWT token for user: " + username);
        String tokenStatus = JWTUtils.getTokenStatus(token);
// System.out.println("DEBUG: Token status: " + tokenStatus + " - " + JWTUtils.getTokenStatusMessage(tokenStatus));
        if (!tokenStatus.equals(JWTUtils.TOKEN_VALID)) {
// System.out.println("DEBUG: JWT token validation failed for user: " + username);
            sendErrorMessage(connection, "Invalid or expired token: " + JWTUtils.getTokenStatusMessage(tokenStatus));
            return;
        }

        // Extract username from token to verify it matches
        String tokenUsername = JWTUtils.extractUsername(token);
        if (tokenUsername == null || !tokenUsername.equals(username)) {
            sendErrorMessage(connection, "Token username mismatch");
            return;
        }

        // For server-side testing, create user on-the-fly if not found
        User user = App.getUser(username);
// System.out.println("DEBUG: Looking up user in App: " + username + ", found: " + (user != null));
        if (user == null) {
// System.out.println("DEBUG: User not found in App, creating temporary user for: " + username);
            // Create a basic user for testing
            user = new User();
            user.setUsername(username);
            user.setEmail(username + "@test.com");
            user.setNickname(username);
            user.setGender(org.example.common.models.enums.PlayerEnums.Gender.Male);
            // Add to App for future lookups
            App.addUser(user);
// System.out.println("DEBUG: Added user to App: " + username);
// System.out.println("DEBUG: App users count: " + App.getUsers().size());
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

// System.out.println("DEBUG: JWT authentication successful for user: " + username);
// System.out.println("Player " + username + " authenticated successfully");
    }

    private void sendErrorMessage(PlayerConnection connection, String errorMessage) {
        Message error = new Message();
        error.setType(Message.Type.ERROR);
        error.putInBody("message", errorMessage);
        error.putInBody("timestamp", System.currentTimeMillis());

// System.out.println("DEBUG: Sending error message: " + errorMessage + " to " + connection.getUsername());

        try {
            connection.sendMessage(error);
// System.out.println("DEBUG: Error message sent successfully");
        } catch (Exception e) {
// System.err.println("DEBUG: Failed to send error message: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void handleCreateLobby(PlayerConnection connection, Message message) {
// System.out.println("DEBUG: handleCreateLobby() called");
        User user = connection.getUser();
        if (user == null) {
// System.out.println("DEBUG: User not authenticated in handleCreateLobby");
            sendErrorMessage(connection, "User not authenticated");
            return;
        }

// System.out.println("DEBUG: User authenticated: " + user.getUsername());
        String lobbyName = message.getFromBody("lobbyName");
        Boolean isPrivate = message.getFromBody("isPrivate");
        Boolean isVisible = message.getFromBody("isVisible");
        String password = message.getFromBody("password");

// System.out.println("DEBUG: Lobby parameters - name: " + lobbyName + ", isPrivate: " + isPrivate + ", isVisible: " + isVisible);

        if (lobbyName == null || lobbyName.trim().isEmpty()) {
// System.out.println("DEBUG: Lobby name is empty or null");
            sendErrorMessage(connection, "Lobby name is required");
            return;
        }

        // Set defaults if null
        isPrivate = isPrivate != null ? isPrivate : false;
        isVisible = isVisible != null ? isVisible : true;

        // Create lobby settings
        LobbySettings settings = new LobbySettings(isPrivate, isVisible, password);

        try {
// System.out.println("DEBUG: About to call lobbyManager.createLobby()");
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

// System.out.println("DEBUG: Lobby created successfully: " + lobby.getId() + " by " + user.getUsername());
        } catch (Exception e) {
// System.err.println("DEBUG: Failed to create lobby: " + e.getMessage());
            e.printStackTrace();
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

// System.out.println("Player " + user.getUsername() + " joined lobby " + lobbyId);
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
// System.out.println("DEBUG: Player left lobby, checking for remaining players");
// System.out.println("DEBUG: Updated lobby: " + (updatedLobby != null ? "found" : "null"));
                if (updatedLobby != null) {
// System.out.println("DEBUG: Remaining players: " + updatedLobby.getPlayers().size());
// System.out.println("DEBUG: Current admin: " + updatedLobby.getAdminId());
                }

                if (updatedLobby != null && !updatedLobby.getPlayers().isEmpty()) {
// System.out.println("DEBUG: Broadcasting lobby update to remaining players");
                    broadcastLobbyUpdate(updatedLobby);
                } else {
// System.out.println("DEBUG: No remaining players, not broadcasting update");
                }

// System.out.println("Player " + user.getUsername() + " left lobby " + lobbyId);
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

// System.out.println("Sent lobby list to " + user.getUsername() + " (" + visibleLobbies.size() + " lobbies)");
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

// System.out.println("Search results for " + user.getUsername() + ": " + foundLobbies.size() + " lobbies");
        } catch (Exception e) {
            System.err.println("Failed to search lobbies: " + e.getMessage());
            sendErrorMessage(connection, "Failed to search lobbies");
        }
    }

    private void handleStartLobbyGame(PlayerConnection connection, Message message) {
        User user = connection.getUser();
// System.out.println("DEBUG: handleStartLobbyGame - connection user: " + (user != null ? user.getUsername() : "null"));
// System.out.println("DEBUG: handleStartLobbyGame - connection state: " + connection.getState());
// System.out.println("DEBUG: handleStartLobbyGame - available player connections: " + playerConnections.keySet());

        // Ensure connection is properly authenticated
        if (connection.getState() != PlayerConnection.ConnectionState.AUTHENTICATED &&
            connection.getState() != PlayerConnection.ConnectionState.IN_GAME) {
// System.out.println("DEBUG: Connection state invalid: " + connection.getState());
            sendErrorMessage(connection, "Invalid connection state");
            return;
        }

        if (user == null) {
// System.out.println("DEBUG: User is null but connection state is: " + connection.getState());
            // Re-authenticate if we have the username but lost the user object
            if (connection.getUsername() != null) {
                user = new User();
                user.setUsername(connection.getUsername());
                connection.setUser(user);
// System.out.println("DEBUG: Re-authenticated user: " + user.getUsername());
            } else {
                sendErrorMessage(connection, "User not authenticated");
                return;
            }
        }

        try {
// System.out.println("DEBUG: handleStartLobbyGame called for user: " + user.getUsername());

            Lobby lobby = lobbyManager.getLobbyByPlayerId(user.getUsername());
            if (lobby == null) {
// System.err.println("DEBUG: User not in any lobby");
                sendErrorMessage(connection, "Not in any lobby");
                return;
            }

// System.out.println("DEBUG: Found lobby: " + lobby.getId() + " with " + lobby.getPlayers().size() + " players");
// System.out.println("DEBUG: Lobby players: " + lobby.getPlayers().stream().map(p -> p.getId()).toList());

            boolean canStart = lobbyManager.canStartGame(lobby.getId(), user.getUsername());
// System.out.println("DEBUG: canStartGame returned: " + canStart);

            if (!canStart) {
                String reason = "Cannot start game";
                if (!lobby.isAdmin(user.getUsername())) {
                    reason = "Only lobby admin can start the game";
// System.out.println("DEBUG: User is not admin");
                } else if (lobby.getPlayers().size() < 2) {
                    reason = "Need at least 2 players to start game";
// System.out.println("DEBUG: Not enough players. Current count: " + lobby.getPlayers().size());
                }
// System.out.println("DEBUG: Sending error: " + reason);
                sendErrorMessage(connection, reason);
                return;
            }

// System.out.println("DEBUG: Starting game in lobby...");
            boolean success = lobbyManager.startGame(lobby.getId(), user.getUsername());
// System.out.println("DEBUG: lobbyManager.startGame returned: " + success);

            if (success) {
                // Create game session from lobby
// System.out.println("DEBUG: Creating game session...");
                GameSession gameSession = createGameSessionFromLobby(lobby);
                if (gameSession != null) {
// System.out.println("DEBUG: Game session created successfully");
                    // Update online players manager for all lobby players
                    for (LobbyPlayer lobbyPlayer : lobby.getPlayers()) {
                        onlinePlayersManager.playerInGame(lobbyPlayer.getId(), gameSession.getSessionId());
                    }

                    // Send success to all lobby members
                    broadcastGameStarted(lobby, gameSession);
// System.out.println("Game started in lobby " + lobby.getId());
                } else {
// System.err.println("DEBUG: Failed to create game session");
                    sendErrorMessage(connection, "Failed to create game session");
                }
            } else {
// System.err.println("DEBUG: lobbyManager.startGame failed");
                sendErrorMessage(connection, "Failed to start game");
            }
        } catch (Exception e) {
// System.err.println("DEBUG: Exception in handleStartLobbyGame: " + e.getMessage());
            e.printStackTrace();
            sendErrorMessage(connection, "Failed to start game: " + e.getMessage());
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

// System.out.println("Player " + user.getUsername() + " is " + (ready ? "ready" : "not ready"));
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
// System.out.println("Sent online players list to " + user.getUsername());
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

        // System.out.println("DEBUG: Broadcasting lobby update for lobby: " + lobby.getId());
        // System.out.println("DEBUG: Lobby players: " + lobby.getPlayers().size());
        // System.out.println("DEBUG: Admin ID: " + lobby.getAdminId());

        Message updateMessage = new Message();
        updateMessage.setType(Message.Type.SUCCESS);
        updateMessage.putInBody("message", "Lobby updated");
        updateMessage.putInBody("lobby", lobby);

        // Send to all players in the lobby
        for (LobbyPlayer player : lobby.getPlayers()) {
            // System.out.println("DEBUG: Sending lobby update to player: " + player.getId() + " (Admin: " + player.isAdmin() + ")");
            PlayerConnection connection = playerConnections.get(player.getId());
            if (connection != null) {
                connection.sendMessage(updateMessage);
                // System.out.println("DEBUG: Lobby update sent to " + player.getId());
            } else {
// System.err.println("DEBUG: No connection found for player: " + player.getId());
            }
        }
    }

    private void broadcastGameStarted(Lobby lobby, GameSession gameSession) {
        if (lobby == null || gameSession == null) return;

        // Create comprehensive game start message with all necessary data
        Message startMessage = new Message();
        startMessage.setType(Message.Type.START_GAME);
        startMessage.putInBody("message", "Game started successfully");
        startMessage.putInBody("gameSessionId", gameSession.getSessionId());

        // Add farm selection phase information
        startMessage.putInBody("inFarmSelectionPhase", true);
        startMessage.putInBody("availableFarms", gameSession.getGameInstance().getAvailableFarmIndices());
        startMessage.putInBody("playerSelections", gameSession.getGameInstance().getPlayerFarmSelections());

        // Add game instance data
        startMessage.putInBody("gameData", gameSession.getGameInstance().getGameState());
        startMessage.putInBody("playersData", gameSession.getGameInstance().getPlayersData());

        // Don't send a specific current player username - each client will set their own
        startMessage.putInBody("currentPlayerUsername", null);
        startMessage.putInBody("playerCount", gameSession.getPlayerCount());
        startMessage.putInBody("isActive", false); // Not fully active until farm selection is complete

        // Send to all players in the lobby
        for (LobbyPlayer lobbyPlayer : lobby.getPlayers()) {
            PlayerConnection connection = playerConnections.get(lobbyPlayer.getId());
            if (connection != null) {
                connection.sendMessage(startMessage);
// System.out.println("DEBUG: Sent game start message to " + lobbyPlayer.getId() + " with session ID: " + gameSession.getSessionId());
            }
        }

// System.out.println("DEBUG: Broadcasted game start to " + lobby.getPlayers().size() + " players - Farm selection phase");
    }

    private GameSession createGameSessionFromLobby(Lobby lobby) {
        try {
// System.out.println("DEBUG: createGameSessionFromLobby called for lobby: " + lobby.getId());
// System.out.println("DEBUG: Lobby players count: " + lobby.getPlayers().size());

            // Get first player as creator
            if (lobby.getPlayers().isEmpty()) {
// System.err.println("DEBUG: Lobby has no players");
                return null;
            }

            LobbyPlayer admin = lobby.getPlayers().stream()
                    .filter(LobbyPlayer::isAdmin)
                    .findFirst()
                    .orElse(lobby.getPlayers().get(0));

// System.out.println("DEBUG: Admin player: " + admin.getId() + " (isAdmin: " + admin.isAdmin() + ")");

            // Get admin user from player connections instead of App
            PlayerConnection adminConnection = playerConnections.get(admin.getId());
// System.out.println("DEBUG: Looking for admin connection with key: " + admin.getId());
// System.out.println("DEBUG: Available player connections: " + playerConnections.keySet());
// System.out.println("DEBUG: Admin connection found: " + (adminConnection != null));
            if (adminConnection != null) {
// System.out.println("DEBUG: Admin connection user: " + (adminConnection.getUser() != null ? adminConnection.getUser().getUsername() : "null"));
            }

            // Also try to get user from App as fallback
            User appUser = App.getUser(admin.getId());
// System.out.println("DEBUG: Looking up admin user in App: " + admin.getId() + ", found: " + (appUser != null));

            User adminUser;
            if (adminConnection == null || adminConnection.getUser() == null) {
// System.err.println("DEBUG: Admin connection or user not found for: " + admin.getId());
// System.err.println("DEBUG: Available player connections: " + playerConnections.keySet());

                // Create a new user object from the lobby player info
                adminUser = new User();
                adminUser.setUsername(admin.getId());
// System.out.println("DEBUG: Created new user for admin: " + adminUser.getUsername());
            } else {
                adminUser = adminConnection.getUser();
            }

            // System.out.println("DEBUG: Admin user found: " + adminUser.getUsername());

            // Create game session
// System.out.println("DEBUG: Creating GameSession...");
            GameSession gameSession = new GameSession(adminUser);
            gameSessions.put(gameSession.getSessionId(), gameSession);
// System.out.println("DEBUG: GameSession created with ID: " + gameSession.getSessionId());

            // First, update the admin's connection in the game session
            if (adminConnection != null) {
// System.out.println("DEBUG: Updating admin's connection in game session");
                gameSession.addPlayer(adminConnection, adminUser);
            }

            // Add all lobby players to game session
// System.out.println("DEBUG: Adding players to game session...");
            for (LobbyPlayer lobbyPlayer : lobby.getPlayers()) {
                // Skip admin since we already added them
                if (lobbyPlayer.getId().equals(adminUser.getUsername())) {
                    continue;
                }

// System.out.println("DEBUG: Processing lobby player: " + lobbyPlayer.getId());
                PlayerConnection connection = playerConnections.get(lobbyPlayer.getId());

                // Try to find connection by username if not found by ID
                if (connection == null) {
// System.out.println("DEBUG: Connection not found by ID, trying to find by username...");
                    for (Map.Entry<String, PlayerConnection> entry : playerConnections.entrySet()) {
                        if (entry.getValue().getUser() != null &&
                            entry.getValue().getUser().getUsername().equals(lobbyPlayer.getId())) {
                            connection = entry.getValue();
// System.out.println("DEBUG: Found connection by username: " + lobbyPlayer.getId());
                            break;
                        }
                    }
                }

                if (connection != null && connection.getUser() != null) {
                    User user = connection.getUser();
// System.out.println("DEBUG: Adding player " + user.getUsername() + " to game session");
                    boolean success = gameSession.addPlayer(connection, user);
                    if (success) {
// System.out.println("DEBUG: Successfully added player " + user.getUsername() + " to game session " + gameSession.getSessionId());
                    } else {
// System.err.println("DEBUG: Failed to add player " + user.getUsername() + " to game session");
                    }
                } else {
// System.err.println("DEBUG: Connection or user not found for lobby player: " + lobbyPlayer.getId());
// System.err.println("DEBUG: Connection: " + (connection != null ? "found" : "null"));
// System.err.println("DEBUG: User: " + (connection != null && connection.getUser() != null ? connection.getUser().getUsername() : "null"));

                    // Create a fallback user if connection is not found
                    if (connection == null) {
// System.out.println("DEBUG: Creating fallback user for lobby player: " + lobbyPlayer.getId());
                        User fallbackUser = new User();
                        fallbackUser.setUsername(lobbyPlayer.getId());

                        // Try to add player to game session without connection
                        Player newPlayer = new Player(fallbackUser);
                        boolean addedToGame = gameSession.getGameInstance().addPlayer(newPlayer);
                        if (addedToGame) {
// System.out.println("DEBUG: Successfully added fallback player " + fallbackUser.getUsername() + " to game instance");
                        } else {
// System.err.println("DEBUG: Failed to add fallback player " + fallbackUser.getUsername() + " to game instance");
                        }
                    }
                }
            }

            // System.out.println("DEBUG: Final game session player count: " + gameSession.getPlayerCount());
            return gameSession;
        } catch (Exception e) {
// System.err.println("DEBUG: Failed to create game session from lobby: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, GameSession> getGameSessions() {
        return gameSessions;
    }

    public Map<String, PlayerConnection> getPlayerConnections() {
        return playerConnections;
    }

    public Set<String> getPlayerConnectionUsernames() {
        return playerConnections.keySet();
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

// System.out.println("MessageHandler shutdown completed");
    }

    private void handleReactionSend(PlayerConnection connection, Message message) {
        User user = connection.getUser();
        if (user == null) {
            sendErrorMessage(connection, "User not authenticated");
            return;
        }

        String reaction = message.getFromBody("reaction");
        String fromPlayer = message.getFromBody("fromPlayer");
        String toPlayer = message.getFromBody("toPlayer");

        if (reaction == null || fromPlayer == null) {
            sendErrorMessage(connection, "Invalid reaction message");
            return;
        }

// System.out.println("Reaction: " + fromPlayer + " sent reaction: " + reaction);

        // Create reaction receive message to broadcast
        Message reactionMessage = new Message();
        reactionMessage.setType(Message.Type.REACTION_RECEIVE);
        reactionMessage.putInBody("reaction", reaction);
        reactionMessage.putInBody("fromPlayer", fromPlayer);
        reactionMessage.putInBody("timestamp", System.currentTimeMillis());

        // Forward to game session for broadcasting
        Object gameSessionObj = connection.getGameSession();
        if (gameSessionObj instanceof GameSession) {
            GameSession gameSession = (GameSession) gameSessionObj;
            gameSession.broadcastToOthers(null, reactionMessage);
        } else {
            sendErrorMessage(connection, "Player not in a game session");
        }
    }

    // Chat-related methods
    public void sendChatMessage(PlayerConnection connection, ChatMessage chatMessage) {
        Message message = new Message();
        message.setType(Message.Type.CHAT);
        message.putInBody("chatMessage", gson.toJson(chatMessage));
        connection.sendMessage(message);
    }

    public void sendPrivateMessage(PlayerConnection connection, ChatMessage chatMessage) {
        Message message = new Message();
        message.setType(Message.Type.CHAT_PRIVATE);
        message.putInBody("chatMessage", gson.toJson(chatMessage));
        connection.sendMessage(message);
    }

    public void sendRoomMessage(PlayerConnection connection, ChatMessage chatMessage) {
        Message message = new Message();
        message.setType(Message.Type.CHAT);
        message.putInBody("chatMessage", gson.toJson(chatMessage));
        message.putInBody("roomId", chatMessage.getRoomId());
        connection.sendMessage(message);
    }

    public void sendRoomHistory(PlayerConnection connection, ChatRoom room) {
        Message message = new Message();
        message.setType(Message.Type.CHAT_HISTORY_REQUEST);
        message.putInBody("roomId", room.getRoomId());
        message.putInBody("roomName", room.getRoomName());
        message.putInBody("messages", gson.toJson(room.getMessageHistory()));
        connection.sendMessage(message);
    }

    public void sendRoomCreatedNotification(PlayerConnection connection, ChatRoom room) {
        Message message = new Message();
        message.setType(Message.Type.CHAT_ROOM_CREATE);
        message.putInBody("roomId", room.getRoomId());
        message.putInBody("roomName", room.getRoomName());
        message.putInBody("owner", room.getOwner());
        connection.sendMessage(message);
    }

    public void sendNotification(PlayerConnection connection, Notification notification) {
        Message message = new Message();
        message.setType(Message.Type.SUCCESS);
        message.putInBody("notification", gson.toJson(notification));
        connection.sendMessage(message);
    }

    // Chat handling methods
    private void handleChat(PlayerConnection connection, Message message) {
        User user = connection.getUser();
        if (user == null) {
            sendErrorMessage(connection, "User not authenticated");
            return;
        }

        String content = message.getFromBody("content");
        String roomId = message.getFromBody("roomId");

        if (content == null || content.trim().isEmpty()) {
            sendErrorMessage(connection, "Message content cannot be empty");
            return;
        }

        ChatManager chatManager = ChatManager.getInstance(this);
        if (roomId != null && !roomId.isEmpty()) {
            chatManager.handleRoomChat(user.getUsername(), roomId, content);
        } else {
            chatManager.handlePublicChat(user.getUsername(), content);
        }
    }

    private void handlePrivateChat(PlayerConnection connection, Message message) {
        User user = connection.getUser();
        if (user == null) {
            sendErrorMessage(connection, "User not authenticated");
            return;
        }

        String content = message.getFromBody("content");
        String recipient = message.getFromBody("recipient");

        if (content == null || content.trim().isEmpty()) {
            sendErrorMessage(connection, "Message content cannot be empty");
            return;
        }

        if (recipient == null || recipient.trim().isEmpty()) {
            sendErrorMessage(connection, "Recipient cannot be empty");
            return;
        }

        ChatManager chatManager = ChatManager.getInstance(this);
        chatManager.handlePrivateChat(user.getUsername(), recipient, content);
    }

    private void handlePublicChat(PlayerConnection connection, Message message) {
        User user = connection.getUser();
        if (user == null) {
            sendErrorMessage(connection, "User not authenticated");
            return;
        }

        String content = message.getFromBody("content");

        if (content == null || content.trim().isEmpty()) {
            sendErrorMessage(connection, "Message content cannot be empty");
            return;
        }

        ChatManager chatManager = ChatManager.getInstance(this);
        chatManager.handlePublicChat(user.getUsername(), content);
    }

    private void handleCreateChatRoom(PlayerConnection connection, Message message) {
        User user = connection.getUser();
        if (user == null) {
            sendErrorMessage(connection, "User not authenticated");
            return;
        }

        String roomName = message.getFromBody("roomName");
        String roomId = message.getFromBody("roomId");

        if (roomName == null || roomName.trim().isEmpty()) {
            sendErrorMessage(connection, "Room name cannot be empty");
            return;
        }

        if (roomId == null || roomId.trim().isEmpty()) {
            roomId = "room_" + System.currentTimeMillis();
        }

        ChatManager chatManager = ChatManager.getInstance(this);
        chatManager.createChatRoom(roomId, roomName, user.getUsername());
    }

    private void handleJoinChatRoom(PlayerConnection connection, Message message) {
        User user = connection.getUser();
        if (user == null) {
            sendErrorMessage(connection, "User not authenticated");
            return;
        }

        String roomId = message.getFromBody("roomId");

        if (roomId == null || roomId.trim().isEmpty()) {
            sendErrorMessage(connection, "Room ID cannot be empty");
            return;
        }

        ChatManager chatManager = ChatManager.getInstance(this);
        chatManager.joinChatRoom(user.getUsername(), roomId);
    }

    private void handleLeaveChatRoom(PlayerConnection connection, Message message) {
        User user = connection.getUser();
        if (user == null) {
            sendErrorMessage(connection, "User not authenticated");
            return;
        }

        String roomId = message.getFromBody("roomId");

        if (roomId == null || roomId.trim().isEmpty()) {
            sendErrorMessage(connection, "Room ID cannot be empty");
            return;
        }

        ChatManager chatManager = ChatManager.getInstance(this);
        chatManager.leaveChatRoom(user.getUsername(), roomId);
    }

    private void handleChatHistoryRequest(PlayerConnection connection, Message message) {
        User user = connection.getUser();
        if (user == null) {
            sendErrorMessage(connection, "User not authenticated");
            return;
        }

        String chatType = message.getFromBody("chatType");
        String target = message.getFromBody("target"); // roomId for room, username for private

        ChatManager chatManager = ChatManager.getInstance(this);

        if ("public".equals(chatType)) {
            List<ChatMessage> history = chatManager.getPublicChatHistory();
            Message response = new Message();
            response.setType(Message.Type.CHAT_HISTORY_REQUEST);
            response.putInBody("chatType", "public");
            response.putInBody("messages", gson.toJson(history));
            connection.sendMessage(response);
        } else if ("private".equals(chatType) && target != null) {
            List<ChatMessage> history = chatManager.getPrivateChatHistory(user.getUsername(), target);
            Message response = new Message();
            response.setType(Message.Type.CHAT_HISTORY_REQUEST);
            response.putInBody("chatType", "private");
            response.putInBody("target", target);
            response.putInBody("messages", gson.toJson(history));
            connection.sendMessage(response);
        } else {
            sendErrorMessage(connection, "Invalid chat history request");
        }
    }
}
