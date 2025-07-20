package org.example.client.controllers;


//expired
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import org.example.client.views.SignUpMenuScreen;
import org.example.client.views.WelcomeMenuScreen;
import org.example.common.models.App;
import org.example.common.models.entities.User;
import org.example.utils.AssetManager;

import java.util.Random;

import static org.example.client.Main.getGame;

public class SignUpMenuController {
    private SignUpMenuScreen screen;
    private int currentImageIndex = 0;
    private float timeSinceLastChange = 0;
    private static final float IMAGE_CHANGE_INTERVAL = 0.1f;

    private final String[] securityQuestions = {
        "What was your first pet's name?",
        "What city were you born in?",
        "What is your mother's maiden name?",
        "What was the name of your first school?",
        "What was your favorite childhood toy?"
    };

    public void setView(SignUpMenuScreen screen) {
        this.screen = screen;
    }

    public void update(float delta) {
        timeSinceLastChange += delta;
        if (timeSinceLastChange >= IMAGE_CHANGE_INTERVAL) {
            timeSinceLastChange = 0;
            currentImageIndex = (currentImageIndex + 1) % AssetManager.getAssetManager().getSignUpMenuImagesCount();
            Texture newTexture = AssetManager.getAssetManager().getSignUpMenuTexture(currentImageIndex);
            screen.updateBackground(newTexture);
        }
    }

    public String[] getSecurityQuestions() {
        return securityQuestions;
    }

    public String generateRandomPassword() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "!@#$%^&*()_+";

        String all = upper + lower + digits + special;
        Random random = new Random();

        StringBuilder password = new StringBuilder();
        password.append(upper.charAt(random.nextInt(upper.length())));
        password.append(lower.charAt(random.nextInt(lower.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(special.charAt(random.nextInt(special.length())));

        for (int i = 0; i < 4; i++) {
            password.append(all.charAt(random.nextInt(all.length())));
        }

        char[] chars = password.toString().toCharArray();
        for (int i = 0; i < chars.length; i++) {
            int j = random.nextInt(chars.length);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }

        return new String(chars);
    }

    public void handleRegister(String username, String email, String password,
                               String confirmPassword, String gender,
                               String securityQuestion, String securityAnswer) {

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() ||
            confirmPassword.isEmpty() || securityAnswer.isEmpty()) {
            screen.showError("All fields are required!");
            return;
        }

        if (!password.equals(confirmPassword)) {
            screen.showError("Passwords don't match!");
            return;
        }

        if (password.length() < 8) {
            screen.showError("Password must be at least 8 characters");
            return;
        }

        if (!password.matches(".*[A-Z].*")) {
            screen.showError("Password needs at least one uppercase letter");
            return;
        }

        if (!password.matches(".*\\d.*")) {
            screen.showError("Password needs at least one digit");
            return;
        }

        if (!password.matches(".*[!@#$%^&*()_+].*")) {
            screen.showError("Password needs at least one special character");
            return;
        }

        if (!isValidEmail(email)) {
            screen.showError("Invalid email format");
            return;
        }

        if (App.getUser(username) != null) {
            screen.showError("Username already exists!");
            return;
        }

        User.Gender genderEnum = User.Gender.valueOf(gender.toUpperCase());
        User newUser = new User(username, password, email, "", genderEnum);
        newUser.setSecurityQuestion(getQuestionIndex(securityQuestion));
        newUser.setSecurityAnswer(securityAnswer);

        //handle it!!

        App.addUser(newUser);
        App.setLoggedInUser(newUser);

        screen.showError("Registration successful!");
        Gdx.app.postRunnable(() -> {
            getGame().getScreen().dispose();
//            getGame().setScreen(new MainMenuView(new MainMenuController(),
//                AssetManager.getAssetManager().getSkin()));
        });
    }

    public void handleBack() {
        getGame().getScreen().dispose();
        getGame().setScreen(new WelcomeMenuScreen(new WelcomeMenuController(),
            AssetManager.getAssetManager().getSkin()));
    }

    private boolean isValidEmail(String email) {
        String regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return email.matches(regex);
    }

    private int getQuestionIndex(String question) {
        for (int i = 0; i < securityQuestions.length; i++) {
            if (securityQuestions[i].equals(question)) {
                return i;
            }
        }
        return 0;
    }
}
