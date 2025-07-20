package org.example.client.network;

import com.badlogic.gdx.Gdx;
import org.example.common.models.Message;
import org.example.common.models.App;
import org.example.common.models.entities.Game;
import org.example.common.models.Player.Player;

import java.util.List;
import java.util.Map;

public class ClientMessageHandler {
    private final NetworkClient networkClient;
    private GameStateUpdateListener gameStateListener;
    private ChatMessageListener chatListener;
    private TradeRequestListener tradeListener;
    private ConnectionStatusListener connectionListener;
    
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
    
    public ClientMessageHandler(NetworkClient networkClient) {
        this.networkClient = networkClient;
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
                default:
                    System.out.println("Unhandled message type: " + message.getType());
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleSuccessMessage(Message message) {
        String messageText = message.getFromBody("message");
        String sessionId = message.getFromBody("sessionId");
        String username = message.getFromBody("username");
        String gameId = message.getFromBody("gameId");
        
        System.out.println("Success: " + messageText);
        
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