package org.example.controllers;

import org.example.models.App;
import org.example.models.common.Result;
import org.example.models.entities.User;
import org.example.models.enums.commands.ProfileMenuCommands;
import org.example.views.AppView;

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
    public Result update(String input) {

        ProfileMenuCommands command = ProfileMenuCommands.getCommand(input);
        String[] args = command.parseInput(input);
        Result result = null;
        switch (command) {
            case ChangeUsername -> result = changeUsername(args);
            case ChangeNickname -> result = changeNickname(args);
            case ChangePassword -> result = changePassword(args);
            case ChangeEmail -> result = changeEmail(args);
            case ShowUserInfo -> result = showUserInfo();
            case Logout -> result = logout();
            case ShowCurrentMenu -> result = Result.success("Profile menu");
            case None -> Result.error("Invalid input");
        }
        appView.handleResult(result, command);
        return result;
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

        App.saveData();

        return Result.success("username changed successfully");
    }

    public Result changePassword(String[] args) {
        String newPassword = args[0];
        String oldPasswordInput = args[1];
        User user = App.getLoggedInUser();

        if (!user.verifyPassword(oldPasswordInput)) {
            return Result.error("invalid old password");
        }

        if (newPassword.equals(oldPasswordInput)) {
            return Result.error("the new password should be different from the current one");
        }

        if (!checkPasswordStrength(newPassword).success()) {
            return Result.error(checkPasswordStrength(newPassword).message());
        }

        user.setPassword(newPassword);

        App.saveData();

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

        App.saveData();

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

        // Save changes to user data
        App.saveData();

        return Result.success("nickname changed successfully");
    }

    private Result checkPasswordStrength(String password) {
        boolean validLength = password.length() > 8;

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
                reasonStrings.add("password doesn't have special character");
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

        String userInfo = "~user info~" + "\n" +
                "Username: " + user.getUsername() + "\n" +
                "Nickname: " + user.getNickname() + "\n" +
                "Most Money Earned: " + user.getMostEarnedMoney() + "\n" +
                "Games Played: " + user.getGamesPlayed() + "\n";

        return Result.success(userInfo);
    }

    public Result logout() {
        App.logout();
        return Result.success("logged out successfully");
    }
}
