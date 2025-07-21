package org.example.client.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.client.controllers.MultiplayerMenuController;
import org.example.client.network.ConnectionManager;
import org.example.common.models.App;

import java.util.List;
import java.util.Map;

public class MultiplayerMenuScreen implements Screen {
    private final MultiplayerMenuController controller;
    private Stage stage;
    private Skin skin;

    // Main UI components
    private Table mainTable;
    private Label titleLabel;
    private Label statusLabel;

    // Connection section
    private Table connectionTable;
    private TextField serverHostField;
    private TextField serverPortField;
    private TextButton connectButton;
    private TextButton disconnectButton;

    // Game section
    private Table gameTable;
    private TextButton createGameButton;
    private TextField gameIdField;
    private TextButton joinGameButton;
    private TextButton leaveGameButton;
    private TextButton lobbyMenuButton;

    // Online players section
    private Table onlinePlayersTable;
    private ScrollPane onlinePlayersScrollPane;
    private Table playersListTable;
    private Label onlinePlayersCountLabel;

    // Back button
    private TextButton backButton;

    // Connection status
    private ConnectionManager connectionManager;

    public MultiplayerMenuScreen(MultiplayerMenuController controller, Skin skin) {
        this.controller = controller;
        this.skin = skin;
        this.connectionManager = ConnectionManager.getInstance();
        controller.setView(this);
        initializeComponents();
    }

    private void initializeComponents() {
        // Main table
        mainTable = new Table();

        // Title
        titleLabel = new Label("MULTIPLAYER", skin);
        titleLabel.setColor(Color.WHITE);
        titleLabel.setFontScale(1.5f);

        // Status label
        statusLabel = new Label("Offline", skin);
        statusLabel.setColor(Color.YELLOW);

        // Connection section
        setupConnectionSection();

        // Game section
        setupGameSection();

        // Back button
        backButton = new TextButton("BACK TO MAIN MENU", skin);

        setupButtonListeners();
    }

    private void setupConnectionSection() {
        connectionTable = new Table();

        Label connectionTitle = new Label("SERVER CONNECTION", skin);
        connectionTitle.setColor(Color.CYAN);

        // Server host input
        Label hostLabel = new Label("Server Host:", skin);
        serverHostField = new TextField("localhost", skin);
        serverHostField.setWidth(200);

        // Server port input
        Label portLabel = new Label("Port:", skin);
        serverPortField = new TextField("8080", skin);
        serverPortField.setWidth(100);

        // Connection buttons
        connectButton = new TextButton("CONNECT", skin);
        disconnectButton = new TextButton("DISCONNECT", skin);
        disconnectButton.setDisabled(true);

        // Layout connection table
        connectionTable.add(connectionTitle).colspan(4).padBottom(10).row();
        connectionTable.add(hostLabel).padRight(10);
        connectionTable.add(serverHostField).width(150).padRight(20);
        connectionTable.add(portLabel).padRight(10);
        connectionTable.add(serverPortField).width(80).row();
        connectionTable.add(connectButton).width(100).padTop(10).padRight(10);
        connectionTable.add(disconnectButton).width(100).padTop(10).colspan(3);
    }

    private void setupGameSection() {
        gameTable = new Table();

        Label gameTitle = new Label("MULTIPLAYER GAMES", skin);
        gameTitle.setColor(Color.CYAN);

        // Create game button
        createGameButton = new TextButton("CREATE GAME", skin);
        createGameButton.setDisabled(true);

        // Join game section
        Label joinLabel = new Label("Game ID:", skin);
        gameIdField = new TextField("", skin);
        gameIdField.setMessageText("Enter game ID...");
        gameIdField.setWidth(200);

        joinGameButton = new TextButton("JOIN GAME", skin);
        joinGameButton.setDisabled(true);

        leaveGameButton = new TextButton("LEAVE GAME", skin);
        leaveGameButton.setDisabled(true);

        lobbyMenuButton = new TextButton("LOBBY MENU", skin);
        lobbyMenuButton.setDisabled(true);

        // Layout game table
        gameTable.add(gameTitle).colspan(3).padBottom(10).row();
        gameTable.add(createGameButton).width(150).padBottom(10).colspan(3).row();
        gameTable.add(lobbyMenuButton).width(150).padBottom(10).colspan(3).row();
        gameTable.add(joinLabel).padRight(10);
        gameTable.add(gameIdField).width(150).padRight(10);
        gameTable.add(joinGameButton).width(100).row();
        gameTable.add(leaveGameButton).width(150).padTop(10).colspan(3);
    }

    private void setupButtonListeners() {
        // Connect button
        connectButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String host = serverHostField.getText().trim();
                String portText = serverPortField.getText().trim();

                if (host.isEmpty()) {
                    updateStatus("Please enter server host", Color.RED);
                    return;
                }

                int port;
                try {
                    port = Integer.parseInt(portText);
                } catch (NumberFormatException e) {
                    updateStatus("Invalid port number", Color.RED);
                    return;
                }

                controller.connectToServer(host, port);
            }
        });

        // Disconnect button
        disconnectButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.disconnect();
            }
        });

        // Create game button
        createGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.createGame();
            }
        });

        // Join game button
        joinGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String gameId = gameIdField.getText().trim();
                if (gameId.isEmpty()) {
                    updateStatus("Please enter a game ID", Color.RED);
                    return;
                }
                controller.joinGame(gameId);
            }
        });

        // Leave game button
        leaveGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.leaveGame();
            }
        });

        // Lobby menu button
        lobbyMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.openLobbyMenu();
            }
        });

        // Back button
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.goBackToMainMenu();
            }
        });
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Setup online players section
        setupOnlinePlayersSection();

        // Setup main layout
        mainTable.setFillParent(true);
        mainTable.center();

        mainTable.add(titleLabel).padBottom(20).row();
        mainTable.add(statusLabel).padBottom(20).row();
        mainTable.add(connectionTable).padBottom(30).row();
        mainTable.add(gameTable).padBottom(30).row();
        mainTable.add(onlinePlayersTable).padBottom(30).row();
        mainTable.add(backButton).width(200).height(50);

        stage.addActor(mainTable);
    }

    @Override
    public void render(float delta) {
        // Update connection manager
        connectionManager.update();

        // Update UI based on connection state
        updateConnectionUI();

        // Clear screen
        ScreenUtils.clear(0.1f, 0.1f, 0.2f, 1);

        // Update and draw stage
        stage.act(Math.min(delta, 1 / 30f));
        stage.draw();
    }

    private void updateConnectionUI() {
        ConnectionManager.ConnectionState state = connectionManager.getCurrentState();
        String statusText = connectionManager.getConnectionStatusText();

        // Update status label
        statusLabel.setText(statusText);

        // Update button states based on connection state
        switch (state) {
            case OFFLINE:
            case ERROR:
                statusLabel.setColor(Color.RED);
                connectButton.setDisabled(false);
                disconnectButton.setDisabled(true);
                createGameButton.setDisabled(true);
                joinGameButton.setDisabled(true);
                leaveGameButton.setDisabled(true);
                lobbyMenuButton.setDisabled(true);
                break;

            case CONNECTING:
            case AUTHENTICATING:
                statusLabel.setColor(Color.YELLOW);
                connectButton.setDisabled(true);
                disconnectButton.setDisabled(false);
                createGameButton.setDisabled(true);
                joinGameButton.setDisabled(true);
                leaveGameButton.setDisabled(true);
                lobbyMenuButton.setDisabled(true);
                break;

            case CONNECTED:
                statusLabel.setColor(Color.ORANGE);
                connectButton.setDisabled(true);
                disconnectButton.setDisabled(false);
                createGameButton.setDisabled(true);
                joinGameButton.setDisabled(true);
                leaveGameButton.setDisabled(true);
                lobbyMenuButton.setDisabled(true);
                break;

            case AUTHENTICATED:
                statusLabel.setColor(Color.GREEN);
                connectButton.setDisabled(true);
                disconnectButton.setDisabled(false);
                createGameButton.setDisabled(false);
                joinGameButton.setDisabled(false);
                leaveGameButton.setDisabled(true);
                lobbyMenuButton.setDisabled(false);

                // Request online players list when authenticated
                controller.requestOnlinePlayersList();
                break;

            case IN_GAME:
                statusLabel.setColor(Color.CYAN);
                connectButton.setDisabled(true);
                disconnectButton.setDisabled(false);
                createGameButton.setDisabled(true);
                joinGameButton.setDisabled(true);
                leaveGameButton.setDisabled(false);
                lobbyMenuButton.setDisabled(true);
                break;
        }
    }

    public void updateStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setColor(color);
    }

    public void showConnectionSuccess() {
        updateStatus("Connected to server!", Color.GREEN);
    }

    public void showConnectionFailed(String reason) {
        updateStatus("Connection failed: " + reason, Color.RED);
    }

    public void showAuthenticationSuccess() {
        updateStatus("Authenticated successfully!", Color.GREEN);
    }

    public void showAuthenticationFailed(String reason) {
        updateStatus("Authentication failed: " + reason, Color.RED);
    }

    public void showGameCreated(String gameId) {
        updateStatus("Game created! ID: " + gameId, Color.CYAN);
    }

    public void showGameJoined(String gameId) {
        updateStatus("Joined game: " + gameId, Color.CYAN);
    }

    public void showError(String message) {
        updateStatus("Error: " + message, Color.RED);
    }

    private void setupOnlinePlayersSection() {
        onlinePlayersTable = new Table();

        Label onlinePlayersTitle = new Label("ONLINE PLAYERS", skin);
        onlinePlayersTitle.setColor(Color.CYAN);

        onlinePlayersCountLabel = new Label("0 players online", skin);
        onlinePlayersCountLabel.setColor(Color.LIGHT_GRAY);

        // Players list
        playersListTable = new Table();
        onlinePlayersScrollPane = new ScrollPane(playersListTable, skin);
        onlinePlayersScrollPane.setScrollingDisabled(true, false);
        onlinePlayersScrollPane.setVariableSizeKnobs(false);

        // Layout
        onlinePlayersTable.add(onlinePlayersTitle).padBottom(5).row();
        onlinePlayersTable.add(onlinePlayersCountLabel).padBottom(10).row();
        onlinePlayersTable.add(onlinePlayersScrollPane).width(400).height(150);
    }

    @SuppressWarnings("unchecked")
    public void updateOnlinePlayersList(List<Object> players) {
        playersListTable.clear();

        if (players == null || players.size() == 0) {
            onlinePlayersCountLabel.setText("0 players online");
            Label noPlayersLabel = new Label("No players online", skin);
            noPlayersLabel.setColor(Color.GRAY);
            playersListTable.add(noPlayersLabel).pad(10);
            return;
        }

        onlinePlayersCountLabel.setText(players.size() + " players online");

        for (Object playerObj : players) {
            if (playerObj instanceof Map) {
                Map<String, Object> playerData = (Map<String, Object>) playerObj;
                addPlayerToList(playerData);
            }
        }
    }

    private void addPlayerToList(Map<String, Object> playerData) {
        String username = (String) playerData.get("username");
        String status = (String) playerData.get("status");
        String lobbyName = (String) playerData.get("lobbyName");

        if (username == null) return;

        Table playerRow = new Table();
        playerRow.pad(5);

        // Player name
        Label nameLabel = new Label(username, skin);
        nameLabel.setColor(Color.WHITE);

        // Status
        String statusText = "";
        Color statusColor = Color.GREEN;

        if ("IN_LOBBY".equals(status) && lobbyName != null) {
            statusText = "In Lobby: " + lobbyName;
            statusColor = Color.YELLOW;
        } else if ("IN_GAME".equals(status)) {
            statusText = "In Game";
            statusColor = Color.RED;
        } else {
            statusText = "Online";
            statusColor = Color.GREEN;
        }

        Label statusLabel = new Label(statusText, skin);
        statusLabel.setColor(statusColor);

        // Layout row
        playerRow.add(nameLabel).width(120).left().padRight(10);
        playerRow.add(statusLabel).expandX().left();

        playersListTable.add(playerRow).fillX().padBottom(2).row();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
    }
}
