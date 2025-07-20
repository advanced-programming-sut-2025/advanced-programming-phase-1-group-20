package org.example.server.controllers;

import org.example.client.views.LoginMenuScreen;
import org.example.utils.AssetManager;

public class LoginMenuController {
    private LoginMenuScreen screen;
    private int currentImageIndex = 0;
    private float timeSinceLastChange = 0;
    private static final float IMAGE_CHANGE_INTERVAL = 0.1f;

    public void setView(LoginMenuScreen screen) {
        this.screen = screen;
    }

    public void update(float delta) {
        timeSinceLastChange += delta;
        if (timeSinceLastChange >= IMAGE_CHANGE_INTERVAL) {
            timeSinceLastChange = 0;
            currentImageIndex = (currentImageIndex + 1) % AssetManager.getAssetManager().getLoginMenuImagesCount();
//            screen.updateBackground(GameAssetManager.getAssetManager().getLoginMenuTexture(currentImageIndex));
        }
    }

    public boolean authenticateUser(String username, String password, boolean stayLoggedIn) {
        if (username.isEmpty() || password.isEmpty()) {
//            screen.showError("Username and password are required");
            return false;
        }

//        User user = App.getUserByUsername(username);
//        if (user == null) {
//            screen.showError("Username not found");
//            return false;
//        }
//
//        if (!user.getPassword().equals(password)) {
//            screen.showError("Incorrect password");
//            return false;
//        }

//        App.setCurrentUser(user);

//        if (stayLoggedIn) {
//            Preferences prefs = Gdx.app.getPreferences("MyGameSettings");
//            prefs.putString("lastUsername", username);
//            prefs.putString("lastPassword", password);
//            prefs.flush();
//        }

        return true;
    }

//    public void handleLoginSuccess() {
//        Main.getMain().getScreen().dispose();
//        Main.getMain().setScreen(new MainMenuView(new MainMenuController()));
//    }
//
//    public void handleForgotPassword() {
//        Main.getMain().getScreen().dispose();
//        Main.getMain().setScreen(new ForgotPasswordMenuView(new ForgotPasswordMenuController()));
//    }
//
//    public void handleBack() {
//        Main.getMain().getScreen().dispose();
//        Main.getMain().setScreen(new WelcomeMenuScreen(new WelcomeMenuController()));
//    }

//    public void checkSavedCredentials() {
//        Preferences prefs = Gdx.app.getPreferences("MyGameSettings");
//        String username = prefs.getString("lastUsername", "");
//        String password = prefs.getString("lastPassword", "");
//
//        if (!username.isEmpty() && !password.isEmpty()) {
//            if (authenticateUser(username, password, true)) {
//                handleLoginSuccess();
//            }
//        }
//    }
}
