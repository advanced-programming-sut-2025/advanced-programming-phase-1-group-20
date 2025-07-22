package org.example.client.views;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.example.client.Main;
import org.example.client.controllers.GameMenuController;
import org.example.common.models.App;
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
import org.example.client.views.effects.ClimateSystem;
import org.example.client.controllers.NPCSpriteController;
import org.example.client.views.effects.LightningSystem;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import com.badlogic.gdx.math.Vector3;

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
    private float lightingUpdateTimer;
    private static final float LIGHTING_UPDATE_INTERVAL = 0.5f;

    // Climate system
    private ClimateSystem climateSystem;

    // Lightning system
    private LightningSystem lightningSystem;

    // Previous state tracking
    private Weather lastKnownWeather;
    private Seasons lastKnownSeason;
    private int lastKnownHour = -1;

    // NPC rendering
    private NPCSpriteController npcSpriteController;

    // Tool usage
    private float lastToolMouseX = 0;
    private float lastToolMouseY = 0;

    // Energy bar components
    private Table energyBarTable;
    private int lastKnownEnergy = -1;
    private static final int ENERGY_BAR_WIDTH = 120;
    private static final int ENERGY_BAR_HEIGHT = 15;

    // --- MINIMAP COMPONENTS (MODIFIED) ---
    private OrthographicCamera miniMapCamera;
    private Viewport miniMapViewport;
    private boolean isMapVisible = false;
    private Texture whitePixelTexture; // For drawing colored shapes efficiently
    private static final int MINIMAP_SIZE_ON_SCREEN = 250; // pixels
    private static final int MINIMAP_MARGIN = 15; // pixels from edge

    public GameView(GameMenuController controller, Player player, Game game, Skin skin, User user) {
        this.controller = controller;
        this.player = player;
        this.game = game;
        this.skin = skin;
        this.user = user;
        this.gameTime = 0;
        this.lightingUpdateTimer = 0;

        initializeLighting();
        climateSystem = new ClimateSystem(camera);
        npcSpriteController = new NPCSpriteController();
        lightningSystem = new LightningSystem();
        lightningSystem.setScreenDimensions(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        loadCustomFont();
        initializeLabels();
        initializeClock();
        createEnergyBar();
        updateWeatherAndSeasonDisplays();

        camera = new OrthographicCamera(120, 120);
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        initializeTables();
        initializeMiniMap(); // NEW: Initialize the mini-map camera and viewport
        controller.setView(this);
    }

    // --- NEW METHOD: Initializes the mini-map camera and viewport ---
    private void initializeMiniMap() {
        // The mini-map camera looks at a 400x400 world unit area.
        // This value determines the initial "zoom" level.
        float miniMapWorldSize = 400f;
        miniMapCamera = new OrthographicCamera(miniMapWorldSize, miniMapWorldSize);

        // Position the mini-map in the top-left corner
        miniMapViewport = new ExtendViewport(miniMapWorldSize, miniMapWorldSize, miniMapCamera);
        miniMapViewport.setScreenBounds(
            MINIMAP_MARGIN,
            Gdx.graphics.getHeight() - MINIMAP_SIZE_ON_SCREEN - MINIMAP_MARGIN,
            MINIMAP_SIZE_ON_SCREEN,
            MINIMAP_SIZE_ON_SCREEN
        );

        // Create a 1x1 white texture to draw colored rectangles for map tiles
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whitePixelTexture = new Texture(pixmap);
        pixmap.dispose();
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
            toggleMinimap();
            return true;
        }
        if (keycode == Input.Keys.L) {
            if (lightningSystem != null) {
                lightningSystem.triggerLightning();
            }
            return true;
        }
        if (keycode == Input.Keys.ESCAPE) {
            Main.getGame().setScreen(new InventoryScreen(player, skin, this));
            return true;
        }
        if(keycode == Input.Keys.B){
            Main.getGame().setScreen(new CraftingScreen(player, skin, this));
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
        // Only handle left mouse button
        if (button == Input.Buttons.LEFT && player.getCurrentTool() != null) {
            // Convert screen coordinates to world coordinates
            Vector3 worldCoords = camera.unproject(new Vector3(screenX, screenY, 0));
            float playerX = player.getPosX();
            float playerY = player.getPosY();
            float dx = worldCoords.x - playerX;
            float dy = worldCoords.y - playerY;
            // Store last mouse position for tool animation
            lastToolMouseX = worldCoords.x;
            lastToolMouseY = worldCoords.y;
            // Calculate angle and direction
            double angle = Math.atan2(dy, dx);
            String direction;
            if (Math.abs(dx) > Math.abs(dy)) {
                direction = dx > 0 ? "east" : "west";
            } else {
                direction = dy > 0 ? "north" : "south";
            }
            player.useTool(direction, game.getGameMap());
            // Trigger tool swing animation with mouse position
            if (controller != null && controller.getPlayerController() != null) {
                controller.getPlayerController().triggerToolSwing(direction, worldCoords.x, worldCoords.y);
            }
            return true;
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
    public boolean scrolled(float amountX, float amountY) {
        // NEW: Handle zooming the mini-map with the mouse scroll wheel
        if (isMapVisible) {
            // amountY is -1 for scroll up (zoom in), 1 for scroll down (zoom out)
            if (amountY > 0) {
                miniMapCamera.zoom += 0.1f; // Zoom out
            } else {
                miniMapCamera.zoom -= 0.1f; // Zoom in
            }
            // Clamp the zoom to reasonable values
            miniMapCamera.zoom = MathUtils.clamp(miniMapCamera.zoom, 0.5f, 4.0f);
            return true; // Input was handled
        }
        return false;
    }

    /**
     * Toggles the visibility of the mini-map. This is the only method you need to call.
     */
    private void toggleMinimap() {
        isMapVisible = !isMapVisible;
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
        // Clear screen
        Color bgColor = currentLightColor.cpy().mul(0.3f);
        ScreenUtils.clear(bgColor.r, bgColor.g, bgColor.b, 1);

        // --- Update Logic ---
        if (!pauseTable.isVisible()) {
            gameTime += deltaTime;
            updateLighting(deltaTime);
            updateClockDisplay();
            updateWeatherAndSeasonDisplays();

            Date currentDate = getCurrentGameDate();
            if (currentDate != null) {
                climateSystem.update(deltaTime, currentDate.getWeatherToday(), currentLightColor);
                lightningSystem.update(deltaTime);
                lightningSystem.updateForWeather(currentDate.getWeatherToday(), deltaTime);
            }
            // NEW: Update the mini-map camera to follow the player
            if (isMapVisible) {
                miniMapCamera.position.set(player.getPosX(), player.getPosY(), 0);
                miniMapCamera.update();
            }
        }

        // --- Render Main Game Scene ---
        Main.getBatch().setProjectionMatrix(camera.combined);
        Main.getBatch().begin();
        Main.getBatch().setColor(currentLightColor);
        if (!pauseTable.isVisible()) {
            controller.update(); // Renders the world
        }
        renderNPCs(deltaTime);
        if (getCurrentGameDate() != null) {
            climateSystem.render(Main.getBatch(), currentLightColor);
        }
        lightningSystem.render(Main.getBatch());
        Main.getBatch().end();


        // --- Render Mini-map (if visible) ---
        if (isMapVisible) {
            // This applies the glViewport and scissor, restricting drawing to the mini-map area
            miniMapViewport.apply();
            Main.getBatch().setProjectionMatrix(miniMapCamera.combined);

            Main.getBatch().begin();
            // First, draw a solid background for the mini-map
            Main.getBatch().setColor(0.1f, 0.1f, 0.1f, 0.8f); // Dark, semi-transparent
            Main.getBatch().draw(whitePixelTexture, miniMapCamera.position.x - miniMapCamera.viewportWidth / 2 * miniMapCamera.zoom,
                miniMapCamera.position.y - miniMapCamera.viewportHeight / 2 * miniMapCamera.zoom,
                miniMapCamera.viewportWidth * miniMapCamera.zoom,
                miniMapCamera.viewportHeight * miniMapCamera.zoom);

            renderMiniMapContents(Main.getBatch());
            Main.getBatch().end();
        }


        // --- Render UI ---
        Main.getBatch().setColor(Color.WHITE); // Reset color for UI
        stage.act(Math.min(deltaTime, 1 / 30f));
        stage.draw();
        renderEnergyBar();
    }

    private void renderMiniMapContents(SpriteBatch batch) {
        GameMap gameMap = App.getGame().getGameMap();
        if (gameMap == null) return;

        // Render the entire map by iterating through its components
        Village village = gameMap.getVillage();
        if(village != null){
            for(int x = 0; x < Village.width; x++){
                for(int y = 0; y < Village.height; y++){
                    Location loc = village.getTiles()[x][y];
                    if(loc != null){
                        Color tileColor = getTileColor(loc.getTile());
                        batch.setColor(tileColor);
                        // Draw a rectangle for each tile. World positions are used directly.
                        batch.draw(whitePixelTexture, loc.getX(), loc.getY(), 1, 1);
                    }
                }
            }
        }
        // You would do the same for your Farm objects here...
        // For example:
        for (Farm farm : gameMap.getFarms()) {
            for (int x = 0; x < Farm.width; x++) {
                for (int y = 0; y < Farm.height; y++) {
                    Location loc = farm.getItem(x,y);
                    if(loc != null) {
                        Color tileColor = getTileColor(loc.getTile());
                        batch.setColor(tileColor);
                        batch.draw(whitePixelTexture, loc.getX(), loc.getY(), 1, 1);
                    }
                }
            }
        }

        // Render other players
        for (Player otherPlayer : gameMap.getPlayers()) {
            if (otherPlayer != player) {
                batch.setColor(Color.BLUE); // Other players are blue
                batch.draw(whitePixelTexture, otherPlayer.getPosX() - 1, otherPlayer.getPosY() - 1, 2, 2);
            }
        }

        // Render the main player on top
        batch.setColor(Color.RED); // Player is red
        batch.draw(whitePixelTexture, player.getPosX() - 1, player.getPosY() - 1, 2, 2);

        // Reset color when done
        batch.setColor(Color.WHITE);
    }

    private Color getTileColor(TileType tileType) {
        switch (tileType) {
            case Dirt: return new Color(0.6f, 0.4f, 0.2f, 1f);
            case WATER: return new Color(0.2f, 0.4f, 0.8f, 1f);
            case STONE: return new Color(0.5f, 0.5f, 0.5f, 1f);
            case TREE: return new Color(0.4f, 0.3f, 0.2f, 1f);
            case VILLAGE: return new Color(0.7f, 0.5f, 0.3f, 1f);
            case MARKET: return new Color(0.9f, 0.7f, 0.5f, 1f);
            case PATH: return new Color(0.8f, 0.6f, 0.4f, 1f);
            case BUILDING: return new Color(0.8f, 0.6f, 0.4f, 1f);
            case SAND: return new Color(0.9f, 0.8f, 0.6f, 1f);
            case PLOWED: return new Color(0.5f, 0.3f, 0.1f, 1f);
            case CROP: return new Color(0.2f, 0.8f, 0.2f, 1f);
            default: return new Color(0.3f, 0.3f, 0.3f, 1f);
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        camera.setToOrtho(false, width, height);

        if (miniMapViewport != null) {
            miniMapViewport.setScreenBounds(
                MINIMAP_MARGIN,
                height - MINIMAP_SIZE_ON_SCREEN - MINIMAP_MARGIN,
                MINIMAP_SIZE_ON_SCREEN,
                MINIMAP_SIZE_ON_SCREEN
            );
        }

        if (climateSystem != null) {
            climateSystem.dispose();
            climateSystem = new ClimateSystem(camera);
        }
        if (lightningSystem != null) {
            lightningSystem.setScreenDimensions(width, height);
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
        if (stage != null) stage.dispose();
        if (skin != null) skin.dispose();
        if (clockBackgroundTexture != null) clockBackgroundTexture.dispose();
        if (clockNeedleTexture != null) clockNeedleTexture.dispose();
        if (customFont != null) customFont.dispose();
        if (smallFont != null) smallFont.dispose();
        if (whitePixelTexture != null) whitePixelTexture.dispose();

        if (climateSystem != null) climateSystem.dispose();
        if (lightningSystem != null) lightningSystem.dispose();
        if (npcSpriteController != null) npcSpriteController.dispose();
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
            moneyLabel.setText("$" + money);
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
        String amPm = (hour >= 12 && hour < 24) ? "pm" : "am";
        String timeText = String.format("%d:%02d %s", displayHour, gameDate.getMinutes(), amPm);
        timeDisplayLabel.setText(timeText);
    }

    private void updateClockNeedle(Date gameDate) {
        int hour = gameDate.getHour();
        int minute = gameDate.getMinutes();
        float totalMinutes = hour * 60 + minute;

        float startTime = 9 * 60f;
        float endTime = 22 * 60f;

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
            clockY + 35f
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

    private void renderEnergyBar() {
        if (energyBarTable == null) return;

        float x = 20;
        float y = Gdx.graphics.getHeight() - ENERGY_BAR_HEIGHT - 20;
        int currentEnergy = player.getEnergy();
        float energyPercentage = Math.max(0, Math.min(1, currentEnergy / 200f));
        float barWidth = ENERGY_BAR_WIDTH * energyPercentage;

        Main.getBatch().begin();
        Main.getBatch().setColor(Color.DARK_GRAY);
        Main.getBatch().draw(skin.getRegion("white"), x, y, ENERGY_BAR_WIDTH, ENERGY_BAR_HEIGHT);

        if (barWidth > 0) {
            Main.getBatch().setColor(Color.GREEN);
            Main.getBatch().draw(skin.getRegion("white"), x, y, barWidth, ENERGY_BAR_HEIGHT);
        }

        Main.getBatch().setColor(Color.WHITE);
        Main.getBatch().draw(skin.getRegion("white"), x, y, ENERGY_BAR_WIDTH, 1);
        Main.getBatch().draw(skin.getRegion("white"), x, y + ENERGY_BAR_HEIGHT - 1, ENERGY_BAR_WIDTH, 1);
        Main.getBatch().draw(skin.getRegion("white"), x, y, 1, ENERGY_BAR_HEIGHT);
        Main.getBatch().draw(skin.getRegion("white"), x + ENERGY_BAR_WIDTH - 1, y, 1, ENERGY_BAR_HEIGHT);

        Main.getBatch().setColor(Color.WHITE);
        Main.getBatch().end();
    }
}
