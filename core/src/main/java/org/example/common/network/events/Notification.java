package org.example.common.network.events;

/**
 * Base class for server-to-client notifications.
 * Used for real-time updates that don't require a response.
 */
public abstract class Notification extends NetworkEvent {
    private final NotificationType notificationType;
    private final String targetId; // null for broadcast
    
    protected Notification(NotificationType notificationType, String sourceId, String targetId) {
        super(EventType.NOTIFICATION, sourceId);
        this.notificationType = notificationType;
        this.targetId = targetId;
    }
    
    public NotificationType getNotificationType() {
        return notificationType;
    }
    
    public String getTargetId() {
        return targetId;
    }
    
    public boolean isBroadcast() {
        return targetId == null;
    }
    
    public enum NotificationType {
        PLAYER_MOVE,
        CHAT_MESSAGE,
        GAME_STATE_UPDATE,
        PLAYER_JOINED,
        PLAYER_LEFT,
        TRADE_REQUEST,
        SYSTEM_MESSAGE
    }
} 