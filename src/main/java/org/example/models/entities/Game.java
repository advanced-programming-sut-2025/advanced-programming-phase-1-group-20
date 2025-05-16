package org.example.models.entities;

import org.example.models.MapDetails.GameMap;
import org.example.models.Player.Player;
import org.example.models.common.Date;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game implements Serializable {
    private final List<Player> players;
    private Player currentPlayer;
    private Date date;
    private int currentPlayerIndex;
    private boolean inMapSelectionPhase;
    private Map<Player, Integer> mapSelections;
    private Map<Player, Boolean> terminateVotes;
    private Player gameCreator;
    private boolean saved;
    private GameMap gameMap;

    public Game(List<Player> players, Player creator) {
        this.players = players;
        this.gameCreator = creator;
        this.currentPlayerIndex = 0;
        this.currentPlayer = players.get(currentPlayerIndex);
        this.date = new Date();
        this.inMapSelectionPhase = true;
        this.mapSelections = new HashMap<>();
        this.terminateVotes = new HashMap<>();
        this.saved = false;
        this.gameMap = new GameMap();

        for (Player player : players) {
            mapSelections.put(player, -1);
            terminateVotes.put(player, false);
        }

        for (int i = 0; i < players.size(); i++) {
            for (int j = i + 1; j < players.size(); j++) {
                Player player1 = players.get(i);
                Player player2 = players.get(j);
                player1.getFriendship(player2);
            }
        }
    }

    public Date getDate() {
        return date;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public boolean isInMapSelectionPhase() {
        return inMapSelectionPhase;
    }

    public void setMapSelectionPhase(boolean inMapSelectionPhase) {
        this.inMapSelectionPhase = inMapSelectionPhase;
    }

    public boolean allPlayersSelectedMap() {
        for (Player player : players) {
            if (mapSelections.get(player) == -1) {
                return false;
            }
        }
        return true;
    }

    public void selectMap(Player player, int mapNumber) {
        mapSelections.put(player, mapNumber);
    }

    public int getMapSelection(Player player) {
        return mapSelections.getOrDefault(player, -1);
    }

    public void nextTurn(GameMap gameMap) {
        // Reset energy used in the current turn for the current player
        currentPlayer.resetEnergyUsedInTurn();


        //update parts for each turn:
//        updateTurns();

        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        currentPlayer = players.get(currentPlayerIndex);

        // If we've gone through all players, advance the game time by 1 hour
        if (currentPlayerIndex == 0) {
            date.advanceTime(1, gameMap);
        }
    }

    public void updateDailyGame() {
        gameMap.updateDailyGameMap(players);
    }

    public void updateTurns(){
        gameMap.updateTurn(players);
    }

    public void voteToTerminate(Player player, boolean vote) {
        terminateVotes.put(player, vote);
    }

    public boolean allPlayersVotedToTerminate() {
        for (Boolean vote : terminateVotes.values()) {
            if (!vote) {
                return false;
            }
        }
        return true;
    }

    public void resetTerminateVotes() {
        for (Player player : players) {
            terminateVotes.put(player, false);
        }
    }

    public Player getGameCreator() {
        return gameCreator;
    }

    public void setGameCreator(Player player) {
        this.gameCreator = player;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public boolean isPlayerInGame(User user) {
        if (players != null) {
            for (Player player : players) {
                if (player.getUser().equals(user)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Player getPlayer(User user) {
        for (Player player : players) {
            if (player.getUser().equals(user)) {
                return player;
            }
        }
        return null;
    }
}
