package org.example.client.views.menu;

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
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import org.example.client.controllers.auth.ForgotPasswordMenuController;
import org.example.utils.AssetManager;

public class ForgotPasswordMenuScreen implements Screen {
    private final ForgotPasswordMenuController controller;
    private final Stage stage;
    private final Skin skin;
    private Image background;
    private TextField usernameField, answerField, newPasswordField, confirmPasswordField;
    private Label questionLabel, passwordLabel, errorLabel, newPasswordLabel, confirmPasswordLabel;
    private ImageButton checkButton, getPasswordButton, resetPasswordButton, backButton;

    public ForgotPasswordMenuScreen(ForgotPasswordMenuController controller, Skin skin) {
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
        mainTable.center().padTop(100);

        Label titleLabel = new Label("FORGOT PASSWORD", skin);
        titleLabel.setFontScale(1.8f);
        titleLabel.setColor(Color.ROYAL);
        mainTable.add(titleLabel).colspan(2).padBottom(40).row();

        usernameField = createTextField("Username");
        checkButton = createImageButton(AssetManager.getAssetManager().getChangeTitleTexture());

        Table usernameRow = new Table();
        usernameRow.add(new Label("Username:", skin)).padRight(20).right();
        usernameRow.add(usernameField).width(300).height(50).padRight(10);
        usernameRow.add(checkButton);
        mainTable.add(usernameRow).center().padBottom(20).row();

        questionLabel = new Label("", skin);
        questionLabel.setFontScale(1.2f);
        mainTable.add(questionLabel).center().padBottom(20).row();

        answerField = createTextField("Answer");
        getPasswordButton = createImageButton(AssetManager.getAssetManager().getRegisterTitleTexture());

        Table answerRow = new Table();
        answerRow.add(new Label("Answer:", skin)).padRight(20).right();
        answerRow.add(answerField).width(300).height(50).padRight(10);
        answerRow.add(getPasswordButton);
        mainTable.add(answerRow).center().padBottom(20).row();

        // New password fields (initially hidden)
        newPasswordField = createPasswordField("New Password");
        newPasswordField.setVisible(false);
        newPasswordLabel = new Label("New Password:", skin);
        newPasswordLabel.setVisible(false);
        mainTable.add(newPasswordLabel).padRight(20).right();
        mainTable.add(newPasswordField).width(300).height(50).padBottom(15).row();

        confirmPasswordField = createPasswordField("Confirm New Password");
        confirmPasswordField.setVisible(false);
        confirmPasswordLabel = new Label("Confirm Password:", skin);
        confirmPasswordLabel.setVisible(false);
        mainTable.add(confirmPasswordLabel).padRight(20).right();
        mainTable.add(confirmPasswordField).width(300).height(50).padBottom(15).row();

        resetPasswordButton = createImageButton(AssetManager.getAssetManager().getRegisterTitleTexture());
        resetPasswordButton.setVisible(false);
        mainTable.add(resetPasswordButton).center().padBottom(20).row();

        passwordLabel = new Label("", skin);
        passwordLabel.setFontScale(1.5f);
        mainTable.add(passwordLabel).center().padBottom(20).row();

        errorLabel = new Label("", skin);
        errorLabel.setColor(Color.RED);
        mainTable.add(errorLabel).width(400).center().padBottom(30).row();

        backButton = createImageButton(AssetManager.getAssetManager().getBackTitleTexture());
        mainTable.add(backButton).center().padTop(20);

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
        TextField field = new TextField("", skin);
        field.setMessageText(hint);
        field.setPasswordMode(true);
        field.setPasswordCharacter('•');
        return field;
    }

    private void addListeners() {
        checkButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.handleCheckUsername(usernameField.getText());
            }
        });

        getPasswordButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.handleCheckAnswer(usernameField.getText(), answerField.getText());
            }
        });

        resetPasswordButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.handleResetPassword(usernameField.getText(), newPasswordField.getText(), confirmPasswordField.getText());
            }
        });

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.handleBackButton();
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

    public void showQuestion(String question) {
        questionLabel.setText(question);
        errorLabel.setText("");
    }

    public void showPassword(String password) {
        passwordLabel.setText("Your Password: " + password);
        errorLabel.setText("");
    }

    public void showPasswordResetFields() {
        newPasswordField.setVisible(true);
        confirmPasswordField.setVisible(true);
        newPasswordLabel.setVisible(true);
        confirmPasswordLabel.setVisible(true);
        resetPasswordButton.setVisible(true);
        passwordLabel.setText("Please enter your new password:");
        errorLabel.setText("");
    }

    public void hidePasswordResetFields() {
        newPasswordField.setVisible(false);
        confirmPasswordField.setVisible(false);
        newPasswordLabel.setVisible(false);
        confirmPasswordLabel.setVisible(false);
        resetPasswordButton.setVisible(false);
        passwordLabel.setText("");
        // Clear the password fields
        newPasswordField.setText("");
        confirmPasswordField.setText("");
    }

    public void clearAllFields() {
        usernameField.setText("");
        answerField.setText("");
        newPasswordField.setText("");
        confirmPasswordField.setText("");
        questionLabel.setText("");
        passwordLabel.setText("");
        errorLabel.setText("");
        hidePasswordResetFields();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        controller.update(delta);
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
    }
}
