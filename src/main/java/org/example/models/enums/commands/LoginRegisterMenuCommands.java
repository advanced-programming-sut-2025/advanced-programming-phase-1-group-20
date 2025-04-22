package org.example.models.enums.commands;

import java.util.regex.Pattern;

public enum LoginRegisterMenuCommands implements Command {
    Login(Pattern.compile("^\\s*login\\s+-u\\s+(\\S+)\\s+-p\\s+(\\S+)\\s+(–stay-logged-in)?\\s*$")),
    RegisterUser(Pattern.compile("^\\s*register\\s+-u\\s+(\\S+)\\s+-p\\s+(\\S+)\\s+(\\S+)\\s+-n\\s+(\\S+)\\s+-e" +
            "\\s+(\\S+)\\s+-g\\s+(\\S+)\\s*$")),
    PickSecurityQuestion(Pattern.compile("^\\s*pick\\s+question\\s+-q\\s+(\\d+)\\s+-a\\s+(\\S+)\\s+-c\\s+(\\S+)\\s*$")),
    ForgotPass(Pattern.compile("^\\s*forget\\s+password\\s+-u\\s+(\\S+)\\s*$")),
    AnswerSecurityQuestion(Pattern.compile("^\\s*answer\\s+-a\\s+(\\S+)\\s*$")),
    AcceptPassword(Pattern.compile("^\\s*accept\\s+password\\s*$")),
    SetCustomPassword(Pattern.compile("^\\s*set\\s+password\\s+-p\\s+(\\S+)\\s+-c\\s+(\\S+)\\s*$")),
    GenerateNewPassword(Pattern.compile("^\\s*generate\\s+new\\s+password\\s*$")),
    None(null);
    private final Pattern pattern;


    LoginRegisterMenuCommands(Pattern pattern) {
        this.pattern = pattern;
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

    @Override
    public Pattern getPattern() {
        return this.pattern;
    }

    public boolean matches(String input) {
        return this.pattern != null && this.pattern.matcher(input).matches();
    }
}