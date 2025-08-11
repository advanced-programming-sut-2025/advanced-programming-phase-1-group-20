package org.example.client.views.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.client.Main;
import org.example.common.models.Player.Player;

public class SocialScreen implements Screen {

    private Stage stage;
    private Skin skin;
    private Player player;
    private Screen previousScreen;

    public SocialScreen(Player player, Skin skin, Screen previousScreen) {
        this.player = player;
        this.skin = skin;
        this.previousScreen = previousScreen;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        Label title = new Label("Social Menu (Placeholder)", skin);
        title.setColor(Color.WHITE);

        TextButton backBtn = new TextButton("Back", skin);
        backBtn.addListener(event -> {
            if (!backBtn.isPressed()) return false;
            Main.getGame().setScreen(previousScreen);
            return true;
        });

        table.add(title).padBottom(20).row();
        table.add(backBtn);

        stage.addActor(table);
    }

    @Override
    public void show() { Gdx.input.setInputProcessor(stage); }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.FOREST);
        stage.act(delta);
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Main.getGame().setScreen(previousScreen);
        }
    }

    @Override
    public void resize(int width, int height) { stage.getViewport().update(width, height, true); }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override
    public void dispose() { stage.dispose(); }
}
