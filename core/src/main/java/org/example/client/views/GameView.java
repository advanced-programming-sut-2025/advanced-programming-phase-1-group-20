package org.example.client.views;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.InputMultiplexer;
import org.example.client.Main;
import org.example.client.controllers.AnimalsController;
import org.example.client.controllers.GameMenuController;
import org.example.client.controllers.gameplay.AnimalController;
import org.example.client.controllers.gameplay.WorldController;
import org.example.client.views.gameplay.CookingScreen;
import org.example.client.views.gameplay.CraftingScreen;
import org.example.client.views.gameplay.InventoryScreen;
import org.example.client.views.gameplay.MapScreen;
import org.example.common.models.App;
import org.example.common.models.Items.Food;
import org.example.common.models.Items.Seed;
import org.example.common.models.Items.Tool;
import org.example.common.models.MapDetails.Farm;
import org.example.common.models.MapDetails.GameMap;
import org.example.common.models.MapDetails.Village;
import org.example.common.models.common.Location;
import org.example.common.models.Player.Player;
import org.example.common.models.common.Result;
import org.example.common.models.entities.animal.Animal;
import org.example.common.models.entities.animal.BarnAnimal;
import org.example.common.models.entities.animal.CoopAnimal;
import org.example.common.models.enums.PlayerEnums.Tools;
import org.example.common.models.enums.Types.TileType;
import org.example.common.models.common.Date;
import org.example.common.models.entities.Game;
import org.example.common.models.entities.User;
import org.example.common.models.enums.Seasons;
import org.example.common.models.enums.Weather;
import org.example.utils.AssetManager;
import org.example.client.views.effects.Lighting;
import org.example.client.views.effects.ClimateSystem; // NEW IMPORT
import org.example.client.views.effects.LightningSystem; // NEW IMPORT
import org.example.client.controllers.NPCSpriteController;

import org.example.client.views.fishing.FishingMiniGame;
import org.example.client.network.NetworkClient;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.graphics.Pixmap;
import org.example.common.models.Barn;
import org.example.common.models.Coop;

import java.util.ArrayList;
import java.util.List;

public class GameView implements Screen, InputProcessor {
    private Stage stage;
    private GameMenuController controller;
    private Player player;
    private Game game;
    private OrthographicCamera camera;
    private Skin skin;
    private TextButton pauseButton;
    private Table mainTable;
    private Table pauseTable;
    private TextButton resumeButton;
    private float gameTime;
    private Label timeLabel;
    private User user;

    // Clock components
    private Texture clockBackgroundTexture;
    private Texture clockNeedleTexture;
    private Image clockBackgroundImage;
    private Image clockNeedleImage;
    private Stack clockStack;
    private Label dateLabel;
    private Label moneyLabel;
    private Label timeDisplayLabel;
    private BitmapFont customFont;
    private BitmapFont smallFont;

    private AnimalsController animalsController;

    // Weather and Season components
    private Texture currentWeatherTexture;
    private Texture currentSeasonTexture;
    private Image weatherDisplayImage;
    private Image seasonDisplayImage;

    // Lighting system
    private Lighting lighting;
    private Color currentLightColor;
    private Label lightingDescriptionLabel;
    private Texture lightingOverlayTexture; // Full-screen lighting overlay

    private float lightingUpdateTimer;
    private static final float LIGHTING_UPDATE_INTERVAL = 0.5f; // Update every 0.5 seconds

    // Rain system - NEW
    private ClimateSystem climateSystem;

    // Lightning system - NEW
    private LightningSystem lightningSystem;

    // Terminal window for cheat commands
    private TerminalWindow terminalWindow;

    // Friends system
    private TextButton friendsButton;
    private FriendsWindow friendsWindow;

    // Previous state tracking for dynamic updates
    private Weather lastKnownWeather;
    private Seasons lastKnownSeason;
    private int lastKnownHour = -1;

    // Minimap
    private boolean isMapVisible = false;
    private Group minimapGroup;



    // Camera zoom state
    private boolean isCameraZoomedOut = false;
    private float normalZoom = 1.0f;
    private float zoomedOutZoom = 6.0f; // Zoom out to show entire map (larger value = more zoomed out)

    // NPC rendering
    private NPCSpriteController npcSpriteController;

    // Add these fields to GameView:
    private float lastToolMouseX = 0;
    private float lastToolMouseY = 0;

    // Vertical energy bars for all players
    private static final int VERTICAL_ENERGY_BAR_WIDTH = 20;
    private static final int VERTICAL_ENERGY_BAR_HEIGHT = 100;
    private static final int ENERGY_BAR_SPACING = 30;

    // Fish catch display - will be implemented later
    // private FishCatchDisplay fishCatchDisplay;

    private Runnable buildingPlacementListener;

    public GameView(GameMenuController controller, Player player, Game game, Skin skin, User user) {
        this.controller = controller;
        this.player = player;
        this.game = game;
        this.skin = skin;
        this.user = user;
        this.gameTime = 0;
        this.lightingUpdateTimer = 0;

        this.animalsController = new AnimalsController();

        // Initialize camera first
        camera = new OrthographicCamera(120, 120);
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Initialize lighting system
        initializeLighting();

        // Initialize rain system - NEW
        climateSystem = new ClimateSystem(camera); // Use the camera for rain coverage

        // Initialize lightning system - NEW
        lightningSystem = new LightningSystem(camera);

        // Initialize lighting overlay texture
        createLightingOverlayTexture();

        // Initialize terminal window for cheat commands
        terminalWindow = new TerminalWindow(controller);

        // Initialize friends system
        initializeFriendsButton();

        // Initialize NPC sprite controller
        npcSpriteController = new NPCSpriteController();

        loadCustomFont();
        initializeLabels();
        initializeClock();
        updateWeatherAndSeasonDisplays();

        initializeTables();
        controller.setView(this);

        // Set the current game instance in the ClientMessageHandler for multiplayer mode
        if (game != null && game.isMultiplayer) {
            NetworkClient networkClient = NetworkClient.getInstance();
            if (networkClient != null && networkClient.getMessageHandler() != null) {
                networkClient.getMessageHandler().setCurrentGame(game);
            }
        }
    }

    private void initializeLighting() {
        lighting = new Lighting();
        currentLightColor = Color.WHITE.cpy();

        // Initialize lighting with current game state
        Date gameDate = getCurrentGameDate();
        if (gameDate != null) {
            lighting.updateLighting(gameDate);
            currentLightColor = lighting.getLibGdxColor();
        }
    }

    private void loadCustomFont() {
        try {
            customFont = new BitmapFont(Gdx.files.internal("content/fonts/new.fnt"));
            smallFont = new BitmapFont(customFont.getData(), customFont.getRegion(), false);
        } catch (Exception e) {
            System.err.println("Failed to load custom font: " + e.getMessage());
            customFont = skin.getFont("default-font");
            smallFont = new BitmapFont(customFont.getData(), customFont.getRegion(), false);
        }
    }

    private void initializeLabels() {
        Label.LabelStyle customStyle = new Label.LabelStyle();
        customStyle.font = customFont;
        customStyle.fontColor = skin.getColor("white");
        customStyle.font.getData().setScale(0.7f);
        customStyle.font.getData().markupEnabled = true;

        timeLabel = new Label("" + gameTime, customStyle);

        // Initialize lighting description label
        Label.LabelStyle lightingStyle = new Label.LabelStyle();
        lightingStyle.font = smallFont;
        lightingStyle.fontColor = Color.WHITE;
        lightingStyle.font.getData().setScale(0.4f);
        lightingStyle.font.getData().markupEnabled = true;

        lightingDescriptionLabel = new Label("", lightingStyle);
    }

    private void initializeClock() {
        clockBackgroundTexture = new Texture("content/clock/clock.png");
        clockNeedleTexture = new Texture("content/clock/flesh.png");

        clockBackgroundImage = new Image(clockBackgroundTexture);
        clockNeedleImage = new Image(clockNeedleTexture);

        float clockSize = 120f;
        clockBackgroundImage.setSize(clockSize, clockSize);
        clockNeedleImage.setSize(13f, 33f);

        clockNeedleImage.setOrigin(clockNeedleImage.getWidth() / 2, 0);
        clockNeedleImage.setPosition(
            (clockBackgroundImage.getWidth() - clockNeedleImage.getWidth()),
            (clockBackgroundImage.getHeight() - clockNeedleImage.getHeight()) / 2 + 35f
        );

        Group clockGroup = new Group();
        clockGroup.setSize(clockBackgroundImage.getWidth(), clockBackgroundImage.getHeight());

        clockGroup.addActor(clockBackgroundImage);

        createWeatherAndSeasonDisplays(clockGroup, clockSize);

        clockGroup.addActor(clockNeedleImage);

        createClockLabels();

        clockStack = new Stack();
        clockStack.add(clockGroup);
        clockStack.add(createTextTable());
        clockStack.setSize(clockSize, clockSize);
    }

    private void createWeatherAndSeasonDisplays(Group clockGroup, float clockSize) {
        weatherDisplayImage = new Image();
        seasonDisplayImage = new Image();

        float height = AssetManager.getAssetManager().getFallTexture().getHeight() * 2.05f;
        float width = AssetManager.getAssetManager().getFallTexture().getWidth() * 1.8f;

        float centerX = (clockSize - width) / 2;
        float centerY = (clockSize - height) / 2;

        weatherDisplayImage.setSize(width, height);
        weatherDisplayImage.setPosition(centerX, centerY + 19.65f);

        seasonDisplayImage.setSize(width, height);
        seasonDisplayImage.setPosition(centerX + 38f, centerY + 19.65f);

        clockGroup.addActor(weatherDisplayImage);
        clockGroup.addActor(seasonDisplayImage);
    }

    private void createClockLabels() {
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = smallFont;
        labelStyle.fontColor = Color.BLACK;
        labelStyle.font.getData().setScale(0.45f);
        labelStyle.font.getData().markupEnabled = true;

        dateLabel = new Label("[b]Mon. 1[/b]", labelStyle);
        timeDisplayLabel = new Label("[b]6:00 am[/b]", labelStyle);

        // Add this part for money label
        Label.LabelStyle moneyStyle = new Label.LabelStyle();
        moneyStyle.font = smallFont;
        moneyStyle.fontColor = Color.RED;
        moneyStyle.font.getData().setScale(0.45f);
        moneyStyle.font.getData().markupEnabled = true;

        moneyLabel = new Label("[b]$0[/b]", moneyStyle);


    }

    private Table createTextTable() {
        Table textTable = new Table();
        textTable.top().right().padTop(10).padRight(10);
        textTable.setFillParent(true);
        textTable.add(dateLabel).row();
        textTable.add(timeDisplayLabel).padTop(5).row();
        textTable.add(moneyLabel).padTop(47).row();

        textTable.add(lightingDescriptionLabel).padTop(5);
        return textTable;
    }

    private void updateLighting(float deltaTime) {
        lightingUpdateTimer += deltaTime;

        if (lightingUpdateTimer >= LIGHTING_UPDATE_INTERVAL || hasTimeChanged()) {
            Date gameDate = getCurrentGameDate();
            if (gameDate != null) {
                lighting.updateLighting(gameDate);
                currentLightColor = lighting.getLibGdxColor();

                // Apply lighting to UI elements
                applyLightingToUI();

                lastKnownHour = gameDate.getHour();
            }
            lightingUpdateTimer = 0;
        }
    }

    private boolean hasTimeChanged() {
        Date gameDate = getCurrentGameDate();
        return gameDate != null && gameDate.getHour() != lastKnownHour;
    }

    private void applyLightingToUI() {
        // Apply lighting color to various UI elements
        if (clockBackgroundImage != null) {
            clockBackgroundImage.setColor(currentLightColor);
        }

        if (weatherDisplayImage != null) {
            weatherDisplayImage.setColor(currentLightColor);
        }

        if (seasonDisplayImage != null) {
            seasonDisplayImage.setColor(currentLightColor);
        }

        // Adjust text colors based on lighting
        adjustTextColors();
    }

    private void adjustTextColors() {
        float brightness = (currentLightColor.r + currentLightColor.g + currentLightColor.b) / 3f;

        if (brightness < 0.3f) {
            if (dateLabel != null) {
                dateLabel.setColor(Color.WHITE);
            }
            if (timeDisplayLabel != null) {
                timeDisplayLabel.setColor(Color.WHITE);
            }
        } else {
            if (dateLabel != null) {
                dateLabel.setColor(Color.BLACK);
            }
            if (timeDisplayLabel != null) {
                timeDisplayLabel.setColor(Color.BLACK);
            }
        }

        if (lightingDescriptionLabel != null) {
            lightingDescriptionLabel.setColor(Color.WHITE);
        }
    }

    private void updateWeatherAndSeasonDisplays() {
        try {
            Date currentDate = getCurrentGameDate();
            if (currentDate == null) return;

            Weather currentWeather = currentDate.getWeatherToday();
            Seasons currentSeason = currentDate.getSeason();

            if (hasWeatherChanged(currentWeather) || hasSeasonChanged(currentSeason)) {
                updateWeatherDisplay(currentWeather);
                updateSeasonDisplay(currentSeason);

                lastKnownWeather = currentWeather;
                lastKnownSeason = currentSeason;
            }
        } catch (Exception e) {
            System.err.println("Error updating weather and season displays: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean hasWeatherChanged(Weather currentWeather) {
        return lastKnownWeather == null || !lastKnownWeather.equals(currentWeather);
    }

    private boolean hasSeasonChanged(Seasons currentSeason) {
        return lastKnownSeason == null || !lastKnownSeason.equals(currentSeason);
    }

    private void updateWeatherDisplay(Weather weather) throws Exception {
        String weatherMethodName = buildWeatherMethodName(weather);
        Texture weatherTexture = getTextureUsingReflection(weatherMethodName);

        setPrivateField("currentWeatherTexture", weatherTexture);
        updateImageDrawable(weatherDisplayImage, weatherTexture);
    }

    private void updateSeasonDisplay(Seasons season) throws Exception {
        String seasonMethodName = buildSeasonMethodName(season);
        Texture seasonTexture = getTextureUsingReflection(seasonMethodName);

        setPrivateField("currentSeasonTexture", seasonTexture);
        updateImageDrawable(seasonDisplayImage, seasonTexture);
    }

    private String buildWeatherMethodName(Weather weather) {
        String weatherName = weather.toString().toLowerCase();
        if (weatherName.equals("stormy")) {
            return "getStormyTexture";
        }
        return "get" + capitalizeFirst(weatherName) + "Texture";
    }

    private String buildSeasonMethodName(Seasons season) {
        String seasonName = season.toString().toLowerCase();

        if ("autumn".equals(seasonName)) {
            seasonName = "fall";
        }

        return "get" + capitalizeFirst(seasonName) + "Texture";
    }

    private Texture getTextureUsingReflection(String methodName) throws Exception {
        AssetManager assetManager = AssetManager.getAssetManager();
        Method textureMethod = AssetManager.class.getMethod(methodName);
        return (Texture) textureMethod.invoke(assetManager);
    }

    private void setPrivateField(String fieldName, Object value) throws Exception {
        Field field = this.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(this, value);
    }

    private void updateImageDrawable(Image image, Texture texture) {
        if (image != null && texture != null) {
            image.setDrawable(new Image(texture).getDrawable());
        }
    }

    private String capitalizeFirst(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    private String getCurrentSeason() {
        try {
            return App.getGame().getDate().getSeason().toString().toLowerCase();
        } catch (Exception e) {
            Gdx.app.error("GameView", "Failed to get season, using spring as default");
            return "spring";
        }
    }

    private Date getCurrentGameDate() {
        // In multiplayer mode, use the game instance from the game session
        if (game != null && game.isMultiplayer) {
            return game.getDate();
        } else {
            // In single player mode, use App.getGame()
            return App.getGame() != null ? App.getGame().getDate() : null;
        }
    }

    private void initializeTables() {
        mainTable = new Table();
        pauseTable = new Table();
        pauseButton = new TextButton("Pause", skin);
        resumeButton = new TextButton("Resume", skin);
    }

    private void initializeFriendsButton() {
        friendsButton = new TextButton("Friends", skin);
        friendsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                openFriendsWindow();
            }
        });
    }

    private void openFriendsWindow() {
        try {
            if (friendsWindow == null) {
                friendsWindow = new FriendsWindow(player, skin, this);
            }
            Main.getGame().setScreen(friendsWindow);
        } catch (Exception e) {
            System.err.println("Error opening friends window: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Getters
    public Player getPlayer() {
        return player;
    }

    public Game getGame() {
        return game;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public TextButton getPauseButton() {
        return pauseButton;
    }

    public Table getMainTable() {
        return mainTable;
    }

    public Table getPauseTable() {
        return pauseTable;
    }

    public TextButton getResumeButton() {
        return resumeButton;
    }

    public float getGameTime() {
        return gameTime;
    }

    public Label getTimeLabel() {
        return timeLabel;
    }

    public User getUser() {
        return user;
    }

    public Image getClockBackgroundImage() {
        return clockBackgroundImage;
    }

    public Image getWeatherDisplayImage() {
        return weatherDisplayImage;
    }

    public Image getSeasonDisplayImage() {
        return seasonDisplayImage;
    }

    public Lighting getLighting() {
        return lighting;
    }

    public Color getCurrentLightColor() {
        return currentLightColor.cpy();
    }

    public Label getLightingDescriptionLabel() {
        return lightingDescriptionLabel;
    }

    public ClimateSystem getClimateSystem() {
        return climateSystem;
    }

    public LightningSystem getLightningSystem() {
        return lightningSystem;
    }

    public Stage getStage() {
        return stage;
    }

    @Override
    public boolean keyDown(int keycode) {
        // Debug: Log all key presses to help troubleshoot F4 issue
        if (keycode == Input.Keys.M) {
            Main.getGame().setScreen(new MapScreen(player, skin, this));
            return true;
        }
        if (keycode == Input.Keys.F) {
            toggleMinimap();
            return true;
        }

        if (keycode == Input.Keys.P) {
            handlePetClosestAnimal();
            return true;
        }

        if (keycode == Input.Keys.ESCAPE) {
            // Show InventoryScreen and pass this as previousScreen
            Main.getGame().setScreen(new InventoryScreen(player, skin, this));
            return true;
        }
        if (keycode == Input.Keys.B) {
            Main.getGame().setScreen(new CraftingScreen(player, skin, this));
            return true;
        }
        if (keycode == Input.Keys.C) {
            Main.getGame().setScreen(new CookingScreen(player, skin, this));
            return true;
        }
        if (keycode == Input.Keys.L) {
            if (lightningSystem != null) {
                lightningSystem.triggerLightning();
            }
            return true;
        }
        if (keycode == Input.Keys.GRAVE) {
            if (terminalWindow != null) {
                terminalWindow.toggle();
            }
            return true;
        }
        if (keycode == Input.Keys.F4 || keycode == Input.Keys.F12 || keycode == Input.Keys.P) {
            takeScreenshot();
            return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int i) {
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        if (button == Input.Buttons.RIGHT) {
            Vector3 worldCoords = camera.unproject(new Vector3(screenX, screenY, 0));
            Animal clickedAnimal = findAnimalAt(worldCoords.x / 60, worldCoords.y / 60);
            if (clickedAnimal != null) {
                showAnimalInteractionDialog(clickedAnimal);
                return true; // Consume the click event
            }

            Player currentPlayer = App.getGame().getCurrentPlayer();

            if(currentPlayer.getCurrentItem() != null) {
                if(currentPlayer.getCurrentItem() instanceof Seed seed){
                    float playerX = currentPlayer.getPosX();
                    float playerY = currentPlayer.getPosY();
                    float dx = worldCoords.x - playerX;
                    float dy = worldCoords.y - playerY;

                    lastToolMouseX = worldCoords.x;
                    lastToolMouseY = worldCoords.y;

                    double angle = Math.atan2(dy, dx);
                    String direction;
                    if (Math.abs(dx) > Math.abs(dy)) {
                        direction = dx > 0 ? "east" : "west";
                    } else {
                        direction = dy > 0 ? "north" : "south";
                    }
                    String[] args = new String[]{seed.getName() , direction};

                    Result result = controller.plant(args);
                    if(result.success()) {
                        currentPlayer.setCurrentItem(null);
                    }else{
                        System.out.println(result.message());
                    }
                }
                else if(currentPlayer.getCurrentItem() instanceof Food food) {
                    controller.eatFood(new String[]{food.getName()});
                }
                else{
                    float playerX = currentPlayer.getPosX();
                    float playerY = currentPlayer.getPosY();
                    float dx = worldCoords.x - playerX;
                    float dy = worldCoords.y - playerY;

                    lastToolMouseX = worldCoords.x;
                    lastToolMouseY = worldCoords.y;

                    double angle = Math.atan2(dy, dx);
                    String direction;
                    if (Math.abs(dx) > Math.abs(dy)) {
                        direction = dx > 0 ? "east" : "west";
                    } else {
                        direction = dy > 0 ? "north" : "south";
                    }
                    String[] args = new String[]{currentPlayer.getCurrentItem().getName() , direction};

                    Result result = controller.plant(args);
                    if(result.success()) {
                        currentPlayer.setCurrentItem(null);
                    }
                }
            }
        }

        if (button == Input.Buttons.LEFT) {
            Vector3 worldCoords = camera.unproject(new Vector3(screenX, screenY, 0));

            int tileX = (int) (worldCoords.x / 60);
            int tileY = (int) (worldCoords.y / 60);

            Player currentPlayer = App.getGame().getCurrentPlayer();
            if (currentPlayer == null) return false;

            // Check if player has a watering can equipped and is clicking on a lake
            Tool currentTool = currentPlayer.getCurrentTool();
            if (currentTool != null && currentTool.getType() == Tool.ToolType.WATERING_CAN) {
                if (currentPlayer.getCurrentFarm() != null && currentPlayer.getCurrentFarm().isInWater(tileX, tileY)) {
                    // Calculate distance between player and clicked tile
                    int playerTileX = (int) (currentPlayer.getPosX() / 60);
                    int playerTileY = (int) (currentPlayer.getPosY() / 60);
                    double distance = Math.sqrt(Math.pow(tileX - playerTileX, 2) + Math.pow(tileY - playerTileY, 2));

                    if (distance <= 2.0) {
                        // Fill the watering can
                        boolean filled = currentTool.fill();
                        if (filled) {
                            showWateringCanFilledNotification();
                        } else {
                            showWateringCanAlreadyFullNotification();
                        }
                        return true;
                    } else {
                        showWateringCanTooFarNotification();
                        return true;
                    }
                }
            }

            // Original fishing logic
            if (currentPlayer.getCurrentFarm() != null && currentPlayer.getCurrentFarm().isInWater(tileX, tileY)) {
                startFishingMiniGame();
                return true;
            }

            if (currentPlayer.getCurrentTool() != null) {
                float playerX = currentPlayer.getPosX();
                float playerY = currentPlayer.getPosY();
                float dx = worldCoords.x - playerX;
                float dy = worldCoords.y - playerY;

                lastToolMouseX = worldCoords.x;
                lastToolMouseY = worldCoords.y;

                double angle = Math.atan2(dy, dx);
                String direction;
                if (Math.abs(dx) > Math.abs(dy)) {
                    direction = dx > 0 ? "east" : "west";
                } else {
                    direction = dy > 0 ? "north" : "south";
                }
                currentPlayer.useTool(direction, game.getGameMap());
                if (controller != null && controller.getPlayerController() != null) {
                    controller.getPlayerController().triggerToolSwing(direction, worldCoords.x, worldCoords.y);
                }
                return true;
            }


        }
        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(float v, float v1) {
        return false;
    }

    private void toggleMinimap() {
        isMapVisible = !isMapVisible;
        if (isMapVisible) {
            showMinimap();
        } else {
            hideMinimap();
        }
    }

    private void showMinimap() {
        if (minimapGroup == null) {
            minimapGroup = createMinimapGroup();
        }
        if (!stage.getActors().contains(minimapGroup, true)) {
            stage.addActor(minimapGroup);
        }
        minimapGroup.setVisible(true);
    }

    private void hideMinimap() {
        if (minimapGroup != null) {
            minimapGroup.setVisible(false);
        }
    }

    private Group createMinimapGroup() {
        Group group = new Group();

        // Create a larger background for the minimap
        Table backgroundTable = new Table();
        backgroundTable.setBackground(skin.newDrawable("white", Color.BLACK));
        backgroundTable.setSize(600, 600);
        backgroundTable.setPosition(100, 100);

        // Add a title label
        Label titleLabel = new Label("World Map", skin);
        titleLabel.setPosition(350, 720);
        titleLabel.setColor(Color.WHITE);

        // Add close button
        TextButton closeButton = new TextButton("X", skin);
        closeButton.setSize(40, 40);
        closeButton.setPosition(650, 720);
        closeButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                toggleMinimap();
                return true;
            }
        });

        // Add components to group
        group.addActor(backgroundTable);
        group.addActor(titleLabel);
        group.addActor(closeButton);

        return group;
    }

    private void renderMinimapTiles(GameMap gameMap, float scale) {
        // Render farms
        for (int farmIndex = 0; farmIndex < 4; farmIndex++) {
            Farm farm = gameMap.getFarmByIndex(farmIndex);
            if (farm == null) continue;

            // Calculate farm position on minimap
            float farmX, farmY;
            switch (farmIndex) {
                case 0: // Top-Left
                    farmX = 120;
                    farmY = 120; // Above village
                    break;
                case 1: // Bottom-Left
                    farmX = 120;
                    farmY = 120 + 234 * scale; // Below village
                    break;
                case 2: // Top-Right
                    farmX = 120 + 78 * scale;
                    farmY = 120; // Above village
                    break;
                case 3: // Bottom-Right
                    farmX = 120 + 78 * scale;
                    farmY = 120 + 234 * scale; // Below village
                    break;
                default:
                    continue;
            }

            // Render each tile in the farm
            for (int x = 0; x < Farm.width; x++) {
                for (int y = 0; y < Farm.height; y++) {
                    Location loc = farm.getItem(x, y);
                    if (loc != null) {
                        renderMinimapTile(loc, farmX + x * scale, farmY + y * scale, scale);
                    }
                }
            }
        }

        // Render village (center, 78x156)
        Village village = gameMap.getVillage();
        if (village != null) {
            float villageX = 120 + 78 * scale; // Center horizontally
            float villageY = 120 + 156 * scale; // Center vertically

            // Render each tile in the village
            for (int x = 0; x < Village.width; x++) {
                for (int y = 0; y < Village.height; y++) {
                    Location loc = village.getTiles()[x][y];
                    if (loc != null) {
                        renderMinimapTile(loc, villageX + x * scale, villageY + y * scale, scale);
                    }
                }
            }
        }
    }

    private void renderMinimapTile(Location loc, float x, float y, float scale) {
        TileType tileType = loc.getTile();
        String currentSeason = getCurrentSeason();

        // Get the appropriate texture from AssetManager
        Texture tileTexture = AssetManager.getAssetManager().getTileTextureForType(tileType.toString().toLowerCase(), currentSeason);

        if (tileTexture != null) {
            Main.getBatch().setColor(Color.WHITE); // Use white color to preserve texture colors
            Main.getBatch().draw(tileTexture, x, y, scale, scale);
        } else {
            // Fallback to colored rectangle if texture not found
            Color tileColor = getTileColor(tileType);
            if (tileColor != null) {
                Main.getBatch().setColor(tileColor);
                Texture whiteTexture = new Texture("content/grass/spring.png");
                Main.getBatch().draw(whiteTexture, x, y, scale, scale);
                whiteTexture.dispose();
            }
        }

        // Reset color to white for next render
        Main.getBatch().setColor(Color.WHITE);
    }

    private Color getTileColor(TileType tileType) {
        switch (tileType) {
            case Dirt:
                return new Color(0.6f, 0.4f, 0.2f, 1f); // Brown
            case WATER:
                return new Color(0.2f, 0.4f, 0.8f, 1f); // Blue
            case STONE:
                return new Color(0.5f, 0.5f, 0.5f, 1f); // Gray
            case TREE:
                return new Color(0.4f, 0.3f, 0.2f, 1f); // Dark brown
            case VILLAGE:
                return new Color(0.7f, 0.5f, 0.3f, 1f); // Village brown
            case MARKET:
                return new Color(0.9f, 0.7f, 0.5f, 1f); // Market color
            case PATH:
                return new Color(0.8f, 0.6f, 0.4f, 1f); // Light brown
            case BUILDING:
                return new Color(0.8f, 0.6f, 0.4f, 1f); // Light brown
            case SAND:
                return new Color(0.9f, 0.8f, 0.6f, 1f); // Sand color
            case PLOWED:
                return new Color(0.5f, 0.3f, 0.1f, 1f); // Dark brown
            case CROP:
                return new Color(0.2f, 0.8f, 0.2f, 1f); // Green
            default:
                return new Color(0.3f, 0.3f, 0.3f, 1f); // Default gray
        }
    }

    private void renderMinimapLabels() {
        // Render farm labels
        String[] farmLabels = {"Farm 0", "Farm 1", "Farm 2", "Farm 3"};
        float scaleX = 500f / 234f;
        float scaleY = 500f / 312f;
        float scale = Math.min(scaleX, scaleY);

        for (int i = 0; i < 4; i++) {
            float labelX, labelY;
            switch (i) {
                case 0: // Bottom-Left
                    labelX = 140;
                    labelY = 140 + 234 * scale;
                    break;
                case 1: // Top-Left
                    labelX = 140;
                    labelY = 140;
                    break;
                case 2: // Top-Right
                    labelX = 140 + 78 * scale;
                    labelY = 140;
                    break;
                case 3: // Bottom-Right
                    labelX = 140 + 78 * scale;
                    labelY = 140 + 234 * scale;
                    break;
                default:
                    continue;
            }

            // Create and render label
            Label farmLabel = new Label(farmLabels[i], skin);
            farmLabel.setPosition(labelX, labelY);
            farmLabel.setColor(Color.WHITE);
            farmLabel.draw(Main.getBatch(), 1f);
        }

        // Render village label (center)
        Label villageLabel = new Label("Village", skin);
        villageLabel.setPosition(140 + 78 * scale, 140 + 156 * scale);
        villageLabel.setColor(Color.WHITE);
        villageLabel.draw(Main.getBatch(), 1f);
    }


    private void renderOtherPlayers() {
        Game game = App.getGame();
        if (game == null || game.getPlayers() == null) {
            return;
        }

        for (Player otherPlayer : game.getPlayers()) {
            if (otherPlayer != null && otherPlayer != player && otherPlayer.getUser() != null && otherPlayer.getIsInVillage() && player.getIsInVillage()) {
                renderPlayerSprite(otherPlayer);
            }
        }
    }

    private void renderPlayerSprite(Player player) {
        final int RENDER_W = 48;
        final int RENDER_H = 72;

        // Load individual sprite file for player
        Texture playerTexture;
        try {
            // Use the first frame of the down animation as the default sprite
            playerTexture = new Texture(Gdx.files.internal("sprites/player/down_1.png"));
        } catch (Exception e) {
            // Fallback to colored dot if sprite can't be loaded
            boolean isCurrentPlayer = (player == this.player);
            Main.getBatch().setColor(isCurrentPlayer ? Color.RED : Color.BLUE);
            Texture whiteTexture = new Texture("content/grass/spring.png");
            float dotSize = 30;
            Main.getBatch().draw(whiteTexture, player.getPosX() - dotSize/2, player.getPosY() - dotSize/2, dotSize, dotSize);
            whiteTexture.dispose();
            return;
        }

        // Determine if this is the current player
        boolean isCurrentPlayer = (player == this.player);

        // Set color based on whether it's the current player or not
        if (isCurrentPlayer) {
            Main.getBatch().setColor(Color.WHITE); // Current player gets normal colors
        } else {
            Main.getBatch().setColor(0.7f, 0.7f, 0.7f, 1f); // Other players get slightly dimmed
        }

        // Draw the player sprite
        Main.getBatch().draw(playerTexture, player.getPosX() - RENDER_W/2, player.getPosY() - RENDER_H/2, RENDER_W, RENDER_H);

        // Dispose the texture to prevent memory leaks
        playerTexture.dispose();

        // Reset batch color to white after drawing player sprite
        Main.getBatch().setColor(Color.WHITE);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);  // Stage first (UI elements)
        multiplexer.addProcessor(this);   // GameView second (world interactions)
        Gdx.input.setInputProcessor(multiplexer);

        mainTable.top().right();
        mainTable.setFillParent(true);
        mainTable.padTop(10).padRight(10);
        mainTable.add(clockStack).size(120, 120).row();
        stage.addActor(mainTable);

        // Add friends button to the stage (positioned in bottom-left corner)
        if (friendsButton != null) {
            Table friendsTable = new Table();
            friendsTable.setFillParent(true);
            friendsTable.bottom().left();
            friendsTable.add(friendsButton).width(100).height(40).pad(20);
            stage.addActor(friendsTable);
        }

        pauseTable.setFillParent(true);
        pauseTable.center();
        pauseTable.add(resumeButton).width(200).height(20).pad(10);
        pauseTable.setVisible(false);
        stage.addActor(pauseTable);
    }

    @Override
    public void render(float deltaTime) {
        // Update network client to process incoming messages
        NetworkClient.getInstance().update();

        // Clear screen with lighting-tinted background
        Color bgColor = currentLightColor.cpy();
        bgColor.mul(0.3f); // Darken for background
        ScreenUtils.clear(bgColor.r, bgColor.g, bgColor.b, 1);

        if (!pauseTable.isVisible()) {
            gameTime += deltaTime;
            updateLighting(deltaTime);
            updateClockDisplay();
            updateWeatherAndSeasonDisplays();

            // Update rain system
            Date currentDate = getCurrentGameDate();
            if (currentDate != null) {
                climateSystem.update(deltaTime, currentDate.getWeatherToday(), currentLightColor);

                // Update lightning system
                lightningSystem.update(deltaTime, currentDate.getWeatherToday());
            }
        }

        // Update NPC sprites
        if (npcSpriteController != null) {
            npcSpriteController.update(deltaTime);
        }

        if(animalsController != null) {
            animalsController.update(deltaTime);
        }

        // Update clock display
        updateClockDisplay();

        // Update money label
        updateMoneyLabel();

        // Update date and time labels
        Date gameDate = getCurrentGameDate();
        if (gameDate != null) {
            updateDateLabel(gameDate);
            updateTimeLabel(gameDate);
            updateClockNeedle(gameDate);
        }

        // Update label positions
        updateLabelPositions();

        // Apply lighting to UI elements
        applyLightingToUI();

        // Set batch projection matrix
        Main.getBatch().setProjectionMatrix(camera.combined);

        // Begin batch rendering
        Main.getBatch().begin();

        // Set batch color to current lighting for world objects
        Main.getBatch().setColor(currentLightColor);

        // Update and render world elements (controller handles world rendering)
        if (!pauseTable.isVisible()) {
            controller.update(); // This will render world elements while batch is active
        }



        renderNPCs(deltaTime);

        // Render player nicknames for all players
        if (controller != null && controller.getPlayerController() != null) {
            // Render nickname for current player
            controller.getPlayerController().renderNickname(Main.getBatch(), player, currentLightColor);

            // Render nicknames for other players
            for (Player otherPlayer : App.getGame().getPlayers()) {
                if (otherPlayer != player && otherPlayer.getUser() != null) {
                    controller.getPlayerController().renderNickname(Main.getBatch(), otherPlayer, currentLightColor);
                }
            }
        }

        if (game.isMultiplayer) {
            renderOtherPlayers();
        }

        if (getCurrentGameDate() != null) {
            climateSystem.render(Main.getBatch(), currentLightColor);
        }

        // Render lightning effects AFTER rain but BEFORE UI
        lightningSystem.render(Main.getBatch(), currentLightColor);

        if (animalsController != null) {
            animalsController.render(Main.getBatch(), currentLightColor);
        }


        Main.getBatch().end();

        Main.getBatch().setColor(Color.WHITE);

        // Render UI on top
        stage.act(Math.min(deltaTime, 1 / 30f));
        stage.draw();

        // Render vertical energy bars for all players
        renderVerticalEnergyBars();

        // Render terminal window if visible
        if (terminalWindow != null) {
            terminalWindow.render(deltaTime);
        }
    }

    @Override
    public void resize(int width, int height) {
        // Update stage viewport to fix clock positioning issues
        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }

        // Update camera dimensions
        camera.setToOrtho(false, width, height);

        // Resize terminal window
        if (terminalWindow != null) {
            terminalWindow.resize(width, height);
        }
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
        if (clockBackgroundTexture != null) clockBackgroundTexture.dispose();
        if (clockNeedleTexture != null) clockNeedleTexture.dispose();
        if (customFont != null) customFont.dispose();
        if (smallFont != null) smallFont.dispose();
        if (lightingOverlayTexture != null) lightingOverlayTexture.dispose();

        // Dispose rain system - NEW
        if (climateSystem != null) {
            climateSystem.dispose();
        }

        // Dispose lightning system - NEW
        if (lightningSystem != null) {
            lightningSystem.dispose();
        }

        // Dispose terminal window
        if (terminalWindow != null) {
            terminalWindow.dispose();
        }


        // Dispose NPC sprite controller
        if (npcSpriteController != null) {
            npcSpriteController.dispose();
        }

        if (animalsController != null) {
            animalsController.dispose();
        }
    }

    private void updateClockDisplay() {
        Date gameDate = getCurrentGameDate();
        if (gameDate == null) {
            return;
        }

        updateDateLabel(gameDate);
        updateTimeLabel(gameDate);
        updateClockNeedle(gameDate);
        updateLabelPositions();
        updateMoneyLabel();
    }

    private void updateMoneyLabel() {
        if (App.getGame() != null && App.getGame().getCurrentPlayer() != null) {
            int money = App.getGame().getCurrentPlayer().getMoney();
            moneyLabel.setText(money);
        }
    }


    private void updateDateLabel(Date gameDate) {
        String dayText = getDayOfWeekAbbreviation(gameDate) + ". " + gameDate.getDay();
        dateLabel.setText(dayText);
    }

    private void updateTimeLabel(Date gameDate) {
        int hour = gameDate.getHour();
        int minute = gameDate.getMinutes();
        int displayHour = (hour == 0) ? 12 : (hour > 12 ? hour - 12 : hour);
        String amPm = (hour >= 12) ? "pm" : "am";
        String timeText = String.format("%d:%02d %s", displayHour, minute, amPm);
        timeDisplayLabel.setText(timeText);
    }

    private void updateClockNeedle(Date gameDate) {
        int hour = gameDate.getHour();
        int minute = gameDate.getMinutes();
        float totalMinutes = hour * 60 + minute;

        float startTime = 9 * 60f;   // 9:00 AM in minutes
        float endTime = 22 * 60f;    // 10:00 PM in minutes

        float rotation;

        if (totalMinutes >= startTime && totalMinutes <= endTime) {
            float progress = (totalMinutes - startTime) / (endTime - startTime);
            rotation = -progress * 180f;
        } else {
            rotation = 0f;
        }

        clockNeedleImage.setRotation(rotation + 180f);
    }

    private void updateLabelPositions() {
        float clockX = clockBackgroundImage.getX();
        float clockY = clockBackgroundImage.getY();
        float clockSize = 120f;

        dateLabel.setPosition(
            clockX + clockSize / 2 - dateLabel.getWidth() / 2 + 16f,
            clockY + 95f
        );

        timeDisplayLabel.setPosition(
            clockX + clockSize / 2 - timeDisplayLabel.getWidth() / 2 + 17f,
            clockY + 49f
        );


        lightingDescriptionLabel.setPosition(
            clockX + clockSize / 2 - lightingDescriptionLabel.getWidth() / 2 + 17f,
            clockY + 5f
        );
    }

    private String getDayOfWeekAbbreviation(Date gameDate) {
        if (gameDate == null) return "Mon";

        int daysPerSeason = 28;
        int year = 1;
        int season = gameDate.getSeason().ordinal();
        int day = gameDate.getDay();

        int totalDays = ((year - 1) * 4 * daysPerSeason) + (season * daysPerSeason) + day - 1;
        int dayOfWeek = (totalDays % 7);

        String[] dayAbbreviations = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        return dayAbbreviations[dayOfWeek];
    }

    private void renderNPCs(float deltaTime) {
        if (npcSpriteController != null) {
            npcSpriteController.update(deltaTime);
            npcSpriteController.render(Main.getBatch(), currentLightColor);
        }
    }

    private void renderVerticalEnergyBars() {
        Game game = App.getGame();
        if (game == null || game.getCurrentPlayer() == null) return;

        // Only show energy bar for the current player (whose turn it is)
        Player currentPlayer = game.getCurrentPlayer();

        // Save current projection matrix
        Matrix4 originalProjection = Main.getBatch().getProjectionMatrix().cpy();

        // Set projection matrix to screen coordinates (orthographic projection)
        Main.getBatch().setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        Main.getBatch().begin();

        // Create a white texture for rendering
        Pixmap whitePixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        whitePixmap.setColor(Color.WHITE);
        whitePixmap.fill();
        Texture whiteTexture = new Texture(whitePixmap);
        whitePixmap.dispose();

        // Position bar in the bottom right corner of the screen
        float barX = Gdx.graphics.getWidth() - VERTICAL_ENERGY_BAR_WIDTH - 20;
        float barY = 20; // 20px from bottom

        // Calculate energy percentage (assuming max energy is 200)
        int currentEnergy = currentPlayer.getEnergy();
        float energyPercentage = Math.max(0, Math.min(1, currentEnergy / 200f));
        float barHeight = VERTICAL_ENERGY_BAR_HEIGHT * energyPercentage;

        // Draw background (empty bar)
        Main.getBatch().setColor(Color.DARK_GRAY);
        Main.getBatch().draw(whiteTexture, barX, barY, VERTICAL_ENERGY_BAR_WIDTH, VERTICAL_ENERGY_BAR_HEIGHT);

        // Draw filled portion from bottom up
        if (barHeight > 0) {
            // Color based on energy level
            if (energyPercentage > 0.6f) {
                Main.getBatch().setColor(Color.GREEN);
            } else if (energyPercentage > 0.3f) {
                Main.getBatch().setColor(Color.YELLOW);
            } else {
                Main.getBatch().setColor(Color.RED);
            }
            Main.getBatch().draw(whiteTexture, barX, barY, VERTICAL_ENERGY_BAR_WIDTH, barHeight);
        }

        // Draw border
        Main.getBatch().setColor(Color.WHITE);
        Main.getBatch().draw(whiteTexture, barX, barY, VERTICAL_ENERGY_BAR_WIDTH, 2); // Bottom border
        Main.getBatch().draw(whiteTexture, barX, barY + VERTICAL_ENERGY_BAR_HEIGHT - 2, VERTICAL_ENERGY_BAR_WIDTH, 2); // Top border
        Main.getBatch().draw(whiteTexture, barX, barY, 2, VERTICAL_ENERGY_BAR_HEIGHT); // Left border
        Main.getBatch().draw(whiteTexture, barX + VERTICAL_ENERGY_BAR_WIDTH - 2, barY, 2, VERTICAL_ENERGY_BAR_HEIGHT); // Right border

        // Draw player name below the bar
        String playerName = currentPlayer.getUser() != null ? currentPlayer.getUser().getUsername() : "Unknown";
        if (playerName.length() > 8) {
            playerName = playerName.substring(0, 8) + "...";
        }

        // Draw player name using smallFont if available
        if (smallFont != null) {
            smallFont.setColor(Color.CYAN); // Current player always cyan
            float nameX = barX - 5; // Center text under bar
            float nameY = barY - 15;
            smallFont.draw(Main.getBatch(), playerName, nameX, nameY);
        }

        // Reset color and end batch
        Main.getBatch().setColor(Color.WHITE);
        Main.getBatch().end();

        // Restore original projection matrix
        Main.getBatch().setProjectionMatrix(originalProjection);

        // Dispose of the white texture
        whiteTexture.dispose();
    }

    public Skin getSkin() {
        return skin;
    }

    public GameMenuController getController() {
        return controller;
    }

    private void startFishingMiniGame() {
        Player currentPlayer = App.getGame().getCurrentPlayer();
        if (currentPlayer == null) return;

        // Check if player has a fishing rod equipped
        Tool currentTool = currentPlayer.getCurrentTool();
        if (currentTool == null || currentTool.getType() != Tool.ToolType.FISHING_ROD) {
            // Show a notification that fishing rod is required
            showFishingRodRequiredNotification();
            return;
        }

        String poleName = currentTool.getName(); // Use the equipped fishing rod

        FishingMiniGame fishingMiniGame = new FishingMiniGame(this, poleName);

        Main.getGame().setScreen(fishingMiniGame);
    }

    private void showFishingRodRequiredNotification() {
        Label notificationLabel = new Label("You need a fishing rod equipped to fish!", skin);
        notificationLabel.setColor(Color.RED);
        notificationLabel.setPosition(Gdx.graphics.getWidth() / 2 - 150, Gdx.graphics.getHeight() - 100);
        notificationLabel.setFontScale(1.5f);

        // Add the notification to the stage
        stage.addActor(notificationLabel);

        // Schedule removal after 3 seconds using a timer
        com.badlogic.gdx.utils.Timer.schedule(new com.badlogic.gdx.utils.Timer.Task() {
            @Override
            public void run() {
                Gdx.app.postRunnable(() -> {
                    if (notificationLabel.getStage() != null) {
                        notificationLabel.remove();
                    }
                });
            }
        }, 3.0f);
    }

    private void showWateringCanFilledNotification() {
        Label notificationLabel = new Label("Watering can filled!", skin);
        notificationLabel.setColor(Color.GREEN);
        notificationLabel.setPosition(Gdx.graphics.getWidth() / 2 - 100, Gdx.graphics.getHeight() - 100);
        notificationLabel.setFontScale(1.5f);

        stage.addActor(notificationLabel);

        com.badlogic.gdx.utils.Timer.schedule(new com.badlogic.gdx.utils.Timer.Task() {
            @Override
            public void run() {
                Gdx.app.postRunnable(() -> {
                    if (notificationLabel.getStage() != null) {
                        notificationLabel.remove();
                    }
                });
            }
        }, 2.0f);
    }

    private void showWateringCanAlreadyFullNotification() {
        Label notificationLabel = new Label("Watering can is already full!", skin);
        notificationLabel.setColor(Color.YELLOW);
        notificationLabel.setPosition(Gdx.graphics.getWidth() / 2 - 120, Gdx.graphics.getHeight() - 100);
        notificationLabel.setFontScale(1.5f);

        stage.addActor(notificationLabel);

        com.badlogic.gdx.utils.Timer.schedule(new com.badlogic.gdx.utils.Timer.Task() {
            @Override
            public void run() {
                Gdx.app.postRunnable(() -> {
                    if (notificationLabel.getStage() != null) {
                        notificationLabel.remove();
                    }
                });
            }
        }, 2.0f);
    }

    private void showWateringCanTooFarNotification() {
        Label notificationLabel = new Label("Too far from lake! Move closer.", skin);
        notificationLabel.setColor(Color.ORANGE);
        notificationLabel.setPosition(Gdx.graphics.getWidth() / 2 - 120, Gdx.graphics.getHeight() - 100);
        notificationLabel.setFontScale(1.5f);

        // Add the notification to the stage
        stage.addActor(notificationLabel);

        // Schedule removal after 2 seconds using a timer
        com.badlogic.gdx.utils.Timer.schedule(new com.badlogic.gdx.utils.Timer.Task() {
            @Override
            public void run() {
                Gdx.app.postRunnable(() -> {
                    if (notificationLabel.getStage() != null) {
                        notificationLabel.remove();
                    }
                });
            }
        }, 2.0f);
    }

    private void scheduleNotificationRemoval(Label notificationLabel, float delaySeconds) {
        com.badlogic.gdx.utils.Timer.schedule(new com.badlogic.gdx.utils.Timer.Task() {
            @Override
            public void run() {
                Gdx.app.postRunnable(() -> {
                    if (notificationLabel.getStage() != null) {
                        notificationLabel.remove();
                    }
                });
            }
        }, delaySeconds);
    }

    private void createLightingOverlayTexture() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1f, 1f, 1f, 1f); // Pure white
        pixmap.fill();
        lightingOverlayTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    public void takeScreenshot() {
        try {
            // Capture the current screen
            Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            // Create a timestamp for the filename
            String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
            String filename = "screenshot_" + timestamp + ".png";

            // Save the screenshot to the external storage directory
            String directory = System.getProperty("user.home") + "/Desktop/";
            String filepath = directory + filename;

            // Create the directory if it doesn't exist
            java.io.File dir = new java.io.File(directory);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Save the pixmap to a PNG file
            // Use absolute path for better cross-platform compatibility
            com.badlogic.gdx.files.FileHandle fileHandle = Gdx.files.absolute(filepath);
            com.badlogic.gdx.graphics.PixmapIO.writePNG(fileHandle, pixmap);

            // Dispose the pixmap to free memory
            pixmap.dispose();

            System.out.println("Screenshot saved: " + filepath);

            // Show a temporary notification to the user
            showScreenshotNotification();

        } catch (Exception e) {
            System.err.println("Failed to take screenshot: " + e.getMessage());
            e.printStackTrace();
        }
    }

    void showScreenshotNotification() {
        Label notificationLabel = new Label("Screenshot taken!", skin);
        notificationLabel.setColor(Color.GREEN);
        notificationLabel.setPosition(Gdx.graphics.getWidth() / 2 - 100, Gdx.graphics.getHeight() - 100);
        notificationLabel.setFontScale(1.5f);

        // Add the notification to the stage
        stage.addActor(notificationLabel);

        // Schedule removal after 2 seconds
        com.badlogic.gdx.utils.Timer.schedule(new com.badlogic.gdx.utils.Timer.Task() {
            @Override
            public void run() {
                Gdx.app.postRunnable(() -> {
                    try {
                        Thread.sleep(2000);
                        Gdx.app.postRunnable(() -> {
                            if (notificationLabel.getStage() != null) {
                                notificationLabel.remove();
                            }
                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        }, 2.0f);
    }

    private Animal findAnimalAt(float worldX, float worldY) {
        Farm currentFarm = player.getCurrentFarm();
        if (currentFarm == null) return null;

        float animalWidth = 48; // Approximate render width
        float animalHeight = 96; // Approximate render height

        // Check Barn Animals
        for (Barn barn : currentFarm.getBarns()) {
            for (BarnAnimal animal : barn.getAnimals()) {
                Rectangle animalBounds = new Rectangle(animal.getPosX(), animal.getPosY(), animalWidth, animalHeight);
                if (animalBounds.contains(worldX, worldY)) {
                    return animal;
                }
            }
        }

        // Check Coop Animals
        for (Coop coop : currentFarm.getCoops()) {
            for (CoopAnimal animal : coop.getAnimals()) {
                Rectangle animalBounds = new Rectangle(animal.getPosX(), animal.getPosY(), animalWidth, animalHeight);
                if (animalBounds.contains(worldX, worldY)) {
                    return animal;
                }
            }
        }
        return null;
    }

    private void showAnimalInteractionDialog(Animal animal) {
        AnimalController animalController = new AnimalController();
        AnimalInteractionDialog dialog = new AnimalInteractionDialog(
            animal.getName(),
            skin,
            animal,
            animalController,
            this::showResultNotification // Pass a method reference to handle the result
        );
        dialog.show(stage);
    }

    private void handlePetClosestAnimal() {
        Farm currentFarm = player.getCurrentFarm();
        if (currentFarm == null) return;

        Animal closestAnimal = null;
        float minDistance = Float.MAX_VALUE;

        // Combine all animals into one list
        List<Animal> allAnimals = new ArrayList<>();
        currentFarm.getBarns().forEach(barn -> allAnimals.addAll(barn.getAnimals()));
        currentFarm.getCoops().forEach(coop -> allAnimals.addAll(coop.getAnimals()));

        // Find the closest animal
        for (Animal animal : allAnimals) {
            float distance = (float) Math.sqrt(Math.pow(player.getPosX() - animal.getPosX(), 2) + Math.pow(player.getPosY() - animal.getPosY(), 2));
            if (distance < minDistance) {
                minDistance = distance;
                closestAnimal = animal;
            }
        }

        // If an animal is found within 2 tiles (120 pixels)
        if (closestAnimal != null && minDistance < 120) {
            AnimalController animalController = new AnimalController();
            Result result = animalController.petAnimal(new String[]{closestAnimal.getName()});
            showResultNotification(result);
        } else {
            showResultNotification(Result.error("No animal is close enough to pet."));
        }
    }

    private void showResultNotification(Result result) {
        Color color = result.success() ? Color.GREEN : Color.RED;
        Label notificationLabel = new Label(result.message(), skin);
        notificationLabel.setColor(color);
        notificationLabel.setPosition(Gdx.graphics.getWidth() / 2f - notificationLabel.getWidth() / 2f, Gdx.graphics.getHeight() - 100);
        stage.addActor(notificationLabel);

        // Schedule removal after 3 seconds
        com.badlogic.gdx.utils.Timer.schedule(new com.badlogic.gdx.utils.Timer.Task() {
            @Override
            public void run() {
                Gdx.app.postRunnable(() -> {
                    if (notificationLabel.getStage() != null) {
                        notificationLabel.remove();
                    }
                });
            }
        }, 3.0f);
    }

    public void setBuildingPlacementListener(Runnable listener) {
        this.buildingPlacementListener = listener;
    }

}
