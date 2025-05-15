package org.example.controllers;

import org.example.models.App;
import org.example.models.Items.*;
import org.example.models.Items.Plant;
import org.example.models.MapDetails.Farm;
import org.example.models.MapDetails.GameMap;
import org.example.models.MapDetails.Village;
import org.example.models.Player.Backpack;
import org.example.models.Player.Player;
import org.example.models.common.Date;
import org.example.models.common.Location;
import org.example.models.common.Result;
import org.example.models.entities.*;
import org.example.models.enums.Npcs;
import org.example.models.enums.Seasons;
import org.example.models.enums.Types.ItemBuilder;
import org.example.models.enums.Types.PlantType;
import org.example.models.enums.Types.TileType;
import org.example.models.enums.Weather;
import org.example.models.enums.commands.GameMenuCommands;
import org.example.views.AppView;
import org.example.views.MainMenu;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameMenuController implements Controller {
    private final AppView appView;
    private final Player player;
    private final Date gameClock;
    private final GameMap gMap = App.getGame().getGameMap();

    public GameMenuController(AppView appView, Player player) {
        this.appView = appView;
        this.player = player;
        this.gameClock = new Date();
    }

    @Override
    public Result update(String input) {

        GameMenuCommands command = GameMenuCommands.getCommand(input);
        String[] args = command.parseInput(input);
        Result result = null;

        switch (command) {
            // game related commands
            case SelectMap -> result = selectMap(args);
            case ExitGame -> result = exitGame();
            case NextTurn -> result = nextTurn();
            case VoteTerminate -> result = voteTerminate(args);

            // time-related commands
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
                App.saveData();
                result = Result.success("Game saved successfully");
            }
            case AutoSave -> {
                result = Result.success("Auto-save completed");
            }


            //plants and foraging related commands
            case CraftInfo -> result = craftInfo(args);
            case Plant -> result = plant(args);
            case ShowPlant -> result = showPlant(args);
            case Fertilize -> result = fertilize(args);
            case HowMuchWater -> result = howMuchWater();
            case GiveWater -> result = giveWater(args);
            case Harvest -> result = harvest(args);
            case AddItem -> result = addItem(args);

            //crafting related commands
            case CraftingShowRecipes -> craftingShowRecipes();


            case CookingShowRecipes -> cookingShowRecipes();
            case EatFood -> result = eatFood(args);
            case ShowEnergy -> result = showEnergy();
            case setEnergy -> result = setEnergy(args);
            case energyUnlimited -> result = energyUnlimited();


            //sell command:
            case SellProduct -> result = sellProduct(args);

            // tool commands
            case ToolEquip -> result = equipTool(args);
            case ToolShowCurrent -> result = showCurrentTool();
            case ToolShowAvailable -> result = showAvailableTools();
            case ToolUse -> result = useTool(args);

            // Greenhouse-related commands
            case GreenhouseBuild -> result = greenhouseBuild();

            // Walking and map commands
            case Walk -> result = walk(args);
            case PrintMap -> result = printMap(args);
            case TeleportToVillage -> result = teleportToVillage();
            case TeleportToMarket -> result = teleportToMarket(args);
            case TeleportToHome -> result = teleportToHome();

            case HelpReadingMap -> result = helpReadingMap();

            // player related commands
            case FriendshipStatus -> result = friendShipsStatus();
            case TalkToPlayer -> result = talkToPlayer(args);
            case TalkHistory -> result = talkHistory(args);
            case GiftToPlayer -> result = giftToPlayer(args);
            case GiftList -> result = giftList();
            case GiftRate -> result = giftRate(args);
            case GiftHistory -> result = giftHistory(args);
            case HugPlayer -> result = hugPlayer(args);
            case FlowerPlayer -> result = flowerPlayer(args);
            case AskToMarry -> result = askMarriage(args);
            case RespondToMarry -> result = respondToMarriage(args);

            // NPC-related commands
            case MeetNPC -> result = meetNPC(args);
            case GiftNPC -> result = giftNPC(args);
            case FriendshipNPCList -> result = friendshipNPCList();

            // Quest-related commands
            case QuestsList -> result = questsList();
            case QuestsFinish -> result = questsFinish(args);

            // Trade-related commands
            case StartTrade -> result = startTrade();
            case TradeRequest -> result = tradeRequest(args);
            case TradeList -> result = tradeList();
            case TradeResponse -> result = tradeResponse(args);
            case TradeHistory -> result = tradeHistory();

            case ShowCurrentMenu -> result = Result.success("Game Menu");

            case CheatSetBackPackFull -> cheatBackPackFull();
            case CheatAddFavourites -> cheatAddFavourites(args);
            case CheatTeleport -> cheatTeleport(args);

            case None -> result = Result.error("Invalid command");
        }

        appView.handleResult(result, command);

        return result;
    }

    private void cheatTeleport(String[] args) {
        int x = Integer.parseInt(args[0]);
        int y = Integer.parseInt(args[1]);
        Location location = player.getCurrentFarm().getItem(x, y);
        System.out.println(location.getTile().toString());
        System.out.println(player.getCurrentFarm().getFarmIndex());
        System.out.println(player.getUser().getUsername());
        if (location.getTile() == TileType.GRASS) {
            System.out.println("hey");
            player.setLocation(location);
        }
    }

    private Result teleportToHome() {
        return Result.error("");
    }

    private Result teleportToMarket(String[] args) {
        return Result.success("");
    }

    private void showInventory() {
        App.getGame().getCurrentPlayer().getBackpack().showInventory();
    }
    // TODO: add items should be checked -> Mostafa

    // ====== time related ======

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
            gameClock.advanceTime(hours, gMap);
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

            gameClock.advanceDays(days, gMap);
            return Result.success("Date advanced by " + days + " days");
        } catch (NumberFormatException e) {
            return Result.error("Invalid number format for days");
        }
    }

    public Result cheatThor(String[] args) {
        if (args == null || args.length < 1) {
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

            Location location = player.getCurrentFarm().getItem(x, y);
            gMap.getFarmByPlayer(player).thor(location);
            gameClock.cheatThor(location);

            return Result.success("Thor has struck at location (" + x + "," + y + ")");
        } catch (NumberFormatException e) {
            return Result.error("Invalid coordinates format");
        }
    }


    // ====== plants and foraging related ======

    private Result craftInfo(String[] args) {
        String name = args[0];
        Item item = ItemBuilder.build(name);
        if (item == null) {
            return Result.error("Item does not exist");
        }
        item.showInfo();
        return Result.success("");
    }


    private Result plant(String[] args) {
        String seedName = args[0];
        String direction = args[1];

        int[] dir = getDirection(direction);
        if (dir == null) {
            return Result.error("Invalid direction");
        }


        Item item = player.getBackpack().getItem(seedName);
        Location loc = player.getLocation();
        int x = loc.getX() + dir[1];
        int y = loc.getY() + dir[0];

        if (item == null) {
            return Result.error(seedName + " does not exist in backpack.");
        }
        if (!player.getBackpack().hasItems(Collections.singletonList(seedName))) {
            return Result.error("Backpack does not contain " + seedName);
        }
        if(!gMap.getFarmByPlayer(player).contains(x,y)){
            return Result.error("this is not your farm!!!");
        }
        if (!gMap.getFarmByPlayer(player).isPlowed(x, y)) {
            return Result.error("the land is not plowed!");
        }
        if (gMap.getFarmByPlayer(player).getItem(x, y).getItem() != null) {
            return Result.error("there is an item on the ground");
        }
        if (!(item instanceof Plant || item instanceof Tree || item instanceof Seed)) {
            return Result.error("item is not plant");
        }


        if (item instanceof Seed) {
            if (seedName.equals("Mixed Seeds")) {
                seedName = gameClock.getSeason().getRandomSeed();
                PlantType type = PlantType.fromSeed(seedName);
                if (type == null) {
                    return Result.error("Plant type not found");
                }
                item = new Plant(type);
            }

            Item seedItem = ItemBuilder.build(seedName);
            Seed seed = (Seed) seedItem;
            Seasons[] seasons = seed.getSeason();
            int counter = 0;

            for (Seasons season : seasons) {
                if (gameClock.getSeason() == season) {
                    counter++;
                }
            }

            if (counter == 0) {
                return Result.error("This seed is not for this season.");
            }


            gMap.getFarmByPlayer(player).placeItem(x, y, item);
        } else if (item instanceof Tree) {
            Tree tree = (Tree) item;
            Seasons[] seasons = tree.getSeasons();
            int counter = 0;
            for (Seasons season : seasons) {
                if (gameClock.getSeason() == season) {
                    counter++;
                }
            }
            if (counter == 0) {
                return Result.error("This Tree is not for this season.");
            }
            gMap.getFarmByPlayer(player).placeItem(x, y, tree);
        }
        player.getSkills().get(0).updateLevel();
        return Result.success(seedName + "planted successfully!");

    }


    private int[] getDirection(String direction) {
        int[] dir = new int[]{0, 0};
        switch (direction) {
            case "north":
                dir[0] = -1;
                break;
            case "south":
                dir[0] = 1;
                break;
            case "east":
                dir[1] = 1;
                break;
            case "west":
                dir[1] = -1;
                break;
            case "north-east":
                dir[0] = -1;
                dir[1] = 1;
                break;
            case "north-west":
                dir[0] = -1;
                dir[1] = -1;
                break;
            case "south-east":
                dir[0] = 1;
                dir[1] = 1;
                break;
            case "south-west":
                dir[0] = 1;
                dir[1] = -1;
                break;
        }
        return dir;
    }


    //this method is completed.
    private Result showPlant(String[] args) {
        int x = Integer.parseInt(args[0]);
        int y = Integer.parseInt(args[1]);

        Location location = gMap.getFarmByPlayer(player).getItem(x, y);

        if (location == null || location.getItem() == null) {
            return Result.error("Item does not exist in " + "(" + x + "," + y + ")");
        }
        Item item = location.getItem();
        item.showInfo();
        player.getSkills().get(0).updateLevel();
        return Result.success("");
    }


    //we dont have fertilize
    private Result fertilize(String[] args) {
        String fertilizer = args[0];
        Item item = ItemBuilder.build(fertilizer);
        Location location = player.getLocation();
        String direction = args[1];
        int[] dir = getDirection(direction);
        int x = location.getX() + dir[1];
        int y = location.getY() + dir[0];
        if (item == null) {
            return Result.error("Fertilizer" + fertilizer + " does not exist.");
        }
        if (!player.getBackpack().hasItems(Collections.singletonList(fertilizer))) {
            return Result.error("Backpack does not contain " + fertilizer);
        }

        Location targetLocation = gMap.getFarmByPlayer(player).getItem(x, y);
        if (targetLocation == null || targetLocation.getItem() == null || !(targetLocation.getItem() instanceof Tool)) {
            return Result.error("Fertilizer" + fertilizer + " is not a tool");
        }
        player.getSkills().get(0).updateLevel();
        return Result.success("fertilized successfully with" + fertilizer);
    }


    private Result giveWater(String[] args) {
        String direction = args[0];
        Location location = player.getLocation();
        int[] dir = getDirection(direction);
        if (dir == null) {
            return Result.error("Invalid direction");
        }

        int x = location.getX() + dir[1];
        int y = location.getY() + dir[0];

        Location targetLocation = gMap.getFarmByPlayer(player).getItem(x, y);
        if (targetLocation == null || targetLocation.getItem() == null) {
            return Result.error("Item does not exist in " + "(" + x + "," + y + ")");
        }

        Item item = targetLocation.getItem();
        if (!(item instanceof Tree || item instanceof Plant)) {
            return Result.error("Item is not a Plant or a Tree");
        }
        if (item instanceof Tree) {
            Tree tree = (Tree) item;
            tree.setMoisture(true);
        } else if (item instanceof Plant) {
            Plant plant = (Plant) item;
            plant.setMoisture(true);
        }
        player.getSkills().get(0).updateLevel();
        return Result.success("Water is given now.");
    }


    private Result howMuchWater() {
        return Result.success("How much water has been cheated");
    }


    //this method is completed.
    private Result harvest(String[] args) {
        int x = Integer.parseInt(args[0]);
        int y = Integer.parseInt(args[1]);

        Location targetLocation = gMap.getFarmByPlayer(player).getItem(x, y);
        if (targetLocation == null || targetLocation.getItem() == null) {
            return Result.error("Plant does not exist in " + "(" + x + "," + y + ")");
        }

        Item item = targetLocation.getItem();
        if (!((item instanceof Plant) || (item instanceof Tree) || (item instanceof Crop))) {
            return Result.error("Item is not harvestable");
        }
        if (!item.getFinished()) {
            return Result.error("Plant is not ready yet");
        }

        if (item instanceof Tree) {
            Tree tree = (Tree) item;
            Item fruit = tree.getFruit();
            if (fruit == null) {
                return Result.error("fruit is not ready yet");
            }
            boolean stack = player.getBackpack().add(fruit, 1);
            if(!stack){
                return Result.error("Backpack is full!");
            }
            tree.setFruitCounter(0);
            tree.setFruitFinished(false);
            player.getSkills().get(0).updateLevel();
        }
        if (item instanceof Plant) {
            Plant plant = (Plant) item;
            Item fruit = plant.getFruit();
            if (fruit == null) {
                return Result.error("fruit is not ready yet");
            }
            boolean stack = player.getBackpack().add(fruit, 1);
            if(!stack){
                return Result.error("Backpack is full!");
            }
            if (plant.getOneTimeHarvest()) {
                gMap.getFarmByPlayer(player).placeItem(x, y, null);
                gMap.getFarmByPlayer(player).getItem(x,y).setTile(TileType.GRASS);
                gMap.getFarmByPlayer(player).getItem(x,y).setType("grass");
            } else {
                plant.setStages(new int[]{1});
                plant.setDaysCounter(plant.getRegrowthTime());
                plant.setFinished(false);
            }
            player.getSkills().get(0).updateLevel();
        }
        if (item instanceof Crop) {
            Crop crop = (Crop) item;
            Item fruit = crop.getFruit();
            if (fruit == null) {
                return Result.error("fruit is not ready yet");
            }
            boolean stack = player.getBackpack().add(fruit, 1);
            if(!stack){
                return Result.error("Backpack is full!");
            }
            gMap.getFarmByPlayer(player).getItem(x,y).setItem(null);
            gMap.getFarmByPlayer(player).getItem(x,y).setType("grass");
            gMap.getFarmByPlayer(player).getItem(x,y).setTile(TileType.GRASS);
            player.getSkills().get(2).updateLevel();
        }
        return Result.success("Plant has been harvested!");
    }

    private Result addItem(String[] args) {
        String itemName = args[0];
        int count = Integer.parseInt(args[1]);
        Item item = ItemBuilder.build(itemName);
        if (item == null) {
            return Result.error("Item does not exist");
        }
        player.getBackpack().add(item, count);
        return Result.success(count + " " + itemName + " has been added to the backpack");
    }


    //this method is completed
    private void craftingShowRecipes() {
        List<CraftingItem> craftingItems = player.getCraftingItems();
        for (CraftingItem craftingItem : craftingItems) {
            craftingItem.showInfo();
        }
    }

    private void cookingShowRecipes() {
        for (CookingItem cookingItem : player.getCookingItems()) {
            cookingItem.showInfo();
        }
    }


    private boolean isCooking(Item item) {
        return item instanceof CookingItem;
    }

    //this method is completed now
    private Result eatFood(String[] args) {
        String foodName = args[0];
        Item item = ItemBuilder.build(foodName);
        if (item == null) {
            return Result.error("Item does not exist");
        }
        if (!player.getBackpack().hasItems(Collections.singletonList(foodName))) {
            return Result.error(foodName + " does not exist in backpack");
        }
        if (!(item instanceof Food || item instanceof ArtisanItem)) {
            return Result.error("Item is not a Food or ArtisanItem");
        }
        if (item instanceof ArtisanItem) {
            ArtisanItem artisanItem = (ArtisanItem) item;
            if (artisanItem.getEnergy() > 0) {
                player.increaseEnergy(artisanItem.getEnergy());
                player.getBackpack().remove(item, 1);
                return Result.success("Food " + foodName + " eaten");
            } else {
                return Result.success("Artisan item is not a food.");
            }
        }
        Food food = (Food) item;
        player.increaseEnergy(food.getEnergy());
        player.getBackpack().remove(item, 1);
        //TODO : adding buffer.
        return Result.success("Food " + foodName + " eaten");
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


    public boolean checkItems(String items, CraftingItem craftingItem) {
        String regex = craftingItem.checkRegex(items);
        if (regex != null) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(items);
            String itemName = matcher.group(1);
            if (player.getBackpack().hasItems(Collections.singletonList(itemName))) {
                return true;
            }
        }
        return false;
    }


    //sell Function:
    private Result sellProduct(String[] args) {
        String productName = args[0];
        if (!player.getBackpack().hasItems(Collections.singletonList(productName))) {
            return Result.error(productName + " does not exist in your backpack");
        }
        Item item = player.getBackpack().getItem(productName);
        int count;
        if (args[1] != null) {
            count = Integer.parseInt(args[1]);
        } else {
            count = player.getBackpack().getInventory().get(item);
        }

        ShippingBin bin = new ShippingBin();
        //gMap.getItemNearby()
        if (bin == null) {
            return Result.error("There is no shipping bin nearby!");
        }
        int amount;
        if (item.getPrice() != 0) {
            amount = count * item.getPrice();
        } else {
            amount = count * 100;
        }


        //checking instance
        if (item instanceof Tool) {
            Tool tool = (Tool) item;
            switch (tool.getMaterial()) {
                case BASIC -> amount *= 1;
                case IRON -> amount *= 2;
                case COPPER -> amount *= 2;
                case GOLD -> amount *= 3;
                case IRIDIUM -> amount *= 4;
            }
        }


        bin.increaseIncome(amount, player);
        return Result.success("your product: " + productName + " has been sold!");
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


    private Result useTool(String[] args) {
        if (args == null || args.length < 1) {
            return Result.error("Direction not specified");
        }

        String direction = args[0];

        // Check if the player has a tool equipped
        Tool currentTool = player.getCurrentTool();
        if (currentTool == null) {
            return Result.error("No tool is currently equipped");
        }

        // Check if the player has enough energy
        if (player.getEnergy() <= 0) {
            return Result.error("Not enough energy to use this tool");
        }

        boolean success = player.useTool(direction, gMap);

        if (success) {
            return Result.success("Tool used successfully in direction " + direction);
        } else {
            // Check if the failure is due to energy limit per turn
            if (!player.canUseEnergy(1)) { // Using a small value to check if any energy can be used
                return Result.error("You've used too much energy this turn. Use 'next turn' command to proceed to the next player's turn.");
            } else {
                return Result.error("Failed to use tool in direction " + direction + ". Make sure you're using it on a valid tile.");
            }
        }
    }

    private Result teleportToVillage() {
        Farm farm = player.getCurrentFarm();
        if (farm == null) {
            return Result.error("shasidiiii");
        }
        if (player.getIsInVillage()) {
            return Result.error("You can't teleport to a village because you are in a village");
        }
        if (!player.checkTeleportToVillage()) {
            return Result.error("You are not in specefic map");
        }
        return Result.success("teleported to a village");
    }

    private Result walk(String[] args) {
        if (args == null || args.length < 2) {
            return Result.error("Coordinates not specified");
        }

        try {
            int x = Integer.parseInt(args[0]);
            int y = Integer.parseInt(args[1]);

            // Check if the destination is valid
            if (!gMap.getFarmByPlayer(player).contains(x, y)) {
                return Result.error("Invalid coordinates");
            }

            // Check if the destination is in another player's farm
            // TODO: Implement this check

            // Get the current location
            Location currentLocation = player.getLocation();
            //Location destination = new Location(x, y, gMap.getFarmByPlayer(player).getTile(x, y));
            Location destination = player.getCurrentFarm().getItem(x, y);

            int energyNeeded = gMap.getFarmByPlayer(player).calculateEnergyNeeded(currentLocation, destination);

            // Check if the player has used too much energy this turn
            if (!player.canUseEnergy(energyNeeded)) {
                return Result.error("You've used too much energy this turn. Use 'next turn' command to proceed to the next player's turn.");
            }

            // Check if the player has enough energy
            if (player.getEnergy() >= energyNeeded || player.isEnergyUnlimited()) {
                // Move the player
                if (player.getCurrentFarm().walk(x, y) <= 0) {
                    return Result.error("You've used too much energy");
                }
                return Result.success("Walked to (" + x + ", " + y + ")");
            }
            else {
                Location furthestLocation = gMap.getFarmByPlayer(player).findFurthestCanGo(currentLocation, destination);
                player.setEnergy(0);
                player.getCurrentFarm().walk(furthestLocation.xAxis, furthestLocation.yAxis);
                return Result.error("You don't have enough energy to reach the destination. You collapsed at (" +
                        furthestLocation.xAxis + ", " + furthestLocation.yAxis + ")");
            }
        }
        catch (NumberFormatException e) {
            return Result.error("Invalid coordinates");
        }
    }

    private Result printMap(String[] args) {
        if (args == null || args.length < 3) {
            return Result.error("Coordinates or size not specified");
        }

        try {
            int x = Integer.parseInt(args[0]);
            int y = Integer.parseInt(args[1]);
            int size = Integer.parseInt(args[2]);

            // Check if the coordinates are valid
            if (!App.getGame().getGameMap().getFarmByPlayer(player).contains(x, y)) {
                return Result.error("Invalid coordinates");
            }

            // Check if the location is in another player's farm
            if (App.getGame().getGameMap().isInOtherPlayersFarm(player, x, y)) {
                return Result.error("You cannot view another player's farm");
            }

            //System.out.println("Printing map with center at (" + x + ", " + y + ") and radius " + size + ":");
            if (player.getIsInVillage()) {
                App.getGame().getGameMap().getVillage().printCurrentViewColored(x, y, size, player);
                return Result.success("Village printed");
            }
            App.getGame().getGameMap().getFarmByPlayer(player).printCurrentViewColored(x, y, size);
            System.out.println("Map printed successfully!");

            return Result.success("Map printed");
        }
        catch (NumberFormatException e) {
            return Result.error("Invalid coordinates or size");
        }
    }


    private Result helpReadingMap() {
        System.out.println("Map Legend:");
        System.out.println("'.' - Grass");
        System.out.println("'=' - Tilled Soil");
        System.out.println("'T' - Tree");
        System.out.println("'S' - Stone");
        System.out.println("'~' - Water");
        System.out.println("'#' - Path");
        System.out.println("'H' - House");
        System.out.println("'V' - Village");
        System.out.println("'B' - Bridge");
        System.out.println("' ' - Empty");
        System.out.println("'@' - Player Position");
        return Result.success("Map legend displayed");
    }

    public Result selectMap(String[] args) {

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

        try {
            int mapIndex = Integer.parseInt(args[0]);
            if (mapIndex < 1 || mapIndex > 4) {
                return Result.error("Invalid map number. Please choose a number between 1 and 4.");
            }

            System.out.println("What type of map do you want to select? (two lakes | bigger quarry)");
            boolean farmType;

            while (true) {
                String input = new Scanner(System.in).nextLine().trim();
                if (input.matches("two\\s+lakes") || input.matches("bigger\\s+quarry")) {
                    if (input.matches("two\\s+lakes")) {
                        farmType = true;
                        System.out.println("Selected two lakes");
                    } else {
                        farmType = false;
                        System.out.println("Selected bigger quarry");
                    }
                    break;
                } else {
                    System.out.println("Invalid input. Please try again.");
                }
            }

            Farm newFarm = new Farm("mazrae'e", player, farmType, mapIndex - 1);
            player.setCurrentFarm(newFarm);
            Village village = App.getGame().getGameMap().getVillage();
            player.setCurrentVillage(village);
            App.getGame().getGameMap().addFarm(newFarm);

            game.selectMap(player, mapIndex);
            game.nextTurn(gMap);

            // If all players have selected a map, start the game
            if (game.allPlayersSelectedMap()) {
                App.makeAllChose();
                return Result.success("All players have selected their maps. The game has started!");
            }

            return Result.success("Map " + mapIndex + " selected. It's now " + game.getCurrentPlayer().getUser().getUsername() + "'s turn to select a map.");
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
//        App.saveCurrentGame();

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
        game.nextTurn(gMap);

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

    // TODO: check if the items required are right
    private Result greenhouseBuild() {
        int requiredWood = 500;
        int requiredStone = 1000;

        Item woodItem = App.getItem("Wood");
        if (woodItem == null) {
            return Result.error("Wood item not found in the game.");
        }

        int woodCount = 0;
        for (Map.Entry<Item, Integer> entry : player.getBackpack().getInventory().entrySet()) {
            if (entry.getKey().getName().equalsIgnoreCase("Wood")) {
                woodCount = entry.getValue();
                break;
            }
        }

        if (woodCount < requiredWood) {
            return Result.error("Not enough wood. You need " + requiredWood + " wood to build a greenhouse.");
        }

        Item stoneItem = App.getItem("Stone");
        if (stoneItem == null) {
            return Result.error("Stone item not found in the game.");
        }

        int stoneCount = 0;
        for (Map.Entry<Item, Integer> entry : player.getBackpack().getInventory().entrySet()) {
            if (entry.getKey().getName().equalsIgnoreCase("Stone")) {
                stoneCount = entry.getValue();
                break;
            }
        }

        if (stoneCount < requiredStone) {
            return Result.error("Not enough stone. You need " + requiredStone + " stone to build a greenhouse.");
        }


        player.getBackpack().remove(woodItem, requiredWood);


        player.getBackpack().remove(stoneItem, requiredStone);


        // The greenhouse is a 5x6 grid (without counting the wall)
        Location leftCorner = new Location(10, 10, TileType.GREENHOUSE);
        Location rightCorner = new Location(16, 15, TileType.GREENHOUSE);

        return Result.success("Greenhouse built successfully! You can now plant crops regardless of the season.");
    }

    private Result friendShipsStatus() {
        Player player = App.getGame().getCurrentPlayer();
        StringBuilder result = new StringBuilder();
        result.append("friendships status:\n");

        Map<Player, Friendship> friendships = player.getAllFriendships();

        if (friendships.isEmpty()) {
            result.append("You have no friendships yet.");
        } else {
            for (Map.Entry<Player, Friendship> entry : friendships.entrySet()) {
                Player friend = entry.getKey();
                Friendship friendship = entry.getValue();

                result.append("- ").append(friend.getUser().getUsername())
                        .append(": Level ").append(friendship.getLevel())
                        .append(" (").append(friendship.getXp()).append("/")
                        .append(friendship.getMaxXpForCurrentLevel()).append(" XP)");

                if (friendship.isMarried()) {
                    result.append(" [Married]");
                }

                result.append("\n");
            }
        }

        return Result.success(result.toString());
    }

    private Result talkToPlayer(String[] args) {
        Player currentPlayer = App.getGame().getCurrentPlayer();
        String username = args[0];
        String message = args[1];

        // Find the target player
        Player targetPlayer = null;
        for (Player player : App.getGame().getPlayers()) {
            if (player.getUser().getUsername().equals(username)) {
                targetPlayer = player;
                break;
            }
        }

        if (targetPlayer == null) {
            return Result.error("Player with username " + username + " not found.");
        }

        // Get or create friendship
        Friendship friendship = currentPlayer.getFriendship(targetPlayer);
        if (friendship == null) {
            return Result.error("Friendship with " + username + " not found.");
        }

        // Talk to the player
        boolean success = friendship.talk(message, currentPlayer);
        if (!success) {
            if (!arePlayersNearEachOther(currentPlayer, targetPlayer)) {
                return Result.error("You need to be near " + username + " to talk to them.");
            } else {
                return Result.error("You have already talked to " + username + " today.");
            }
        }

        return Result.success("Message sent to " + username + ". Friendship increased by " + Friendship.XP_TALK + " XP.");
    }

    private Result talkHistory(String[] args) {
        Player currentPlayer = App.getGame().getCurrentPlayer();
        String username = args[0];

        // Find the target player
        Player targetPlayer = null;
        for (Player player : App.getGame().getPlayers()) {
            if (player.getUser().getUsername().equals(username)) {
                targetPlayer = player;
                break;
            }
        }

        if (targetPlayer == null) {
            return Result.error("Player with username " + username + " not found.");
        }

        Friendship friendship = currentPlayer.getFriendship(targetPlayer);
        if (friendship == null) {
            return Result.error("Friendship with " + username + " not found.");
        }

        List<String> chatHistory = friendship.getChatHistory();
        if (chatHistory.isEmpty()) {
            return Result.success("No chat history with " + username + ".");
        }

        StringBuilder result = new StringBuilder();
        result.append("Chat history with ").append(username).append(":\n");

        for (String message : chatHistory) {
            result.append(message).append("\n");
        }

        return Result.success(result.toString());
    }

    private boolean arePlayersNearEachOther(Player player1, Player player2) {
        Location loc1 = player1.getLocation();
        Location loc2 = player2.getLocation();

        int distance = Math.abs(loc1.xAxis - loc2.xAxis) + Math.abs(loc1.yAxis - loc2.yAxis);

        return distance <= 2;
    }

    public Result giftToPlayer(String[] args) {
        String username = args[0];
        String itemName = args[1];
        int amount = Integer.parseInt(args[2]);
        User user = App.getUser(username);
        if (user == null) {
            return Result.error("User " + username + " not found.");
        }

        Player targetPlayer = App.getGame().getPlayer(user);
        if (targetPlayer == null) {
            return Result.error("Player " + username + " not found.");
        }
        Item item = App.getItem(itemName);
        if (item == null) {
            return Result.error("Item " + itemName + " not found.");
        }


        if (App.getGame().getCurrentPlayer().getFriendship(targetPlayer).gift(item, App.getGame().getCurrentPlayer(), amount)) {
            return Result.success("You have gifted " + amount + " " + itemName + "to " + username + ".");
        } else {
            if (arePlayersNearEachOther(App.getGame().getCurrentPlayer(), targetPlayer)) {
                return Result.error("you must be near the target to gift them");
            } else if (App.getGame().getCurrentPlayer().getFriendship(targetPlayer).getLevel() < 1) {
                return Result.error("You must be friends with " + username + " to gift them.");
            } else {
                return Result.error("You have already gifted " + username + " today.");
            }
        }
    }

    public Result giftList() {
        Player currentPlayer = App.getGame().getCurrentPlayer();
        StringBuilder result = new StringBuilder();
        for (Friendship friendShip : currentPlayer.getAllFriendships().values()) {
            result.append("gift history with: ").append(friendShip.getTheOtherPlayer(currentPlayer).getUser().getUsername()).append("\n");
            friendShip.getGiftHistory().forEach(gift -> result.append("~Gift Name: ").append(gift).append("\n").append("~Amount: ").append(gift.getAmount()).append("\n"));
        }
        return Result.success(result.toString());
    }

    public Result giftRate(String[] args) {
        int giftIndex = Integer.parseInt(args[0]);
        int rating = Integer.parseInt(args[1]);

        Player currentPlayer = App.getGame().getCurrentPlayer();

        // Check if the rating is valid
        if (rating < 1 || rating > 5) {
            return Result.error("Rating must be between 1 and 5.");
        }

        // Find the gift in the player's gift history
        boolean ratedAny = false;
        for (Friendship friendship : currentPlayer.getAllFriendships().values()) {
            if (friendship.rateGift(giftIndex - 1, rating)) {
                ratedAny = true;
                int xpChange = 15 + 30 * (rating - 3);
                return Result.success("Gift rated successfully. Friendship " +
                        (xpChange >= 0 ? "increased" : "decreased") + " by " + Math.abs(xpChange) + " XP.");
            }
        }

        if (!ratedAny) {
            return Result.error("Gift with index " + giftIndex + " not found or already rated.");
        }

        return Result.success("Gift rated successfully.");
    }

    public Result giftHistory(String[] args) {
        String username = args[0];
        Player player = App.getGame().getPlayer(App.getUser(username));
        Friendship friendship = App.getGame().getCurrentPlayer().getFriendship(player);
        return Result.success(friendship.getGiftHistory().toString());
    }

    public Result hugPlayer(String[] args) {
        String username = args[0];
        Player currentPlayer = App.getGame().getCurrentPlayer();

        // Find the target player
        Player targetPlayer = null;
        for (Player player : App.getGame().getPlayers()) {
            if (player.getUser().getUsername().equals(username)) {
                targetPlayer = player;
                break;
            }
        }

        if (targetPlayer == null) {
            return Result.error("Player with username " + username + " not found.");
        }

        // Check if players are near each other
        if (!arePlayersNearEachOther(currentPlayer, targetPlayer)) {
            return Result.error("You need to be near " + username + " to hug them.");
        }

        // Check friendship level
        Friendship friendship = currentPlayer.getFriendship(targetPlayer);
        if (friendship == null) {
            return Result.error("Friendship with " + username + " not found.");
        }

        if (friendship.getLevel() < 2) {
            return Result.error("You need to be at friendship level 2 with " + username + " to hug them.");
        }

        // Hug the player
        boolean success = currentPlayer.hugMob(targetPlayer);
        if (!success) {
            return Result.error("You have already hugged " + username + " today.");
        }

        return Result.success("You hugged " + username + ". Friendship increased by 60 XP.");
    }

    public Result flowerPlayer(String[] args) {
        String username = args[0];
        Player currentPlayer = App.getGame().getCurrentPlayer();

        // Find the target player
        Player targetPlayer = null;
        for (Player player : App.getGame().getPlayers()) {
            if (player.getUser().getUsername().equals(username)) {
                targetPlayer = player;
                break;
            }
        }

        if (targetPlayer == null) {
            return Result.error("Player with username " + username + " not found.");
        }

        // Check if players are near each other
        if (!arePlayersNearEachOther(currentPlayer, targetPlayer)) {
            return Result.error("You need to be near " + username + " to give them a flower.");
        }

        Item flower = currentPlayer.getBackpack().getItem("Flower");
        if (flower == null || currentPlayer.getBackpack().getInventory().getOrDefault(flower, 0) <= 0) {
            return Result.error("You don't have a flower to give.");
        }

        Friendship friendship = currentPlayer.getFriendship(targetPlayer);
        if (friendship == null) {
            return Result.error("Friendship with " + username + " not found.");
        }

        if (friendship.getLevel() < 2) {
            return Result.error("You need to be at friendship level 2 with " + username + " to give them a flower.");
        }

        currentPlayer.getBackpack().remove(App.getItem("Flower"), 1);
        boolean success = currentPlayer.giveBouquetTo(targetPlayer);
        if (!success) {
            return Result.error("Failed to give flower to " + username + ".");
        }

        return Result.success("You gave a flower to " + username + ". Your friendship level is now 3.");
    }

    public Result askMarriage(String[] args) {
        String username = args[0];
        String ringName = args[1];
        Player currentPlayer = App.getGame().getCurrentPlayer();

        Player targetPlayer = null;
        for (Player player : App.getGame().getPlayers()) {
            if (player.getUser().getUsername().equals(username)) {
                targetPlayer = player;
                break;
            }
        }

        if (targetPlayer == null) {
            return Result.error("Player with username " + username + " not found.");
        }

        if (!arePlayersNearEachOther(currentPlayer, targetPlayer)) {
            return Result.error("You need to be near " + username + " to propose marriage.");
        }

        Item ring = currentPlayer.getBackpack().getItem(ringName);
        if (ring == null || currentPlayer.getBackpack().getInventory().getOrDefault(ring, 0) <= 0) {
            return Result.error("You don't have a " + ringName + " to propose with.");
        }

        Friendship friendship = currentPlayer.getFriendship(targetPlayer);
        if (friendship == null) {
            return Result.error("Friendship with " + username + " not found.");
        }

        if (friendship.getLevel() < 3) {
            return Result.error("You need to be at friendship level 3 with " + username + " to propose marriage.");
        }

        if (currentPlayer.getUser().getGender().equals(targetPlayer.getUser().getGender())) {
            return Result.error("You can only propose to someone of the opposite gender.");
        }

        boolean success = currentPlayer.proposeMarriageTo(targetPlayer);
        if (!success) {
            return Result.error("Failed to propose marriage to " + username + ".");
        }

        // Remove the ring from the player's inventory
        currentPlayer.getBackpack().remove(App.getItem(ringName), 1);

        return Result.success("You proposed marriage to " + username + ". Waiting for their response.");
    }

    public Result respondToMarriage(String[] args) {
        String response = args[0];
        String username = args[1];
        Player currentPlayer = App.getGame().getCurrentPlayer();

        // Find the proposer
        Player proposer = null;
        for (Player player : App.getGame().getPlayers()) {
            if (player.getUser().getUsername().equals(username)) {
                proposer = player;
                break;
            }
        }

        if (proposer == null) {
            return Result.error("Player with username " + username + " not found.");
        }

        // Check if players are near each other
        if (!arePlayersNearEachOther(currentPlayer, proposer)) {
            return Result.error("You need to be near " + username + " to respond to their proposal.");
        }

        // Check friendship level
        Friendship friendship = currentPlayer.getFriendship(proposer);
        if (friendship == null) {
            return Result.error("Friendship with " + username + " not found.");
        }

        if (friendship.getLevel() < 3) {
            return Result.error("You need to be at friendship level 3 with " + username + " to respond to their proposal.");
        }

        if (response.equals("accept")) {
            // Accept the proposal
            proposer.marry(currentPlayer);
            currentPlayer.marry(proposer);

            // Combine money
            int totalMoney = proposer.getMoney() + currentPlayer.getMoney();
            proposer.decreaseMoney(proposer.getMoney());
            currentPlayer.decreaseMoney(currentPlayer.getMoney());
            proposer.increaseMoney(totalMoney);
            currentPlayer.increaseMoney(totalMoney);

            return Result.success("You accepted " + username + "'s marriage proposal. You are now married!");
        } else {
            friendship.decreaseXp(friendship.getXp()); // Reset friendship to level 0

            proposer.setEnergy(proposer.getEnergy() / 2);

            return Result.success("You rejected " + username + "'s marriage proposal. Your friendship level has been reset to 0.");
        }
    }

    // NPC-related methods
    private Result meetNPC(String[] args) {
        if (args == null || args.length < 1) {
            return Result.error("NPC name not specified");
        }

        String npcName = args[0];
        Npcs npcEnum = Npcs.fromName(npcName);

        if (npcEnum == null) {
            return Result.error("NPC with name " + npcName + " not found.");
        }

        Location playerLocation = player.getLocation();
        Location npcLocation = npcEnum.getLocation();

        int distance = Math.abs(playerLocation.xAxis - npcLocation.xAxis) + Math.abs(playerLocation.yAxis - npcLocation.yAxis);

        if (distance > 1) {
            return Result.error("You need to be adjacent to " + npcName + " to talk to them.");
        }

        NPC npc = createNPCFromEnum(npcEnum);

        Date currentDate = App.getGame().getDate();

        String response = player.meetNPC(npc);

        String dialogue = npcName + ": " + response;

        Map<String, String> friendships = player.getNPCFriendships();
        if (!friendships.containsKey(npcName) || friendships.get(npcName).contains("Level: 0")) {
            dialogue += "\n\n" + npcEnum.getDescription();
        }

        return Result.success(dialogue);
    }


    private NPC createNPCFromEnum(Npcs npcEnum) {
        // Create a new NPC with the properties from the enum
        HashMap<Integer, HashMap<Item, Integer>> missions = new HashMap<>();
        NPC npc = new NPC(
                npcEnum.getCharacteristic(),
                npcEnum.getName(),
                npcEnum.getJob(),
                missions
        );

        npc.setLocation(npcEnum.getLocation());
        npc.setDescription(npcEnum.getDescription());

        for (String itemName : npcEnum.getFavoriteItems()) {
            Item item = App.getItem(itemName);
            if (item != null) {
                npc.addFavoriteItem(item);
            }
        }

        return npc;
    }

    private Result giftNPC(String[] args) {
        if (args == null || args.length < 2) {
            return Result.error("NPC name or item not specified");
        }

        String npcName = args[0];
        String itemName = args[1];

        Npcs npcEnum = Npcs.fromName(npcName);
        if (npcEnum == null) {
            return Result.error("NPC with name " + npcName + " not found.");
        }

        Item item = App.getItem(itemName);
        if (item == null) {
            return Result.error("Item " + itemName + " not found.");
        }

        // Check if player has the item
        if (!player.getBackpack().hasItems(Collections.singletonList(itemName))) {
            return Result.error("You don't have " + itemName + " in your backpack.");
        }

        // Check if player is near the NPC
        Location playerLocation = player.getLocation();
        Location npcLocation = npcEnum.getLocation();

        int distance = Math.abs(playerLocation.xAxis - npcLocation.xAxis) + Math.abs(playerLocation.yAxis - npcLocation.yAxis);

        if (distance > 1) {
            return Result.error("You need to be adjacent to " + npcName + " to give them a gift.");
        }

        if (item instanceof Tool) {
            return Result.error("You can't gift tools to NPCs");
        }

        NPC npc = createNPCFromEnum(npcEnum);

        boolean success = player.giftNPC(npc, item);

        if (!success) {
            return Result.error("You can't gift that item to " + npcName + ".");
        }

        Date currentDate = App.getGame().getDate();

        boolean isFavorite = npc.isFavoriteItem(item);

        Map<String, String> friendships = player.getNPCFriendships();
        String friendshipInfo = friendships.getOrDefault(npcName, "Level: 0, Points: 0");

        StringBuilder resultMessage = new StringBuilder();
        resultMessage.append("You gave ").append(itemName).append(" to ").append(npcName).append(".\n\n");

        if (isFavorite) {
            resultMessage.append(npcName).append(" loved your gift! Friendship increased by 200 points.");
        } else {
            resultMessage.append(npcName).append(" appreciated your gift. Friendship increased by 50 points.");
        }

        resultMessage.append("\n\nCurrent friendship with ").append(npcName).append(": ").append(friendshipInfo);

        return Result.success(resultMessage.toString());
    }

    private boolean isToolItem(Item item) {
        String name = item.getName().toLowerCase();
        return name.contains("axe") ||
                name.contains("pickaxe") ||
                name.contains("hoe") ||
                name.contains("watering can") ||
                name.contains("fishing rod") ||
                name.contains("scythe");
    }

    private Result friendshipNPCList() {
        // Get the actual NPC friendships from the player
        Map<String, String> friendships = player.getNPCFriendships();

        StringBuilder result = new StringBuilder("NPC Friendships:\n");

        // Sort NPCs by name for consistent display
        List<Npcs> sortedNpcs = new ArrayList<>(Arrays.asList(Npcs.values()));
        Collections.sort(sortedNpcs, Comparator.comparing(Npcs::getName));

        for (Npcs npc : sortedNpcs) {
            String npcName = npc.getName();
            String friendshipInfo = friendships.getOrDefault(npcName, "Level: 0, Points: 0");

            result.append("- ").append(npcName).append(": ").append(friendshipInfo).append("\n");

            // Add the NPC's characteristic and job for more detail
            result.append("  Trait: ").append(npc.getCharacteristic()).append(", Job: ").append(npc.getJob()).append("\n");

            // Add favorite items
            result.append("  Favorite Items: ").append(String.join(", ", npc.getFavoriteItems())).append("\n\n");
        }

        return Result.success(result.toString());
    }

    private Result questsList() {
        QuestManager questManager = QuestManager.getInstance();
        List<Quest> activeQuests = questManager.getActiveQuestsForPlayer(player);

        StringBuilder result = new StringBuilder("Available Quests:\n");
        int questIndex = 1;

        if (activeQuests.isEmpty()) {
            result.append("You don't have any active quests at the moment.\n");
            result.append("Talk to NPCs to discover new quests!\n");
        } else {
            for (Quest quest : activeQuests) {
                result.append(questIndex).append(". [").append(quest.getNpc().getName()).append("] ");
                result.append(quest.getTitle()).append(": ").append(quest.getDescription()).append("\n");

                result.append("   Requirements: ");
                for (Map.Entry<Item, Integer> requirement : quest.getRequirements().entrySet()) {
                    result.append(requirement.getValue()).append(" ").append(requirement.getKey().getName()).append(", ");
                }
                result.delete(result.length() - 2, result.length()); // Remove last comma and space
                result.append("\n");

                // Add rewards
                result.append("   Rewards: ");
                if (quest.getGoldReward() > 0) {
                    result.append(quest.getGoldReward()).append(" gold");
                    if (quest.getItemReward() != null) {
                        result.append(", ");
                    }
                }

                if (quest.getItemReward() != null) {
                    result.append(quest.getItemRewardQuantity()).append(" ").append(quest.getItemReward().getName());
                }
                result.append("\n\n");

                questIndex++;
            }
        }

        questManager.updateQuestsForPlayer(player, App.getGame().getDate());

        return Result.success(result.toString());
    }

    private Result questsFinish(String[] args) {
        if (args == null || args.length < 1) {
            return Result.error("Quest index not specified");
        }

        try {
            int questIndex = Integer.parseInt(args[0]);
            QuestManager questManager = QuestManager.getInstance();
            List<Quest> activeQuests = questManager.getActiveQuestsForPlayer(player);

            if (questIndex < 1 || questIndex > activeQuests.size()) {
                return Result.error("Invalid quest index. Please choose a number between 1 and " + activeQuests.size() + ".");
            }

            Quest quest = activeQuests.get(questIndex - 1);
            Npcs npc = quest.getNpc();

            Location playerLocation = player.getLocation();
            Location npcLocation = npc.getLocation();

            int distance = Math.abs(playerLocation.xAxis - npcLocation.xAxis) + Math.abs(playerLocation.yAxis - npcLocation.yAxis);
            if (quest.isCompleted()) {
                return Result.error("This quest is already finished");
            }

            if (distance > 1) {
                return Result.error("You need to be adjacent to " + npc.getName() + " to complete their quest.");
            }

            // Check if player has the required items
            if (!quest.hasRequiredItems(player)) {
                StringBuilder missingItems = new StringBuilder("You don't have the required items. You need:\n");
                for (Map.Entry<Item, Integer> requirement : quest.getRequirements().entrySet()) {
                    Item requiredItem = requirement.getKey();
                    int requiredQuantity = requirement.getValue();

                    int playerQuantity = 0;
                    for (Map.Entry<Item, Integer> playerItem : player.getBackpack().getInventory().entrySet()) {
                        if (playerItem.getKey().getName().equalsIgnoreCase(requiredItem.getName())) {
                            playerQuantity = playerItem.getValue();
                            break;
                        }
                    }

                    if (playerQuantity < requiredQuantity) {
                        missingItems.append("- ").append(requiredQuantity).append(" ").append(requiredItem.getName())
                                .append(" (you have ").append(playerQuantity).append(")\n");
                    }
                }
                return Result.error(missingItems.toString());
            }

            // Complete the quest
            boolean completed = questManager.completeQuest(player, quest.getId());

            if (!completed) {
                return Result.error("Failed to complete the quest. Please try again.");
            }

            // Build success message
            StringBuilder successMessage = new StringBuilder("Quest completed! ");
            successMessage.append(npc.getName()).append(" gave you ");

            // Add rewards to message
            if (quest.getGoldReward() > 0) {
                int goldReward = quest.getGoldReward();
                // Check if rewards are doubled due to friendship level
                Map<String, String> friendships = player.getNPCFriendships();
                String friendshipInfo = friendships.get(npc.getName());
                int friendshipLevel = 0;
                if (friendshipInfo != null && friendshipInfo.startsWith("Level: ")) {
                    try {
                        friendshipLevel = Integer.parseInt(friendshipInfo.substring(7, 8));
                    } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                        // Default to level 0 if parsing fails
                    }
                }

                if (friendshipLevel >= 2) {
                    goldReward *= 2;
                    successMessage.append(goldReward).append(" gold (doubled due to friendship level) ");
                } else {
                    successMessage.append(goldReward).append(" gold ");
                }

                if (quest.getItemReward() != null) {
                    successMessage.append("and ");
                }
            }

            if (quest.getItemReward() != null) {
                int itemQuantity = quest.getItemRewardQuantity();
                // Check if rewards are doubled due to friendship level
                Map<String, String> friendships = player.getNPCFriendships();
                String friendshipInfo = friendships.get(npc.getName());
                int friendshipLevel = 0;
                if (friendshipInfo != null && friendshipInfo.startsWith("Level: ")) {
                    try {
                        friendshipLevel = Integer.parseInt(friendshipInfo.substring(7, 8));
                    } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                        // Default to level 0 if parsing fails
                    }
                }

                if (friendshipLevel >= 2) {
                    itemQuantity *= 2;
                    successMessage.append(itemQuantity).append(" ").append(quest.getItemReward().getName())
                            .append(" (doubled due to friendship level)");
                } else {
                    successMessage.append(itemQuantity).append(" ").append(quest.getItemReward().getName());
                }
            }

            successMessage.append(" as a reward.");
            return Result.success(successMessage.toString());

        } catch (NumberFormatException e) {
            return Result.error("Invalid quest index format");
        }
    }

    // Trade-related methods

    private Result startTrade() {
        Game game = App.getGame();
        if (game == null) {
            return Result.error("No active game");
        }

        List<Player> players = game.getPlayers();
        if (players.size() <= 1) {
            return Result.error("There are no other players to trade with");
        }

        StringBuilder sb = new StringBuilder("Players in the game:\n");
        for (Player p : players) {
            if (!p.equals(player)) {
                sb.append("- ").append(p.getUser().getUsername()).append("\n");
            }
        }

        // Show unviewed trade requests
        List<TradeRequest> unviewedRequests = TradeManager.getInstance().getUnviewedTradeRequestsForPlayer(player);
        if (!unviewedRequests.isEmpty()) {
            sb.append("\nYou have ").append(unviewedRequests.size()).append(" new trade requests:\n");
            for (TradeRequest request : unviewedRequests) {
                sb.append("- ").append(request.toString()).append("\n");
                request.markAsViewed();
            }
        }

        return Result.success(sb.toString());
    }


    private Result tradeRequest(String[] args) {
        if (args == null || args.length < 4) {
            return Result.error("Invalid trade request format");
        }

        String username = args[0];
        String type = args[1];
        String itemName = args[2];
        int amount = Integer.parseInt(args[3]);

        // Find the target player
        Player targetPlayer = null;
        for (Player p : App.getGame().getPlayers()) {
            if (p.getUser().getUsername().equals(username)) {
                targetPlayer = p;
                break;
            }
        }

        if (targetPlayer == null) {
            return Result.error("Player with username " + username + " not found");
        }

        if (targetPlayer.equals(player)) {
            return Result.error("You cannot trade with yourself");
        }

        // Get the item
        Item item = App.getItem(itemName);
        if (item == null) {
            return Result.error("Item " + itemName + " not found");
        }

        // Check if the amount is valid
        if (amount <= 0) {
            return Result.error("Amount must be greater than 0");
        }

        boolean isRequest = type.equals("request");

        // Check if the player has the item if it's an offer
        if (!isRequest && player.getBackpack().getInventory().getOrDefault(item, 0) < amount) {
            return Result.error("You don't have enough " + itemName);
        }

        // Check if price or target item is specified
        int price = -1;
        Item targetItem = null;
        int targetAmount = 0;

        if (args.length >= 5 && args[4] != null) {
            price = Integer.parseInt(args[4]);
            if (price < 0) {
                return Result.error("Price must be non-negative");
            }
        }

        if (args.length >= 7 && args[5] != null && args[6] != null) {
            String targetItemName = args[5];
            targetAmount = Integer.parseInt(args[6]);

            targetItem = App.getItem(targetItemName);
            if (targetItem == null) {
                return Result.error("Target item " + targetItemName + " not found");
            }

            if (targetAmount <= 0) {
                return Result.error("Target amount must be greater than 0");
            }

            if (price >= 0) {
                return Result.error("You cannot specify both price and target item");
            }
        }

        // Create the trade request
        TradeRequest request;
        if (targetItem != null) {
            request = TradeManager.getInstance().createTradeRequest(
                    player, targetPlayer, item, amount, targetItem, targetAmount, isRequest);
        } else {
            request = TradeManager.getInstance().createTradeRequest(
                    player, targetPlayer, item, amount, Math.max(price, 0), isRequest);
        }

        if (request == null) {
            return Result.error("Failed to create trade request");
        }

        return Result.success("Trade request sent to " + username);
    }

    private Result tradeList() {
        List<TradeRequest> pendingRequests = TradeManager.getInstance().getPendingTradeRequestsForPlayer(player);

        if (pendingRequests.isEmpty()) {
            return Result.success("You have no pending trade requests");
        }

        StringBuilder sb = new StringBuilder("Pending trade requests:\n");
        for (TradeRequest request : pendingRequests) {
            sb.append("- ").append(request.toString()).append("\n");
        }

        return Result.success(sb.toString());
    }

    private Result tradeResponse(String[] args) {
        if (args == null || args.length < 2) {
            return Result.error("Invalid trade response format");
        }

        String response = args[0];
        int id = Integer.parseInt(args[1]);

        TradeRequest request = TradeManager.getInstance().getTradeRequest(id);
        if (request == null) {
            return Result.error("Trade request with ID " + id + " not found");
        }

        if (!request.getReceiver().equals(player)) {
            return Result.error("This trade request is not for you");
        }

        if (request.isAccepted() || request.isRejected()) {
            return Result.error("This trade request has already been " +
                    (request.isAccepted() ? "accepted" : "rejected"));
        }

        if (response.equals("--accept")) {
            boolean success = TradeManager.getInstance().acceptTradeRequest(id, player);
            if (success) {
                return Result.success("Trade request accepted");
            } else {
                return Result.error("Failed to accept trade request. Make sure you have the required items or money.");
            }
        } else if (response.equals("--reject")) {
            boolean success = TradeManager.getInstance().rejectTradeRequest(id, player);
            if (success) {
                return Result.success("Trade request rejected");
            } else {
                return Result.error("Failed to reject trade request");
            }
        } else {
            return Result.error("Invalid response. Use --accept or --reject");
        }
    }

    private Result tradeHistory() {
        List<TradeRequest> history = TradeManager.getInstance().getTradeHistoryForPlayer(player);

        if (history.isEmpty()) {
            return Result.success("You have no trade history");
        }

        StringBuilder sb = new StringBuilder("Trade history:\n");
        for (TradeRequest request : history) {
            sb.append("- ").append(request.toString()).append("\n");
        }

        return Result.success(sb.toString());
    }


    //cheats:
    private void cheatBackPackFull() {
        player.getBackpack().setType(Backpack.Type.Deluxe);
    }


    private void cheatAddFavourites(String[] args) {
        Npcs npcType = Npcs.fromName(args[0]);

        for (String s : npcType.getFavoriteItems()) {
            Item item = ItemBuilder.build(s);
            if (item != null) {
                player.getBackpack().add(item, 1);
            }
        }
    }

}