package org.example.common.network.requests;

import org.example.common.network.responses.AuthenticationResponse;
import org.example.common.network.responses.Response;
import org.example.common.network.routes.Route;

/**
 * Request for user authentication operations.
 */
public class AuthenticationRequest extends Request {
    private final String username;
    private final String token;
    private final AuthType authType;
    
    public AuthenticationRequest(String username, String token, AuthType authType, String sourceId) {
        super(new Route("/auth", Route.RouteType.AUTHENTICATION, "AuthenticationHandler"), sourceId);
        this.username = username;
        this.token = token;
        this.authType = authType;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getToken() {
        return token;
    }
    
    public AuthType getAuthType() {
        return authType;
    }
    
    @Override
    public Class<? extends Response> getExpectedResponseType() {
        return AuthenticationResponse.class;
    }
    
    public enum AuthType {
        LOGIN,
        LOGOUT,
        VALIDATE
    }
} 