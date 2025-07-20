package org.example.server;

import org.example.common.models.App;
import org.example.common.models.Message;
import org.example.common.models.Player.Player;
import org.example.common.models.entities.Game;
import org.example.common.models.entities.User;
import org.example.server.GameServers.PlayerConnection;
import com.google.gson.Gson;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;

public class GameSession {
    private final String sessionId;
    private final Game gameInstance;
    private final Map<String, PlayerConnection> playerConnections;
    private final ScheduledExecutorService gameLoop;
    private final Gson gson;
    private final ServerConfig config;
    private boolean isActive;

    public GameSession(User creator) {
        this.sessionId = UUID.randomUUID().toString();
        this.playerConnections = new ConcurrentHashMap<>();
        this.gameLoop = Executors.newSingleThreadScheduledExecutor();
        this.gson = new Gson();
        this.config = ServerConfig.getInstance();
        this.isActive = false;

        // Create game instance with the creator as the first player
        List<Player> players = new ArrayList<>();
        Player creatorPlayer = new Player();
        players.add(creatorPlayer);

        this.gameInstance = new Game(players, creatorPlayer);
        App.setGame(this.gameInstance);

        System.out.println("Created new game session: " + sessionId + " for user: " + creator.getUsername());
    }

    public String getSessionId() {
        return sessionId;
    }

    public Game getGameInstance() {
        return gameInstance;
    }

    public boolean isActive() {
        return isActive;
    }

    public int getPlayerCount() {
        return playerConnections.size();
    }

    public boolean isFull() {
        return playerConnections.size() >= config.getMaxPlayersPerGame();
    }

    public boolean addPlayer(PlayerConnection connection, User user) {
        if (isFull()) {
            return false;
        }

        // Add player to game instance
        Player newPlayer = new Player(user);
        gameInstance.addPlayer(newPlayer);

        // Add connection to session
        playerConnections.put(user.getUsername(), connection);
        connection.setGameSession(this);

        // Notify all players about new player
        broadcastPlayerJoined(user.getUsername());

        System.out.println("Player " + user.getUsername() + " joined game session: " + sessionId);
        return true;
    }

    public void removePlayer(String username) {
        PlayerConnection connection = playerConnections.remove(username);
        if (connection != null) {
            // Remove player from game instance
            Player player = gameInstance.getPlayerByUsername(username);
            if (player != null) {
                gameInstance.removePlayer(player);
            }

            // Notify remaining players
            broadcastPlayerLeft(username);

            System.out.println("Player " + username + " left game session: " + sessionId);

            // If no players left, stop the session
            if (playerConnections.isEmpty()) {
                stopSession();
            }
        }
    }

    public void startGame() {
        if (playerConnections.size() < 2) {
            System.out.println("Cannot start game with less than 2 players");
            return;
        }

        this.isActive = true;

        gameInstance.initializeMultiplayerGame();

        int tickRate = config.getGameTickRate();
        gameLoop.scheduleAtFixedRate(this::gameLoop, 0, 1000 / tickRate, TimeUnit.MILLISECONDS);

        // Send full game state to all players
        broadcastFullGameState();

        System.out.println("Started game session: " + sessionId + " with " + playerConnections.size() + " players");
    }

    public void processMessage(String username, Message message) {
        if (!isActive && message.getType() != Message.Type.START_GAME) {
            return; // Only allow start game messages before game is active
        }

        switch (message.getType()) {
            case PLAYER_MOVE:
                handlePlayerMove(username, message);
                break;
            case USE_TOOL:
                handleUseTool(username, message);
                break;
            case PLANT_SEED:
                handlePlantSeed(username, message);
                break;
            case HARVEST_CROP:
                handleHarvestCrop(username, message);
                break;
            case CHAT:
                handleChat(username, message);
                break;
            case TRADE_REQUEST:
                handleTradeRequest(username, message);
                break;
            case START_GAME:
                startGame();
                break;
            default:
                System.out.println("Unhandled message type: " + message.getType());
        }
    }

    private void gameLoop() {
        try {
            // Update game time and state
            gameInstance.updateGameState();

            // Send periodic state updates
            broadcastGameStateUpdate();

        } catch (Exception e) {
            System.err.println("Error in game loop for session " + sessionId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handlePlayerMove(String username, Message message) {
        float x = message.getFromBody("x");
        float y = message.getFromBody("y");

        Player player = gameInstance.getPlayerByUsername(username);
        if (player != null) {
            player.setPosX(x);
            player.setPosY(y);

            // Broadcast movement to other players
            broadcastToOthers(username, message);
        }
    }

    private void handleUseTool(String username, Message message) {
        String toolName = message.getFromBody("tool");
        int targetX = message.getIntFromBody("targetX");
        int targetY = message.getIntFromBody("targetY");
        String direction = message.getFromBody("direction");

        Player player = gameInstance.getPlayerByUsername(username);
        if (player != null) {
            // Execute tool use on server
            player.useTool(direction, gameInstance.getGameMap());

            // Broadcast action to all players
            broadcastToAll(message);
        }
    }

    private void handlePlantSeed(String username, Message message) {
        // Implementation for planting seeds
        broadcastToAll(message);
    }

    private void handleHarvestCrop(String username, Message message) {
        // Implementation for harvesting crops
        broadcastToAll(message);
    }

    private void handleChat(String username, Message message) {
        String chatMessage = message.getFromBody("message");

        // Add sender info
        message.putInBody("sender", username);
        message.putInBody("timestamp", System.currentTimeMillis());

        // Broadcast chat to all players
        broadcastToAll(message);
    }

    private void handleTradeRequest(String username, Message message) {
        String targetPlayer = message.getFromBody("targetPlayer");

        // Send trade request to specific player
        PlayerConnection targetConnection = playerConnections.get(targetPlayer);
        if (targetConnection != null) {
            message.putInBody("fromPlayer", username);
            targetConnection.sendMessage(message);
        }
    }

    private void broadcastToAll(Message message) {
        String messageJson = gson.toJson(message);
        playerConnections.values().forEach(connection -> {
            connection.sendMessage(messageJson);
        });
    }

    private void broadcastToOthers(String excludeUsername, Message message) {
        String messageJson = gson.toJson(message);
        playerConnections.entrySet().stream()
            .filter(entry -> !entry.getKey().equals(excludeUsername))
            .forEach(entry -> entry.getValue().sendMessage(messageJson));
    }

    private void broadcastPlayerJoined(String username) {
        Message message = new Message();
        message.setType(Message.Type.PLAYER_UPDATE);
        message.putInBody("action", "joined");
        message.putInBody("username", username);
        broadcastToAll(message);
    }

    private void broadcastPlayerLeft(String username) {
        Message message = new Message();
        message.setType(Message.Type.PLAYER_UPDATE);
        message.putInBody("action", "left");
        message.putInBody("username", username);
        broadcastToAll(message);
    }

    private void broadcastFullGameState() {
        Message message = new Message();
        message.setType(Message.Type.GAME_STATE_FULL);
        message.putInBody("gameState", gameInstance.getGameState());
        message.putInBody("players", gameInstance.getPlayersData());
        message.putInBody("worldTime", gameInstance.getCurrentDate().toString());
        broadcastToAll(message);
    }

    private void broadcastGameStateUpdate() {
        Message message = new Message();
        message.setType(Message.Type.GAME_STATE_UPDATE);
        message.putInBody("worldTime", gameInstance.getCurrentDate().toString());
        message.putInBody("weather", gameInstance.getCurrentDate().getWeatherToday().toString());
        broadcastToAll(message);
    }

    public void stopSession() {
        this.isActive = false;

        // Stop game loop
        if (gameLoop != null && !gameLoop.isShutdown()) {
            gameLoop.shutdown();
        }

        // Close all connections
        playerConnections.values().forEach(PlayerConnection::disconnect);
        playerConnections.clear();

        System.out.println("Stopped game session: " + sessionId);
    }
}
