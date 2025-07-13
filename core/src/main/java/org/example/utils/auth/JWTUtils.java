package org.example.utils.auth;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;


public class JWTUtils {
    // Secret key used for signing the token
    private static final String SECRET_KEY = "stardew_valley_secret_key_for_jwt_authentication";
    // Token expiration time in milliseconds (24 hours)
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;

    // Token status constants
    public static final String TOKEN_VALID = "valid";
    public static final String TOKEN_INVALID_FORMAT = "invalid_format";
    public static final String TOKEN_INVALID_SIGNATURE = "invalid_signature";
    public static final String TOKEN_EXPIRED = "expired";
    public static final String TOKEN_ERROR = "error";

    public static String generateToken(String username) {
        // Create header
        JsonObject header = new JsonObject();
        header.addProperty("alg", "HS256");
        header.addProperty("typ", "JWT");

        // Create payload
        JsonObject payload = new JsonObject();
        payload.addProperty("sub", username);
        payload.addProperty("iat", new Date().getTime());
        payload.addProperty("exp", new Date().getTime() + EXPIRATION_TIME);

        // Encode header and payload
        String encodedHeader = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(new Gson().toJson(header).getBytes(StandardCharsets.UTF_8));
        String encodedPayload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(new Gson().toJson(payload).getBytes(StandardCharsets.UTF_8));

        // Create signature
        String signature = createSignature(encodedHeader + "." + encodedPayload);

        // Combine all parts to form the JWT token
        return encodedHeader + "." + encodedPayload + "." + signature;
    }

    public static String validateToken(String token) {
        try {
            // Split the token into its parts
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return null; // Invalid token format
            }

            // Verify signature
            String signature = createSignature(parts[0] + "." + parts[1]);
            if (!signature.equals(parts[2])) {
                return null; // Invalid signature
            }

            // Decode payload
            String decodedPayload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            JsonObject payload = new Gson().fromJson(decodedPayload, JsonObject.class);

            // Check if token is expired
            long expTime = payload.get("exp").getAsLong();
            if (expTime < new Date().getTime()) {
                return null; // Token expired
            }

            // Return the username
            return payload.get("sub").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Error during validation
        }
    }

    public static String getTokenStatus(String token) {
        try {
            if (token == null || token.isEmpty()) {
                return TOKEN_INVALID_FORMAT;
            }

            // Split the token into its parts
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return TOKEN_INVALID_FORMAT;
            }

            // Verify signature
            String signature = createSignature(parts[0] + "." + parts[1]);
            if (!signature.equals(parts[2])) {
                return TOKEN_INVALID_SIGNATURE;
            }

            // Decode payload
            String decodedPayload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            JsonObject payload = new Gson().fromJson(decodedPayload, JsonObject.class);

            // Check if token is expired
            long expTime = payload.get("exp").getAsLong();
            if (expTime < new Date().getTime()) {
                return TOKEN_EXPIRED;
            }

            return TOKEN_VALID;
        } catch (Exception e) {
            e.printStackTrace();
            return TOKEN_ERROR;
        }
    }

    public static String getTokenStatusMessage(String status) {
        switch (status) {
            case TOKEN_VALID:
                return "Token is valid";
            case TOKEN_INVALID_FORMAT:
                return "Invalid token format";
            case TOKEN_INVALID_SIGNATURE:
                return "Invalid token signature";
            case TOKEN_EXPIRED:
                return "Token has expired";
            case TOKEN_ERROR:
                return "Error processing token";
            default:
                return "Unknown token status";
        }
    }


    public static long extractExpirationTime(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return 0;
            }
            String decodedPayload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            JsonObject payload = new Gson().fromJson(decodedPayload, JsonObject.class);
            return payload.get("exp").getAsLong();
        } catch (Exception e) {
            return 0;
        }
    }

    public static String refreshToken(String token) {
        String username = validateToken(token);
        if (username != null) {
            return generateToken(username);
        }
        return null;
    }


    private static String createSignature(String content) {
        try {
            // Create a signature using HMAC SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((content + SECRET_KEY).getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }


    public static String extractUsername(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return null;
            }
            String decodedPayload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            JsonObject payload = new Gson().fromJson(decodedPayload, JsonObject.class);
            return payload.get("sub").getAsString();
        } catch (Exception e) {
            return null;
        }
    }
}
