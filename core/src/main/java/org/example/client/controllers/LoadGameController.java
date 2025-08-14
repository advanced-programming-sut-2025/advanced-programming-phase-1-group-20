package org.example.client.controllers;

import com.badlogic.gdx.graphics.Color;
import org.example.client.Main;
import org.example.client.controllers.menu.MainMenuController;
import org.example.client.network.ClientMessageHandler;
import org.example.client.network.ConnectionManager;
import org.example.client.network.NetworkClient;
import org.example.client.views.menu.LoadGameScreen;
import org.example.client.views.GameView;
import org.example.client.views.menu.MainMenuScreen;
import org.example.common.models.App;
import org.example.common.models.Message;
import org.example.common.models.entities.Game;
import org.example.common.models.entities.User;
import org.example.common.models.Player.Player;
import org.example.utils.AssetManager;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class LoadGameController implements Controller,
    ClientMessageHandler.OnlinePlayersListener,
    ClientMessageHandler.LobbyMessageListener,
    ClientMessageHandler.ConnectionStatusListener {
    private LoadGameScreen view;
    private final ConnectionManager connectionManager;
    private Game loadedGame;
    private List<String> expectedPlayerUsernames;
    private List<Object> lastOnlinePlayers;

    public LoadGameController() {
        this.connectionManager = ConnectionManager.getInstance();
    }

    public void setView(LoadGameScreen view) {
        this.view = view;
    }

    public void initialize() {
        if (!connectionManager.isConnected()) {
            connectToServer("localhost", 8080); // Default connection
        } else {
            onConnected();
        }
        setupListeners();
    }

    // Expose connection status and update for the view
    public void updateNetwork() {
        connectionManager.update();
    }

    public String getConnectionStatusText() {
        return connectionManager.getConnectionStatusText();
    }

    public boolean isConnected() {
        return connectionManager.isConnected();
    }

    public boolean isAuthenticated() {
        return connectionManager.isAuthenticated();
    }

    // Expose connect for UI button
    public void connectToServerPublic(String host, int port) {
        connectToServerInternal(host, port);
    }

    @Override
    public void setupListeners() {
        NetworkClient.getInstance().getMessageHandler().setOnlinePlayersListener(this);
        NetworkClient.getInstance().getMessageHandler().setLobbyListener(this);
        NetworkClient.getInstance().getMessageHandler().setConnectionListener(this);
    }

    private void connectToServer(String host, int port) { // kept for backward calls
        connectToServerInternal(host, port);
    }

    private void connectToServerInternal(String host, int port) {
        view.setStatus("Connecting to server...", Color.YELLOW);
        CompletableFuture<Boolean> connectionFuture = connectionManager.connectToServer(host, port);
        connectionFuture.thenAccept(success -> {
            if (success) {
                authenticateUser();
            } else {
                view.setStatus("Could not connect to server.", Color.RED);
            }
        });
    }

    private void authenticateUser() {
        view.setStatus("Authenticating...", Color.YELLOW);
        CompletableFuture<Boolean> authFuture = connectionManager.authenticateUser(App.getLoggedInUser());
        authFuture.thenAccept(success -> {
            if (success) {
                onConnected();
            } else {
                view.setStatus("Authentication failed.", Color.RED);
            }
        });
    }

    private void onConnected() {
        view.setStatus("Connected. Loading data...", Color.GREEN);
        requestOnlinePlayers();
        loadSavedGamesForUser();
    }

    private void requestOnlinePlayers() {
        if (connectionManager.isAuthenticated()) {
            Message requestMessage = new Message();
            requestMessage.setType(Message.Type.REQUEST_PLAYERS_LIST);
            NetworkClient.getInstance().sendMessage(requestMessage);
        }
    }

    private void loadSavedGamesForUser() {
        if (App.getLoggedInUser() != null) {
            App.loadAllGames(); // Make sure games are loaded from storage
            List<Game> userGames = App.getAllGames().stream()
                .filter(g -> g.isPlayerInGame(App.getLoggedInUser()))
                .collect(Collectors.toList());
            view.updateSavedGames(userGames);
        }
    }

    public void loadGame(String saveName) {
        view.setStatus("Loading game: " + saveName, Color.YELLOW);
        // Load game locally to get player list and show lobby-like section
        Game game = App.loadGameByName(saveName);
        if (game == null) {
            view.setStatus("Failed to load game locally: " + saveName, Color.RED);
            return;
        }

        this.loadedGame = game;
        this.expectedPlayerUsernames = game.getPlayersUsernames();
        view.showLoadedGameLobby(game);

        // Optionally notify server (if implemented) about load intent
        try {
            NetworkClient.getInstance().loadGame(saveName, expectedPlayerUsernames);
        } catch (Exception ignored) {
        }

        // Update lobby readiness based on currently known online players
        if (lastOnlinePlayers != null) {
            view.updateLoadedGameLobbyStatuses(expectedPlayerUsernames, lastOnlinePlayers);
        }
    }

    public void startLoadedGame() {
        if (loadedGame == null) {
            view.setStatus("No loaded game selected.", Color.RED);
            return;
        }
        // Ask server to start (or re-attempt) this loaded game session with expected players
        try {
            NetworkClient.getInstance().loadGame(loadedGame.getSaveName(), expectedPlayerUsernames);
            view.setStatus("Requesting server to start the game...", Color.YELLOW);
        } catch (Exception e) {
            view.setStatus("Failed to request start: " + e.getMessage(), Color.RED);
        }
    }

    private void navigateToLoadedGame() {
        if (loadedGame == null) {
            view.setStatus("No loaded game to navigate.", Color.RED);
            return;
        }

        // Ensure current player is set from the logged-in user
        User currentUser = App.getLoggedInUser();
        Player currentPlayer = null;
        if (currentUser != null) {
            currentPlayer = loadedGame.getPlayer(currentUser);
        }
        if (currentPlayer == null && loadedGame.getPlayers() != null && !loadedGame.getPlayers().isEmpty()) {
            currentPlayer = loadedGame.getPlayers().get(0);
        }
        if (currentPlayer == null) {
            view.setStatus("Failed to identify current player for the loaded game.", Color.RED);
            return;
        }

        App.setGame(loadedGame);
        NetworkClient.getInstance().getMessageHandler().setCurrentGame(loadedGame);

        GameView gameView = new GameView(new GameMenuController(currentPlayer), currentPlayer, loadedGame,
            AssetManager.getAssetManager().getSkin(), currentPlayer.getUser());
        Main.getGame().getScreen().dispose();
        Main.getGame().setScreen(gameView);
    }

    public void goBackToMainMenu() {
        Main.getGame().getScreen().dispose();
        MainMenuController mainMenuController = new MainMenuController();
        MainMenuScreen mainMenuScreen = new MainMenuScreen(mainMenuController, AssetManager.getAssetManager().getSkin());
        Main.getGame().setScreen(mainMenuScreen);
    }

    @Override
    public void onOnlinePlayersUpdate(List<Object> players) {
        this.lastOnlinePlayers = players;
        if (view != null) {
            view.updateOnlinePlayers(players);
            if (expectedPlayerUsernames != null && !expectedPlayerUsernames.isEmpty()) {
                view.updateLoadedGameLobbyStatuses(expectedPlayerUsernames, players);
            }
        }
    }

    @Override
    public void onLobbyMessage(Message message) {
        // Handle messages from the server about the game loading process
        if (message.getType() == Message.Type.LOAD_GAME_STATUS) {
            boolean success = message.getFromBody("success");
            String statusMessage = message.getFromBody("message");
            if (success) {
                view.setStatus(statusMessage, Color.GREEN);
                // The server will send a START_GAME message next if successful
            } else {
                view.setStatus(statusMessage, Color.RED);
            }
        } else if (message.getType() == Message.Type.START_GAME) {
            // Server has started the session; navigate into game locally
            view.setStatus("Game is starting!", Color.GREEN);
            navigateToLoadedGame();
        }
    }

    // ===== ConnectionStatusListener =====
    @Override
    public void onConnectionEstablished(String sessionId) {
        view.setStatus("Connected (session " + sessionId + ")", Color.GREEN);
    }

    @Override
    public void onAuthenticationSuccess(String username) {
        view.setStatus("Authenticated as " + username, Color.GREEN);
    }

    @Override
    public void onAuthenticationFailed(String reason) {
        view.setStatus("Authentication failed: " + reason, Color.RED);
    }

    @Override
    public void onGameJoined(String gameId) {
        view.setStatus("In game (session " + gameId + ")", Color.GREEN);
    }

    @Override
    public void onGameLeft() {
        view.setStatus("Left game", Color.YELLOW);
    }

    @Override
    public void onError(String errorMessage) {
        view.setStatus("Error: " + errorMessage, Color.RED);
    }
}
