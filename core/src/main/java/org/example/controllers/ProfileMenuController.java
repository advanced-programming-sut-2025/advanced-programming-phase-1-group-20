package org.example.controllers;

import org.example.models.entities.User;
import org.example.utils.AssetManager;
import org.example.views.ProfileMenuView;

public class ProfileMenuController {
    private ProfileMenuView view;
    private int currentImageIndex = 0;
    private float timeSinceLastChange = 0;
    private static final float IMAGE_CHANGE_INTERVAL = 0.1f;

    public void setView(ProfileMenuView view) {
        this.view = view;
    }

    public void update(float delta) {
        timeSinceLastChange += delta;
        if (timeSinceLastChange >= IMAGE_CHANGE_INTERVAL) {
            timeSinceLastChange = 0;
            currentImageIndex = (currentImageIndex + 1) % AssetManager.getAssetManager().getProfileMenuImagesCount();
            view.updateBackground(AssetManager.getAssetManager().getProfileMenuTexture(currentImageIndex));
        }
    }

    public void handleSaveChanges(String username, String password, String email, String nickname) {
        User user = App.currentUser;
        boolean changed = false;

        if (!username.equals(user.getUsername())) {
            if (username.isEmpty()) {
                view.showError("Username cannot be empty");
                return;
            }
            if (App.userExists(username)) {
                view.showError("Username already taken");
                return;
            }
            user.setUsername(username);
            changed = true;
        }

        if (!password.isEmpty()) {
            if (password.length() < 8) {
                view.showError("Password must be at least 8 characters");
                return;
            }
            user.setPassword(password);
            changed = true;
        }

        if (!email.equals(user.getEmail())) {
            if (!isValidEmail(email)) {
                view.showError("Invalid email format");
                return;
            }
            user.setEmail(email);
            changed = true;
        }

        if (!nickname.equals(user.getNickname())) {
            user.setNickname(nickname);
            changed = true;
        }

        if (changed) {
            App.updateUser(user);
            view.showSuccess("Changes saved successfully!");
        } else {
            view.showError("No changes detected");
        }
    }

    public void handleBackButton() {
        Main.getMain().getScreen().dispose();
        Main.getMain().setScreen(new MainMenuView(new MainMenuController()));
    }

    public void handleChangeAvatar() {
        Main.getMain().getScreen().dispose();
        Main.getMain().setScreen(new ChooseAvatarMenuView(new ChooseAvatarMenuController()));
    }

    private boolean isValidEmail(String email) {
        String regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return email.matches(regex);
    }
}
