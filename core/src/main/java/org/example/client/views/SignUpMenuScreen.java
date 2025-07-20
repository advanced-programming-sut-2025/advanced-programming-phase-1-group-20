package org.example.client.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.client.controllers.SignUpMenuController;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class SignUpMenuScreen implements Screen {
    private final SignUpMenuController controller;
    private final Stage stage;
    private Image background;
    private TextField usernameField, passwordField, confirmPasswordField, emailField;
    private TextField securityAnswerField;
    private SelectBox<String> genderSelect, securityQuestionSelect;
    private Label errorLabel;
    private TextButton generatePasswordButton;
    private Skin skin;

    public SignUpMenuScreen(SignUpMenuController controller, Skin skin) {
        this.controller = controller;
        this.skin = skin;
        this.stage = new Stage(new ScreenViewport());
        this.controller.setView(this);
        setupUI();
    }

    private void setupUI() {
        background = new Image();
        background.setFillParent(true);
        stage.addActor(background);

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top().padTop(80);

        Label title = new Label("CREATE ACCOUNT", skin, "default");
        title.setFontScale(1.8f);
        title.setColor(Color.ROYAL);
        mainTable.add(title).colspan(2).padBottom(40).row();

        usernameField = createTextField("Username");
        mainTable.add(new Label("Username:", skin, "default")).padRight(20).right();
        mainTable.add(usernameField).width(400).height(50).padBottom(15).row();

        emailField = createTextField("Email");
        mainTable.add(new Label("Email:", skin, "default")).padRight(20).right();
        mainTable.add(emailField).width(400).height(50).padBottom(15).row();

        Table passwordTable = new Table();
        passwordField = createPasswordField("Password");
        generatePasswordButton = new TextButton("Generate", skin);
        passwordTable.add(passwordField).width(300).height(50);
        passwordTable.add(generatePasswordButton).padLeft(10).height(50);

        mainTable.add(new Label("Password:", skin, "default")).padRight(20).right();
        mainTable.add(passwordTable).width(400).padBottom(15).row();

        confirmPasswordField = createPasswordField("Confirm Password");
        mainTable.add(new Label("Confirm:", skin, "default")).padRight(20).right();
        mainTable.add(confirmPasswordField).width(400).height(50).padBottom(15).row();

        genderSelect = new SelectBox<>(skin);
        genderSelect.setItems("Male", "Female", "Other");
        mainTable.add(new Label("Gender:", skin, "default")).padRight(20).right();
        mainTable.add(genderSelect).width(400).height(50).padBottom(15).row();

        securityQuestionSelect = new SelectBox<>(skin);
        securityQuestionSelect.setItems(controller.getSecurityQuestions());
        mainTable.add(new Label("Security Question:", skin, "default")).padRight(20).right();
        mainTable.add(securityQuestionSelect).width(400).height(50).padBottom(15).row();

        securityAnswerField = createTextField("Security Answer");
        mainTable.add(new Label("Answer:", skin, "default")).padRight(20).right();
        mainTable.add(securityAnswerField).width(400).height(50).padBottom(30).row();

        errorLabel = new Label("", skin, "default");
        errorLabel.setColor(Color.RED);
        mainTable.add(errorLabel).colspan(2).width(600).padBottom(20).row();

        TextButton registerButton = new TextButton("REGISTER", skin);
        TextButton backButton = new TextButton("BACK", skin);

        Table buttonsTable = new Table();
        buttonsTable.add(backButton).pad(10);
        buttonsTable.add(registerButton).pad(10);

        mainTable.add(buttonsTable).colspan(2).padTop(20);

        stage.addActor(mainTable);
        addListeners(registerButton, backButton, generatePasswordButton);
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

    private void addListeners(TextButton registerButton, TextButton backButton,
                              TextButton generatePasswordButton) {
        registerButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.handleRegister(
                    usernameField.getText(),
                    emailField.getText(),
                    passwordField.getText(),
                    confirmPasswordField.getText(),
                    genderSelect.getSelected(),
                    securityQuestionSelect.getSelected(),
                    securityAnswerField.getText()
                );
            }
        });

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.handleBack();
            }
        });

        generatePasswordButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String randomPass = controller.generateRandomPassword();
                passwordField.setText(randomPass);
                confirmPasswordField.setText(randomPass);
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
