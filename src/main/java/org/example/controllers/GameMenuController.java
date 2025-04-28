package org.example.controllers;

import org.example.models.App;
import org.example.models.Items.CookingItem;
import org.example.models.Items.CraftingItem;
import org.example.models.Items.Item;
import org.example.models.Items.Plant;
import org.example.models.MapDetails.GameMap;
import org.example.models.Player.Player;
import org.example.models.common.Date;
import org.example.models.common.Location;
import org.example.models.common.Result;
import org.example.models.enums.Types.CraftingType;
import org.example.models.enums.Types.PlantType;
import org.example.models.enums.Weather;
import org.example.models.enums.commands.GameMenuCommands;
import org.example.views.AppView;

public class GameMenuController implements Controller {
    private AppView appView;
    private Player player;
    private Date gameClock;
    private GameMap gMap;

    public GameMenuController(AppView appView, Player player) {
        this.appView = appView;
        this.player = player;
        this.gameClock = new Date();
        this.gMap = new GameMap();
    }

    @Override
    public Result update(String input) {
        GameMenuCommands command = GameMenuCommands.getCommand(input);
        String[] args = command.parseInput(input);
        Result result = null;

        switch (command) {
            // time related commands
            case ShowTime -> showTime();
            case ShowDate -> showDate();
            case ShowDateTime -> showDateTime();
            case AdvanceTime -> result = advanceTime(args);
            case AdvanceDate -> result = advanceDate(args);
            case DayOfWeek -> showDayOfWeek();
            case ShowSeason -> showSeason();

            // Weather related commands
            case ShowWeather -> showWeather();
            case ShowWeatherForecast -> showWeatherForecast();
            case SetWeather -> result = setWeather(args);
            case CheatThor -> result = cheatThor(args);

            // Map related commands


            // Player related commands

            // Farm related commands

            // saving related commands
            case SaveGame -> {
                result = Result.success("Game saved successfully");
            }
            case AutoSave -> {
                result = Result.success("Auto-save completed");
            }


            //plants and foraging related commands
            case CraftInfo -> craftInfo(args);
            case Plant -> plant(args);
            case ShowPlant -> showPlant(args);
            case Fertilize -> fertilize(args);
            case HowMuchWater -> howMuchWater();
            case Harvest -> harvest(args);

            //crafting related commands
            case CraftingShowRecipes -> craftingShowRecipes();
            case CraftingCraft -> craftItem(args);
            case PlaceItem -> placeItem(args);
            case AddItem -> addItem(args);


            //cooking related commands
            case AddRefrigerator -> addRefrigerator(args);
            case CookingShowRecipes -> cookingShowRecipes();
            case CookingPrepare -> cookingPrepare(args);
            case EatFood -> eatFood(args);

            //sell command:
            case SellProduct -> sellProduct(args);


            case None -> result = Result.error("Invalid command");
        }

        if (result == null) {
            result = Result.error("");
        }

        appView.handleResult(result, command);

        return result;
    }


    //time related

    public void showTime() {
        gameClock.displayTime();
    }

    public void showDate() {
        gameClock.displayDate();
    }

    public void showDateTime() {
        gameClock.displayClock();
    }

    public void showSeason() {
        gameClock.displaySeason();
    }

    public void showDayOfWeek() {
        gameClock.displayDayOfWeek();
    }

    public void showWeather() {
        gameClock.displayWeather();
    }

    public void showWeatherForecast() {
        gameClock.displayWeatherForecast();
    }

    public Result setWeather(String[] args) {
        if (args == null || args.length < 1) {
            return Result.error("Weather type not specified");
        }

        try {
            Weather weatherType = Weather.valueOf(args[0].toUpperCase());
            gameClock.setWeatherTomorrow(weatherType);
            return Result.success("Tomorrow's weather has been set to " + weatherType);
        } catch (IllegalArgumentException e) {
            return Result.error("Invalid weather type. Available types: SUNNY, RAINY, STORMY, SNOWY");
        }
    }

    public Result advanceTime(String[] args) {
        if (args == null || args.length < 1) {
            return Result.error("Hours not specified");
        }

        try {
            int hours = Integer.parseInt(args[0]);
            if (hours < 0) {
                return Result.error("Cannot advance time by negative values");
            }
            gameClock.advanceTime(hours);
            return Result.success("Time advanced by " + hours + " hours");
        } catch (NumberFormatException e) {
            return Result.error("Invalid number format for hours");
        }
    }

    public Result advanceDate(String[] args) {
        if (args == null || args.length < 1) {
            return Result.error("Days not specified");
        }

        try {
            int days = Integer.parseInt(args[0]);

            gameClock.advanceDays(days);
            return Result.success("Date advanced by " + days + " days");
        } catch (NumberFormatException e) {
            return Result.error("Invalid number format for days");
        }
    }

    public Result cheatThor(String[] args) {
        if (args == null || args.length < 2) {
            return Result.error("Location not properly specified. Use: cheat Thor -l <x,y>");
        }

        try {
            // Parse location from args
            String[] coordinates = args[0].split(",");
            if (coordinates.length != 2) {
                return Result.error("Invalid location format. Use: <x,y>");
            }

            int x = Integer.parseInt(coordinates[0].trim());
            int y = Integer.parseInt(coordinates[1].trim());

            Location location = new Location(x, y, null);
            gameClock.cheatThor(location);

            return Result.success("Thor has struck at location (" + x + "," + y + ")");
        } catch (NumberFormatException e) {
            return Result.error("Invalid coordinates format");
        }
    }




    //plants and foraging related
    private void craftInfo(String[] args) {
        String name = args[0];
        PlantType type = PlantType.fromName(name);
        if(type == null){
            System.out.println("Item " + name + " not found");
        }else{
            Item item = new Plant(type);
            item.showInfo();
        }
    }


    private void plant(String[] args) {
    }

    private void showPlant(String[] args) {
    }

    private void fertilize(String[] args) {
    }

    private void howMuchWater() {
    }

    private void harvest(String[] args) {
    }


    //crafting related
    private void craftingShowRecipes() {
        for(CraftingType type :CraftingType.values()){
            type.showInfo();
        }
    }

    private void craftItem(String[] args) {
        String itemName = args[0];
        CraftingType type = CraftingType.fromName(itemName);
        if(type == null){
            System.out.println("item does not exist");
        }else{
            CraftingItem craftingItem = new CraftingItem(type);
        }
    }

    private boolean checkItem(Item item){
        if(item == null){
            System.out.println("item does not exist");
            return false;
        }
        return true;
    }

    private void placeItem(String[] args) {
        String itemName = args[0];

        //TODO : i must get items from inventory
        Item item = App.getItem(itemName);
        boolean flag = checkItem(item);
        if (flag) {
            //place item.
        }
    }

    private void addItem(String[] args) {
        String itemName = args[0];
        int count = Integer.parseInt(args[1]);
        Item item = App.getItem(itemName);
        boolean flag = checkItem(item);
        if (flag) {
            //adding items to inventory.
        }
    }



    //cooking related
    private void addRefrigerator(String[] args) {
        String key = args[0];
        String itemName = args[1];
        Item item = App.getItem(itemName);
        boolean flag = checkItem(item);
        if (flag) {
            //implementing put or pick method
        }
    }

    private void cookingShowRecipes() {
        for(CookingItem cookingItem : player.getCookingItems()){
            cookingItem.showInfo();
        }
    }

    private void cookingPrepare(String[] args) {
        CookingItem cookingItem = (CookingItem) App.getItem(args[0]);
        boolean flag = checkItem(cookingItem);
        if (flag) {
            //cooking recipe and adding it to inventory
        }
    }

    private void eatFood(String[] args) {
        String foodName = args[0];
        //getting food and eating function.
    }


    //sell Function:
    private void sellProduct(String[] args) {
        String productName = args[0];
        int count = Integer.parseInt(args[1]);
        // checkTrashBin();
    }

    // TODO: map showing + map related commands
}