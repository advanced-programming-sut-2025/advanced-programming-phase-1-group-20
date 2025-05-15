package org.example.models;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.controllers.NPCController;
import org.example.models.Items.Item;
import org.example.models.Player.Player;
import org.example.models.entities.Game;
import org.example.models.entities.User;
import org.example.models.utils.AutoLoginUtil;
import org.example.models.utils.FileStorage;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.models.savegame.GameSerializer.createKryo;

public class App {
    // File paths for saving games
    private static final String GAMES_DIRECTORY = "saved_games";
    private static final String CURRENT_GAME_FILE = GAMES_DIRECTORY + "/current_game.bin";
    // TODO: add the saving methods
    // static structure for saving App Data
    private static Map<String, User> users = new HashMap<>();
    private static User loggedInUser;
    private static Map<Integer, String> securityQuestions = new HashMap<>();
    private static boolean dataLoaded = false;
    private static List<Game> allGames = new ArrayList<>();
    private static Game currentGame;
    private static boolean allChose = false;

    //    private static Lists for game
    private static List<Item> items = new ArrayList<>();


    private static boolean isMapSelectionPhase = false;

    public static void initialize() {
        if (!dataLoaded) {
            users = FileStorage.loadUsers();
            System.out.println("Loaded users: " + users.size());
            addSecurityQuestion();

            items = FileStorage.loadItems();

            File directory = new File(GAMES_DIRECTORY);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            loadAllGames();

            NPCController.initialize();

            dataLoaded = true;
        }
    }

    public static void saveData() {
        FileStorage.saveUsers(users);
        saveAllGames();
    }

    public static void saveAllGames() {
        // Save all games to files
        for (int i = 0; i < allGames.size(); i++) {
            Game game = allGames.get(i);
            saveGame(game, GAMES_DIRECTORY + "/game_" + i + ".bin");
        }
    }

    public static void saveCurrentGame() {
        if (currentGame != null) {
            currentGame.setSaved(true);
            saveGame(currentGame, CURRENT_GAME_FILE);
        }
    }

    private static void saveGame(Game game, String filePath) {
        Kryo kryo = createKryo();
        try (Output output = new Output(new FileOutputStream(filePath))) {
            kryo.writeObject(output, game);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save game", e);
        }
    }

    public static void loadAllGames() {
        allGames = new ArrayList<>();
        File directory = new File(GAMES_DIRECTORY);
        if (directory.exists()) {
            File[] files = directory.listFiles((dir, name) -> name.startsWith("game_") && name.endsWith(".bin"));
            if (files != null) {
                for (File file : files) {
                    Game game = loadGame(file.getPath());
                    if (game != null) {
                        allGames.add(game);
                    }
                }
            }
        }
    }

    public static Game loadCurrentGame() {
        File file = new File(CURRENT_GAME_FILE);
        if (file.exists()) {
            Game game = loadGame(CURRENT_GAME_FILE);
            if (game != null) {
                currentGame = game;
                return game;
            }
        }
        return null;
    }

    private static Game loadGame(String filePath) {
        Kryo kryo = createKryo();
        try (Input input = new Input(new FileInputStream(filePath))) {
            return kryo.readObject(input, Game.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load game", e);
        }
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
        AutoLoginUtil.clearAutoLogin();
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
                File file = new File(GAMES_DIRECTORY + "/game_" + i + ".json");
                if (file.exists()) {
                    file.delete();
                }
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
}
