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
import java.util.HashMap;
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

        }
    }

    public void processMessage(String username, Message message) {
        PlayerConnection connection = playerConnections.get(username);
        if (connection == null) {
            System.err.println("No connection found for user: " + username);
            return;
        }

        // Log movement messages specifically
        if (message.getType() == Message.Type.PLAYER_MOVE) {
            float x = message.getFloatFromBody("x");
            float y = message.getFloatFromBody("y");
        }

        switch (message.getType()) {
            // Authentication messages
            case AUTH_LOGIN:
                handleAuthentication(connection, message);
                break;

            // Game session messages
            case CREATE_GAME:
                handleCreateGame(connection, message);
                break;
            case PLAYER_DEBUG_UPDATE:
                // forward to game session if present
                Object gameSessionObjDbg = connection.getGameSession();
                if (gameSessionObjDbg instanceof GameSession) {
                    ((GameSession) gameSessionObjDbg).processMessage(username, message);
                } else {
                    sendErrorMessage(connection, "Player not in a game session");
                }
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

            // Trade messages (log and forward to game session)
            case TRADE_REQUEST:
            case TRADE_RESPONSE:
            case TRADE_ACCEPT:
            case TRADE_DECLINE:
                forwardToGameSession(connection, message);
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

            // Reaction system messages
            case REACTION_SEND:
                handleReactionSend(connection, message);
                break;

            // Voting messages (forward to game session)
            case VOTE_START:
            case VOTE_CAST:
                forwardToGameSession(connection, message);
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
            case TAKE_QUEST:
                handleTakeQuest(connection, message);
            default:
                // Forward to game session if player is in one
                forwardToGameSession(connection, message);
                break;
        }
    }

    private void forwardToGameSession(PlayerConnection connection, Message message) {
        Object gameSessionObj = connection.getGameSession();
        if (gameSessionObj instanceof GameSession) {
            GameSession gameSession = (GameSession) gameSessionObj;
            gameSession.processMessage(connection.getUser().getUsername(), message);
        } else {
            sendErrorMessage(connection, "Player not in a game session");
        }
    }

    private void handleTakeQuest(PlayerConnection connection, Message message) {
        // Forward to game session for processing
        Object gameSessionObj = connection.getGameSession();
        if (gameSessionObj instanceof GameSession) {
            GameSession gameSession = (GameSession) gameSessionObj;
            gameSession.processMessage(connection.getUser().getUsername(), message);
        } else {
            sendErrorMessage(connection, "Player not in a game session");
        }
    }

    private void handleCreateGame(PlayerConnection connection, Message message) {
        User user = connection.getUser();
        if (user == null) {
            sendErrorMessage(connection, "User not authenticated");
            return;
        }

        // Create new game session
        GameSession gameSession = new GameSession(user, this);
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


            // Notify other players about the rejoin
            gameSession.broadcastPlayerRejoined(username);
        } else {
            sendErrorMessage(connection, "Failed to rejoin game session");
        }
    }

    private void handleAuthentication(PlayerConnection connection, Message message) {
        String token = message.getFromBody("token");
        String username = message.getFromBody("username");


        if (token == null || username == null) {
            sendErrorMessage(connection, "Token and username required");
            return;
        }

        // For testing purposes, accept temp tokens
        if (token.startsWith("temp_token_")) {
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

            return;
        }

        // Validate JWT token properly
        String tokenStatus = JWTUtils.getTokenStatus(token);
        if (!tokenStatus.equals(JWTUtils.TOKEN_VALID)) {
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
        if (user == null) {
            // Create a basic user for testing
            user = new User();
            user.setUsername(username);
            user.setEmail(username + "@test.com");
            user.setNickname(username);
            user.setGender(org.example.common.models.enums.PlayerEnums.Gender.Male);
            // Add to App for future lookups
            App.addUser(user);
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

    }

    private void sendErrorMessage(PlayerConnection connection, String errorMessage) {
        Message error = new Message();
        error.setType(Message.Type.ERROR);
        error.putInBody("message", errorMessage);
        error.putInBody("timestamp", System.currentTimeMillis());


        try {
            connection.sendMessage(error);
        } catch (Exception e) {
// System.err.println("DEBUG: Failed to send error message: " + e.getMessage());
            e.printStackTrace();
        }
    }


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
                if (updatedLobby != null) {
                }

                if (updatedLobby != null && !updatedLobby.getPlayers().isEmpty()) {
                    broadcastLobbyUpdate(updatedLobby);
                } else {
                }

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

        } catch (Exception e) {
            System.err.println("Failed to search lobbies: " + e.getMessage());
            sendErrorMessage(connection, "Failed to search lobbies");
        }
    }

    private void handleStartLobbyGame(PlayerConnection connection, Message message) {
        User user = connection.getUser();

        // Ensure connection is properly authenticated
        if (connection.getState() != PlayerConnection.ConnectionState.AUTHENTICATED &&
            connection.getState() != PlayerConnection.ConnectionState.IN_GAME) {
            sendErrorMessage(connection, "Invalid connection state");
            return;
        }

        if (user == null) {
            // Re-authenticate if we have the username but lost the user object
            if (connection.getUsername() != null) {
                user = new User();
                user.setUsername(connection.getUsername());
                connection.setUser(user);
            } else {
                sendErrorMessage(connection, "User not authenticated");
                return;
            }
        }

        try {

            Lobby lobby = lobbyManager.getLobbyByPlayerId(user.getUsername());
            if (lobby == null) {
// System.err.println("DEBUG: User not in any lobby");
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
            // Get online players from both OnlinePlayersManager and ChatManager
            ChatManager chatManager = ChatManager.getInstance(this);
            List<String> chatManagerPlayers = chatManager.getOnlinePlayers();

            // Create a combined list of online players
            List<Object> allPlayers = new ArrayList<>();

            // Add players from ChatManager (includes game session players)
            for (String username : chatManagerPlayers) {
                Map<String, Object> playerInfo = new HashMap<>();
                playerInfo.put("username", username);
                playerInfo.put("status", "ONLINE");
                allPlayers.add(playerInfo);
            }

            // Also add players from OnlinePlayersManager for completeness
            List<OnlinePlayersManager.OnlinePlayerInfo> onlinePlayers = onlinePlayersManager.getOnlinePlayers();
            for (OnlinePlayersManager.OnlinePlayerInfo playerInfo : onlinePlayers) {
                // Check if player is already in the list
                boolean alreadyInList = false;
                for (Object existingPlayer : allPlayers) {
                    if (existingPlayer instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> existingPlayerMap = (Map<String, Object>) existingPlayer;
                        if (playerInfo.getUsername().equals(existingPlayerMap.get("username"))) {
                            alreadyInList = true;
                            break;
                        }
                    }
                }

                if (!alreadyInList) {
                    Map<String, Object> playerInfoMap = new HashMap<>();
                    playerInfoMap.put("username", playerInfo.getUsername());
                    playerInfoMap.put("status", playerInfo.getStatus().toString());
                    allPlayers.add(playerInfoMap);
                }
            }

            // Send the combined list to the requesting user
            Message response = new Message();
            response.setType(Message.Type.ONLINE_PLAYERS_LIST);
            response.putInBody("players", allPlayers);
            response.putInBody("timestamp", System.currentTimeMillis());

            connection.sendMessage(response);

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
            }
        }

    }

    private GameSession createGameSessionFromLobby(Lobby lobby) {
        try {

            // Get first player as creator
            if (lobby.getPlayers().isEmpty()) {
// System.err.println("DEBUG: Lobby has no players");
                return null;
            }

            LobbyPlayer admin = lobby.getPlayers().stream()
                .filter(LobbyPlayer::isAdmin)
                .findFirst()
                .orElse(lobby.getPlayers().get(0));


            // Get admin user from player connections instead of App
            PlayerConnection adminConnection = playerConnections.get(admin.getId());
            if (adminConnection != null) {
            }

            // Also try to get user from App as fallback
            User appUser = App.getUser(admin.getId());

            User adminUser;
            if (adminConnection == null || adminConnection.getUser() == null) {
// System.err.println("DEBUG: Admin connection or user not found for: " + admin.getId());
// System.err.println("DEBUG: Available player connections: " + playerConnections.keySet());

                // Create a new user object from the lobby player info
                adminUser = new User();
                adminUser.setUsername(admin.getId());
            } else {
                adminUser = adminConnection.getUser();
            }


            // Create game session
            GameSession gameSession = new GameSession(adminUser, this);
            gameSessions.put(gameSession.getSessionId(), gameSession);

            // First, update the admin's connection in the game session
            if (adminConnection != null) {
                gameSession.addPlayer(adminConnection, adminUser);
            }

            // Add all lobby players to game session
            for (LobbyPlayer lobbyPlayer : lobby.getPlayers()) {
                // Skip admin since we already added them
                if (lobbyPlayer.getId().equals(adminUser.getUsername())) {
                    continue;
                }

                PlayerConnection connection = playerConnections.get(lobbyPlayer.getId());

                // Try to find connection by username if not found by ID
                if (connection == null) {
                    for (Map.Entry<String, PlayerConnection> entry : playerConnections.entrySet()) {
                        if (entry.getValue().getUser() != null &&
                            entry.getValue().getUser().getUsername().equals(lobbyPlayer.getId())) {
                            connection = entry.getValue();
                            break;
                        }
                    }
                }

                if (connection != null && connection.getUser() != null) {
                    User user = connection.getUser();
                    boolean success = gameSession.addPlayer(connection, user);
                    if (success) {
                    } else {
// System.err.println("DEBUG: Failed to add player " + user.getUsername() + " to game session");
                    }
                } else {
// System.err.println("DEBUG: Connection or user not found for lobby player: " + lobbyPlayer.getId());
// System.err.println("DEBUG: Connection: " + (connection != null ? "found" : "null"));
// System.err.println("DEBUG: User: " + (connection != null && connection.getUser() != null ? connection.getUser().getUsername() : "null"));

                    // Create a fallback user if connection is not found
                    if (connection == null) {
                        User fallbackUser = new User();
                        fallbackUser.setUsername(lobbyPlayer.getId());

                        // Try to add player to game session without connection
                        Player newPlayer = new Player(fallbackUser);
                        boolean addedToGame = gameSession.getGameInstance().addPlayer(newPlayer);
                        if (addedToGame) {
                        } else {
// System.err.println("DEBUG: Failed to add fallback player " + fallbackUser.getUsername() + " to game instance");
                        }
                    }
                }
            }

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
