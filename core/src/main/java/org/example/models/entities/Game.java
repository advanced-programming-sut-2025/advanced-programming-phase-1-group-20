package org.example.models.entities;

import org.example.models.MapDetails.GameMap;
import org.example.models.Player.Player;
import org.example.models.common.Date;
import org.example.models.enums.Seasons; // اگر این‌ها فیلدهای شما هستند باید اضافه شوند
import org.example.models.enums.Weather; // اگر این‌ها فیلدهای شما هستند باید اضافه شوند

import java.io.Serializable; // دیگر نیازی به Serializable نیست اگر Kryo حذف شده است، اما می‌توانید نگه دارید.
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList; // اگر players همیشه باید مقداردهی اولیه شود

import java.util.Objects; // برای equals و hashCode

public class Game implements Serializable {
    private List<Player> players;
    private Player currentPlayer;
    private Date date;
    private int currentPlayerIndex;
    private boolean inMapSelectionPhase;
    private Map<Player, Integer> mapSelections;
    private Map<Player, Boolean> terminateVotes;
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
}
