package org.example.views;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.controllers.ProfileMenuController;
import org.example.models.entities.User;

import java.awt.*;

public class ProfileMenuScreen implements Screen {
    private final ProfileMenuController controller;
    private final Stage stage;
    private Image background;
    private TextField usernameField;
    private TextField passwordField;
    private TextField emailField;
    private TextField nicknameField;
    private Label errorLabel, statsLabel;
    private SelectBox<String> genderSelect;
    private TextButton changeAvatarButton;
    private Image avatarImage;

    public ProfileMenuScreen(ProfileMenuController controller, Skin skin) {
        this.controller = controller;
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

        Label titleLabel = new Label("USER PROFILE", skin, "title");
        titleLabel.setFontScale(1.8f);
        titleLabel.setColor(Color.GOLD);
        mainTable.add(titleLabel).colspan(2).padBottom(40).row();

        avatarImage = new Image(new Texture(Gdx.files.internal("avatars/avatar_" + App.currentUser.getAvatarId() + ".png")));
        avatarImage.setScaling(Scaling.fit);
        mainTable.add(avatarImage).size(150).colspan(2).padBottom(20).row();

        changeAvatarButton = new TextButton("Change Avatar", skin);
        mainTable.add(changeAvatarButton).colspan(2).padBottom(30).row();

        usernameField = createEditableField(App.currentUser.getUsername(), "Username:");
        mainTable.add(usernameField.label).padRight(20).right();
        mainTable.add(usernameField.field).width(300).height(50).padBottom(15).row();

        passwordField = createPasswordField("Password:");
        mainTable.add(passwordField.label).padRight(20).right();
        mainTable.add(passwordField.field).width(300).height(50).padBottom(15).row();

        emailField = createEditableField(App.currentUser.getEmail(), "Email:");
        mainTable.add(emailField.label).padRight(20).right();
        mainTable.add(emailField.field).width(300).height(50).padBottom(15).row();

        nicknameField = createEditableField(App.currentUser.getNickname(), "Nickname:");
        mainTable.add(nicknameField.label).padRight(20).right();
        mainTable.add(nicknameField.field).width(300).height(50).padBottom(15).row();

        Table genderTable = new Table();
        genderTable.add(new Label("Gender:", skin)).padRight(20).right();
        genderSelect = new SelectBox<>(skin);
        genderSelect.setItems("Male", "Female", "Other");
        genderSelect.setSelected(App.currentUser.getGender().toString());
        genderSelect.setDisabled(true);
        genderTable.add(genderSelect).width(300).height(50);
        mainTable.add(genderTable).colspan(2).padBottom(30).row();

        statsLabel = new Label("", skin);
        updateStatsLabel();
        mainTable.add(statsLabel).colspan(2).padBottom(20).row();

        errorLabel = new Label("", skin);
        errorLabel.setColor(Color.RED);
        mainTable.add(errorLabel).colspan(2).padBottom(20).row();

        TextButton saveButton = new TextButton("SAVE CHANGES", skin);
        TextButton backButton = new TextButton("BACK", skin);

        Table buttonsTable = new Table();
        buttonsTable.add(backButton).pad(10);
        buttonsTable.add(saveButton).pad(10);

        mainTable.add(buttonsTable).colspan(2).padTop(20);

        stage.addActor(mainTable);
        addListeners(saveButton, backButton, changeAvatarButton);
    }

    private FieldGroup createEditableField(String initialValue, String labelText) {
        FieldGroup group = new FieldGroup();
        group.label = new Label(labelText, skin);
        group.field = new TextField(initialValue, skin);
        return group;
    }

    private FieldGroup createPasswordField(String labelText) {
        FieldGroup group = new FieldGroup();
        group.label = new Label(labelText, skin);
        group.field = new TextField("", skin);
        group.field.setPasswordMode(true);
        group.field.setPasswordCharacter('•');
        group.field.setMessageText("Enter new password");
        return group;
    }

    private void updateStatsLabel() {
        User user = App.currentUser;
        statsLabel.setText(String.format(
            "Games Played: %d\nHighest Score: %d",
            user.getGamesPlayed(),
            user.getMostEarnedMoney()
        ));
        statsLabel.setAlignment(Align.center);
    }

    private void addListeners(TextButton saveButton, TextButton backButton, TextButton changeAvatarButton) {
        saveButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.handleSaveChanges(
                    usernameField.getText(),
                    passwordField.getText(),
                    emailField.getText(),
                    nicknameField.getText()
                );
            }
        });

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.handleBackButton();
            }
        });

        changeAvatarButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.handleChangeAvatar();
            }
        });
    }

    public void showError(String message) {
        errorLabel.setText(message);
    }

    public void showSuccess(String message) {
        errorLabel.setColor(Color.GREEN);
        errorLabel.setText(message);
    }

    public void updateAvatar(int avatarId) {
        avatarImage.setDrawable(new TextureRegionDrawable(new TextureRegion(
            new Texture(Gdx.files.internal("avatars/avatar_" + avatarId + ".png"))));
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float v) {

    }

    @Override
    public void resize(int i, int i1) {

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

    }

    private static class FieldGroup {
        Label label;
        TextField field;
    }
}
