package org.example.client.controllers.auth;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import org.example.client.views.menu.WelcomeMenuScreen;
import org.example.client.views.menu.ForgotPasswordMenuScreen;
import org.example.common.models.App;
import org.example.common.models.entities.User;
import org.example.utils.AssetManager;

public class ForgotPasswordMenuController {
    private ForgotPasswordMenuScreen view;
    private int currentImageIndex = 0;
    private float timeSinceLastChange = 0;
    private static final float IMAGE_CHANGE_INTERVAL = 0.1f;

    public void setView(ForgotPasswordMenuScreen view) {
        this.view = view;
    }

    public void update(float delta) {
        timeSinceLastChange += delta;
        if (timeSinceLastChange >= IMAGE_CHANGE_INTERVAL) {
            timeSinceLastChange = 0;
            currentImageIndex = (currentImageIndex + 1) % AssetManager.getAssetManager().getLoginMenuImagesCount();
            Texture newTexture = AssetManager.getAssetManager().getLoginMenuTexture(currentImageIndex);
            if (newTexture != null) {
                view.updateBackground(newTexture);
            } else {
                System.err.println("Failed to load login menu texture at index: " + currentImageIndex);
            }
        }
    }

    public void handleCheckUsername(String username) {
        if (username == null || username.isEmpty()) {
            view.showError("Username cannot be empty");
            return;
        }

        User user = App.getUser(username);
        if (user == null) {
            view.showError("Username does not exist");
        }
        else {
            String question = App.getSecurityQuestion(user.getSecurityQuestionIndex());
            view.showQuestion(question);
        }
    }

    public void handleCheckAnswer(String username, String answer) {
        if (answer == null || answer.isEmpty()) {
            view.showError("Answer cannot be empty");
            return;
        }

        User user = App.getUser(username);
        if (user == null) {
            view.showError("User not found");
            return;
        }

        if (!user.getSecurityAnswer().equalsIgnoreCase(answer.trim())) {
            view.showError("Incorrect answer");
        }
        else {
            view.showPasswordResetFields();
        }
    }

    public void handleResetPassword(String username, String newPassword, String confirmPassword) {
        if (newPassword == null || newPassword.isEmpty()) {
            view.showError("New password cannot be empty");
            return;
        }

        if (confirmPassword == null || confirmPassword.isEmpty()) {
            view.showError("Please confirm your password");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            view.showError("Passwords do not match");
            return;
        }

        if (newPassword.length() < 6) {
            view.showError("Password must be at least 6 characters long");
            return;
        }

        User user = App.getUser(username);
        if (user == null) {
            view.showError("User not found");
            return;
        }

        // Update the user's password
        user.setPassword(newPassword);
        App.addUser(user); // Save the updated user

        view.showSuccess("Password updated successfully! You can now login with your new password.");
        
        // Clear all fields
        view.clearAllFields();
    }

    public void handleBackButton() {
        Gdx.app.postRunnable(() -> {
            view.dispose();
            org.example.client.Main.getGame().setScreen(new WelcomeMenuScreen(new org.example.client.controllers.WelcomeMenuController(), AssetManager.getAssetManager().getSkin()));
        });
    }
}
