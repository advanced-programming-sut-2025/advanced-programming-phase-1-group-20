package org.example.client.controllers;

import com.badlogic.gdx.Gdx;
import org.example.client.network.ConnectionManager;
import org.example.client.network.NetworkClient;
import org.example.client.views.LobbyMenuScreen;
import org.example.common.Lobby.Lobby;
import org.example.common.models.*;
import org.example.common.models.entities.User;

import java.util.List;

public class LobbyMenuController implements Controller {
    private LobbyMenuScreen view;
    private final NetworkClient networkClient;
    private final ConnectionManager connectionManager;
    private User currentUser;

    public LobbyMenuController() {
        this.networkClient = NetworkClient.getInstance();
        this.connectionManager = ConnectionManager.getInstance();
    }

    public void setView(LobbyMenuScreen view) {
        this.view = view;
    }

    @Override
    public void setupListeners() {
        // Setup listeners will be called by the view
    }

    // =====================
    // LOBBY OPERATIONS
    // =====================

    public void createLobby(String lobbyName, boolean isPrivate, boolean isVisible, String password) {
        if (!connectionManager.isAuthenticated()) {
            showError("Not authenticated");
            return;
        }

        if (lobbyName == null || lobbyName.trim().isEmpty()) {
            showError("Lobby name is required");
            return;
        }

        if (isPrivate && (password == null || password.trim().isEmpty())) {
            showError("Password is required for private lobbies");
            return;
        }

        try {
            Message message = new Message();
            message.setType(Message.Type.CREATE_LOBBY);
            message.putInBody("lobbyName", lobbyName.trim());
            message.putInBody("isPrivate", isPrivate);
            message.putInBody("isVisible", isVisible);
            if (isPrivate) {
                message.putInBody("password", password.trim());
            }

            networkClient.sendMessage(message);

            if (view != null) {
                view.showStatus("Creating lobby...");
            }
        } catch (Exception e) {
            showError("Failed to create lobby: " + e.getMessage());
        }
    }

    public void joinLobby(String lobbyId, String password) {
        if (!connectionManager.isAuthenticated()) {
            showError("Not authenticated");
            return;
        }

        if (lobbyId == null || lobbyId.trim().isEmpty()) {
            showError("Lobby ID is required");
            return;
        }

        try {
            Message message = new Message();
            message.setType(Message.Type.JOIN_LOBBY);
            message.putInBody("lobbyId", lobbyId.trim());
            if (password != null && !password.trim().isEmpty()) {
                message.putInBody("password", password.trim());
            }

            networkClient.sendMessage(message);

            if (view != null) {
                view.showStatus("Joining lobby...");
            }
        } catch (Exception e) {
            showError("Failed to join lobby: " + e.getMessage());
        }
    }

    public void leaveLobby() {
        if (!connectionManager.isAuthenticated()) {
            showError("Not authenticated");
            return;
        }

        try {
            Message message = new Message();
            message.setType(Message.Type.LEAVE_LOBBY);

            networkClient.sendMessage(message);

            if (view != null) {
                view.showStatus("Leaving lobby...");
            }
        } catch (Exception e) {
            showError("Failed to leave lobby: " + e.getMessage());
        }
    }

    public void refreshLobbies() {
        if (!connectionManager.isAuthenticated()) {
            showError("Not authenticated");
            return;
        }

        try {
            Message message = new Message();
            message.setType(Message.Type.LIST_LOBBIES);

            networkClient.sendMessage(message);

            if (view != null) {
                view.showStatus("Refreshing lobby list...");
            }
        } catch (Exception e) {
            showError("Failed to refresh lobbies: " + e.getMessage());
        }
    }

    public void searchLobbies(String searchTerm, String lobbyId) {
        if (!connectionManager.isAuthenticated()) {
            showError("Not authenticated");
            return;
        }

        try {
            Message message = new Message();
            message.setType(Message.Type.SEARCH_LOBBY);

            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                message.putInBody("searchTerm", searchTerm.trim());
            }

            if (lobbyId != null && !lobbyId.trim().isEmpty()) {
                message.putInBody("lobbyId", lobbyId.trim());
            }

            networkClient.sendMessage(message);

            if (view != null) {
                view.showStatus("Searching lobbies...");
            }
        } catch (Exception e) {
            showError("Failed to search lobbies: " + e.getMessage());
        }
    }

    public void setPlayerReady(boolean ready) {
        if (!connectionManager.isAuthenticated()) {
            showError("Not authenticated");
            return;
        }

        try {
            Message message = new Message();
            message.setType(Message.Type.PLAYER_READY);
            message.putInBody("ready", ready);

            networkClient.sendMessage(message);

            if (view != null) {
                view.showStatus(ready ? "Setting ready..." : "Setting not ready...");
            }
        } catch (Exception e) {
            showError("Failed to update ready status: " + e.getMessage());
        }
    }

    public void startGame() {
        if (!connectionManager.isAuthenticated()) {
            showError("Not authenticated");
            return;
        }

        try {
            Message message = new Message();
            message.setType(Message.Type.START_LOBBY_GAME);

            networkClient.sendMessage(message);

            if (view != null) {
                view.showStatus("Starting game...");
            }
        } catch (Exception e) {
            showError("Failed to start game: " + e.getMessage());
        }
    }

    // =====================
    // MESSAGE HANDLING
    // =====================

    public void handleLobbyMessage(Message message) {
        if (view == null) return;

        Gdx.app.postRunnable(() -> {
            try {
                switch (message.getType()) {
                    case SUCCESS:
                        handleSuccessMessage(message);
                        break;
                    case ERROR:
                        handleErrorMessage(message);
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                System.err.println("Error handling lobby message: " + e.getMessage());
            }
        });
    }

    private void handleSuccessMessage(Message message) {
        String messageText = message.getFromBody("message");

        if (messageText == null) return;

        if (messageText.contains("Lobby created successfully")) {
            Lobby lobby = message.getFromBody("lobby");
            view.onLobbyCreated(lobby);
        } else if (messageText.contains("Joined lobby successfully")) {
            Lobby lobby = message.getFromBody("lobby");
            view.onLobbyJoined(lobby);
        } else if (messageText.contains("Left lobby successfully")) {
            view.onLobbyLeft();
        } else if (messageText.contains("Lobby list retrieved")) {
            List<Lobby> lobbies = message.getFromBody("lobbies");
            view.onLobbiesReceived(lobbies);
        } else if (messageText.contains("Search completed")) {
            List<Lobby> lobbies = message.getFromBody("lobbies");
            view.onSearchResults(lobbies);
        } else if (messageText.contains("Lobby updated")) {
            Lobby lobby = message.getFromBody("lobby");
            view.onLobbyUpdated(lobby);
        } else if (messageText.contains("Player ready") || messageText.contains("Player not ready")) {
            Boolean ready = message.getFromBody("ready");
            view.onPlayerReadyChanged(ready);
        } else if (messageText.contains("Game starting")) {
            String gameSessionId = message.getFromBody("gameSessionId");
            view.onGameStarting(gameSessionId);
        }

        view.showStatus(messageText);
    }

    private void handleErrorMessage(Message message) {
        String errorMessage = message.getFromBody("message");
        showError(errorMessage != null ? errorMessage : "Unknown error");
    }

    // =====================
    // UTILITY METHODS
    // =====================

    public void update(float deltaTime) {
        // Update connection manager
        connectionManager.update();

        // Process any pending network messages
        networkClient.update();
    }

    private void showError(String error) {
        if (view != null) {
            view.showError(error);
        }
        System.err.println("Lobby Error: " + error);
    }

    public boolean isConnected() {
        return connectionManager.isConnected();
    }

    public boolean isAuthenticated() {
        return connectionManager.isAuthenticated();
    }

    public User getCurrentUser() {
        return connectionManager.getAuthenticatedUser();
    }

    public void goBack() {
        // Navigate back to multiplayer menu
        if (view != null) {
            view.goBackToMultiplayerMenu();
        }
    }
}
