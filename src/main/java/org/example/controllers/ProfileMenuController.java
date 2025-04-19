package org.example.controllers;

import models.App;
import models.Result;
import models.User;
import models.enums.commands.ProfileMenuCommands;
import views.AppView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ProfileMenuController implements Controller {
    private AppView appView;
    private User user;

    public ProfileMenuController(AppView appView, User user) {
        this.appView = appView;
        this.user = user;
    }

    @Override
    public void update(String input) {
        ProfileMenuCommands command = ProfileMenuCommands.getCommand(input);
        String[] args = command.parseInput(input);
        switch (command) {
            case ChangeUsername -> changeUsername(args);
            case ChangeNickname -> changeNickname(args);
            case ChangePassword -> changePassword(args);
            case ChangeEmail -> changeEmail(args);
            case None -> Result.error("Invalid input");

        }
    }

    public Result changeUsername(String[] args) {
        String newUsername = args[0];
        if (!checkUsername(newUsername)) {
            return Result.error("invalid username format");
        }
        User user = App.getLoggedInUser();
        if (newUsername.equals(user.getUsername())) {
            return Result.error("the username should be different from the current one");
        }

        if (App.getUser(newUsername) != null) {
            return Result.error("the username is already taken");
        }

        user.setUsername(newUsername);

        return Result.success("username changed successfully");
    }

    public Result changePassword(String[] args) {
        String newPassword = args[0];
        String oldPasswordInput = args[1];
        User user = App.getLoggedInUser();
        if (!oldPasswordInput.equals(user.getPassword())) {
            return Result.error("invalid old password");
        }

        if (newPassword.equals(oldPasswordInput)) {
            return Result.error("the old password should be different from the current one");
        }

        if (!checkPasswordStrength(newPassword).success()) {
            return Result.error(checkPasswordStrength(newPassword).message());
        }

        user.setPassword(newPassword);
        return Result.success("password changed successfully");
    }

    public Result changeEmail(String[] args) {
        String newEmail = args[0];

        if (!checkEmail(newEmail)) {
            return Result.error("invalid email format");
        }

        if (isEmailUsed(newEmail)) {
            return Result.error("the email address is already taken");
        }

        User user = App.getLoggedInUser();
        user.setEmail(newEmail);

        return Result.success("email changed successfully");
    }

    public boolean isEmailUsed(String email) {
        for (User user : App.getUsers().values()) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }


    private boolean checkEmail(String email) {
        // email validation pattern
        String emailRegex = "^[a-zA-Z0-9][a-zA-Z0-9._-]*[a-zA-Z0-9]@[a-zA-Z0-9][a-zA-Z0-9.-]*[a-zA-Z0-9]\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);

        if (!pattern.matcher(email).matches()) {
            return false;
        }

        if (email.contains("..")) {
            return false;
        }

        return email.indexOf('@') == email.lastIndexOf('@');
    }


    public Result changeNickname(String[] args) {

        String newNickname = args[0];
        User user = App.getLoggedInUser();
        user.setNickname(newNickname);

        return Result.success("nickname changed successfully");
    }


    private Result checkPasswordStrength(String password) {
        // At least 8 characters
        boolean validLength = password.length() > 8;

        // Check for at least one lowercase, one uppercase, one digit, and one special character
        boolean hasLower = false, hasUpper = false, hasDigit = false, hasSpecial = false;
        String specialChars = "!#$%^&*()=+{}[]|\\:;'\"<>?";

        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (specialChars.contains(String.valueOf(c))) hasSpecial = true;
        }

        StringBuilder reason = new StringBuilder();
        if (!hasLower || !hasUpper || !hasDigit || !hasSpecial || !validLength) {
            reason.append("weak password");
            List<String> reasonStrings = new ArrayList<>();
            boolean oneError = false;
            if (!validLength) {
                oneError = true;
                reason.append("password too short");
            }

            if (!hasSpecial) {
                if (oneError) reason.append(" ");
                oneError = true;
                reasonStrings.add("passsword doesn't have special character");
            }

            if (!hasUpper) {
                if (oneError) reason.append(" ");
                oneError = true;
                reasonStrings.add("password doesn't have upper case");
            }

            if (!hasLower) {
                if (oneError) reason.append(" ");
                reasonStrings.add("password doesn't have lower case");
            }

            return Result.error(reason.toString());
        }


        return Result.success("");
    }

    public boolean checkUsername(String username) {
        Pattern pattern = Pattern.compile("[a-zA-Z][a-zA-Z0-9_]*");
        return pattern.matcher(username).matches();
    }

    public Result showUserInfo() {
        User user = App.getLoggedInUser();
        StringBuilder userInfo = new StringBuilder();

        userInfo.append("~user info~").append("\n");
        userInfo.append("Username: ").append(user.getUsername()).append("\n");
        userInfo.append("Nickname: ").append(user.getNickname()).append("\n");
        userInfo.append("Most Money Earned: ").append(user.getMostEarnedMoney()).append("\n");
        userInfo.append("Games Played: ").append(user.getGamesPlayed()).append("\n\n");

        return Result.success(userInfo.toString());
    }
}