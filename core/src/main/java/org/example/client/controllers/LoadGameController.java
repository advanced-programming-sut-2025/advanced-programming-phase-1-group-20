package org.example.client.controllers;

import com.badlogic.gdx.graphics.Color;
import org.example.client.Main;
import org.example.client.controllers.Controller;
import org.example.client.controllers.menu.MainMenuController;
import org.example.client.network.ClientMessageHandler;
import org.example.client.network.ConnectionManager;
import org.example.client.network.NetworkClient;
import org.example.client.views.menu.LoadGameScreen;
import org.example.client.views.menu.MainMenuScreen;
import org.example.common.models.App;
import org.example.common.models.Message;
import org.example.common.models.entities.Game;
import org.example.utils.AssetManager;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class LoadGameController implements Controller, ClientMessageHandler.OnlinePlayersListener, ClientMessageHandler.LobbyMessageListener {
    private LoadGameScreen view;
    private final ConnectionManager connectionManager;

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

    @Override
    public void setupListeners() {
        NetworkClient.getInstance().getMessageHandler().setOnlinePlayersListener(this);
        NetworkClient.getInstance().getMessageHandler().setLobbyListener(this);
    }

    private void connectToServer(String host, int port) {
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
        // 1. Load game locally to get player list
        Game game = App.loadGameByName(saveName);
        if (game == null) {
            view.setStatus("Failed to load game locally: " + saveName, Color.RED);
            return;
        }

        // 2. Send request to server to start this saved game
        NetworkClient.getInstance().loadGame(saveName, game.getPlayersUsernames());
    }

    public void goBackToMainMenu() {
        Main.getGame().getScreen().dispose();
        MainMenuController mainMenuController = new MainMenuController();
        MainMenuScreen mainMenuScreen = new MainMenuScreen(mainMenuController, AssetManager.getAssetManager().getSkin());
        Main.getGame().setScreen(mainMenuScreen);
    }

    @Override
    public void onOnlinePlayersUpdate(List<Object> players) {
        if (view != null) {
            view.updateOnlinePlayers(players);
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
            // Server has started the game, navigate to game screen
            // This part is complex and will need a proper transition to the GameView
            view.setStatus("Game is starting!", Color.GREEN);
            // TODO: Implement navigation to the GameView with the loaded game state.
            // For example: Main.getGame().setScreen(new GameView(loadedGame));
        }
    }
}
