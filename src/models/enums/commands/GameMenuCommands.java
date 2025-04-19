package models.enums.commands;

import java.util.regex.Pattern;

public enum GameMenuCommands implements Command {
    ShowTime(Pattern.compile("^time$")),
    None(null);

    private final Pattern pattern;
    GameMenuCommands(Pattern pattern) {
        this.pattern = pattern;
    }
    @Override
    public Pattern getPattern() {
        return this.pattern;
    }

    @Override
    public boolean matches(String input) {
        return pattern.matcher(input).matches();
    }

    public static GameMenuCommands getCommand(String input) {
        input = input.trim();
        for (GameMenuCommands command : values()) {
            if (command != None && command.matches(input)) {
                return command;
            }
        }
        return None;
    }
}
