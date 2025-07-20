package org.example.common.network.routes;

/**
 * Defines a network route for request routing.
 * Routes are used to direct requests to appropriate handlers.
 */
public class Route {
    private final String path;
    private final RouteType type;
    private final String handler;
    
    public Route(String path, RouteType type, String handler) {
        this.path = path;
        this.type = type;
        this.handler = handler;
    }
    
    public String getPath() {
        return path;
    }
    
    public RouteType getType() {
        return type;
    }
    
    public String getHandler() {
        return handler;
    }
    
    public enum RouteType {
        AUTHENTICATION,
        GAME_MANAGEMENT,
        PLAYER_ACTIONS,
        LOBBY_MANAGEMENT,
        TRADING,
        COMMUNICATION,
        SYSTEM
    }
    
    @Override
    public String toString() {
        return "Route{path='" + path + "', type=" + type + ", handler='" + handler + "'}";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Route route = (Route) obj;
        return path.equals(route.path) && type == route.type;
    }
    
    @Override
    public int hashCode() {
        return path.hashCode() * 31 + type.hashCode();
    }
} 