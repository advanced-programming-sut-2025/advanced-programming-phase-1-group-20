package org.example.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
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

public class GameMenuScreen implements Screen , InputProcessor {
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
//    private App app;

    public GameMenuScreen(GameMenuController controller, Player player, Game game , Skin skin , User user) {
        this.controller = controller;
        this.player = player;
        this.game = game;
        this.skin = skin;
//        this.app = app;
        this.user = user;
        gameTime = 0;

        initializingLabels();

        camera = new OrthographicCamera(120 , 120);
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        initializingTable();

        controller.setView(this);
    }


    public void initializingLabels() {
        timeLabel = new Label("Time: " + gameTime, skin);
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
        table.add(pauseButton).padRight(10);
        table.add(timeLabel).width(100).height(40);

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

        if(!pauseTable.isVisible()) {
            controller.update();
            gameTime += v;
        }
        Main.getBatch().end();

        timeLabel.setText(String.format("Time: " + gameTime));
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

    }
}
