package org.example.server;

import org.example.common.models.App;
import org.example.common.models.MapDetails.Farm;
import org.example.common.models.Message;
import org.example.common.models.Player.Player;
import org.example.common.models.Player.Skill;
import org.example.common.models.entities.Game;
import org.example.common.models.entities.User;
import org.example.common.models.entities.animal.Animal;
import org.example.server.GameServers.PlayerConnection;
import org.example.common.models.enums.Weather;
import org.example.common.models.MapDetails.GameMap;
import com.google.gson.Gson;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameSession {
    private final String sessionId;
    private final Game gameInstance;
    private final Map<String, PlayerConnection> playerConnections;
    private final ScheduledExecutorService gameLoop;
    private final Gson gson;
    private final ServerConfig config;
    private boolean isActive;
    private final Map<String, Long> lastMovementTime; // Track last movement time per player
    private int gameTickCounter; // Track game ticks for time synchronization
    private Weather lastBroadcastedWeather; // Track last broadcasted weather to avoid duplicate broadcasts
    private Random animalAiRandom = new Random();

    public GameSession(User creator) {
        System.out.println("DEBUG: GameSession constructor called for creator: " + (creator != null ? creator.getUsername() : "null"));

        try {
            this.sessionId = UUID.randomUUID().toString();
            this.playerConnections = new ConcurrentHashMap<>();
            this.gameLoop = Executors.newSingleThreadScheduledExecutor();
            this.gson = new Gson();
            this.config = ServerConfig.getInstance();
            this.isActive = false;
            this.lastMovementTime = new ConcurrentHashMap<>();
            this.gameTickCounter = 0;
            this.lastBroadcastedWeather = null; // Initialize lastBroadcastedWeather

            System.out.println("DEBUG: GameSession initialized with sessionId: " + sessionId);

            // Create game instance with the creator as the first player
            System.out.println("DEBUG: Creating game instance...");
            List<Player> players = new ArrayList<>();
            Player creatorPlayer = new Player(creator);
            players.add(creatorPlayer);

            this.gameInstance = new Game(players, creatorPlayer);
            this.gameInstance.isMultiplayer = true; // Mark as multiplayer game on server
            System.out.println("DEBUG: Game instance created successfully");
            // Set the game in App for server-side access
            App.setGame(this.gameInstance);
            App.getGame().isMultiplayer = true; // Mark as multiplayer game on server

            // Start the game loop immediately - time should advance from the beginning
            int tickRate = config.getGameTickRate();
            System.out.println("DEBUG: Starting game loop immediately with tick rate: " + tickRate + " ticks per second");
            gameLoop.scheduleAtFixedRate(this::gameLoop, 0, 1000 / tickRate, TimeUnit.MILLISECONDS);
            System.out.println("DEBUG: Game loop started successfully");

            System.out.println("DEBUG: Created new game session: " + sessionId + " for user: " + creator.getUsername());
        } catch (Exception e) {
            System.err.println("DEBUG: Exception in GameSession constructor: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw to let caller handle
        }
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
        boolean addedToGame = App.getGame().addPlayer(newPlayer);
        // System.out.println("DEBUG: Player added to game instance: " + addedToGame);

        // Add connection to session
        System.out.println("DEBUG: Adding connection to session...");
        playerConnections.put(user.getUsername(), connection);
        connection.setGameSession(this);

        // Notify all players about new player
        broadcastPlayerJoined(user.getUsername());

        // Send immediate game state update for new player
        broadcastGameStateUpdate();

        // System.out.println("DEBUG: Player " + user.getUsername() + " joined game session: " + sessionId);
        return true;
    }

    public void removePlayer(String username) {
        PlayerConnection connection = playerConnections.remove(username);
        if (connection != null) {
            // Remove player from game instance
            Player player = App.getGame().getPlayerByUsername(username);
            if (player != null) {
                App.getGame().removePlayer(player);
            }

            // Clean up movement tracking
            lastMovementTime.remove(username);

            // Notify remaining players
            broadcastPlayerLeft(username);

            // Send immediate game state update for player leaving
            broadcastGameStateUpdate();

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
        System.out.println("DEBUG: Game session started - isActive: " + isActive);

        // Don't initialize farms yet - wait for farm selection
        // gameInstance.initializeMultiplayerGame();

        // Send farm selection phase start message
        Message farmSelectionStart = new Message();
        farmSelectionStart.setType(Message.Type.START_GAME);
        farmSelectionStart.putInBody("gameSessionId", sessionId);
        farmSelectionStart.putInBody("message", "Game started! Please select your farm.");
        farmSelectionStart.putInBody("inFarmSelectionPhase", true);
        farmSelectionStart.putInBody("availableFarms", App.getGame().getAvailableFarmIndices());
        farmSelectionStart.putInBody("playerSelections", App.getGame().getPlayerFarmSelections());
        farmSelectionStart.putInBody("playersData", App.getGame().getPlayersData());
        farmSelectionStart.putInBody("gameData", App.getGame().getGameState());
        // Don't send a specific current player username - each client will set their own
        farmSelectionStart.putInBody("currentPlayerUsername", null);
        farmSelectionStart.putInBody("playerCount", App.getGame().getPlayerCount());
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
            gameTickCounter++;

            // Always log the first few ticks to confirm the game loop is running
            if (gameTickCounter <= 5) {
                System.out.println("DEBUG: Game loop started - tick " + gameTickCounter + " - isActive: " + isActive);
            }

            // Debug: Log game loop execution
            if (gameTickCounter % 10 == 0) { // Log every 10 ticks to avoid spam
                System.out.println("DEBUG: Game loop tick " + gameTickCounter +
                    " - isActive: " + isActive +
                    " - Date: " + (App.getGame().getCurrentDate() != null ? App.getGame().getCurrentDate().getCurrentTimeString() : "null") +
                    " - GameMap: " + (App.getGame().getGameMap() != null ? "initialized" : "null") +
                    " - Time advancement rate: " + config.getTimeAdvancementRate() + " minutes per tick");
            }

            // Advance time on server (this is the single source of truth for time)
            // Time advancement should happen regardless of isActive status
            if (App.getGame().getCurrentDate() != null) {
                int timeAdvancementRate = config.getTimeAdvancementRate();
                // Advance time by configured minutes per game tick
                // Pass null for GameMap if it's not initialized yet
                GameMap gameMap = App.getGame().getGameMap();
                App.getGame().getCurrentDate().advanceMinutes(timeAdvancementRate, gameMap);

                if (gameTickCounter % 10 == 0) { // Log every 10 ticks
                    System.out.println("DEBUG: Server advanced time by " + timeAdvancementRate + " minutes - " +
                        App.getGame().getCurrentDate().getCurrentTimeString() +
                        " (Weather: " + App.getGame().getCurrentDate().getWeatherToday() + ")");
                }
                updateAnimalAI(1.0f / config.getGameTickRate());
            } else {
                System.out.println("DEBUG: Server game loop - current date is null");
            }

            // Update game time and state
            App.getGame().updateGameState();

            // Broadcast time updates to all clients periodically
            // This ensures all clients stay synchronized with server time
            int timeSyncInterval = config.getTimeSyncInterval();
            if (gameTickCounter % timeSyncInterval == 0) {
                broadcastGameStateUpdate();
                System.out.println("DEBUG: Broadcasted time update to all clients - " +
                    App.getGame().getCurrentDate().getCurrentTimeString() +
                    " (Weather: " + App.getGame().getCurrentDate().getWeatherToday() + ")");
            }

            // Also broadcast weather updates immediately when weather changes
            // This ensures all clients see weather changes at the same time
            if (App.getGame().getCurrentDate() != null) {
                Weather currentWeather = App.getGame().getCurrentDate().getWeatherToday();
                if (currentWeather != null) {
                    // Store the last broadcasted weather to detect changes
                    if (lastBroadcastedWeather == null || !lastBroadcastedWeather.equals(currentWeather)) {
                        broadcastWeatherUpdate();
                        lastBroadcastedWeather = currentWeather;
                        System.out.println("DEBUG: Weather changed to " + currentWeather + " - broadcasting to all clients");
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error in game loop for session " + sessionId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handlePlayerMove(String username, Message message) {
        float x = message.getFloatFromBody("x");
        float y = message.getFloatFromBody("y");

        Player player = App.getGame().getPlayerByUsername(username);
        if (player != null) {
            // Check movement throttling to prevent excessive updates
            long currentTime = System.currentTimeMillis();
            Long lastTime = lastMovementTime.get(username);
            int throttleMs = config.getMovementUpdateThrottle();

            if (lastTime != null && (currentTime - lastTime) < throttleMs) {
                // Skip update if too soon since last movement
                System.out.println("🚀 SERVER: Skipping movement update for " + username + " due to throttling");
                return;
            }

            System.out.println("🚀 SERVER: Processing movement for " + username + " to (" + x + ", " + y + ")");
            System.out.println("🚀 SERVER: Player energy before movement: " + player.getEnergy());
            System.out.println("🚀 SERVER: Player energy unlimited: " + player.isEnergyUnlimited());

            // Update player position on server
            player.setPosX(x);
            player.setPosY(y);
            player.getLocation().setxAxis((int) x);
            player.getLocation().setyAxis((int) y);

            // Consume energy on server side to stay synchronized with client
            if (!player.isEnergyUnlimited()) {
                // Calculate energy cost (same logic as client)
                int currentEnergy = player.getEnergy();
                int energyCost = Math.max(1, currentEnergy * 5 / 10000); // 0.05% of current energy

                // Ensure we don't consume more than 1 energy for very low energy levels
                if (currentEnergy < 2000 && energyCost > 1) {
                    energyCost = 1;
                }

                // Always consume at least 1 energy for movement
                energyCost = Math.max(1, energyCost);

                System.out.println("🚀 SERVER: Energy calculation - Current: " + currentEnergy + ", Cost: " + energyCost);

                if (player.getEnergy() >= energyCost) {
                    player.decreaseEnergy(energyCost);
                    System.out.println("🚀 SERVER: Player " + username + " energy consumed: " + energyCost + ", Remaining: " + player.getEnergy());
                } else {
                    System.out.println("🚀 SERVER: Player " + username + " not enough energy for movement");
                }
            } else {
                System.out.println("🚀 SERVER: Player " + username + " has unlimited energy - no consumption");
            }

            // Add username to message for client identification
            message.putInBody("username", username);
            message.putInBody("timestamp", currentTime);

            // Broadcast movement to other players immediately
            broadcastToOthers(username, message);

            // Only send comprehensive player data updates periodically, not on every movement
            // This prevents large JSON messages from being sent too frequently
            if (gameTickCounter % 30 == 0) { // Send comprehensive update every 30 ticks
                System.out.println("🚀 SERVER: Broadcasting comprehensive player data update");
                broadcastPlayerDataUpdate();
            }

            // Update last movement time
            lastMovementTime.put(username, currentTime);

            System.out.println("🚀 SERVER: Player " + username + " moved to (" + x + ", " + y + ") - Broadcasting to " +
                (playerConnections.size() - 1) + " other players");
        } else {
            System.err.println("DEBUG: Player " + username + " not found in game instance");
        }
    }

    private void broadcastPlayerDataUpdate() {
        Message playerDataMessage = new Message();
        playerDataMessage.setType(Message.Type.PLAYER_DATA_UPDATE);

        // Create focused player data with only essential information
        Map<String, Object> allPlayersData = new HashMap<>();
        for (Player p : App.getGame().getPlayers()) {
            System.out.println("🚀 SERVER: Preparing player data for " + p.getUser().getUsername() + " - Energy: " + p.getEnergy());

            Map<String, Object> playerInfo = new HashMap<>();
            playerInfo.put("username", p.getUser().getUsername());
            playerInfo.put("posX", p.getPosX());
            playerInfo.put("posY", p.getPosY());
            playerInfo.put("energy", p.getEnergy());
            playerInfo.put("money", p.getMoney());
            playerInfo.put("isInVillage", p.getIsInVillage());

            // Add farm information (essential for game state)
            if (p.getCurrentFarm() != null) {
                playerInfo.put("farmIndex", p.getCurrentFarm().getFarmIndex());
                playerInfo.put("farmName", p.getCurrentFarm().getName());
            } else {
                playerInfo.put("farmIndex", -1);
                playerInfo.put("farmName", "No Farm");
            }

            // Add location information (essential for rendering)
            if (p.getLocation() != null) {
                playerInfo.put("locationX", p.getLocation().getX());
                playerInfo.put("locationY", p.getLocation().getY());
                playerInfo.put("tileType", p.getLocation().getTile().toString());
            }

            // Only include current tool if it's different from default
            if (p.getCurrentTool() != null && !p.getCurrentTool().getName().equals("Basic Hoe")) {
                Map<String, Object> toolInfo = new HashMap<>();
                toolInfo.put("name", p.getCurrentTool().getName());
                toolInfo.put("type", p.getCurrentTool().getType().toString());
                playerInfo.put("currentTool", toolInfo);
            }

            allPlayersData.put(p.getUser().getUsername(), playerInfo);
        }

        playerDataMessage.putInBody("players", allPlayersData);
        playerDataMessage.putInBody("timestamp", System.currentTimeMillis());

        // Broadcast to all players
        broadcastToAll(playerDataMessage);

        System.out.println("🔄 SERVER: Broadcasted focused player data update for " + allPlayersData.size() + " players");
    }

    private void handleUseTool(String username, Message message) {
        String toolName = message.getFromBody("tool");
        int targetX = message.getIntFromBody("targetX");
        int targetY = message.getIntFromBody("targetY");
        String direction = message.getFromBody("direction");

        Player player = App.getGame().getPlayerByUsername(username);
        if (player != null) {
            // Execute tool use on server
            player.useTool(direction, App.getGame().getGameMap());

            // Broadcast action to all players immediately
            broadcastToAll(message);

            // Send immediate game state update for tool effects
            broadcastGameStateUpdate();

            // Send player data update to sync any energy/money changes
            broadcastPlayerDataUpdate();
        }
    }

    private void handlePlantSeed(String username, Message message) {
        // Implementation for planting seeds
        broadcastToAll(message);

        // Send immediate game state update for planted seeds
        broadcastGameStateUpdate();
    }

    private void handleHarvestCrop(String username, Message message) {
        // Implementation for harvesting crops
        broadcastToAll(message);

        // Send immediate game state update for harvested crops
        broadcastGameStateUpdate();
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

        Player player = App.getGame().getPlayerByUsername(username);

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
        if (!App.getGame().isFarmIndexAvailable(farmIndex, player)) {
            System.err.println("DEBUG: Farm index " + farmIndex + " is already taken by another player");
            sendErrorMessage(username, "Farm index " + farmIndex + " is already taken by another player.");
            return;
        }
        System.out.println("DEBUG: Farm index " + farmIndex + " is available");

        // Select the farm for the player
        App.getGame().selectFarm(player, farmIndex);
        System.out.println("DEBUG: Farm " + farmIndex + " selected for player " + username);

        // Debug: Check current state
        System.out.println("DEBUG: Current players: " + App.getGame().getPlayers().size());
        System.out.println("DEBUG: Current farm selections: " + App.getGame().getPlayerFarmSelections());
        System.out.println("DEBUG: All players selected farm: " + App.getGame().allPlayersSelectedFarm());

        // Create response message
        Message response = new Message();
        response.setType(Message.Type.FARM_SELECTION_UPDATE);
        response.putInBody("username", username);
        response.putInBody("farmIndex", farmIndex);
        response.putInBody("availableFarms", App.getGame().getAvailableFarmIndices());
        response.putInBody("playerSelections", App.getGame().getPlayerFarmSelections());

        System.out.println("DEBUG: Broadcasting FARM_SELECTION_UPDATE to all players");
        // Broadcast to all players
        broadcastToAll(response);

        // Send immediate game state update for farm selection
        broadcastGameStateUpdate();

        // Check if all players have selected farms
        if (App.getGame().allPlayersSelectedFarm()) {
            System.out.println("DEBUG: All players have selected farms, initializing game");
            System.out.println("DEBUG: Final farm selections: " + App.getGame().getPlayerFarmSelections());

            // Initialize the game with selected farms
            App.getGame().initializeMultiplayerGame();

            // Set the game session as fully active
            this.isActive = true;

            // Send immediate full game state update for game initialization
            broadcastFullGameState();

            // Create comprehensive game state for clients
            Map<String, Object> completeGameState = new HashMap<>();
            completeGameState.put("gameSessionId", sessionId);
            completeGameState.put("isActive", true);
            completeGameState.put("inFarmSelectionPhase", false); // Explicitly mark as not in farm selection
            completeGameState.put("playerSelections", App.getGame().getPlayerFarmSelections());
            completeGameState.put("playersData", App.getGame().getPlayersData());
            completeGameState.put("gameData", App.getGame().getGameState());
            completeGameState.put("dateState", App.getGame().getCurrentDate().getDateState());
            // Don't send a specific current player username - each client will set their own
            completeGameState.put("currentPlayerUsername", null);
            completeGameState.put("playerCount", App.getGame().getPlayerCount());

            // Add all players with their farm assignments and current state
            Map<String, Object> allPlayersInfo = new HashMap<>();
            for (Player p : App.getGame().getPlayers()) {
                Map<String, Object> playerInfo = new HashMap<>();
                playerInfo.put("username", p.getUser().getUsername());
                playerInfo.put("farmIndex", App.getGame().getFarmSelection(p));
                playerInfo.put("farmName", p.getCurrentFarm() != null ? p.getCurrentFarm().getName() : "Unknown");
                playerInfo.put("posX", p.getPosX());
                playerInfo.put("posY", p.getPosY());
                playerInfo.put("energy", p.getEnergy());
                playerInfo.put("money", p.getMoney());

                playerInfo.put("isCurrentPlayer", false);
                allPlayersInfo.put(p.getUser().getUsername(), playerInfo);

                System.out.println("DEBUG: Player " + p.getUser().getUsername() + " - Farm: " +
                    App.getGame().getFarmSelection(p) + ", Position: (" + p.getPosX() + ", " + p.getPosY() +
                    "), Energy: " + p.getEnergy() + ", Money: " + p.getMoney());
            }
            completeGameState.put("allPlayersInfo", allPlayersInfo);

            Message completeMessage = new Message();
            completeMessage.setType(Message.Type.FARM_SELECTION_COMPLETE);
            completeMessage.putInBody("message", "All players have selected their farms! Game is now starting.");
            completeMessage.putInBody("completeGameState", completeGameState);
            completeMessage.putInBody("isActive", true);
            completeMessage.putInBody("inFarmSelectionPhase", false);

            System.out.println("DEBUG: Broadcasting FARM_SELECTION_COMPLETE to all players");
            String messageJson = gson.toJson(completeMessage);
            System.out.println("DEBUG: FARM_SELECTION_COMPLETE message JSON length: " + messageJson.length());
            System.out.println("DEBUG: FARM_SELECTION_COMPLETE message JSON (first 500 chars): " + messageJson.substring(0, Math.min(500, messageJson.length())));

            // Check if message is too large for WebSocket frame
            if (messageJson.length() > 65536) { // 64KB limit for WebSocket frames
                System.err.println("WARNING: FARM_SELECTION_COMPLETE message is too large (" + messageJson.length() + " bytes)");
                System.err.println("This may cause WebSocket frame fragmentation issues");
            }

            broadcastToAll(completeMessage);

            // Also send a full game state update to ensure all clients have the complete game state
            broadcastFullGameState();

            System.out.println("Farm selection complete - Game fully initialized for session: " + sessionId);
            System.out.println("DEBUG: Game is now active and ready for gameplay");
        } else {
            System.out.println("DEBUG: Not all players have selected farms yet");
            System.out.println("DEBUG: Players who haven't selected: ");
            for (Player p : App.getGame().getPlayers()) {
                Integer selection = App.getGame().getFarmSelection(p);
                if (selection == -1) {
                    System.out.println("DEBUG: - " + p.getUser().getUsername() + " (no selection)");
                } else {
                    System.out.println("DEBUG: - " + p.getUser().getUsername() + " (farm " + selection + ")");
                }
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

    public void broadcastToOthers(String excludeUsername, Message message) {
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
        message.putInBody("gameState", App.getGame().getGameState());
        message.putInBody("players", App.getGame().getPlayersData());
        message.putInBody("dateState", App.getGame().getCurrentDate().getDateState());
        broadcastToAll(message);
    }

    private void broadcastGameStateUpdate() {
        Message message = new Message();
        message.setType(Message.Type.GAME_STATE_UPDATE);
        message.putInBody("dateState", App.getGame().getCurrentDate().getDateState());
        message.putInBody("weather", App.getGame().getCurrentDate().getWeatherToday().toString());
        broadcastToAll(message);
    }

    private void broadcastWeatherUpdate() {
        Message message = new Message();
        message.setType(Message.Type.WEATHER_UPDATE);
        message.putInBody("weather", App.getGame().getCurrentDate().getWeatherToday().toString());
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


    private void updateAnimalAI(float deltaTime) {
        if (gameInstance == null || gameInstance.getGameMap() == null) return;

        List<Animal> allAnimals = new ArrayList<>();
        for (Farm farm : gameInstance.getGameMap().getFarms()) {
            farm.getBarns().forEach(barn -> allAnimals.addAll(barn.getAnimals()));
            farm.getCoops().forEach(coop -> allAnimals.addAll(coop.getAnimals()));
        }

        for (Animal animal : allAnimals) {
            // Update the state timer
            animal.setStateTimer(animal.getStateTimer() - deltaTime);

            // Time to change state
            if (animal.getStateTimer() <= 0) {
                // 50% chance to start moving, 50% to stay idle
                if (animalAiRandom.nextBoolean()) {
                    animal.setMoving(true);

                    // Pick a new target location within 5 tiles
                    double angle = animalAiRandom.nextDouble() * 2 * Math.PI;
                    double distance = animalAiRandom.nextDouble() * 5 * 60; // Max 5 tiles distance

                    float targetX = (float) (animal.getPosX() + Math.cos(angle) * distance);
                    float targetY = (float) (animal.getPosY() + Math.sin(angle) * distance);

                    // TODO: Add boundary checks to keep animals within a fenced area

                    animal.setTargetX(targetX);
                    animal.setTargetY(targetY);
                } else {
                    animal.setMoving(false);
                }
                // Reset timer for next state change (e.g., 2 to 5 seconds)
                animal.setStateTimer(2 + animalAiRandom.nextFloat() * 3);
            }

            // If moving, update position towards target
            if (animal.isMoving()) {
                float currentX = animal.getPosX();
                float currentY = animal.getPosY();
                float targetX = animal.getTargetX();
                float targetY = animal.getTargetY();

                float dx = targetX - currentX;
                float dy = targetY - currentY;

                // Stop if close to the target
                if (Math.abs(dx) < 1 && Math.abs(dy) < 1) {
                    animal.setMoving(false);
                } else {
                    // Normalize direction vector
                    float length = (float) Math.sqrt(dx * dx + dy * dy);
                    float moveX = (dx / length) * animal.getSpeed() * deltaTime;
                    float moveY = (dy / length) * animal.getSpeed() * deltaTime;

                    animal.setPosX(currentX + moveX);
                    animal.setPosY(currentY + moveY);

                    // Update facing direction for animation
                    if (Math.abs(dx) > Math.abs(dy)) {
                        animal.setFacing(dx > 0 ? Animal.Direction.RIGHT : Animal.Direction.LEFT);
                    } else {
                        animal.setFacing(dy > 0 ? Animal.Direction.UP : Animal.Direction.DOWN);
                    }
                }
            }
        }
    }
}
