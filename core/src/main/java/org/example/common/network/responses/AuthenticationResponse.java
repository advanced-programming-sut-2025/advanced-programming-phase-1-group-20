package org.example.common.network.responses;

import org.example.common.models.entities.User;

/**
 * Response for authentication operations.
 */
public class AuthenticationResponse extends Response {
    private final User user;
    private final String sessionId;
    private final boolean authenticated;
    
    public AuthenticationResponse(String requestId, String sourceId, ResponseStatus status, 
                                String message, User user, String sessionId, boolean authenticated) {
        super(requestId, sourceId, status, message);
        this.user = user;
        this.sessionId = sessionId;
        this.authenticated = authenticated;
    }
    
    public User getUser() {
        return user;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public boolean isAuthenticated() {
        return authenticated;
    }
    
    public static AuthenticationResponse success(String requestId, String sourceId, 
                                               User user, String sessionId) {
        return new AuthenticationResponse(requestId, sourceId, ResponseStatus.SUCCESS, 
                                        "Authentication successful", user, sessionId, true);
    }
    
    public static AuthenticationResponse error(String requestId, String sourceId, String errorMessage) {
        return new AuthenticationResponse(requestId, sourceId, ResponseStatus.ERROR, 
                                        errorMessage, null, null, false);
    }
} 