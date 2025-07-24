package org.example.common.models;

import org.example.client.controllers.gameplay.NPCController;
import org.example.common.models.Items.Item;
import org.example.common.models.Player.Player;
import org.example.common.models.entities.Game;
import org.example.common.models.entities.User;
import org.example.utils.FileStorage;
import org.example.utils.GameSaveLoadManager;
import org.example.utils.MongoDBConnection;
import org.example.utils.auth.JWTUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {
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

            // اگر loadItems() هنوز از فایل می‌خواند، باید به FileStorage/MongoDB منتقل شود.
            // در غیر این صورت، مطمئن شوید که این متد آیتم‌ها را از یک منبع ثابت یا MongoDB بارگذاری می‌کند.
            // فعلاً فرض می‌کنیم FileStorage.loadItems() به درستی کار می‌کند.
            items = FileStorage.loadItems();

            // Initialize save manager (برای MongoDB)
            GameSaveLoadManager.initialize();

            // Load all saved games from MongoDB
            GameSaveLoadManager.loadAllGames(); // این متد App.setAllGames را فراخوانی می‌کند.

            NPCController.initialize();

            dataLoaded = true;

            // **** checkForAutosaveRecovery() - حذف شده، زیرا منطق آن برای فایل‌های .bin بود. ****
            // این منطق باید در GameSaveLoadManager پیاده سازی شود اگر بخواهید Autosave را از MongoDB چک کنید.
        }
    }

    public static void saveData() {
        FileStorage.saveUsers(users);
        saveAllGames(); // این متد حالا بازی‌ها را در MongoDB ذخیره می‌کند
    }

    public static void saveAllGames() {
        // ذخیره تمام بازی‌ها در MongoDB
        // هر بازی در لیست allGames باید نام saveName خود را داشته باشد.
        for (Game game : allGames) {
            if (game != null) {
                // اگر saveName تنظیم نشده بود، یک نام پیش‌فرض یا بر اساس ID بدهید
                if (game.getSaveName() == null || game.getSaveName().isEmpty()) {
                    // مثال: game.setSaveName("user_" + game.getPlayer().getUser().getUsername() + "_game_" + System.currentTimeMillis());
                    // این بخش نیاز به منطق دقیق‌تری دارد که چگونه نام منحصر به فرد را برای بازی جدید تولید کنید.
                    // می‌توانید آن را به GameSaveLoadManager بسپارید.
                    GameSaveLoadManager.saveGameWithName(game, null); // GameSaveLoadManager نام را تولید می‌کند
                } else {
                    GameSaveLoadManager.saveGameWithName(game, game.getSaveName());
                }
            }
        }
    }

    public static void saveCurrentGame() {
        if (currentGame != null) {
            currentGame.setSaved(true);
            // اطمینان حاصل کنید currentGame.saveName تنظیم شده است قبل از فراخوانی GameSaveLoadManager.saveCurrentGame()
            if (currentGame.getSaveName() == null || currentGame.getSaveName().isEmpty()) {
                currentGame.setSaveName("current_game"); // نام پیش‌فرض برای current game
            }
            GameSaveLoadManager.saveCurrentGame();
        }
    }

    // این متد قبلاً به GameSaveLoadManager.loadAllGames() ارجاع می‌داد.
    // اگر می‌خواهید App.allGames را پس از بارگذاری به‌روز کنید، باید از App.setAllGames استفاده کنید.
    // GameSaveLoadManager.loadAllGames() قبلاً App.setAllGames را صدا می‌زند.
    @Deprecated // این متد ممکن است دیگر نیازی نباشد اگر GameSaveLoadManager.loadAllGames() مستقیم لیست App را مدیریت می‌کند.
    public static void loadAllGames() {
        GameSaveLoadManager.loadAllGames();
    }

    public static Game loadCurrentGame() {
        Game game = GameSaveLoadManager.loadCurrentGame();
        if (game != null) {
            currentGame = game;
            // اگر بازی با نام "current_game" بارگذاری شد، saveName آن را تنظیم کنید
            game.setSaveName("current_game");
            // مطمئن شوید که بازی بارگذاری شده به allGames اضافه می‌شود اگر هنوز نبود
            if (!allGames.contains(game)) {
                allGames.add(game);
            }
            return game;
        }
        return null;
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
            // Clear the JWT token and expiration time
            loggedInUser.setJwtToken(null);
            loggedInUser.setTokenExpirationTime(0);
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
        return new ArrayList<>(securityQuestions.values()); // برگرداندن یک کپی از لیست
    }

    public static Item getItem(String itemName) {
        return items.stream().filter(item -> item.getName().equals(itemName))
            .findFirst().orElse(null);
    }

    // این متد در GameSaveLoadManager.java به App.setGame() تغییر کرده است.
    // اما اگر از بیرون App نیز game را ست می‌کنید، این متد صحیح است.
    public static Game getGame() {
        return currentGame;
    }

    public static void setGame(Game game) {
        App.currentGame = game;
        if (game != null && !allGames.contains(game)) { // اگر بازی null نیست و قبلاً در لیست نیست
            allGames.add(game);
        }
    }

    public static void removeGame(Game game) {
        if (game == null) return;

        // حذف از لیست App
        allGames.remove(game);
        if (currentGame == game) {
            currentGame = null;
        }

        // **** حذف ذخیره بازی از MongoDB ****
        // نیاز داریم که game.getSaveName() نام صحیح را برگرداند.
        // GameSaveLoadManager.deleteSavedGame اکنون فقط نام ذخیره را می‌پذیرد.
        if (game.getSaveName() != null && !game.getSaveName().isEmpty()) {
            GameSaveLoadManager.deleteSavedGame(game.getSaveName());
        } else {
            System.err.println("Cannot delete game from MongoDB: saveName is null or empty for game: " + game);
        }
    }

    // این متد از قبل وجود داشت و App.allGames را برمی‌گرداند.
    public static List<Game> getAllGames() {
        return allGames;
    }

    public static void setAllGames(List<Game> allGames) {
        App.allGames = allGames;
    }

    // این متد نیاز به متد User.equals() برای کارکرد صحیح دارد
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
        // هنگام ایجاد بازی جدید، یک saveName اولیه برای آن تنظیم کنید
        game.setSaveName("new_game_" + System.currentTimeMillis()); // نام موقت و منحصر به فرد
        setGame(game); // این متد بازی را به currentGame و allGames اضافه می‌کند.
        saveCurrentGame(); // این بازی را در MongoDB با نام "current_game" ذخیره می‌کند
        // شما همچنین می‌توانید این بازی جدید را با saveName خود نیز ذخیره کنید:
        // GameSaveLoadManager.saveGameWithName(game, game.getSaveName());
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

    public static boolean autosave() {
        if (currentGame != null) {
            return GameSaveLoadManager.autosave();
        }
        return false;
    }

    public static boolean saveGameWithName(String saveName) {
        if (currentGame != null) {
            currentGame.setSaveName(saveName); // نام ذخیره را در شیء Game تنظیم کنید
            return GameSaveLoadManager.saveGameWithName(currentGame, saveName);
        }
        return false;
    }

    public static Game loadGameByName(String saveName) {
        // دیگر نیازی به filePath و ".bin" نیست.
        Game game = GameSaveLoadManager.loadGame(saveName);
        if (game != null) {
            currentGame = game;
            game.setSaveName(saveName); // نام ذخیره را پس از بارگذاری تنظیم کنید
            // مطمئن شوید که بازی بارگذاری شده به allGames اضافه می‌شود اگر هنوز نبود
            if (!allGames.contains(game)) {
                allGames.add(game);
            }
        }
        return game;
    }

    public static boolean deleteSavedGame(String saveName) {
        boolean deleted = GameSaveLoadManager.deleteSavedGame(saveName);
        if (deleted) {
            allGames.removeIf(game -> game.getSaveName() != null && game.getSaveName().equals(saveName));
            if (currentGame != null && currentGame.getSaveName() != null && currentGame.getSaveName().equals(saveName)) {
                currentGame = null;
            }
        }
        return deleted;
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
