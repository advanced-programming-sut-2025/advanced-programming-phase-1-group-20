package org.example.client.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.client.Main;
import org.example.client.controllers.LobbyMenuController;
import org.example.client.controllers.MultiplayerMenuController;
import org.example.common.Lobby.Lobby;
import org.example.common.Lobby.LobbyPlayer;

import java.util.List;

public class LobbyMenuScreen implements Screen {
    private final LobbyMenuController controller;
    private Stage stage;
    private Skin skin;

    // Main UI components
    private Table mainTable;
    private ScrollPane lobbyListScrollPane;
    private Table lobbyListTable;

    // Header
    private Label titleLabel;
    private Label statusLabel;
    private TextButton backButton;
    private TextButton refreshButton;

    // Search section
    private Table searchTable;
    private TextField searchField;
    private TextField lobbyIdField;
    private TextButton searchButton;

    // Create lobby section
    private Table createTable;
    private TextField lobbyNameField;
    private CheckBox privateCheckBox;
    private CheckBox visibleCheckBox;
    private TextField passwordField;
    private TextButton createLobbyButton;

    // Currently in lobby section
    private Table currentLobbyTable;
    private Label currentLobbyLabel;
    private Label currentLobbyPlayersLabel;
    private CheckBox readyCheckBox;
    private TextButton leaveLobbyButton;
    private TextButton startGameButton;

    // Current state
    private Lobby currentLobby;
    private boolean isInLobby = false;

    public LobbyMenuScreen(LobbyMenuController controller, Skin skin) {
        this.controller = controller;
        this.skin = skin;
        controller.setView(this);
        initializeComponents();
    }

    private void initializeComponents() {
        stage = new Stage(new ScreenViewport());

        // Main table
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.pad(20);

        // Initialize components
        setupHeader();
        setupSearchSection();
        setupCreateSection();
        setupCurrentLobbySection();
        setupLobbyList();

        // Layout main table
        layoutMainTable();

        stage.addActor(mainTable);
        setupEventListeners();
    }

    private void setupHeader() {
        titleLabel = new Label("LOBBY MENU", skin);
        titleLabel.setColor(Color.CYAN);
        titleLabel.setFontScale(1.5f);

        statusLabel = new Label("Select or create a lobby", skin);
        statusLabel.setColor(Color.WHITE);

        backButton = new TextButton("BACK", skin);
        refreshButton = new TextButton("REFRESH", skin);
    }

    private void setupSearchSection() {
        searchTable = new Table();

        Label searchTitle = new Label("SEARCH LOBBIES", skin);
        searchTitle.setColor(Color.YELLOW);

        searchField = new TextField("", skin);
        searchField.setMessageText("Search by name...");

        lobbyIdField = new TextField("", skin);
        lobbyIdField.setMessageText("Or enter lobby ID...");

        searchButton = new TextButton("SEARCH", skin);

        searchTable.add(searchTitle).colspan(3).padBottom(10).row();
        searchTable.add(new Label("Name:", skin)).padRight(5);
        searchTable.add(searchField).width(200).padRight(10);
        searchTable.add(searchButton).width(80).row();
        searchTable.add(new Label("ID:", skin)).padRight(5);
        searchTable.add(lobbyIdField).width(200).padRight(10);
        searchTable.add().width(80);
    }

    private void setupCreateSection() {
        createTable = new Table();

        Label createTitle = new Label("CREATE LOBBY", skin);
        createTitle.setColor(Color.GREEN);

        lobbyNameField = new TextField("", skin);
        lobbyNameField.setMessageText("Enter lobby name...");

        privateCheckBox = new CheckBox(" Private (requires password)", skin);
        visibleCheckBox = new CheckBox(" Visible in lobby list", skin);
        visibleCheckBox.setChecked(true);

        passwordField = new TextField("", skin);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        passwordField.setMessageText("Password (if private)...");
        passwordField.setDisabled(true);

        createLobbyButton = new TextButton("CREATE LOBBY", skin);

        createTable.add(createTitle).colspan(2).padBottom(10).row();
        createTable.add(new Label("Name:", skin)).padRight(10);
        createTable.add(lobbyNameField).width(200).row();
        createTable.add(privateCheckBox).colspan(2).padTop(5).row();
        createTable.add(visibleCheckBox).colspan(2).padTop(5).row();
        createTable.add(new Label("Password:", skin)).padRight(10);
        createTable.add(passwordField).width(200).row();
        createTable.add(createLobbyButton).colspan(2).width(150).padTop(10);
    }

    private void setupCurrentLobbySection() {
        currentLobbyTable = new Table();

        Label currentTitle = new Label("CURRENT LOBBY", skin);
        currentTitle.setColor(Color.ORANGE);

        currentLobbyLabel = new Label("Not in any lobby", skin);
        currentLobbyLabel.setColor(Color.GRAY);

        currentLobbyPlayersLabel = new Label("", skin);
        currentLobbyPlayersLabel.setColor(Color.LIGHT_GRAY);

        readyCheckBox = new CheckBox(" Ready to start", skin);
        readyCheckBox.setDisabled(true);

        leaveLobbyButton = new TextButton("LEAVE LOBBY", skin);
        leaveLobbyButton.setDisabled(true);

        startGameButton = new TextButton("START GAME", skin);
        startGameButton.setDisabled(true);
        startGameButton.setColor(Color.GREEN);

        currentLobbyTable.add(currentTitle).colspan(3).padBottom(10).row();
        currentLobbyTable.add(currentLobbyLabel).colspan(3).padBottom(5).row();
        currentLobbyTable.add(currentLobbyPlayersLabel).colspan(3).padBottom(10).row();
        currentLobbyTable.add(readyCheckBox).padRight(10);
        currentLobbyTable.add(leaveLobbyButton).width(100).padRight(10);
        currentLobbyTable.add(startGameButton).width(100);

        currentLobbyTable.setVisible(false);
    }

    private void setupLobbyList() {
        lobbyListTable = new Table();
        lobbyListScrollPane = new ScrollPane(lobbyListTable, skin);
        lobbyListScrollPane.setScrollingDisabled(true, false);
        lobbyListScrollPane.setVariableSizeKnobs(false);
    }

    private void layoutMainTable() {
        // Header
        Table headerTable = new Table();
        headerTable.add(titleLabel).expandX().left();
        headerTable.add(refreshButton).width(100).padRight(10);
        headerTable.add(backButton).width(80);

        mainTable.add(headerTable).fillX().padBottom(10).row();
        mainTable.add(statusLabel).fillX().padBottom(20).row();

        // Main content
        Table contentTable = new Table();

        // Left side - controls
        Table leftTable = new Table();
        leftTable.add(searchTable).fillX().padBottom(20).row();
        leftTable.add(new Separator()).fillX().padBottom(20).row();
        leftTable.add(createTable).fillX().padBottom(20).row();
        leftTable.add(new Separator()).fillX().padBottom(20).row();
        leftTable.add(currentLobbyTable).fillX();

        // Right side - lobby list
        Table rightTable = new Table();
        Label lobbyListTitle = new Label("AVAILABLE LOBBIES", skin);
        lobbyListTitle.setColor(Color.WHITE);
        rightTable.add(lobbyListTitle).padBottom(10).row();
        rightTable.add(lobbyListScrollPane).expand().fill();

        contentTable.add(leftTable).width(400).top().padRight(20);
        contentTable.add(new Separator(true)).fillY().padRight(20);
        contentTable.add(rightTable).expand().fill();

        mainTable.add(contentTable).expand().fill();
    }

    private void setupEventListeners() {
        // Back button
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.goBack();
            }
        });

        // Refresh button
        refreshButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.refreshLobbies();
            }
        });

        // Search button
        searchButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String searchTerm = searchField.getText();
                String lobbyId = lobbyIdField.getText();
                controller.searchLobbies(searchTerm, lobbyId);
            }
        });

        // Create lobby button
        createLobbyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                createLobby();
            }
        });

        // Private checkbox
        privateCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                passwordField.setDisabled(!privateCheckBox.isChecked());
                if (!privateCheckBox.isChecked()) {
                    passwordField.setText("");
                }
            }
        });

        // Ready checkbox
        readyCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.setPlayerReady(readyCheckBox.isChecked());
            }
        });

        // Leave lobby button
        leaveLobbyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.leaveLobby();
            }
        });

        // Start game button
        startGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.startGame();
            }
        });
    }

    private void createLobby() {
        String lobbyName = lobbyNameField.getText();
        boolean isPrivate = privateCheckBox.isChecked();
        boolean isVisible = visibleCheckBox.isChecked();
        String password = passwordField.getText();

        controller.createLobby(lobbyName, isPrivate, isVisible, password);
    }

    private void addLobbyToList(Lobby lobby) {
        if (lobby == null) return;

        Table lobbyRow = new Table();
        lobbyRow.pad(10);

        // Lobby info
        String lobbyInfo = lobby.getName() + " (" + lobby.getId() + ")";
        Label lobbyNameLabel = new Label(lobbyInfo, skin);

        String playerInfo = lobby.getPlayers().size() + "/" + lobby.getSettings().getMaxPlayers() + " players";
        Label playersLabel = new Label(playerInfo, skin);
        playersLabel.setColor(Color.LIGHT_GRAY);

        // Status indicators
        String statusText = "";
        Color statusColor = Color.WHITE;

        if (!lobby.getSettings().isVisible()) {
            statusText += "[HIDDEN] ";
            statusColor = Color.ORANGE;
        }
        if (lobby.getSettings().isPrivate()) {
            statusText += "[PRIVATE] ";
            statusColor = Color.YELLOW;
        }
        if (lobby.getStatus() == Lobby.LobbyStatus.IN_GAME) {
            statusText += "[IN GAME] ";
            statusColor = Color.RED;
        }

        Label statusLabel = new Label(statusText, skin);
        statusLabel.setColor(statusColor);

        // Join button
        TextButton joinButton = new TextButton("JOIN", skin);
        joinButton.setDisabled(isInLobby || !lobby.canJoin());

        joinButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                joinLobby(lobby);
            }
        });

        // Layout row
        lobbyRow.add(lobbyNameLabel).expandX().left().padRight(10);
        lobbyRow.add(playersLabel).padRight(10);
        lobbyRow.add(statusLabel).padRight(10);
        lobbyRow.add(joinButton).width(80);

        lobbyListTable.add(lobbyRow).fillX().padBottom(5).row();
        lobbyListTable.add(new Separator()).fillX().row();
    }

    private void joinLobby(Lobby lobby) {
        if (lobby.getSettings().requiresPassword()) {
            // Show password dialog
            showPasswordDialog(lobby);
        } else {
            controller.joinLobby(lobby.getId(), null);
        }
    }

    private void showPasswordDialog(Lobby lobby) {
        TextField passwordDialogField = new TextField("", skin);
        passwordDialogField.setPasswordMode(true);
        passwordDialogField.setPasswordCharacter('*');

        Dialog passwordDialog = new Dialog("Enter Password", skin) {
            @Override
            protected void result(Object object) {
                if ((Boolean) object) {
                    String password = passwordDialogField.getText();
                    controller.joinLobby(lobby.getId(), password);
                }
            }
        };

        passwordDialog.text("Enter password for lobby: " + lobby.getName());
        passwordDialog.getContentTable().row();
        passwordDialog.getContentTable().add(passwordDialogField).width(200).padTop(10);

        passwordDialog.button("Join", true);
        passwordDialog.button("Cancel", false);

        passwordDialog.show(stage);
    }

    // =====================
    // CONTROLLER CALLBACKS
    // =====================

    public void onLobbyCreated(Lobby lobby) {
        setCurrentLobby(lobby);
        clearCreateForm();
        showStatus("Lobby created successfully!");
    }

    public void onLobbyJoined(Lobby lobby) {
        setCurrentLobby(lobby);
        showStatus("Joined lobby successfully!");
    }

    public void onLobbyLeft() {
        setCurrentLobby(null);
        showStatus("Left lobby");
    }

    public void onLobbiesReceived(List<Lobby> lobbies) {
        updateLobbyList(lobbies);
        showStatus("Lobby list updated (" + lobbies.size() + " lobbies)");
    }

    public void onSearchResults(List<Lobby> lobbies) {
        updateLobbyList(lobbies);
        showStatus("Search results (" + lobbies.size() + " lobbies found)");
    }

    public void onLobbyUpdated(Lobby lobby) {
        if (isInLobby && currentLobby != null && currentLobby.getId().equals(lobby.getId())) {
            setCurrentLobby(lobby);
        }
        // Also refresh the lobby list
        controller.refreshLobbies();
    }

    public void onPlayerReadyChanged(Boolean ready) {
        readyCheckBox.setChecked(ready);
        showStatus(ready ? "You are ready!" : "You are not ready");
    }

    public void onGameStarting(String gameSessionId) {
        showStatus("Game starting! Session ID: " + gameSessionId);
        // TODO: Navigate to game screen
    }

    // =====================
    // UTILITY METHODS
    // =====================

    private void setCurrentLobby(Lobby lobby) {
        this.currentLobby = lobby;
        this.isInLobby = (lobby != null);

        if (isInLobby) {
            currentLobbyLabel.setText(lobby.getName() + " (ID: " + lobby.getId() + ")");
            currentLobbyLabel.setColor(Color.GREEN);

            StringBuilder playersText = new StringBuilder("Players: ");
            for (LobbyPlayer player : lobby.getPlayers()) {
                playersText.append(player.getUsername());
                if (player.isAdmin()) {
                    playersText.append(" (Admin)");
                }
                if (player.isReady()) {
                    playersText.append(" [Ready]");
                }
                playersText.append(", ");
            }
            if (playersText.length() > 10) {
                playersText.setLength(playersText.length() - 2); // Remove last ", "
            }

            currentLobbyPlayersLabel.setText(playersText.toString());

            readyCheckBox.setDisabled(false);
            leaveLobbyButton.setDisabled(false);

            // Only admin can start game
            boolean canStart = false;
            String currentUserName = controller.getCurrentUser().getUsername();
            for (LobbyPlayer player : lobby.getPlayers()) {
                if (player.getUsername().equals(currentUserName) && player.isAdmin()) {
                    canStart = lobby.getPlayers().size() >= 2;
                    break;
                }
            }
            startGameButton.setDisabled(!canStart);

            currentLobbyTable.setVisible(true);
        } else {
            currentLobbyLabel.setText("Not in any lobby");
            currentLobbyLabel.setColor(Color.GRAY);
            currentLobbyPlayersLabel.setText("");

            readyCheckBox.setChecked(false);
            readyCheckBox.setDisabled(true);
            leaveLobbyButton.setDisabled(true);
            startGameButton.setDisabled(true);

            currentLobbyTable.setVisible(false);
        }

        // Update join buttons
        updateLobbyListButtons();
    }

    private void updateLobbyList(List<Lobby> lobbies) {
        lobbyListTable.clear();

        if (lobbies == null || lobbies.isEmpty()) {
            Label noLobbiesLabel = new Label("No lobbies available", skin);
            noLobbiesLabel.setColor(Color.GRAY);
            lobbyListTable.add(noLobbiesLabel).pad(20);
            return;
        }

        for (Lobby lobby : lobbies) {
            addLobbyToList(lobby);
        }
    }

    private void updateLobbyListButtons() {
        // This would require keeping references to join buttons
        // For simplicity, we'll refresh the list when needed
    }

    private void clearCreateForm() {
        lobbyNameField.setText("");
        privateCheckBox.setChecked(false);
        visibleCheckBox.setChecked(true);
        passwordField.setText("");
        passwordField.setDisabled(true);
    }

    public void showStatus(String message) {
        statusLabel.setText(message);
        statusLabel.setColor(Color.WHITE);
    }

    public void showError(String error) {
        statusLabel.setText("Error: " + error);
        statusLabel.setColor(Color.RED);
    }

    public void goBackToMultiplayerMenu() {
        Main.getGame().getScreen().dispose();
        MultiplayerMenuController multiplayerController = new MultiplayerMenuController();
        MultiplayerMenuScreen multiplayerScreen = new MultiplayerMenuScreen(multiplayerController, skin);
        Main.getGame().setScreen(multiplayerScreen);
    }

    // =====================
    // SCREEN INTERFACE
    // =====================

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        controller.refreshLobbies(); // Load initial lobby list
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.1f, 0.2f, 1);

        controller.update(delta);

        stage.act(delta);
        stage.draw();
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
        stage.dispose();
    }

    private static class Separator extends Actor {
        private final boolean vertical;

        public Separator() {
            this(false);
        }

        public Separator(boolean vertical) {
            this.vertical = vertical;
        }

        @Override
        public void draw(com.badlogic.gdx.graphics.g2d.Batch batch, float parentAlpha) {
            // Simple separator using ShapeRenderer would be better, but for simplicity
            // we'll just draw a colored rectangle using batch
            Color oldColor = batch.getColor();
            batch.setColor(0.5f, 0.5f, 0.5f, parentAlpha);

            // We can't easily draw without a texture, so we'll skip the visual separator
            // In a real implementation, you'd use ShapeRenderer or create a 1x1 white texture

            batch.setColor(oldColor);
        }
    }
}
