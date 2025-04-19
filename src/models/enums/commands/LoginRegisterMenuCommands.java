package models.enums.commands;

import java.util.regex.Pattern;

public enum LoginRegisterMenuCommands implements Command {
    Login(Pattern.compile("^\\s*login\\s+-u\\s+(\\S+)\\s+-p\\s+(\\S+)\\s+(–stay-logged-in)?\\s*$")),
    RegisterUser(Pattern.compile("^\\s*register\\s+-u\\s+(\\S+)\\s+-p\\s+(\\S+)\\s+(\\S+)\\s+-n\\s+(\\S+)\\s+-e" +
            "\\s+(\\S+)\\s+-g\\s+(\\S+)\\s*$")),
    PickSecurityQuestion(Pattern.compile("^pick\\s+question-q\\s+(\\S+)\\s+-a(\\S+)\\s+(-c\\S+)$")),
    ForgotPass(Pattern.compile("^forget\\s+password\\s+-u\\s+(\\S+)$")),
    AnswerSecurityQuestion(Pattern.compile("^answer\\s+-a\\s+(\\S+)$")),
    None(null);
    private final Pattern pattern;


    LoginRegisterMenuCommands(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public Pattern getPattern() {
        return this.pattern;
    }

    public boolean matches(String input) {
        return this.pattern.matcher(input).matches();
    }


    public static LoginRegisterMenuCommands getCommand(String input) {
        input = input.trim();
        for (LoginRegisterMenuCommands command : values()) {
            if (command != None && command.matches(input)) {
                return command;
            }
        }
        return None;
    }
}
