package org.example.client.network;

import com.badlogic.gdx.Gdx;
import org.example.common.models.Message;
import org.example.common.models.App;
import org.example.common.models.entities.Game;
import org.example.common.models.Player.Player;
import org.example.common.models.Player.Skill;
import org.example.common.models.common.Location;
import org.example.common.models.MapDetails.Farm;
import org.example.common.Lobby.Lobby;

import java.util.List;
import java.util.Map;

public class ClientMessageHandler {
    private final NetworkClient networkClient;
    private GameStateUpdateListener gameStateListener;
    private ChatMessageListener chatListener;
    private TradeRequestListener tradeListener;
    private ConnectionStatusListener connectionListener;
    private OnlinePlayersListener onlinePlayersListener;
    private LobbyMessageListener lobbyListener;
    private Game currentGame; // Add reference to current game instance

    public interface GameStateUpdateListener {
        void onGameStateUpdate(Object gameState);
        void onPlayersUpdate(List<Player> players);
        void onPlayerMove(String username, float x, float y);
    }

    public interface ChatMessageListener {
        void onChatMessage(String sender, String message, long timestamp);
    }

    public interface TradeRequestListener {
        void onTradeRequest(String fromPlayer, String toPlayer, String item, int quantity);
        void onTradeResponse(String fromPlayer, String toPlayer, boolean accepted);
    }

    public interface ConnectionStatusListener {
        void onConnectionEstablished(String sessionId);
        void onAuthenticationSuccess(String username);
        void onAuthenticationFailed(String reason);
        void onGameJoined(String gameId);
        void onGameLeft();
        void onError(String errorMessage);
    }

    public interface OnlinePlayersListener {
        void onOnlinePlayersUpdate(List<Object> players);
    }

    public interface LobbyMessageListener {
        void onLobbyMessage(Message message);
    }

    public ClientMessageHandler(NetworkClient networkClient) {
        this.networkClient = networkClient;
        this.currentGame = null;
    }

    /**
     * Set the current game instance for multiplayer mode
     */
    public void setCurrentGame(Game game) {
        this.currentGame = game;
        System.out.println("DEBUG: ClientMessageHandler - Set current game: " + (game != null ? "present" : "null"));
    }

    /**
     * Get the current game instance, preferring the set game over App.getGame()
     */
    private Game getCurrentGame() {
        if (currentGame != null) {
            System.out.println("DEBUG: ClientMessageHandler - Using set game instance");
            return currentGame;
        } else {
            System.out.println("DEBUG: ClientMessageHandler - Using App.getGame()");
            return App.getGame();
        }
    }

    public void handleMessage(Message message) {
        // Run message processing on main thread
        Gdx.app.postRunnable(() -> processMessage(message));
    }

    private void processMessage(Message message) {
        try {
            System.out.println("DEBUG: Processing message type: " + message.getType() + " with body: " + message.getBody());

            // Add specific debug for PLAYER_DATA_UPDATE
            if (message.getType() == Message.Type.PLAYER_DATA_UPDATE) {
                System.out.println("🔄 CLIENT: Received PLAYER_DATA_UPDATE message - about to handle it");
            }

            switch (message.getType()) {
                case SUCCESS:
                    handleSuccessMessage(message);
                    break;
                case ERROR:
                    handleErrorMessage(message);
                    break;
                case CHAT:
                    handleChatMessage(message);
                    break;
                case PLAYER_MOVE:
                    handlePlayerMove(message);
                    break;
                case PLAYER_DATA_UPDATE:
                    System.out.println("🔄 CLIENT: Routing to handlePlayerDataUpdate");
                    handlePlayerDataUpdate(message);
                    break;
                case GAME_STATE_UPDATE:
                    handleGameStateUpdate(message);
                    break;
                case GAME_STATE_FULL:
                    handleFullGameState(message);
                    break;
                case WEATHER_UPDATE:
                    handleWeatherUpdate(message);
                    break;
                case TRADE_REQUEST:
                    handleTradeRequest(message);
                    break;
                case TRADE_RESPONSE:
                    handleTradeResponse(message);
                    break;
                case PING:
                    handlePing(message);
                    break;
                case PONG:
                    handlePong(message);
                    break;
                case ONLINE_PLAYERS_LIST:
                    handleOnlinePlayersList(message);
                    break;
                case PLAYER_UPDATE:
                    handlePlayerUpdate(message);
                    break;
                case START_GAME:
                    handleGameStarted(message);
                    break;
                case FARM_SELECTION_UPDATE:
                    System.out.println("DEBUG: ClientMessageHandler - Received FARM_SELECTION_UPDATE message");
                    handleFarmSelectionUpdate(message);
                    break;
                case FARM_SELECTION_COMPLETE:
                    System.out.println("DEBUG: ClientMessageHandler - Received FARM_SELECTION_COMPLETE message");
                    handleFarmSelectionComplete(message);
                    break;
                // Lobby-related message types
                case CREATE_LOBBY:
                case JOIN_LOBBY:
                case LEAVE_LOBBY:
                case LIST_LOBBIES:
                case SEARCH_LOBBY:
                case PLAYER_READY:
                case START_LOBBY_GAME:
                    System.out.println("DEBUG: Handling lobby message type: " + message.getType());
                    handleLobbyMessage(message);
                    break;
                default:
                    System.out.println("Unhandled message type: " + message.getType());
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleGameStarted(Message message) {
        System.out.println("DEBUG: handleGameStarted called");
        String gameSessionId = message.getFromBody("gameSessionId");
        String messageText = message.getFromBody("message");
        Object gameData = message.getFromBody("gameData");
        Object playersData = message.getFromBody("playersData");
        String currentPlayerUsername = message.getFromBody("currentPlayerUsername");

        // Handle playerCount which might come as Double from JSON
        Object playerCountObj = message.getFromBody("playerCount");
        Integer playerCount = null;
        if (playerCountObj instanceof Double) {
            playerCount = ((Double) playerCountObj).intValue();
        } else if (playerCountObj instanceof Integer) {
            playerCount = (Integer) playerCountObj;
        }

        Boolean isActive = message.getFromBody("isActive");

        // Check for both old and new field names for backward compatibility
        Boolean inFarmSelection = message.getFromBody("inFarmSelectionPhase");
        Boolean inMapSelection = message.getFromBody("inMapSelectionPhase");

        // Use either field name
        Boolean isInSelectionPhase = (inFarmSelection != null && inFarmSelection) ||
            (inMapSelection != null && inMapSelection);

        System.out.println("DEBUG: Game started - Session ID: " + gameSessionId + ", Message: " + messageText);
        System.out.println("DEBUG: In farm selection phase: " + isInSelectionPhase + ", Active: " + isActive);
        System.out.println("DEBUG: Current player username: " + currentPlayerUsername);

        // Store game session ID for reconnection
        if (gameSessionId != null) {
            setGameSessionId(gameSessionId);
        }

        // In multiplayer mode, currentPlayerUsername will be null since each client sets their own current player
        if (currentPlayerUsername == null) {
            System.out.println("DEBUG: No specific current player from server - each client will set their own");
        }

        // Only set to IN_GAME if the game is fully active (not in farm selection phase)
        if (isActive != null && isActive && !isInSelectionPhase) {
            networkClient.setConnectionState(NetworkClient.ConnectionState.IN_GAME);
        } else {
            // Stay in AUTHENTICATED state during farm selection phase
            networkClient.setConnectionState(NetworkClient.ConnectionState.AUTHENTICATED);
        }

        // Notify connection listener about game start
        if (connectionListener != null) {
            connectionListener.onGameJoined(gameSessionId);
        }

        // Forward to lobby listener for UI updates
        if (lobbyListener != null) {
            lobbyListener.onLobbyMessage(message);
        }
    }

    private void handleFarmSelectionUpdate(Message message) {
        System.out.println("DEBUG: handleFarmSelectionUpdate called");
        String username = message.getFromBody("username");
        Integer farmIndex = message.getFromBody("farmIndex");
        Object availableFarms = message.getFromBody("availableFarms");
        Object playerSelections = message.getFromBody("playerSelections");

        System.out.println("DEBUG: Player " + username + " selected farm " + farmIndex);
        System.out.println("DEBUG: Available farms: " + availableFarms);
        System.out.println("DEBUG: Player selections: " + playerSelections);

        // Forward to lobby listener for UI updates
        if (lobbyListener != null) {
            System.out.println("DEBUG: Forwarding FARM_SELECTION_UPDATE to lobby listener");
            lobbyListener.onLobbyMessage(message);
        } else {
            System.out.println("DEBUG: No lobby listener set for FARM_SELECTION_UPDATE");
        }
    }

    private void handleFarmSelectionComplete(Message message) {
        System.out.println("DEBUG: handleFarmSelectionComplete called");
        String messageText = message.getFromBody("message");
        Object completeGameStateObj = message.getFromBody("completeGameState");
        Boolean isActive = message.getFromBody("isActive");
        Boolean inFarmSelectionPhase = message.getFromBody("inFarmSelectionPhase");
        Object playersData = message.getFromBody("playersData");
        Object gameData = message.getFromBody("gameData");
        String currentPlayerUsername = message.getFromBody("currentPlayerUsername");

        System.out.println("DEBUG: Farm selection complete - " + messageText);
        System.out.println("DEBUG: Complete game state received: " + (completeGameStateObj != null ? "yes" : "no"));
        System.out.println("DEBUG: Is active: " + isActive);
        System.out.println("DEBUG: In farm selection phase: " + inFarmSelectionPhase);
        System.out.println("DEBUG: Current player username: " + currentPlayerUsername);

        // In multiplayer mode, currentPlayerUsername will be null since each client sets their own current player
        if (currentPlayerUsername == null) {
            System.out.println("DEBUG: No specific current player from server - each client will set their own");
        }

        // Validate essential data
        if (completeGameStateObj == null) {
            System.err.println("DEBUG: No complete game state received in FARM_SELECTION_COMPLETE");
            return;
        }

        if (isActive == null || !isActive) {
            System.err.println("DEBUG: Game is not active in FARM_SELECTION_COMPLETE");
            return;
        }

        // Set connection state to IN_GAME if the game is now fully active
        if (isActive) {
            networkClient.setConnectionState(NetworkClient.ConnectionState.IN_GAME);
            System.out.println("DEBUG: Connection state set to IN_GAME");
        }

        // Forward to lobby listener for UI updates with enhanced data
        if (lobbyListener != null) {
            System.out.println("DEBUG: ClientMessageHandler - Lobby listener is set, forwarding FARM_SELECTION_COMPLETE");
            // If we have the new complete game state structure, use it
            if (completeGameStateObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> completeGameState = (Map<String, Object>) completeGameStateObj;

                // Create a new message with all the data properly structured
                Message enhancedMessage = new Message();
                enhancedMessage.setType(Message.Type.FARM_SELECTION_COMPLETE);
                enhancedMessage.putInBody("message", messageText);
                enhancedMessage.putInBody("completeGameState", completeGameState);
                enhancedMessage.putInBody("isActive", completeGameState.get("isActive"));
                enhancedMessage.putInBody("inFarmSelectionPhase", completeGameState.get("inFarmSelectionPhase"));
                enhancedMessage.putInBody("gameSessionId", completeGameState.get("gameSessionId"));
                enhancedMessage.putInBody("playerSelections", completeGameState.get("playerSelections"));
                enhancedMessage.putInBody("playersData", completeGameState.get("playersData"));
                enhancedMessage.putInBody("gameData", completeGameState.get("gameData"));
                enhancedMessage.putInBody("currentPlayerUsername", completeGameState.get("currentPlayerUsername"));
                enhancedMessage.putInBody("playerCount", completeGameState.get("playerCount"));
                enhancedMessage.putInBody("allPlayersInfo", completeGameState.get("allPlayersInfo"));

                lobbyListener.onLobbyMessage(enhancedMessage);
                System.out.println("DEBUG: lobbyListener.onLobbyMessage called");
            } else {
                // Fallback to original message structure for backward compatibility
                System.out.println("DEBUG: Using fallback message structure for FARM_SELECTION_COMPLETE");
                lobbyListener.onLobbyMessage(message);
            }
        } else {
            System.err.println("DEBUG: No lobby listener set for FARM_SELECTION_COMPLETE");
        }
    }

    private void handleLobbyMessage(Message message) {
        System.out.println("DEBUG: handleLobbyMessage called with type: " + message.getType());
        if (lobbyListener != null) {
            System.out.println("DEBUG: Forwarding lobby message to listener: " + lobbyListener.getClass().getSimpleName());
            lobbyListener.onLobbyMessage(message);
            System.out.println("DEBUG: Lobby message forwarded successfully");
        } else {
            System.out.println("DEBUG: Lobby message received but no listener registered: " + message.getType());
        }
    }

    private void handleSuccessMessage(Message message) {
        String messageText = message.getFromBody("message");
        String sessionId = message.getFromBody("sessionId");
        String username = message.getFromBody("username");
        String gameId = message.getFromBody("gameId");
        Object lobby = message.getFromBody("lobby");

        System.out.println("DEBUG: handleSuccessMessage - message: " + messageText + ", lobby: " + (lobby != null ? "present" : "null"));

        if (sessionId != null && connectionListener != null) {
            connectionListener.onConnectionEstablished(sessionId);
        }

        if (username != null && messageText != null && messageText.contains("Authentication successful")) {
            networkClient.setConnectionState(NetworkClient.ConnectionState.AUTHENTICATED);
            if (connectionListener != null) {
                connectionListener.onAuthenticationSuccess(username);
            }
        }

        if (gameId != null && messageText != null && messageText.contains("joined")) {
            networkClient.setConnectionState(NetworkClient.ConnectionState.IN_GAME);
            if (connectionListener != null) {
                connectionListener.onGameJoined(gameId);
            }
        }

        // Handle lobby creation success
        if (lobby != null && messageText != null && messageText.contains("Lobby created successfully")) {
            System.out.println("DEBUG: Lobby creation success received!");
            System.out.println("DEBUG: Lobby object type: " + lobby.getClass().getSimpleName());
            // Forward to lobby listener if available
            if (lobbyListener != null) {
                System.out.println("DEBUG: Forwarding lobby creation message to listener");
                lobbyListener.onLobbyMessage(message);
            } else {
                System.out.println("DEBUG: No lobby listener available for lobby creation message");
            }
        }

        // Handle other lobby-related success messages
        if (messageText != null && (messageText.contains("Lobby list retrieved") ||
            messageText.contains("Joined lobby successfully") ||
            messageText.contains("Left lobby successfully") ||
            messageText.contains("Search completed"))) {
            System.out.println("DEBUG: Lobby-related success message: " + messageText);
            // Forward to lobby listener if available
            if (lobbyListener != null) {
                System.out.println("DEBUG: Forwarding lobby success message to listener");
                lobbyListener.onLobbyMessage(message);
            } else {
                System.out.println("DEBUG: No lobby listener available for lobby success message");
            }
        }
    }

    private void handleErrorMessage(Message message) {
        String errorMessage = message.getFromBody("message");
        System.err.println("Server error: " + errorMessage);

        if (connectionListener != null) {
            connectionListener.onError(errorMessage);
        }

        // Handle authentication failures
        if (errorMessage != null && errorMessage.toLowerCase().contains("authentication")) {
            networkClient.setConnectionState(NetworkClient.ConnectionState.CONNECTED);
            if (connectionListener != null) {
                connectionListener.onAuthenticationFailed(errorMessage);
            }
        }
    }

    private void handleChatMessage(Message message) {
        String sender = message.getFromBody("sender");
        String messageText = message.getFromBody("message");
        Long timestamp = message.getFromBody("timestamp");

        System.out.println("[CHAT] " + sender + ": " + messageText);

        if (chatListener != null && sender != null && messageText != null) {
            chatListener.onChatMessage(sender, messageText, timestamp != null ? timestamp : System.currentTimeMillis());
        }
    }

    private void handlePlayerMove(Message message) {
        String username = message.getFromBody("username");
        float x = message.getFloatFromBody("x");
        float y = message.getFloatFromBody("y");

        if (username != null) {
            System.out.println("📱 CLIENT: Received player move - " + username + " moved to (" + x + ", " + y + ")");

            // Update the player's position in the game state
            Game currentGame = getCurrentGame();
            if (currentGame != null) {
                Player targetPlayer = currentGame.getPlayerByUsername(username);
                if (targetPlayer != null) {
                    targetPlayer.setPosX(x);
                    targetPlayer.setPosY(y);
                    // Update the sprite position to reflect the new coordinates
                    targetPlayer.updatePosition();
                    System.out.println("✅ CLIENT: Updated player " + username + " position in game state and sprite");
                } else {
                    System.out.println("❌ CLIENT: Player " + username + " not found in current game");
                }
            }

            // Notify the game state listener
            if (gameStateListener != null) {
                gameStateListener.onPlayerMove(username, x, y);
            }
        } else {
            System.err.println("❌ CLIENT: Invalid player move message - missing data");
        }
    }

    private void handlePlayerDataUpdate(Message message) {
        System.out.println("🔄 CLIENT: handlePlayerDataUpdate method called!");

        Object playersData = message.getFromBody("players");
        Object timestampObj = message.getFromBody("timestamp");

        // Handle timestamp conversion from Double to Long
        Long timestamp = null;
        if (timestampObj instanceof Double) {
            timestamp = ((Double) timestampObj).longValue();
        } else if (timestampObj instanceof Long) {
            timestamp = (Long) timestampObj;
        }

        if (playersData != null) {
            System.out.println("🔄 CLIENT: Received comprehensive player data update with timestamp: " + timestamp);

            Game currentGame = getCurrentGame();
            if (currentGame != null) {
                try {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> playersMap = (Map<String, Object>) playersData;

                    System.out.println("🔄 CLIENT: Processing " + playersMap.size() + " players from server update");

                    // Get current player username to exclude from server updates
                    String currentPlayerUsername = null;
                    if (currentGame.getCurrentPlayer() != null && currentGame.getCurrentPlayer().getUser() != null) {
                        currentPlayerUsername = currentGame.getCurrentPlayer().getUser().getUsername();
                        System.out.println("🔄 CLIENT: Current player is " + currentPlayerUsername + " - will exclude from server updates");
                    }

                    for (Map.Entry<String, Object> entry : playersMap.entrySet()) {
                        String username = entry.getKey();
                        @SuppressWarnings("unchecked")
                        Map<String, Object> playerData = (Map<String, Object>) entry.getValue();

                        System.out.println("🔄 CLIENT: Processing player: " + username + " with data: " + playerData);

                        // Skip updating the current player to avoid conflicts with local state
                        if (username.equals(currentPlayerUsername)) {
                            System.out.println("🔄 CLIENT: Skipping update for current player: " + username);
                            continue;
                        }

                        // Find the player in the current game
                        Player targetPlayer = currentGame.getPlayerByUsername(username);
                        if (targetPlayer != null) {
                            // Log current state before update
                            System.out.println("🔄 CLIENT: Before update - Player " + username +
                                " - Energy: " + targetPlayer.getEnergy() +
                                ", Position: (" + targetPlayer.getPosX() + ", " + targetPlayer.getPosY() + ")");

                            // Force update player with exact server data
                            forceUpdatePlayerFromServerData(targetPlayer, playerData);

                            // Log state after update
                            System.out.println("✅ CLIENT: After update - Player " + username +
                                " - Energy: " + targetPlayer.getEnergy() +
                                ", Position: (" + targetPlayer.getPosX() + ", " + targetPlayer.getPosY() + ")");
                        } else {
                            System.out.println("❌ CLIENT: Player " + username + " not found in current game");
                            System.out.println("❌ CLIENT: Available players in game: " +
                                (currentGame.getPlayers() != null ?
                                    currentGame.getPlayers().stream()
                                        .map(p -> p.getUser().getUsername())
                                        .collect(java.util.stream.Collectors.joining(", ")) : "null"));
                        }
                    }
                } catch (Exception e) {
                    System.err.println("❌ CLIENT: Error processing player data update: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("❌ CLIENT: No current game available for player data update");
            }
        } else {
            System.err.println("❌ CLIENT: Invalid player data update message - missing players data");
        }
    }

    private void forceUpdatePlayerFromServerData(Player player, Map<String, Object> playerData) {
        try {
            System.out.println("🔄 CLIENT: Force updating player " + player.getUser().getUsername() + " with server data");

            // Always update basic player properties with exact server values
            if (playerData.containsKey("posX")) {
                Object posXObj = playerData.get("posX");
                Float posX = null;
                if (posXObj instanceof Double) {
                    posX = ((Double) posXObj).floatValue();
                } else if (posXObj instanceof Float) {
                    posX = (Float) posXObj;
                }
                if (posX != null) {
                    System.out.println("🔄 CLIENT: Force updating posX to " + posX);
                    player.setPosX(posX);
                }
            }

            if (playerData.containsKey("posY")) {
                Object posYObj = playerData.get("posY");
                Float posY = null;
                if (posYObj instanceof Double) {
                    posY = ((Double) posYObj).floatValue();
                } else if (posYObj instanceof Float) {
                    posY = (Float) posYObj;
                }
                if (posY != null) {
                    System.out.println("🔄 CLIENT: Force updating posY to " + posY);
                    player.setPosY(posY);
                }
            }

            if (playerData.containsKey("energy")) {
                Object energyObj = playerData.get("energy");
                Integer energy = null;
                if (energyObj instanceof Double) {
                    energy = ((Double) energyObj).intValue();
                } else if (energyObj instanceof Integer) {
                    energy = (Integer) energyObj;
                }
                if (energy != null) {
                    System.out.println("🔄 CLIENT: Force updating energy to " + energy);
                    player.setEnergy(energy);
                }
            }

            if (playerData.containsKey("money")) {
                Object moneyObj = playerData.get("money");
                Integer money = null;
                if (moneyObj instanceof Double) {
                    money = ((Double) moneyObj).intValue();
                } else if (moneyObj instanceof Integer) {
                    money = (Integer) moneyObj;
                }
                if (money != null) {
                    System.out.println("🔄 CLIENT: Force updating money to " + money);
                    // Calculate the difference and adjust money accordingly
                    int currentMoney = player.getMoney();
                    int difference = money - currentMoney;
                    if (difference > 0) {
                        player.increaseMoney(difference);
                    } else if (difference < 0) {
                        player.decreaseMoney(-difference);
                    }
                }
            }

            if (playerData.containsKey("isInVillage")) {
                Boolean isInVillage = (Boolean) playerData.get("isInVillage");
                if (isInVillage != null) {
                    System.out.println("🔄 CLIENT: Force updating isInVillage to " + isInVillage);
                    player.setIsInVillage(isInVillage);
                }
            }

            // Update location if available
            if (playerData.containsKey("locationX") && playerData.containsKey("locationY")) {
                Object locationXObj = playerData.get("locationX");
                Object locationYObj = playerData.get("locationY");
                Integer locationX = null;
                Integer locationY = null;

                if (locationXObj instanceof Double) {
                    locationX = ((Double) locationXObj).intValue();
                } else if (locationXObj instanceof Integer) {
                    locationX = (Integer) locationXObj;
                }

                if (locationYObj instanceof Double) {
                    locationY = ((Double) locationYObj).intValue();
                } else if (locationYObj instanceof Integer) {
                    locationY = (Integer) locationYObj;
                }

                if (locationX != null && locationY != null) {
                    System.out.println("🔄 CLIENT: Force updating location to (" + locationX + ", " + locationY + ")");
                    // Create new location object and set it
                    Location newLocation = new Location(locationX, locationY, org.example.common.models.enums.Types.TileType.Dirt);
                    player.setLocation(newLocation);
                }
            }

            // Update farm information if available
            if (playerData.containsKey("farmIndex")) {
                Object farmIndexObj = playerData.get("farmIndex");
                Integer farmIndex = null;
                if (farmIndexObj instanceof Double) {
                    farmIndex = ((Double) farmIndexObj).intValue();
                } else if (farmIndexObj instanceof Integer) {
                    farmIndex = (Integer) farmIndexObj;
                }
                if (farmIndex != null && farmIndex >= 0) {
                    System.out.println("🔄 CLIENT: Force updating farm index to " + farmIndex);
                    // Find the farm by index and set it
                    Game currentGame = getCurrentGame();
                    if (currentGame != null && currentGame.getGameMap() != null) {
                        List<Farm> farms = currentGame.getGameMap().getFarms();
                        if (farmIndex < farms.size()) {
                            player.setCurrentFarm(farms.get(farmIndex));
                        }
                    }
                }
            }

            // Update sprite position to reflect new coordinates
            player.updatePosition();

            System.out.println("✅ CLIENT: Finished force updating player " + player.getUser().getUsername());

        } catch (Exception e) {
            System.err.println("DEBUG: Error force updating player from server data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleGameStateUpdate(Message message) {
        Object gameState = message.getFromBody("gameState");
        Object dateState = message.getFromBody("dateState");

        if (dateState != null) {
            try {
                Game currentGame = getCurrentGame();
                System.out.println("DEBUG: ClientMessageHandler - Syncing date with game: " + (currentGame != null ? "present" : "null"));
                if (currentGame != null) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> serverDateState = (Map<String, Object>) dateState;
                    currentGame.syncDateFromServer(serverDateState);
                    System.out.println("DEBUG: Date synced from server in game state update - " +
                        (currentGame.getCurrentDate() != null ? currentGame.getCurrentDate().getCurrentTimeString() : "null"));
                } else {
                    System.out.println("DEBUG: ClientMessageHandler - No game instance available for date sync");
                }
            } catch (Exception e) {
                System.err.println("DEBUG: Error syncing date from game state update: " + e.getMessage());
                e.printStackTrace();
            }
        }

        if (gameState != null && gameStateListener != null) {
            gameStateListener.onGameStateUpdate(gameState);
        }
    }

    private void handleFullGameState(Message message) {
        Object gameState = message.getFromBody("gameState");
        Object playersData = message.getFromBody("players");
        Object dateState = message.getFromBody("dateState");

        // Sync date from server
        if (dateState != null) {
            try {
                Game currentGame = getCurrentGame();
                if (currentGame != null) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> serverDateState = (Map<String, Object>) dateState;
                    currentGame.syncDateFromServer(serverDateState);
                    System.out.println("DEBUG: Date synced from server in full game state - " +
                        (currentGame.getCurrentDate() != null ? currentGame.getCurrentDate().getCurrentTimeString() : "null"));
                }
            } catch (Exception e) {
                System.err.println("DEBUG: Error syncing date from full game state: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Update the local game state with server data
        if (gameState != null) {
            try {
                // Update the current game in App with server state
                Game currentGame = getCurrentGame();
                if (currentGame != null) {
                    // Update game state from server data
                    @SuppressWarnings("unchecked")
                    Map<String, Object> serverGameState = (Map<String, Object>) gameState;
                    currentGame.syncGameStateFromServer(serverGameState);
                    System.out.println("DEBUG: Updated local game state with server data");
                }
            } catch (Exception e) {
                System.err.println("Failed to update game state: " + e.getMessage());
                e.printStackTrace();
            }
        }

        if (gameState != null && gameStateListener != null) {
            gameStateListener.onGameStateUpdate(gameState);
        }

        // Try to parse players data
        if (playersData != null && gameStateListener != null) {
            try {
                // This would need proper deserialization based on your Player class structure
                // For now, we'll just notify that an update occurred
                gameStateListener.onPlayersUpdate(null);
            } catch (Exception e) {
                System.err.println("Failed to parse players data: " + e.getMessage());
            }
        }
    }

    private void handleTradeRequest(Message message) {
        String fromPlayer = message.getFromBody("fromPlayer");
        String toPlayer = message.getFromBody("toPlayer");
        String item = message.getFromBody("item");
        Integer quantity = message.getFromBody("quantity");

        if (fromPlayer != null && toPlayer != null && item != null && quantity != null && tradeListener != null) {
            tradeListener.onTradeRequest(fromPlayer, toPlayer, item, quantity);
        }
    }

    private void handleTradeResponse(Message message) {
        String fromPlayer = message.getFromBody("fromPlayer");
        String toPlayer = message.getFromBody("toPlayer");
        Boolean accepted = message.getFromBody("accepted");

        if (fromPlayer != null && toPlayer != null && accepted != null && tradeListener != null) {
            tradeListener.onTradeResponse(fromPlayer, toPlayer, accepted);
        }
    }

    private void handlePing(Message message) {
        // Respond to ping with pong
        Message pongMessage = new Message();
        pongMessage.setType(Message.Type.PONG);
        pongMessage.putInBody("timestamp", System.currentTimeMillis());
        networkClient.sendMessage(pongMessage);
    }

    private void handlePong(Message message) {
        Long timestamp = message.getFromBody("timestamp");
        if (timestamp != null) {
            long latency = System.currentTimeMillis() - timestamp;
            System.out.println("Server latency: " + latency + "ms");
        }
    }

    private void handleWeatherUpdate(Message message) {
        String weatherStr = message.getFromBody("weather");
        if (weatherStr != null) {
            try {
                Game currentGame = getCurrentGame();
                if (currentGame != null && currentGame.getCurrentDate() != null) {
                    // Update the weather directly from server
                    org.example.common.models.enums.Weather weather =
                        org.example.common.models.enums.Weather.valueOf(weatherStr);
                    currentGame.getCurrentDate().setWeatherToday(weather);
                    System.out.println("DEBUG: Weather updated from server: " + weather);
                }
            } catch (Exception e) {
                System.err.println("DEBUG: Error updating weather from server: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void handleOnlinePlayersList(Message message) {
        Object playersObj = message.getFromBody("players");

        if (playersObj instanceof List && onlinePlayersListener != null) {
            List<Object> players = (List<Object>) playersObj;
            onlinePlayersListener.onOnlinePlayersUpdate(players);
            System.out.println("Received online players list: " + players.size() + " players");
        }
    }

    private void handlePlayerUpdate(Message message) {
        // System.out.println("DEBUG: handlePlayerUpdate called");
        try {
            String action = message.getFromBody("action");
            String username = message.getFromBody("username");
            // System.out.println("DEBUG: Player update - Action: " + action + ", Username: " + username);

            // Forward to appropriate listeners if needed
            if (lobbyListener != null) {
                lobbyListener.onLobbyMessage(message);
            }
        } catch (Exception e) {
            System.err.println("Error handling player update: " + e.getMessage());
        }
    }

    // Listener setters
    public void setGameStateListener(GameStateUpdateListener listener) {
        this.gameStateListener = listener;
    }

    public void setChatListener(ChatMessageListener listener) {
        this.chatListener = listener;
    }

    public void setTradeListener(TradeRequestListener listener) {
        this.tradeListener = listener;
    }

    public void setConnectionListener(ConnectionStatusListener listener) {
        this.connectionListener = listener;
    }

    public void setOnlinePlayersListener(OnlinePlayersListener listener) {
        this.onlinePlayersListener = listener;
    }

    public void setLobbyListener(LobbyMessageListener listener) {
        System.out.println("DEBUG: ClientMessageHandler.setLobbyListener called with listener: " + (listener != null ? listener.getClass().getSimpleName() : "null"));
        this.lobbyListener = listener;
    }

    // Convenience methods for sending responses
    public void sendTradeResponse(String fromPlayer, String toPlayer, boolean accepted) {
        Message response = new Message();
        response.setType(Message.Type.TRADE_RESPONSE);
        response.putInBody("fromPlayer", fromPlayer);
        response.putInBody("toPlayer", toPlayer);
        response.putInBody("accepted", accepted);
        response.putInBody("timestamp", System.currentTimeMillis());

        networkClient.sendMessage(response);
    }

    public void sendHeartbeat() {
        Message heartbeatMessage = new Message();
        heartbeatMessage.setType(Message.Type.HEARTBEAT);
        heartbeatMessage.putInBody("timestamp", System.currentTimeMillis());
        networkClient.sendMessage(heartbeatMessage);
    }

    public void onReconnectionTimeout() {
        System.out.println("CLIENT: Reconnection timeout - notifying UI");

        // Notify connection listener about timeout
        if (connectionListener != null) {
            connectionListener.onError("Reconnection timeout after 2 minutes. Returning to main menu.");
        }

        // Reset game state
        if (currentGame != null) {
            currentGame = null;
        }
    }


    public void setGameSessionId(String gameSessionId) {
        networkClient.setGameSessionId(gameSessionId);
        System.out.println("CLIENT: Stored game session ID: " + gameSessionId);
    }
}
