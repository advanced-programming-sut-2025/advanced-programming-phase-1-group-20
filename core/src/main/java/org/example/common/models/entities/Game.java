package org.example.common.models.entities;

import org.example.common.models.MapDetails.GameMap;
import org.example.common.models.Player.Player;
import org.example.common.models.common.Date;

import java.io.Serializable; // دیگر نیازی به Serializable نیست اگر Kryo حذف شده است، اما می‌توانید نگه دارید.
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Objects; // برای equals و hashCode

public class Game implements Serializable {
    private List<Player> players;
    private Player currentPlayer;
    private Date date;
    private int currentPlayerIndex;
    private boolean inMapSelectionPhase;
    private Map<Player, Integer> mapSelections = new HashMap<>();
    private Map<Player, Boolean> terminateVotes = new HashMap<>();
    private Player gameCreator;
    private boolean saved;
    private GameMap gameMap;
    private String saveName;


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

        this.inMapSelectionPhase = true;
        this.saved = false;

        if (players != null) {
            for (Player player : players) {
                mapSelections.put(player, -1);
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

    public boolean isInMapSelectionPhase() {
        return inMapSelectionPhase;
    }

    public void setInMapSelectionPhase(boolean inMapSelectionPhase) {
        this.inMapSelectionPhase = inMapSelectionPhase;
    }

    public Map<Player, Integer> getMapSelections() {
        return mapSelections;
    }

    public void setMapSelections(Map<Player, Integer> mapSelections) {
        this.mapSelections = mapSelections;
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


    public boolean allPlayersSelectedMap() {
        if (players == null || mapSelections == null) return false; // Null check
        for (Player player : players) {
            if (mapSelections.getOrDefault(player, -1) == -1) {
                return false;
            }
        }
        return true;
    }

    public void selectMap(Player player, int mapNumber) {
        if (mapSelections != null) {
            mapSelections.put(player, mapNumber);
        }
    }

    public int getMapSelection(Player player) {
        return mapSelections != null ? mapSelections.getOrDefault(player, -1) : -1;
    }

    public void nextTurn(GameMap gameMap) {
        if (currentPlayer != null) { // Null check
            currentPlayer.resetEnergyUsedInTurn();
        }

        if (players != null && !players.isEmpty()) { // Null and empty check
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            currentPlayer = players.get(currentPlayerIndex);
        }
//        if (currentPlayerIndex == 0 && date != null) { // Null check for date
//            date.advanceTime(1, gameMap);
//        }
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
        if (players != null && newPlayer != null && !players.contains(newPlayer)) {
            players.add(newPlayer);

            // Initialize friendship with existing players
            for (Player existingPlayer : players) {
                if (existingPlayer != newPlayer) {
                    existingPlayer.getFriendship(newPlayer);
                    newPlayer.getFriendship(existingPlayer);
                }
            }

            // Initialize map selection and terminate vote
            if (mapSelections != null) {
                mapSelections.put(newPlayer, -1);
            }
            if (terminateVotes != null) {
                terminateVotes.put(newPlayer, false);
            }

            return true;
        }
        return false;
    }

    /**
     * Remove a player from the game
     */
    public boolean removePlayer(Player player) {
        if (players != null && player != null) {
            boolean removed = players.remove(player);

            if (removed) {
                // Clean up player data
                if (mapSelections != null) {
                    mapSelections.remove(player);
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

        // Initialize farms for all players
        if (players != null) {
            for (int i = 0; i < players.size(); i++) {
                Player player = players.get(i);
                if (player != null) {
                    // Create farm for each player
                    org.example.common.models.MapDetails.Farm farm =
                        new org.example.common.models.MapDetails.Farm(
                            player.getUser().getUsername() + "'s Farm",
                            player,
                            i == 0, // First player gets main farm
                            i
                        );
                    player.setCurrentFarm(farm);
                    gameMap.addFarm(farm);
                }
            }
        }

        // Initialize game map
        if (gameMap.getVillage() != null) {
            gameMap.getVillage().initializeNPCs();
        }

        // End map selection phase
        this.inMapSelectionPhase = false;
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
        state.put("inMapSelectionPhase", inMapSelectionPhase);
        state.put("playerCount", players != null ? players.size() : 0);
        state.put("gameTime", date != null ? date.toString() : null);
        state.put("weather", date != null ? date.getWeatherToday().toString() : null);
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
     * Get current date (alias for getDate() for consistency)
     */
    public Date getCurrentDate() {
        return this.date;
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
}
