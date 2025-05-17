package org.example.controllers;

import org.example.models.App;
import org.example.models.utils.GameSaveLoadManager;

/**
 * Controller class for handling game save and load operations.
 */
public class GameSaveLoadController {

    /**
     * Saves the current game with automatic timestamp.
     *
     * @param saveName Optional custom name for the save
     * @return A message indicating success or failure
     */
    public static String saveGame(String saveName) {
        if (App.getGame() == null) {
            return "Error: No active game to save.";
        }

        boolean success;
        if (saveName != null && !saveName.trim().isEmpty()) {
            success = GameSaveLoadManager.saveGameWithName(App.getGame(), saveName);
        } else {
            success = GameSaveLoadManager.saveCurrentGame();
        }

        if (success) {
            return "Game saved successfully.";
        } else {
            return "Error: Failed to save the game.";
        }
    }

    /**
     * Loads a game from a saved file.
     *
     * @param saveName Name of the save to load, or null for current game
     * @return A message indicating success or failure
     */
    public static String loadGame(String saveName) {
        if (saveName != null && !saveName.trim().isEmpty()) {
            String filePath = "saved_games/" + saveName + ".bin";
            if (GameSaveLoadManager.loadGame(filePath) != null) {
                return "Game '" + saveName + "' loaded successfully.";
            } else {
                return "Error: Failed to load game '" + saveName + "'.";
            }
        } else {
            if (GameSaveLoadManager.loadCurrentGame() != null) {
                return "Current game loaded successfully.";
            } else {
                return "Error: No current game found to load.";
            }
        }
    }

    /**
     * Lists all available saved games.
     *
     * @return A formatted string listing all saved games
     */
    public static String listSavedGames() {
        var savedGames = GameSaveLoadManager.listSavedGames();

        if (savedGames.isEmpty()) {
            return "No saved games found.";
        }

        StringBuilder sb = new StringBuilder("Available saved games:\n");
        for (int i = 0; i < savedGames.size(); i++) {
            String filename = savedGames.get(i).getName();
            String nameWithoutExtension = filename.substring(0, filename.lastIndexOf('.'));
            sb.append(i + 1).append(". ").append(nameWithoutExtension).append("\n");
        }

        return sb.toString();
    }

    /**
     * Deletes a saved game.
     *
     * @param saveName Name of the save to delete
     * @return A message indicating success or failure
     */
    public static String deleteSavedGame(String saveName) {
        if (saveName == null || saveName.trim().isEmpty()) {
            return "Error: Invalid save name.";
        }

        String filePath = "saved_games/" + saveName + ".bin";
        boolean success = GameSaveLoadManager.deleteSavedGame(filePath);

        if (success) {
            return "Saved game '" + saveName + "' deleted successfully.";
        } else {
            return "Error: Failed to delete saved game '" + saveName + "'.";
        }
    }
}