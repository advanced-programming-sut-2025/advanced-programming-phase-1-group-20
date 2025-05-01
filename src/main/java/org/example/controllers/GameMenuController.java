package org.example.controllers;

import org.example.models.App;
import org.example.models.Items.CookingItem;
import org.example.models.Items.CraftingItem;
import org.example.models.Items.Food;
import org.example.models.Items.Item;
import org.example.models.MapDetails.GameMap;
import org.example.models.Player.Player;
import org.example.models.common.Date;
import org.example.models.common.Location;
import org.example.models.common.Result;
import org.example.models.enums.Types.CraftingType;
import org.example.models.enums.Weather;
import org.example.models.enums.commands.GameMenuCommands;
import org.example.views.AppView;

import java.util.Collections;

public class GameMenuController implements Controller {
    private AppView appView;
    private Player player;
    private Date gameClock;
    private GameMap gMap;

    public GameMenuController(AppView appView, Player player) {
        this.appView = appView;
        this.player = player;
        this.gameClock = new Date();
        this.gMap = new GameMap(100, 100, player);
        // طول و عرض همینطوری گذاشته شده!
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
            case ShowEnergy -> result = showEnergy();
            case setEnergy -> result = setEnergy(args);
            case energyUnlimited -> result = energyUnlimited();

            //artisan related commands
            case ArtisanUse -> artisanUse(args);
            case ArtisanGet -> artisanGet(args);

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
        Item item = App.getItem(name);
        boolean flag = checkItem(item);
        if (flag) {
            item.showInfo();
        }
    }


    private void plant(String[] args) {
        String seedName = args[0];
        String direction = args[1];
        Item item = App.getItem(seedName);
        boolean flag = checkItem(item) && player.getInventory().hasItems(Collections.singletonList(seedName));
        //TODO : check direction && check collision
        if (flag) {
            //TODO : implementing plant , addToMap(Item item) ,
            //TODO : item.updateItem(); added just need to be added to gameClock
            //TODO : implementing giant Items.
            if (seedName.equals("Mixed Seeds")) {
                seedName = gameClock.getSeason().getRandomSeed();
                item = App.getItem(seedName);
                //TODO: implementing planting seed.
            }
        }
    }


    private void showPlant(String[] args) {
        int x = Integer.parseInt(args[0]);
        int y = Integer.parseInt(args[1]);
        boolean flag = true;
        //TODO: check location (x,y) and if there is a plant
        Item item = App.getItem("this must change later and " + "we must get it from map");
        if (flag) {
            item.showInfo();
        }
    }

    private void fertilize(String[] args) {
        String fertilizer = args[0];
        Item item = App.getItem(fertilizer);
        String direction = args[1];
        boolean flag = checkItem(item) && player.getInventory().hasItems(Collections.singletonList(fertilizer));
        if (flag) {
            //TODO : (kasra) implementing fertilize function in tools.
        }
    }

    private void howMuchWater() {
        //TODO : checking water in our bucket. (kasra)
        //TODO : adding useTool() method to tools.
    }

    private void harvest(String[] args) {
        int x = Integer.parseInt(args[0]);
        int y = Integer.parseInt(args[1]);
        //TODO: getting Location from (x,y).
        //TODO: getting plant or Tree from Map;
        Item item = App.getItem("getting Tree or Plant" + "from map this must change later");
        boolean flag = checkItem(item) && item.getFinished();
        if (flag) {
            player.getInventory().add(item);
        }
    }


    //crafting related
    private void craftingShowRecipes() {
        for (CraftingType type : CraftingType.values()) {
            type.showInfo();
        }
    }

    private void craftItem(String[] args) {
        String itemName = args[0];
        CraftingType type = CraftingType.fromName(itemName);
        if (type == null) {
            System.out.println("item does not exist");
        } else {
            CraftingItem craftingItem = new CraftingItem(type);
        }
    }

    private boolean checkItem(Item item) {
        if (item == null) {
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
            for (int i = 0; i < count; i++) {
                player.getInventory().add(item);
            }
        }
    }


    //cooking related
    private void addRefrigerator(String[] args) {
        String key = args[0];
        String itemName = args[1];
        Item item = App.getItem(itemName);
        boolean flag = checkItem(item);
        if (flag) {
            switch (key) {
                //TODO : checking refrigerator collision and check Item in refrigerator (taha).
                case "put":
                    //TODO : add item to refrigerator
                    flag = flag && player.getInventory().hasItems(Collections.singletonList(key));
                    if (flag) {
                        player.getInventory().add(item);
                    }
                    break;
                case "pick":
                    //TODO : check refrigerator.
                    break;
            }
        }
    }

    private void cookingShowRecipes() {
        for (CookingItem cookingItem : player.getCookingItems()) {
            cookingItem.showInfo();
        }
    }

    private void cookingPrepare(String[] args) {
        String name = args[0];
        Item item = App.getItem(name);
        boolean flag = checkItem(item) && isCooking(item) && player.getInventory().hasItems(Collections.singletonList(name));
        //TODO : checking refrigerator.
        //TODO : we must check inventory is full or not.
        if (flag) {
            CookingItem cookingItem = (CookingItem) item;
            //TODO : decrease energy.
            Food food = cookingItem.cook(player.getInventory());
            player.getInventory().add(food);
            //TODO : controlling xp.
        }
    }

    private boolean isCooking(Item item) {
        if (item instanceof CookingItem) {
            return true;
        }
        System.out.println("item is not a cooking item");
        return false;
    }

    private void eatFood(String[] args) {
        String foodName = args[0];
        Item item = App.getItem(foodName);
        boolean flag = checkItem(item) && player.getInventory().hasItems(Collections.singletonList(foodName)) && isFood(item);
        if (flag) {
            Food food = (Food) item;
            //TODO : advance energy.
            player.getInventory().remove(food);
        }
    }

    private boolean isFood(Item item) {
        if (item instanceof Food) {
            return true;
        }
        System.out.println("item is not a food");
        return false;
    }



    public void artisanUse(String[] args) {

    }

    public void artisanGet(String[] args) {

    }

    //sell Function:
    private void sellProduct(String[] args) {
        String productName = args[0];
        int count = Integer.parseInt(args[1]);
        // checkTrashBin();
    }

    // TODO: map showing + map related commands
}