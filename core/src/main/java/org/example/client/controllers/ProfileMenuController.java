package org.example.client.controllers;

import org.example.views.ProfileMenuScreen;

public class ProfileMenuController {
    private ProfileMenuScreen screen;
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
            currentImageIndex = (currentImageIndex + 1) % GameAssetManager.getGameAssetManager().getProfileMenuImagesCount();
            Texture newTexture = GameAssetManager.getGameAssetManager().getProfileMenuTexture(currentImageIndex);
            view.updateBackground(newTexture);
        }
    }

    public void handleSaveChanges(String username, String password,
                                  String email, String nickname,
                                  boolean stayLoggedIn) {
        User user = App.getLoggedInUser();
        boolean changed = false;

        // Validate and update username
        if (!username.equals(user.getUsername())) {
            if (username.isEmpty()) {
                view.showError("Username cannot be empty");
                return;
            }
            if (App.getUser(username) != null && !username.equals(user.getUsername())) {
                view.showError("Username already taken");
                return;
            }
            user.setUsername(username);
            changed = true;
        }

        // Validate and update password
        if (!password.isEmpty()) {
            if (password.length() < 8) {
                view.showError("Password must be at least 8 characters");
                return;
            }
            if (!password.matches(".*[A-Z].*") || !password.matches(".*\\d.*") ||
                !password.matches(".*[!@#$%^&*()_+].*")) {
                view.showError("Password must contain uppercase, number and special char");
                return;
            }
            user.setPassword(password);
            changed = true;
        }

        // Validate and update email
        if (!email.equals(user.getEmail())) {
            if (!isValidEmail(email)) {
                view.showError("Invalid email format");
                return;
            }
            user.setEmail(email);
            changed = true;
        }

        // Update nickname
        if (!nickname.equals(user.getNickname())) {
            user.setNickname(nickname);
            changed = true;
        }

        // Update stay logged in preference
        if (stayLoggedIn != user.isStayLoggedIn()) {
            user.setStayLoggedIn(stayLoggedIn);
            changed = true;
        }

        if (changed) {
            App.addUser(user); // This will save the changes
            view.showSuccess("Changes saved successfully!");
        } else {
            view.showError("No changes detected");
        }
    }

    public void handleBackButton() {
        Main.getMain().getScreen().dispose();
        Main.getMain().setScreen(new MainMenuView(new MainMenuController(),
            GameAssetManager.getGameAssetManager().getSkin()));
    }

    public void handleDeleteAccount() {
        User currentUser = App.getLoggedInUser();

        // Create confirmation dialog
        Dialog dialog = new Dialog("Confirm Delete", GameAssetManager.getGameAssetManager().getSkin(), "dialog");
        dialog.text("Are you sure you want to delete your account?\nAll your data will be lost!");

        TextButton cancelButton = new TextButton("Cancel", skin);
        TextButton confirmButton = new TextButton("Delete", skin);

        dialog.button(cancelButton, false);
        dialog.button(confirmButton, true);

        dialog.show(view.getStage());

        dialog.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (dialog.getResult() != null && (boolean)dialog.getResult()) {
                    // User confirmed deletion
                    App.removeUser(currentUser);
                    App.logout();
                    view.showSuccess("Account deleted successfully");

                    // Return to welcome menu after short delay
                    Gdx.app.postRunnable(() -> {
                        Main.getMain().getScreen().dispose();
                        Main.getMain().setScreen(new WelcomeMenuView(new WelcomeMenuController(),
                            GameAssetManager.getGameAssetManager().getSkin()));
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
