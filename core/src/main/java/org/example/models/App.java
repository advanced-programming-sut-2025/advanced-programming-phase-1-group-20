package org.example.models;

import org.example.controllers.NPCController;
import org.example.models.Items.Item;
import org.example.models.Player.Player;
import org.example.models.entities.Game;
import org.example.models.entities.User;
import org.example.models.utils.FileStorage;
import org.example.models.utils.GameSaveLoadManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {
    // File paths for saving games (now delegated to GameSaveLoadManager)

    // Static structure for saving App Data
    private static Map<String, User> users = new HashMap<>();
    private static User loggedInUser;
    private static Map<Integer, String> securityQuestions = new HashMap<>();
    private static boolean dataLoaded = false;
    private static List<Game> allGames = new ArrayList<>();
    private static Game currentGame;
    private static boolean allChose = false;

    // Lists for game
    private static List<Item> items = new ArrayList<>();

    private static boolean isMapSelectionPhase = false;

    public static void initialize() {
        if (!dataLoaded) {
            users = FileStorage.loadUsers();
            System.out.println("Loaded users: " + users.size());
            addSecurityQuestion();

            items = FileStorage.loadItems();

            // Initialize save directory
            GameSaveLoadManager.initialize();

            // Load all saved games
            GameSaveLoadManager.loadAllGames();

            NPCController.initialize();

            dataLoaded = true;

            // Check if there is an autosave that might need to be recovered
            checkForAutosaveRecovery();
        }
    }

    /**
     * Checks if there is an autosave file that might need to be recovered
     * This is useful after a crash or unexpected shutdown
     */
    private static void checkForAutosaveRecovery() {
        File autosaveFile = new File("saved_games/autosave.bin");
        if (autosaveFile.exists()) {
            // Check if the autosave is more recent than the current game
            File currentGameFile = new File("saved_games/current_game.bin");

            if (!currentGameFile.exists() || autosaveFile.lastModified() > currentGameFile.lastModified()) {
                // Autosave is more recent or current game doesn't exist
                System.out.println("Found more recent autosave. Use loadAutosave() to recover it.");
            }
        }
    }

    public static void saveData() {
        FileStorage.saveUsers(users);
        saveAllGames();
    }

    public static void saveAllGames() {
        // Save all games to files
        for (Game game : allGames) {
            if (game != null) {
                GameSaveLoadManager.saveGameWithName(game, "game_" + allGames.indexOf(game));
            }
        }
    }

    public static void saveCurrentGame() {
        if (currentGame != null) {
            currentGame.setSaved(true);
            GameSaveLoadManager.saveCurrentGame();
        }
    }

    public static void loadAllGames() {
        GameSaveLoadManager.loadAllGames();
    }

    public static Game loadCurrentGame() {
        Game game = GameSaveLoadManager.loadCurrentGame();
        if (game != null) {
            currentGame = game;
            return game;
        }
        return null;
    }

    public static void addUser(User user) {
        users.put(user.getUsername(), user);
        // Save data whenever a user is added
        saveData();
    }

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    public static void setLoggedInUser(User user) {
        loggedInUser = user;
    }

    public static void logout() {
        loggedInUser = null;
        org.example.models.utils.AutoLoginUtil.clearAutoLogin();
    }

    public static User getUser(String username) {
        for (User user : users.values()) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public static Map<String, User> getUsers() {
        return users;
    }

    public static void removeUser(User user) {
        users.remove(user.getUsername());
    }

    public static void addSecurityQuestion() {
        securityQuestions.put(0, "Whats was the name of your best friend in high school");
        securityQuestions.put(1, "In which city did your parents meet?");
        securityQuestions.put(2, "Whats your favorite band of music?"); // guns 'n roses
        securityQuestions.put(3, "Whats your favorite programming language?");
    }

    public static String getSecurityQuestion(int index) {
        return securityQuestions.get(index);
    }

    public static List<String> getSecurityQuestions() {
        return (List<String>) securityQuestions.values();
    }

    public static Item getItem(String itemName) {
        return items.stream().filter(item -> item.getName().equals(itemName))
                .findFirst().orElse(null);
    }

    public static Game getGame() {
        return currentGame;
    }

    public static void setGame(Game game) {
        currentGame = game;
        if (!allGames.contains(game)) {
            allGames.add(game);
        }
    }

    public static void removeGame(Game game) {
        allGames.remove(game);
        if (currentGame == game) {
            currentGame = null;
        }

        // Delete the saved game file
        for (int i = 0; i < allGames.size(); i++) {
            if (allGames.get(i) == game) {
                GameSaveLoadManager.deleteSavedGame("saved_games/game_" + i + ".bin");
                break;
            }
        }
    }

    public static List<Game> getAllGames() {
        return allGames;
    }

    public static Game findGameForUser(User user) {
        for (Game game : allGames) {
            if (game.isPlayerInGame(user)) {
                return game;
            }
        }
        return null;
    }

    public static boolean isUserInGame(User user) {
        return findGameForUser(user) != null;
    }

    public static Game createNewGame(List<Player> players, Player creator) {
        Game game = new Game(players, creator);
        setGame(game);
        saveCurrentGame();
        return game;
    }

    public static void toggleMapSelectionPhase() {
        isMapSelectionPhase = !isMapSelectionPhase;
    }

    public static boolean isMapSelectionPhase() {
        return isMapSelectionPhase;
    }

    public static boolean allChose() {
        return allChose;
    }

    public static void makeAllChose() {
        allChose = true;
    }

    /**
     * Performs an autosave operation
     *
     * @return true if autosave was successful
     */
    public static boolean autosave() {
        if (currentGame != null) {
            return GameSaveLoadManager.autosave();
        }
        return false;
    }

    /**
     * Save game with a custom name
     *
     * @param saveName The custom name for the save file
     * @return true if save was successful
     */
    public static boolean saveGameWithName(String saveName) {
        if (currentGame != null) {
            return GameSaveLoadManager.saveGameWithName(currentGame, saveName);
        }
        return false;
    }

    /**
     * Load game by name
     *
     * @param saveName The name of the save file to load
     * @return The loaded game or null if loading failed
     */
    public static Game loadGameByName(String saveName) {
        String filePath = "saved_games/" + saveName + ".bin";
        Game game = GameSaveLoadManager.loadGame(filePath);
        if (game != null) {
            currentGame = game;
        }
        return game;
    }

    /**
     * Get a list of all saved games
     *
     * @return List of save file names without extensions
     */
    public static List<String> getSavedGamesList() {
        List<String> saveNames = new ArrayList<>();
        List<File> saveFiles = GameSaveLoadManager.listSavedGames();

        for (File file : saveFiles) {
            String fileName = file.getName();
            // Remove .bin extension
            saveNames.add(fileName.substring(0, fileName.lastIndexOf('.')));
        }

        return saveNames;
    }

    public static boolean deleteSavedGame(String saveName) {
        String filePath = "saved_games/" + saveName + ".bin";
        return GameSaveLoadManager.deleteSavedGame(filePath);
    }

    public static void shutdown() {
        saveData();

        System.out.println("Application shutdown completed. All data saved.");
    }
}