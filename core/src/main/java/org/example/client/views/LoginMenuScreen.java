package org.example.client.views;

import com.badlogic.gdx.Screen;

public class LoginMenuScreen implements Screen {
//    private final LoginMenuController controller;
//    private final Stage stage;
//    private Image background;
//    private TextField usernameField, passwordField;
//    private Label errorLabel;
//    private CheckBox stayLoggedInCheckbox;
//    private Skin skin;
//
//    public LoginMenuScreen(LoginMenuController controller, Skin skin) {
//        this.controller = controller;
//        this.stage = new Stage(new ScreenViewport());
//        this.controller.setView(this);
//        this.skin = skin;
//        setupUI();
//    }
//
//    private void setupUI() {
//        background = new Image();
//        background.setFillParent(true);
//        stage.addActor(background);
//
//        Table mainTable = new Table();
//        mainTable.setFillParent(true);
//        mainTable.top().padTop(100);
//
//        Label title = new Label("LOGIN", skin, "title");
//        title.setFontScale(1.8f);
//        mainTable.add(title).colspan(2).padBottom(50).row();
//
//        usernameField = createTextField("Username");
//        mainTable.add(new Label("Username:", skin)).padRight(20).right();
//        mainTable.add(usernameField).width(400).height(60).padBottom(15).row();
//
//        passwordField = createPasswordField("Password");
//        mainTable.add(new Label("Password:", skin)).padRight(20).right();
//        mainTable.add(passwordField).width(400).height(60).padBottom(15).row();
//
//        stayLoggedInCheckbox = new CheckBox(" Stay Logged In", skin);
//        mainTable.add(new Label("", skin)).padRight(20).right();
//        mainTable.add(stayLoggedInCheckbox).left().width(400).height(60).padBottom(15).row();
//
//        errorLabel = new Label("", skin);
//        errorLabel.setColor(Color.RED);
//        mainTable.add(errorLabel).colspan(2).width(600).padBottom(20).row();
//
//        TextButton loginButton = new TextButton("LOGIN", skin);
//        TextButton forgotButton = new TextButton("FORGOT PASSWORD", skin);
//        TextButton backButton = new TextButton("BACK", skin);
//
//        Table buttonsTable = new Table();
//        buttonsTable.add(backButton).pad(10);
//        buttonsTable.add(forgotButton).pad(10);
//        buttonsTable.add(loginButton).pad(10);
//
//        mainTable.add(buttonsTable).colspan(2).padTop(20);
//
//        stage.addActor(mainTable);
//        addListeners();
//    }
//
//    private TextField createTextField(String hint) {
//        TextField field = new TextField("", skin);
//        field.setMessageText(hint);
//        return field;
//    }
//
//    private TextField createPasswordField(String hint) {
//        TextField field = createTextField(hint);
//        field.setPasswordMode(true);
//        field.setPasswordCharacter('•');
//        return field;
//    }
//
//    private void addListeners() {
//        loginButton.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                boolean success = controller.authenticateUser(
//                    usernameField.getText(),
//                    passwordField.getText(),
//                    stayLoggedInCheckbox.isChecked()
//                );
//
//                if (success) {
//                    GameAssetManager.getAssetManager().playClickSound();
//                    controller.handleLoginSuccess();
//                }
//            }
//        });
//
//        forgotButton.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                GameAssetManager.getAssetManager().playClickSound();
//                controller.handleForgotPassword();
//            }
//        });
//
//        backButton.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                GameAssetManager.getAssetManager().playClickSound();
//                controller.handleBack();
//            }
//        });
//    }
//
//    public void updateBackground(Texture texture) {
//        background.setDrawable(new TextureRegionDrawable(new TextureRegion(texture)));
//    }
//
//    public void showError(String error) {
//        errorLabel.setText(error);
//    }

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
}
