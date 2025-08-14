package org.example.server;

import io.javalin.Javalin;
import org.example.common.models.App;
import org.example.server.GameServers.AppWebSocket;
import org.example.utils.MongoDBConnection;
import org.example.utils.NetworkUtils;

public class ServerMain {
    private static Javalin app;
    private static MessageHandler messageHandler;
    private static AppWebSocket webSocket;
    private static ServerConfig config;

    public static void main(String[] args) {
        // nokaram be mola
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
            String host = config.getServerHost();
            app.start(host, port);



            // Display network information
            NetworkUtils.printNetworkInfo();

            // Display client connection instructions

            // Display firewall instructions

            // Keep the server running

            // Keep main thread alive
            try {
                Thread.currentThread().join();
            } catch (InterruptedException e) {
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

    }

    public static void shutdown() {

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
