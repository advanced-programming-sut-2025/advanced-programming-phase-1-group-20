package org.example.client.views.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.client.Main;
import org.example.client.controllers.menu.LobbyMenuController;
import org.example.client.controllers.MultiplayerMenuController;
import org.example.client.views.MultiplayerMenuScreen;
import org.example.common.Lobby.Lobby;
import org.example.common.Lobby.LobbyPlayer;
import org.example.common.models.Message;

import java.util.List;

public class LobbyMenuScreen implements Screen {
    private final LobbyMenuController controller;
    private Stage stage;
    private Skin skin;
    private SpriteBatch batch;
    private BitmapFont titleFont;

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
    private float refreshTimer = 0;
    private static final float REFRESH_INTERVAL = 3.0f; // Refresh every 3 seconds

    public LobbyMenuScreen(LobbyMenuController controller, Skin skin) {
        this.controller = controller;
        this.skin = skin;
        this.batch = new SpriteBatch();
        this.titleFont = new BitmapFont();
        this.titleFont.getData().setScale(2.0f);

        controller.setView(this);
        initializeComponents();
    }

    private void initializeComponents() {
        stage = new Stage(new ScreenViewport());

        // Main table with gradient background
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
        // Breadcrumb navigation
        Label breadcrumbLabel = new Label("Main Menu > Multiplayer > Lobby Menu", skin);
        breadcrumbLabel.setColor(Color.LIGHT_GRAY);
        breadcrumbLabel.setFontScale(0.8f);
        breadcrumbLabel.setAlignment(Align.left);

        titleLabel = new Label("LOBBY MENU", skin);
        titleLabel.setColor(new Color(0.2f, 0.8f, 1.0f, 1.0f)); // Cyan blue
        titleLabel.setFontScale(1.8f);
        titleLabel.setAlignment(Align.center);

        statusLabel = new Label("Select or create a lobby", skin);
        statusLabel.setColor(Color.WHITE);
        statusLabel.setAlignment(Align.center);

        backButton = new TextButton("BACK", skin);
        backButton.setColor(new Color(0.8f, 0.2f, 0.2f, 1.0f)); // Red

        refreshButton = new TextButton("REFRESH", skin);
        refreshButton.setColor(new Color(0.2f, 0.8f, 0.2f, 1.0f)); // Green
    }

    private void setupSearchSection() {
        searchTable = new Table();
        searchTable.pad(10);
        searchTable.setBackground(skin.newDrawable("white", new Color(0.1f, 0.1f, 0.15f, 0.8f)));

        Label searchTitle = new Label("SEARCH LOBBIES", skin);
        searchTitle.setColor(new Color(1.0f, 1.0f, 0.0f, 1.0f)); // Yellow
        searchTitle.setFontScale(1.2f);

        searchField = new TextField("", skin);
        searchField.setMessageText("Search by name...");
        searchField.setColor(Color.WHITE);

        lobbyIdField = new TextField("", skin);
        lobbyIdField.setMessageText("Or enter lobby ID...");
        lobbyIdField.setColor(Color.WHITE);

        searchButton = new TextButton("SEARCH", skin);
        searchButton.setColor(new Color(0.2f, 0.6f, 1.0f, 1.0f)); // Blue

        searchTable.add(searchTitle).colspan(3).padBottom(15).row();
        searchTable.add(new Label("Name:", skin)).padRight(10);
        searchTable.add(searchField).width(250).padRight(15);
        searchTable.add(searchButton).width(100).row();
        searchTable.add(new Label("ID:", skin)).padRight(10);
        searchTable.add(lobbyIdField).width(250).padRight(15);
        searchTable.add().width(100);
    }

    private void setupCreateSection() {
        createTable = new Table();
        createTable.pad(10);
        createTable.setBackground(skin.newDrawable("white", new Color(0.1f, 0.15f, 0.1f, 0.8f)));

        Label createTitle = new Label("CREATE LOBBY", skin);
        createTitle.setColor(new Color(0.2f, 1.0f, 0.2f, 1.0f)); // Green
        createTitle.setFontScale(1.2f);

        lobbyNameField = new TextField("", skin);
        lobbyNameField.setMessageText("Enter lobby name...");
        lobbyNameField.setColor(Color.WHITE);

        privateCheckBox = new CheckBox(" Private (requires password)", skin);
        privateCheckBox.setColor(Color.WHITE);

        visibleCheckBox = new CheckBox(" Visible in lobby list", skin);
        visibleCheckBox.setColor(Color.WHITE);
        visibleCheckBox.setChecked(true);

        passwordField = new TextField("", skin);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        passwordField.setMessageText("Password (if private)...");
        passwordField.setColor(Color.WHITE);
        passwordField.setDisabled(true);

        createLobbyButton = new TextButton("CREATE LOBBY", skin);
        createLobbyButton.setColor(new Color(0.2f, 1.0f, 0.2f, 1.0f)); // Green

        createTable.add(createTitle).colspan(2).padBottom(15).row();
        createTable.add(new Label("Name:", skin)).padRight(15);
        createTable.add(lobbyNameField).width(250).row();
        createTable.add(privateCheckBox).colspan(2).padTop(10).row();
        createTable.add(visibleCheckBox).colspan(2).padTop(5).row();
        createTable.add(new Label("Password:", skin)).padRight(15);
        createTable.add(passwordField).width(250).row();
        createTable.add(createLobbyButton).colspan(2).width(200).padTop(15);
    }

    private void setupCurrentLobbySection() {
        currentLobbyTable = new Table();
        currentLobbyTable.pad(10);
        currentLobbyTable.setBackground(skin.newDrawable("white", new Color(0.15f, 0.1f, 0.1f, 0.8f)));

        Label currentTitle = new Label("CURRENT LOBBY", skin);
        currentTitle.setColor(new Color(1.0f, 0.6f, 0.0f, 1.0f)); // Orange
        currentTitle.setFontScale(1.2f);

        currentLobbyLabel = new Label("Not in any lobby", skin);
        currentLobbyLabel.setColor(Color.GRAY);

        currentLobbyPlayersLabel = new Label("", skin);
        currentLobbyPlayersLabel.setColor(Color.LIGHT_GRAY);

        readyCheckBox = new CheckBox(" Ready to start", skin);
        readyCheckBox.setColor(Color.WHITE);
        readyCheckBox.setDisabled(true);

        leaveLobbyButton = new TextButton("LEAVE LOBBY", skin);
        leaveLobbyButton.setColor(new Color(0.8f, 0.2f, 0.2f, 1.0f)); // Red
        leaveLobbyButton.setDisabled(true);

        startGameButton = new TextButton("START GAME", skin);
        startGameButton.setColor(new Color(0.2f, 1.0f, 0.2f, 1.0f)); // Green
        startGameButton.setDisabled(true);

        currentLobbyTable.add(currentTitle).colspan(3).padBottom(15).row();
        currentLobbyTable.add(currentLobbyLabel).colspan(3).padBottom(10).row();
        currentLobbyTable.add(currentLobbyPlayersLabel).colspan(3).padBottom(15).row();
        currentLobbyTable.add(readyCheckBox).padRight(15);
        currentLobbyTable.add(leaveLobbyButton).width(120).padRight(15);
        currentLobbyTable.add(startGameButton).width(120);

        currentLobbyTable.setVisible(false);
    }

    private void setupLobbyList() {
        lobbyListTable = new Table();
        lobbyListScrollPane = new ScrollPane(lobbyListTable, skin);
        lobbyListScrollPane.setScrollingDisabled(true, false);
        lobbyListScrollPane.setVariableSizeKnobs(false);
        lobbyListScrollPane.setFadeScrollBars(false);
    }

    private void layoutMainTable() {
        // Breadcrumb
        Label breadcrumbLabel = new Label("Main Menu > Multiplayer > Lobby Menu", skin);
        breadcrumbLabel.setColor(Color.LIGHT_GRAY);
        breadcrumbLabel.setFontScale(0.8f);
        breadcrumbLabel.setAlignment(Align.left);
        mainTable.add(breadcrumbLabel).fillX().padBottom(10).row();

        // Header
        Table headerTable = new Table();
        headerTable.add(titleLabel).expandX().center();
        headerTable.add(refreshButton).width(120).padRight(15);
        headerTable.add(backButton).width(100);

        mainTable.add(headerTable).fillX().padBottom(20).row();
        mainTable.add(statusLabel).fillX().padBottom(30).row();

        // Main content
        Table contentTable = new Table();

        // Left side - controls
        Table leftTable = new Table();
        leftTable.add(searchTable).fillX().padBottom(20).row();
        leftTable.add(createTable).fillX().padBottom(20).row();
        leftTable.add(currentLobbyTable).fillX();

        // Right side - lobby list
        Table rightTable = new Table();
        rightTable.pad(10);
        rightTable.setBackground(skin.newDrawable("white", new Color(0.1f, 0.1f, 0.1f, 0.8f)));

        Label lobbyListTitle = new Label("AVAILABLE LOBBIES", skin);
        lobbyListTitle.setColor(new Color(0.8f, 0.8f, 1.0f, 1.0f)); // Light blue
        lobbyListTitle.setFontScale(1.3f);
        rightTable.add(lobbyListTitle).padBottom(15).row();
        rightTable.add(lobbyListScrollPane).expand().fill();

        contentTable.add(leftTable).width(450).top().padRight(30);
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
        lobbyRow.pad(15);
        lobbyRow.setBackground(skin.newDrawable("white", new Color(0.15f, 0.15f, 0.2f, 0.9f)));

        // Lobby info
        String lobbyInfo = lobby.getName() + " (ID: " + lobby.getId() + ")";
        Label lobbyNameLabel = new Label(lobbyInfo, skin);
        lobbyNameLabel.setColor(Color.WHITE);
        lobbyNameLabel.setFontScale(1.1f);

        String playerInfo = lobby.getPlayers().size() + "/" + lobby.getSettings().getMaxPlayers() + " players";
        Label playersLabel = new Label(playerInfo, skin);
        playersLabel.setColor(new Color(0.7f, 0.7f, 0.7f, 1.0f));

        // Status indicators
        String statusText = "";
        Color statusColor = Color.WHITE;

        if (!lobby.getSettings().isVisible()) {
            statusText += "[HIDDEN] ";
            statusColor = new Color(1.0f, 0.6f, 0.0f, 1.0f); // Orange
        }
        if (lobby.getSettings().isPrivate()) {
            statusText += "[PRIVATE] ";
            statusColor = new Color(1.0f, 1.0f, 0.0f, 1.0f); // Yellow
        }
        if (lobby.getStatus() == Lobby.LobbyStatus.IN_GAME) {
            statusText += "[IN GAME] ";
            statusColor = new Color(1.0f, 0.2f, 0.2f, 1.0f); // Red
        }

        Label statusLabel = new Label(statusText, skin);
        statusLabel.setColor(statusColor);

        // Join button
        TextButton joinButton = new TextButton("JOIN", skin);
        joinButton.setColor(new Color(0.2f, 0.8f, 0.2f, 1.0f)); // Green
        joinButton.setDisabled(isInLobby || !lobby.canJoin());

        joinButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                joinLobby(lobby);
            }
        });

        // Layout row
        lobbyRow.add(lobbyNameLabel).expandX().left().padRight(15);
        lobbyRow.add(playersLabel).padRight(15);
        lobbyRow.add(statusLabel).padRight(15);
        lobbyRow.add(joinButton).width(100);

        lobbyListTable.add(lobbyRow).fillX().padBottom(8).row();
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
        passwordDialog.getContentTable().add(passwordDialogField).width(250).padTop(15);

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
        showStatus("Lobby created successfully! ID: " + lobby.getId());
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
            currentLobbyLabel.setColor(new Color(0.2f, 1.0f, 0.2f, 1.0f)); // Green

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
            noLobbiesLabel.setFontScale(1.2f);
            lobbyListTable.add(noLobbiesLabel).pad(30);
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
        statusLabel.setColor(new Color(1.0f, 0.2f, 0.2f, 1.0f)); // Red
    }

    public void goBackToMultiplayerMenu() {
        Main.getGame().getScreen().dispose();
        MultiplayerMenuController multiplayerController = new MultiplayerMenuController();
        org.example.client.views.MultiplayerMenuScreen multiplayerScreen = new org.example.client.views.MultiplayerMenuScreen(multiplayerController, skin);
        Main.getGame().setScreen(multiplayerScreen);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        controller.refreshLobbies(); // Load initial lobby list
    }

    @Override
    public void render(float delta) {
        // Clear with gradient background
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.1f, 1.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update refresh timer
        refreshTimer += delta;
        if (refreshTimer >= REFRESH_INTERVAL) {
            refreshTimer = 0;
            controller.refreshLobbies(); // Auto-refresh lobby list
        }

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
        batch.dispose();
        titleFont.dispose();
    }
}
