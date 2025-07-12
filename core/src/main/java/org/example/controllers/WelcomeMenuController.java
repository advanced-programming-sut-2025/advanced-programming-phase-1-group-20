package org.example.controllers;

import com.badlogic.gdx.graphics.Texture;
import org.example.utils.GameAssetManager;
import org.example.views.WelcomeMenuScreen;

public class WelcomeMenuController {
    private final WelcomeMenuScreen screen;
    private final GameAssetManager assetManager;

    private int currentImageIndex = 0;
    private float timeSinceLastChange = 0;
    private static final float IMAGE_CHANGE_INTERVAL = 0.1f;

    public WelcomeMenuController(WelcomeMenuScreen screen) {
        this.screen = screen;
        this.assetManager = GameAssetManager.getGameAssetManager();
    }

    public void update(float delta) {
        timeSinceLastChange += delta;
        if (timeSinceLastChange >= IMAGE_CHANGE_INTERVAL) {
            timeSinceLastChange = 0;
            currentImageIndex = (currentImageIndex + 1) % assetManager.getWelcomeMenuImagesCount();
            updateBackground();
        }
    }

    private void updateBackground() {
        Texture currentTexture = assetManager.getWelcomeMenuTexture(currentImageIndex);
        screen.updateBackground(currentTexture);
    }

    public void handleLoginButton() {
    }

    public void handleSignUpButton() {
    }
}
