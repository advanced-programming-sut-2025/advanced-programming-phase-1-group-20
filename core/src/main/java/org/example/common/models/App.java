package org.example.common.models;

import org.example.client.controllers.gameplay.NPCController;
import org.example.common.models.Items.Item;
import org.example.common.models.MapDetails.Farm;
import org.example.common.models.Player.Player;
import org.example.common.models.entities.Game;
import org.example.common.models.entities.User;
import org.example.utils.FileStorage;
import org.example.utils.MongoDBConnection;
import org.example.utils.auth.JWTUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {
    private static Map<String, User> users = new HashMap<>();
    private static User loggedInUser;
    private static Map<Integer, String> securityQuestions = new HashMap<>();
    private static boolean dataLoaded = false;
    private static List<Game> allGames = new ArrayList<>();
    private static Game currentGame;
    private static boolean allChose = false;
    private static List<Item> items = new ArrayList<>();
    private static boolean isFarmSelectionPhase = false;

    public static void initialize() {
        if (!dataLoaded) {
            users = FileStorage.loadUsers(); // load users from mongo
            addSecurityQuestion();
            items = FileStorage.loadItems();
//            loadAllGames(); // load all games from mongo
            NPCController.initialize();
            dataLoaded = true;
        }
    }

    public static void saveData() {
        FileStorage.saveUsers(users);
    }

    public static void saveAllGames() {
        if(allGames == null) return;
        for (Game game : allGames) {
            if (game != null) {
                FileStorage.saveGame(game);
            }
        }
    }

    public static void saveCurrentGame() {
        if (currentGame != null) {
            currentGame.setSaved(true);
            FileStorage.saveGame(currentGame);
        }
    }

    public static void loadAllGames() {
        allGames = FileStorage.loadAllGames();
    }

    public static boolean saveGameWithName(String saveName) {
        if (currentGame != null) {
            currentGame.setSaveName(saveName); // نام ذخیره را در شیء Game تنظیم کنید
            return FileStorage.saveGame(currentGame);
        }
        return false;
    }

    public static Game loadGameByName(String saveName) {
        Game game = FileStorage.loadGame(saveName);

        if (game != null) {
            if (game.getGameMap() != null && game.getPlayers() != null) {
                for (Player p : game.getPlayers()) {
                    Farm farm = game.getGameMap().getFarmByPlayer(p);
                    if (farm != null) {
                        p.setCurrentFarm(farm);
                        System.out.println("Player '" + p.getUser().getUsername() + "' re-linked to farm '" + farm.getName() + "'.");
                    }
                }
            }

            currentGame = game;
            if (allGames != null && !allGames.contains(game)) {
                allGames.add(game);
            }
        }
        return game;
    }

    public static boolean deleteSavedGame(String saveName) {
        boolean deleted = FileStorage.deleteGame(saveName);
        if (deleted && allGames != null) {
            allGames.removeIf(game -> game.getSaveName() != null && game.getSaveName().equals(saveName));
            if (currentGame != null && currentGame.getSaveName() != null && currentGame.getSaveName().equals(saveName)) {
                currentGame = null;
            }
        }
        return deleted;
    }

    public static void addUser(User user) {
        users.put(user.getUsername(), user);
        saveData();
    }

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    public static void setLoggedInUser(User user) {
        loggedInUser = user;
    }

    public static void logout() {
        if (loggedInUser != null) {
            loggedInUser.setJwtToken(null);
            loggedInUser.setTokenExpirationTime(0);
            loggedInUser.setRefreshToken(null);
            loggedInUser.setRefreshTokenExpirationTime(0);
            loggedInUser.setStayLoggedIn(false);
            loggedInUser = null;
        }
        org.example.utils.AutoLoginUtil.clearAutoLogin();
    }

    public static User getUser(String username) {
        return users.get(username);
    }

    public static Map<String, User> getUsers() {
        return users;
    }

    public static void removeUser(User user) {
        users.remove(user.getUsername());
        saveData();
    }

    public static void addSecurityQuestion() {
        securityQuestions.put(0, "Whats was the name of your best friend in high school");
        securityQuestions.put(1, "In which city did your parents meet?");
        securityQuestions.put(2, "Whats your favorite band of music?");
        securityQuestions.put(3, "Whats your favorite programming language?");
    }

    public static String getSecurityQuestion(int index) {
        return securityQuestions.get(index);
    }

    public static List<String> getSecurityQuestions() {
        return new ArrayList<>(securityQuestions.values());
    }

    public static Item getItem(String itemName) {
        return items.stream().filter(item -> item.getName().equalsIgnoreCase(itemName))
            .findFirst().orElse(null);
    }

    public static Game getGame() {
        return currentGame;
    }

    public static void setGame(Game game) {
        App.currentGame = game;
        if (game != null && !allGames.contains(game)) {
            allGames.add(game);
        }
    }

    public static void removeGame(Game game) {
        if (game == null) return;
        allGames.remove(game);
        if (currentGame == game) {
            currentGame = null;
        }
        if (game.getSaveName() != null && !game.getSaveName().isEmpty()) {
            deleteSavedGame(game.getSaveName());
        }
    }

    public static List<Game> getAllGames() {
        return allGames;
    }

    public static void setAllGames(List<Game> allGames) {
        App.allGames = allGames;
    }

    public static Game findGameForUser(User user) {
        if (allGames != null && user != null) {
            for (Game game : allGames) {
                if (game != null && game.isPlayerInGame(user)) {
                    return game;
                }
            }
        }
        return null;
    }

    public static boolean isUserInGame(User user) {
        return findGameForUser(user) != null;
    }

    public static Game createNewGame(List<Player> players, Player creator) {
        Game game = new Game(players, creator);
        game.setSaveName("new_game_" + System.currentTimeMillis());
        setGame(game);
        saveCurrentGame();
        return game;
    }

    public static void toggleFarmSelectionPhase() {
        isFarmSelectionPhase = !isFarmSelectionPhase;
    }

    public static boolean isFarmSelectionPhase() {
        return isFarmSelectionPhase;
    }

    public static boolean allChose() {
        return allChose;
    }

    public static void makeAllChose() {
        allChose = true;
    }

    public static void shutdown() {
        saveData();
        MongoDBConnection.closeConnection();
        System.out.println("Application shutdown completed. All data saved.");
    }

    public static User validateUserToken(String token) {
        if (token == null || token.isEmpty()) {
            System.out.println("Token validation failed: Token is null or empty");
            return null;
        }

        // Check token status
        String tokenStatus = JWTUtils.getTokenStatus(token);
        if (!tokenStatus.equals(JWTUtils.TOKEN_VALID)) {
            System.out.println("Token validation failed: " + JWTUtils.getTokenStatusMessage(tokenStatus));
            return null;
        }

        // Extract username from token
        String username = JWTUtils.extractUsername(token);
        if (username == null) {
            System.out.println("Token validation failed: Could not extract username");
            return null;
        }

        // Get user by username
        User user = getUser(username);
        if (user == null) {
            System.out.println("Token validation failed: User not found");
            return null;
        }

        // Check if the token matches the one stored in the user object
        if (!token.equals(user.getJwtToken())) {
            System.out.println("Token validation failed: Token does not match stored token");
            return null;
        }

        // Check if the token is still valid (not expired)
        if (!user.isTokenValid()) {
            System.out.println("Token validation failed: Token has expired");
            return null;
        }

        // Check if token is close to expiration (less than 1 hour remaining)
        long currentTime = System.currentTimeMillis();
        long expirationTime = user.getTokenExpirationTime();
        long oneHour = 60 * 60 * 1000;

        if (expirationTime - currentTime < oneHour) {
            // Refresh the token
            String newToken = JWTUtils.refreshToken(token);
            if (newToken != null) {
                user.setJwtToken(newToken);
                user.setTokenExpirationTime(JWTUtils.extractExpirationTime(newToken));
                System.out.println("Token refreshed successfully");
                saveData();
            }
        }

        return user;
    }

    public static boolean authenticateWithToken(String token) {
        User user = validateUserToken(token);
        if (user != null) {
            setLoggedInUser(user);
            System.out.println("User authenticated successfully with token");
            return true;
        }
        return false;
    }

    public static String getUserTokenStatus(String username) {
        User user = getUser(username);
        if (user == null) {
            return null;
        }

        String token = user.getJwtToken();
        if (token == null || token.isEmpty()) {
            return "No token available";
        }

        String tokenStatus = JWTUtils.getTokenStatus(token);
        return JWTUtils.getTokenStatusMessage(tokenStatus);
    }
}
