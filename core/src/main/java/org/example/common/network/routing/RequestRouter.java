package org.example.common.network.routing;

import org.example.common.network.requests.Request;
import org.example.common.network.responses.Response;
import org.example.common.network.routes.Route;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Routes requests to appropriate handlers based on the request route.
 */
public class RequestRouter {
    private final Map<Route.RouteType, RequestHandler> handlers;
    private final Map<String, RequestHandler> pathHandlers;
    
    public RequestRouter() {
        this.handlers = new HashMap<>();
        this.pathHandlers = new HashMap<>();
    }
    
    /**
     * Register a handler for a specific route type.
     */
    public void registerHandler(Route.RouteType routeType, RequestHandler handler) {
        handlers.put(routeType, handler);
    }
    
    /**
     * Register a handler for a specific path.
     */
    public void registerPathHandler(String path, RequestHandler handler) {
        pathHandlers.put(path, handler);
    }
    
    /**
     * Route a request to the appropriate handler.
     */
    public CompletableFuture<Response> route(Request request) {
        Route route = request.getRoute();
        
        // First try path-based routing
        RequestHandler pathHandler = pathHandlers.get(route.getPath());
        if (pathHandler != null) {
            return pathHandler.handle(request);
        }
        
        // Fall back to type-based routing
        RequestHandler typeHandler = handlers.get(route.getType());
        if (typeHandler != null) {
            return typeHandler.handle(request);
        }
        
        // No handler found
        CompletableFuture<Response> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("No handler found for route: " + route.getPath()));
        return future;
    }
    
    /**
     * Check if a route has a registered handler.
     */
    public boolean hasHandler(Request request) {
        Route route = request.getRoute();
        return pathHandlers.containsKey(route.getPath()) || handlers.containsKey(route.getType());
    }
} 