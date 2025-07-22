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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.common.models.App;
import org.example.client.controllers.ProfileMenuController;
import org.example.common.models.entities.User;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.graphics.Color;

public class ProfileMenuScreen implements Screen {
    private final ProfileMenuController controller;
    private final Stage stage;
    private final Skin skin;
    private com.badlogic.gdx.scenes.scene2d.ui.Image background;
    private TextField usernameField, passwordField, emailField, nicknameField;
    private com.badlogic.gdx.scenes.scene2d.ui.Label errorLabel, statsLabel, genderLabel;
    private CheckBox stayLoggedInCheckbox;

    public ProfileMenuScreen(ProfileMenuController controller, Skin skin) {
        this.controller = controller;
        this.skin = skin;
        this.stage = new Stage(new ScreenViewport());
        this.controller.setView(this);
        setupUI();
    }

    private void setupUI() {
        background = new com.badlogic.gdx.scenes.scene2d.ui.Image();
        background.setFillParent(true);
        stage.addActor(background);

        User currentUser = App.getLoggedInUser();

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top().padTop(80);

        com.badlogic.gdx.scenes.scene2d.ui.Label titleLabel = new com.badlogic.gdx.scenes.scene2d.ui.Label("USER PROFILE", skin, "default");
        titleLabel.setFontScale(1.8f);
        titleLabel.setColor(Color.GOLD);
        mainTable.add(titleLabel).colspan(2).padBottom(40).row();

        usernameField = new TextField(currentUser.getUsername(), skin);
        mainTable.add(new com.badlogic.gdx.scenes.scene2d.ui.Label("Username:", skin, "default")).padRight(20).right();
        mainTable.add(usernameField).width(350).height(50).padBottom(15).row();

        passwordField = new TextField("", skin);
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('•');
        passwordField.setMessageText("Enter new password");
        mainTable.add(new com.badlogic.gdx.scenes.scene2d.ui.Label("Password:", skin, "default")).padRight(20).right();
        mainTable.add(passwordField).width(350).height(50).padBottom(15).row();

        emailField = new TextField(currentUser.getEmail(), skin);
        mainTable.add(new com.badlogic.gdx.scenes.scene2d.ui.Label("Email:", skin, "default")).padRight(20).right();
        mainTable.add(emailField).width(350).height(50).padBottom(15).row();

        nicknameField = new TextField(currentUser.getNickname(), skin);
        mainTable.add(new com.badlogic.gdx.scenes.scene2d.ui.Label("Nickname:", skin, "default")).padRight(20).right();
        mainTable.add(nicknameField).width(350).height(50).padBottom(15).row();

        genderLabel = new com.badlogic.gdx.scenes.scene2d.ui.Label("Gender: " + currentUser.getGender().toString(), skin, "default");
        mainTable.add(genderLabel).colspan(2).padBottom(15).row();

        stayLoggedInCheckbox = new CheckBox(" Stay Logged In", skin);
        stayLoggedInCheckbox.setChecked(currentUser.isStayLoggedIn());
        mainTable.add(stayLoggedInCheckbox).colspan(2).padBottom(30).row();

        statsLabel = new com.badlogic.gdx.scenes.scene2d.ui.Label("", skin, "default");
        updateStatsLabel(currentUser);
        mainTable.add(statsLabel).colspan(2).padBottom(20).row();

        errorLabel = new com.badlogic.gdx.scenes.scene2d.ui.Label("", skin, "default");
        errorLabel.setColor(Color.RED);
        mainTable.add(errorLabel).colspan(2).padBottom(20).row();

        Table buttonsTable = new Table();
        TextButton saveButton = new TextButton("SAVE CHANGES", skin);
        TextButton deleteButton = new TextButton("DELETE ACCOUNT", skin);
        TextButton backButton = new TextButton("BACK", skin);

        buttonsTable.add(backButton).pad(10);
        buttonsTable.add(deleteButton).pad(10);
        buttonsTable.add(saveButton).pad(10);

        mainTable.add(buttonsTable).colspan(2).padTop(20);

        stage.addActor(mainTable);
        addListeners(saveButton, backButton, deleteButton);
    }

    private void updateStatsLabel(User user) {
        statsLabel.setText(String.format(
            "Games Played: %d\nHighest Score: %d",
            user.getGamesPlayed(),
            user.getMostEarnedMoney()
        ));
        statsLabel.setAlignment(Align.center);
    }

    private void addListeners(TextButton saveButton, TextButton backButton,
                              TextButton deleteButton) {
        saveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.handleSaveChanges(
                    usernameField.getText(),
                    passwordField.getText(),
                    emailField.getText(),
                    nicknameField.getText(),
                    stayLoggedInCheckbox.isChecked()
                );
            }
        });

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.handleBackButton();
            }
        });

        deleteButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.handleDeleteAccount();
            }
        });
    }

    public void updateBackground(Texture texture) {
        background.setDrawable(new TextureRegionDrawable(new TextureRegion(texture)));
    }

    public void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setColor(Color.RED);
    }

    public void showSuccess(String message) {
        errorLabel.setText(message);
        errorLabel.setColor(Color.GREEN);
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
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        stage.dispose();
    }

    public Stage getStage() {
        return stage;
    }
}
