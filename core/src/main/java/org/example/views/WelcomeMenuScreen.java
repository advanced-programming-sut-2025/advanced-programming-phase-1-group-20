package org.example.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.controllers.WelcomeMenuController;
import org.example.utils.AutoLoginUtil;
import org.example.utils.GameAssetManager;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.graphics.Color;

public class WelcomeMenuScreen implements Screen {
    private TextButton signUpButton;
    private TextButton loginButton;
    private TextButton exitButton;
    private Label titleLabel;
    private Label loadingLabel;
    private final WelcomeMenuController controller;
    private final Stage stage;
    private final Skin skin;
    private Image background;
    private Table loadingTable;
    private Table mainTable;
    private boolean isAutoLoginChecking = false;

    public WelcomeMenuScreen(WelcomeMenuController controller, Skin skin) {
        this.controller = controller;
        this.skin = skin;
        this.stage = new Stage(new ScreenViewport());
        setupUI();
        controller.setScreen(this);

        checkAutoLogin();
    }

    private void setupUI() {
        // Background
        background = new Image();
        background.setFillParent(true);
        stage.addActor(background);

        // Loading screen for auto-login check
        setupLoadingScreen();

        // Main menu
        setupMainMenu();

        // Initially show loading screen
        showLoadingScreen();
    }

    private void setupLoadingScreen() {
        loadingTable = new Table();
        loadingTable.setFillParent(true);
        loadingTable.center();

        // Title
        titleLabel = new Label("WELCOME", skin);
        titleLabel.setColor(Color.WHITE);

        // Loading message
        loadingLabel = new Label("Checking for saved login...", skin);
        loadingLabel.setColor(Color.LIGHT_GRAY);

        loadingLabel.addAction(Actions.forever(
            Actions.sequence(
                Actions.alpha(0.3f, 1f),
                Actions.alpha(1f, 1f)
            )
        ));

        loadingTable.add(titleLabel).padBottom(50).row();
        loadingTable.add(loadingLabel).padBottom(20).row();

        stage.addActor(loadingTable);
    }

    private void setupMainMenu() {
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center();

        // Title

        // Subtitle
        Label subtitle = new Label("Welcome to the Adventure", skin);
        subtitle.setColor(Color.LIGHT_GRAY);

        // Buttons with improved styling
        signUpButton = new TextButton("SIGN UP", skin);
        loginButton = new TextButton("LOGIN", skin);
        exitButton = new TextButton("EXIT", skin);

        // Style buttons
        styleButton(signUpButton, Color.GREEN);
        styleButton(loginButton, Color.BLUE);
        styleButton(exitButton, Color.RED);

        // Layout
        mainTable.add(subtitle).padBottom(50).row();

        Table buttonTable = new Table();
        buttonTable.add(signUpButton).width(200).height(50).pad(10);
        buttonTable.add(loginButton).width(200).height(50).pad(10);
        buttonTable.row();
        buttonTable.add(exitButton).width(200).height(50).pad(10).colspan(2);

        mainTable.add(buttonTable);

        stage.addActor(mainTable);

        // Add button listeners
        setupButtonListeners();
    }

    private void styleButton(TextButton button, Color color) {
        button.setColor(color);
        button.addAction(Actions.forever(
            Actions.sequence(
                Actions.scaleTo(1.0f, 1.0f, 0.1f),
                Actions.scaleTo(1.05f, 1.05f, 0.1f)
            )
        ));
    }

    private void setupButtonListeners() {
        loginButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                controller.handleLoginButton();
            }
        });

        signUpButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                controller.handleSignUpButton();
            }
        });

        exitButton.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
    }

    private void checkAutoLogin() {
        isAutoLoginChecking = true;
        if (AutoLoginUtil.checkAndPerformAutoLogin()) {
            isAutoLoginChecking = false;
        }
        isAutoLoginChecking = false;
        mainTable.setVisible(true);
    }

    public void showLoadingScreen() {
        loadingTable.setVisible(true);
        mainTable.setVisible(false);
    }

    public void showMainMenu() {
        isAutoLoginChecking = false;
        loadingTable.setVisible(false);
        mainTable.setVisible(true);

        // Add fade-in animation
        mainTable.setColor(1, 1, 1, 0);
        mainTable.addAction(Actions.fadeIn(0.5f));
    }

    public void updateLoadingMessage(String message) {
        if (loadingLabel != null) {
            loadingLabel.setText(message);
        }
    }

    public void updateBackground(Texture texture) {
        if (background != null) {
            background.setDrawable(new TextureRegionDrawable(new TextureRegion(texture)));
        }
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
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void pause() {
    }

    public void resume() {
    }

    public void hide() {
    }

    public void dispose() {
        stage.dispose();
        GameAssetManager.getGameAssetManager().disposeWelcomeMenuTextures();
    }
}
