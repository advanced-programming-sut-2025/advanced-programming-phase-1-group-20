package org.example.client.controllers;

import com.badlogic.gdx.Gdx;
import org.example.client.network.ConnectionManager;
import org.example.client.network.NetworkClient;
import org.example.client.views.LobbyMenuScreen;
import org.example.common.Lobby.Lobby;
import org.example.common.models.*;
import org.example.common.models.entities.User;

import java.util.ArrayList;
import java.util.List;

import static org.example.common.Lobby.LobbyMessage.LobbyMessageType.LOBBY_UPDATED;

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
        System.out.println("Creating lobby - Auth: " + connectionManager.isAuthenticated() + ", Connected: " + connectionManager.isConnected());

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

            System.out.println("Sending CREATE_LOBBY message: " + message.getType());
            networkClient.sendMessage(message);

            if (view != null) {
                view.showStatus("Creating lobby...");
            }
        } catch (Exception e) {
            System.err.println("Exception in createLobby: " + e.getMessage());
            e.printStackTrace();
            showError("Failed to create lobby: " + e.getMessage());
        }
    }

    public void joinLobby(String lobbyId, String password) {
        System.out.println("Joining lobby - Auth: " + connectionManager.isAuthenticated() + ", Connected: " + connectionManager.isConnected());

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

            System.out.println("Sending JOIN_LOBBY message for lobby: " + lobbyId);
            networkClient.sendMessage(message);

            if (view != null) {
                view.showStatus("Joining lobby...");
            }
        } catch (Exception e) {
            System.err.println("Exception in joinLobby: " + e.getMessage());
            e.printStackTrace();
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

            System.out.println("Sending LEAVE_LOBBY message");
            networkClient.sendMessage(message);

            if (view != null) {
                view.showStatus("Leaving lobby...");
            }
        } catch (Exception e) {
            System.err.println("Exception in leaveLobby: " + e.getMessage());
            e.printStackTrace();
            showError("Failed to leave lobby: " + e.getMessage());
        }
    }

    public void refreshLobbies() {
        System.out.println("Refreshing lobbies - Auth: " + connectionManager.isAuthenticated() + ", Connected: " + connectionManager.isConnected());

        if (!connectionManager.isAuthenticated()) {
            showError("Not authenticated");
            return;
        }

        try {
            Message message = new Message();
            message.setType(Message.Type.LIST_LOBBIES);

            System.out.println("Sending LIST_LOBBIES message");
            networkClient.sendMessage(message);

            if (view != null) {
                view.showStatus("Refreshing lobby list...");
            }
        } catch (Exception e) {
            System.err.println("Exception in refreshLobbies: " + e.getMessage());
            e.printStackTrace();
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

            System.out.println("Sending SEARCH_LOBBY message");
            networkClient.sendMessage(message);

            if (view != null) {
                view.showStatus("Searching lobbies...");
            }
        } catch (Exception e) {
            System.err.println("Exception in searchLobbies: " + e.getMessage());
            e.printStackTrace();
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

            System.out.println("Sending PLAYER_READY message: " + ready);
            networkClient.sendMessage(message);

            if (view != null) {
                view.showStatus(ready ? "Setting ready..." : "Setting not ready...");
            }
        } catch (Exception e) {
            System.err.println("Exception in setPlayerReady: " + e.getMessage());
            e.printStackTrace();
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

            System.out.println("Sending START_LOBBY_GAME message");
            networkClient.sendMessage(message);

            if (view != null) {
                view.showStatus("Starting game...");
            }
        } catch (Exception e) {
            System.err.println("Exception in startGame: " + e.getMessage());
            e.printStackTrace();
            showError("Failed to start game: " + e.getMessage());
        }
    }

    // =====================
    // MESSAGE HANDLING
    // =====================

    public void handleLobbyMessage(Message message) {
        if (view == null) return;

        System.out.println("Received lobby message: " + message.getType());

        Gdx.app.postRunnable(() -> {
            try {
                switch (message.getType()) {
                    case SUCCESS:
                        handleSuccessMessage(message);
                        break;
                    case ERROR:
                        handleErrorMessage(message);
                        break;

                    // Handle specific lobby message types
                    case CREATE_LOBBY:
                        handleCreateLobbyResponse(message);
                        break;
                    case LIST_LOBBIES:
                        handleListLobbiesResponse(message);
                        break;
                    case JOIN_LOBBY:
                        handleJoinLobbyResponse(message);
                        break;
                    case LEAVE_LOBBY:
                        handleLeaveLobbyResponse(message);
                        break;
                    case SEARCH_LOBBY:
                        handleSearchLobbyResponse(message);
                        break;
                    case PLAYER_READY:
                        handlePlayerReadyResponse(message);
                        break;
                    case START_LOBBY_GAME:
                        handleStartGameResponse(message);
                        break;

                    default:
                        System.out.println("Unhandled lobby message type: " + message.getType());
                        break;
                }
            } catch (Exception e) {
                System.err.println("Error handling lobby message: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void handleCreateLobbyResponse(Message message) {
        System.out.println("Handling CREATE_LOBBY response");
        String status = message.getFromBody("status");
        if ("success".equals(status)) {
            Lobby lobby = message.getFromBody("lobby");
            if (lobby != null) {
                System.out.println("Lobby created successfully: " + lobby.getName());
                view.onLobbyCreated(lobby);
            } else {
                System.err.println("Lobby created but no lobby data received");
                showError("Lobby created but no lobby data received");
            }
        } else {
            String error = message.getFromBody("error");
            System.err.println("Failed to create lobby: " + error);
            showError("Failed to create lobby: " + (error != null ? error : "Unknown error"));
        }
    }

    private void handleListLobbiesResponse(Message message) {
        System.out.println("Handling LIST_LOBBIES response");
        String status = message.getFromBody("status");
        if ("success".equals(status)) {
            List<Lobby> lobbies = message.getFromBody("lobbies");
            if (lobbies != null) {
                System.out.println("Received " + lobbies.size() + " lobbies");
                view.onLobbiesReceived(lobbies);
            } else {
                System.out.println("No lobbies received, showing empty list");
                view.onLobbiesReceived(new ArrayList<>());
            }
        } else {
            String error = message.getFromBody("error");
            System.err.println("Failed to get lobby list: " + error);
            showError("Failed to get lobby list: " + (error != null ? error : "Unknown error"));
        }
    }

    private void handleJoinLobbyResponse(Message message) {
        System.out.println("Handling JOIN_LOBBY response");
        String status = message.getFromBody("status");
        if ("success".equals(status)) {
            Lobby lobby = message.getFromBody("lobby");
            if (lobby != null) {
                System.out.println("Joined lobby successfully: " + lobby.getName());
                view.onLobbyJoined(lobby);
            } else {
                System.err.println("Joined lobby but no lobby data received");
                showError("Joined lobby but no lobby data received");
            }
        } else {
            String error = message.getFromBody("error");
            System.err.println("Failed to join lobby: " + error);
            showError("Failed to join lobby: " + (error != null ? error : "Unknown error"));
        }
    }

    private void handleLeaveLobbyResponse(Message message) {
        System.out.println("Handling LEAVE_LOBBY response");
        String status = message.getFromBody("status");
        if ("success".equals(status)) {
            System.out.println("Left lobby successfully");
            view.onLobbyLeft();
        } else {
            String error = message.getFromBody("error");
            System.err.println("Failed to leave lobby: " + error);
            showError("Failed to leave lobby: " + (error != null ? error : "Unknown error"));
        }
    }

    private void handleSearchLobbyResponse(Message message) {
        System.out.println("Handling SEARCH_LOBBY response");
        String status = message.getFromBody("status");
        if ("success".equals(status)) {
            List<Lobby> lobbies = message.getFromBody("lobbies");
            if (lobbies != null) {
                System.out.println("Search found " + lobbies.size() + " lobbies");
                view.onSearchResults(lobbies);
            } else {
                System.out.println("Search completed with no results");
                view.onSearchResults(new ArrayList<>());
            }
        } else {
            String error = message.getFromBody("error");
            System.err.println("Failed to search lobbies: " + error);
            showError("Failed to search lobbies: " + (error != null ? error : "Unknown error"));
        }
    }

    private void handlePlayerReadyResponse(Message message) {
        System.out.println("Handling PLAYER_READY response");
        String status = message.getFromBody("status");
        if ("success".equals(status)) {
            Boolean ready = message.getFromBody("ready");
            System.out.println("Player ready status updated: " + ready);
            view.onPlayerReadyChanged(ready != null ? ready : false);
        } else {
            String error = message.getFromBody("error");
            System.err.println("Failed to update ready status: " + error);
            showError("Failed to update ready status: " + (error != null ? error : "Unknown error"));
        }
    }

    private void handleStartGameResponse(Message message) {
        System.out.println("Handling START_LOBBY_GAME response");
        String status = message.getFromBody("status");
        if ("success".equals(status)) {
            String gameSessionId = message.getFromBody("gameSessionId");
            System.out.println("Game starting with session ID: " + gameSessionId);
            view.onGameStarting(gameSessionId);
        } else {
            String error = message.getFromBody("error");
            System.err.println("Failed to start game: " + error);
            showError("Failed to start game: " + (error != null ? error : "Unknown error"));
        }
    }

    private void handleLobbyUpdatedResponse(Message message) {
        System.out.println("Handling LOBBY_UPDATED response");
        Lobby lobby = message.getFromBody("lobby");
        if (lobby != null) {
            System.out.println("Lobby updated: " + lobby.getName());
            view.onLobbyUpdated(lobby);
        } else {
            System.err.println("Lobby updated but no lobby data received");
        }
    }

    private void handleSuccessMessage(Message message) {
        String messageText = message.getFromBody("message");

        if (messageText == null) return;

        System.out.println("Success message: " + messageText);

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
        System.err.println("Error message: " + errorMessage);
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
