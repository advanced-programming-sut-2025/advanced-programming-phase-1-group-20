package org.example.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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
import org.example.models.entities.Game;
import org.example.models.entities.User;

public class GameView implements Screen , InputProcessor {
    private Stage stage;
    private GameMenuController controller;
    private Player player;
    private Game game;
    private OrthographicCamera camera;
    private Skin skin;
    private TextButton pauseButton;
    private Table table;
    private Table pauseTable;
    private TextButton resumeButton;
    private float gameTime;
    private Label timeLabel;
    private User user;
    private Texture clockTexture;
    private Texture fleshTexture;
    private Image clockImage;
    private Image fleshImage;
    private Stack clockStack;
    private Table clockOverlay;
    private Label dateLabel;
    private Label timeDisplayLabel;
//    private App app;

    public GameView(GameMenuController controller, Player player, Game game , Skin skin , User user) {
        this.controller = controller;
        this.player = player;
        this.game = game;
        this.skin = skin;
//        this.app = app;
        this.user = user;
        gameTime = 0;

        initializingLabels();
        initializingClock();

        camera = new OrthographicCamera(120 , 120);
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        initializingTable();

        controller.setView(this);
    }


    public void initializingLabels() {
        timeLabel = new Label("Time: " + gameTime, skin);
    }

    public void initializingClock() {
        clockTexture = new Texture("content/clock/clock.png");
        fleshTexture = new Texture("content/clock/flesh.png");

        clockImage = new Image(clockTexture);
        fleshImage = new Image(fleshTexture);

        // ize everything proportionally
        float clockSize = 120f;
        clockImage.setSize(clockSize, clockSize);
        fleshImage.setSize(20f, 60f); // make it a long needle

        // Center the flesh origin at its bottom center
        fleshImage.setOrigin(fleshImage.getWidth() / 2, 0);

        // Position the flesh to rotate from center of the clock
        fleshImage.setPosition(
            (clockImage.getWidth() - fleshImage.getWidth()) / 2,
            (clockImage.getHeight() - fleshImage.getHeight()) / 2
        );

        // Create a group to overlay images manually
        Group clockGroup = new Group();
        clockGroup.setSize(clockImage.getWidth(), clockImage.getHeight());

        clockGroup.addActor(clockImage);
        clockGroup.addActor(fleshImage);

        // Add date and time labels
        dateLabel = new Label("Mon. 1", skin);
        timeDisplayLabel = new Label("6:00 am", skin);

        Table textTable = new Table();
        textTable.top().right().padTop(10).padRight(10);
        textTable.setFillParent(true);
        textTable.add(dateLabel).row();
        textTable.add(timeDisplayLabel).padTop(5);

        // Wrap everything in a Stack
        clockStack = new Stack();
        clockStack.add(clockGroup);
        clockStack.add(textTable);
        clockStack.setSize(clockSize, clockSize);
    }


    public void initializingTable() {
        table = new Table();
        pauseTable = new Table();
        pauseButton = new TextButton("Pause", skin);
        resumeButton = new TextButton("Resume", skin);
    }


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

    public Table getTable() {
        return table;
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

    public Image getClockImage() {
        return clockImage;
    }

    @Override
    public boolean keyDown(int i) {
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
    public boolean touchDown(int i, int i1, int i2, int i3) {
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

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());

        InputMultiplexer multiplexer = new InputMultiplexer();

        multiplexer.addProcessor(stage);

        multiplexer.addProcessor(this);
        Gdx.input.setInputProcessor(multiplexer);

        table.top().right();
        table.setFillParent(true);
        table.padTop(10).padRight(10);

        // Add the clock stack (image + flesh + text overlay) to the table
        table.add(clockStack).size(120, 120);

        stage.addActor(table);

        pauseTable.setFillParent(true);

        pauseTable.center();
        pauseTable.add(resumeButton).width(200).height(20).pad(10);
        // pauseTable.row();

        pauseTable.setVisible(false);
        stage.addActor(pauseTable);
    }

    @Override
    public void render(float v) {
        ScreenUtils.clear(0, 0, 0, 1);
        Main.getBatch().begin();

        try {
            Main.getBatch().draw(clockTexture, 200, 200, 100, 100);
            System.out.println("Clock texture drawn successfully");
        } catch (Exception e) {
            System.out.println("Failed to draw clock texture: " + e.getMessage());
        }

        if(!pauseTable.isVisible()) {
            controller.update();
            gameTime += v;
            updateClockDisplay();
        }
        Main.getBatch().end();

        stage.act(Math.min(v, 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int i, int i1) {

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
        if (clockTexture != null) {
            clockTexture.dispose();
        }
        if (fleshTexture != null) {
            fleshTexture.dispose();
        }
    }

    private void updateClockDisplay() {
        org.example.models.common.Date gameDate = game != null ? game.getDate() : App.getGame().getDate();
        if (gameDate != null) {
            String dayText = getDayOfWeekAbbreviation(gameDate) + ". " + gameDate.getDay();
            dateLabel.setText(dayText);

            int hour = gameDate.getHour();

            int displayHour = (hour == 0) ? 12 : (hour > 12 ? hour - 12 : hour);
            String amPm = (hour >= 12) ? "pm" : "am";
            String timeText = String.format("%d:%02d %s", displayHour, 0, amPm);
            timeDisplayLabel.setText(timeText);

            // Rotate flesh (0 = 6am, 12 = 6pm, full 360 = 24h)
            float totalMinutes = hour * 60;
            float rotation = (totalMinutes - 360) / 2; // Start from 6:00 AM

            fleshImage.setRotation(rotation);
        }
    }

    private String getDayOfWeekAbbreviation(org.example.models.common.Date gameDate) {
        if (gameDate != null) {
            int daysPerSeason = 28;
            int year = 1; // You might need to add a getYear() method to your Date class
            int season = gameDate.getSeason().ordinal();
            int day = gameDate.getDay();

            int totalDays = ((year - 1) * 4 * daysPerSeason) + (season * daysPerSeason) + day - 1;
            int dayOfWeek = (totalDays % 7);

            String[] dayAbbreviations = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
            return dayAbbreviations[dayOfWeek];
        }
        return "Mon";
    }
}
