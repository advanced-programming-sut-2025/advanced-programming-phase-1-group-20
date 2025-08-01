package org.example.server;

import io.github.cdimascio.dotenv.Dotenv;

public class ServerConfig {
    private static ServerConfig instance;
    private final Dotenv dotenv;

    private ServerConfig() {
        this.dotenv = Dotenv.configure()
                .directory(".")
                .ignoreIfMissing()
                .load();
    }

    public static ServerConfig getInstance() {
        if (instance == null) {
            instance = new ServerConfig();
        }
        return instance;
    }

    // Server Configuration
    public int getServerPort() {
        return Integer.parseInt(dotenv.get("SERVER_PORT", "8080"));
    }

    public String getServerHost() {
        return dotenv.get("SERVER_HOST", "0.0.0.0"); // Changed from "localhost" to "0.0.0.0" to allow connections from all devices
    }

    // Database Configuration
    public String getMongoDbUri() {
        return dotenv.get("MONGODB_URI", "mongodb://localhost:27017");
    }

    public String getDatabaseName() {
        return dotenv.get("DATABASE_NAME", "stardew_valley_db");
    }

    // Security Configuration
    public String getJwtSecret() {
        return dotenv.get("JWT_SECRET", "stardew_valley_secret_key_for_jwt_authentication");
    }

    // WebSocket Configuration
    public String getWebSocketPath() {
        return dotenv.get("WEBSOCKET_PATH", "/ws/game");
    }

    // Game Configuration
    public int getMaxPlayersPerGame() {
        return Integer.parseInt(dotenv.get("MAX_PLAYERS_PER_GAME", "4"));
    }

    public int getGameTickRate() {
        return Integer.parseInt(dotenv.get("GAME_TICK_RATE", "10")); // Reduced from 20 to 10 for better performance
    }

    public int getHeartbeatInterval() {
        return Integer.parseInt(dotenv.get("HEARTBEAT_INTERVAL", "30"));
    }

    // New configuration for movement update throttling
    public int getMovementUpdateThrottle() {
        return Integer.parseInt(dotenv.get("MOVEMENT_UPDATE_THROTTLE", "16")); // Minimum 16ms between movement updates (60 FPS)
    }

    // Development Configuration
    public boolean isDebugMode() {
        return Boolean.parseBoolean(dotenv.get("DEBUG_MODE", "false"));
    }

    public String getLogLevel() {
        return dotenv.get("LOG_LEVEL", "INFO");
    }
}
