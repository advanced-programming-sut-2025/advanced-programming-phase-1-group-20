package org.example.utils;

import org.example.models.App;
import org.example.models.entities.User;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class AutoLoginUtil {
    private static final String AUTO_LOGIN_FILE = "autologin.txt";


    public static void saveAutoLogin(String username) {
        try (FileWriter writer = new FileWriter(AUTO_LOGIN_FILE)) {
            writer.write(username);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clearAutoLogin() {
        File file = new File(AUTO_LOGIN_FILE);
        if (file.exists()) {
            file.delete();
        }
    }


    /**
     * Checks if there's a saved auto-login username and attempts to log in the user.
     * Validates the JWT token before logging in to ensure it's still valid.
     *
     * @return true if auto-login was successful, false otherwise
     */
    public static boolean checkAndPerformAutoLogin() {
        File file = new File(AUTO_LOGIN_FILE);
        if (!file.exists()) {
            System.out.println("Auto-login file not found");
            return false;
        }

        try (FileReader reader = new FileReader(file)) {
            char[] buf = new char[1024];
            int len = reader.read(buf);
            if (len <= 0) {
                System.out.println("Auto-login file is empty");
                return false;
            }

            String username = new String(buf, 0, len).trim();
            User user = App.getUser(username);

            if (user == null) {
                System.out.println("User not found for auto-login: " + username);
                clearAutoLogin(); // Clear invalid auto-login
                return false;
            }

            if (!user.isStayLoggedIn()) {
                System.out.println("User has not enabled stay logged in: " + username);
                clearAutoLogin(); // Clear invalid auto-login
                return false;
            }

            // Check if the user has a valid JWT token
            String token = user.getJwtToken();
            if (token == null || token.isEmpty()) {
                System.out.println("No JWT token found for user: " + username);
                clearAutoLogin(); // Clear invalid auto-login
                return false;
            }

            // Validate the token and get its status
            String tokenStatus = App.getUserTokenStatus(username);
            if (tokenStatus == null || !tokenStatus.equals("Token is valid")) {
                System.out.println("Invalid JWT token for user: " + username + " - " + tokenStatus);
                clearAutoLogin(); // Clear invalid auto-login
                return false;
            }

            // Token is valid, authenticate the user
            boolean authenticated = App.authenticateWithToken(token);
            if (!authenticated) {
                System.out.println("Failed to authenticate user with token: " + username);
                clearAutoLogin(); // Clear invalid auto-login
                return false;
            }

            System.out.println("Auto-login successful for user: " + username);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error reading auto-login file: " + e.getMessage());
        }

        return false;
    }
}
