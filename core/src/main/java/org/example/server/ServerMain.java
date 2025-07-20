package org.example.server;

import io.javalin.Javalin;
import org.example.common.models.App;
import org.example.server.GameServers.AppWebSocket;
import org.example.utils.MongoDBConnection;

public class ServerMain {
    private static Javalin app;
    private static MessageHandler messageHandler;
    private static AppWebSocket webSocket;
    private static ServerConfig config;
    
    public static void main(String[] args) {
        System.out.println("Starting Stardew Valley Multiplayer Server...");
        
        try {
            // Initialize configuration
            config = ServerConfig.getInstance();
            
            // Initialize App (loads users, items, etc.)
            App.initialize();
            
            // Initialize message handler
            messageHandler = new MessageHandler();
            
            // Create Javalin app
            app = Javalin.create();
            
            // Set up WebSocket
            webSocket = new AppWebSocket(app, messageHandler);
            
            // Set up REST API endpoints
            setupRestEndpoints();
            
            // Add shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(ServerMain::shutdown));
            
            // Start server
            int port = config.getServerPort();
            app.start(port);
            
            System.out.println("Server started successfully on port " + port);
            System.out.println("WebSocket endpoint: " + config.getWebSocketPath());
            System.out.println("Debug mode: " + config.isDebugMode());
            
            // Keep the server running
            System.out.println("Server is running. Press Ctrl+C to stop.");
            
            // Keep main thread alive
            try {
                Thread.currentThread().join();
            } catch (InterruptedException e) {
                System.out.println("Server interrupted, shutting down...");
                shutdown();
            }
            
        } catch (Exception e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private static void setupRestEndpoints() {
        // Health check endpoint
        app.get("/health", ctx -> {
            ctx.json("{ \"status\": \"OK\", \"message\": \"Server is running\" }");
        });
        
        // Server info endpoint
        app.get("/info", ctx -> {
            ctx.json("{ \"name\": \"Stardew Valley Multiplayer Server\", " +
                    "\"version\": \"1.0.0\", " +
                    "\"maxPlayersPerGame\": " + config.getMaxPlayersPerGame() + ", " +
                    "\"activeGames\": " + messageHandler.getGameSessions().size() + ", " +
                    "\"connectedPlayers\": " + messageHandler.getPlayerConnections().size() + " }");
        });
        
        // List active games endpoint
        app.get("/games", ctx -> {
            ctx.json(messageHandler.getGameSessions().keySet());
        });
        
        // List lobbies endpoint for debugging
        app.get("/lobbies", ctx -> {
            LobbyManager lobbyManager = LobbyManager.getInstance();
            ctx.json("{ \"totalLobbies\": " + lobbyManager.getTotalLobbies() + ", " +
                    "\"activeLobbies\": " + lobbyManager.getActiveLobbies() + ", " +
                    "\"playersInLobbies\": " + lobbyManager.getTotalPlayersInLobbies() + " }");
        });
        
        // CORS preflight for all endpoints
        app.options("/*", ctx -> {
            ctx.header("Access-Control-Allow-Origin", "*");
            ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            ctx.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        });
        
        System.out.println("REST endpoints configured:");
        System.out.println("  GET /health - Health check");
        System.out.println("  GET /info - Server information");
        System.out.println("  GET /games - List active games");
        System.out.println("  GET /lobbies - List lobbies info");
    }
    
    public static void shutdown() {
        System.out.println("Shutting down server...");
        
        try {
            // Shutdown message handler (closes all games and connections)
            if (messageHandler != null) {
                messageHandler.shutdown();
            }
            
            // Shutdown WebSocket
            if (webSocket != null) {
                webSocket.shutdown();
            }
            
            // Close MongoDB connection
            MongoDBConnection.closeConnection();
            
            // Stop Javalin app
            if (app != null) {
                app.stop();
            }
            
            System.out.println("Server shutdown completed");
            
        } catch (Exception e) {
            System.err.println("Error during shutdown: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Getters for testing or external access
    public static Javalin getApp() {
        return app;
    }
    
    public static MessageHandler getMessageHandler() {
        return messageHandler;
    }
    
    public static ServerConfig getConfig() {
        return config;
    }
} 