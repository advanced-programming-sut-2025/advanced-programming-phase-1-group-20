package org.example.client.views.menu;

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
import org.example.client.Main;
import org.example.client.controllers.WelcomeMenuController;
import org.example.client.controllers.menu.MainMenuController;
import org.example.utils.AutoLoginUtil;
import org.example.utils.AssetManager;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.graphics.Color;

public class WelcomeMenuScreen implements Screen {
    // Constants
    private static final float BUTTON_WIDTH = 300f;
    private static final float BUTTON_HEIGHT = 80f;
    private static final float BUTTON_PADDING = 15f;
    private static final float TOP_PADDING = 100f;
    private static final float TITLE_BOTTOM_PADDING = 80f;
    private static final float LOADING_BOTTOM_PADDING = 20f;
    private static final float ANIMATION_DURATION = 0.1f;
    private static final float SCALE_NORMAL = 1.0f;
    private static final float SCALE_HOVER = 1.05f;
    private static final float FADE_DURATION = 0.5f;
    private static final float ALPHA_MIN = 0.3f;
    private static final float ALPHA_MAX = 1.0f;
    private static final float ALPHA_DURATION = 1.0f;

    // UI Components
    private ImageButton signUpButton;
    private ImageButton loginButton;
    private ImageButton exitButton;
    private ImageButton tryGameButton;
    private ImageButton loadGameButton;
    private Label titleLabel;
    private Label loadingLabel;
    private Image background;
    private Table loadingTable;
    private Table mainTable;

    // Core components
    private final WelcomeMenuController controller;
    private final Stage stage;
    private final Skin skin;
    private boolean isAutoLoginChecking = false;

    public WelcomeMenuScreen(WelcomeMenuController controller, Skin skin) {
        this.controller = controller;
        this.skin = skin;
        this.stage = new Stage(new ScreenViewport());

        initializeUI();
        updateBackground(AssetManager.getAssetManager().getWelcomeMenuTexture(0));
        checkAutoLogin();

        controller.setScreen(this);
    }

    // =================
    // UI INITIALIZATION
    // =================

    private void initializeUI() {
        setupBackground();
        setupLoadingScreen();
        setupMainMenu();
    }

    private void setupBackground() {
        background = new Image();
        background.setFillParent(true);
        stage.addActor(background);
    }

    private void setupLoadingScreen() {
        loadingTable = new Table();
        loadingTable.setFillParent(true);
        loadingTable.center();

        createLoadingComponents();
        layoutLoadingComponents();

        stage.addActor(loadingTable);
    }

    private void createLoadingComponents() {
        loadingLabel = new Label("Checking for saved login...", skin);
        loadingLabel.setColor(Color.LIGHT_GRAY);

        // Add pulsing animation to loading label
        loadingLabel.addAction(Actions.forever(
            Actions.sequence(
                Actions.alpha(ALPHA_MIN, ALPHA_DURATION),
                Actions.alpha(ALPHA_MAX, ALPHA_DURATION)
            )
        ));
    }

    private void layoutLoadingComponents() {
        if (titleLabel != null) {
            loadingTable.add(titleLabel).padBottom(TITLE_BOTTOM_PADDING).row();
        }
        loadingTable.add(loadingLabel).padBottom(LOADING_BOTTOM_PADDING).row();
    }

    // =================
    // MAIN MENU SETUP
    // =================

    private void setupMainMenu() {
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center();

        createButtons();
        styleButtons();
        layoutButtons();
        setupButtonListeners();

        stage.addActor(mainTable);
        showMainMenu();
    }

    private void createButtons() {
        // Create image buttons from textures
        signUpButton = createImageButton("content/Titles/signUp.png");
        loginButton = createImageButton("content/Titles/login.png");
        exitButton = createImageButton("content/Titles/exit.png");
        tryGameButton = createImageButton("content/Titles/tryGame.png");
        loadGameButton = createImageButton("content/Titles/loadGame.png");
    }

    private void styleButtons() {
        styleImageButton(signUpButton, Color.WHITE);
        styleImageButton(loginButton, Color.WHITE);
        styleImageButton(exitButton, Color.WHITE);
        styleImageButton(tryGameButton, Color.WHITE);
        styleImageButton(loadGameButton, Color.WHITE);
    }

    private void layoutButtons() {
        // Add top padding to the entire layout
        mainTable.padTop(TOP_PADDING);

        // Create button layout table
        Table buttonTable = new Table();
        buttonTable.defaults().height(BUTTON_HEIGHT).pad(BUTTON_PADDING);

        // Row 1: Authentication buttons (Sign Up, Exit, Login)
        buttonTable.add(signUpButton).width(BUTTON_WIDTH * 0.8f);
        buttonTable.add(exitButton).width(BUTTON_WIDTH * 0.6f).padLeft(10).padRight(10);
        buttonTable.add(loginButton).width(BUTTON_WIDTH * 0.8f);
        buttonTable.row();

        // Row 2: Game buttons (Try Game & Load Game)
        buttonTable.add(tryGameButton).colspan(3).width(BUTTON_WIDTH).padTop(30);
        buttonTable.row();

        buttonTable.add(loadGameButton).colspan(3).width(BUTTON_WIDTH).padTop(15);
        buttonTable.row();

        mainTable.add(buttonTable);
    }

    // =================
    // BUTTON CREATION & STYLING
    // =================

    private ImageButton createImageButton(String imagePath) {
        try {
            Texture buttonTexture = new Texture(Gdx.files.internal(imagePath));
            TextureRegionDrawable buttonDrawable = new TextureRegionDrawable(new TextureRegion(buttonTexture));
            return new ImageButton(buttonDrawable);
        }
        catch (Exception e) {
            Gdx.app.error("WelcomeMenuScreen", "Failed to load button texture: " + imagePath, e);
            // Fallback to a basic button if texture fails to load
            return new ImageButton(skin);
        }
    }

    private void styleImageButton(ImageButton button, Color color) {
        button.setColor(color);
        addHoverAnimation(button);
    }

    private void addHoverAnimation(Actor button) {
        button.addAction(Actions.forever(
            Actions.sequence(
                Actions.scaleTo(SCALE_NORMAL, SCALE_NORMAL, ANIMATION_DURATION),
                Actions.scaleTo(SCALE_HOVER, SCALE_HOVER, ANIMATION_DURATION)
            )
        ));
    }

    // =================
    // EVENT LISTENERS
    // =================

    private void setupButtonListeners() {
        loginButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.handleLoginButton();
            }
        });

        signUpButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.handleSignUpButton();
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        tryGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.handleTryGameButton();
            }
        });

        loadGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.handleLoadGameButton();
            }
        });
    }

    // =================
    // AUTO-LOGIN HANDLING
    // =================

    private void checkAutoLogin() {
        isAutoLoginChecking = true;
        showMainMenu();
    }

    // =================
    // SCREEN STATE MANAGEMENT
    // =================

    public void showLoadingScreen() {
        loadingTable.setVisible(true);
        mainTable.setVisible(false);
    }

    public void showMainMenu() {
        isAutoLoginChecking = false;
        loadingTable.setVisible(false);
        mainTable.setVisible(true);

        // Add smooth fade-in animation
        mainTable.setColor(1, 1, 1, 0);
        mainTable.addAction(Actions.fadeIn(FADE_DURATION));
    }

    public void updateLoadingMessage(String message) {
        if (loadingLabel != null) {
            loadingLabel.setText(message);
        }
    }

    public void updateBackground(Texture texture) {
        if (background != null && texture != null) {
            background.setDrawable(new TextureRegionDrawable(new TextureRegion(texture)));
        }
    }

    // =================
    // SCREEN LIFECYCLE
    // =================

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        controller.update(delta);

        stage.act(delta);
        stage.draw();

        if(background.getDrawable() == null) {
            Gdx.app.error("Render", "Background drawable is null!");
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
        AssetManager.getAssetManager().disposeWelcomeMenuTextures();
    }
}
