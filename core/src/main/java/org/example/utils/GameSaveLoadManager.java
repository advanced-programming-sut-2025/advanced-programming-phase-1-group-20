package org.example.utils;

import org.example.common.models.App;
import org.example.common.models.MapDetails.Farm;
import org.example.common.models.Player.Player;
import org.example.common.models.entities.Game;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GameSaveLoadManager {
    private static final String SAVE_DIRECTORY = "saves/";
    private static final int SAVE_FORMAT_VERSION = 1;

    private static void initializeCollection() {
        File dir = new File(SAVE_DIRECTORY);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static void initialize() {
        initializeCollection();
        System.out.println("GameSaveLoadManager initialized for file-based saves.");
    }

    public static boolean saveCurrentGame() {
        if (App.getGame() != null) {
            Game game = App.getGame();
            game.setSaved(true);
            return saveGameInternal(game, "current_game.sav");
        }
        return false;
    }

    public static boolean autosave() {
        if (App.getGame() != null) {
            return saveGameInternal(App.getGame(), "autosave.sav");
        }
        return false;
    }

    public static boolean saveGameWithName(Game game, String customSaveName) {
        String cleanSaveName = customSaveName.replaceAll("[^a-zA-Z0-9-_]", "_");
        String fileName = cleanSaveName + ".sav";
        boolean saved = saveGameInternal(game, fileName);
        if (saved) {
            game.setSaveName(cleanSaveName);
            if (App.getAllGames() != null && !App.getAllGames().contains(game)) {
                App.getAllGames().add(game);
            }
        }
        return saved;
    }

    private static boolean saveGameInternal(Game game, String fileName) {
        initializeCollection();
        String filePath = SAVE_DIRECTORY + fileName;
        try (FileOutputStream fos = new FileOutputStream(filePath);
             DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(fos))) {
            CustomSaveManager.saveGame(game, filePath);
            System.out.println("Game saved successfully to: " + filePath);
            return true;
        } catch (IOException e) {
            System.err.println("Failed to save game to file: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static Game loadCurrentGame() {
        return loadGame("current_game");
    }

    public static Game loadAutosave() {
        return loadGame("autosave");
    }

    public static Game loadGame(String saveName) {
        initializeCollection();
        String filePath = SAVE_DIRECTORY + saveName + ".sav";
        File file = new File(filePath);
        if (!file.exists()) {
            filePath = SAVE_DIRECTORY + saveName;
            file = new File(filePath);
            if(!file.exists()){
                System.err.println("Save file not found: " + filePath);
                return null;
            }
        }

        try {
            Game game = CustomSaveManager.loadGame(filePath); // Delegate to your custom logic
            if (game != null) {
                // After loading, re-link players to their farms.
                if (game.getGameMap() != null && game.getPlayers() != null) {
                    for (Player p : game.getPlayers()) {
                        Farm farm = game.getGameMap().getFarmByPlayer(p);
                        if (farm != null) {
                            p.setCurrentFarm(farm);
                        }
                    }
                }
                App.setGame(game);
                System.out.println("Game loaded successfully from: " + filePath);
            }else{
                App.setGame(null);
            }
            return game;
        } catch (IOException e) {
            System.err.println("Failed to load game from file: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static List<String> listSavedGames() {
        initializeCollection();
        List<String> saveNames = new ArrayList<>();
        File dir = new File(SAVE_DIRECTORY);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".sav"));

        if (files != null) {
            for (File file : files) {
                String name = file.getName();
                if (!name.equals("current_game.sav") && !name.equals("autosave.sav")) {
                    saveNames.add(name.substring(0, name.length() - 4)); // Remove .sav extension
                }
            }
        }
        return saveNames;
    }

    public static void loadAllGames() {
        List<String> savedGameNames = listSavedGames();
        if (App.getAllGames() != null) {
            App.getAllGames().clear();
        } else {
            App.setAllGames(new ArrayList<>());
        }

        for (String saveName : savedGameNames) {
            Game game = loadGame(saveName);
            if (game != null && !App.getAllGames().contains(game)) {
                App.getAllGames().add(game);
            }
        }
        System.out.println("Loaded all unique games into App from save files.");
    }

    public static boolean deleteSavedGame(String saveName) {
        initializeCollection();
        String filePath = SAVE_DIRECTORY + saveName + ".sav";
        File file = new File(filePath);

        if (file.exists()) {
            if (file.delete()) {
                System.out.println("Saved game '" + saveName + "' deleted successfully.");
                if (App.getAllGames() != null) {
                    App.getAllGames().removeIf(game -> game.getSaveName() != null && game.getSaveName().equals(saveName));
                }
                return true;
            } else {
                System.err.println("Failed to delete saved game file: " + filePath);
                return false;
            }
        } else {
            System.out.println("No game found to delete with name: " + saveName);
            return false;
        }
    }

    public static boolean isGameSaved(Game game) {
        return game != null && game.isSaved();
    }
}
