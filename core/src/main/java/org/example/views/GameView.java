package org.example.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
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
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.Main;
import org.example.controllers.GameMenuController;
import org.example.models.App;
import org.example.models.Player.Player;
import org.example.models.common.Date;
import org.example.models.entities.Game;
import org.example.models.entities.User;
import org.example.models.enums.Seasons;
import org.example.models.enums.Weather;
import org.example.utils.GameAssetManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

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
    private Label timeDisplayLabel;
    private BitmapFont customFont;
    private BitmapFont smallFont;

    // Weather and Season components
    private Texture currentWeatherTexture;
    private Texture currentSeasonTexture;
    private Image weatherDisplayImage;
    private Image seasonDisplayImage;

    // Previous state tracking for dynamic updates
    private Weather lastKnownWeather;
    private Seasons lastKnownSeason;

    public GameView(GameMenuController controller, Player player, Game game, Skin skin, User user) {
        this.controller = controller;
        this.player = player;
        this.game = game;
        this.skin = skin;
        this.user = user;
        this.gameTime = 0;

        loadCustomFont();
        initializeLabels();
        initializeClock();
        updateWeatherAndSeasonDisplays();

        camera = new OrthographicCamera(120, 120);
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        initializeTables();
        controller.setView(this);
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
    }

    private void initializeClock() {
        clockBackgroundTexture = new Texture("content/clock/clock.png");
        clockNeedleTexture = new Texture("content/clock/flesh.png");

        clockBackgroundImage = new Image(clockBackgroundTexture);
        clockNeedleImage = new Image(clockNeedleTexture);

        float clockSize = 120f;
        clockBackgroundImage.setSize(clockSize, clockSize);
        clockNeedleImage.setSize(20f, 60f);

        // Configure needle rotation
        clockNeedleImage.setOrigin(clockNeedleImage.getWidth() / 2, 0);
        clockNeedleImage.setPosition(
            (clockBackgroundImage.getWidth() - clockNeedleImage.getWidth()) / 2,
            (clockBackgroundImage.getHeight() - clockNeedleImage.getHeight()) / 2
        );

        // Create clock group with layered components
        Group clockGroup = new Group();
        clockGroup.setSize(clockBackgroundImage.getWidth(), clockBackgroundImage.getHeight());

        // Add background first
        clockGroup.addActor(clockBackgroundImage);

        // Add weather and season displays in the center
        createWeatherAndSeasonDisplays(clockGroup, clockSize);

        // Add needle on top
        clockGroup.addActor(clockNeedleImage);

        // Create time/date text labels
        createClockLabels();

        // Combine everything in a stack
        clockStack = new Stack();
        clockStack.add(clockGroup);
        clockStack.add(createTextTable());
        clockStack.setSize(clockSize, clockSize);
    }

    private void createWeatherAndSeasonDisplays(Group clockGroup, float clockSize) {
        weatherDisplayImage = new Image();
        seasonDisplayImage = new Image();

        float height = GameAssetManager.getGameAssetManager().getFallTexture().getHeight() * 2.05f;
        float width = GameAssetManager.getGameAssetManager().getFallTexture().getWidth() * 1.8f;

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
        labelStyle.font.getData().setScale(0.35f);
        labelStyle.font.getData().markupEnabled = true;

        dateLabel = new Label("[b]Mon. 1[/b]", labelStyle);
        timeDisplayLabel = new Label("[b]6:00 am[/b]", labelStyle);
    }

    private Table createTextTable() {
        Table textTable = new Table();
        textTable.top().right().padTop(10).padRight(10);
        textTable.setFillParent(true);
        textTable.add(dateLabel).row();
        textTable.add(timeDisplayLabel).padTop(5);
        return textTable;
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
        switch (weatherName) {
            case "stormy":
                return "getStormyTexture";
            default:
                return "get" + capitalizeFirst(weatherName) + "Texture";
        }
    }

    private String buildSeasonMethodName(Seasons season) {
        String seasonName = season.toString().toLowerCase();

        if ("autumn".equals(seasonName)) {
            seasonName = "fall";
        }

        return "get" + capitalizeFirst(seasonName) + "Texture";
    }

    private Texture getTextureUsingReflection(String methodName) throws Exception {
        GameAssetManager assetManager = GameAssetManager.getGameAssetManager();
        Method textureMethod = GameAssetManager.class.getMethod(methodName);
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


    @Override
    public boolean keyDown(int i) { return false; }
    @Override
    public boolean keyUp(int i) { return false; }
    @Override
    public boolean keyTyped(char c) { return false; }
    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) { return false; }
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


    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(this);
        Gdx.input.setInputProcessor(multiplexer);

        mainTable.top().right();
        mainTable.setFillParent(true);
        mainTable.padTop(10).padRight(10);
        mainTable.add(clockStack).size(120, 120);
        stage.addActor(mainTable);

        pauseTable.setFillParent(true);
        pauseTable.center();
        pauseTable.add(resumeButton).width(200).height(20).pad(10);
        pauseTable.setVisible(false);
        stage.addActor(pauseTable);
    }

    @Override
    public void render(float deltaTime) {
        ScreenUtils.clear(0, 0, 0, 1);
        Main.getBatch().begin();

        try {
            Main.getBatch().draw(clockBackgroundTexture, 200, 200, 100, 100);
        } catch (Exception e) {
        }

        if (!pauseTable.isVisible()) {
            controller.update();
            gameTime += deltaTime;
            updateClockDisplay();
            updateWeatherAndSeasonDisplays();
        }

        Main.getBatch().end();
        stage.act(Math.min(deltaTime, 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {}
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
    }

    private void updateClockDisplay() {
        Date gameDate = getCurrentGameDate();
        if (gameDate == null) return;

        updateDateLabel(gameDate);
        updateTimeLabel(gameDate);
        updateClockNeedle(gameDate);
        updateLabelPositions();
    }

    private void updateDateLabel(Date gameDate) {
        String dayText = getDayOfWeekAbbreviation(gameDate) + ". " + gameDate.getDay();
        dateLabel.setText(dayText);
    }

    private void updateTimeLabel(Date gameDate) {
        int hour = gameDate.getHour();
        int displayHour = (hour == 0) ? 12 : (hour > 12 ? hour - 12 : hour);
        String amPm = (hour >= 12) ? "pm" : "am";
        String timeText = String.format("%d:%02d %s", displayHour, 0, amPm);
        timeDisplayLabel.setText(timeText);
    }

    private void updateClockNeedle(Date gameDate) {
        int hour = gameDate.getHour();
        float totalMinutes = hour * 60;
        float rotation = (totalMinutes - 360) / 2;
        clockNeedleImage.setRotation(rotation);
    }

    private void updateLabelPositions() {
        float clockX = clockBackgroundImage.getX();
        float clockY = clockBackgroundImage.getY();
        float clockSize = 120f;

        dateLabel.setPosition(
            clockX + clockSize/2 - dateLabel.getWidth()/2 + 15f,
            clockY + 95f
        );

        timeDisplayLabel.setPosition(
            clockX + clockSize/2 - timeDisplayLabel.getWidth()/2 + 15f,
            clockY + 49f
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
}
