package org.example.models.enums.commands;

import java.util.regex.Pattern;

public enum GameMenuCommands implements Command {

    // time related
    ShowTime(Pattern.compile("^time$")), ShowDate(Pattern.compile("^date$")), ShowDateTime(Pattern.compile("^clock$|^datetime$")), AdvanceTime(Pattern.compile("^cheat\\s+advance\\s+time\\s+(\\d+)h?$")), // cheat command
    AdvanceDate(Pattern.compile("^cheat\\s+advance\\s+date\\s+(\\d+)d?$")), // cheat command
    DayOfWeek(Pattern.compile("^day\\s+of\\s+(the\\s+)?week$")), ShowInventory(Pattern.compile("^inventory$")), ShowLocation(Pattern.compile("^location$")), ShowEnergy(Pattern.compile("^energy$")), ShowMap(Pattern.compile("^map$")),

    // weather related
    ShowSeason(Pattern.compile("^season$")), ShowWeather(Pattern.compile("^weather$")), ShowWeatherForecast(Pattern.compile("^weather\\s+forecast$")), SetWeather(Pattern.compile("^cheat\\s+weather\\s+set\\s+(\\w+)$")), // cheat code
    CheatThor(Pattern.compile("^cheat\\s+Thor\\s+-l\\s+([\\d\\s,]+)$")), // cheat code


    // player related
    Move(Pattern.compile("^move\\s+(\\d+)\\s+(\\d+)$")),

    // saving related
    SaveGame(Pattern.compile("^save$")), AutoSave(Pattern.compile("^autosave$")),

    // plants and foraging related
    CraftInfo(Pattern.compile("^craftinfo\\s+-n\\s+(.+)$")), Plant(Pattern.compile("^plant\\s+-s\\s+(?<seedName>.+)\\s+" + "(?<direction>north|south|east|west|north-east|north-west|south-east|south-west)$")), ShowPlant(Pattern.compile("^showplant\\s+-l\\s+(?<x>\\d+)\\s+(?<y>\\d+)$")), Fertilize(Pattern.compile("^fertilize\\s+-f\\s+(?<fertilizerName>.+)\\s+-d\\s+(?<x>\\d+)\\s+(?<y>\\d+)$")), HowMuchWater(Pattern.compile("^howmuch\\s+water$")), Harvest(Pattern.compile("^harvest\\s+(?<x>\\d+)\\s+(?<y>\\d+)$")),


    // crafting related
    CraftingShowRecipes(Pattern.compile("^crafting\\s+show\\s+recipes$")), CraftingCraft(Pattern.compile("^crafting\\s+craft\\s+(?<itemName>.+)$")), PlaceItem(Pattern.compile("^place\\s+item\\s+-n\\s+(?<itemName>.+)\\s+" + "-d\\s+(?<direction>north|south|east|west|north-east|north-west|south-east|south-west)$")), AddItem(Pattern.compile("^cheat\\s+add\\s+item\\s+-n\\s+(?<itemName>.+)\\s+-c\\s+(?<count>\\d+)$")),


    // cooking related
    AddRefrigerator(Pattern.compile("^cooking\\s+refrigerator\\s+(put|pick)\\s+(?<itemName>.+)$")), CookingShowRecipes(Pattern.compile("^cooking\\s+show\\s+recipes$")), CookingPrepare(Pattern.compile("^cooking\\s+prepare\\s+(?<recipeName>.+)$")), EatFood(Pattern.compile("^eat\\s+(?<foodName>.+)$")), showEnergy(Pattern.compile("^energy show$")), setEnergy(Pattern.compile("^energy set -v (\\d+)$")), energyUnlimited(Pattern.compile("^energy unlimited$")),

    // artisan related
    ArtisanUse(Pattern.compile("^artisan\\s+use\\s+(?<artisanName>.+)\\s+(?<itemName>.+)$")),
    ArtisanGet(Pattern.compile("^artisan\\s+get\\s+(?<artisanName>.+)$")),

    // sell command
    SellProduct(Pattern.compile("\\s*sell\\s+(?<productName>.+)\\s+-n\\s+(?<count>\\d+)\\s*")),

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