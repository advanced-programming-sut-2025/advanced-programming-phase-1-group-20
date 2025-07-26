package org.example.client.controllers.auth;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
//import org.example.client.controllers.WelcomeMenuController;
import org.example.client.controllers.WelcomeMenuController;
import org.example.client.views.WelcomeMenuScreen;
import org.example.client.views.menu.LoginMenuScreen;
import org.example.common.models.App;
import org.example.common.models.entities.User;
import org.example.utils.AssetManager;
import org.example.utils.auth.JWTUtils;

import static org.example.client.Main.getGame;

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
            Texture newTexture = AssetManager.getAssetManager().getLoginMenuTexture(currentImageIndex);
            screen.updateBackground(newTexture);
        }
    }

    public void handleLogin(String username, String password, boolean stayLoggedIn) {
        if (username.isEmpty() || password.isEmpty()) {
            screen.showError("Username and password are required!");
            return;
        }

        User user = App.getUser(username);
        if (user == null) {
            screen.showError("Username not found!");
            return;
        }

        if (!user.verifyPassword(password)) {
            screen.showError("Incorrect password!");
            return;
        }

        user.setStayLoggedIn(stayLoggedIn);
        App.setLoggedInUser(user);

        if (stayLoggedIn) {
            String token = JWTUtils.generateToken(username);
            user.setJwtToken(token);
            user.setTokenExpirationTime(JWTUtils.extractExpirationTime(token));
        }

        App.addUser(user);

        screen.showError("Login successful!");
        Gdx.app.postRunnable(() -> {
            getGame().getScreen().dispose();
//            getGame().setScreen(new MainMenuView(new MainMenuController(),
//                AssetManager.getAssetManager().getSkin()));
        });
    }

    public void handleForgotPassword() {
        getGame().getScreen().dispose();
//        getGame().setScreen(new ForgotPasswordMenuScreen(new ForgotPasswordMenuController(),
//            AssetManager.getAssetManager().getSkin()));
    }

    public void handleBack() {
        getGame().getScreen().dispose();
        getGame().setScreen(new WelcomeMenuScreen(new WelcomeMenuController(),
            AssetManager.getAssetManager().getSkin()));
    }
}
