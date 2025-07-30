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

import java.util.HashMap;

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
    private List<Object> availableFarms;
    private Map<String, Integer> playerSelections;
    private boolean inFarmSelectionPhase = false;
    private String gameSessionId;

    public FarmSelectionScreen() {
        this.batch = new SpriteBatch();
        this.stage = new Stage(new ScreenViewport());
        this.skin = AssetManager.getAssetManager().getSkin();
        this.farmButtons = new TextButton[4];

        setupUI();
        setupNetworkListeners();
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
        System.out.println("DEBUG: FarmSelectionScreen.updateFarmSelectionUI called - inFarmSelectionPhase: " + inFarmSelectionPhase);
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
                System.out.println("DEBUG: FarmSelectionScreen - Farm " + i + " available: " + isAvailable);
            }

            // Update player selections display
            updatePlayerSelectionsDisplay();
            System.out.println("DEBUG: FarmSelectionScreen - Updated player selections display");
        } else {
            System.out.println("DEBUG: FarmSelectionScreen - Not in farm selection phase, skipping UI update");
        }
    }

    private boolean isFarmAvailable(int farmIndex) {
        if (availableFarms == null) return true;

        // Check if the farm index is in the available farms list
        for (Object farm : availableFarms) {
            if (farm instanceof Integer && (Integer) farm == farmIndex) {
                return true;
            }
            if (farm instanceof String && farm.equals(String.valueOf(farmIndex))) {
                return true;
            }
            if (farm instanceof Double && ((Double) farm).intValue() == farmIndex) {
                return true;
            }
        }
        return false;
    }

    private void updatePlayerSelectionsDisplay() {
        System.out.println("DEBUG: FarmSelectionScreen.updatePlayerSelectionsDisplay called");
        System.out.println("DEBUG: FarmSelectionScreen - playerSelections: " + playerSelections);

        playerSelectionsTable.clear();

        Label selectionsTitle = new Label("Player Selections:", skin);
        selectionsTitle.setColor(Color.CYAN);
        playerSelectionsTable.add(selectionsTitle).colspan(2).padBottom(10);
        playerSelectionsTable.row();

        if (playerSelections != null && !playerSelections.isEmpty()) {
            System.out.println("DEBUG: FarmSelectionScreen - Processing " + playerSelections.size() + " player selections");
            for (Map.Entry<String, Integer> entry : playerSelections.entrySet()) {
                String username = entry.getKey();
                Integer farmIndex = entry.getValue();
                System.out.println("DEBUG: FarmSelectionScreen - Player " + username + " selected farm " + farmIndex);

                Label playerLabel = new Label(username + ": ", skin);
                playerLabel.setColor(Color.WHITE);

                if (farmIndex != null && farmIndex >= 0) {
                    Label farmLabel = new Label("Farm " + farmIndex, skin);
                    farmLabel.setColor(Color.GREEN);
                    playerSelectionsTable.add(playerLabel).left().padRight(10);
                    playerSelectionsTable.add(farmLabel).left();
                } else {
                    Label farmLabel = new Label("Not selected yet", skin);
                    farmLabel.setColor(Color.YELLOW);
                    playerSelectionsTable.add(playerLabel).left().padRight(10);
                    playerSelectionsTable.add(farmLabel).left();
                }
                playerSelectionsTable.row();
            }
        } else {
            System.out.println("DEBUG: FarmSelectionScreen - No player selections to display");
            Label noSelectionsLabel = new Label("No selections yet", skin);
            noSelectionsLabel.setColor(Color.GRAY);
            playerSelectionsTable.add(noSelectionsLabel).colspan(2);
        }
    }

    @Override
    public void onLobbyMessage(Message message) {
        System.out.println("DEBUG: FarmSelectionScreen.onLobbyMessage - Received message type: " + message.getType());
        Gdx.app.postRunnable(() -> {
            switch (message.getType()) {
                case START_GAME:
                    System.out.println("DEBUG: FarmSelectionScreen - Handling START_GAME message");
                    handleGameStarted(message);
                    break;
                case FARM_SELECTION_UPDATE:
                    System.out.println("DEBUG: FarmSelectionScreen - Handling FARM_SELECTION_UPDATE message");
                    handleFarmSelectionUpdate(message);
                    break;
                case FARM_SELECTION_COMPLETE:
                    System.out.println("DEBUG: FarmSelectionScreen - Handling FARM_SELECTION_COMPLETE message");
                    handleFarmSelectionComplete(message);
                    break;
                default:
                    System.out.println("DEBUG: FarmSelectionScreen - Unhandled message type: " + message.getType());
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

            // Extract farm selection data from the message
            Object availableFarmsObj = message.getFromBody("availableFarms");
            if (availableFarmsObj instanceof List) {
                availableFarms = (List<Object>) availableFarmsObj;
            } else {
                availableFarms = new ArrayList<>();
            }

            Object playerSelectionsObj = message.getFromBody("playerSelections");
            if (playerSelectionsObj instanceof Map) {
                playerSelections = (Map<String, Integer>) playerSelectionsObj;
            } else {
                playerSelections = new HashMap<>();
            }

            statusLabel.setText(messageText != null ? messageText : "Game started! Select your farm.");
            statusLabel.setColor(Color.GREEN);
            updateFarmSelectionUI();
            System.out.println("DEBUG: FarmSelectionScreen - Entered farm selection phase");
        } else {
            System.out.println("DEBUG: FarmSelectionScreen - Not in farm selection phase, isInSelectionPhase: " + isInSelectionPhase);
        }
    }

    private void handleFarmSelectionUpdate(Message message) {
        System.out.println("DEBUG: FarmSelectionScreen.handleFarmSelectionUpdate called");

        Object availableFarmsObj = message.getFromBody("availableFarms");
        if (availableFarmsObj instanceof List) {
            availableFarms = (List<Object>) availableFarmsObj;
            System.out.println("DEBUG: FarmSelectionScreen - Updated availableFarms: " + availableFarms);
        }

        Object playerSelectionsObj = message.getFromBody("playerSelections");
        if (playerSelectionsObj instanceof Map) {
            playerSelections = (Map<String, Integer>) playerSelectionsObj;
            System.out.println("DEBUG: FarmSelectionScreen - Updated playerSelections: " + playerSelections);
        }

        String username = message.getFromBody("username");
        Integer farmIndex = message.getFromBody("farmIndex");

        System.out.println("DEBUG: FarmSelectionScreen - username: " + username + ", farmIndex: " + farmIndex);
        System.out.println("DEBUG: FarmSelectionScreen - availableFarms: " + availableFarms);
        System.out.println("DEBUG: FarmSelectionScreen - playerSelections: " + playerSelections);

        if (username != null && farmIndex != null) {
            statusLabel.setText(username + " selected Farm " + farmIndex);
            statusLabel.setColor(Color.CYAN);
            System.out.println("DEBUG: FarmSelectionScreen - Updated status label");
        }

        updateFarmSelectionUI();
        System.out.println("DEBUG: FarmSelectionScreen - Updated farm selection UI");
    }

    private void handleFarmSelectionComplete(Message message) {
        System.out.println("DEBUG: FarmSelectionScreen.handleFarmSelectionComplete called");
        inFarmSelectionPhase = false;

        // Extract data from the new message structure
        Object completeGameStateObj = message.getFromBody("completeGameState");
        String messageText = message.getFromBody("message");
        Boolean isActive = message.getFromBody("isActive");
        Boolean inFarmSelectionPhase = message.getFromBody("inFarmSelectionPhase");

        System.out.println("DEBUG: Complete game state object: " + (completeGameStateObj != null ? "present" : "null"));
        System.out.println("DEBUG: Message text: " + messageText);
        System.out.println("DEBUG: Is active: " + isActive);
        System.out.println("DEBUG: In farm selection phase: " + inFarmSelectionPhase);

        // Validate that we have the required data
        if (completeGameStateObj == null) {
            System.err.println("DEBUG: No complete game state received, cannot proceed to game");
            statusLabel.setText("Error: No game state received from server");
            statusLabel.setColor(Color.RED);
            return;
        }

        if (isActive == null || !isActive) {
            System.err.println("DEBUG: Game is not active, cannot proceed");
            statusLabel.setText("Error: Game is not active");
            statusLabel.setColor(Color.RED);
            return;
        }

        // Extract data from the complete game state if available
        final String gameSessionId;
        final Object playersData;
        final Object gameData;
        final String currentPlayerUsername;
        final Object allPlayersInfoObj;

        if (completeGameStateObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> completeGameState = (Map<String, Object>) completeGameStateObj;

            gameSessionId = (String) completeGameState.get("gameSessionId");
            playersData = completeGameState.get("playersData");
            gameData = completeGameState.get("gameData");
            currentPlayerUsername = (String) completeGameState.get("currentPlayerUsername");
            allPlayersInfoObj = completeGameState.get("allPlayersInfo");

            System.out.println("DEBUG: Extracted from complete game state - sessionId: " + gameSessionId);
            System.out.println("DEBUG: Current player username: " + currentPlayerUsername);
            System.out.println("DEBUG: All players info: " + (allPlayersInfoObj != null ? "present" : "null"));
        } else {
            // Fallback to old structure
            gameSessionId = message.getFromBody("gameSessionId");
            playersData = message.getFromBody("playersData");
            gameData = message.getFromBody("gameData");
            currentPlayerUsername = message.getFromBody("currentPlayerUsername");
            allPlayersInfoObj = message.getFromBody("allPlayersInfo");

            System.out.println("DEBUG: Using fallback structure - sessionId: " + gameSessionId);
        }

        // Validate essential data
        if (gameSessionId == null) {
            System.err.println("DEBUG: No game session ID received");
            statusLabel.setText("Error: No game session ID");
            statusLabel.setColor(Color.RED);
            return;
        }

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
        System.out.println("DEBUG: FarmSelectionScreen - Farm selection complete, navigating to multiplayer game");

        // Use a timer to navigate after showing the completion message
        new Thread(() -> {
            try {
                Thread.sleep(2000); // Wait 2 seconds to show completion message
                Gdx.app.postRunnable(() -> {
                    navigateToMultiplayerGame(gameSessionId, playersData, gameData, currentPlayerUsername, allPlayersInfoObj);
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("DEBUG: Navigation thread interrupted");
            }
        }).start();
    }

    private void navigateToMultiplayerGame(String gameSessionId, Object playersData, Object gameData, String currentPlayerUsername, Object allPlayersInfoObj) {
        try {
            System.out.println("DEBUG: FarmSelectionScreen - Navigating to multiplayer game with session ID: " + gameSessionId);

            // Get current user from network client
            NetworkClient networkClient = NetworkClient.getInstance();
            User currentUser = networkClient.getAuthenticatedUser();
            if (currentUser == null) {
                System.err.println("DEBUG: No authenticated user found");
                statusLabel.setText("Error: No authenticated user");
                statusLabel.setColor(Color.RED);
                return;
            }

            System.out.println("DEBUG: Current authenticated user: " + currentUser.getUsername());

            // Create a complete game structure for multiplayer with all players
            List<Player> allPlayers = new ArrayList<>();
            Player currentPlayer = null;
            GameMap gameMap = new GameMap();

            // Process all players info if available
            if (allPlayersInfoObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> allPlayersInfo = (Map<String, Object>) allPlayersInfoObj;

                System.out.println("DEBUG: Processing " + allPlayersInfo.size() + " players from server");

                for (Map.Entry<String, Object> entry : allPlayersInfo.entrySet()) {
                    String username = entry.getKey();
                    Object playerInfoObj = entry.getValue();

                    if (playerInfoObj instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> playerInfo = (Map<String, Object>) playerInfoObj;

                        // Create user and player for this username
                        User user = new User(username, "temp", "temp@temp.com", username, null);
                        Player player = new Player(user);

                        // Set player properties from server data
                        Object posX = playerInfo.get("posX");
                        Object posY = playerInfo.get("posY");
                        Object energy = playerInfo.get("energy");
                        Object money = playerInfo.get("money");
                        Object isCurrentPlayer = playerInfo.get("isCurrentPlayer");

                        if (posX instanceof Number) player.setPosX(((Number) posX).floatValue());
                        if (posY instanceof Number) player.setPosY(((Number) posY).floatValue());
                        if (energy instanceof Number) player.setEnergy(((Number) energy).intValue());
                        if (money instanceof Number) {
                            int currentMoney = player.getMoney();
                            int targetMoney = ((Number) money).intValue();
                            if (targetMoney > currentMoney) {
                                player.increaseMoney(targetMoney - currentMoney);
                            } else if (targetMoney < currentMoney) {
                                player.decreaseMoney(currentMoney - targetMoney);
                            }
                        }

                        // Create farm for this player
                        Object farmIndexObj = playerInfo.get("farmIndex");
                        if (farmIndexObj instanceof Number) {
                            int farmIndex = ((Number) farmIndexObj).intValue();
                            if (farmIndex >= 0 && farmIndex <= 3) {
                                Farm farm = new Farm(username + "'s Farm", player, farmIndex == 0, farmIndex);
                                player.setCurrentFarm(farm);
                                gameMap.addFarm(farm);
                                System.out.println("DEBUG: Created farm " + farmIndex + " for player " + username);
                            }
                        }

                        allPlayers.add(player);

                        // Identify current player (the one for this client)
                        if (username.equals(currentUser.getUsername())) {
                            currentPlayer = player;
                            System.out.println("DEBUG: Identified current player: " + username);
                        }

                        // Log if this player is the current player in the game
                        if (isCurrentPlayer instanceof Boolean && (Boolean) isCurrentPlayer) {
                            System.out.println("DEBUG: Player " + username + " is the current player in the game");
                        }
                    }
                }
            }

            // Fallback: if we don't have all players info, create basic structure
            if (allPlayers.isEmpty()) {
                System.out.println("DEBUG: No allPlayersInfo available, creating fallback structure");
                currentPlayer = new Player(currentUser);
                allPlayers.add(currentPlayer);

                // Create farms based on player selections
                if (playerSelections != null && !playerSelections.isEmpty()) {
                    for (Map.Entry<String, Integer> entry : playerSelections.entrySet()) {
                        String username = entry.getKey();
                        Integer farmIndex = entry.getValue();

                        if (farmIndex != null && farmIndex >= 0 && farmIndex <= 3) {
                            // Create a player for this username
                            User farmUser = new User(username, "temp", "temp@temp.com", username, null);
                            Player farmPlayer = new Player(farmUser);

                            // Create farm for this player
                            Farm farm = new Farm(username + "'s Farm", farmPlayer, farmIndex == 0, farmIndex);
                            farmPlayer.setCurrentFarm(farm);
                            gameMap.addFarm(farm);

                            // If this is the current user's farm, update the current player
                            if (username.equals(currentUser.getUsername())) {
                                currentPlayer.setCurrentFarm(farm);
                            }
                        }
                    }
                }
            }

            // Ensure we have a current player
            if (currentPlayer == null) {
                System.err.println("DEBUG: Failed to identify current player, creating fallback");
                currentPlayer = new Player(currentUser);
                allPlayers.add(currentPlayer);
            }

            // Validate that current player has a farm
            if (currentPlayer.getCurrentFarm() == null) {
                System.err.println("DEBUG: Current player has no farm assigned");
                statusLabel.setText("Error: No farm assigned to current player");
                statusLabel.setColor(Color.RED);
                return;
            }

            // Create the game with all players
            Game game = new Game(allPlayers, currentPlayer);
            game.setSaveName("Multiplayer_" + gameSessionId);
            game.setGameMap(gameMap);
            game.isMultiplayer = true; // Mark as multiplayer game

            // Sync farm selections from server data
            if (playerSelections != null) {
                game.syncFarmSelectionsFromServer(playerSelections);
            }

            // Set the game in App
            App.setGame(game);

            // Debug: Verify game state
            System.out.println("DEBUG: Game created with " + game.getPlayers().size() + " players");
            System.out.println("DEBUG: Current player: " + (game.getCurrentPlayer() != null ? game.getCurrentPlayer().getUser().getUsername() : "null"));
            System.out.println("DEBUG: Current player's farm: " + (game.getCurrentPlayer() != null && game.getCurrentPlayer().getCurrentFarm() != null ?
                game.getCurrentPlayer().getCurrentFarm().getName() : "null"));
            System.out.println("DEBUG: Game map farms: " + (game.getGameMap() != null ? game.getGameMap().getFarms().size() : "null"));
            System.out.println("DEBUG: Farm selections: " + game.getPlayerFarmSelections());

            // Initialize the game map
            System.out.println("FarmSelectionScreen: About to initialize NPCs...");
            if (gameMap.getVillage() != null) {
                System.out.println("FarmSelectionScreen: Village is not null, calling initializeNPCs()");
                gameMap.getVillage().initializeNPCs();
            } else {
                System.out.println("FarmSelectionScreen: Village is null, cannot initialize NPCs");
            }
            gameMap.updateTilesFromRegions();

            System.out.println("DEBUG: FarmSelectionScreen - Initialized game with " + allPlayers.size() + " players");

            // Create and set the game view
            System.out.println("DEBUG: Creating GameView...");
            GameView gameView = new GameView(new GameMenuController(currentPlayer), currentPlayer, game,
                AssetManager.getAssetManager().getSkin(), currentUser);
            System.out.println("DEBUG: GameView created successfully");

            // Navigate to game - always use postRunnable to ensure it happens on the main thread
            System.out.println("DEBUG: Scheduling screen change on main thread...");
            Gdx.app.postRunnable(() -> {
                System.out.println("DEBUG: Getting Main game instance on main thread...");
                Main mainGame = Main.getGame();
                if (mainGame != null) {
                    System.out.println("DEBUG: Main game instance found, disposing current screen...");
                    if (mainGame.getScreen() != null) {
                        mainGame.getScreen().dispose();
                    }
                    System.out.println("DEBUG: Setting new screen to GameView...");
                    mainGame.setScreen(gameView);
                    System.out.println("DEBUG: Successfully navigated to multiplayer game with session ID: " + gameSessionId);
                    System.out.println("DEBUG: Each player now has their own Player object and the same game state");
                } else {
                    System.err.println("DEBUG: Main game instance is null on main thread!");
                }
            });

        } catch (Exception e) {
            System.err.println("DEBUG: Failed to navigate to multiplayer game: " + e.getMessage());
            e.printStackTrace();
            statusLabel.setText("Error: Failed to start game");
            statusLabel.setColor(Color.RED);
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
