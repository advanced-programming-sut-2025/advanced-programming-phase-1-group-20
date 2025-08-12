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
import org.example.common.models.Market;
import org.example.common.models.Product;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class ClientMessageHandler {
    private final NetworkClient networkClient;
    private GameStateUpdateListener gameStateListener;
    private ChatMessageListener chatListener;
    private TradeRequestListener tradeListener;
    private ConnectionStatusListener connectionListener;
    private OnlinePlayersListener onlinePlayersListener;
    private LobbyMessageListener lobbyListener;
    private RadioMessageListener radioListener;
    private ReactionMessageListener reactionListener;
    private MarketUpdateListener marketUpdateListener;
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

    public interface RadioMessageListener {
        void onRadioTrackUpdate(String trackName, String trackPath, String fromPlayer);
        void onRadioTrackUpload(String trackName, String trackPath, String fromPlayer);
        void onRadioConnectRequest(String requestingPlayer, String targetPlayer);
        void onRadioConnectResponse(String respondingPlayer, String targetPlayer, boolean accepted);
        void onRadioDisconnect(String disconnectingPlayer, String targetPlayer);
    }

    public interface ReactionMessageListener {
        void onReactionReceived(String fromPlayer, String reaction);
    }

    public interface MarketUpdateListener {
        void onMarketStockUpdate(String marketName, String itemName, double newStock);
        void onPlayerDataUpdate(); // For money and inventory updates
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
    }

    /**
     * Get the current game instance, preferring the set game over App.getGame()
     */
    private Game getCurrentGame() {
        if (currentGame != null) {
            return currentGame;
        } else {
            return App.getGame();
        }
    }

    public void handleMessage(Message message) {
        // Run message processing on main thread
        Gdx.app.postRunnable(() -> processMessage(message));
    }

    private void processMessage(Message message) {
        try {

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
                case CHAT_PRIVATE:
                    handlePrivateChatMessage(message);
                    break;
                case CHAT_PUBLIC:
                    handlePublicChatMessage(message);
                    break;
                case CHAT_ROOM_CREATE:
                    handleChatRoomCreated(message);
                    break;
                case CHAT_ROOM_JOIN:
                    handleChatRoomJoined(message);
                    break;
                case CHAT_ROOM_LEAVE:
                    handleChatRoomLeft(message);
                    break;
                case CHAT_HISTORY_REQUEST:
                    handleChatHistoryReceived(message);
                    break;
                case PLAYER_MOVE:
                    handlePlayerMove(message);
                    break;
                case PLAYER_DATA_UPDATE:
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
                case NPC_UPDATE:
                    handleNPCUpdate(message);
                    break;
                case TRADE_REQUEST:
                    handleTradeRequest(message);
                    break;
                case TRADE_RESPONSE:
                    handleTradeResponse(message);
                    break;
                case TRADE_ACCEPT:
                    handleTradeAccept(message);
                    break;
                case TRADE_DECLINE:
                    handleTradeDecline(message);
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
                    handleFarmSelectionUpdate(message);
                    break;
                case FARM_SELECTION_COMPLETE:
                    handleFarmSelectionComplete(message);
                    break;
                // Radio system messages
                case RADIO_TRACK_UPDATE:
                    handleRadioTrackUpdate(message);
                    break;
                case RADIO_TRACK_UPLOAD:
                    handleRadioTrackUpload(message);
                    break;
                case RADIO_CONNECT_REQUEST:
                    handleRadioConnectRequest(message);
                    break;
                case RADIO_CONNECT_RESPONSE:
                    handleRadioConnectResponse(message);
                    break;
                case RADIO_DISCONNECT:
                    handleRadioDisconnect(message);
                    break;
                // Reaction system messages
                case REACTION_SEND:
                    handleReactionSend(message);
                    break;
                case REACTION_RECEIVE:
                    handleReactionReceive(message);
                    break;
                case SLEEP_TRANSITION:
                    handleSleepTransition(message);
                    break;
                // Lobby-related message types
                case CREATE_LOBBY:
                case JOIN_LOBBY:
                case LEAVE_LOBBY:
                case LIST_LOBBIES:
                case SEARCH_LOBBY:
                case PLAYER_READY:
                case START_LOBBY_GAME:
                    handleLobbyMessage(message);
                    break;
                default:
                    // Unhandled message type
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleGameStarted(Message message) {
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

        // Store game session ID for reconnection
        if (gameSessionId != null) {
            setGameSessionId(gameSessionId);
        }

        // In multiplayer mode, currentPlayerUsername will be null since each client sets their own current player
        if (currentPlayerUsername == null) {
            // Each client sets their own current player
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
        String username = message.getFromBody("username");
        Integer farmIndex = message.getFromBody("farmIndex");
        Object availableFarms = message.getFromBody("availableFarms");
        Object playerSelections = message.getFromBody("playerSelections");

        // Forward to lobby listener for UI updates
        if (lobbyListener != null) {
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

        // In multiplayer mode, currentPlayerUsername will be null since each client sets their own current player
        if (currentPlayerUsername == null) {
            // Each client sets their own current player
        }

        // Validate essential data
        if (completeGameStateObj == null) {
            return;
        }

        if (isActive == null || !isActive) {
            return;
        }

        // Set connection state to IN_GAME if the game is now fully active
        if (isActive) {
            networkClient.setConnectionState(NetworkClient.ConnectionState.IN_GAME);
        }

        // Forward to lobby listener for UI updates with enhanced data
        if (lobbyListener != null) {
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
            } else {
                // Fallback to original message structure for backward compatibility
                lobbyListener.onLobbyMessage(message);
            }
        }
    }

    private void handleLobbyMessage(Message message) {
        if (lobbyListener != null) {
            lobbyListener.onLobbyMessage(message);
        }
    }

    private void handleSuccessMessage(Message message) {
        String messageText = message.getFromBody("message");
        String sessionId = message.getFromBody("sessionId");
        String username = message.getFromBody("username");
        String gameId = message.getFromBody("gameId");
        Object lobby = message.getFromBody("lobby");
        String source = message.getFromBody("source");

        // Handle market purchase success
        if ("MARKET_BUY".equals(source)) {
            handleMarketPurchaseSuccess(message);
            return;
        }

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
            // Forward to lobby listener if available
            if (lobbyListener != null) {
                lobbyListener.onLobbyMessage(message);
            }
        }

        // Handle other lobby-related success messages
        if (messageText != null && (messageText.contains("Lobby list retrieved") ||
            messageText.contains("Joined lobby successfully") ||
            messageText.contains("Left lobby successfully") ||
            messageText.contains("Search completed"))) {
            // Forward to lobby listener if available
            if (lobbyListener != null) {
                lobbyListener.onLobbyMessage(message);
            }
        }
    }

    private void handleMarketPurchaseSuccess(Message message) {
        Object playerDataObj = message.getFromBody("playerData");
        if (playerDataObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> playerData = (Map<String, Object>) playerDataObj;

            // Update the current player's data
            Game currentGame = getCurrentGame();
            if (currentGame != null) {
                Player currentPlayer = currentGame.getCurrentPlayer();
                if (currentPlayer != null) {
                    // Update money
                    Object moneyObj = playerData.get("money");
                    if (moneyObj instanceof Integer) {
                        int newMoney = (Integer) moneyObj;
                        int currentMoney = currentPlayer.getMoney();
                        if (newMoney != currentMoney) {
                            if (newMoney > currentMoney) {
                                currentPlayer.increaseMoney(newMoney - currentMoney);
                            } else {
                                currentPlayer.decreaseMoney(currentMoney - newMoney);
                            }
                        }
                    }

                    // Handle the specific purchased item
                    String purchasedItemName = (String) playerData.get("purchasedItem");
                    Object purchasedQuantityObj = playerData.get("purchasedQuantity");

                    if (purchasedItemName != null && purchasedQuantityObj instanceof Integer) {
                        int purchasedQuantity = (Integer) purchasedQuantityObj;

                        // Find the item in the market and add it to the player's backpack
                        // We need to find the item from the market's stock
                        if (currentGame.getGameMap() != null && currentGame.getGameMap().getVillage() != null) {
                            for (Market market : currentGame.getGameMap().getVillage().getMarkets()) {
                                if (market != null) {
                                    org.example.common.models.Items.Item item = market.getItem(purchasedItemName);
                                    if (item != null) {
                                        // Add the item to the player's backpack
                                        boolean added = currentPlayer.getBackpack().add(item, purchasedQuantity);
                                        if (added) {
                                            System.out.println("✅ Added " + purchasedQuantity + "x " + purchasedItemName + " to player's backpack");
                                        } else {
                                            System.out.println("⚠️ Failed to add " + purchasedQuantity + "x " + purchasedItemName + " to backpack (inventory full?)");
                                        }
                                        break;
                                    }
                                }
                            }
                        }

                        // Notify market update listener about player data changes
                        if (marketUpdateListener != null) {
                            marketUpdateListener.onPlayerDataUpdate();
                        }
                    }
                }
            }
        }

        System.out.println("✅ Market purchase successful: " + message.getFromBody("message"));
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

        // Show notification for chat messages
        showChatNotification(sender, messageText);
    }

    private void showChatNotification(String sender, String message) {
        // This will be implemented when we integrate with the notification system
        // For now, just log the notification
        System.out.println("[NOTIFICATION] New chat message from " + sender + ": " + message);
    }

    private void handlePrivateChatMessage(Message message) {
        String sender = message.getFromBody("sender");
        String messageText = message.getFromBody("message");
        String recipient = message.getFromBody("recipient");
        Long timestamp = message.getFromBody("timestamp");

        System.out.println("[PRIVATE CHAT] " + sender + " -> " + recipient + ": " + messageText);

        if (chatListener != null && sender != null && messageText != null) {
            chatListener.onChatMessage(sender, messageText, timestamp != null ? timestamp : System.currentTimeMillis());
        }
    }

    private void handlePublicChatMessage(Message message) {
        String sender = message.getFromBody("sender");
        String messageText = message.getFromBody("message");
        Long timestamp = message.getFromBody("timestamp");

        System.out.println("[PUBLIC CHAT] " + sender + ": " + messageText);

        if (chatListener != null && sender != null && messageText != null) {
            chatListener.onChatMessage(sender, messageText, timestamp != null ? timestamp : System.currentTimeMillis());
        }
    }

    private void handleChatRoomCreated(Message message) {
        String roomId = message.getFromBody("roomId");
        String roomName = message.getFromBody("roomName");
        String owner = message.getFromBody("owner");

        System.out.println("[CHAT ROOM] Created: " + roomName + " (ID: " + roomId + ") by " + owner);

        // Notify chat listener about new room
        if (chatListener != null) {
            // You might want to add a method to the ChatMessageListener interface for room events
        }
    }

    private void handleChatRoomJoined(Message message) {
        String roomId = message.getFromBody("roomId");
        String username = message.getFromBody("username");

        System.out.println("[CHAT ROOM] " + username + " joined room: " + roomId);
    }

    private void handleChatRoomLeft(Message message) {
        String roomId = message.getFromBody("roomId");
        String username = message.getFromBody("username");

        System.out.println("[CHAT ROOM] " + username + " left room: " + roomId);
    }

    private void handleChatHistoryReceived(Message message) {
        String roomId = message.getFromBody("roomId");
        Object historyData = message.getFromBody("history");

        System.out.println("[CHAT HISTORY] Received history for room: " + roomId);

        // Parse and display chat history
        if (historyData != null && chatListener != null) {
            // You might want to add a method to handle chat history
        }
    }

    private void handlePlayerMove(Message message) {
        String username = message.getFromBody("username");
        float x = message.getFloatFromBody("x");
        float y = message.getFloatFromBody("y");

        if (username != null) {
            // Update the player's position in the game state
            Game currentGame = getCurrentGame();
            if (currentGame != null) {
                Player targetPlayer = currentGame.getPlayerByUsername(username);
                if (targetPlayer != null) {
                    targetPlayer.setPosX(x);
                    targetPlayer.setPosY(y);
                    // Update the sprite position to reflect the new coordinates
                    targetPlayer.updatePosition();
                }
            }

            // Notify the game state listener
            if (gameStateListener != null) {
                gameStateListener.onPlayerMove(username, x, y);
            }
        }
    }

    private void handlePlayerDataUpdate(Message message) {

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
            System.out.println("🔍 DEBUG: Players data object type: " + playersData.getClass().getSimpleName());

            Game currentGame = getCurrentGame();
            if (currentGame != null) {
                try {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> playersMap = (Map<String, Object>) playersData;

                    // Get current player username to exclude from server updates
                    String currentPlayerUsername = null;
                    if (currentGame.getCurrentPlayer() != null && currentGame.getCurrentPlayer().getUser() != null) {
                        currentPlayerUsername = currentGame.getCurrentPlayer().getUser().getUsername();
                    }

                    for (Map.Entry<String, Object> entry : playersMap.entrySet()) {
                        String username = entry.getKey();
                        @SuppressWarnings("unchecked")
                        Map<String, Object> playerData = (Map<String, Object>) entry.getValue();

                        System.out.println("🔍 DEBUG: Processing player data for username: " + username);
                        System.out.println("🔍 DEBUG: Player data keys: " + playerData.keySet());

                        // Check if isPlayerInVillage field exists in the data
                        if (playerData.containsKey("isPlayerInVillage")) {
                            Object isInVillageObj = playerData.get("isPlayerInVillage");
                            System.out.println("🔍 DEBUG: isPlayerInVillage field found for " + username + ": " + isInVillageObj + " (type: " + (isInVillageObj != null ? isInVillageObj.getClass().getSimpleName() : "null") + ")");
                        } else {
                            System.out.println("🔍 DEBUG: isPlayerInVillage field NOT found for " + username);
                        }

                        // Also check for isInVillage field (alternative naming)
                        if (playerData.containsKey("isInVillage")) {
                            Object isInVillageObj = playerData.get("isInVillage");
                            System.out.println("🔍 DEBUG: isInVillage field found for " + username + ": " + isInVillageObj + " (type: " + (isInVillageObj != null ? isInVillageObj.getClass().getSimpleName() : "null") + ")");
                        } else {
                            System.out.println("🔍 DEBUG: isInVillage field NOT found for " + username);
                        }

                        // Skip updating the current player to avoid conflicts with local state
                        if (username.equals(currentPlayerUsername)) {
                            System.out.println("🔍 DEBUG: Skipping current player: " + username);
                            continue;
                        }

                        // Find the player in the current game
                        Player targetPlayer = currentGame.getPlayerByUsername(username);
                        if (targetPlayer != null) {
                            System.out.println("🔍 DEBUG: Found target player: " + username + ", updating with server data");
                            // Force update player with exact server data
                            forceUpdatePlayerFromServerData(targetPlayer, playerData);
                        } else {
                            System.out.println("🔍 DEBUG: Target player not found in current game: " + username);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void forceUpdatePlayerFromServerData(Player player, Map<String, Object> playerData) {
        System.out.println("🔍 DEBUG: forceUpdatePlayerFromServerData called for player: " + player.getUser().getUsername());
        System.out.println("🔍 DEBUG: Available fields in playerData: " + playerData.keySet());
        try {

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
                System.out.println("🔍 DEBUG: Processing isInVillage for player " + player.getUser().getUsername() + ": " + isInVillage);

                if (isInVillage != null) {
                    boolean oldValue = player.getIsInVillage();
                    player.setIsInVillage(isInVillage);
                    System.out.println("🔍 DEBUG: Updated isInVillage for " + player.getUser().getUsername() + " from " + oldValue + " to " + isInVillage);
                } else {
                    System.out.println("🔍 DEBUG: isInVillage value is null for player " + player.getUser().getUsername());
                }
            } else {
                System.out.println("🔍 DEBUG: isInVillage field not found in player data for " + player.getUser().getUsername());
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

            // Update animation state if available
            if (playerData.containsKey("currentAnimation")) {
                String currentAnimation = (String) playerData.get("currentAnimation");
                if (currentAnimation != null) {
                    player.setCurrentAnimation(currentAnimation);
                }
            }

            if (playerData.containsKey("isMoving")) {
                Boolean isMoving = (Boolean) playerData.get("isMoving");
                if (isMoving != null) {
                    player.setMoving(isMoving);
                }
            }

            if (playerData.containsKey("animationTimer")) {
                Object animationTimerObj = playerData.get("animationTimer");
                Float animationTimer = null;
                if (animationTimerObj instanceof Double) {
                    animationTimer = ((Double) animationTimerObj).floatValue();
                } else if (animationTimerObj instanceof Float) {
                    animationTimer = (Float) animationTimerObj;
                }
                if (animationTimer != null) {
                    player.setAnimationTimer(animationTimer);
                }
            }

            // Update sprite position to reflect new coordinates
            player.updatePosition();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleGameStateUpdate(Message message) {
        // Check if this is a market update
        Boolean isMarketUpdate = message.getFromBody("marketUpdate");
        if (isMarketUpdate != null && isMarketUpdate) {
            handleMarketUpdate(message);
            return;
        }

        Object gameState = message.getFromBody("gameState");
        Object dateState = message.getFromBody("dateState");

        if (dateState != null) {
            try {
                Game currentGame = getCurrentGame();
                if (currentGame != null) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> serverDateState = (Map<String, Object>) dateState;
                    currentGame.syncDateFromServer(serverDateState);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (gameState != null && gameStateListener != null) {
            gameStateListener.onGameStateUpdate(gameState);
        }
    }

    private void handleMarketUpdate(Message message) {
        String marketName = message.getFromBody("marketName");
        String itemName = message.getFromBody("itemName");
        Double newStock = message.getFromBody("newStock");

        if (marketName != null && itemName != null && newStock != null) {
            System.out.println("🛒 MARKET UPDATE: " + marketName + " - " + itemName + " stock updated to: " + newStock);

            // Update the local market stock
            Game currentGame = getCurrentGame();
            if (currentGame != null && currentGame.getGameMap() != null && currentGame.getGameMap().getVillage() != null) {
                for (Market market : currentGame.getGameMap().getVillage().getMarkets()) {
                    if (market != null && market.getName().equals(marketName)) {
                        Product product = market.getProduct(itemName);
                        if (product != null) {
                            product.setAmount(newStock);
                            System.out.println("✅ Updated local stock for " + itemName + " in " + marketName + " to: " + newStock);
                        }
                        break;
                    }
                }
            }

            // Notify market update listener
            if (marketUpdateListener != null) {
                marketUpdateListener.onMarketStockUpdate(marketName, itemName, newStock);
            }

            // Send a chat message to notify all players about the stock update
            String chatMessage = marketName + " " + itemName + " " + (int) newStock.doubleValue();
            if (chatListener != null) {
                chatListener.onChatMessage("SYSTEM", chatMessage, System.currentTimeMillis());
            }
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
                }
            } catch (Exception e) {
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
                e.printStackTrace();
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

    private void handleTradeAccept(Message message) {
        String fromPlayer = message.getFromBody("fromPlayer");
        String toPlayer = message.getFromBody("toPlayer");
        Object tradeItems = message.getFromBody("tradeItems");

        if (fromPlayer != null && toPlayer != null && tradeListener != null) {
            // Handle trade acceptance with items
        }
    }

    private void handleTradeDecline(Message message) {
        String fromPlayer = message.getFromBody("fromPlayer");
        String toPlayer = message.getFromBody("toPlayer");

        if (fromPlayer != null && toPlayer != null && tradeListener != null) {
            // Handle trade decline
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
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleNPCUpdate(Message message) {
        Object npcsData = message.getFromBody("npcs");
        if (npcsData != null) {

            Game currentGame = getCurrentGame();
            if (currentGame != null && currentGame.getGameMap() != null &&
                currentGame.getGameMap().getVillage() != null) {

                try {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> npcList = (List<Map<String, Object>>) npcsData;

                    for (Map<String, Object> npcData : npcList) {
                        String npcName = (String) npcData.get("name");
                        Float posX = ((Double) npcData.get("posX")).floatValue();
                        Float posY = ((Double) npcData.get("posY")).floatValue();
                        String currentAnimation = (String) npcData.get("currentAnimation");
                        Boolean isMoving = (Boolean) npcData.get("isMoving");
                        String spriteName = (String) npcData.get("spriteName");

                        // Find and update the NPC in the village
                        List<org.example.common.models.entities.NPC> residents =
                            currentGame.getGameMap().getVillage().getResidents();

                        for (org.example.common.models.entities.NPC npc : residents) {
                            if (npc.getName().equals(npcName)) {
                                npc.setPosX(posX);
                                npc.setPosY(posY);
                                npc.setCurrentAnimation(currentAnimation);
                                npc.setMoving(isMoving);
                                npc.setSpriteName(spriteName);


                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("DEBUG: Error processing NPC update: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void handleOnlinePlayersList(Message message) {
        Object playersObj = message.getFromBody("players");

        if (playersObj instanceof List && onlinePlayersListener != null) {
            List<Object> players = (List<Object>) playersObj;
            onlinePlayersListener.onOnlinePlayersUpdate(players);
        }
    }

    private void handlePlayerUpdate(Message message) {
        try {
            String action = message.getFromBody("action");
            String username = message.getFromBody("username");

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
        this.lobbyListener = listener;
    }

    public void setRadioListener(RadioMessageListener listener) {
        this.radioListener = listener;
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

    // Radio system message handlers
    private void handleRadioTrackUpdate(Message message) {
        String trackName = message.getFromBody("trackName");
        String trackPath = message.getFromBody("trackPath");
        String playerName = message.getFromBody("playerName");

        System.out.println("Radio: Received track update from " + playerName + ": " + trackName);

        if (radioListener != null) {
            radioListener.onRadioTrackUpdate(trackName, trackPath, playerName);
        }
    }

    private void handleRadioTrackUpload(Message message) {
        String trackName = message.getFromBody("trackName");
        String trackPath = message.getFromBody("trackPath");
        String playerName = message.getFromBody("playerName");

        System.out.println("Radio: Received track upload from " + playerName + ": " + trackName);

        if (radioListener != null) {
            radioListener.onRadioTrackUpload(trackName, trackPath, playerName);
        }
    }

    private void handleRadioConnectRequest(Message message) {
        String targetPlayer = message.getFromBody("targetPlayer");
        String requestingPlayer = message.getFromBody("requestingPlayer");

        System.out.println("Radio: Connection request from " + requestingPlayer + " to " + targetPlayer);

        if (radioListener != null) {
            radioListener.onRadioConnectRequest(requestingPlayer, targetPlayer);
        }
    }

    private void handleRadioConnectResponse(Message message) {
        String targetPlayer = message.getFromBody("targetPlayer");
        String respondingPlayer = message.getFromBody("respondingPlayer");
        Boolean accepted = message.getFromBody("accepted");

        System.out.println("Radio: Connection response from " + respondingPlayer + " to " + targetPlayer + ": " + accepted);

        if (radioListener != null) {
            radioListener.onRadioConnectResponse(respondingPlayer, targetPlayer, accepted);
        }
    }

    private void handleRadioDisconnect(Message message) {
        String targetPlayer = message.getFromBody("targetPlayer");
        String disconnectingPlayer = message.getFromBody("disconnectingPlayer");

        System.out.println("Radio: Disconnect from " + disconnectingPlayer + " to " + targetPlayer);

        if (radioListener != null) {
            radioListener.onRadioDisconnect(disconnectingPlayer, targetPlayer);
        }
    }

    // Reaction system message handlers
    private void handleReactionSend(Message message) {
        String reaction = message.getFromBody("reaction");
        String fromPlayer = message.getFromBody("fromPlayer");
        String toPlayer = message.getFromBody("toPlayer");

        System.out.println("Reaction: Sending reaction from " + fromPlayer + " to " + toPlayer + ": " + reaction);

        // Forward to server for broadcasting
        networkClient.sendMessage(message);
    }

    private void handleReactionReceive(Message message) {
        String fromPlayer = message.getFromBody("fromPlayer");
        String reaction = message.getFromBody("reaction");

        if (reactionListener != null) {
            reactionListener.onReactionReceived(fromPlayer, reaction);
        }
    }

    private void handleSleepTransition(Message message) {
        boolean allPlayersAtHome = message.getFromBody("allPlayersAtHome");
        List<String> playersNeedingToReturn = message.getFromBody("playersNeedingToReturn");
        List<String> playersToCollapse = message.getFromBody("playersToCollapse");
        String currentTime = message.getFromBody("currentTime");

        System.out.println("🌙 SLEEP TRANSITION: Received sleep transition message");
        System.out.println("🌙 All players at home: " + allPlayersAtHome);
        System.out.println("🌙 Players needing to return: " + playersNeedingToReturn);
        System.out.println("🌙 Players to collapse: " + playersToCollapse);
        System.out.println("🌙 Current time: " + currentTime);

        // Check if current player needs to return home
        if (getCurrentGame() != null && getCurrentGame().getCurrentPlayer() != null) {
            Player currentPlayer = getCurrentGame().getCurrentPlayer();
            String currentPlayerName = currentPlayer.getUser().getUsername();

            if (playersNeedingToReturn != null && playersNeedingToReturn.contains(currentPlayerName)) {
                System.out.println("🌙 WARNING: You need to return home before the day can end!");
                // You could show a UI notification here
            }

            if (playersToCollapse != null && playersToCollapse.contains(currentPlayerName)) {
                System.out.println("🌙 WARNING: You don't have enough energy to return home - you will collapse!");
                // You could show a UI notification here
            }
        }
    }

    // Setter for reaction listener
    public void setReactionListener(ReactionMessageListener listener) {
        this.reactionListener = listener;
    }

    public void setMarketUpdateListener(MarketUpdateListener listener) {
        this.marketUpdateListener = listener;
    }
}
