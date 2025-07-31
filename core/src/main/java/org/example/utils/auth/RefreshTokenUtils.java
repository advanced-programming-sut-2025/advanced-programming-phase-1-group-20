package org.example.utils.auth;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

/**
 * Utility class for managing refresh tokens securely.
 * Implements industry standard refresh token patterns for "stay logged in" functionality.
 */
public class RefreshTokenUtils {
    // Secret key for refresh token encryption
    private static final String REFRESH_SECRET = "stardew_valley_refresh_token_secret_key_2024";
    
    // Refresh token expiration time (30 days)
    private static final long REFRESH_EXPIRATION_TIME = 30L * 24 * 60 * 60 * 1000;
    
    // Token status constants
    public static final String REFRESH_TOKEN_VALID = "valid";
    public static final String REFRESH_TOKEN_INVALID_FORMAT = "invalid_format";
    public static final String REFRESH_TOKEN_INVALID_SIGNATURE = "invalid_signature";
    public static final String REFRESH_TOKEN_EXPIRED = "expired";
    public static final String REFRESH_TOKEN_ERROR = "error";

    /**
     * Generates a new refresh token for the given username
     * 
     * @param username The username to generate token for
     * @return Encrypted refresh token string
     */
    public static String generateRefreshToken(String username) {
        try {
            // Create payload with username, issued time, and expiration
            JsonObject payload = new JsonObject();
            payload.addProperty("sub", username);
            payload.addProperty("iat", new Date().getTime());
            payload.addProperty("exp", new Date().getTime() + REFRESH_EXPIRATION_TIME);
            payload.addProperty("type", "refresh");
            
            // Add random jti (JWT ID) for uniqueness
            payload.addProperty("jti", generateSecureRandom());
            
            // Convert to JSON string
            String tokenData = new Gson().toJson(payload);
            
            // Encrypt the token data
            return encryptToken(tokenData);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Validates a refresh token and returns the username if valid
     * 
     * @param encryptedToken The encrypted refresh token
     * @return Username if token is valid, null otherwise
     */
    public static String validateRefreshToken(String encryptedToken) {
        try {
            // Decrypt the token
            String tokenData = decryptToken(encryptedToken);
            if (tokenData == null) {
                return null;
            }
            
            // Parse the JSON payload
            JsonObject payload = new Gson().fromJson(tokenData, JsonObject.class);
            
            // Check token type
            if (!payload.has("type") || !payload.get("type").getAsString().equals("refresh")) {
                return null;
            }
            
            // Check expiration
            long expTime = payload.get("exp").getAsLong();
            if (expTime < new Date().getTime()) {
                return null; // Token expired
            }
            
            // Return username
            return payload.get("sub").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets the status of a refresh token
     * 
     * @param encryptedToken The encrypted refresh token
     * @return Token status constant
     */
    public static String getRefreshTokenStatus(String encryptedToken) {
        try {
            if (encryptedToken == null || encryptedToken.isEmpty()) {
                return REFRESH_TOKEN_INVALID_FORMAT;
            }
            
            // Decrypt the token
            String tokenData = decryptToken(encryptedToken);
            if (tokenData == null) {
                return REFRESH_TOKEN_INVALID_SIGNATURE;
            }
            
            // Parse the JSON payload
            JsonObject payload = new Gson().fromJson(tokenData, JsonObject.class);
            
            // Check token type
            if (!payload.has("type") || !payload.get("type").getAsString().equals("refresh")) {
                return REFRESH_TOKEN_INVALID_FORMAT;
            }
            
            // Check expiration
            long expTime = payload.get("exp").getAsLong();
            if (expTime < new Date().getTime()) {
                return REFRESH_TOKEN_EXPIRED;
            }
            
            return REFRESH_TOKEN_VALID;
        } catch (Exception e) {
            e.printStackTrace();
            return REFRESH_TOKEN_ERROR;
        }
    }

    /**
     * Extracts the expiration time from a refresh token
     * 
     * @param encryptedToken The encrypted refresh token
     * @return Expiration timestamp, or 0 if invalid
     */
    public static long extractRefreshTokenExpiration(String encryptedToken) {
        try {
            String tokenData = decryptToken(encryptedToken);
            if (tokenData == null) {
                return 0;
            }
            
            JsonObject payload = new Gson().fromJson(tokenData, JsonObject.class);
            return payload.get("exp").getAsLong();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Generates a new access token using a valid refresh token
     * 
     * @param refreshToken The refresh token to use
     * @return New access token if refresh token is valid, null otherwise
     */
    public static String refreshAccessToken(String refreshToken) {
        String username = validateRefreshToken(refreshToken);
        if (username != null) {
            return JWTUtils.generateToken(username);
        }
        return null;
    }

    /**
     * Encrypts token data using AES encryption
     */
    private static String encryptToken(String data) {
        try {
            // Create AES key from our secret
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = digest.digest(REFRESH_SECRET.getBytes(StandardCharsets.UTF_8));
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
     * Decrypts token data using AES decryption
     */
    private static String decryptToken(String encryptedData) {
        try {
            // Create AES key from our secret
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = digest.digest(REFRESH_SECRET.getBytes(StandardCharsets.UTF_8));
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

    /**
     * Generates a secure random string for token uniqueness
     */
    private static String generateSecureRandom() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
}