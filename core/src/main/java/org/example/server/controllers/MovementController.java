package org.example.server.controllers;

import org.example.common.models.Player.Player;
import org.example.common.models.common.Location;
import org.example.common.models.entities.Game;
import org.example.common.models.Message;
import org.example.server.GameSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MovementController {
    private final Game gameInstance;
    private final GameSession gameSession;
    private final Map<String, Long> lastMovementTime;
    private final int movementThrottleMs;

    public MovementController(Game gameInstance, GameSession gameSession, int movementThrottleMs) {
        this.gameInstance = gameInstance;
        this.gameSession = gameSession;
        this.lastMovementTime = new ConcurrentHashMap<>();
        this.movementThrottleMs = movementThrottleMs;
    }

    public boolean handlePlayerMove(String username, Message message) {
        System.out.println("DEBUG: MovementController.handlePlayerMove() - Processing movement for player: " + username);

        float x = message.getFromBody("x");
        float y = message.getFromBody("y");

        Player player = gameInstance.getPlayerByUsername(username);
        if (player == null) {
            System.err.println("DEBUG: Player " + username + " not found for movement");
            return false;
        }

        // Check movement throttling to prevent excessive updates (but allow real-time movement)
        if (isMovementThrottled(username)) {
            System.out.println("DEBUG: MovementController - Movement throttled for " + username + ", but still processing for real-time updates");
            // Continue processing even if throttled to ensure real-time updates
        }

        // Update player position on server immediately
        updatePlayerPosition(player, x, y);

        // Broadcast movement to other players immediately for real-time updates
        broadcastPlayerMovement(username, x, y);

        // Update last movement time
        updateLastMovementTime(username);

        System.out.println("DEBUG: MovementController - Player " + username + " moved to (" + x + ", " + y + ") - REAL-TIME UPDATE");
        return true;
    }

    /**
     * Check if movement should be throttled for a player
     */
    private boolean isMovementThrottled(String username) {
        long currentTime = System.currentTimeMillis();
        Long lastTime = lastMovementTime.get(username);

        if (lastTime != null && (currentTime - lastTime) < movementThrottleMs) {
            System.out.println("DEBUG: MovementController - Throttled movement for " + username);
            return true;
        }
        return false;
    }

    /**
     * Update player position and location
     */
    private void updatePlayerPosition(Player player, float x, float y) {
        // Update pixel coordinates
        player.setPosX(x);
        player.setPosY(y);

        // Update tile coordinates for map positioning
        int tileX = Math.round(x / 60);
        int tileY = Math.round(y / 60);
        player.setLocation(new Location(tileX, tileY, player.getLocation().getTile()));
    }

    /**
     * Broadcast player movement to other players
     */
    private void broadcastPlayerMovement(String username, float x, float y) {
        try {
            int tileX = Math.round(x / 60);
            int tileY = Math.round(y / 60);

            Message moveMessage = new Message();
            moveMessage.setType(Message.Type.PLAYER_MOVE);
            moveMessage.putInBody("username", username);
            moveMessage.putInBody("x", x);
            moveMessage.putInBody("y", y);
            moveMessage.putInBody("tileX", tileX);
            moveMessage.putInBody("tileY", tileY);
            moveMessage.putInBody("timestamp", System.currentTimeMillis());

            // Broadcast to all other players
            gameSession.broadcastToOthers(username, moveMessage);

            System.out.println("DEBUG: MovementController - Broadcasted movement for " + username + " to (" + x + ", " + y + ")");
        } catch (Exception e) {
            System.err.println("Error broadcasting player movement: " + e.getMessage());
        }
    }

    private void updateLastMovementTime(String username) {
        lastMovementTime.put(username, System.currentTimeMillis());
    }

    public Long getLastMovementTime(String username) {
        return lastMovementTime.get(username);
    }


    public void clearPlayerMovementHistory(String username) {
        lastMovementTime.remove(username);
    }

    public boolean isValidMovement(float x, float y) {
        // Basic bounds checking - can be enhanced with more sophisticated validation
        return x >= 0 && y >= 0 && x < 10000 && y < 10000; // Reasonable bounds
    }
}
