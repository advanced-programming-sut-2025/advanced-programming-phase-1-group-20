package org.example.controllers;

import org.example.common.models.App;
import org.example.utils.GameSaveLoadManager;
import org.example.common.models.entities.Game; // اضافه شده برای استفاده از شیء Game

import java.util.List; // اضافه شده برای listSavedGames

/**
 * Controller class for handling game save and load operations.
 */
public class GameSaveLoadController {

    /**
     * Saves the current game.
     * If saveName is provided, it saves with that custom name.
     * Otherwise, it updates the "current_game" save.
     *
     * @param saveName Optional custom name for the save. Can be null or empty for "current_game" update.
     * @return A message indicating success or failure
     */
    public static String saveGame(String saveName) {
        Game gameToSave = App.getGame();
        if (gameToSave == null) {
            return "Error: No active game to save.";
        }

        boolean success;
        if (saveName != null && !saveName.trim().isEmpty()) {
            // اگر نام سفارشی داده شده، بازی را با آن نام ذخیره کن.
            // GameSaveLoadManager.saveGameWithName() اکنون saveName را در شیء Game هم تنظیم می‌کند.
            success = GameSaveLoadManager.saveGameWithName(gameToSave, saveName.trim());
        } else {
            // اگر نامی داده نشده، "current_game" را به‌روزرسانی کن.
            // GameSaveLoadManager.saveCurrentGame() نام "current_game" را در شیء Game تنظیم می‌کند.
            success = GameSaveLoadManager.saveCurrentGame();
        }

        if (success) {
            return "Game saved successfully.";
        } else {
            return "Error: Failed to save the game.";
        }
    }

    /**
     * Loads a game from MongoDB.
     *
     * @param saveName Name of the save to load. If null or empty, attempts to load "current_game".
     * @return A message indicating success or failure
     */
    public static String loadGame(String saveName) {
        Game loadedGame;
        if (saveName != null && !saveName.trim().isEmpty()) {
            // بارگذاری بازی با نام سفارشی
            loadedGame = GameSaveLoadManager.loadGame(saveName.trim());
            if (loadedGame != null) {
                // App.setGame() و App.allGames() در GameSaveLoadManager.loadGame() مدیریت می‌شوند.
                return "Game '" + saveName.trim() + "' loaded successfully.";
            } else {
                return "Error: Failed to load game '" + saveName.trim() + "'. Save not found or corrupted.";
            }
        } else {
            // بارگذاری "current_game"
            loadedGame = GameSaveLoadManager.loadCurrentGame();
            if (loadedGame != null) {
                // App.setGame() و App.allGames() در GameSaveLoadManager.loadCurrentGame() مدیریت می‌شوند.
                return "Current game loaded successfully.";
            } else {
                return "Error: No current game found to load.";
            }
        }
    }

    /**
     * Lists all available saved games (excluding "current_game" and "autosave").
     *
     * @return A formatted string listing all saved games
     */
    public static String listSavedGames() {
        List<String> savedGameNames = GameSaveLoadManager.listSavedGames();

        if (savedGameNames.isEmpty()) {
            return "No custom saved games found.";
        }

        StringBuilder sb = new StringBuilder("Available saved games:\n");
        for (int i = 0; i < savedGameNames.size(); i++) {
            sb.append(i + 1).append(". ").append(savedGameNames.get(i)).append("\n");
        }

        return sb.toString();
    }

    /**
     * Deletes a saved game from MongoDB.
     *
     * @param saveName Name of the save to delete
     * @return A message indicating success or failure
     */
    public static String deleteSavedGame(String saveName) {
        if (saveName == null || saveName.trim().isEmpty()) {
            return "Error: Invalid save name. Please provide a name to delete.";
        }

        // GameSaveLoadManager.deleteSavedGame اکنون فقط نام ذخیره را می‌پذیرد.
        boolean success = GameSaveLoadManager.deleteSavedGame(saveName.trim());

        if (success) {
            return "Saved game '" + saveName.trim() + "' deleted successfully.";
        } else {
            return "Error: Failed to delete saved game '" + saveName.trim() + "'. Save not found.";
        }
    }
}
