package org.example.client.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.client.network.NetworkClient;
import org.example.client.network.ClientMessageHandler;
import org.example.common.models.Message;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import org.example.common.models.entities.Game;
import org.example.common.models.Player.Player;
import org.example.common.models.App;
import org.example.client.Main;
import org.example.client.views.GameView;
import org.example.client.controllers.GameMenuController;
import org.example.utils.AssetManager;
import org.example.common.models.entities.User;
import org.example.common.models.MapDetails.Farm;
import org.example.common.models.MapDetails.GameMap;
import org.example.common.models.enums.PlayerEnums.Gender;

public class FarmSelectionScreen implements Screen, ClientMessageHandler.LobbyMessageListener {
    private Stage stage;
    private SpriteBatch batch;
    private Skin skin;
    private Table mainTable;
    private Label titleLabel;
    private Label statusLabel;
    private Label infoLabel;
    private Table farmSelectionTable;
    private Table playerSelectionsTable;
    private TextButton[] farmButtons;
    private List<String> availableFarms;
    private Map<String, Integer> playerSelections;
    private boolean inFarmSelectionPhase = false;
    private String gameSessionId;

    public FarmSelectionScreen() {
        this.batch = new SpriteBatch();
        this.stage = new Stage(new ScreenViewport());
        this.skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        this.farmButtons = new TextButton[4];
        
        setupUI();
        setupNetworkListeners();
        
        // Check if this is single player mode and handle accordingly
        handleSinglePlayerMode();
    }

    private void setupUI() {
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.pad(20);

        // Title
        titleLabel = new Label("FARM SELECTION", skin);
        titleLabel.setColor(Color.WHITE);
        titleLabel.setFontScale(1.5f);
        mainTable.add(titleLabel).colspan(4).padBottom(20);
        mainTable.row();

        // Status label
        statusLabel = new Label("Waiting for game to start...", skin);
        statusLabel.setColor(Color.YELLOW);
        mainTable.add(statusLabel).colspan(4).padBottom(10);
        mainTable.row();

        // Info label
        infoLabel = new Label("Select your farm location:", skin);
        infoLabel.setColor(Color.CYAN);
        mainTable.add(infoLabel).colspan(4).padBottom(20);
        mainTable.row();

        // Farm selection grid
        farmSelectionTable = new Table();
        farmSelectionTable.pad(10);
        
        // Create farm buttons in a 2x2 grid
        for (int i = 0; i < 4; i++) {
            final int farmIndex = i;
            farmButtons[i] = new TextButton("Farm " + i, skin);
            farmButtons[i].setSize(150, 100);
            farmButtons[i].addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    selectFarm(farmIndex);
                }
            });
            
            // Add to grid (2x2 layout)
            if (i < 2) {
                farmSelectionTable.add(farmButtons[i]).width(150).height(100).pad(10);
            } else {
                farmSelectionTable.add(farmButtons[i]).width(150).height(100).pad(10);
            }
            
            if (i == 1 || i == 3) {
                farmSelectionTable.row();
            }
        }
        
        mainTable.add(farmSelectionTable).colspan(4);
        mainTable.row();

        // Player selections table
        playerSelectionsTable = new Table();
        playerSelectionsTable.pad(10);
        Label selectionsTitle = new Label("Player Selections:", skin);
        selectionsTitle.setColor(Color.CYAN);
        playerSelectionsTable.add(selectionsTitle).colspan(2).padBottom(10);
        playerSelectionsTable.row();
        
        mainTable.add(playerSelectionsTable).colspan(4).padTop(20);
        mainTable.row();

        // Back button
        TextButton backBtn = new TextButton("Back to Lobby", skin);
        backBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Handle back navigation
                goBackToLobby();
            }
        });
        mainTable.add(backBtn).colspan(4).padTop(20);

        stage.addActor(mainTable);
    }

    private void setupNetworkListeners() {
        // Don't override the existing lobby listener
        // The LobbyMenuController should handle the initial START_GAME message
        // and navigate to this screen. This screen will handle subsequent messages.
        System.out.println("DEBUG: FarmSelectionScreen - Network listeners setup (not overriding lobby listener)");
    }

    private void selectFarm(int farmIndex) {
        NetworkClient networkClient = NetworkClient.getInstance();
        networkClient.selectFarm(farmIndex);
        statusLabel.setText("Selecting farm " + farmIndex + "...");
        statusLabel.setColor(Color.YELLOW);
    }

    private void goBackToLobby() {
        // Navigate back to lobby menu
        // This would need to be implemented based on your navigation system
        statusLabel.setText("Returning to lobby...");
    }

    private void updateFarmSelectionUI() {
        if (inFarmSelectionPhase) {
            // Update farm button states
            for (int i = 0; i < 4; i++) {
                boolean isAvailable = isFarmAvailable(i);
                farmButtons[i].setDisabled(!isAvailable);
                
                if (isAvailable) {
                    farmButtons[i].setColor(Color.WHITE);
                } else {
                    farmButtons[i].setColor(Color.GRAY);
                }
            }
            
            // Update player selections display
            updatePlayerSelectionsDisplay();
        }
    }

    private boolean isFarmAvailable(int farmIndex) {
        if (availableFarms == null) return true;
        return availableFarms.contains(String.valueOf(farmIndex));
    }

    private void updatePlayerSelectionsDisplay() {
        playerSelectionsTable.clear();
        
        Label selectionsTitle = new Label("Player Selections:", skin);
        selectionsTitle.setColor(Color.CYAN);
        playerSelectionsTable.add(selectionsTitle).colspan(2).padBottom(10);
        playerSelectionsTable.row();
        
        if (playerSelections != null && !playerSelections.isEmpty()) {
            for (Map.Entry<String, Integer> entry : playerSelections.entrySet()) {
                String username = entry.getKey();
                Integer farmIndex = entry.getValue();
                
                Label playerLabel = new Label(username + ": ", skin);
                playerLabel.setColor(Color.WHITE);
                
                Label farmLabel = new Label("Farm " + farmIndex, skin);
                farmLabel.setColor(Color.GREEN);
                
                playerSelectionsTable.add(playerLabel).left().padRight(10);
                playerSelectionsTable.add(farmLabel).left();
                playerSelectionsTable.row();
            }
        } else {
            Label noSelectionsLabel = new Label("No selections yet", skin);
            noSelectionsLabel.setColor(Color.GRAY);
            playerSelectionsTable.add(noSelectionsLabel).colspan(2);
        }
    }

    @Override
    public void onLobbyMessage(Message message) {
        Gdx.app.postRunnable(() -> {
            switch (message.getType()) {
                case START_GAME:
                    handleGameStarted(message);
                    break;
                case FARM_SELECTION_UPDATE:
                    handleFarmSelectionUpdate(message);
                    break;
                case FARM_SELECTION_COMPLETE:
                    handleFarmSelectionComplete(message);
                    break;
                default:
                    statusLabel.setText("Received message: " + message.getType());
            }
        });
    }

    private void handleGameStarted(Message message) {
        // Check for both old and new field names for backward compatibility
        Boolean inFarmSelection = message.getFromBody("inFarmSelectionPhase");
        Boolean inMapSelection = message.getFromBody("inMapSelectionPhase");
        
        // Use either field name
        Boolean isInSelectionPhase = (inFarmSelection != null && inFarmSelection) || 
                                   (inMapSelection != null && inMapSelection);
        
        String sessionId = message.getFromBody("gameSessionId");
        String messageText = message.getFromBody("message");
        
        System.out.println("DEBUG: FarmSelectionScreen.handleGameStarted - inFarmSelection: " + inFarmSelection + 
                          ", inMapSelection: " + inMapSelection + 
                          ", isInSelectionPhase: " + isInSelectionPhase + 
                          ", sessionId: " + sessionId);
        
        if (isInSelectionPhase) {
            inFarmSelectionPhase = true;
            gameSessionId = sessionId;
            statusLabel.setText(messageText != null ? messageText : "Game started! Select your farm.");
            statusLabel.setColor(Color.GREEN);
            updateFarmSelectionUI();
            System.out.println("DEBUG: FarmSelectionScreen - Entered farm selection phase");
        } else {
            System.out.println("DEBUG: FarmSelectionScreen - Not in farm selection phase, isInSelectionPhase: " + isInSelectionPhase);
        }
    }

    private void handleFarmSelectionUpdate(Message message) {
        availableFarms = message.getFromBody("availableFarms");
        playerSelections = message.getFromBody("playerSelections");
        String username = message.getFromBody("username");
        Integer farmIndex = message.getFromBody("farmIndex");
        
        if (username != null && farmIndex != null) {
            statusLabel.setText(username + " selected Farm " + farmIndex);
            statusLabel.setColor(Color.CYAN);
        }
        
        updateFarmSelectionUI();
    }

    private void handleFarmSelectionComplete(Message message) {
        inFarmSelectionPhase = false;
        String messageText = message.getFromBody("message");
        Boolean isActive = message.getFromBody("isActive");
        String gameSessionId = message.getFromBody("gameSessionId");
        Object playersData = message.getFromBody("playersData");
        Object gameData = message.getFromBody("gameData");
        String currentPlayerUsername = message.getFromBody("currentPlayerUsername");
        
        statusLabel.setText(messageText != null ? messageText : "Farm selection complete! Game is starting...");
        statusLabel.setColor(Color.GREEN);
        
        // Disable all farm buttons
        for (TextButton button : farmButtons) {
            button.setDisabled(true);
            button.setColor(Color.GRAY);
        }
        
        updateFarmSelectionUI();
        
        // Show completion message
        infoLabel.setText("All players have selected their farms!");
        infoLabel.setColor(Color.GREEN);
        
        // Navigate to the actual multiplayer game after a short delay
        if (isActive != null && isActive && gameSessionId != null) {
            System.out.println("DEBUG: FarmSelectionScreen - Farm selection complete, navigating to multiplayer game");
            
            // Use a timer to navigate after showing the completion message
            new Thread(() -> {
                try {
                    Thread.sleep(2000); // Wait 2 seconds to show completion message
                    Gdx.app.postRunnable(() -> {
                        navigateToMultiplayerGame(gameSessionId, playersData, gameData, currentPlayerUsername);
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }
    
    private void navigateToMultiplayerGame(String gameSessionId, Object playersData, Object gameData, String currentPlayerUsername) {
        try {
            System.out.println("DEBUG: FarmSelectionScreen - Navigating to multiplayer game with session ID: " + gameSessionId);
            
            // Get current user from network client
            NetworkClient networkClient = NetworkClient.getInstance();
            User currentUser = networkClient.getAuthenticatedUser();
            if (currentUser == null) {
                System.err.println("DEBUG: No authenticated user found");
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
            
            // Don't initialize farms here - the server will handle farm initialization
            // based on the farm selections that were made during the farm selection phase
            // The server will send the complete game state via GAME_STATE_FULL message
            
            // Create and set the game view
            GameView gameView = new GameView(new GameMenuController(player), player, game, 
                AssetManager.getAssetManager().getSkin(), currentUser);
            
            // Navigate to game
            Main.getGame().getScreen().dispose();
            Main.getGame().setScreen(gameView);
            
            System.out.println("DEBUG: Successfully navigated to multiplayer game with session ID: " + gameSessionId);
            System.out.println("DEBUG: Waiting for server to send complete game state...");
            
        } catch (Exception e) {
            System.err.println("DEBUG: Failed to navigate to multiplayer game: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleSinglePlayerMode() {
        Game game = App.getGame();
        if (game != null && !game.isMultiplayer) {
            // Single player mode - automatically assign farms and start game
            System.out.println("DEBUG: Single player mode detected, auto-assigning farms");
            
            // Auto-assign farms to players
            List<Player> players = game.getPlayers();
            GameMap gameMap = game.getGameMap();
            
            for (int i = 0; i < players.size() && i < 4; i++) {
                Player player = players.get(i);
                boolean farmType = (i % 2 == 0); // Alternate farm types
                Farm farm = new Farm("guest farm", player, farmType, i);
                player.setCurrentFarm(farm);
                gameMap.addFarm(farm);
                
                // Mark farm as selected
                game.selectFarm(player, i);
            }
            
            // Update tiles and start game
            gameMap.updateTilesFromRegions();
            game.setInFarmSelectionPhase(false);
            
            // Navigate to game after a short delay
            new Thread(() -> {
                try {
                    Thread.sleep(1000); // Wait 1 second to show the screen
                    Gdx.app.postRunnable(() -> {
                        navigateToSinglePlayerGame();
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }
    
    private void navigateToSinglePlayerGame() {
        try {
            System.out.println("DEBUG: Navigating to single player game");
            
            Game game = App.getGame();
            Player currentPlayer = game.getCurrentPlayer();
            User currentUser = App.getLoggedInUser();
            
            if (currentUser == null) {
                // Create a default user for single player
                currentUser = new User("guest user", "1234", "guest@gmail.com", "guest", Gender.Male);
                App.setLoggedInUser(currentUser);
            }
            
            // Create and set the game view
            GameView gameView = new GameView(new GameMenuController(currentPlayer), currentPlayer, game, 
                AssetManager.getAssetManager().getSkin(), currentUser);
            
            // Navigate to game
            Main.getGame().getScreen().dispose();
            Main.getGame().setScreen(gameView);
            
            System.out.println("DEBUG: Successfully navigated to single player game");
            
        } catch (Exception e) {
            System.err.println("DEBUG: Failed to navigate to single player game: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void render(float delta) {
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
    public void show() {
        Gdx.input.setInputProcessor(stage);
        
        // Register as lobby listener to handle farm selection messages
        NetworkClient networkClient = NetworkClient.getInstance();
        ClientMessageHandler messageHandler = networkClient.getMessageHandler();
        messageHandler.setLobbyListener(this);
        
        System.out.println("DEBUG: FarmSelectionScreen - Registered as lobby listener");
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        skin.dispose();
    }
} 