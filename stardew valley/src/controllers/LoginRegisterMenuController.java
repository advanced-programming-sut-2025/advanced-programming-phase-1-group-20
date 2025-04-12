package controllers;

import models.App;
import models.Result;
import models.User;
import models.enums.Gender;
import models.enums.LoginRegisterMenuCommands;
import views.AppView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginRegisterMenuController implements Controller {
    private AppView appView;
    private User user;

    public LoginRegisterMenuController(AppView appView , User user) {
        this.appView = appView;
        this.user = user;
    }


    @Override
    public void update(String input) {
        LoginRegisterMenuCommands command = LoginRegisterMenuCommands.getCommand(input);
        String[] args = command.parseInput(input);
        switch (command) {
            case RegisterUser -> registerUser(args);
            case Login -> login(args);
            case ForgotPass -> forgotPassword(args);
            case PickSecurityQuestion -> pickSecurityQuestion(args , user);
            case AnswerSecurityQuestion -> answerSecurityQuestion(args , user.getUsername());
            case None -> Result.error("Invalid input");
        }
    }
    public Result registerUser(String[] args) {

        String username = args[0];
        String password = args[1];
        String passwordConfirm = args[2];
        String nickname = args[3];
        String email = args[4];
        String genderString = args[5];

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
        user = newUser;
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

    public Result login(String[] args) {
        String username = args[0];
        String password = args[1];
        String stayLoggedInStr = args[2];
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

    public Result pickSecurityQuestion(String[] args, User user) {

        String questionNumberStr = args[0];
        String answer = args[1];
        String answerConfirm = args[2];
        int questionNumber = Integer.parseInt(questionNumberStr) - 1;

        if (!answer.equals(answerConfirm)) {
            return Result.error("the answer confirmation is not correct");
        }

        user.setSecurityAnswer(answer);
        user.setSecurityQuestionIndex(questionNumber);

        return Result.success("security question added successfully");
    }

    public Result forgotPassword(String[] args) {
        String username = args[0];
        User user = App.getUser(username);
        if (user == null) {
            return Result.error("user not found");
        }

        return Result.success(username);
    }

    public Result answerSecurityQuestion(String[] args, String username) {
        User user = App.getUser(username);
        String answer = args[0];
        if (!answer.equals(user.getSecurityAnswer())) {
            return Result.error("the answer is not correct");
        }

        return Result.success("your password is " + user.getPassword());
    }

}