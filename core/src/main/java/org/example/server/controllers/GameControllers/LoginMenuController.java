package org.example.server.controllers.GameControllers;

import org.example.client.views.LoginMenuScreen;
import org.example.utils.AssetManager;

public class LoginMenuController {
    private LoginMenuScreen screen;
    private int currentImageIndex = 0;
    private float timeSinceLastChange = 0;
    private static final float IMAGE_CHANGE_INTERVAL = 0.1f;

    public void setView(LoginMenuView view) {
        this.view = view;
    }

    public void update(float delta) {
        timeSinceLastChange += delta;
        if (timeSinceLastChange >= IMAGE_CHANGE_INTERVAL) {
            timeSinceLastChange = 0;
            currentImageIndex = (currentImageIndex + 1) % GameAssetManager.getGameAssetManager().getLoginMenuImagesCount();
            Texture newTexture = GameAssetManager.getGameAssetManager().getLoginMenuTexture(currentImageIndex);
            view.updateBackground(newTexture);
        }
    }

    public void handleLogin(String username, String password, boolean stayLoggedIn) {
        if (username.isEmpty() || password.isEmpty()) {
            view.showError("Username and password are required!");
            return;
        }

        User user = App.getUser(username);
        if (user == null) {
            view.showError("Username not found!");
            return;
        }

        if (!user.verifyPassword(password)) {
            view.showError("Incorrect password!");
            return;
        }

        // Set login status
        user.setStayLoggedIn(stayLoggedIn);
        App.setLoggedInUser(user);

        // Generate and save JWT token if stay logged in
        if (stayLoggedIn) {
            String token = JWTUtils.generateToken(username);
            user.setJwtToken(token);
            user.setTokenExpirationTime(JWTUtils.extractExpirationTime(token));
        }

        // Save user data
        App.addUser(user);

        // Proceed to main menu
        view.showError("Login successful!");
        Gdx.app.postRunnable(() -> {
            Main.getMain().getScreen().dispose();
            Main.getMain().setScreen(new MainMenuView(new MainMenuController(),
                GameAssetManager.getGameAssetManager().getSkin()));
        });
    }

    public void handleForgotPassword() {
        Main.getMain().getScreen().dispose();
        Main.getMain().setScreen(new ForgotPasswordMenuView(new ForgotPasswordMenuController(),
            GameAssetManager.getGameAssetManager().getSkin()));
    }

    public void handleBack() {
        Main.getMain().getScreen().dispose();
        Main.getMain().setScreen(new WelcomeMenuView(new WelcomeMenuController(),
            GameAssetManager.getGameAssetManager().getSkin()));
    }
}
