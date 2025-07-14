package org.example.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.controllers.WelcomeMenuController;
import org.example.utils.GameAssetManager;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class WelcomeMenuScreen implements Screen {
    private ImageButton signUpButton;
    private ImageButton loginButton;
    private ImageButton exitButton;
    private final WelcomeMenuController controller;
    private final Stage stage;
    private Image background;

    public WelcomeMenuScreen(WelcomeMenuController controller) {
        this.controller = controller;
        this.stage = new Stage(new ScreenViewport());
        setupUI();
    }

    private void setupUI() {
        background = new Image();
        background.setFillParent(true);
        stage.addActor(background);

        GameAssetManager assetManager = GameAssetManager.getGameAssetManager();

        signUpButton = createImageButton(assetManager.getSignUpTexture());
        loginButton = createImageButton(assetManager.getLoginTexture());
        exitButton = createImageButton(assetManager.getExitTexture());

        Table table = new Table();
        table.setFillParent(true);
        table.bottom().padBottom(180);

        table.add(signUpButton).pad(20);
        table.add(loginButton).pad(20);
        table.row();
        table.add(exitButton).colspan(2).center().padTop(20);

        stage.addActor(table);

        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
//                GameAssetManager.getGameAssetManager().playClickSound();
                controller.handleLoginButton();
            }
        });

        signUpButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
//                GameAssetManager.getGameAssetManager().playClickSound();
                controller.handleSignUpButton();
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
//                GameAssetManager.getGameAssetManager().playClickSound();
                Gdx.app.exit();
            }
        });
    }

    private ImageButton createImageButton(Texture texture) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = new TextureRegionDrawable(new TextureRegion(texture));
        style.imageDown = new TextureRegionDrawable(new TextureRegion(texture));
        return new ImageButton(style);
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
//        Pixmap pixmap = new Pixmap(Gdx.files.internal("all/cursor.png"));
//        Gdx.graphics.setCursor(Gdx.graphics.newCursor(pixmap, 0, 0));
//        pixmap.dispose();
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
