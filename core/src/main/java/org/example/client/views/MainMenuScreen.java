package org.example.client.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.client.Main;
import org.example.client.controllers.MainMenuController;
import org.example.common.models.App;

public class MainMenuScreen implements Screen {
    private final MainMenuController controller;
    private Stage stage;
    private Skin skin;

    // UI Components
    private Table mainTable;
    private Label titleLabel;
    private Label welcomeLabel;
    
    // Buttons
    private TextButton singlePlayerButton;
    private TextButton multiplayerButton;
    private TextButton loadGameButton;
    private TextButton profileButton;
    private TextButton settingsButton;
    private TextButton logoutButton;
    private TextButton exitButton;

    // Constants for layout
    private static final float BUTTON_WIDTH = 300f;
    private static final float BUTTON_HEIGHT = 60f;
    private static final float BUTTON_PADDING = 15f;

    public MainMenuScreen(App app, MainMenuController controller, Skin skin) {
        this.controller = controller;
        this.skin = skin;
        controller.setView(this);
        initializeComponents();
    }

    private void initializeComponents() {
        // Main table for layout
        mainTable = new Table();
        
        // Title and welcome labels
        titleLabel = new Label("STARDEW VALLEY", skin);
        titleLabel.setColor(Color.WHITE);
        titleLabel.setFontScale(2.0f);
        
        String username = App.getLoggedInUser() != null ? App.getLoggedInUser().getUsername() : "Player";
        welcomeLabel = new Label("Welcome, " + username + "!", skin);
        welcomeLabel.setColor(Color.CYAN);
        welcomeLabel.setFontScale(1.2f);
        
        // Create buttons
        createButtons();
        setupButtonListeners();
    }

    private void createButtons() {
        singlePlayerButton = new TextButton("SINGLE PLAYER", skin);
        singlePlayerButton.setColor(Color.GREEN);
        
        multiplayerButton = new TextButton("MULTIPLAYER", skin);
        multiplayerButton.setColor(Color.YELLOW);
        
        loadGameButton = new TextButton("LOAD GAME", skin);
        loadGameButton.setColor(Color.CYAN);
        
        profileButton = new TextButton("PROFILE", skin);
        profileButton.setColor(Color.PURPLE);
        
        settingsButton = new TextButton("SETTINGS", skin);
        settingsButton.setColor(Color.GRAY);
        
        logoutButton = new TextButton("LOGOUT", skin);
        logoutButton.setColor(Color.ORANGE);
        
        exitButton = new TextButton("EXIT GAME", skin);
        exitButton.setColor(Color.RED);
    }

    private void setupButtonListeners() {
        singlePlayerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Handle single player - create new game or continue existing
                handleSinglePlayer();
            }
        });

        multiplayerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                handleMultiplayer();
            }
        });

        loadGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                handleLoadGame();
            }
        });

        profileButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                handleProfile();
            }
        });

        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                handleSettings();
            }
        });

        logoutButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                handleLogout();
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
    }

    private void handleSinglePlayer() {
        // Navigate to single player game setup or directly start game
        System.out.println("Single Player selected");
        // TODO: Implement single player flow - could show map selection, etc.
    }

    private void handleMultiplayer() {
        // Navigate to multiplayer menu
        System.out.println("Multiplayer selected");
        Main.getGame().getScreen().dispose();
        
        org.example.client.controllers.MultiplayerMenuController multiplayerController = 
            new org.example.client.controllers.MultiplayerMenuController();
        org.example.client.views.MultiplayerMenuScreen multiplayerScreen = 
            new org.example.client.views.MultiplayerMenuScreen(multiplayerController, skin);
        Main.getGame().setScreen(multiplayerScreen);
    }

    private void handleLoadGame() {
        // Load existing game
        System.out.println("Load Game selected");
        // TODO: Implement load game functionality
    }

    private void handleProfile() {
        // Show profile/settings screen
        System.out.println("Profile selected");
        // TODO: Implement profile screen
    }

    private void handleSettings() {
        // Show settings screen
        System.out.println("Settings selected");
        // TODO: Implement settings screen
    }

    private void handleLogout() {
        // Logout and return to welcome screen
        System.out.println("Logout selected");
        controller.logout();
        
        // Navigate back to welcome screen
        Main.getGame().getScreen().dispose();
        org.example.client.controllers.WelcomeMenuController welcomeController = 
            new org.example.client.controllers.WelcomeMenuController();
        org.example.client.views.WelcomeMenuScreen welcomeScreen = 
            new org.example.client.views.WelcomeMenuScreen(welcomeController, skin);
        Main.getGame().setScreen(welcomeScreen);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        
        // Setup main layout
        mainTable.setFillParent(true);
        mainTable.center();
        
        // Add title and welcome message
        mainTable.add(titleLabel).padBottom(20).row();
        mainTable.add(welcomeLabel).padBottom(40).row();
        
        // Create button layout table
        Table buttonTable = new Table();
        buttonTable.defaults().width(BUTTON_WIDTH).height(BUTTON_HEIGHT).pad(BUTTON_PADDING);
        
        // Layout buttons in a nice grid
        buttonTable.add(singlePlayerButton).uniform().row();
        buttonTable.add(multiplayerButton).uniform().row();
        buttonTable.add(loadGameButton).uniform().row();
        buttonTable.add(profileButton).uniform().row();
        buttonTable.add(settingsButton).uniform().row();
        buttonTable.add(logoutButton).uniform().padTop(20).row();
        buttonTable.add(exitButton).uniform().row();
        
        mainTable.add(buttonTable);
        stage.addActor(mainTable);
    }

    @Override
    public void render(float delta) {
        // Update controller
        if (controller != null) {
            // Any controller updates can go here
        }
        
        // Clear screen with dark background
        ScreenUtils.clear(0.1f, 0.1f, 0.2f, 1);
        
        // Update and draw stage
        stage.act(Math.min(delta, 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        if (stage != null) {
            stage.getViewport().update(width, height, true);
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
        if (stage != null) {
            stage.dispose();
        }
    }
}
