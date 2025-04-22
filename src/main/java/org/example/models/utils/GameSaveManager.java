package org.example.models.utils;

import org.example.models.common.Date;
import org.example.models.entities.Game;
import org.example.models.common.GameMap;
import org.example.models.Player.Player;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameSaveManager {
    private static final String GAME_SAVE_FILE = "game_save.dat";

    public static boolean saveGameState(Game game) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(GAME_SAVE_FILE))) {
            // Create a map to hold all game state data
            Map<String, Object> gameState = new HashMap<>();

            // Save game date
            gameState.put("date", game.getDate());

            // Save current player
            gameState.put("currentPlayer", game.getCurrentPlayer());

            // Save all players
            gameState.put("players", game.getPlayers());

            // Save game map state
            gameState.put("gameMap", GameMap.getMapState());

            // Save the entire game state map
            out.writeObject(gameState);

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

//    public static Game loadGameState() {
//        File file = new File(GAME_SAVE_FILE);
//        if (!file.exists()) {
//            return null;
//        }
//
//        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
//            // Read the game state map
//            Map<String, Object> gameState = (Map<String, Object>) in.readObject();
//
//            // Extract saved date
//            Date date = (Date) gameState.get("date");
//
//            // Extract players
//            List<Player> players = (List<Player>) gameState.get("players");
//
//            // Create a new game instance with the loaded players
//            Game game = new Game(players);
//
//            // Set the current player
//            Player currentPlayer = (Player) gameState.get("currentPlayer");
//            game.setCurrentPlayer(currentPlayer);
//
//            // Restore date
//            game.setDate(date);
//
//            // Restore game map
//            Map<String, Object> mapState = (Map<String, Object>) gameState.get("gameMap");
//            GameMap.restoreMapState(mapState);
//
//            return game;
//        } catch (IOException | ClassNotFoundException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    public static boolean hasSavedGame() {
        File file = new File(GAME_SAVE_FILE);
        return file.exists();
    }

    public static boolean deleteSavedGame() {
        File file = new File(GAME_SAVE_FILE);
        if (file.exists()) {
            return file.delete();
        }
        return true;
    }
}