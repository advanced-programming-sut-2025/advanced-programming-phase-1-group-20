package org.example.client.controllers.menu;

import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.example.client.Main;
import org.example.client.controllers.GameMenuController;
import org.example.client.network.ConnectionManager;
import org.example.client.network.NetworkClient;
import org.example.client.network.ClientMessageHandler;
import org.example.client.views.GameView;
import org.example.client.views.menu.LobbyMenuScreen;
import org.example.common.Lobby.Lobby;
import org.example.common.Lobby.LobbyPlayer;
import org.example.common.models.*;
import org.example.common.models.entities.Game;
import org.example.common.models.entities.User;
import org.example.common.models.Player.Player;
import org.example.utils.AssetManager;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LobbyMenuController implements ClientMessageHandler.LobbyMessageListener {
    private LobbyMenuScreen view;
    private final NetworkClient networkClient;
    private final ConnectionManager connectionManager;
    private final ClientMessageHandler messageHandler;
    private User currentUser;
    private final Gson gson;

    public LobbyMenuController() {
        this.networkClient = NetworkClient.getInstance();
        this.connectionManager = ConnectionManager.getInstance();
        this.messageHandler = networkClient.getMessageHandler();
        this.gson = new Gson();

        // Register this controller to receive lobby messages
        this.messageHandler.setLobbyListener(this);
    }

    public void setView(LobbyMenuScreen view) {
        this.view = view;
    }

    @Override
    public void onLobbyMessage(Message message) {
        handleLobbyMessage(message);
    }

    // Helper method to safely deserialize Lobby objects from JSON
    private Lobby deserializeLobby(Object lobbyObj) {
        System.out.println("DEBUG: deserializeLobby called with object type: " + (lobbyObj != null ? lobbyObj.getClass().getSimpleName() : "null"));

        if (lobbyObj == null) {
            System.out.println("DEBUG: lobbyObj is null");
            return null;
        }

        if (lobbyObj instanceof Lobby) {
            System.out.println("DEBUG: Object is already a Lobby instance");
            return (Lobby) lobbyObj;
        }

        try {
            // Convert to JSON string and back to Lobby object
            String jsonString = gson.toJson(lobbyObj);
            System.out.println("DEBUG: Converted to JSON: " + jsonString);

            // Try to parse with more detailed error handling
            try {
                Lobby lobby = gson.fromJson(jsonString, Lobby.class);
                System.out.println("DEBUG: Successfully deserialized Lobby: " + (lobby != null ? lobby.getName() : "null"));
                if (lobby != null) {
                    System.out.println("DEBUG: Lobby details - ID: " + lobby.getId() + ", Name: " + lobby.getName() + ", Players: " + lobby.getPlayers().size());
                }
                return lobby;
            } catch (Exception parseException) {
                System.err.println("DEBUG: Gson parsing failed: " + parseException.getMessage());
                parseException.printStackTrace();

                // Try manual parsing as fallback
                return createLobbyFromMap(lobbyObj);
            }
        } catch (Exception e) {
            System.err.println("DEBUG: Failed to deserialize Lobby: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Fallback method to create Lobby from Map-like object
    private Lobby createLobbyFromMap(Object lobbyObj) {
        System.out.println("DEBUG: Attempting manual lobby creation from map");
        try {
            // Create a new Lobby with basic info
            Lobby lobby = new Lobby();

            // Use reflection to get values from the map-like object
            if (lobbyObj instanceof java.util.Map) {
                java.util.Map<?, ?> map = (java.util.Map<?, ?>) lobbyObj;

                // Extract basic fields
                Object idObj = map.get("id");
                Object nameObj = map.get("name");
                Object adminIdObj = map.get("adminId");
                Object statusObj = map.get("status");

                if (idObj != null) lobby.setId(idObj.toString());
                if (nameObj != null) lobby.setName(nameObj.toString());
                if (adminIdObj != null) lobby.setAdminId(adminIdObj.toString());

                // Handle status enum
                if (statusObj != null) {
                    try {
                        Lobby.LobbyStatus status = Lobby.LobbyStatus.valueOf(statusObj.toString());
                        lobby.setStatus(status);
                    } catch (Exception e) {
                        System.err.println("DEBUG: Failed to parse status: " + statusObj);
                        lobby.setStatus(Lobby.LobbyStatus.WAITING);
                    }
                }

                System.out.println("DEBUG: Manually created lobby: " + lobby.getName());
                return lobby;
            }
        } catch (Exception e) {
            System.err.println("DEBUG: Manual lobby creation failed: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Helper method to safely deserialize List<Lobby> objects from JSON
    private List<Lobby> deserializeLobbyList(Object lobbiesObj) {
        System.out.println("DEBUG: deserializeLobbyList called with object type: " + (lobbiesObj != null ? lobbiesObj.getClass().getSimpleName() : "null"));

        if (lobbiesObj == null) {
            System.out.println("DEBUG: lobbiesObj is null");
            return new ArrayList<>();
        }

        if (lobbiesObj instanceof List) {
            List<?> list = (List<?>) lobbiesObj;
            if (list.isEmpty()) {
                System.out.println("DEBUG: List is empty");
                return new ArrayList<>();
            }

            // Check if it's already a List<Lobby>
            if (list.get(0) instanceof Lobby) {
                System.out.println("DEBUG: List already contains Lobby objects");
                return (List<Lobby>) list;
            }

            // Convert each element
            List<Lobby> result = new ArrayList<>();
            for (Object obj : list) {
                Lobby lobby = deserializeLobby(obj);
                if (lobby != null) {
                    result.add(lobby);
                }
            }
            System.out.println("DEBUG: Converted " + result.size() + " lobbies from list");
            return result;
        }

        System.out.println("DEBUG: Object is not a List, returning empty list");
        return new ArrayList<>();
    }


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
            System.out.println("Starting lobby game...");
            networkClient.startLobbyGame();

            if (view != null) {
                view.showStatus("Starting game...");
            }
        } catch (Exception e) {
            System.err.println("Exception in startGame: " + e.getMessage());
            e.printStackTrace();
            showError("Failed to start game: " + e.getMessage());
        }
    }



    public void handleLobbyMessage(Message message) {
        if (view == null) return;

        System.out.println("Received lobby message: " + message.getType());

        Gdx.app.postRunnable(() -> {
            try {
                System.out.println("DEBUG: Processing lobby message on main thread: " + message.getType());
                switch (message.getType()) {
                    case SUCCESS:
                        System.out.println("DEBUG: Handling SUCCESS message");
                        handleSuccessMessage(message);
                        break;
                    case ERROR:
                        System.out.println("DEBUG: Handling ERROR message");
                        handleErrorMessage(message);
                        break;

                    // Handle specific lobby message types
                    case LIST_LOBBIES:
                        System.out.println("DEBUG: Handling LIST_LOBBIES message");
                        handleListLobbiesResponse(message);
                        break;
                    case JOIN_LOBBY:
                        System.out.println("DEBUG: Handling JOIN_LOBBY message");
                        handleJoinLobbyResponse(message);
                        break;
                    case LEAVE_LOBBY:
                        System.out.println("DEBUG: Handling LEAVE_LOBBY message");
                        handleLeaveLobbyResponse(message);
                        break;
                    case SEARCH_LOBBY:
                        System.out.println("DEBUG: Handling SEARCH_LOBBY message");
                        handleSearchLobbyResponse(message);
                        break;
                    case PLAYER_READY:
                        System.out.println("DEBUG: Handling PLAYER_READY message");
                        handlePlayerReadyResponse(message);
                        break;
                    case START_LOBBY_GAME:
                        System.out.println("DEBUG: Handling START_LOBBY_GAME message");
                        handleStartGameResponse(message);
                        break;
                    case START_GAME:
                        System.out.println("DEBUG: Handling START_GAME message");
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



    private void handleListLobbiesResponse(Message message) {
        System.out.println("Handling LIST_LOBBIES response");
        // The server sends SUCCESS message with lobbies data directly
        Object lobbiesObj = message.getFromBody("lobbies");
        List<Lobby> lobbies = deserializeLobbyList(lobbiesObj);
        if (lobbies != null && !lobbies.isEmpty()) {
            System.out.println("Received " + lobbies.size() + " lobbies");
            view.onLobbiesReceived(lobbies);
        } else {
            System.out.println("No lobbies received, showing empty list");
            view.onLobbiesReceived(new ArrayList<>());
        }
    }

    private void handleJoinLobbyResponse(Message message) {
        System.out.println("Handling JOIN_LOBBY response");
        // The server sends SUCCESS message with lobby data directly
        Object lobbyObj = message.getFromBody("lobby");
        Lobby lobby = deserializeLobby(lobbyObj);
        if (lobby != null) {
            System.out.println("Joined lobby successfully: " + lobby.getName());
            view.onLobbyJoined(lobby);
        } else {
            System.err.println("Joined lobby but no lobby data received");
            showError("Joined lobby but no lobby data received");
        }
    }

    private void handleLeaveLobbyResponse(Message message) {
        System.out.println("Handling LEAVE_LOBBY response");
        // The server sends SUCCESS message for leave lobby
        System.out.println("Left lobby successfully");
        view.onLobbyLeft();
    }

    private void handleSearchLobbyResponse(Message message) {
        System.out.println("Handling SEARCH_LOBBY response");
        // The server sends SUCCESS message with lobbies data directly
        Object lobbiesObj = message.getFromBody("lobbies");
        List<Lobby> lobbies = deserializeLobbyList(lobbiesObj);
        if (lobbies != null && !lobbies.isEmpty()) {
            System.out.println("Search found " + lobbies.size() + " lobbies");
            view.onSearchResults(lobbies);
        } else {
            System.out.println("Search completed with no results");
            view.onSearchResults(new ArrayList<>());
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
        System.out.println("Handling START_GAME response");

        // Handle both START_LOBBY_GAME and START_GAME message formats
        String gameSessionId = message.getFromBody("gameSessionId");
        String messageText = message.getFromBody("message");

        // Check for both old and new field names for backward compatibility
        Boolean inFarmSelection = message.getFromBody("inFarmSelectionPhase");
        Boolean inMapSelection = message.getFromBody("inMapSelectionPhase");

        // Use either field name
        Boolean isInSelectionPhase = (inFarmSelection != null && inFarmSelection) ||
                                   (inMapSelection != null && inMapSelection);

        System.out.println("DEBUG: START_GAME response - gameSessionId: " + gameSessionId +
                          ", inFarmSelection: " + inFarmSelection +
                          ", inMapSelection: " + inMapSelection +
                          ", isInSelectionPhase: " + isInSelectionPhase);

        if (gameSessionId != null && messageText != null) {
            System.out.println("Game starting with session ID: " + gameSessionId);
            view.onGameStarting(gameSessionId);

            // Always show farm selection for multiplayer unless server says to skip
            if (isInSelectionPhase || (inFarmSelection == null && inMapSelection == null)) {
                navigateToFarmSelection();
            } else {
                // Navigate directly to multiplayer game (fallback)
                navigateToMultiplayerGame(gameSessionId);
            }
        } else {
            // Fallback for old START_LOBBY_GAME format
            String status = message.getFromBody("status");
            if ("success".equals(status)) {
                String oldGameSessionId = message.getFromBody("gameSessionId");
                System.out.println("Game starting with session ID: " + oldGameSessionId);
                view.onGameStarting(oldGameSessionId);
                // Always show farm selection for multiplayer in fallback
                navigateToFarmSelection();
            } else {
                String error = message.getFromBody("error");
                System.err.println("Failed to start game: " + error);
                showError("Failed to start game: " + (error != null ? error : "Unknown error"));
            }
        }
    }

    private void navigateToMultiplayerGame(String gameSessionId) {
        try {
            // Get current user
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                showError("No user logged in");
                return;
            }

            // Create player for current user
            Player player = new Player(currentUser);

            // Create a basic game structure for multiplayer
            List<Player> players = new ArrayList<>();
            players.add(player);

            Game game = new Game(players, player);
            game.setSaveName("Multiplayer_" + gameSessionId);

            // Set the game in App
            App.setGame(game);

            // Don't initialize farms here - they should be initialized based on server-side farm selections
            // The server will send the complete game state with proper farm assignments

            // Create and set the game view
            GameView gameView = new GameView(new GameMenuController(player), player, game,
                AssetManager.getAssetManager().getSkin(), currentUser);

            // Navigate to game
            Main.getGame().getScreen().dispose();
            Main.getGame().setScreen(gameView);

            System.out.println("DEBUG: Navigated to multiplayer game with session ID: " + gameSessionId);

        } catch (Exception e) {
            System.err.println("DEBUG: Failed to navigate to multiplayer game: " + e.getMessage());
            e.printStackTrace();
            showError("Failed to start multiplayer game: " + e.getMessage());
        }
    }

    private void navigateToFarmSelection() {
        try {
            System.out.println("DEBUG: Navigating to FarmSelectionScreen");

            // Navigate to farm selection screen
            Main.getGame().getScreen().dispose();
            org.example.client.views.FarmSelectionScreen farmSelectionScreen = new org.example.client.views.FarmSelectionScreen();
            Main.getGame().setScreen(farmSelectionScreen);

            System.out.println("DEBUG: Successfully navigated to FarmSelectionScreen");

        } catch (Exception e) {
            System.err.println("DEBUG: Failed to navigate to farm selection: " + e.getMessage());
            e.printStackTrace();
            showError("Failed to navigate to farm selection: " + e.getMessage());
        }
    }

    private void handleLobbyUpdatedResponse(Message message) {
        System.out.println("Handling LOBBY_UPDATED response");
        Object lobbyObj = message.getFromBody("lobby");
        Lobby lobby = deserializeLobby(lobbyObj);
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

        try {
            if (messageText.contains("Lobby created successfully")) {
                System.out.println("DEBUG: Lobby creation success received!");
                Object lobbyObj = message.getFromBody("lobby");
                Lobby lobby = deserializeLobby(lobbyObj);
                if (lobby != null) {
                    view.onLobbyCreated(lobby);
                } else {
                    System.err.println("DEBUG: Failed to deserialize lobby from creation response");
                }
            } else if (messageText.contains("Joined lobby successfully")) {
                Object lobbyObj = message.getFromBody("lobby");
                Lobby lobby = deserializeLobby(lobbyObj);
                if (lobby != null) {
                    view.onLobbyJoined(lobby);
                } else {
                    System.err.println("DEBUG: Failed to deserialize lobby from join response");
                }
            } else if (messageText.contains("Left lobby successfully")) {
                view.onLobbyLeft();
            } else if (messageText.contains("Lobby list retrieved")) {
                Object lobbiesObj = message.getFromBody("lobbies");
                List<Lobby> lobbies = deserializeLobbyList(lobbiesObj);
                view.onLobbiesReceived(lobbies);
            } else if (messageText.contains("Search completed")) {
                Object lobbiesObj = message.getFromBody("lobbies");
                List<Lobby> lobbies = deserializeLobbyList(lobbiesObj);
                view.onSearchResults(lobbies);
            } else if (messageText.contains("Lobby updated")) {
                System.out.println("DEBUG: Received lobby updated message");
                Object lobbyObj = message.getFromBody("lobby");
                Lobby lobby = deserializeLobby(lobbyObj);
                if (lobby != null) {
                    System.out.println("DEBUG: Successfully deserialized updated lobby");
                    System.out.println("DEBUG: Updated lobby - ID: " + lobby.getId() + ", Admin: " + lobby.getAdminId() + ", Players: " + lobby.getPlayers().size());
                    for (LobbyPlayer player : lobby.getPlayers()) {
                        System.out.println("DEBUG: Player: " + player.getUsername() + ", Admin: " + player.isAdmin());
                    }
                    view.onLobbyUpdated(lobby);
                } else {
                    System.err.println("DEBUG: Failed to deserialize updated lobby");
                }
            } else if (messageText.contains("Player ready") || messageText.contains("Player not ready")) {
                Boolean ready = message.getFromBody("ready");
                view.onPlayerReadyChanged(ready);
            } else if (messageText.contains("Game starting")) {
                String gameSessionId = message.getFromBody("gameSessionId");
                view.onGameStarting(gameSessionId);
                navigateToMultiplayerGame(gameSessionId);
            }

            view.showStatus(messageText);
        } catch (Exception e) {
            System.err.println("Error handling lobby message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleErrorMessage(Message message) {
        String errorMessage = message.getFromBody("message");
        System.err.println("Error message: " + errorMessage);
        showError(errorMessage != null ? errorMessage : "Unknown error");
    }

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
