package org.example.models.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class PasswordUtils {

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(
                    password.getBytes(StandardCharsets.UTF_8));

            // Convert the byte array to a Base64 string for storage
            return Base64.getEncoder().encodeToString(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            // This exception shouldn't occur since SHA-256 is a standard algorithm
            e.printStackTrace();
            return null;
        }
    }

    public static boolean verifyPassword(String plainPassword, String storedHash) {
        String newHash = hashPassword(plainPassword);
        return newHash != null && newHash.equals(storedHash);
    }
}