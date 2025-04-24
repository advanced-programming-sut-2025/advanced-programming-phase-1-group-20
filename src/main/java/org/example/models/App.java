package org.example.models;

import org.example.models.Items.*;
import org.example.models.entities.User;
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

    //    private static List<Game> allGames;

    //    private static Lists for game
    private static List<Plant> plants = new ArrayList<>();
    private static List<Crop> crops = new ArrayList<>();
    private static List<Mineral> minerals = new ArrayList<>();
    private static List<Seed> seeds = new ArrayList<>();
    private static List<Tree> trees = new ArrayList<>();
    private static List<CookingItem> cookingItems = new ArrayList<>();
    private static List<CraftingItem> craftingItems = new ArrayList<>();
    private static List<Item> items = new ArrayList<>();


    public static void initialize() {
        if (!dataLoaded) {
            // Load users from file storage
            users = FileStorage.loadUsers();

            // Add security questions
            addSecurityQuestion();

            //initializing game static lists
            plants = FileStorage.loadPlants();
            crops = FileStorage.loadCrops();
            minerals = FileStorage.loadMinerals();
            seeds = FileStorage.loadSeeds();
            trees = FileStorage.loadTrees();
            cookingItems = FileStorage.loadCookingItems();
            craftingItems = FileStorage.loadCraftingItems();
            items.addAll(plants);
            items.addAll(crops);
            items.addAll(minerals);
            items.addAll(seeds);
            items.addAll(trees);
            items.addAll(cookingItems);
            items.addAll(craftingItems);

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
}
