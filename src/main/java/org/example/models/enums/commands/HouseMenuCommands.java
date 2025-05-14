package org.example.models.enums.commands;

import java.util.regex.Pattern;

public enum HouseMenuCommands implements Command {
    //home sweet home!
    // crafting related
    CraftingShowRecipes(Pattern.compile("^crafting\\s+show\\s+recipes$")),
    CraftingCraft(Pattern.compile("^crafting\\s+craft\\s+(?<itemName>.+)$")),
    PlaceItem(Pattern.compile("^place\\s+item\\s+-n\\s+(?<itemName>.+)\\s+" +
            "-d\\s+(?<direction>north|south|east|west|north-east|north-west|south-east|south-west)$")),
    AddItem(Pattern.compile("^cheat\\s+add\\s+item\\s+-n\\s+(?<itemName>.+)\\s+-c\\s+(?<count>\\d+)$")),


    // cooking related
    AddRefrigerator(Pattern.compile("^cooking\\s+refrigerator\\s+(put|pick)\\s+(?<itemName>.+)$")),
    CookingShowRecipes(Pattern.compile("^cooking\\s+show\\s+recipes$")),
    CookingPrepare(Pattern.compile("^cooking\\s+prepare\\s+(?<recipeName>.+)$")),

    // artisan related
    ArtisanUse(Pattern.compile("^artisan\\s+use\\s+(?<artisanName>.+)\\s+(?<itemName>.+)$")),
    ArtisanGet(Pattern.compile("^artisan\\s+get\\s+(?<artisanName>.+)$")),
    None(null);

    private final Pattern pattern;
    HouseMenuCommands(Pattern pattern) {
        this.pattern = pattern;
    }


    public static HouseMenuCommands getCommand(String input) {
        input = input.trim();
        for (HouseMenuCommands command : values()) {
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
