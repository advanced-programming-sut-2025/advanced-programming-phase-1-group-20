package org.example.models.enums.commands;

import java.util.regex.Pattern;

public enum MainMenuCommands implements Command {
    NewGame(Pattern.compile("^game new -u (\\S+) (\\S+) (\\S+)$")),
    None(null);
    private final Pattern pattern;

    MainMenuCommands(Pattern pattern) {
        this.pattern = pattern;
    }

    public static MainMenuCommands getCommand(String input) {
        input = input.trim();
        for (MainMenuCommands command : values()) {
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

    @Override
    public boolean matches(String input) {
        return pattern.matcher(input).matches();
    }
}
