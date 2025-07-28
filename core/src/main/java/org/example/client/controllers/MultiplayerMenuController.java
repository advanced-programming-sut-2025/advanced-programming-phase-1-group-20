package org.example.client.controllers;

import org.example.client.Main;
import org.example.client.controllers.menu.LobbyMenuController;
import org.example.client.controllers.menu.MainMenuController;
import org.example.client.network.ClientMessageHandler;
import org.example.client.network.ConnectionManager;
import org.example.client.network.NetworkClient;
import org.example.client.views.MultiplayerMenuScreen;
import org.example.client.views.menu.LobbyMenuScreen;
import org.example.client.views.menu.MainMenuScreen;
import org.example.common.models.App;
import org.example.common.models.Message;
import org.example.common.models.entities.User;
import org.example.utils.AssetManager;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MultiplayerMenuController implements Controller, ClientMessageHandler.OnlinePlayersListener, ClientMessageHandler.LobbyMessageListener {
    private MultiplayerMenuScreen view;
    private final ConnectionManager connectionManager;

    public MultiplayerMenuController() {
        this.connectionManager = ConnectionManager.getInstance();

        // Set up online players listener
        NetworkClient.getInstance().getMessageHandler().setOnlinePlayersListener(this);
    }

    public void setView(MultiplayerMenuScreen view) {
        this.view = view;
    }

    @Override
    public void setupListeners() {
        connectionManager.getNetworkClient().getMessageHandler().setLobbyListener(this);
    }

    public void connectToServer(String host, int port) {
        if (view != null) {
            view.updateStatus("Connecting to " + host + ":" + port + "...", com.badlogic.gdx.graphics.Color.YELLOW);
        }

        CompletableFuture<Boolean> connectionFuture = connectionManager.connectToServer(host, port);

        connectionFuture.thenAccept(success -> {
            if (success) {
                // Connection successful, now authenticate
                authenticateUser();
            } else {
                if (view != null) {
                    view.showConnectionFailed("Could not connect to server");
                }
            }
        }).exceptionally(throwable -> {
            if (view != null) {
                view.showConnectionFailed("Connection error: " + throwable.getMessage());
            }
            return null;
        });
    }

    private void authenticateUser() {
        User loggedInUser = App.getLoggedInUser();
        if (loggedInUser == null) {
            if (view != null) {
                view.showAuthenticationFailed("No user logged in");
            }
            return;
        }

        if (view != null) {
            view.updateStatus("Authenticating...", com.badlogic.gdx.graphics.Color.YELLOW);
        }

        CompletableFuture<Boolean> authFuture = connectionManager.authenticateUser(loggedInUser);

        authFuture.thenAccept(success -> {
            if (success) {
                if (view != null) {
                    view.showAuthenticationSuccess();
                }
            } else {
                if (view != null) {
                    view.showAuthenticationFailed("Authentication failed");
                }
            }
        }).exceptionally(throwable -> {
            if (view != null) {
                view.showAuthenticationFailed("Authentication error: " + throwable.getMessage());
            }
            return null;
        });
    }

    public void disconnect() {
        connectionManager.disconnect();
        if (view != null) {
            view.updateStatus("Disconnected", com.badlogic.gdx.graphics.Color.RED);
        }
    }

    public void createGame() {
        System.out.println("DEBUG: createGame() called - redirecting to lobby creation");
        if (!connectionManager.isAuthenticated()) {
            System.out.println("DEBUG: Cannot create lobby - not authenticated");
            if (view != null) {
                view.showError("Cannot create lobby - not authenticated");
            }
            return;
        }

        System.out.println("DEBUG: About to call connectionManager.createLobby()");
        if (view != null) {
            view.updateStatus("Creating lobby...", com.badlogic.gdx.graphics.Color.YELLOW);
        }

        // Create a lobby instead of a game session
        User currentUser = connectionManager.getAuthenticatedUser();
        String lobbyName = currentUser != null ? currentUser.getUsername() + "'s Lobby" : "New Lobby";
        connectionManager.createLobby(lobbyName, false, true, null);
        System.out.println("DEBUG: createLobby() called");

        // Note: The actual lobby creation response will be handled by the ConnectionManager listeners
        if (view != null) {
            view.showGameCreated("Lobby creation pending...");
        }
    }

    public void joinGame(String gameId) {
        if (!connectionManager.canJoinGame()) {
            if (view != null) {
                view.showError("Cannot join game - not authenticated");
            }
            return;
        }

        if (gameId == null || gameId.trim().isEmpty()) {
            if (view != null) {
                view.showError("Please enter a valid game ID");
            }
            return;
        }

        if (view != null) {
            view.updateStatus("Joining game " + gameId + "...", com.badlogic.gdx.graphics.Color.YELLOW);
        }

        connectionManager.joinMultiplayerGame(gameId);

        // Note: The actual game join response will be handled by the ConnectionManager listeners
        if (view != null) {
            view.showGameJoined(gameId);
        }
    }

    public void leaveGame() {
        if (!connectionManager.isInGame()) {
            if (view != null) {
                view.showError("Not currently in a game");
            }
            return;
        }

        connectionManager.leaveMultiplayerGame();

        if (view != null) {
            view.updateStatus("Left game", com.badlogic.gdx.graphics.Color.GREEN);
        }
    }

    public void openLobbyMenu() {
        if (!connectionManager.isAuthenticated()) {
            if (view != null) {
                view.showError("Must be authenticated to access lobby system");
            }
            return;
        }

        // Navigate to lobby menu
        Main.getGame().getScreen().dispose();
        LobbyMenuController lobbyController = new LobbyMenuController();
        LobbyMenuScreen lobbyScreen = new LobbyMenuScreen(lobbyController, AssetManager.getAssetManager().getSkin());
        Main.getGame().setScreen(lobbyScreen);
    }

    public void goBackToMainMenu() {
        // Don't disconnect - keep connection alive for online players list
        // Only disconnect if explicitly requested

        // Navigate back to main menu
        User loggedInUser = App.getLoggedInUser();
        if (loggedInUser != null) {
            Main.getGame().getScreen().dispose();
            MainMenuController mainMenuController = new MainMenuController();
            MainMenuScreen mainMenuScreen = new MainMenuScreen(mainMenuController, AssetManager.getAssetManager().getSkin());
            Main.getGame().setScreen(mainMenuScreen);
        } else {
            // If no user is logged in, go to welcome screen
            Main.getGame().getScreen().dispose();
            org.example.client.controllers.WelcomeMenuController welcomeController = new org.example.client.controllers.WelcomeMenuController();
            org.example.client.views.WelcomeMenuScreen welcomeScreen = new org.example.client.views.WelcomeMenuScreen(welcomeController, AssetManager.getAssetManager().getSkin());
            Main.getGame().setScreen(welcomeScreen);
        }
    }

    public void disconnectAndGoBack() {
        // Explicitly disconnect when user wants to go offline
        if (connectionManager.isConnected()) {
            connectionManager.disconnect();
        }
        goBackToMainMenu();
    }

    // Utility methods for the view
    public boolean isConnected() {
        return connectionManager.isConnected();
    }

    public boolean isAuthenticated() {
        return connectionManager.isAuthenticated();
    }

    public boolean isInGame() {
        return connectionManager.isInGame();
    }

    public String getConnectionStatus() {
        return connectionManager.getConnectionStatusText();
    }

    public User getCurrentUser() {
        return App.getLoggedInUser();
    }


    public void requestOnlinePlayersList() {
        if (!connectionManager.isAuthenticated()) {
            return;
        }

        try {
            Message message = new Message();
            message.setType(Message.Type.REQUEST_PLAYERS_LIST);
            NetworkClient.getInstance().sendMessage(message);
        } catch (Exception e) {
            System.err.println("Failed to request online players list: " + e.getMessage());
        }
    }

    public void requestLobbyList() {
        if (connectionManager.isAuthenticated()) {
            connectionManager.requestLobbyList();
        }
    }

    public void handleOnlinePlayersListUpdate(List<Object> playersList) {
        if (view != null && playersList != null) {
            view.updateOnlinePlayersList(playersList);
        }
    }

    // OnlinePlayersListener implementation
    @Override
    public void onOnlinePlayersUpdate(List<Object> players) {
        handleOnlinePlayersListUpdate(players);
    }

    @Override
    public void onLobbyMessage(Message message) {
        System.out.println("DEBUG: MultiplayerMenuController.onLobbyMessage() called with type: " + message.getType());
        String messageText = message.getFromBody("message");

        if (messageText != null && messageText.contains("Lobby created successfully")) {
            System.out.println("DEBUG: Lobby created successfully!");
            if (view != null) {
                view.updateStatus("Lobby created successfully!", com.badlogic.gdx.graphics.Color.GREEN);
            }
        }

        // Handle lobby list response
        if (messageText != null && messageText.contains("Lobby list retrieved")) {
            System.out.println("DEBUG: Lobby list received!");
            Object lobbies = message.getFromBody("lobbies");
            if (lobbies != null && view != null) {
                view.updateLobbyList((List<Object>) lobbies);
            }
        }
    }
}
