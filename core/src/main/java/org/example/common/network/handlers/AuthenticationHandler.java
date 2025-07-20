package org.example.common.network.handlers;

import org.example.common.network.requests.AuthenticationRequest;
import org.example.common.network.requests.Request;
import org.example.common.network.responses.AuthenticationResponse;
import org.example.common.network.responses.Response;
import org.example.common.network.routing.RequestHandler;
import org.example.common.models.entities.User;
import org.example.utils.auth.JWTUtils;

import java.util.concurrent.CompletableFuture;

/**
 * Handler for authentication requests.
 */
public class AuthenticationHandler implements RequestHandler {
    
    @Override
    public CompletableFuture<Response> handle(Request request) {
        if (!(request instanceof AuthenticationRequest)) {
            CompletableFuture<Response> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalArgumentException("Invalid request type"));
            return future;
        }
        
        AuthenticationRequest authRequest = (AuthenticationRequest) request;
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                return processAuthentication(authRequest);
            } catch (Exception e) {
                return AuthenticationResponse.error(authRequest.getRequestId(), 
                                                   authRequest.getSourceId(), e.getMessage());
            }
        });
    }
    
    private AuthenticationResponse processAuthentication(AuthenticationRequest request) {
        String token = request.getToken();
        String username = request.getUsername();
        
        // Validate JWT token
        String tokenStatus = JWTUtils.validateToken(token);
        if (!tokenStatus.equals(JWTUtils.TOKEN_VALID)) {
            return AuthenticationResponse.error(request.getRequestId(), 
                                               request.getSourceId(), "Invalid or expired token");
        }
        
        // Get user from database (this would need to be implemented based on your User model)
        User user = getUserFromDatabase(username);
        if (user == null) {
            return AuthenticationResponse.error(request.getRequestId(), 
                                               request.getSourceId(), "User not found");
        }
        
        // Generate session ID
        String sessionId = generateSessionId();
        
        return AuthenticationResponse.success(request.getRequestId(), 
                                            request.getSourceId(), user, sessionId);
    }
    
    private User getUserFromDatabase(String username) {
        // This would need to be implemented based on your User model and database
        // For now, return null to indicate user not found
        return null;
    }
    
    private String generateSessionId() {
        return "session_" + System.currentTimeMillis() + "_" + 
               java.util.UUID.randomUUID().toString().substring(0, 8);
    }
} 