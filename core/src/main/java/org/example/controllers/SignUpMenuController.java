package org.example.controllers;

import org.example.models.entities.User;
import org.example.utils.GameAssetManager;
import org.example.views.SignUpMenuScreen;
import java.util.Random;

public class SignUpMenuController {
    private SignUpMenuScreen screen;
    private int currentImageIndex = 0;
    private float timeSinceLastChange = 0;
    private static final float IMAGE_CHANGE_INTERVAL = 0.1f;

    public void setView(SignUpMenuScreen screen) {
        this.screen = screen;
    }

    public void update(float delta) {
        timeSinceLastChange += delta;
        if (timeSinceLastChange >= IMAGE_CHANGE_INTERVAL) {
            timeSinceLastChange = 0;
            currentImageIndex = (currentImageIndex + 1) % GameAssetManager.getGameAssetManager().getSignUpMenuImagesCount();
            screen.updateBackground(GameAssetManager.getGameAssetManager().getSignUpMenuTexture(currentImageIndex));
        }
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

    public boolean validateUsername(String username) {
        if (username.isEmpty()) {
            screen.showError("Username cannot be empty");
            return false;
        }

        if (!username.matches("^[a-zA-Z0-9-]+$")) {
            screen.showError("Username can only contain letters, numbers and hyphens");
            return false;
        }

//        if (App.userExists(username)) {
//            String suggested = username + new Random().nextInt(1000);
//            screen.showError("Username exists. Try: " + suggested);
//            return false;
//        }

        return true;
    }

    public boolean validateEmail(String email) {
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        if (!email.matches(regex)) {
            screen.showError("Invalid email format");
            return false;
        }
        return true;
    }

    public boolean validatePassword(String password) {
        if (password.length() < 8) {
            screen.showError("Password must be at least 8 characters");
            return false;
        }

        if (!password.matches(".*[A-Z].*")) {
            screen.showError("Password needs at least one uppercase letter");
            return false;
        }

        if (!password.matches(".*[a-z].*")) {
            screen.showError("Password needs at least one lowercase letter");
            return false;
        }

        if (!password.matches(".*\\d.*")) {
            screen.showError("Password needs at least one digit");
            return false;
        }

        if (!password.matches(".*[!@#$%^&*()_+].*")) {
            screen.showError("Password needs at least one special character");
            return false;
        }

        return true;
    }

    public boolean registerUser(String username, String email, String password,
                                String confirmPassword, String gender,
                                String securityQuestion, String securityAnswer) {

        if (!validateUsername(username)) return false;
        if (!validateEmail(email)) return false;
        if (!validatePassword(password)) return false;

        if (!password.equals(confirmPassword)) {
            screen.showError("Passwords don't match");
            return false;
        }

        if (securityAnswer.isEmpty()) {
            screen.showError("Security answer is required");
            return false;
        }

//        User user = new User(username, password, email, gender,
//            securityQuestion, securityAnswer);
//        App.registerUser(user);
        return true;
    }

    public String[] getSecurityQuestions() {
        return new String[] {
            "What was your first pet's name?",
            "What city were you born in?",
            "What is your mother's maiden name?",
            "What was the name of your first school?",
            "What was your favorite childhood toy?"
        };
    }

}
