package org.example.client.views.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.client.controllers.LoadGameController;
import org.example.common.models.entities.Game;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;

public class LoadGameScreen implements Screen {
    private final LoadGameController controller;
    private final Stage stage;
    private final Skin skin;

    private Table mainTable;
    private Label statusLabel;
    private Table savedGamesTable;
    private ScrollPane savedGamesScrollPane;
    private ScrollPane onlinePlayersScrollPane;
    private Table playersListTable;

    private Table loadedLobbyContainer;
    private Label loadedLobbyTitle;
    private Table expectedPlayersTable;
    private TextButton startGameButton;

    public LoadGameScreen(LoadGameController controller, Skin skin) {
        this.controller = controller;
        this.skin = skin;
        this.stage = new Stage(new ScreenViewport());
        this.controller.setView(this);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        Label title = new Label("Load Game", skin);
        statusLabel = new Label("Connect to the server to see online players and load games.", skin);
        statusLabel.setWrap(true);

        // Connect to Server Section
        Table connectContainer = new Table();
        Label connectTitle = new Label("Server", skin);
        TextField hostField = new TextField("localhost", skin);
        TextField portField = new TextField("8080", skin);
        TextButton connectButton = new TextButton("Connect", skin);
        connectButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    int port = Integer.parseInt(portField.getText().trim());
                    controller.initialize(); // ensure listeners
                    controller.connectToServerPublic(hostField.getText().trim(), port);
                } catch (NumberFormatException e) {
                    setStatus("Invalid port.", Color.RED);
                }
            }
        });
        connectContainer.add(connectTitle).left().padRight(10);
        connectContainer.add(new Label("Host:", skin)).padRight(5);
        connectContainer.add(hostField).width(180).padRight(10);
        connectContainer.add(new Label("Port:", skin)).padRight(5);
        connectContainer.add(portField).width(90).padRight(10);
        connectContainer.add(connectButton).width(120);

        // Saved Games Section
        Table savedGamesContainer = new Table();
        Label savedGamesTitle = new Label("Saved Games", skin);
        savedGamesTable = new Table();
        savedGamesScrollPane = new ScrollPane(savedGamesTable, skin);
        savedGamesScrollPane.setFadeScrollBars(false);
        savedGamesContainer.add(savedGamesTitle).row();
        savedGamesContainer.add(savedGamesScrollPane).grow().pad(10);

        // Online Players Section
        Table onlinePlayersContainer = new Table();
        Label onlinePlayersTitle = new Label("Online Players", skin);
        playersListTable = new Table();
        onlinePlayersScrollPane = new ScrollPane(playersListTable, skin);
        onlinePlayersScrollPane.setFadeScrollBars(false);
        onlinePlayersContainer.add(onlinePlayersTitle).row();
        onlinePlayersContainer.add(onlinePlayersScrollPane).grow().pad(10);

        // Loaded Game Lobby Section (hidden until a game is chosen)
        loadedLobbyContainer = new Table();
        loadedLobbyContainer.setVisible(false);
        loadedLobbyTitle = new Label("Loaded Game Lobby", skin);
        expectedPlayersTable = new Table();
        startGameButton = new TextButton("Start Game", skin);
        startGameButton.setDisabled(true);
        startGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!startGameButton.isDisabled()) {
                    controller.startLoadedGame();
                }
            }
        });
        loadedLobbyContainer.add(loadedLobbyTitle).padBottom(10).row();
        loadedLobbyContainer.add(expectedPlayersTable).growX().pad(10).row();
        loadedLobbyContainer.add(startGameButton).pad(10);


        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.goBackToMainMenu();
            }
        });

        mainTable.add(title).colspan(2).pad(20).row();
        mainTable.add(statusLabel).colspan(2).width(Gdx.graphics.getWidth() * 0.8f).pad(10).row();
        mainTable.add(connectContainer).colspan(2).growX().pad(10).row();
        mainTable.add(savedGamesContainer).width(Gdx.graphics.getWidth() * 0.4f).growY();
        mainTable.add(onlinePlayersContainer).width(Gdx.graphics.getWidth() * 0.4f).growY().row();
        mainTable.add(loadedLobbyContainer).colspan(2).growX().padTop(10).row();
        mainTable.add(backButton).colspan(2).pad(20);

        controller.initialize();
    }

    public void updateSavedGames(List<Game> savedGames) {
        savedGamesTable.clear();
        if (savedGames == null || savedGames.isEmpty()) {
            savedGamesTable.add(new Label("No saved games found.", skin));
            return;
        }

        for (Game game : savedGames) {
            Table gameRow = new Table();
            gameRow.add(new Label(game.getSaveName(), skin)).expandX().left();
            TextButton loadButton = new TextButton("Load", skin);
            loadButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    controller.loadGame(game.getSaveName());
                }
            });
            gameRow.add(loadButton).right();
            savedGamesTable.add(gameRow).growX().pad(5).row();
        }
    }

    public void updateOnlinePlayers(List<Object> players) {
        playersListTable.clear();
        if (players == null || players.isEmpty()) {
            playersListTable.add(new Label("No players online.", skin));
            return;
        }

        for (Object playerObj : players) {
            if (playerObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> playerData = (Map<String, Object>) playerObj;
                String username = (String) playerData.get("username");
                playersListTable.add(new Label(username, skin)).row();
            }
        }
    }

    public void showLoadedGameLobby(Game game) {
        loadedLobbyContainer.setVisible(true);
        String name = game.getSaveName() != null ? game.getSaveName() : "Loaded Game";
        loadedLobbyTitle.setText("Loaded Game Lobby - " + name);
        expectedPlayersTable.clear();
        startGameButton.setDisabled(true);
    }

    public void updateLoadedGameLobbyStatuses(List<String> expectedPlayerUsernames, List<Object> onlinePlayers) {
        if (expectedPlayerUsernames == null || expectedPlayerUsernames.isEmpty()) {
            loadedLobbyContainer.setVisible(false);
            return;
        }

        loadedLobbyContainer.setVisible(true);
        expectedPlayersTable.clear();

        Set<String> inLobbySet = new HashSet<>();
        if (onlinePlayers != null) {
            for (Object obj : onlinePlayers) {
                if (obj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> data = (Map<String, Object>) obj;
                    Object usernameObj = data.get("username");
                    Object statusObj = data.get("status");
                    String statusStr = statusObj instanceof String ? (String) statusObj : "";
                    if (usernameObj instanceof String && "IN_LOBBY".equals(statusStr)) {
                        inLobbySet.add((String) usernameObj);
                    }
                }
            }
        }

        boolean allPresent = true;
        for (String username : expectedPlayerUsernames) {
            boolean isOnline = inLobbySet.contains(username);
            Label nameLabel = new Label(username, skin);
            Label status = new Label(isOnline ? "in lobby" : "waiting", skin);
            status.setColor(isOnline ? Color.GREEN : Color.RED);
            expectedPlayersTable.add(nameLabel).left().pad(3);
            expectedPlayersTable.add(status).right().pad(3).row();
            if (!isOnline) allPresent = false;
        }

        startGameButton.setDisabled(!allPresent);
        if (allPresent) {
            setStatus("All players are online. You can start the game.", Color.GREEN);
        } else {
            setStatus("Waiting for all players to come online...", Color.YELLOW);
        }
    }

    public void setStatus(String text, Color color) {
        statusLabel.setText(text);
        statusLabel.setColor(color);
    }

    @Override
    public void render(float delta) {
        // Drive network update and reflect status
        controller.updateNetwork();
        String status = controller.getConnectionStatusText();
        if (status != null) {
            statusLabel.setText(status);
            if (controller.isAuthenticated()) {
                statusLabel.setColor(Color.GREEN);
            }
        }

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
