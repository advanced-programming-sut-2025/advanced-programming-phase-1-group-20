package org.example.models.utils;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.example.models.App;
import org.example.models.entities.Game;
import org.example.models.savegame.GameSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This utility class manages saving and loading game data
 * using Kryo serialization.
 */
public class GameSaveLoadManager {
    // Directory where saves are stored
    private static final String SAVE_DIRECTORY = "saved_games";

    // File extension for saved games
    private static final String SAVE_EXTENSION = ".bin";

    // Current game file name
    private static final String CURRENT_GAME_FILE = SAVE_DIRECTORY + "/current_game" + SAVE_EXTENSION;

    // Autosave file name
    private static final String AUTOSAVE_FILE = SAVE_DIRECTORY + "/autosave" + SAVE_EXTENSION;

    /**
     * Initializes the save directory structure if it doesn't exist.
     */
    public static void initialize() {
        File directory = new File(SAVE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    /**
     * Saves the current game to the default location.
     *
     * @return true if save was successful, false otherwise
     */
    public static boolean saveCurrentGame() {
        if (App.getGame() != null) {
            App.getGame().setSaved(true);
            return saveGame(App.getGame(), CURRENT_GAME_FILE);
        }
        return false;
    }

    /**
     * Performs an autosave of the current game.
     *
     * @return true if autosave was successful, false otherwise
     */
    public static boolean autosave() {
        if (App.getGame() != null) {
            return saveGame(App.getGame(), AUTOSAVE_FILE);
        }
        return false;
    }

    /**
     * Saves a game with a custom name.
     *
     * @param game     The game to save
     * @param saveName The name for the save file (without extension)
     * @return true if save was successful, false otherwise
     */
    public static boolean saveGameWithName(Game game, String saveName) {
        // Sanitize the filename to remove any invalid characters
        saveName = saveName.replaceAll("[^a-zA-Z0-9-_]", "_");
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = SAVE_DIRECTORY + "/" + saveName + "_" + timestamp + SAVE_EXTENSION;

        boolean saved = saveGame(game, filename);
        if (saved) {
            // Add the game to the list of all games if it's not already there
            if (!App.getAllGames().contains(game)) {
                App.getAllGames().add(game);
            }
        }
        return saved;
    }

    /**
     * Core method for saving a game to a specified file.
     *
     * @param game     The game to save
     * @param filePath The path where the game should be saved
     * @return true if save was successful, false otherwise
     */
    public static boolean saveGame(Game game, String filePath) {
        Kryo kryo = GameSerializer.createKryo();
        try (Output output = new Output(new FileOutputStream(filePath))) {
            kryo.writeObject(output, game);
            System.out.println("Game saved successfully to: " + filePath);
            return true;
        } catch (IOException e) {
            System.err.println("Failed to save game: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Loads the current game from the default location.
     *
     * @return The loaded game, or null if loading failed
     */
    public static Game loadCurrentGame() {
        File file = new File(CURRENT_GAME_FILE);
        if (file.exists()) {
            Game game = loadGame(CURRENT_GAME_FILE);
            if (game != null) {
                App.setGame(game);
                return game;
            }
        }
        return null;
    }

    /**
     * Loads an autosaved game.
     *
     * @return The loaded game, or null if loading failed
     */
    public static Game loadAutosave() {
        File file = new File(AUTOSAVE_FILE);
        if (file.exists()) {
            Game game = loadGame(AUTOSAVE_FILE);
            if (game != null) {
                App.setGame(game);
                return game;
            }
        }
        return null;
    }

    /**
     * Loads a game from a specific file.
     *
     * @param filePath The path to the saved game file
     * @return The loaded game, or null if loading failed
     */
    public static Game loadGame(String filePath) {
        Kryo kryo = GameSerializer.createKryo();
        try (Input input = new Input(new FileInputStream(filePath))) {
            Game game = kryo.readObject(input, Game.class);
            return game;
        } catch (IOException e) {
            System.err.println("Failed to load game: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lists all available saved games.
     *
     * @return A list of saved game files
     */
    public static List<File> listSavedGames() {
        List<File> savedGames = new ArrayList<>();
        File directory = new File(SAVE_DIRECTORY);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles((dir, name) -> name.endsWith(SAVE_EXTENSION) &&
                    !name.equals("current_game" + SAVE_EXTENSION) &&
                    !name.equals("autosave" + SAVE_EXTENSION));
            if (files != null) {
                for (File file : files) {
                    savedGames.add(file);
                }
            }
        }

        return savedGames;
    }

    /**
     * Loads all saved games into the App's game list.
     */
    public static void loadAllGames() {
        App.getAllGames().clear();
        File directory = new File(SAVE_DIRECTORY);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles((dir, name) -> name.endsWith(SAVE_EXTENSION));
            if (files != null) {
                for (File file : files) {
                    Game game = loadGame(file.getPath());
                    if (game != null) {
                        App.getAllGames().add(game);
                    }
                }
            }
        }
    }

    /**
     * Deletes a saved game.
     *
     * @param filePath The path to the saved game file
     * @return true if deletion was successful, false otherwise
     */
    public static boolean deleteSavedGame(String filePath) {
        try {
            Path path = Paths.get(filePath);
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            System.err.println("Failed to delete saved game: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if a game is saved.
     *
     * @param game The game to check
     * @return true if the game is saved, false otherwise
     */
    public static boolean isGameSaved(Game game) {
        return game != null && game.isSaved();
    }
}