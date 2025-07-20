package org.example.client.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.client.controllers.LoginRegisterMenuController;
import org.example.common.models.common.Result;

public class LoginRegisterMenuScreen implements Screen {
    private final LoginRegisterMenuController controller;
    private Stage stage;
    private Skin skin;

    // Navigation buttons
    private TextButton registerButton;
    private TextButton loginButton;
    private TextButton backButton;

    // Main tables
    private Table mainTable;
    private Table registerTable;
    private Table loginTable;

    // Register form elements
    private Label registerTitle;
    private Label randomPasswordLabel;
    private TextField usernameField;
    private TextField passwordField;
    private TextField confirmPasswordField;
    private TextField nicknameField;
    private TextField emailField;
    private SelectBox<String> genderSelect;
    private TextButton submitRegisterButton;
    private TextButton backToMainFromRegisterButton;

    // Login form elements
    private Label loginTitle;
    private TextField loginUsernameField;
    private TextField loginPasswordField;
    private CheckBox stayLoggedInCheckBox;
    private TextButton submitLoginButton;
    private TextButton backToMainFromLoginButton;
    private TextButton forgotPasswordButton;

    // Status/error display
    private Label statusLabel;

    public LoginRegisterMenuScreen(LoginRegisterMenuController controller, Skin skin) {
        this.controller = controller;
        this.skin = skin;
        controller.setView(this);
        initializeComponents();
    }

    private void initializeComponents() {
        // Initialize main navigation buttons
        registerButton = new TextButton("REGISTER", skin);
        loginButton = new TextButton("LOGIN", skin);
        backButton = new TextButton("BACK TO WELCOME", skin);

        // Initialize tables
        mainTable = new Table();
        registerTable = new Table();
        loginTable = new Table();

        // Initialize register form
        setupRegisterForm();

        // Initialize login form
        setupLoginForm();

        // Status label for messages
        statusLabel = new Label("", skin);
        statusLabel.setColor(Color.RED);
    }

    private void setupRegisterForm() {
        registerTitle = new Label("CREATE ACCOUNT", skin);
        registerTitle.setColor(Color.WHITE);

        randomPasswordLabel = new Label("Tip: Type 'random' in password field for auto-generated password", skin);
        randomPasswordLabel.setColor(Color.LIGHT_GRAY);

        usernameField = new TextField("", skin);
        usernameField.setMessageText("Username");

        passwordField = new TextField("", skin);
        passwordField.setMessageText("Password");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');

        confirmPasswordField = new TextField("", skin);
        confirmPasswordField.setMessageText("Confirm Password");
        confirmPasswordField.setPasswordMode(true);
        confirmPasswordField.setPasswordCharacter('*');

        nicknameField = new TextField("", skin);
        nicknameField.setMessageText("Nickname");

        emailField = new TextField("", skin);
        emailField.setMessageText("Email");

        genderSelect = new SelectBox<>(skin);
        genderSelect.setItems("male", "female", "other");

        submitRegisterButton = new TextButton("CREATE ACCOUNT", skin);
        backToMainFromRegisterButton = new TextButton("BACK", skin);

        // Style buttons
        submitRegisterButton.setColor(Color.GREEN);
        backToMainFromRegisterButton.setColor(Color.GRAY);
    }

    private void setupLoginForm() {
        loginTitle = new Label("LOGIN", skin);
        loginTitle.setColor(Color.WHITE);

        loginUsernameField = new TextField("", skin);
        loginUsernameField.setMessageText("Username");

        loginPasswordField = new TextField("", skin);
        loginPasswordField.setMessageText("Password");
        loginPasswordField.setPasswordMode(true);
        loginPasswordField.setPasswordCharacter('*');

        stayLoggedInCheckBox = new CheckBox(" Keep me logged in", skin);
        stayLoggedInCheckBox.setChecked(true);

        submitLoginButton = new TextButton("LOGIN", skin);
        backToMainFromLoginButton = new TextButton("BACK", skin);
        forgotPasswordButton = new TextButton("FORGOT PASSWORD", skin);

        // Style buttons
        submitLoginButton.setColor(Color.BLUE);
        backToMainFromLoginButton.setColor(Color.GRAY);
        forgotPasswordButton.setColor(Color.ORANGE);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Clear stage
        stage.clear();

        // Setup main container
        Table rootContainer = new Table();
        rootContainer.setFillParent(true);
        rootContainer.center();

        // Setup main menu table
        setupMainMenuTable();

        // Setup register table
        setupRegisterTableLayout();

        // Setup login table
        setupLoginTableLayout();

        // Add status label
        Table statusContainer = new Table();
        statusContainer.setFillParent(true);
        statusContainer.bottom().padBottom(20);
        statusContainer.add(statusLabel);

        // Add tables to root container
        rootContainer.add(mainTable);
        rootContainer.add(loginTable);
        rootContainer.add(registerTable);

        stage.addActor(rootContainer);
        stage.addActor(statusContainer);

        // Set initial visibility
        showMainMenu();

        // Setup button listeners
        setupButtonListeners();
    }

    private void setupMainMenuTable() {
        mainTable.clear();
        mainTable.center();

        Label title = new Label("WELCOME", skin);
        title.setColor(Color.WHITE);

        mainTable.add(title).padBottom(50).row();
        mainTable.add(loginButton).width(200).height(50).pad(10).row();
        mainTable.add(registerButton).width(200).height(50).pad(10).row();
        mainTable.add(backButton).width(200).height(50).pad(10);

        // Add animations
        mainTable.addAction(Actions.fadeIn(0.5f));
    }

    private void setupRegisterTableLayout() {
        registerTable.clear();
        registerTable.center();

        registerTable.add(registerTitle).padBottom(20).row();
        registerTable.add(randomPasswordLabel).padBottom(20).row();

        // Username
        registerTable.add(new Label("Username:", skin)).padRight(10).right();
        registerTable.add(usernameField).width(200).left().row();

        // Password
        registerTable.add(new Label("Password:", skin)).padRight(10).right();
        registerTable.add(passwordField).width(200).left().row();

        // Confirm Password
        registerTable.add(new Label("Confirm Password:", skin)).padRight(10).right();
        registerTable.add(confirmPasswordField).width(200).left().row();

        // Nickname
        registerTable.add(new Label("Nickname:", skin)).padRight(10).right();
        registerTable.add(nicknameField).width(200).left().row();

        // Email
        registerTable.add(new Label("Email:", skin)).padRight(10).right();
        registerTable.add(emailField).width(200).left().row();

        // Gender
        registerTable.add(new Label("Gender:", skin)).padRight(10).right();
        registerTable.add(genderSelect).width(200).left().row();

        // Buttons
        registerTable.add(submitRegisterButton).width(150).height(40).pad(10);
        registerTable.add(backToMainFromRegisterButton).width(150).height(40).pad(10);

        // Add some padding between rows
        for (int i = 0; i < registerTable.getCells().size; i++) {
            registerTable.getCells().get(i).padTop(5).padBottom(5);
        }
    }

    private void setupLoginTableLayout() {
        loginTable.clear();
        loginTable.center();

        loginTable.add(loginTitle).padBottom(30).row();

        // Username
        loginTable.add(new Label("Username:", skin)).padRight(10).right();
        loginTable.add(loginUsernameField).width(200).left().row();

        // Password
        loginTable.add(new Label("Password:", skin)).padRight(10).right();
        loginTable.add(loginPasswordField).width(200).left().row();

        // Stay logged in checkbox
        loginTable.add(stayLoggedInCheckBox).colspan(2).center().padTop(10).row();

        // Buttons
        Table buttonTable = new Table();
        buttonTable.add(submitLoginButton).width(120).height(40).pad(5);
        buttonTable.add(backToMainFromLoginButton).width(120).height(40).pad(5);
        buttonTable.row();
        buttonTable.add(forgotPasswordButton).width(200).height(30).pad(5).colspan(2);

        loginTable.add(buttonTable).colspan(2).center().padTop(20);

        // Add some padding between rows
        for (int i = 0; i < loginTable.getCells().size; i++) {
            loginTable.getCells().get(i).padTop(5).padBottom(5);
        }
    }

    private void setupButtonListeners() {
        // Main menu buttons
        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showLoginForm();
            }
        });

        registerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showRegisterForm();
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.goBackToWelcome();
            }
        });

        // Register form buttons
        submitRegisterButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleRegisterSubmit();
            }
        });

        backToMainFromRegisterButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showMainMenu();
            }
        });

        // Login form buttons
        submitLoginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleLoginSubmit();
            }
        });

        backToMainFromLoginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showMainMenu();
            }
        });

        forgotPasswordButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                handleForgotPassword();
            }
        });
    }

    private void handleRegisterSubmit() {
        String[] args = {
            usernameField.getText().trim(),
            passwordField.getText(),
            confirmPasswordField.getText(),
            nicknameField.getText().trim(),
            emailField.getText().trim(),
            genderSelect.getSelected()
        };

        Result result = controller.registerUser(args);

        if (result.success()) {
            showStatus("Registration successful! You can now login.", Color.GREEN);
            clearRegisterForm();
            showLoginForm();
        } else {
            showStatus("Registration failed: " + result.message(), Color.RED);
        }
    }

    private void handleLoginSubmit() {
        String[] args = {
            loginUsernameField.getText().trim(),
            loginPasswordField.getText(),
            stayLoggedInCheckBox.isChecked() ? "true" : ""
        };

        Result result = controller.login(args);

        if (result.success()) {
            showStatus("Login successful! Welcome back.", Color.GREEN);

            // Navigate to MainMenuScreen after successful login
            stage.getRoot().addAction(Actions.sequence(
                Actions.delay(1.5f), // Wait for success message to show
                Actions.run(() -> {
                    // Get the logged-in user and navigate to MainMenuScreen
                    org.example.common.models.entities.User loggedInUser = org.example.common.models.App.getLoggedInUser();
                    if (loggedInUser != null) {
                        org.example.client.Main.getGame().getScreen().dispose();

                        org.example.client.controllers.MainMenuController mainMenuController =
                            new org.example.client.controllers.MainMenuController(loggedInUser);
                        org.example.client.views.MainMenuScreen mainMenuScreen =
                            new org.example.client.views.MainMenuScreen(null, mainMenuController,
                                org.example.utils.AssetManager.getAssetManager().getSkin());

                        org.example.client.Main.getGame().setScreen(mainMenuScreen);
                    }
                })
            ));
        } else {
            showStatus("Login failed: " + result.message(), Color.RED);
        }
    }

    private void handleForgotPassword() {
        // TODO: Implement forgot password functionality
        showStatus("Forgot password feature coming soon!", Color.ORANGE);
    }

    private void showStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setColor(color);
        statusLabel.addAction(Actions.sequence(
            Actions.alpha(1f),
            Actions.delay(3f),
            Actions.fadeOut(1f)
        ));
    }

    private void clearRegisterForm() {
        usernameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        nicknameField.setText("");
        emailField.setText("");
        genderSelect.setSelectedIndex(0);
    }

    private void showMainMenu() {
        mainTable.setVisible(true);
        loginTable.setVisible(false);
        registerTable.setVisible(false);
        clearStatus();
    }

    private void showLoginForm() {
        mainTable.setVisible(false);
        loginTable.setVisible(true);
        registerTable.setVisible(false);
        clearStatus();
    }

    private void showRegisterForm() {
        mainTable.setVisible(false);
        loginTable.setVisible(false);
        registerTable.setVisible(true);
        clearStatus();
    }

    public void showRegisterFormDirectly() {
        // Method to be called from controller to show register form directly
        Gdx.app.postRunnable(this::showRegisterForm);
    }

    private void clearStatus() {
        statusLabel.setText("");
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.1f, 0.2f, 1); // Dark blue background
        stage.act(Math.min(delta, 1 / 30f));
        stage.draw();
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
    }
}
