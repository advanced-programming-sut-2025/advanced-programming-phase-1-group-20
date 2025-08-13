package org.example.client.views.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.client.controllers.LoadGameController;
import org.example.common.models.entities.Game;

import java.util.List;
import java.util.Map;

public class LoadGameScreen implements Screen {
    private final LoadGameController controller;
    private final Stage stage;
    private final Skin skin;

    private Table mainTable;
    private Label statusLabel;
    private Table savedGamesTable;
    private ScrollPane savedGamesScrollPane;
    private Table onlinePlayersTable;
    private ScrollPane onlinePlayersScrollPane;
    private Table playersListTable;

    public LoadGameScreen(LoadGameController controller, Skin skin) {
        this.controller = controller;
        this.skin = skin;
        this.stage = new Stage(new ScreenViewport());
        this.controller.setView(this);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        Label title = new Label("Load Game", skin, "title");
        statusLabel = new Label("Connect to the server to see online players and load games.", skin);
        statusLabel.setWrap(true);

        // Saved Games Section
        Table savedGamesContainer = new Table();
        Label savedGamesTitle = new Label("Saved Games", skin);
        savedGamesTable = new Table();
        savedGamesScrollPane = new ScrollPane(savedGamesTable, skin);
        savedGamesScrollPane.setFadeScrollBars(false);
        savedGamesContainer.add(savedGamesTitle).row();
        savedGamesContainer.add(savedGamesScrollPane).grow().pad(10);

        // Online Players Section
        Table onlinePlayersContainer = new Table();
        Label onlinePlayersTitle = new Label("Online Players", skin);
        playersListTable = new Table();
        onlinePlayersScrollPane = new ScrollPane(playersListTable, skin);
        onlinePlayersScrollPane.setFadeScrollBars(false);
        onlinePlayersContainer.add(onlinePlayersTitle).row();
        onlinePlayersContainer.add(onlinePlayersScrollPane).grow().pad(10);


        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.goBackToMainMenu();
            }
        });

        mainTable.add(title).colspan(2).pad(20).row();
        mainTable.add(statusLabel).colspan(2).width(Gdx.graphics.getWidth() * 0.8f).pad(10).row();
        mainTable.add(savedGamesContainer).width(Gdx.graphics.getWidth() * 0.4f).growY();
        mainTable.add(onlinePlayersContainer).width(Gdx.graphics.getWidth() * 0.4f).growY().row();
        mainTable.add(backButton).colspan(2).pad(20);

        controller.initialize();
    }

    public void updateSavedGames(List<Game> savedGames) {
        savedGamesTable.clear();
        if (savedGames == null || savedGames.isEmpty()) {
            savedGamesTable.add(new Label("No saved games found.", skin));
            return;
        }

        for (Game game : savedGames) {
            Table gameRow = new Table();
            gameRow.add(new Label(game.getSaveName(), skin)).expandX().left();
            TextButton loadButton = new TextButton("Load", skin);
            loadButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    controller.loadGame(game.getSaveName());
                }
            });
            gameRow.add(loadButton).right();
            savedGamesTable.add(gameRow).growX().pad(5).row();
        }
    }

    public void updateOnlinePlayers(List<Object> players) {
        playersListTable.clear();
        if (players == null || players.isEmpty()) {
            playersListTable.add(new Label("No players online.", skin));
            return;
        }

        for (Object playerObj : players) {
            if (playerObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> playerData = (Map<String, Object>) playerObj;
                String username = (String) playerData.get("username");
                playersListTable.add(new Label(username, skin)).row();
            }
        }
    }

    public void setStatus(String text, Color color) {
        statusLabel.setText(text);
        statusLabel.setColor(color);
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
        stage.dispose();
    }
}
