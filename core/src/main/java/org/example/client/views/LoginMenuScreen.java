package org.example.client.views;

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
import org.example.server.controllers.GameControllers.LoginMenuController;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.graphics.Color;
import org.example.utils.AssetManager;

public class LoginMenuScreen implements Screen {
    private final LoginMenuController controller;
    private final Stage stage;
    private Image background;
    private TextField usernameField, passwordField;
    private Label errorLabel;
    private CheckBox stayLoggedInCheckbox;
    private final Skin skin;
    private ImageButton loginButton, forgotButton, backButton;

    public LoginMenuScreen(LoginMenuController controller, Skin skin) {
        this.controller = controller;
        this.stage = new Stage(new ScreenViewport());
        this.controller.setView(this);
        this.skin = skin;
        setupUI();
    }

    private void setupUI() {
        background = new Image();
        background.setFillParent(true);
        stage.addActor(background);

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top().padTop(100);

        Label title = new Label("LOGIN", skin, "title");
        title.setFontScale(1.8f);
        title.setColor(Color.ROYAL);
        mainTable.add(title).colspan(2).padBottom(40).row();

        usernameField = createTextField("Username");
        mainTable.add(new Label("Username:", skin)).padRight(20).right();
        mainTable.add(usernameField).width(400).height(50).padBottom(15).row();

        passwordField = createPasswordField("Password");
        mainTable.add(new Label("Password:", skin)).padRight(20).right();
        mainTable.add(passwordField).width(400).height(50).padBottom(15).row();

        stayLoggedInCheckbox = new CheckBox(" Stay Logged In", skin);
        mainTable.add(stayLoggedInCheckbox).colspan(2).padBottom(30).row();

        errorLabel = new Label("", skin);
        errorLabel.setColor(Color.RED);
        mainTable.add(errorLabel).colspan(2).padBottom(20).row();

        loginButton = createImageButton(AssetManager.getAssetManager().getLoginTitleTexture());
        forgotButton = createImageButton(AssetManager.getAssetManager().getForgotPasswordTitleTexture());
        backButton = createImageButton(AssetManager.getAssetManager().getBackTitleTexture());

        Table buttonsTable = new Table();
        buttonsTable.add(backButton).pad(10);
        buttonsTable.add(forgotButton).pad(10);
        buttonsTable.add(loginButton).pad(10);

        mainTable.add(buttonsTable).colspan(2).padTop(20);

        stage.addActor(mainTable);
        addListeners();
    }

    private ImageButton createImageButton(Texture texture) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = new TextureRegionDrawable(new TextureRegion(texture));
        style.imageDown = new TextureRegionDrawable(new TextureRegion(texture));
        return new ImageButton(style);
    }

    private TextField createTextField(String hint) {
        TextField field = new TextField("", skin);
        field.setMessageText(hint);
        return field;
    }

    private TextField createPasswordField(String hint) {
        TextField field = createTextField(hint);
        field.setPasswordMode(true);
        field.setPasswordCharacter('•');
        return field;
    }

    private void addListeners() {
        loginButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.handleLogin(
                    usernameField.getText(),
                    passwordField.getText(),
                    stayLoggedInCheckbox.isChecked()
                );
            }
        });

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.handleBack();
            }
        });

        forgotButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.handleForgotPassword();
            }
        });
    }

    public void updateBackground(Texture texture) {
        background.setDrawable(new TextureRegionDrawable(new TextureRegion(texture)));
    }

    public void showError(String message) {
        errorLabel.setText(message);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        controller.update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
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
        stage.dispose();
        AssetManager.getAssetManager().disposeLoginMenuTextures();
    }
}
