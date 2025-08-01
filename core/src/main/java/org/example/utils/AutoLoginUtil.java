package org.example.utils;

import org.example.client.Main;
import org.example.client.controllers.menu.MainMenuController;
import org.example.client.views.menu.MainMenuScreen;
import org.example.common.models.App;
import org.example.common.models.entities.User;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class AutoLoginUtil {
    private static final String AUTO_LOGIN_FILE = "auto_login.dat";
    private static final String ENCRYPTION_SECRET = "stardew_valley_auto_login_secret_2024";

    public static void saveAutoLogin(String username, String passwordHash) {
        try {
            // Create login data
            String loginData = username + ":" + passwordHash;

            // Encrypt the login data
            String encryptedData = encryptData(loginData);

            // Save the encrypted data to file
            try (FileWriter writer = new FileWriter(AUTO_LOGIN_FILE)) {
                writer.write(encryptedData);
            }
            System.out.println("Auto-login credentials saved for user: " + username);
        } catch (IOException e) {
            System.out.println("Error saving auto-login credentials: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void clearAutoLogin() {
        File file = new File(AUTO_LOGIN_FILE);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                System.out.println("Auto-login credentials cleared");
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
            char[] buf = new char[2048];
            int len = reader.read(buf);
            if (len <= 0) {
                System.out.println("Auto-login file is empty");
                clearAutoLogin();
                return false;
            }

            String encryptedData = new String(buf, 0, len).trim();

            // Decrypt the login data
            String loginData = decryptData(encryptedData);
            if (loginData == null) {
                System.out.println("Failed to decrypt auto-login data");
                clearAutoLogin();
                return false;
            }

            // Parse username and password hash
            String[] parts = loginData.split(":");
            if (parts.length != 2) {
                System.out.println("Invalid auto-login data format");
                clearAutoLogin();
                return false;
            }

            String username = parts[0];
            String passwordHash = parts[1];

            // Get the user from database
            User user = App.getUser(username);
            if (user == null) {
                System.out.println("User not found for auto-login: " + username);
                clearAutoLogin();
                return false;
            }

            // Verify password hash matches
            if (!user.getPassword().equals(passwordHash)) {
                System.out.println("Password hash mismatch for auto-login: " + username);
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

            System.out.println("Auto-login successful for user: " + username);
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
            char[] buf = new char[2048];
            int len = reader.read(buf);
            if (len <= 0) {
                return false;
            }

            String encryptedData = new String(buf, 0, len).trim();
            String loginData = decryptData(encryptedData);

            if (loginData == null) {
                return false;
            }

            String[] parts = loginData.split(":");
            if (parts.length != 2) {
                return false;
            }

            String username = parts[0];
            String passwordHash = parts[1];

            // Check if user exists and credentials are valid
            User user = App.getUser(username);
            return user != null && user.getPassword().equals(passwordHash) && user.isStayLoggedIn();
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Encrypts data using AES encryption
     */
    private static String encryptData(String data) {
        try {
            // Create AES key from our secret
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = digest.digest(ENCRYPTION_SECRET.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

            // Encrypt the data
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

            // Return Base64 encoded encrypted data
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Decrypts data using AES decryption
     */
    private static String decryptData(String encryptedData) {
        try {
            // Create AES key from our secret
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = digest.digest(ENCRYPTION_SECRET.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

            // Decrypt the data
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
