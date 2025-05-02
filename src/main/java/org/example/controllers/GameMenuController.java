package org.example.controllers;

import org.example.models.App;
import org.example.models.Items.*;
import org.example.models.MapDetails.GameMap;
import org.example.models.Player.Player;
import org.example.models.common.Date;
import org.example.models.common.Location;
import org.example.models.common.Result;
import org.example.models.entities.Game;
import org.example.models.enums.Types.CraftingType;
import org.example.models.enums.Weather;
import org.example.models.enums.commands.GameMenuCommands;
import org.example.views.AppView;
import org.example.views.MainMenu;

import java.util.Collections;
import java.util.List;

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
        // Check if the input is a menu navigation command
        if (isMenuNavigationCommand(input)) {
            return processMenuNavigationCommand(input);
        }

        GameMenuCommands command = GameMenuCommands.getCommand(input);
        String[] args = command.parseInput(input);
        Result result = null;

        switch (command) {
            // multiplayer game related commands
            case SelectMap -> result = selectMap(args);
            case ExitGame -> result = exitGame();
            case NextTurn -> result = nextTurn();
            case VoteTerminate -> result = voteTerminate(args);

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


            // Player Related
            case ShowInventory -> showInventory();

            // Map related commands


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

            //artisan-related commands
            case ArtisanUse -> artisanUse(args);
            case ArtisanGet -> artisanGet(args);

            //sell command:
            case SellProduct -> sellProduct(args);

            // tool commands
            case ToolEquip -> result = equipTool(args);
            case ToolShowCurrent -> result = showCurrentTool();
            case ToolShowAvailable -> result = showAvailableTools();
            case ToolUpgrade -> result = upgradeTool(args);
            case ToolUse -> result = useTool(args);

            // Walking and map commands
            case Walk -> result = walk(args);
            case PrintMap -> result = printMap(args);

            case None -> result = Result.error("Invalid command");
        }

        if (result == null) {
            result = Result.error("");
        }

        appView.handleResult(result, command);

        return result;
    }

    private boolean isMenuNavigationCommand(String input) {
        return input.trim().startsWith("menu ") || input.trim().equals("show current menu");
    }

    private Result processMenuNavigationCommand(String input) {
        input = input.trim();

        if (input.equals("show current menu")) {
            return Result.success(appView.getCurrentMenuName());
        } else if (input.equals("menu exit")) {
            appView.navigateMenu(new MainMenu(appView, player.getUser()));
            return Result.success("Exited to main menu");
        } else if (input.startsWith("menu enter ")) {
            String menuName = input.substring("menu enter ".length()).trim().toLowerCase();

            if (menuName.equals("main")) {
                appView.navigateMenu(new MainMenu(appView, player.getUser()));
                return Result.success("Entered main menu");
            } else {
                return Result.error("Cannot navigate from game menu to " + menuName + " menu");
            }
        }

        return Result.error("Invalid menu navigation command");
    }

    private void showInventory() {
        App.getGame().getCurrentPlayer().getBackpack().showInventory();
    }
    // TODO: add items should be checked -> Mostafa
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
        boolean flag = checkItem(item) && player.getBackpack().hasItems(Collections.singletonList(seedName));
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
        boolean flag = checkItem(item) && player.getBackpack().hasItems(Collections.singletonList(fertilizer));
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
            player.getBackpack().add(item);
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
                player.getBackpack().add(item);
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
                    flag = flag && player.getBackpack().hasItems(Collections.singletonList(key));
                    if (flag) {
                        player.getBackpack().add(item);
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
        boolean flag = checkItem(item) && isCooking(item) && player.getBackpack().hasItems(Collections.singletonList(name));
        //TODO : checking refrigerator.
        //TODO : we must check inventory is full or not.
        if (flag) {
            CookingItem cookingItem = (CookingItem) item;
            //TODO : decrease energy.
            Food food = cookingItem.cook(player.getBackpack());
            player.getBackpack().add(food);
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
        boolean flag = checkItem(item) && player.getBackpack().hasItems(Collections.singletonList(foodName)) && isFood(item);
        if (flag) {
            Food food = (Food) item;
            //TODO : advance energy.
            player.getBackpack().remove(food);
        }
    }

    private boolean isFood(Item item) {
        if (item instanceof Food) {
            return true;
        }
        System.out.println("item is not a food");
        return false;
    }


    public Result showEnergy() {
        return Result.success(String.format("%s", App.getGame().getCurrentPlayer().getEnergy()));
    }

    public Result setEnergy(String[] args) {
        int amount = Integer.parseInt(args[0]);
        App.getGame().getCurrentPlayer().setEnergy(amount);
        return Result.success("energy set to: " + amount);
    }

    public Result energyUnlimited() {
        App.getGame().getCurrentPlayer().setEnergyUnlimited();
        return Result.success("energy unlimited");
    }


    //artisan related
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

    // Tool-related methods

    private Result equipTool(String[] args) {
        if (args == null || args.length < 1) {
            return Result.error("Tool name not specified");
        }

        String toolName = args[0];
        boolean success = player.equipTool(toolName);

        if (success) {
            return Result.success("Tool " + toolName + " equipped successfully");
        } else {
            return Result.error("Tool " + toolName + " not found in backpack");
        }
    }

    private Result showCurrentTool() {
        Tool currentTool = player.getCurrentTool();

        if (currentTool == null) {
            return Result.error("No tool is currently equipped");
        } else {
            return Result.success("Currently equipped tool: " + currentTool.getName());
        }
    }


    private Result showAvailableTools() {
        List<Tool> tools = player.getAvailableTools();

        if (tools.isEmpty()) {
            return Result.error("No tools available in backpack");
        } else {
            StringBuilder sb = new StringBuilder("Available tools in backpack:\n");
            for (Tool tool : tools) {
                sb.append("- ").append(tool.getName()).append("\n");
            }
            return Result.success(sb.toString());
        }
    }


    private Result upgradeTool(String[] args) {
        if (args == null || args.length < 1) {
            return Result.error("Tool name not specified");
        }

        String toolName = args[0];
        boolean success = player.upgradeTool(toolName);

        if (success) {
            return Result.success("Tool " + toolName + " upgraded successfully");
        } else {
            return Result.error("Failed to upgrade tool " + toolName + ". Make sure you are in a blacksmith and have enough resources.");
        }
    }


    private Result useTool(String[] args) {
        if (args == null || args.length < 1) {
            return Result.error("Direction not specified");
        }

        String direction = args[0];
        boolean success = player.useTool(direction);

        if (success) {
            return Result.success("Tool used successfully in direction " + direction);
        } else {
            Tool currentTool = player.getCurrentTool();
            if (currentTool == null) {
                return Result.error("No tool is currently equipped");
            } else if (player.getEnergy() <= 0) {
                return Result.error("Not enough energy to use this tool");
            } else {
                return Result.error("Failed to use tool in direction " + direction + ". Make sure you're using it on a valid tile.");
            }
        }
    }


    private Result walk(String[] args) {
        if (args == null || args.length < 2) {
            return Result.error("Coordinates not specified");
        }

        try {
            int x = Integer.parseInt(args[0]);
            int y = Integer.parseInt(args[1]);

            // Check if the destination is valid
            if (!gMap.isValidCoordinate(x, y)) {
                return Result.error("Invalid coordinates");
            }

            // Check if the destination is in another player's farm
            // TODO: Implement this check

            // Get the current location
            Location currentLocation = player.getLocation();
            Location destination = new Location(x, y, gMap.getTile(x, y));

            int energyNeeded = GameMap.calculateEnergyNeeded(currentLocation, destination);

            // Check if the player has enough energy
            if (player.getEnergy() >= energyNeeded || player.isEnergyUnlimited()) {
                // Move the player
                player.move(x, y);
                return Result.success("Walked to (" + x + ", " + y + ")");
            } else {
                Location furthestLocation = GameMap.findFurthestCanGo(currentLocation, destination);
                player.setEnergy(0);
                player.move(furthestLocation.xAxis, furthestLocation.yAxis);
                return Result.error("You don't have enough energy to reach the destination. You collapsed at (" +
                        furthestLocation.xAxis + ", " + furthestLocation.yAxis + ")");
            }
        } catch (NumberFormatException e) {
            return Result.error("Invalid coordinates");
        }
    }

    // TODO: implement more details (Taha please)
    private Result printMap(String[] args) {
        if (args == null || args.length < 3) {
            return Result.error("Coordinates or size not specified");
        }

        try {
            int x = Integer.parseInt(args[0]);
            int y = Integer.parseInt(args[1]);
            int size = Integer.parseInt(args[2]);

            // Check if the coordinates are valid
            if (!gMap.isValidCoordinate(x, y)) {
                return Result.error("Invalid coordinates");
            }

            gMap.printCurrentView(x, y, size);

            return Result.success("Map printed");
        } catch (NumberFormatException e) {
            return Result.error("Invalid coordinates or size");
        }
    }

    private Result selectMap(String[] args) {
        if (args == null || args.length < 1) {
            return Result.error("Map number not specified");
        }

        Game game = App.getGame();
        if (game == null) {
            return Result.error("No active game");
        }

        if (!game.isInMapSelectionPhase()) {
            return Result.error("Map selection phase is over");
        }

        if (game.getCurrentPlayer() != player) {
            return Result.error("It's not your turn to select a map");
        }

        try {
            int mapNumber = Integer.parseInt(args[0]);
            if (mapNumber < 1 || mapNumber > 4) {
                return Result.error("Invalid map number. Please choose a number between 1 and 4.");
            }

            game.selectMap(player, mapNumber);

            // Move to the next player's turn
            game.nextTurn();

            // If all players have selected a map, start the game
            if (game.allPlayersSelectedMap()) {
                game.setMapSelectionPhase(false);
                return Result.success("All players have selected their maps. The game has started!");
            }

            return Result.success("Map " + mapNumber + " selected. It's now " + game.getCurrentPlayer().getUser().getUsername() + "'s turn to select a map.");
        } catch (NumberFormatException e) {
            return Result.error("Invalid map number format");
        }
    }


    private Result exitGame() {
        Game game = App.getGame();
        if (game == null) {
            return Result.error("No active game");
        }

        if (game.getGameCreator() != player) {
            return Result.error("Only the game creator can exit the game");
        }

        // Save the game
        App.saveCurrentGame();

        // Return to main menu
        appView.navigateMenu(new MainMenu(appView, player.getUser()));

        return Result.success("Game saved and exited");
    }

    private Result nextTurn() {
        Game game = App.getGame();
        if (game == null) {
            return Result.error("No active game");
        }

        if (game.isInMapSelectionPhase()) {
            return Result.error("Cannot advance turn during map selection phase");
        }

        if (game.getCurrentPlayer() != player) {
            return Result.error("It's not your turn");
        }

        // Move to the next player's turn
        game.nextTurn();

        return Result.success("Turn advanced. It's now " + game.getCurrentPlayer().getUser().getUsername() + "'s turn.");
    }

    private Result voteTerminate(String[] args) {
        if (args == null || args.length < 1) {
            return Result.error("Vote (yes/no) not specified");
        }

        Game game = App.getGame();
        if (game == null) {
            return Result.error("No active game");
        }

        if (game.isInMapSelectionPhase()) {
            return Result.error("Cannot vote during map selection phase");
        }

        String vote = args[0].toLowerCase();
        boolean voteValue;

        if (vote.equals("yes")) {
            voteValue = true;
        } else if (vote.equals("no")) {
            voteValue = false;
        } else {
            return Result.error("Invalid vote. Please specify 'yes' or 'no'.");
        }

        game.voteToTerminate(player, voteValue);

        // If all players voted to terminate, remove the game
        if (game.allPlayersVotedToTerminate()) {
            App.removeGame(game);
            appView.navigateMenu(new MainMenu(appView, player.getUser()));
            return Result.success("All players voted to terminate the game. Game terminated.");
        }

        return Result.success("Vote recorded. Waiting for other players to vote.");
    }
}
