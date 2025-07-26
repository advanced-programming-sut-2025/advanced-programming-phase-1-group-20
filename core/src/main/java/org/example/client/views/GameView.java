package org.example.client.views;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.client.Main;
import org.example.client.controllers.GameMenuController;
import org.example.client.views.gameplay.CraftingScreen;
import org.example.client.views.gameplay.InventoryScreen;
import org.example.common.models.App;
import org.example.common.models.Items.Item;
import org.example.common.models.Items.Tool;
import org.example.common.models.MapDetails.Farm;
import org.example.common.models.MapDetails.GameMap;
import org.example.common.models.MapDetails.Village;
import org.example.common.models.common.Location;
import org.example.common.models.Player.Player;
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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.Pixmap;

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
    private float zoomedOutZoom = 3.0f; // Zoom out to show entire map (larger value = more zoomed out)

    // NPC rendering
    private NPCSpriteController npcSpriteController;

    // Add these fields to GameView:
    private float lastToolMouseX = 0;
    private float lastToolMouseY = 0;

    // Energy bar components
    private Table energyBarTable;
    private int lastKnownEnergy = -1;
    private static final int ENERGY_BAR_WIDTH = 120;
    private static final int ENERGY_BAR_HEIGHT = 15;

    public GameView(GameMenuController controller, Player player, Game game, Skin skin, User user) {
        this.controller = controller;
        this.player = player;
        this.game = game;
        this.skin = skin;
        this.user = user;
        this.gameTime = 0;
        this.lightingUpdateTimer = 0;

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

        // Initialize NPC sprite controller
        npcSpriteController = new NPCSpriteController();

        loadCustomFont();
        initializeLabels();
        initializeClock();
        createEnergyBar();
        updateWeatherAndSeasonDisplays();

        camera = new OrthographicCamera(120, 120);
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Set camera for climate system
        climateSystem.setCamera(camera);
        lightningSystem.setCamera(camera);

        initializeTables();
        controller.setView(this);
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
            (clockBackgroundImage.getWidth() - clockNeedleImage.getWidth()) / 2 - 20f,
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
        weatherDisplayImage.setPosition(centerX , centerY + 19.65f);

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

    private void createEnergyBar() {
        // Create table to hold energy bar components
        energyBarTable = new Table();
        // We'll render the energy bar manually in the render method
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

    private Date getCurrentGameDate() {
        return game != null ? game.getDate() : App.getGame().getDate();
    }

    private void initializeTables() {
        mainTable = new Table();
        pauseTable = new Table();
        pauseButton = new TextButton("Pause", skin);
        resumeButton = new TextButton("Resume", skin);
    }

    // Getters
    public Player getPlayer() { return player; }
    public Game getGame() { return game; }
    public OrthographicCamera getCamera() { return camera; }
    public TextButton getPauseButton() { return pauseButton; }
    public Table getMainTable() { return mainTable; }
    public Table getPauseTable() { return pauseTable; }
    public TextButton getResumeButton() { return resumeButton; }
    public float getGameTime() { return gameTime; }
    public Label getTimeLabel() { return timeLabel; }
    public User getUser() { return user; }
    public Image getClockBackgroundImage() { return clockBackgroundImage; }
    public Image getWeatherDisplayImage() { return weatherDisplayImage; }
    public Image getSeasonDisplayImage() { return seasonDisplayImage; }
    public Lighting getLighting() { return lighting; }
    public Color getCurrentLightColor() { return currentLightColor.cpy(); }
    public Label getLightingDescriptionLabel() { return lightingDescriptionLabel; }
    public ClimateSystem getClimateSystem() { return climateSystem; }
    public LightningSystem getLightningSystem() { return lightningSystem; }


    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.M) {
            toggleCameraZoom();
            return true;
        }

        if (keycode == Input.Keys.ESCAPE) {
            // Show InventoryScreen and pass this as previousScreen
            Main.getGame().setScreen(new InventoryScreen(player, skin, this));
            return true;
        }
        if(keycode == Input.Keys.B){
            Main.getGame().setScreen(new CraftingScreen(player, skin, this));
            return true;
        }
        if(keycode == Input.Keys.L){
            if (lightningSystem != null) {
                lightningSystem.triggerLightning();
                System.out.println("⚡ Lightning triggered manually!");
            }
            return true;
        }
        if(keycode == Input.Keys.GRAVE){
            if (terminalWindow != null) {
                terminalWindow.toggle();
            }
            return true;
        }
        return false;
    }
    @Override
    public boolean keyUp(int i) { return false; }
    @Override
    public boolean keyTyped(char c) { return false; }
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            Vector3 worldCoords = camera.unproject(new Vector3(screenX, screenY, 0));

            int tileX = (int) (worldCoords.x / 60);
            int tileY = (int) (worldCoords.y / 60);

            if (player.getCurrentFarm() != null && player.getCurrentFarm().isInWater(tileX, tileY)) {
                startFishingMiniGame();
                return true;
            }

            if (player.getCurrentTool() != null) {
                float playerX = player.getPosX();
                float playerY = player.getPosY();
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
                player.useTool(direction, game.getGameMap());
                if (controller != null && controller.getPlayerController() != null) {
                    controller.getPlayerController().triggerToolSwing(direction, worldCoords.x, worldCoords.y);
                }
                return true;
            }
        }
        return false;
    }
    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) { return false; }
    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) { return false; }
    @Override
    public boolean touchDragged(int i, int i1, int i2) { return false; }
    @Override
    public boolean mouseMoved(int i, int i1) { return false; }
    @Override
    public boolean scrolled(float v, float v1) { return false; }

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

    private void renderMinimap() {
        if (!isMapVisible || minimapGroup == null) return;

        // Get the game map
        GameMap gameMap = App.getGame().getGameMap();
        if (gameMap == null) return;

        // Calculate map scale (312x468 world map to 500x500 display)
        float scaleX = 500f / 312f;
        float scaleY = 500f / 468f;
        float scale = Math.min(scaleX, scaleY); // Use the smaller scale to fit everything

        // Start rendering minimap
        Main.getBatch().begin();

        // Render the actual map tiles
        renderMinimapTiles(gameMap, scale);

        // Render player position
        Player currentPlayer = App.getGame().getCurrentPlayer();
        if (currentPlayer != null) {
            float playerX = currentPlayer.getPosX() / 60f * scale + 120;
            float playerY = currentPlayer.getPosY() / 60f * scale + 120;

            // Draw player as a red dot
            Main.getBatch().setColor(Color.RED);
            Texture whiteTexture = new Texture("content/grass/spring.png");
            Main.getBatch().draw(whiteTexture, playerX - 3, playerY - 3, 6, 6);
            whiteTexture.dispose();
        }

        // Render other players
        for (Player otherPlayer : gameMap.getPlayers()) {
            if (otherPlayer != currentPlayer) {
                float otherX = otherPlayer.getPosX() / 60f * scale + 120;
                float otherY = otherPlayer.getPosY() / 60f * scale + 120;

                // Draw other players as blue dots
                Main.getBatch().setColor(Color.BLUE);
                Texture whiteTexture = new Texture("content/grass/spring.png");
                Main.getBatch().draw(whiteTexture, otherX - 3, otherY - 3, 6, 6);
                whiteTexture.dispose();
            }
        }

        // Render labels
        renderMinimapLabels();

        // Reset color and end batch
        Main.getBatch().setColor(Color.WHITE);
        Main.getBatch().end();
    }

    private void renderMinimapTiles(GameMap gameMap, float scale) {
        // Render farms
        for (int farmIndex = 0; farmIndex < 4; farmIndex++) {
            Farm farm = gameMap.getFarmByIndex(farmIndex);
            if (farm == null) continue;

            // Calculate farm position on minimap
            float farmX, farmY;
            switch (farmIndex) {
                case 0: // Bottom-Left
                    farmX = 120;
                    farmY = 120 + 312 * scale; // Below village
                    break;
                case 1: // Top-Left
                    farmX = 120;
                    farmY = 120; // Above village
                    break;
                case 2: // Top-Right
                    farmX = 120 + 156 * scale;
                    farmY = 120; // Above village
                    break;
                case 3: // Bottom-Right
                    farmX = 120 + 156 * scale;
                    farmY = 120 + 312 * scale; // Below village
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

        // Render village (center, 156x312)
        Village village = gameMap.getVillage();
        if (village != null) {
            float villageX = 120 + 78 * scale; // Center horizontally
            float villageY = 120 + 78 * scale; // Center vertically

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
        Color tileColor = getTileColor(tileType);

        if (tileColor != null) {
            Main.getBatch().setColor(tileColor);
            Texture whiteTexture = new Texture("content/grass/spring.png");
            Main.getBatch().draw(whiteTexture, x, y, scale, scale);
            whiteTexture.dispose();
        }
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
        float scaleX = 500f / 312f;
        float scaleY = 500f / 468f;
        float scale = Math.min(scaleX, scaleY);

        for (int i = 0; i < 4; i++) {
            float labelX, labelY;
            switch (i) {
                case 0: // Bottom-Left
                    labelX = 140;
                    labelY = 140 + 312 * scale;
                    break;
                case 1: // Top-Left
                    labelX = 140;
                    labelY = 140;
                    break;
                case 2: // Top-Right
                    labelX = 140 + 156 * scale;
                    labelY = 140;
                    break;
                case 3: // Bottom-Right
                    labelX = 140 + 156 * scale;
                    labelY = 140 + 312 * scale;
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

    private void renderMinimapLegend(Texture whiteTexture) {
        float legendX = 120;
        float legendY = 650;

        // Legend title
        Label legendTitle = new Label("Legend:", skin);
        legendTitle.setPosition(legendX, legendY);
        legendTitle.setColor(Color.WHITE);
        legendTitle.draw(Main.getBatch(), 1f);

        // Player indicator
        Main.getBatch().setColor(Color.RED);
        Main.getBatch().draw(whiteTexture, legendX, legendY - 20, 4, 4);
        Main.getBatch().setColor(Color.WHITE);

        Label playerLabel = new Label("You", skin);
        playerLabel.setPosition(legendX + 10, legendY - 20);
        playerLabel.setColor(Color.WHITE);
        playerLabel.draw(Main.getBatch(), 1f);

        // Other players indicator
        Main.getBatch().setColor(Color.BLUE);
        Main.getBatch().draw(whiteTexture, legendX, legendY - 40, 4, 4);
        Main.getBatch().setColor(Color.WHITE);

        Label othersLabel = new Label("Other Players", skin);
        othersLabel.setPosition(legendX + 10, legendY - 40);
        othersLabel.setColor(Color.WHITE);
        othersLabel.draw(Main.getBatch(), 1f);

        // Farm indicator
        Main.getBatch().setColor(0.2f, 0.8f, 0.2f, 1f);
        Main.getBatch().draw(whiteTexture, legendX, legendY - 60, 8, 8);
        Main.getBatch().setColor(Color.WHITE);

        Label farmLabel = new Label("Farms", skin);
        farmLabel.setPosition(legendX + 10, legendY - 60);
        farmLabel.setColor(Color.WHITE);
        farmLabel.draw(Main.getBatch(), 1f);

        // Village indicator
        Main.getBatch().setColor(0.8f, 0.6f, 0.4f, 1f);
        Main.getBatch().draw(whiteTexture, legendX, legendY - 80, 8, 8);
        Main.getBatch().setColor(Color.WHITE);

        Label villageLegendLabel = new Label("Village", skin);
        villageLegendLabel.setPosition(legendX + 10, legendY - 80);
        villageLegendLabel.setColor(Color.WHITE);
        villageLegendLabel.draw(Main.getBatch(), 1f);

        // Path indicator
        Main.getBatch().setColor(0.8f, 0.6f, 0.4f, 1f);
        Main.getBatch().draw(whiteTexture, legendX, legendY - 100, 8, 8);
        Main.getBatch().setColor(Color.WHITE);

        Label pathLegendLabel = new Label("Paths", skin);
        pathLegendLabel.setPosition(legendX + 10, legendY - 100);
        pathLegendLabel.setColor(Color.WHITE);
        pathLegendLabel.draw(Main.getBatch(), 1f);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(this);   // GameView first
        multiplexer.addProcessor(stage);  // Stage second
        Gdx.input.setInputProcessor(multiplexer);

        mainTable.top().right();
        mainTable.setFillParent(true);
        mainTable.padTop(10).padRight(10);
        mainTable.add(clockStack).size(120, 120).row();
        mainTable.add(energyBarTable).padTop(10);
        stage.addActor(mainTable);

        pauseTable.setFillParent(true);
        pauseTable.center();
        pauseTable.add(resumeButton).width(200).height(20).pad(10);
        pauseTable.setVisible(false);
        stage.addActor(pauseTable);
    }

    @Override
    public void render(float deltaTime) {
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

        Main.getBatch().begin();

        // Set batch color to current lighting for world objects
        Main.getBatch().setColor(currentLightColor);

        // Update and render world elements (controller handles world rendering)
        if (!pauseTable.isVisible()) {
            controller.update(); // This will render world elements while batch is active
        }

        renderNPCs(deltaTime);

        // Render player nicknames using PlayerController
        if (controller != null && controller.getPlayerController() != null) {
            controller.getPlayerController().renderNickname(Main.getBatch(), player, currentLightColor);
        }

        if (getCurrentGameDate() != null) {
            climateSystem.render(Main.getBatch(), currentLightColor);
        }

        // Render lightning effects AFTER rain but BEFORE UI
        lightningSystem.render(Main.getBatch(), currentLightColor);

        // Render full-screen lighting overlay for proper coverage
        renderLightingOverlay();

        Main.getBatch().end();

        Main.getBatch().setColor(Color.WHITE);

        // Render UI on top
        stage.act(Math.min(deltaTime, 1 / 30f));
        stage.draw();

        // Render energy bar manually
        renderEnergyBar();

        // Render minimap if visible
        renderMinimap();

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
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}

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
    }

    private void updateClockDisplay() {
        Date gameDate = getCurrentGameDate();
        if (gameDate == null) return;

        updateDateLabel(gameDate);
        updateTimeLabel(gameDate);
        updateClockNeedle(gameDate);
        updateLabelPositions();
        updateMoneyLabel();

        updateEnergyBar();
    }

    private void updateMoneyLabel() {
        if (App.getGame() != null && App.getGame().getCurrentPlayer() != null) {
            int money = App.getGame().getCurrentPlayer().getMoney();
            moneyLabel.setText(money);
        }
    }



    private void updateEnergyBar() {
        int currentEnergy = player.getEnergy();
        if (currentEnergy != lastKnownEnergy) {
            lastKnownEnergy = currentEnergy;
        }
    }

    private void updateDateLabel(Date gameDate) {
        String dayText = getDayOfWeekAbbreviation(gameDate) + ". " + gameDate.getDay();
        dateLabel.setText(dayText);
    }

    private void updateTimeLabel(Date gameDate) {
        int hour = gameDate.getHour();
        int displayHour = (hour == 0) ? 12 : (hour > 12 ? hour - 12 : hour);
        String amPm = (hour >= 12) ? "pm" : "am";
        String timeText = String.format("%d:%02d %s", displayHour, gameDate.getMinutes(), amPm);
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
            clockX + clockSize/2 - dateLabel.getWidth()/2 + 16f,
            clockY + 95f
        );

        timeDisplayLabel.setPosition(
            clockX + clockSize/2 - timeDisplayLabel.getWidth()/2 + 17f,
            clockY + 49f
        );



        lightingDescriptionLabel.setPosition(
            clockX + clockSize/2 - lightingDescriptionLabel.getWidth()/2 + 17f,
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
        if (player.getIsInVillage()){
            if (npcSpriteController != null) {
                npcSpriteController.update(deltaTime);
                npcSpriteController.render(Main.getBatch(), currentLightColor);
            }
        }
    }

    private void renderEnergyBar() {
        if (energyBarTable == null) return;

        // Get the position of the energy bar table - position it in the top-left corner
        float x = 20;
        float y = Gdx.graphics.getHeight() - ENERGY_BAR_HEIGHT - 20;

        // Calculate energy percentage
        int currentEnergy = player.getEnergy();
        float energyPercentage = Math.max(0, Math.min(1, currentEnergy / 200f));
        float barWidth = ENERGY_BAR_WIDTH * energyPercentage;

        // Begin batch for energy bar rendering
        Main.getBatch().begin();

        // Draw background (empty bar)
        Main.getBatch().setColor(Color.DARK_GRAY);
        Main.getBatch().draw(skin.getRegion("white"), x, y, ENERGY_BAR_WIDTH, ENERGY_BAR_HEIGHT);

        // Draw filled portion - always green
        if (barWidth > 0) {
            Main.getBatch().setColor(Color.GREEN);
            Main.getBatch().draw(skin.getRegion("white"), x, y, barWidth, ENERGY_BAR_HEIGHT);
        }

        // Draw border
        Main.getBatch().setColor(Color.WHITE);
        Main.getBatch().draw(skin.getRegion("white"), x, y, ENERGY_BAR_WIDTH, 1); // Top border
        Main.getBatch().draw(skin.getRegion("white"), x, y + ENERGY_BAR_HEIGHT - 1, ENERGY_BAR_WIDTH, 1); // Bottom border
        Main.getBatch().draw(skin.getRegion("white"), x, y, 1, ENERGY_BAR_HEIGHT); // Left border
        Main.getBatch().draw(skin.getRegion("white"), x + ENERGY_BAR_WIDTH - 1, y, 1, ENERGY_BAR_HEIGHT); // Right border

        // Reset color
        Main.getBatch().setColor(Color.WHITE);
        Main.getBatch().end();
    }

    public Skin getSkin() {
        return skin;
    }

    private void startFishingMiniGame() {
        String poleName = "training rod"; // Default pole name

        for (Item item : player.getBackpack().getInventory().keySet()) {
            if (item instanceof Tool tool) {
                if (tool.getType() == Tool.ToolType.FISHING_ROD) {
                    poleName = tool.getName(); // Use the actual tool name
                    break;
                }
            }
        }

        FishingMiniGame fishingMiniGame = new FishingMiniGame(this, poleName);

        Main.getGame().setScreen(fishingMiniGame);
    }

    private void renderLightingOverlay() {
        if (lightingOverlayTexture == null) return;

        // Create a lighting color that blends with the current lighting
        Color overlayColor = new Color(currentLightColor);

        // Adjust alpha based on lighting intensity - more subtle effect
        float lightingIntensity = lighting.getLightIntensity();
        overlayColor.a = (1.0f - lightingIntensity) * 0.3f; // Subtle darkening effect

        // Set batch color for the overlay
        Main.getBatch().setColor(overlayColor);

        // Draw the full-screen overlay
        Main.getBatch().draw(lightingOverlayTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Reset batch color
        Main.getBatch().setColor(Color.WHITE);
    }

    private void createLightingOverlayTexture() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1f, 1f, 1f, 1f); // Pure white
        pixmap.fill();
        lightingOverlayTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    private void toggleCameraZoom() {
        isCameraZoomedOut = !isCameraZoomedOut;
        if (isCameraZoomedOut) {
            // Zoom out to show entire game map
            camera.zoom = zoomedOutZoom;
            // Center camera on the entire game map center
            float totalMapWidth = 312 * 60; // GameMap.TOTAL_WIDTH * TILE_SIZE
            float totalMapHeight = 468 * 60; // GameMap.TOTAL_HEIGHT * TILE_SIZE
            camera.position.set(totalMapWidth / 2, totalMapHeight / 2, 0);
        } else {
            // Return to normal zoom
            camera.zoom = normalZoom;
        }
        camera.update();
    }
}
