package org.example.models.enums.commands;

import java.util.regex.Pattern;

public enum GameMenuCommands implements Command {
    ShowTime(Pattern.compile("^time$")),
    ShowDate(Pattern.compile("^date$")),
    ShowDateTime(Pattern.compile("^clock$")),
    SaveGame(Pattern.compile("^save$")),
    AdvanceTime(Pattern.compile("^advance\\s+time\\s+(\\d+)$")),
    Move(Pattern.compile("^move\\s+(\\d+)\\s+(\\d+)$")),
    DayOfWeek(Pattern.compile("^day\\s+of\\s+week$")),
    ShowInventory(Pattern.compile("^inventory$")),
    ShowLocation(Pattern.compile("^location$")),
    ShowEnergy(Pattern.compile("^energy$")),
    ShowMap(Pattern.compile("^map$")),
    AutoSave(Pattern.compile("^autosave$")),

    // TODO: add more commands
    None(null);

    private final Pattern pattern;

    GameMenuCommands(Pattern pattern) {
        this.pattern = pattern;
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

    @Override
    public Pattern getPattern() {
        return this.pattern;
    }

    @Override
    public boolean matches(String input) {
        return pattern != null && pattern.matcher(input).matches();
    }
}