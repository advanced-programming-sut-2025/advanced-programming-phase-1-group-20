package controllers;

import models.App;
import models.Result;
import models.User;
import models.enums.Gender;
import models.enums.LoginRegisterMenuCommands;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginRegisterMenuController {
    public Result registerUser(String input) {
        Pattern pattern = Pattern.compile(LoginRegisterMenuCommands.RegisterUser.getRegex());
        Matcher matcher = pattern.matcher(input);

        if (!matcher.find()) {
            return Result.error("invalid input");
        }

        String username = matcher.group(1);
        String password = matcher.group(2);
        String passwordConfirm = matcher.group(3);
        String nickname = matcher.group(4);
        String email = matcher.group(5);
        String genderString = matcher.group(6);

        if (App.getUser(username) != null) {
            return Result.error("username already taken");
        }

        if (!checkUsername(username)) {
            return Result.error("invalid username format");
        }

        if (!checkEmail(email)) {
            return Result.error("invalid email format");
        }

        if (isEmailUsed(email)) {
            return Result.error("email already used");
        }

        boolean isRandom = false;
        if (password.equals("random")) {
            isRandom = true;
            password = generateRandomPassword();
        }

        if (!password.equals(passwordConfirm) && !isRandom) {
            return Result.error("password confirm does not match the password");
        }

        if (!checkPasswordStrength(password).success()) {
            return Result.error(checkPasswordStrength(password).message());
        }
        Gender gender = Gender.getGenderByName(genderString);
        User newUser = new User(username, password, email, nickname, gender);

        App.addUser(newUser);

        return Result.success("user registered successfully");
    }

    public boolean checkUsername(String username) {
        Pattern pattern = Pattern.compile("[a-zA-Z][a-zA-Z0-9_]*");
        return pattern.matcher(username).matches();
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

    public boolean isEmailUsed(String email) {
        for (User user : App.getUsers().values()) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    private String generateRandomPassword() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "!#$%^&*()=+{}[]|\\:;'\"<>?";
        String all = upper + lower + digits + special;

        Random random = new Random();
        StringBuilder password = new StringBuilder();

        // Ensure at least one character from each category
        password.append(upper.charAt(random.nextInt(upper.length())));
        password.append(lower.charAt(random.nextInt(lower.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(special.charAt(random.nextInt(special.length())));

        // Fill the rest to make it 12 characters long
        for (int i = 0; i < 8; i++) {
            password.append(all.charAt(random.nextInt(all.length())));
        }

        // Shuffle the characters
        char[] array = password.toString().toCharArray();
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            char temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }

        return new String(array);
    }

    public Result login(String input) {
        Pattern pattern = Pattern.compile(LoginRegisterMenuCommands.Login.getRegex());
        Matcher matcher = pattern.matcher(input);

        if (!matcher.matches()) {
            return Result.error("invalid input");
        }

        String username = matcher.group(1);
        String password = matcher.group(2);
        String stayLoggedInStr = matcher.group(3);
        boolean stayLoggedIn = !stayLoggedInStr.isEmpty();

        User user = App.getUser(username);

        if (user == null) {
            return Result.error("user not found");
        }

        if (!user.getPassword().equals(password)) {
            return Result.error("wrong password");
        }

        user.setStayLoggedIn(stayLoggedIn);
        App.setLoggedInUser(user);

        return Result.success("logged in successfully");
    }

    public Result logout() {
        App.setLoggedInUser(null);

        return Result.success("logged out");
    }

    public Result pickSecurityQuestion(String input, User user) {
        Pattern pattern = Pattern.compile(LoginRegisterMenuCommands.PickSecurityQuestion.getRegex());
        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            return Result.error("invalid input");
        }

        String questionNumberStr = matcher.group(1);
        String answer = matcher.group(2);
        String answerConfirm = matcher.group(3);
        int questionNumber = Integer.parseInt(questionNumberStr) - 1;

        if (!answer.equals(answerConfirm)) {
            return Result.error("the answer confirmation is not correct");
        }

        user.setSecurityAnswer(answer);
        user.setSecurityQuestionIndex(questionNumber);

        return Result.success("security question added successfully");
    }

    public Result forgotPassword(String input) {
        Pattern pattern = Pattern.compile(LoginRegisterMenuCommands.ForgotPass.getRegex());
        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            return Result.error("invalid input");
        }

        String username = matcher.group(1);
        User user = App.getUser(username);
        if (user == null) {
            return Result.error("user not found");
        }

        return Result.success(username);
    }

    public Result answerSecurityQuestion(String input, String username) {
        User user = App.getUser(username);

        Pattern pattern = Pattern.compile(LoginRegisterMenuCommands.AnswerSecurityQuestion.getRegex());
        Matcher matcher = pattern.matcher(input);
        if (!matcher.matches()) {
            return Result.error("invalid input");
        }
        String answer = matcher.group(1);
        if (!answer.equals(user.getSecurityAnswer())) {
            return Result.error("the answer is not correct");
        }

        return Result.success("your password is " + user.getPassword());
    }
}