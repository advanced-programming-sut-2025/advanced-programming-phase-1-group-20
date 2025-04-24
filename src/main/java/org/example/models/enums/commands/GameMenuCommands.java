package org.example.models.enums.commands;

import java.util.regex.Pattern;

public enum GameMenuCommands implements Command {

    // time related
    ShowTime(Pattern.compile("^time$")),
    ShowDate(Pattern.compile("^date$")),
    ShowDateTime(Pattern.compile("^clock$|^datetime$")),
    AdvanceTime(Pattern.compile("^cheat\\s+advance\\s+time\\s+(\\d+)h?$")), // cheat command
    AdvanceDate(Pattern.compile("^cheat\\s+advance\\s+date\\s+(\\d+)d?$")), // cheat command
    DayOfWeek(Pattern.compile("^day\\s+of\\s+(the\\s+)?week$")),
    ShowInventory(Pattern.compile("^inventory$")),
    ShowLocation(Pattern.compile("^location$")),
    ShowEnergy(Pattern.compile("^energy$")),
    ShowMap(Pattern.compile("^map$")),

    // weather related
    ShowSeason(Pattern.compile("^season$")),
    ShowWeather(Pattern.compile("^weather$")),
    ShowWeatherForecast(Pattern.compile("^weather\\s+forecast$")),
    SetWeather(Pattern.compile("^cheat\\s+weather\\s+set\\s+(\\w+)$")), // cheat code
    CheatThor(Pattern.compile("^cheat\\s+Thor\\s+-l\\s+([\\d\\s,]+)$")), // cheat code
    Move(Pattern.compile("^move\\s+(\\d+)\\s+(\\d+)$")),
    SaveGame(Pattern.compile("^save$")),
    AutoSave(Pattern.compile("^autosave$")),

    //plants and foraging related
    CraftInfo(Pattern.compile("^craftinfo\\s+-n\\s+(.+)$")),
    Plant(Pattern.compile("^plant\\s+-s\\s+(?<seedName>.+)\\s+" +
            "(?<direction>north|south|east|west|north-east|north-west|south-east|south-west)$")),
    ShowPlant(Pattern.compile("^showplant\\s+-l\\s+(?<x>\\d+)\\s+(?<y>\\d+)$")),
    Fertilize(Pattern.compile("^fertilize\\s+-f\\s+(?<fertilizerName>.+)\\s+-d\\s+(?<x>\\d+)\\s+(?<y>\\d+)$")),
    HowMuchWater(Pattern.compile("^howmuch\\s+water$")),
    Harvest(Pattern.compile("^harvest\\s+(?<x>\\d+)\\s+(?<y>\\d+)$")),


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