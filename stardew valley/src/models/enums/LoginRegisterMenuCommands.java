package models.enums;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum LoginRegisterMenuCommands implements Command {
    Login("^\\s*login\\s+-u\\s+(\\S+)\\s+-p\\s+(\\S+)\\s+(–stay-logged-in)?\\s*$"),
    RegisterUser("^\\s*register\\s+-u\\s+(\\S+)\\s+-p\\s+(\\S+)\\s+(\\S+)\\s+-n\\s+(\\S+)\\s+-e" +
            "\\s+(\\S+)\\s+-g\\s+(\\S+)\\s*$"),
    PickSecurityQuestion("^pick\\s+question-q\\s+(\\S+)\\s+-a(\\S+)\\s+(-c\\S+)$"),
    ForgotPass("^forget\\s+password\\s+-u\\s+(\\S+)$"),
    AnswerSecurityQuestion("^answer\\s+-a\\s+(\\S+)$");
    private final String regex;

    LoginRegisterMenuCommands(String regex) {
        this.regex = regex;
    }

    @Override
    public String getRegex() {
        return regex;
    }

    @Override
    public boolean matches(String input) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }
}
