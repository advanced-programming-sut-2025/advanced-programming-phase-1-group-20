package org.example.common.network.responses;

/**
 * Response for player movement operations.
 */
public class PlayerMoveResponse extends Response {
    private final String username;
    private final float x;
    private final float y;
    private final boolean valid;
    
    public PlayerMoveResponse(String requestId, String sourceId, ResponseStatus status, 
                            String message, String username, float x, float y, boolean valid) {
        super(requestId, sourceId, status, message);
        this.username = username;
        this.x = x;
        this.y = y;
        this.valid = valid;
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
    
    public boolean isValid() {
        return valid;
    }
    
    public static PlayerMoveResponse success(String requestId, String sourceId, 
                                           String username, float x, float y) {
        return new PlayerMoveResponse(requestId, sourceId, ResponseStatus.SUCCESS, 
                                    "Player move successful", username, x, y, true);
    }
    
    public static PlayerMoveResponse error(String requestId, String sourceId, String errorMessage) {
        return new PlayerMoveResponse(requestId, sourceId, ResponseStatus.ERROR, 
                                    errorMessage, null, 0, 0, false);
    }
} 