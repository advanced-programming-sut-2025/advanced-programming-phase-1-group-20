package org.example.common.network.events;

/**
 * Notification for player movement updates.
 */
public class PlayerMoveNotification extends Notification {
    private final String username;
    private final float x;
    private final float y;
    private final long moveTimestamp;
    
    public PlayerMoveNotification(String username, float x, float y, String sourceId) {
        super(NotificationType.PLAYER_MOVE, sourceId, null); // Broadcast to all players
        this.username = username;
        this.x = x;
        this.y = y;
        this.moveTimestamp = System.currentTimeMillis();
    }
    
    public String getUsername() {
        return username;
    }
    
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
    }
    
    public long getMoveTimestamp() {
        return moveTimestamp;
    }
} 