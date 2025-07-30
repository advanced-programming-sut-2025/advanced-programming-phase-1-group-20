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
import java.util.HashMap;

public class GameSession {
    private final String sessionId;
    private final Game gameInstance;
    private final Map<String, PlayerConnection> playerConnections;
    private final ScheduledExecutorService gameLoop;
    private final Gson gson;
    private final ServerConfig config;
    private boolean isActive;

    public GameSession(User creator) {
        System.out.println("DEBUG: GameSession constructor called for creator: " + creator.getUsername());
        
        this.sessionId = UUID.randomUUID().toString();
        this.playerConnections = new ConcurrentHashMap<>();
        this.gameLoop = Executors.newSingleThreadScheduledExecutor();
        this.gson = new Gson();
        this.config = ServerConfig.getInstance();
        this.isActive = false;

        // Create game instance with the creator as the first player
        System.out.println("DEBUG: Creating game instance...");
        List<Player> players = new ArrayList<>();
        Player creatorPlayer = new Player(creator);
        players.add(creatorPlayer);

        this.gameInstance = new Game(players, creatorPlayer);
        System.out.println("DEBUG: Game instance created successfully");
        // Don't set the game in App on server side - this is client-side only
        // App.setGame(this.gameInstance);

        System.out.println("DEBUG: Created new game session: " + sessionId + " for user: " + creator.getUsername());
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
        // System.out.println("DEBUG: addPlayer called for user: " + user.getUsername());
        
        if (isFull()) {
            // System.err.println("DEBUG: Game session is full, cannot add player");
            return false;
        }

        // Add player to game instance
        // System.out.println("DEBUG: Adding player to game instance...");
        Player newPlayer = new Player(user);
        boolean addedToGame = gameInstance.addPlayer(newPlayer);
        // System.out.println("DEBUG: Player added to game instance: " + addedToGame);

        // Add connection to session
        System.out.println("DEBUG: Adding connection to session...");
        playerConnections.put(user.getUsername(), connection);
        connection.setGameSession(this);

        // Notify all players about new player
        broadcastPlayerJoined(user.getUsername());

        // System.out.println("DEBUG: Player " + user.getUsername() + " joined game session: " + sessionId);
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
        if (playerConnections.size() < 1) {
            System.out.println("Cannot start game with less than 1 player");
            return;
        }

        this.isActive = true;

        // Don't initialize farms yet - wait for farm selection
        // gameInstance.initializeMultiplayerGame();

        int tickRate = config.getGameTickRate();
        gameLoop.scheduleAtFixedRate(this::gameLoop, 0, 1000 / tickRate, TimeUnit.MILLISECONDS);

        // Send farm selection phase start message
        Message farmSelectionStart = new Message();
        farmSelectionStart.setType(Message.Type.START_GAME);
        farmSelectionStart.putInBody("gameSessionId", sessionId);
        farmSelectionStart.putInBody("message", "Game started! Please select your farm.");
        farmSelectionStart.putInBody("inFarmSelectionPhase", true);
        farmSelectionStart.putInBody("availableFarms", gameInstance.getAvailableFarmIndices());
        farmSelectionStart.putInBody("playerSelections", gameInstance.getPlayerFarmSelections());
        farmSelectionStart.putInBody("playersData", gameInstance.getPlayersData());
        farmSelectionStart.putInBody("gameData", gameInstance.getGameState());
        farmSelectionStart.putInBody("currentPlayerUsername", gameInstance.getCurrentPlayer() != null ? 
            gameInstance.getCurrentPlayer().getUser().getUsername() : null);
        farmSelectionStart.putInBody("playerCount", gameInstance.getPlayerCount());
        farmSelectionStart.putInBody("isActive", false); // Not fully active until farm selection is complete

        broadcastToAll(farmSelectionStart);

        System.out.println("Started game session: " + sessionId + " with " + playerConnections.size() + " players - Farm selection phase");
    }

    public void processMessage(String username, Message message) {
        System.out.println("DEBUG: GameSession.processMessage - username: " + username + ", message type: " + message.getType() + ", isActive: " + isActive);
        
        // Allow SELECT_FARM messages even if game is not fully active (during farm selection phase)
        if (!isActive && message.getType() != Message.Type.START_GAME && message.getType() != Message.Type.SELECT_FARM) {
            System.out.println("DEBUG: Game not active, ignoring message type: " + message.getType());
            return; // Only allow start game and farm selection messages before game is active
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
            case SELECT_FARM:
                System.out.println("DEBUG: Routing SELECT_FARM message to handleFarmSelection");
                handleFarmSelection(username, message);
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

    private void handleFarmSelection(String username, Message message) {
        System.out.println("DEBUG: handleFarmSelection called for username: " + username);
        int farmIndex = message.getIntFromBody("farmIndex");
        System.out.println("DEBUG: Farm index requested: " + farmIndex);
        
        Player player = gameInstance.getPlayerByUsername(username);
        
        if (player == null) {
            System.err.println("DEBUG: Player not found: " + username);
            return;
        }
        System.out.println("DEBUG: Player found: " + player.getUser().getUsername());

        // Check if farm index is valid (0-3)
        if (farmIndex < 0 || farmIndex > 3) {
            System.err.println("DEBUG: Invalid farm index: " + farmIndex);
            sendErrorMessage(username, "Invalid farm index. Must be between 0 and 3.");
            return;
        }

        // Check if farm index is available (allow players to change their own selection)
        if (!gameInstance.isFarmIndexAvailable(farmIndex, player)) {
            System.err.println("DEBUG: Farm index " + farmIndex + " is already taken by another player");
            sendErrorMessage(username, "Farm index " + farmIndex + " is already taken by another player.");
            return;
        }
        System.out.println("DEBUG: Farm index " + farmIndex + " is available");

        // Select the farm for the player
        gameInstance.selectFarm(player, farmIndex);
        System.out.println("DEBUG: Farm " + farmIndex + " selected for player " + username);
        
        // Debug: Check current state
        System.out.println("DEBUG: Current players: " + gameInstance.getPlayers().size());
        System.out.println("DEBUG: Current farm selections: " + gameInstance.getPlayerFarmSelections());
        System.out.println("DEBUG: All players selected farm: " + gameInstance.allPlayersSelectedFarm());
        
        // Create response message
        Message response = new Message();
        response.setType(Message.Type.FARM_SELECTION_UPDATE);
        response.putInBody("username", username);
        response.putInBody("farmIndex", farmIndex);
        response.putInBody("availableFarms", gameInstance.getAvailableFarmIndices());
        response.putInBody("playerSelections", gameInstance.getPlayerFarmSelections());
        
        System.out.println("DEBUG: Broadcasting FARM_SELECTION_UPDATE to all players");
        // Broadcast to all players
        broadcastToAll(response);
        
        // Check if all players have selected farms
        if (gameInstance.allPlayersSelectedFarm()) {
            System.out.println("DEBUG: All players have selected farms, initializing game");
            // Initialize the game with selected farms
            gameInstance.initializeMultiplayerGame();
            
            // Set the game session as fully active
            this.isActive = true;
            
            // Create comprehensive game state for clients
            Map<String, Object> completeGameState = new HashMap<>();
            completeGameState.put("gameSessionId", sessionId);
            completeGameState.put("isActive", true);
            completeGameState.put("playerSelections", gameInstance.getPlayerFarmSelections());
            completeGameState.put("playersData", gameInstance.getPlayersData());
            completeGameState.put("gameData", gameInstance.getGameState());
            completeGameState.put("currentPlayerUsername", gameInstance.getCurrentPlayer() != null ? 
                gameInstance.getCurrentPlayer().getUser().getUsername() : null);
            completeGameState.put("playerCount", gameInstance.getPlayerCount());
            
            // Add all players with their farm assignments
            Map<String, Object> allPlayersInfo = new HashMap<>();
            for (Player p : gameInstance.getPlayers()) {
                Map<String, Object> playerInfo = new HashMap<>();
                playerInfo.put("username", p.getUser().getUsername());
                playerInfo.put("farmIndex", gameInstance.getFarmSelection(p));
                playerInfo.put("farmName", p.getCurrentFarm() != null ? p.getCurrentFarm().getName() : "Unknown");
                playerInfo.put("posX", p.getPosX());
                playerInfo.put("posY", p.getPosY());
                playerInfo.put("energy", p.getEnergy());
                playerInfo.put("money", p.getMoney());
                allPlayersInfo.put(p.getUser().getUsername(), playerInfo);
            }
            completeGameState.put("allPlayersInfo", allPlayersInfo);
            
            Message completeMessage = new Message();
            completeMessage.setType(Message.Type.FARM_SELECTION_COMPLETE);
            completeMessage.putInBody("message", "All players have selected their farms! Game is now starting.");
            completeMessage.putInBody("completeGameState", completeGameState);
            
            broadcastToAll(completeMessage);
            
            // Also send a full game state update to ensure all clients have the complete game state
            broadcastFullGameState();
            
            System.out.println("Farm selection complete - Game fully initialized for session: " + sessionId);
        } else {
            System.out.println("DEBUG: Not all players have selected farms yet");
            System.out.println("DEBUG: Players who haven't selected: ");
            for (Player p : gameInstance.getPlayers()) {
                Integer selection = gameInstance.getFarmSelection(p);
                System.out.println("DEBUG: - " + p.getUser().getUsername() + ": " + selection);
            }
        }
    }

    private void sendErrorMessage(String username, String errorMessage) {
        Message errorMsg = new Message();
        errorMsg.setType(Message.Type.ERROR);
        errorMsg.putInBody("message", errorMessage);
        
        PlayerConnection connection = playerConnections.get(username);
        if (connection != null) {
            connection.sendMessage(errorMsg);
        }
    }

    private void broadcastToAll(Message message) {
        System.out.println("DEBUG: broadcastToAll - message type: " + message.getType() + ", player connections: " + playerConnections.size());
        String messageJson = gson.toJson(message);
        playerConnections.values().forEach(connection -> {
            connection.sendMessage(messageJson);
        });
        System.out.println("DEBUG: broadcastToAll - message sent to all players");
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
