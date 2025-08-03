package org.example.common.models.entities;

import org.example.common.models.MapDetails.Farm;
import org.example.common.models.MapDetails.GameMap;
import org.example.common.models.MapDetails.Building;
import org.example.common.models.common.Location;
import org.example.common.models.enums.Types.TileType;
import org.example.common.models.Player.Player;
import org.example.common.models.common.Date;

import java.io.Serializable; // دیگر نیازی به Serializable نیست اگر Kryo حذف شده است، اما می‌توانید نگه دارید.
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import java.util.Objects; // برای equals و hashCode

public class Game implements Serializable {
    private List<Player> players;
    private Player currentPlayer;
    private Date date;
    private int currentPlayerIndex;
    private boolean inFarmSelectionPhase;
    private Map<Player, Integer> farmSelections = new HashMap<>();
    private Map<Player, Boolean> terminateVotes = new HashMap<>();
    private Player gameCreator;
    private boolean saved;
    private GameMap gameMap;
    private String saveName;
    public boolean isMultiplayer = false;


    public Game() {

    }

    public Game(List<Player> players, Player creator) {
        this.players = players;
        this.gameCreator = creator;
        this.currentPlayerIndex = 0;
        this.date = new Date();
        if (players != null && !players.isEmpty()) {
            this.currentPlayer = players.get(currentPlayerIndex);
        }

        this.inFarmSelectionPhase = true;

        this.saved = false;


        if (players != null) {
            for (Player player : players) {
                farmSelections.put(player, -1);
                terminateVotes.put(player, false);
            }

            for (int i = 0; i < players.size(); i++) {
                for (int j = i + 1; j < players.size(); j++) {
                    Player player1 = players.get(i);
                    Player player2 = players.get(j);
                    // مطمئن شوید getFriendship(Player) در Player به درستی کار می‌کند
                    player1.getFriendship(player2);
                }
            }
        }
    }

    // Getters and Setters for all fields (بسیار مهم برای Gson)

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        this.currentPlayerIndex = currentPlayerIndex;
    }

    public boolean isInFarmSelectionPhase() {
        return inFarmSelectionPhase;
    }

    public void setInFarmSelectionPhase(boolean inFarmSelectionPhase) {
        this.inFarmSelectionPhase = inFarmSelectionPhase;
    }

    public Map<Player, Integer> getFarmSelections() {
        return farmSelections;
    }

    public void setFarmSelections(Map<Player, Integer> farmSelections) {
        this.farmSelections = farmSelections;
    }

    public Map<Player, Boolean> getTerminateVotes() {
        return terminateVotes;
    }

    public void setTerminateVotes(Map<Player, Boolean> terminateVotes) {
        this.terminateVotes = terminateVotes;
    }

    public Player getGameCreator() {
        return gameCreator;
    }

    public void setGameCreator(Player gameCreator) {
        this.gameCreator = gameCreator;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    // **** متد getSaveName() و setSaveName() ****
    public String getSaveName() {
        return saveName;
    }

    public void setSaveName(String saveName) {
        this.saveName = saveName;
    }


    public boolean allPlayersSelectedFarm() {
        System.out.println("DEBUG: allPlayersSelectedFarm called");
        System.out.println("DEBUG: players: " + players);
        System.out.println("DEBUG: farmSelections: " + farmSelections);
        System.out.println("DEBUG: players size: " + (players != null ? players.size() : "null"));
        System.out.println("DEBUG: farmSelections size: " + (farmSelections != null ? farmSelections.size() : "null"));

        if (players == null || farmSelections == null) {
            System.out.println("DEBUG: players or farmSelections is null");
            return false; // Null check
        }

        for (Player player : players) {
            System.out.println("DEBUG: Checking player: " + player.getUser().getUsername());
            System.out.println("DEBUG: Player object: " + player);
            System.out.println("DEBUG: Player hash code: " + player.hashCode());

            Integer selection = farmSelections.getOrDefault(player, -1);
            System.out.println("DEBUG: Player " + player.getUser().getUsername() + " selection: " + selection);

            // Check if the player exists in farmSelections
            boolean playerInSelections = farmSelections.containsKey(player);
            System.out.println("DEBUG: Player " + player.getUser().getUsername() + " in farmSelections: " + playerInSelections);

            if (selection == -1) {
                System.out.println("DEBUG: Player " + player.getUser().getUsername() + " has not selected a farm yet");
                return false;
            }
        }
        System.out.println("DEBUG: All players have selected farms");
        return true;
    }

    public void selectFarm(Player player, int farmNumber) {
        System.out.println("DEBUG: selectFarm called for player " + player.getUser().getUsername() + " with farm " + farmNumber);
        if (farmSelections != null) {
            farmSelections.put(player, farmNumber);
            System.out.println("DEBUG: Farm selection stored. Current selections: " + farmSelections);
        } else {
            System.out.println("DEBUG: farmSelections is null!");
        }
    }

    public int getFarmSelection(Player player) {
        return farmSelections != null ? farmSelections.getOrDefault(player, -1) : -1;
    }

    /**
     * Check if a farm index is available for selection
     */
    public boolean isFarmIndexAvailable(int farmIndex) {
        return isFarmIndexAvailable(farmIndex, null);
    }

    /**
     * Check if a farm index is available for selection (allows player to change their own selection)
     */
    public boolean isFarmIndexAvailable(int farmIndex, Player requestingPlayer) {
        System.out.println("DEBUG: isFarmIndexAvailable called for farm index: " + farmIndex + " by player: " +
            (requestingPlayer != null ? requestingPlayer.getUser().getUsername() : "null"));
        System.out.println("DEBUG: Current farm selections: " + farmSelections);

        if (farmSelections == null) {
            System.out.println("DEBUG: farmSelections is null, farm " + farmIndex + " is available");
            return true;
        }

        for (Map.Entry<Player, Integer> entry : farmSelections.entrySet()) {
            Player player = entry.getKey();
            Integer selectedIndex = entry.getValue();
            if (selectedIndex != null && selectedIndex == farmIndex) {
                // If the requesting player is the same as the player who selected this farm, allow it
                if (requestingPlayer != null && player.equals(requestingPlayer)) {
                    System.out.println("DEBUG: Farm " + farmIndex + " is available (player changing their own selection)");
                    return true;
                } else {
                    System.out.println("DEBUG: Farm " + farmIndex + " is not available (selected by " +
                        (player != null ? player.getUser().getUsername() : "unknown player") + ")");
                    return false;
                }
            }
        }
        System.out.println("DEBUG: Farm " + farmIndex + " is available");
        return true;
    }

    /**
     * Get list of available farm indices
     */
    public List<Integer> getAvailableFarmIndices() {
        List<Integer> available = new ArrayList<>();
        for (int i = 0; i <= 3; i++) {
            if (isFarmIndexAvailable(i)) {
                available.add(i);
            }
        }
        return available;
    }

    /**
     * Get map of player usernames to their selected farm indices
     */
    public Map<String, Integer> getPlayerFarmSelections() {
        System.out.println("DEBUG: getPlayerFarmSelections called");
        System.out.println("DEBUG: farmSelections: " + farmSelections);

        Map<String, Integer> selections = new HashMap<>();
        if (farmSelections != null) {
            for (Map.Entry<Player, Integer> entry : farmSelections.entrySet()) {
                Player player = entry.getKey();
                Integer farmIndex = entry.getValue();
                System.out.println("DEBUG: Processing player: " + (player != null ? player.getUser().getUsername() : "null") +
                    ", farmIndex: " + farmIndex);
                if (player != null && player.getUser() != null && farmIndex != null) {
                    selections.put(player.getUser().getUsername(), farmIndex);
                    System.out.println("DEBUG: Added selection for " + player.getUser().getUsername() + ": " + farmIndex);
                }
            }
        }
        System.out.println("DEBUG: Final selections map: " + selections);
        return selections;
    }

    public void nextTurn(GameMap gameMap) {
        System.out.println("DEBUG: nextTurn called");
        System.out.println("DEBUG: Current player before turn change: " + (currentPlayer != null ? currentPlayer.getUser().getUsername() : "null"));
        System.out.println("DEBUG: Current player index before turn change: " + currentPlayerIndex);
        System.out.println("DEBUG: Total players: " + (players != null ? players.size() : "null"));
        System.out.println("DEBUG: isMultiplayer = " + isMultiplayer);

        if (currentPlayer != null) { // Null check
            currentPlayer.resetEnergyUsedInTurn();
            System.out.println("DEBUG: Reset energy used in turn for: " + currentPlayer.getUser().getUsername());
        }

        if (players != null && !players.isEmpty()) { // Null and empty check
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            currentPlayer = players.get(currentPlayerIndex);
            System.out.println("Turn advanced to player: " + currentPlayer.getUser().getUsername() + " (index: " + currentPlayerIndex + ")");
            System.out.println("DEBUG: New current player farm: " + (currentPlayer.getCurrentFarm() != null ? currentPlayer.getCurrentFarm().getName() : "null"));
            System.out.println("DEBUG: New current player position: " + currentPlayer.getPosX() + ", " + currentPlayer.getPosY());
            System.out.println("DEBUG: New current player is in village: " + currentPlayer.getIsInVillage());

            // Notify the WorldController to update the PlayerController
            // This will make the camera follow the new player
            try {
                if (com.badlogic.gdx.Gdx.files != null) { // Check if we're in client environment
                    org.example.client.views.GameView gameView = (org.example.client.views.GameView) org.example.client.Main.getGame().getScreen();
                    if (gameView != null && gameView.getController() != null) {
                        org.example.client.controllers.GameMenuController gameMenuController = gameView.getController();
                        System.out.println("DEBUG: Found GameMenuController: " + (gameMenuController != null ? "not null" : "null"));

                        // Update the GameMenuController's player reference
                        gameMenuController.updatePlayer();

                        // Update the WorldController's PlayerController
                        org.example.client.controllers.gameplay.WorldController worldController = gameMenuController.getWorldController();
                        System.out.println("DEBUG: Found WorldController: " + (worldController != null ? "not null" : "null"));
                        if (worldController != null) {
                            worldController.updatePlayerController();
                        }
                    } else {
                        System.out.println("DEBUG: GameView or controller is null");
                    }
                } else {
                    System.out.println("DEBUG: Not in client environment (Gdx.files is null)");
                }
            } catch (Exception e) {
                System.out.println("Could not update PlayerController: " + e.getMessage());
                e.printStackTrace();
            }
        }
//        if (currentPlayerIndex == 0 && date != null) { // Null check for date
//            date.advanceTime(1, gameMap);
//        }
    }

    /**
     * Check if current player is out of energy and automatically advance turn
     * if this is not a multiplayer game
     */
    public boolean checkAndAdvanceTurnIfEnergyDepleted() {
        System.out.println("DEBUG: checkAndAdvanceTurnIfEnergyDepleted called");
        System.out.println("DEBUG: currentPlayer = " + (currentPlayer != null ? currentPlayer.getUser().getUsername() : "null"));
        System.out.println("DEBUG: gameMap = " + (gameMap != null ? "not null" : "null"));
        System.out.println("DEBUG: isMultiplayer = " + isMultiplayer);

        if (currentPlayer == null || gameMap == null) {
            System.out.println("DEBUG: Early return due to null currentPlayer or gameMap");
            return false;
        }

        // For single-player games (including "Try Game" mode), always advance turns
        // For multiplayer games, don't auto-advance (players should manually advance)
        if (isMultiplayer) {
            System.out.println("Multiplayer game detected - not auto-advancing turn when energy depleted");
            return false;
        }

        System.out.println("DEBUG: Checking if player is out of energy for turn...");
        System.out.println("DEBUG: Player energy: " + currentPlayer.getEnergy());
        System.out.println("DEBUG: Player energy used this turn: " + currentPlayer.getEnergyUsedInTurn());
        System.out.println("DEBUG: Player can use 1 energy: " + currentPlayer.canUseEnergy(1));
        System.out.println("DEBUG: Player is out of energy for turn: " + currentPlayer.isOutOfEnergyForTurn());

        if (currentPlayer.isOutOfEnergyForTurn()) {
            System.out.println("Player " + currentPlayer.getUser().getUsername() + " is out of energy for the turn. Advancing to next player.");
            nextTurn(gameMap);
            return true;
        } else {
            System.out.println("DEBUG: Player is not out of energy for turn");
        }

        return false;
    }

    public void updateDailyGame() {
        if (gameMap != null && players != null) { // Null check
            gameMap.updateDailyGameMap(players);
        }
    }

    public void updateTurns() {
        if (gameMap != null && players != null) { // Null check
            gameMap.updateTurn(players);
        }
    }

    public void voteToTerminate(Player player, boolean vote) {
        if (terminateVotes != null) { // Null check
            terminateVotes.put(player, vote);
        }
    }

    public boolean allPlayersVotedToTerminate() {
        if (terminateVotes == null) return false; // Null check
        for (Boolean vote : terminateVotes.values()) {
            if (!vote) {
                return false;
            }
        }
        return true;
    }

    public void resetTerminateVotes() {
        if (players != null && terminateVotes != null) { // Null check
            for (Player player : players) {
                terminateVotes.put(player, false);
            }
        }
    }

    public boolean isPlayerInGame(User user) {
        if (players != null && user != null) {
            for (Player player : players) {
                if (player != null && player.getUser() != null && player.getUser().equals(user)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Player getPlayer(User user) {
        if (players != null && user != null) {
            for (Player player : players) {
                if (player != null && player.getUser() != null && player.getUser().equals(user)) {
                    return player;
                }
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game1 = (Game) o;
        return Objects.equals(saveName, game1.saveName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(saveName);
    }

    // ===== MULTIPLAYER SUPPORT METHODS =====
    /**
     * Find a player by their username
     */

    public Player getPlayerByUsername(String username) {
        if (players != null && username != null) {
            for (Player player : players) {
                if (player != null && player.getUser() != null &&
                    username.equals(player.getUser().getUsername())) {
                    return player;
                }
            }
        }
        return null;
    }

    /**
     * Add a new player to the game
     */
    public boolean addPlayer(Player newPlayer) {
        System.out.println("DEBUG: Game.addPlayer called for player: " + (newPlayer != null ? newPlayer.getUser().getUsername() : "null"));
        System.out.println("DEBUG: Current players list: " + (players != null ? players.size() : "null"));
        
        try {
            if (players == null) {
                System.err.println("DEBUG: Players list is null, initializing...");
                players = new ArrayList<>();
            }
            
            if (newPlayer == null) {
                System.err.println("DEBUG: New player is null");
                return false;
            }
            
            if (newPlayer.getUser() == null) {
                System.err.println("DEBUG: New player's user is null");
                return false;
            }
            
            if (players.contains(newPlayer)) {
                System.err.println("DEBUG: Player already exists in game");
                return false;
            }
            
            System.out.println("DEBUG: Adding player to game instance...");
            players.add(newPlayer);

            // Initialize friendship with existing players
            for (Player existingPlayer : players) {
                if (existingPlayer != newPlayer && existingPlayer != null) {
                    try {
                        existingPlayer.getFriendship(newPlayer);
                        newPlayer.getFriendship(existingPlayer);
                    } catch (Exception e) {
                        System.err.println("DEBUG: Error initializing friendship: " + e.getMessage());
                    }
                }
            }

            // Initialize map selection and terminate vote
            if (farmSelections != null) {
                farmSelections.put(newPlayer, -1);
            }
            if (terminateVotes != null) {
                terminateVotes.put(newPlayer, false);
            }

            System.out.println("DEBUG: Player successfully added to game. Total players: " + players.size());
            return true;
        } catch (Exception e) {
            System.err.println("DEBUG: Exception in Game.addPlayer: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Remove a player from the game
     */
    public boolean removePlayer(Player player) {
        if (players != null && player != null) {
            boolean removed = players.remove(player);

            if (removed) {
                // Clean up player data
                if (farmSelections != null) {
                    farmSelections.remove(player);
                }
                if (terminateVotes != null) {
                    terminateVotes.remove(player);
                }

                // Update current player if needed
                if (currentPlayer == player && !players.isEmpty()) {
                    currentPlayerIndex = 0;
                    currentPlayer = players.get(0);
                }

                return true;
            }
        }
        return false;
    }

    /**
     * Initialize the game for multiplayer mode
     */
    public void initializeMultiplayerGame() {
        if (gameMap == null) {
            gameMap = new GameMap();
        }

        // Set multiplayer flag
        this.isMultiplayer = true;

        // Initialize farms for all players based on their selections
        if (players != null) {
            for (Player player : players) {
                if (player != null) {
                    Integer farmIndex = farmSelections.get(player);
                    if (farmIndex != null && farmIndex >= 0 && farmIndex <= 3) {
                        // Create farm for player based on their selection
                        Farm farm =
                            new Farm(
                                player.getUser().getUsername() + "'s Farm",
                                player,
                                farmIndex == 0, // Farm 0 is considered the main farm
                                farmIndex
                            );
                        player.setCurrentFarm(farm);
                        gameMap.addFarm(farm);
                        System.out.println("DEBUG: Created farm " + farmIndex + " for player " + player.getUser().getUsername());
                    }
                }
            }
        }

        // Position players in their farms using global coordinates
        if (players != null) {
            for (Player player : players) {
                if (player != null && player.getCurrentFarm() != null) {
                    Farm farm = player.getCurrentFarm();
                    int farmIndex = farm.getFarmIndex();

                    // Get the farm's building location
                    Building building = farm.getBuilding();
                    int houseCenterX = building.getX() + building.getWidth() / 2;
                    int houseCenterY = building.getY() + building.getHeight() / 2;

                    // Calculate global coordinates based on farm index
                    int globalStartX = 0, globalStartY = 0;
                    switch (farmIndex) {
                        case 0: // Top-Left
                            globalStartX = 0;
                            globalStartY = 0;
                            break;
                        case 1: // Bottom-Left
                            globalStartX = 0;
                            globalStartY = 78;
                            break;
                        case 2: // Top-Right
                            globalStartX = 156;
                            globalStartY = 0;
                            break;
                        case 3: // Bottom-Right
                            globalStartX = 156;
                            globalStartY = 78;
                            break;
                    }

                    // Position player near the house in global coordinates
                    int playerStartX = globalStartX + houseCenterX;
                    int playerStartY = globalStartY + houseCenterY - 3; // 3 tiles below house center

                    // Ensure player is within farm boundaries
                    if (playerStartY < globalStartY) {
                        playerStartY = globalStartY + houseCenterY + 3;
                    }
                    if (playerStartX < globalStartX) {
                        playerStartX = globalStartX + houseCenterX + 3;
                    }
                    if (playerStartX >= globalStartX + Farm.width) {
                        playerStartX = globalStartX + houseCenterX - 3;
                    }
                    if (playerStartY >= globalStartY + Farm.height) {
                        playerStartY = globalStartY + houseCenterY - 3;
                    }

                    // Create global location and set player position
                    Location globalLocation = new Location(playerStartX, playerStartY, TileType.Dirt);
                    player.setLocation(globalLocation);
                    player.setIsInVillage(false);

                    System.out.println("DEBUG: Positioned player " + player.getUser().getUsername() +
                        " at global coordinates (" + playerStartX + ", " + playerStartY + ") in farm " + farmIndex);
                }
            }
        }

        // Initialize game map
        System.out.println("Game.initializeMultiplayerGame(): About to initialize NPCs...");
        if (gameMap.getVillage() != null) {
            System.out.println("Game.initializeMultiplayerGame(): Village is not null, calling initializeNPCs()");
            gameMap.getVillage().initializeNPCs();
        } else {
            System.out.println("Game.initializeMultiplayerGame(): Village is null, cannot initialize NPCs");
        }

        // Update global game map tiles from all farms
        gameMap.updateTilesFromRegions();

        // End farm selection phase
        this.inFarmSelectionPhase = false;

        // In multiplayer mode, don't set a specific current player here
        // Each client will set their own current player based on their user
        // The currentPlayer will be set by the client when they create their game instance
        System.out.println("DEBUG: Multiplayer game initialized with " + (players != null ? players.size() : 0) + " players");
        System.out.println("DEBUG: Current player will be set by each client individually");
    }

    /**
     * Update game state for multiplayer (called periodically)
     */
    public void updateGameState() {
        if (date != null && gameMap != null) {
            // Update game time and world state
            updateTurns();
            updateDailyGame();
        }
    }

    /**
     * Get current game state as a map for serialization
     */
    public Map<String, Object> getGameState() {
        Map<String, Object> state = new HashMap<>();

        state.put("currentPlayerIndex", currentPlayerIndex);
        state.put("currentPlayerUsername", currentPlayer != null && currentPlayer.getUser() != null ?
            currentPlayer.getUser().getUsername() : null);
        state.put("inFarmSelectionPhase", inFarmSelectionPhase);
        state.put("playerCount", players != null ? players.size() : 0);
        state.put("gameTime", date != null ? date.toString() : null);
        state.put("weather", date != null ? date.getWeatherToday().toString() : null);
        state.put("dateState", date != null ? date.getDateState() : null);
        state.put("saved", saved);

        return state;
    }

    /**
     * Get players data for network transmission
     */
    public Map<String, Object> getPlayersData() {
        Map<String, Object> playersData = new HashMap<>();

        if (players != null) {
            for (Player player : players) {
                if (player != null && player.getUser() != null) {
                    String username = player.getUser().getUsername();
                    Map<String, Object> playerData = new HashMap<>();

                    playerData.put("username", username);
                    playerData.put("posX", player.getPosX());
                    playerData.put("posY", player.getPosY());
                    playerData.put("energy", player.getEnergy());
                    playerData.put("money", player.getMoney());

                    // Add current tool info
                    if (player.getCurrentTool() != null) {
                        playerData.put("currentTool", player.getCurrentTool().getName());
                    }

                    playersData.put(username, playerData);
                }
            }
        }

        return playersData;
    }

    /**
     * Sync farm selections from server data
     */
    public void syncFarmSelectionsFromServer(Map<String, Integer> serverSelections) {
        System.out.println("DEBUG: syncFarmSelectionsFromServer called with: " + serverSelections);
        if (serverSelections != null && players != null) {
            for (Player player : players) {
                if (player != null && player.getUser() != null) {
                    String username = player.getUser().getUsername();
                    Integer farmIndex = serverSelections.get(username);
                    if (farmIndex != null) {
                        farmSelections.put(player, farmIndex);
                        System.out.println("DEBUG: Synced farm selection for " + username + ": " + farmIndex);

                        // Validate that the farm index is valid
                        if (farmIndex >= 0 && farmIndex <= 3) {
                            System.out.println("DEBUG: Farm selection " + farmIndex + " is valid for " + username);
                        } else {
                            System.err.println("DEBUG: Invalid farm index " + farmIndex + " for " + username);
                        }
                    } else {
                        System.out.println("DEBUG: No farm selection found for " + username);
                    }
                }
            }

            // Log final state
            System.out.println("DEBUG: Final farm selections after sync: " + farmSelections);
            System.out.println("DEBUG: All players selected farm: " + allPlayersSelectedFarm());
        } else {
            System.err.println("DEBUG: Cannot sync farm selections - serverSelections: " + serverSelections + ", players: " + players);
        }
    }

    /**
     * Get current date (alias for getDate() for consistency)
     */
    public Date getCurrentDate() {
        return this.date;
    }

    /**
     * Sync date from server data
     */
    public void syncDateFromServer(Map<String, Object> serverDateState) {
        System.out.println("DEBUG: Game.syncDateFromServer called with: " + serverDateState);
        if (serverDateState != null) {
            // Ensure date is initialized
            if (this.date == null) {
                System.out.println("DEBUG: Creating new Date object for synchronization");
                this.date = new Date();
            }
            this.date.syncFromServer(serverDateState);
            System.out.println("DEBUG: Date synchronized - Current time: " + this.date.getCurrentTimeString());
        } else {
            System.out.println("DEBUG: syncDateFromServer called with null serverDateState");
        }
    }

    /**
     * Check if game is ready to start (has enough players)
     */
    public boolean isReadyToStart() {
        return players != null && players.size() >= 2 && players.size() <= 4;
    }

    /**
     * Get the number of players in the game
     */
    public int getPlayerCount() {
        return players != null ? players.size() : 0;
    }

    /**
     * Sync game state from server data
     */
    public void syncGameStateFromServer(Map<String, Object> serverGameState) {
        if (serverGameState == null) {
            return;
        }

        // Sync date if present
        if (serverGameState.containsKey("dateState")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> dateState = (Map<String, Object>) serverGameState.get("dateState");
            syncDateFromServer(dateState);
        }

        // Sync other game state as needed
        // This can be expanded to sync other game properties
    }
}
