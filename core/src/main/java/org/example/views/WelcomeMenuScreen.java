package org.example.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.controllers.WelcomeMenuController;
import org.example.utils.GameAssetManager;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class WelcomeMenuScreen implements Screen {
    private TextButton signUpButton;
    private TextButton loginButton;
    private TextButton exitButton;
    private final WelcomeMenuController controller;
    private final Stage stage;
    private final Skin skin;
    private Image background;

    public WelcomeMenuScreen(WelcomeMenuController controller, Skin skin) {
        this.controller = controller;
        this.skin = skin;
        this.stage = new Stage(new ScreenViewport());
        setupUI();
    }

    private void setupUI() {
        background = new Image();
        background.setFillParent(true);
        stage.addActor(background);

        Table table = new Table();
        table.setFillParent(true);
        table.bottom().padBottom(180);

        signUpButton = new TextButton("SIGN UP", skin);
        loginButton = new TextButton("LOGIN", skin);
        exitButton = new TextButton("EXIT", skin);

        table.add(signUpButton).pad(10);
        table.add(loginButton).pad(10);
        table.row();
        table.add(exitButton).colspan(2).center().padTop(10);

        stage.addActor(table);

        loginButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                //GameAssetManager.getGameAssetManager().playClickSound();
                controller.handleLoginButton();
            }
        });

        signUpButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                //GameAssetManager.getGameAssetManager().playClickSound();
                controller.handleSignUpButton();
            }
        });

        exitButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                //GameAssetManager.getGameAssetManager().playClickSound();
                Gdx.app.exit();
            }
        });
    }

    public void updateBackground(Texture texture) {
        background.setDrawable(new TextureRegionDrawable(new TextureRegion(texture)));
    }

    public void render(float delta) {
        controller.update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    public void show() {
        Gdx.input.setInputProcessor(stage);
        Pixmap pixmap = new Pixmap(Gdx.files.internal("all/cursor.png"));
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(pixmap, 0, 0));
        pixmap.dispose();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void pause() {}
    public void resume() {}
    public void hide() {}

    public void dispose() {
        stage.dispose();
        GameAssetManager.getGameAssetManager().disposeWelcomeMenuTextures();
    }
}
