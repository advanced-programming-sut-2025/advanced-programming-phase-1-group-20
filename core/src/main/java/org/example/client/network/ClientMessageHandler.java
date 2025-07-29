package org.example.client.network;

import com.badlogic.gdx.Gdx;
import org.example.common.models.Message;
import org.example.common.models.App;
import org.example.common.models.entities.Game;
import org.example.common.models.Player.Player;
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
    }
    
    public void handleMessage(Message message) {
        // Run message processing on main thread
        Gdx.app.postRunnable(() -> processMessage(message));
    }
    
    private void processMessage(Message message) {
        try {
            System.out.println("DEBUG: Processing message type: " + message.getType() + " with body: " + message.getBody());
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
                case GAME_STATE_UPDATE:
                    handleGameStateUpdate(message);
                    break;
                case GAME_STATE_FULL:
                    handleFullGameState(message);
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
        Object playersData = message.getFromBody("playersData");
        Object gameData = message.getFromBody("gameData");
        String currentPlayerUsername = message.getFromBody("currentPlayerUsername");
        
        System.out.println("DEBUG: Farm selection complete - " + messageText);
        System.out.println("DEBUG: Complete game state received: " + (completeGameStateObj != null ? "yes" : "no"));
        
        // Set connection state to IN_GAME if the game is now fully active
        if (isActive != null && isActive) {
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
                enhancedMessage.putInBody("gameSessionId", completeGameState.get("gameSessionId"));
                enhancedMessage.putInBody("playerSelections", completeGameState.get("playerSelections"));
                enhancedMessage.putInBody("playersData", completeGameState.get("playersData"));
                enhancedMessage.putInBody("gameData", completeGameState.get("gameData"));
                enhancedMessage.putInBody("currentPlayerUsername", completeGameState.get("currentPlayerUsername"));
                enhancedMessage.putInBody("playerCount", completeGameState.get("playerCount"));
                enhancedMessage.putInBody("allPlayersInfo", completeGameState.get("allPlayersInfo"));
                
                System.out.println("DEBUG: Forwarding enhanced FARM_SELECTION_COMPLETE to lobby listener");
                lobbyListener.onLobbyMessage(enhancedMessage);
            } else {
                // Fallback to original message structure for backward compatibility
                System.out.println("DEBUG: Using fallback message structure for FARM_SELECTION_COMPLETE");
                lobbyListener.onLobbyMessage(message);
            }
        }
    }
    
    private void handleLobbyMessage(Message message) {
        System.out.println("DEBUG: handleLobbyMessage called with type: " + message.getType());
        if (lobbyListener != null) {
            System.out.println("DEBUG: Forwarding lobby message to listener");
            lobbyListener.onLobbyMessage(message);
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
        Float x = message.getFromBody("x");
        Float y = message.getFromBody("y");
        
        if (username != null && x != null && y != null && gameStateListener != null) {
            gameStateListener.onPlayerMove(username, x, y);
        }
    }
    
    private void handleGameStateUpdate(Message message) {
        Object gameState = message.getFromBody("gameState");
        
        if (gameState != null && gameStateListener != null) {
            gameStateListener.onGameStateUpdate(gameState);
        }
    }
    
    private void handleFullGameState(Message message) {
        Object gameState = message.getFromBody("gameState");
        Object playersData = message.getFromBody("players");
        
        System.out.println("DEBUG: handleFullGameState called - gameState: " + (gameState != null ? "present" : "null") + 
                          ", playersData: " + (playersData != null ? "present" : "null"));
        
        // Update the local game state with server data
        if (gameState != null) {
            try {
                // Update the current game in App with server state
                Game currentGame = App.getGame();
                if (currentGame != null) {
                    // Update game state from server data
                    // This would need proper deserialization and state synchronization
                    System.out.println("DEBUG: Updating local game state with server data");
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
        Message heartbeat = new Message();
        heartbeat.setType(Message.Type.HEARTBEAT);
        heartbeat.putInBody("timestamp", System.currentTimeMillis());
        
        networkClient.sendMessage(heartbeat);
    }
} 