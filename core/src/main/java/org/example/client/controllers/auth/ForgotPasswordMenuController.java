package org.example.client.controllers.auth;

import com.badlogic.gdx.Gdx;
import org.example.client.views.WelcomeMenuScreen;
import org.example.client.views.menu.ForgotPasswordMenuScreen;
import org.example.common.models.App;
import org.example.common.models.entities.User;
import org.example.utils.AssetManager;

public class ForgotPasswordMenuController {
    private ForgotPasswordMenuScreen view;

    public void setView(ForgotPasswordMenuScreen view) {
        this.view = view;
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
            view.showPassword(user.getPassword());
        }
    }

    public void handleBackButton() {
        Gdx.app.postRunnable(() -> {
            view.dispose();
            new WelcomeMenuScreen(new org.example.client.controllers.WelcomeMenuController(), AssetManager.getAssetManager().getSkin());
        });
    }
}
