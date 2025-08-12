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
    }

    private void selectFarm(int farmIndex) {
        NetworkClient networkClient = NetworkClient.getInstance();
        networkClient.selectFarm(farmIndex);
        statusLabel.setText("Selecting farm " + farmIndex + "...");
        statusLabel.setColor(Color.YELLOW);
    }

    private void goBackToLobby() {
        // Navigate back to lobby menu
        // TODO
        statusLabel.setText("Returning to lobby...");
    }

    private void updateFarmSelectionUI() {
        // Always update UI if we have farm selection data, regardless of phase flag
        boolean shouldUpdate = inFarmSelectionPhase || (availableFarms != null || playerSelections != null);

        if (shouldUpdate) {
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
        if (availableFarms == null) {
            return true;
        }

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

        // Always set farm selection phase to true when we receive a START_GAME message
        // This ensures we can handle farm selection updates properly
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
    }

    private void handleFarmSelectionUpdate(Message message) {
        Object availableFarmsObj = message.getFromBody("availableFarms");
        if (availableFarmsObj instanceof List) {
            availableFarms = (List<Object>) availableFarmsObj;
        }

        Object playerSelectionsObj = message.getFromBody("playerSelections");
        if (playerSelectionsObj instanceof Map) {
            playerSelections = (Map<String, Integer>) playerSelectionsObj;
        }

        String username = message.getFromBody("username");
        Integer farmIndex = message.getFromBody("farmIndex");

        if (username != null && farmIndex != null) {
            statusLabel.setText(username + " selected Farm " + farmIndex);
            statusLabel.setColor(Color.CYAN);
        }

        // Ensure we're in farm selection phase when we receive updates
        if (!inFarmSelectionPhase) {
            inFarmSelectionPhase = true;
        }

        updateFarmSelectionUI();
    }

    private void handleFarmSelectionComplete(Message message) {
        inFarmSelectionPhase = false;

        // Extract data from the new message structure
        Object completeGameStateObj = message.getFromBody("completeGameState");
        String messageText = message.getFromBody("message");
        Boolean isActive = message.getFromBody("isActive");
        Boolean inFarmSelectionPhase = message.getFromBody("inFarmSelectionPhase");

        // Validate that we have the required data
        if (completeGameStateObj == null) {
            statusLabel.setText("Error: No game state received from server");
            statusLabel.setColor(Color.RED);
            return;
        }

        if (isActive == null || !isActive) {
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

        } else {
            // Fallback to old structure
            gameSessionId = message.getFromBody("gameSessionId");
            playersData = message.getFromBody("playersData");
            gameData = message.getFromBody("gameData");
            currentPlayerUsername = message.getFromBody("currentPlayerUsername");
            allPlayersInfoObj = message.getFromBody("allPlayersInfo");
        }

        // Validate essential data
        if (gameSessionId == null) {
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

        // Navigate to the actual multiplayer game immediately
        // Navigate immediately instead of waiting
        Gdx.app.postRunnable(() -> {
            navigateToMultiplayerGame(gameSessionId, playersData, gameData, currentPlayerUsername, allPlayersInfoObj);
        });
    }

    private void navigateToMultiplayerGame(String gameSessionId, Object playersData, Object gameData, String currentPlayerUsername, Object allPlayersInfoObj) {
        try {
            // Get current user from network client
            NetworkClient networkClient = NetworkClient.getInstance();
            User currentUser = networkClient.getAuthenticatedUser();
            if (currentUser == null) {
                statusLabel.setText("Error: No authenticated user");
                statusLabel.setColor(Color.RED);
                return;
            }

            // Create a complete game structure for multiplayer with all players
            List<Player> allPlayers = new ArrayList<>();
            Player currentPlayer = null;
            GameMap gameMap = new GameMap();

            // Process all players info if available
            if (allPlayersInfoObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> allPlayersInfo = (Map<String, Object>) allPlayersInfoObj;



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
                        // Update sprite position after setting coordinates
                        player.updatePosition();
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

                            }
                        }

                        allPlayers.add(player);

                        // Identify current player (the one for this client)
                        if (username.equals(currentUser.getUsername())) {
                            currentPlayer = player;
                        }

                        // Log if this player is the current player in the game
                        if (isCurrentPlayer instanceof Boolean && (Boolean) isCurrentPlayer) {
                            // This player is the current player in the game
                        }
                    }
                }
            }

            // Fallback: if we don't have all players info, create basic structure
            if (allPlayers.isEmpty()) {
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
                currentPlayer = new Player(currentUser);
                allPlayers.add(currentPlayer);
            }

            // Validate that current player has a farm
            if (currentPlayer.getCurrentFarm() == null) {
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

            game.setCurrentPlayer(currentPlayer);

            // Find the index of the current player in the players list
            int currentPlayerIndex = allPlayers.indexOf(currentPlayer);
            if (currentPlayerIndex >= 0) {
                game.setCurrentPlayerIndex(currentPlayerIndex);
            }

            // Set the game in App
            App.setGame(game);

            // Initialize the game map
            if (gameMap.getVillage() != null) {
                gameMap.getVillage().initializeNPCs();
            }
            gameMap.updateTilesFromRegions();

            // Create and set the game view
//            System.out.println("DEBUG: Creating GameView...");
//            System.out.println("DEBUG: Current player: " + (currentPlayer != null ? currentPlayer.getUser().getUsername() : "null"));
//            System.out.println("DEBUG: Game: " + (game != null ? "not null" : "null"));
//            System.out.println("DEBUG: Current user: " + (currentUser != null ? currentUser.getUsername() : "null"));

            // Ensure the game is properly set in App before creating the GameMenuController
            if (App.getGame() != game) {
                App.setGame(game);
            }

            // Verify that the current player has a valid farm before creating the GameMenuController
            if (currentPlayer.getCurrentFarm() == null) {
                statusLabel.setText("Error: No farm assigned to current player");
                statusLabel.setColor(Color.RED);
                return;
            }

//            System.out.println("DEBUG: Creating GameMenuController with player: " + currentPlayer.getUser().getUsername());
//            System.out.println("DEBUG: Player's farm: " + currentPlayer.getCurrentFarm().getName());
//            System.out.println("DEBUG: Game in App: " + (App.getGame() != null ? "set" : "null"));

            // Create GameView with exception handling
            GameView gameView = new GameView(new GameMenuController(currentPlayer),
                currentPlayer, game, AssetManager.getAssetManager().getSkin(), currentUser);


            Gdx.app.postRunnable(() -> {
                try {
                    Main mainGame = Main.getGame();
                    if (mainGame != null) {
                        if (mainGame.getScreen() != null) {
                            mainGame.getScreen().dispose();
                        }
                        mainGame.setScreen(gameView);
                    } else {
                        statusLabel.setText("Error: Failed to get main game instance");
                        statusLabel.setColor(Color.RED);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    statusLabel.setText("Error: Failed to transition to game");
                    statusLabel.setColor(Color.RED);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error: Failed to start game");
            statusLabel.setColor(Color.RED);
        }
    }

    @Override
    public void render(float delta) {
        // Process network messages
        NetworkClient networkClient = NetworkClient.getInstance();
        networkClient.update();

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


    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
        // Unregister as lobby listener to prevent interference with screen transition
        NetworkClient networkClient = NetworkClient.getInstance();
        if (networkClient != null && networkClient.getMessageHandler() != null) {
            networkClient.getMessageHandler().setLobbyListener(null);
        }
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        //i think this line makes the glitch
        // skin.dispose();
    }
}
