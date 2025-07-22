package org.example.client.controllers;

import com.badlogic.gdx.graphics.Texture;
import org.example.client.views.*;
import org.example.common.models.App;
import org.example.server.controllers.GameControllers.LoginMenuController;
import org.example.utils.AssetManager;
import org.example.client.views.MainMenuScreen;

import static org.example.client.Main.getGame;

public class MainMenuController {
    private MainMenuScreen view;
    private int currentImageIndex = 0;
    private float timeSinceLastChange = 0;
    private static final float IMAGE_CHANGE_INTERVAL = 0.1f;

    public void setView(MainMenuScreen view) {
        this.view = view;
    }

    public void update(float delta) {
        timeSinceLastChange += delta;
        if (timeSinceLastChange >= IMAGE_CHANGE_INTERVAL) {
            timeSinceLastChange = 0;
            currentImageIndex = (currentImageIndex + 1) % AssetManager.getAssetManager().getMainMenuImagesCount();
            Texture newTexture = AssetManager.getAssetManager().getMainMenuTexture(currentImageIndex);
            view.updateBackground(newTexture);
        }
    }

    public void handleSinglePlayer() {
        getGame().getScreen().dispose();
//        getGame().setScreen(new SinglePlayerMenuScreen(new SinglePlayerMenuController(),
//            AssetManager.getAssetManager().getSkin()));
    }

    public void handleMultiPlayer() {
        getGame().getScreen().dispose();
//        getGame().setScreen(new MultiPlayerMenuScreen(new MultiPlayerMenuController(),
//            AssetManager.getAssetManager().getSkin()));
    }

    public void handleLoadGame() {
        getGame().getScreen().dispose();
//        getGame().setScreen(new LoadGameScreen(new LoadGameController(),
//            AssetManager.getAssetManager().getSkin()));
    }

    public void handleProfile() {
        getGame().getScreen().dispose();
        getGame().setScreen(new ProfileMenuScreen(new ProfileMenuController(),
            AssetManager.getAssetManager().getSkin()));
    }

    public void handleSettings() {
        getGame().getScreen().dispose();
//        getGame().setScreen(new SettingsScreen(new SettingsController(),
//            AssetManager.getAssetManager().getSkin()));
    }

    public void handleLogout() {
        App.logout();
        getGame().getScreen().dispose();
        getGame().setScreen(new LoginMenuScreen(new LoginMenuController(),
            AssetManager.getAssetManager().getSkin()));
    }
}
