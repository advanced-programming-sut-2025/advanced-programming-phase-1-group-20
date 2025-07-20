package org.example.client.network;

import org.example.common.models.App;
import org.example.common.models.entities.User;
import org.example.client.network.ClientMessageHandler.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ConnectionManager implements ConnectionStatusListener, ChatMessageListener, TradeRequestListener, GameStateUpdateListener {
    private static ConnectionManager instance;
    private final NetworkClient networkClient;
    private final ClientMessageHandler messageHandler;
    private ConnectionState currentState;
    private String lastError;
    private CompletableFuture<Boolean> authenticationFuture;
    private CompletableFuture<Boolean> connectionFuture;

    public enum ConnectionState {
        OFFLINE,
        CONNECTING,
        CONNECTED,
        AUTHENTICATING,
        AUTHENTICATED,
        IN_GAME,
        ERROR
    }

    private ConnectionManager() {
        this.networkClient = NetworkClient.getInstance();
        this.messageHandler = networkClient.getMessageHandler();
        this.currentState = ConnectionState.OFFLINE;

        // Set up listeners
        messageHandler.setConnectionListener(this);
        messageHandler.setChatListener(this);
        messageHandler.setTradeListener(this);
        messageHandler.setGameStateListener(this);
    }

    public static ConnectionManager getInstance() {
        if (instance == null) {
            instance = new ConnectionManager();
        }
        return instance;
    }

    public CompletableFuture<Boolean> connectToServer(String host, int port) {
        if (currentState == ConnectionState.CONNECTING || currentState == ConnectionState.CONNECTED) {
            return CompletableFuture.completedFuture(false);
        }

        connectionFuture = new CompletableFuture<>();
        currentState = ConnectionState.CONNECTING;

        // Set server address and connect
        networkClient.setServerAddress(host, port);

        // Attempt connection in background
        new Thread(() -> {
            boolean success = networkClient.connect();
            if (success) {
                currentState = ConnectionState.CONNECTED;
                connectionFuture.complete(true);
            } else {
                currentState = ConnectionState.ERROR;
                connectionFuture.complete(false);
            }
        }).start();

        return connectionFuture;
    }

    public CompletableFuture<Boolean> authenticateUser(User user) {
        if (currentState != ConnectionState.CONNECTED) {
            return CompletableFuture.completedFuture(false);
        }

        authenticationFuture = new CompletableFuture<>();
        currentState = ConnectionState.AUTHENTICATING;

        String jwtToken = user.getJwtToken();
        if (jwtToken == null || jwtToken.isEmpty()) {
            // Generate a temporary token for testing
            jwtToken = "temp_token_" + user.getUsername();
        }

        boolean authSent = networkClient.authenticate(user, jwtToken);
        if (!authSent) {
            currentState = ConnectionState.ERROR;
            authenticationFuture.complete(false);
        }

        return authenticationFuture;
    }

    public void disconnect() {
        networkClient.disconnect();
        currentState = ConnectionState.OFFLINE;
        lastError = null;

        if (connectionFuture != null && !connectionFuture.isDone()) {
            connectionFuture.complete(false);
        }
        if (authenticationFuture != null && !authenticationFuture.isDone()) {
            authenticationFuture.complete(false);
        }
    }

    public void update() {
        networkClient.update();
    }

    // Game actions
    public void createMultiplayerGame() {
        if (currentState == ConnectionState.AUTHENTICATED) {
            // Create a lobby for multiplayer games instead of direct game sessions
            User currentUser = getAuthenticatedUser();
            String lobbyName = currentUser != null ? currentUser.getUsername() + "'s Lobby" : "New Lobby";
            networkClient.createLobby(lobbyName, false, true, null);
        }
    }
    
    public void createLobby(String lobbyName, boolean isPrivate, boolean isVisible, String password) {
        if (currentState == ConnectionState.AUTHENTICATED) {
            networkClient.createLobby(lobbyName, isPrivate, isVisible, password);
        }
    }

    public void joinMultiplayerGame(String gameId) {
        if (currentState == ConnectionState.AUTHENTICATED) {
            networkClient.joinGame(gameId);
        }
    }

    public void leaveMultiplayerGame() {
        if (currentState == ConnectionState.IN_GAME) {
            networkClient.leaveGame();
            currentState = ConnectionState.AUTHENTICATED;
        }
    }

    public void sendPlayerMovement(float x, float y) {
        if (currentState == ConnectionState.IN_GAME || currentState == ConnectionState.AUTHENTICATED) {
            networkClient.sendPlayerMove(x, y);
        }
    }

    public void sendChatMessage(String message) {
        if (currentState == ConnectionState.IN_GAME || currentState == ConnectionState.AUTHENTICATED) {
            networkClient.sendChatMessage(message);
        }
    }

    public void sendTradeRequest(String targetPlayer, String item, int quantity) {
        if (currentState == ConnectionState.IN_GAME) {
            networkClient.sendTradeRequest(targetPlayer, item, quantity);
        }
    }

    // ConnectionStatusListener implementation
    @Override
    public void onConnectionEstablished(String sessionId) {
        System.out.println("Connection established with session ID: " + sessionId);
        if (connectionFuture != null && !connectionFuture.isDone()) {
            connectionFuture.complete(true);
        }
    }

    @Override
    public void onAuthenticationSuccess(String username) {
        System.out.println("Authentication successful for user: " + username);
        currentState = ConnectionState.AUTHENTICATED;
        if (authenticationFuture != null && !authenticationFuture.isDone()) {
            authenticationFuture.complete(true);
        }
    }

    @Override
    public void onAuthenticationFailed(String reason) {
        System.err.println("Authentication failed: " + reason);
        currentState = ConnectionState.CONNECTED; // Back to connected but not authenticated
        lastError = reason;
        if (authenticationFuture != null && !authenticationFuture.isDone()) {
            authenticationFuture.complete(false);
        }
    }

    @Override
    public void onGameJoined(String gameId) {
        System.out.println("Joined game: " + gameId);
        currentState = ConnectionState.IN_GAME;
    }

    @Override
    public void onGameLeft() {
        System.out.println("Left game");
        currentState = ConnectionState.AUTHENTICATED;
    }

    @Override
    public void onError(String errorMessage) {
        System.err.println("Connection error: " + errorMessage);
        currentState = ConnectionState.ERROR;
        lastError = errorMessage;
    }

    // ChatMessageListener implementation
    @Override
    public void onChatMessage(String sender, String message, long timestamp) {
        System.out.println("[CHAT] " + sender + ": " + message);
        // Here you would typically update the UI to show the chat message
    }

    // TradeRequestListener implementation
    @Override
    public void onTradeRequest(String fromPlayer, String toPlayer, String item, int quantity) {
        System.out.println("Trade request from " + fromPlayer + " to " + toPlayer + ": " + quantity + "x " + item);
        // Here you would typically show a trade dialog to the user
    }

    @Override
    public void onTradeResponse(String fromPlayer, String toPlayer, boolean accepted) {
        System.out.println("Trade response from " + fromPlayer + " to " + toPlayer + ": " + (accepted ? "ACCEPTED" : "REJECTED"));
        // Here you would typically update the UI with the trade result
    }

    // GameStateUpdateListener implementation
    @Override
    public void onGameStateUpdate(Object gameState) {
        System.out.println("Game state updated");
        // Here you would typically update the game with the new state
    }

    @Override
    public void onPlayersUpdate(java.util.List<org.example.common.models.Player.Player> players) {
        System.out.println("Players data updated");
        // Here you would typically update the player positions and states
    }

    @Override
    public void onPlayerMove(String username, float x, float y) {
        System.out.println("Player " + username + " moved to (" + x + ", " + y + ")");
        // Here you would typically update the player's position in the game
    }

    // Getters
    public ConnectionState getCurrentState() {
        return currentState;
    }

    public String getLastError() {
        return lastError;
    }

    public boolean isConnected() {
        return currentState == ConnectionState.CONNECTED ||
               currentState == ConnectionState.AUTHENTICATED ||
               currentState == ConnectionState.IN_GAME;
    }

    public boolean isAuthenticated() {
        return currentState == ConnectionState.AUTHENTICATED ||
               currentState == ConnectionState.IN_GAME;
    }

    public boolean isInGame() {
        return currentState == ConnectionState.IN_GAME;
    }

    public User getAuthenticatedUser() {
        return networkClient.getAuthenticatedUser();
    }

    // Utility methods for UI
    public String getConnectionStatusText() {
        switch (currentState) {
            case OFFLINE:
                return "Offline";
            case CONNECTING:
                return "Connecting...";
            case CONNECTED:
                return "Connected";
            case AUTHENTICATING:
                return "Authenticating...";
            case AUTHENTICATED:
                return "Authenticated";
            case IN_GAME:
                return "In Game";
            case ERROR:
                return "Error: " + (lastError != null ? lastError : "Unknown error");
            default:
                return "Unknown";
        }
    }

    public boolean canCreateGame() {
        return currentState == ConnectionState.AUTHENTICATED;
    }

    public boolean canJoinGame() {
        return currentState == ConnectionState.AUTHENTICATED;
    }

    public boolean canSendMessages() {
        return currentState == ConnectionState.AUTHENTICATED || currentState == ConnectionState.IN_GAME;
    }
}
