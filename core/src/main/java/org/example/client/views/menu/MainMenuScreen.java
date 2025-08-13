package org.example.client.views.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.client.Main;
import org.example.client.controllers.menu.MainMenuController;
import org.example.common.models.App;
import org.example.utils.AssetManager;
import com.badlogic.gdx.graphics.Color;
import java.util.List;

public class MainMenuScreen implements Screen {
    private final MainMenuController controller;
    private final Stage stage;
    private Image background;
    private Skin skin;

    private ImageButton singlePlayerBtn;
    private ImageButton multiPlayerBtn;
    private ImageButton loadGameBtn;
    private ImageButton profileBtn;
    private ImageButton settingsBtn;
    private ImageButton logoutBtn;
    private Label errorLabel;

    public MainMenuScreen(MainMenuController controller, Skin skin) {
        this.controller = controller;
        this.stage = new Stage(new ScreenViewport());
        this.skin = skin;
        this.controller.setView(this);
        setupUI();
    }

    private void setupUI() {
        background = new Image();
        background.setFillParent(true);
        stage.addActor(background);

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center();

        Label title = new Label("MAIN MENU", skin);
        title.setFontScale(2.0f);
        title.setColor(Color.GOLD);
        mainTable.add(title).colspan(2).padBottom(50).row();

        singlePlayerBtn = createImageButton(AssetManager.getAssetManager().getSinglePlayerTitleTexture());
        multiPlayerBtn = createImageButton(AssetManager.getAssetManager().getMultiPlayerTitleTexture());
        loadGameBtn = createImageButton(AssetManager.getAssetManager().getLoadTitleTexture());
        profileBtn = createImageButton(AssetManager.getAssetManager().getProfileMenuTitleTexture());
        settingsBtn = createImageButton(AssetManager.getAssetManager().getSettingMenuTitleTexture());
        logoutBtn = createImageButton(AssetManager.getAssetManager().getLogoutTitleTexture());

        mainTable.add(singlePlayerBtn).pad(15).width(300).height(60).row();
        mainTable.add(multiPlayerBtn).pad(15).width(300).height(60).row();
        mainTable.add(loadGameBtn).pad(15).width(300).height(60).row();
        mainTable.add(profileBtn).pad(15).width(300).height(60).row();
        mainTable.add(settingsBtn).pad(15).width(300).height(60).row();
        mainTable.add(logoutBtn).pad(15).width(300).height(60).row();

        errorLabel = new Label("", skin);
        errorLabel.setColor(Color.RED);
        mainTable.add(errorLabel).colspan(2).padTop(30).row();

        stage.addActor(mainTable);
        addListeners();
    }

    private ImageButton createImageButton(Texture texture) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = new TextureRegionDrawable(new TextureRegion(texture));
        style.imageDown = new TextureRegionDrawable(new TextureRegion(texture));
        return new ImageButton(style);
    }

    private void addListeners() {
        singlePlayerBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.handleSinglePlayer();
            }
        });

        multiPlayerBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.handleMultiPlayer();
            }
        });

        loadGameBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(App.getLoggedInUser() != null) {
                    controller.handleLoadGame();
                }
            }
        });

        profileBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.handleProfile();
            }
        });

        settingsBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.handleSettings();
            }
        });

        logoutBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.handleLogout();
            }
        });
    }

    public void updateBackground(Texture texture) {
        if (texture != null) {
            background.setDrawable(new TextureRegionDrawable(new TextureRegion(texture)));
        }
    }

    public void showError(String message) {
        errorLabel.setText(message);
    }

    public void updateOnlinePlayersList(List<Object> players) {
        // Stub method - online players functionality moved to multiplayer menu
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
    }
}
