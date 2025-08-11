package org.example.client.controllers;

import org.example.common.models.App;
import org.example.utils.GameSaveLoadManager;
import org.example.common.models.entities.Game;

import java.util.List;


public class GameSaveLoadController {
    public static String saveGame(String saveName) {
        Game gameToSave = App.getGame();
        if (gameToSave == null) {
            return "Error: No active game to save.";
        }

        boolean success;
        if (saveName != null && !saveName.trim().isEmpty()) {
            success = GameSaveLoadManager.saveGameWithName(gameToSave, saveName.trim());
        } else {
            success = GameSaveLoadManager.saveCurrentGame();
        }

        if (success) {
            return "Game saved successfully.";
        } else {
            return "Error: Failed to save the game.";
        }
    }

    public static String loadGame(String saveName) {
        Game loadedGame;
        if (saveName != null && !saveName.trim().isEmpty()) {
            loadedGame = GameSaveLoadManager.loadGame(saveName.trim());
            if (loadedGame != null) {
                return "Game '" + saveName.trim() + "' loaded successfully.";
            } else {
                return "Error: Failed to load game '" + saveName.trim() + "'. Save not found or corrupted.";
            }
        } else {
            loadedGame = GameSaveLoadManager.loadCurrentGame();
            if (loadedGame != null) {
                return "Current game loaded successfully.";
            } else {
                return "Error: No current game found to load.";
            }
        }
    }

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

    public static String deleteSavedGame(String saveName) {
        if (saveName == null || saveName.trim().isEmpty()) {
            return "Error: Invalid save name. Please provide a name to delete.";
        }

        boolean success = GameSaveLoadManager.deleteSavedGame(saveName.trim());

        if (success) {
            return "Saved game '" + saveName.trim() + "' deleted successfully.";
        } else {
            return "Error: Failed to delete saved game '" + saveName.trim() + "'. Save not found.";
        }
    }
}
