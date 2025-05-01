package org.example.models;

import org.example.models.Items.Item;
import org.example.models.entities.Game;
import org.example.models.entities.User;
import org.example.models.utils.AutoLoginUtil;
import org.example.models.utils.FileStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {

    // TODO: add the saving methods
    // static structure for saving App Data
    private static Map<String, User> users = new HashMap<>();
    private static User loggedInUser;
    private static Map<Integer, String> securityQuestions = new HashMap<>();
    private static boolean dataLoaded = false;

    private static List<Game> allGames;
    private static Game currentGame;

    //    private static Lists for game
    private static List<Item> items = new ArrayList<>();


    public static void initialize() {
        if (!dataLoaded) {
            // Load users from file storage
            users = FileStorage.loadUsers();

            // Add security questions
            addSecurityQuestion();

            //initializing game static lists
            items = FileStorage.loadItems();

            dataLoaded = true;
        }
    }

    public static void saveData() {
        FileStorage.saveUsers(users);
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


    //game lists

    public static Item getItem(String itemName) {
        return items.stream().filter(item -> item.getName().equals(itemName))
                .findFirst().orElse(null);
    }

    public static Game getGame() {
        return currentGame;
    }

    public static void setGame(Game game) {
        currentGame = game;
    }

    public static void removeGame(Game game) {
        allGames.remove(game);
    }
}
