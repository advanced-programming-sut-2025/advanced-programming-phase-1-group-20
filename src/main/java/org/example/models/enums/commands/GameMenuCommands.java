package org.example.models.enums.commands;

import java.util.regex.Pattern;

public enum GameMenuCommands implements Command {

    // game related
    SelectMap(Pattern.compile("^game\\s+map\\s+(\\d+)$")),
    ExitGame(Pattern.compile("^exit\\s+game$")),
    NextTurn(Pattern.compile("^next\\s+turn$")),
    VoteTerminate(Pattern.compile("^vote\\s+terminate\\s+(yes|no)$")),

    // time related
    ShowTime(Pattern.compile("^time$")),
    ShowDate(Pattern.compile("^date$")),
    ShowDateTime(Pattern.compile("^clock$|^datetime$")),
    AdvanceTime(Pattern.compile("^cheat\\s+advance\\s+time\\s+(\\d+)h?$")), // cheat command
    AdvanceDate(Pattern.compile("^cheat\\s+advance\\s+date\\s+(\\d+)d?$")), // cheat command
    DayOfWeek(Pattern.compile("^day\\s+of\\s+(the\\s+)?week$")),

    // weather related
    ShowSeason(Pattern.compile("^season$")),
    ShowWeather(Pattern.compile("^weather$")),
    ShowWeatherForecast(Pattern.compile("^weather\\s+forecast$")),
    SetWeather(Pattern.compile("^cheat\\s+weather\\s+set\\s+(\\w+)$")), // cheat code
    CheatThor(Pattern.compile("^cheat\\s+Thor\\s+-l\\s+([\\d\\s,]+)$")), // cheat code


    // player related
    Walk(Pattern.compile("^walk\\s+-l\\s+(?<x>\\d+)\\s*,\\s*(?<y>\\d+)$")),
    ShowInventory(Pattern.compile("^inventory$")),
    ShowLocation(Pattern.compile("^location$")),
    ShowEnergy(Pattern.compile("^energy$")),
    ShowMap(Pattern.compile("^map$")),
    PrintMap(Pattern.compile("^print\\s+map\\s+-l\\s+(?<x>\\d+)\\s*,\\s*(?<y>\\d+)\\s+-s\\s+(?<size>\\d+)$")),
    TestPrintMap(Pattern.compile("^test\\s+print\\s+map$")),
    HelpReadingMap(Pattern.compile("^help\\s+reading\\s+map$")),
    EatFood(Pattern.compile("^eat\\s+(?<foodName>.+)$")),
    setEnergy(Pattern.compile("^energy set -v (\\d+)$")),
    energyUnlimited(Pattern.compile("^energy unlimited$")),

    // saving related
    SaveGame(Pattern.compile("^save$")), AutoSave(Pattern.compile("^autosave$")),

    // plants and foraging related
    CraftInfo(Pattern.compile("^craftinfo\\s+-n\\s+(.+)$")),
    Plant(Pattern.compile("^plant\\s+-s\\s+(?<seedName>.+)\\s+" + "(?<direction>north|south|east|west|north-east|north-west|south-east|south-west)$")),
    ShowPlant(Pattern.compile("^showplant\\s+-l\\s+(?<x>\\d+)\\s+(?<y>\\d+)$")),
    Fertilize(Pattern.compile("^fertilize\\s+-f\\s+(?<fertilizerName>.+)\\s+-d\\s+(?<x>\\d+)\\s+(?<y>\\d+)$")),
    HowMuchWater(Pattern.compile("^howmuch\\s+water$")),
    GiveWater(Pattern.compile("^give\\s+-d\\s+(?<direction>north|south|east|west|north-east|north-west|south-east|south-west)$")),
    Harvest(Pattern.compile("^harvest\\s+(?<x>\\d+)\\s+(?<y>\\d+)$")),


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

    // sell command
    SellProduct(Pattern.compile("\\s*sell\\s+(?<productName>.+)\\s+-n\\s+(?<count>\\d+)\\s*")),

    // tool commands
    ToolEquip(Pattern.compile("^tools\\s+equip\\s+(?<toolName>.+)$")),
    ToolShowCurrent(Pattern.compile("^tools\\s+show\\s+current$")),
    ToolShowAvailable(Pattern.compile("^tools\\s+show\\s+available$")),
    ToolUse(Pattern.compile("^tools\\s+use\\s+-d\\s+(?<direction>north|south|east|west|north-east|north-west|south-east|south-west)$")),

    // greenhouse related
    GreenhouseBuild(Pattern.compile("^greenhouse\\s+build$")),

    // Friendship-related commands
    FriendshipStatus(Pattern.compile("^friendships$")),
    TalkToPlayer(Pattern.compile("^talk\\s+-u\\s+(?<username>\\S+)\\s+-m\\s+(?<message>.+)$")),
    TalkHistory(Pattern.compile("^talk\\s+history\\s+-u\\s+(?<username>\\S+)$")),
    GiftToPlayer(Pattern.compile("^gift\\s+-u\\s+(?<username>\\S+)\\s+-i\\s+(?<item>.+)\\s+-a\\s+(?<amount>\\d+)$")),
    GiftList(Pattern.compile("^gift\\s+list$")),
    GiftRate(Pattern.compile("^gift\\s+rate\\s+-i\\s+(?<giftNumber>\\d+)\\s+-r\\s+(?<rating>[1-5])$")),
    GiftHistory(Pattern.compile("^gift\\s+history\\s+-u\\s+(?<username>\\S+)$")),
    HugPlayer(Pattern.compile("^hug\\s+-u\\s+(?<username>\\S+)$")),
    FlowerPlayer(Pattern.compile("^flower\\s+-u\\s+(?<username>\\S+)$")),
    AskToMarry(Pattern.compile("^ask\\s+marriage\\s+-u\\s+(?<username>\\S+)\\s+-r\\s+(?<ring>\\S+)$")),
    RespondToMarry(Pattern.compile("^respond\\s+-(accept|reject)\\s+-u\\s+(?<username>\\S+)")), // TODO: add marriage notification

    // NPC-related commands
    MeetNPC(Pattern.compile("^meet\\s+NPC\\s+(?<npcName>\\S+)$")),
    GiftNPC(Pattern.compile("^gift\\s+NPC\\s+(?<npcName>\\S+)\\s+-i\\s+(?<item>.+)$")),
    FriendshipNPCList(Pattern.compile("^friendship\\s+NPC\\s+list$")),

    // Quest-related commands
    QuestsList(Pattern.compile("^quests\\s+list$")),
    QuestsFinish(Pattern.compile("^quests\\s+finish\\s+-i\\s+(?<index>\\d+)$")),

    // Trade-related commands
    StartTrade(Pattern.compile("^start\\s+trade$")),
    TradeRequest(Pattern.compile("^trade\\s+-u\\s+(?<username>\\S+)\\s+-t\\s+(?<type>request|offer)\\s+-i\\s+(?<item>.+)\\s+-a\\s+(?<amount>\\d+)(\\s+-p\\s+(?<price>\\d+))?(\\s+-ti\\s+(?<targetItem>.+)\\s+-ta\\s+(?<targetAmount>\\d+))?$")),
    TradeList(Pattern.compile("^trade\\s+list$")),
    TradeResponse(Pattern.compile("^trade\\s+response\\s+(--accept|--reject)\\s+-i\\s+(?<id>\\d+)$")),
    TradeHistory(Pattern.compile("^trade\\s+history$")),
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
