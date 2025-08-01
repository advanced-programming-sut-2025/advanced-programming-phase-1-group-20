package org.example.utils;

import org.example.client.Main;
import org.example.client.controllers.menu.MainMenuController;
import org.example.client.views.menu.MainMenuScreen;
import org.example.common.models.App;
import org.example.common.models.entities.User;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class AutoLoginUtil {
    private static final String AUTO_LOGIN_FILE = "auto_login.json";

    public static void saveAutoLogin(String username) {
        try {
            // Create simple JSON structure
            String jsonData = "{\"username\":\"" + username + "\"}";

            // Save the data to file
            try (FileWriter writer = new FileWriter(AUTO_LOGIN_FILE)) {
                writer.write(jsonData);
            }
            System.out.println("Auto-login saved for user: " + username);
        } catch (IOException e) {
            System.out.println("Error saving auto-login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void clearAutoLogin() {
        File file = new File(AUTO_LOGIN_FILE);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                System.out.println("Auto-login cleared");
            } else {
                System.out.println("Failed to delete auto-login file");
            }
        }
    }

    public static boolean checkAndPerformAutoLogin() {
        File file = new File(AUTO_LOGIN_FILE);
        if (!file.exists()) {
            System.out.println("No auto-login file found");
            return false;
        }

        try (FileReader reader = new FileReader(file)) {
            char[] buf = new char[1024];
            int len = reader.read(buf);
            if (len <= 0) {
                System.out.println("Auto-login file is empty");
                clearAutoLogin();
                return false;
            }

            String jsonData = new String(buf, 0, len).trim();

            // Simple JSON parsing - extract username
            String username = extractUsernameFromJson(jsonData);
            if (username == null || username.isEmpty()) {
                System.out.println("Invalid auto-login data format");
                clearAutoLogin();
                return false;
            }

            // Get the user from database
            User user = App.getUser(username);
            if (user == null) {
                System.out.println("User not found for auto-login: " + username);
                clearAutoLogin();
                return false;
            }

            // Check if user still has stay logged in enabled
            if (!user.isStayLoggedIn()) {
                System.out.println("User has disabled stay logged in: " + username);
                clearAutoLogin();
                return false;
            }

            App.setLoggedInUser(user);
            App.saveData();

            Main.getGame().setScreen(new MainMenuScreen(new MainMenuController(), AssetManager.getAssetManager().getSkin()));
            return true;

        } catch (IOException e) {
            System.out.println("Error reading auto-login file: " + e.getMessage());
            e.printStackTrace();
            clearAutoLogin();
        }

        return false;
    }

    public static boolean isAutoLoginAvailable() {
        File file = new File(AUTO_LOGIN_FILE);
        if (!file.exists()) {
            return false;
        }

        try (FileReader reader = new FileReader(file)) {
            char[] buf = new char[1024];
            int len = reader.read(buf);
            if (len <= 0) {
                return false;
            }

            String jsonData = new String(buf, 0, len).trim();
            String username = extractUsernameFromJson(jsonData);

            if (username == null || username.isEmpty()) {
                return false;
            }

            // Check if user exists and has stay logged in enabled
            User user = App.getUser(username);
            return user != null && user.isStayLoggedIn();
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Simple JSON parser to extract username from {"username":"value"}
     */
    private static String extractUsernameFromJson(String jsonData) {
        try {
            // Remove whitespace
            jsonData = jsonData.trim();

            // Check if it starts with {"username":
            if (!jsonData.startsWith("{\"username\":")) {
                return null;
            }

            // Find the start of the username value
            int startIndex = jsonData.indexOf("\"username\":\"") + 12;
            if (startIndex < 12) {
                return null;
            }

            // Find the end of the username value
            int endIndex = jsonData.indexOf("\"", startIndex);
            if (endIndex == -1) {
                return null;
            }

            // Extract the username
            String username = jsonData.substring(startIndex, endIndex);

            // Check if the JSON ends properly
            if (!jsonData.substring(endIndex + 1).trim().equals("}")) {
                return null;
            }

            return username;
        } catch (Exception e) {
            return null;
        }
    }
}
