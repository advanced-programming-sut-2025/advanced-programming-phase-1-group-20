package org.example.utils;

import org.example.common.models.App;
import org.example.common.models.entities.User;
import org.example.utils.auth.RefreshTokenUtils;
import org.example.utils.auth.JWTUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class AutoLoginUtil {
    private static final String REFRESH_TOKEN_FILE = "refresh_token.dat";

    public static void saveAutoLogin(String username) {
        try {
            // Generate a secure refresh token
            String refreshToken = RefreshTokenUtils.generateRefreshToken(username);
            if (refreshToken != null) {
                // Save the encrypted refresh token to file
                try (FileWriter writer = new FileWriter(REFRESH_TOKEN_FILE)) {
                    writer.write(refreshToken);
                }
                System.out.println("Auto-login refresh token saved for user: " + username);
            } else {
                System.out.println("Failed to generate refresh token for user: " + username);
            }
        } catch (IOException e) {
            System.out.println("Error saving auto-login refresh token: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void clearAutoLogin() {
        File file = new File(REFRESH_TOKEN_FILE);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                System.out.println("Auto-login refresh token cleared");
            } else {
                System.out.println("Failed to delete refresh token file");
            }
        }
    }

    public static boolean checkAndPerformAutoLogin() {
        File file = new File(REFRESH_TOKEN_FILE);
        if (!file.exists()) {
            System.out.println("No refresh token file found for auto-login");
            return false;
        }

        try (FileReader reader = new FileReader(file)) {
            char[] buf = new char[2048]; // Larger buffer for encrypted tokens
            int len = reader.read(buf);
            if (len <= 0) {
                System.out.println("Refresh token file is empty");
                clearAutoLogin();
                return false;
            }

            String refreshToken = new String(buf, 0, len).trim();

            // Validate the refresh token
            String username = RefreshTokenUtils.validateRefreshToken(refreshToken);
            if (username == null) {
                System.out.println("Invalid or expired refresh token");
                clearAutoLogin(); // Clear invalid token
                return false;
            }

            // Get the user from database
            User user = App.getUser(username);
            if (user == null) {
                System.out.println("User not found for auto-login: " + username);
                clearAutoLogin(); // Clear invalid auto-login
                return false;
            }

            // Check if user still has stay logged in enabled
            if (!user.isStayLoggedIn()) {
                System.out.println("User has disabled stay logged in: " + username);
                clearAutoLogin(); // Clear auto-login as user disabled it
                return false;
            }

            // Generate new access token using refresh token
            String newAccessToken = RefreshTokenUtils.refreshAccessToken(refreshToken);
            if (newAccessToken == null) {
                System.out.println("Failed to generate new access token from refresh token");
                clearAutoLogin(); // Clear invalid refresh token
                return false;
            }

            // Update user with new access token
            user.setJwtToken(newAccessToken);
            user.setTokenExpirationTime(JWTUtils.extractExpirationTime(newAccessToken));

            // Update user's refresh token info if needed
            user.setRefreshToken(refreshToken);
            user.setRefreshTokenExpirationTime(RefreshTokenUtils.extractRefreshTokenExpiration(refreshToken));

            // Authenticate the user
            App.setLoggedInUser(user);
            App.saveData();

            System.out.println("Auto-login successful for user: " + username);
            System.out.println("New access token generated using refresh token");
            return true;

        } catch (IOException e) {
            System.out.println("Error reading refresh token file: " + e.getMessage());
            e.printStackTrace();
            clearAutoLogin(); // Clear on error
        }

        return false;
    }


    public static boolean isAutoLoginAvailable() {
        File file = new File(REFRESH_TOKEN_FILE);
        if (!file.exists()) {
            return false;
        }

        try (FileReader reader = new FileReader(file)) {
            char[] buf = new char[2048];
            int len = reader.read(buf);
            if (len <= 0) {
                return false;
            }

            String refreshToken = new String(buf, 0, len).trim();
            String status = RefreshTokenUtils.getRefreshTokenStatus(refreshToken);
            return RefreshTokenUtils.REFRESH_TOKEN_VALID.equals(status);
        } catch (IOException e) {
            return false;
        }
    }
}
