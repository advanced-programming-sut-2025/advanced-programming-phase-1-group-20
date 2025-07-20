package org.example.server.controllers.GameControllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import org.example.client.controllers.WelcomeMenuController;
import org.example.client.views.WelcomeMenuScreen;
import org.example.common.models.App;
import org.example.common.models.entities.User;
import org.example.utils.AssetManager;
import org.example.views.ProfileMenuScreen;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

import static org.example.client.Main.getGame;

public class ProfileMenuController {
    private ProfileMenuScreen screen;
    private int currentImageIndex = 0;
    private float timeSinceLastChange = 0;
    private static final float IMAGE_CHANGE_INTERVAL = 0.1f;

    public void setView(ProfileMenuScreen screen) {
        this.screen = screen;
    }

    public void update(float delta) {
        timeSinceLastChange += delta;
        if (timeSinceLastChange >= IMAGE_CHANGE_INTERVAL) {
            timeSinceLastChange = 0;
            currentImageIndex = (currentImageIndex + 1) % AssetManager.getAssetManager().getProfileMenuImagesCount();
            Texture newTexture = AssetManager.getAssetManager().getProfileMenuTexture(currentImageIndex);
            screen.updateBackground(newTexture);
        }
    }

    public void handleSaveChanges(String username, String password,
                                  String email, String nickname,
                                  boolean stayLoggedIn) {
        User user = App.getLoggedInUser();
        boolean changed = false;

        if (!username.equals(user.getUsername())) {
            if (username.isEmpty()) {
                screen.showError("Username cannot be empty");
                return;
            }
            if (App.getUser(username) != null && !username.equals(user.getUsername())) {
                screen.showError("Username already taken");
                return;
            }
            user.setUsername(username);
            changed = true;
        }

        if (!password.isEmpty()) {
            if (password.length() < 8) {
                screen.showError("Password must be at least 8 characters");
                return;
            }
            if (!password.matches(".*[A-Z].*") || !password.matches(".*\\d.*") ||
                !password.matches(".*[!@#$%^&*()_+].*")) {
                screen.showError("Password must contain uppercase, number and special char");
                return;
            }
            user.setPassword(password);
            changed = true;
        }

        if (!email.equals(user.getEmail())) {
            if (!isValidEmail(email)) {
                screen.showError("Invalid email format");
                return;
            }
            user.setEmail(email);
            changed = true;
        }

        if (!nickname.equals(user.getNickname())) {
            user.setNickname(nickname);
            changed = true;
        }

        if (stayLoggedIn != user.isStayLoggedIn()) {
            user.setStayLoggedIn(stayLoggedIn);
            changed = true;
        }

        if (changed) {
            App.addUser(user); // This will save the changes
            screen.showSuccess("Changes saved successfully!");
        }
        else {
            screen.showError("No changes detected");
        }
    }

    public void handleBackButton() {
        getGame().getScreen().dispose();
//        getGame().setScreen(new MainMenuView(new MainMenuController(),
//            AssetManager.getAssetManager().getSkin()));
    }

    public void handleDeleteAccount() {
        User currentUser = App.getLoggedInUser();

//        Dialog dialog = new Dialog("Confirm Delete", AssetManager.getAssetManager().getSkin(), "dialog");
//        dialog.text("Are you sure you want to delete your account?\nAll your data will be lost!");

        TextButton cancelButton = new TextButton("Cancel", skin);
        TextButton confirmButton = new TextButton("Delete", skin);

        dialog.button(cancelButton, false);
        dialog.button(confirmButton, true);

        dialog.show(screen.getStage());

        dialog.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (dialog.getResult() != null && (boolean)dialog.getResult()) {
                    App.removeUser(currentUser);
                    App.logout();
                    screen.showSuccess("Account deleted successfully");

                    Gdx.app.postRunnable(() -> {
                        getGame().getScreen().dispose();
                        getGame().setScreen(new WelcomeMenuScreen(new WelcomeMenuController(),
                            AssetManager.getAssetManager().getSkin()));
                    });
                }
            }
        });
    }

    private boolean isValidEmail(String email) {
        String regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return email.matches(regex);
    }
}
