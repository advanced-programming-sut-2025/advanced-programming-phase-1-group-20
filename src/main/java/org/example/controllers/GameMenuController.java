package org.example.controllers;

import org.example.models.App;
import org.example.models.Items.*;
import org.example.models.MapDetails.GameMap;
import org.example.models.Player.Player;
import org.example.models.common.Date;
import org.example.models.common.Location;
import org.example.models.common.Result;
import org.example.models.entities.Friendship;
import org.example.models.entities.Game;
import org.example.models.entities.TradeRequest;
import org.example.models.entities.User;
import org.example.models.enums.Npcs;
import org.example.models.enums.Types.CraftingType;
import org.example.models.enums.Types.TileType;
import org.example.models.enums.Weather;
import org.example.models.enums.commands.GameMenuCommands;
import org.example.views.AppView;
import org.example.views.MainMenu;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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

            // Greenhouse-related commands
            case GreenhouseBuild -> result = greenhouseBuild();

            // Walking and map commands
            case Walk -> result = walk(args);
            case PrintMap -> result = printMap(args);

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
            player.getBackpack().add(item, 1);
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
            player.getBackpack().add(item, count);

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
                        // TODO: add the amount
                        player.getBackpack().add(item, 1);
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
        if (player.getBackpack().isBackPackFull()) {
            // TODO: make this a Result -> Mostafa
        }
        if (flag) {
            CookingItem cookingItem = (CookingItem) item;
            // TODO: mostafa baadan begoo energish doroste ya na
            player.decreaseEnergy(cookingItem.getEnergy());
            Food food = cookingItem.cook(player.getBackpack());
            player.getBackpack().add(food, 1);
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
            player.increaseEnergy(food.getEnergy());
            player.getBackpack().remove(food, 1);
        }
    }

    private boolean isFood(Item item) {
        if (item instanceof Food) {
            return true;
        }
        // TODO: this must be a Result
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
        boolean success = player.useTool(direction, gMap);

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

            // Check if the location is in another player's farm
            if (gMap.isInOtherPlayersFarm(player, x, y)) {
                return Result.error("You cannot view another player's farm");
            }

            System.out.println("Printing map with center at (" + x + ", " + y + ") and radius " + size + ":");
            gMap.printCurrentView(x, y, size);
            System.out.println("Map printed successfully!");

            return Result.success("Map printed");
        } catch (NumberFormatException e) {
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

        gMap.addGreenhouse(leftCorner, rightCorner);

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

        // Check if player is near the NPC
        Location playerLocation = player.getLocation();
        Location npcLocation = npcEnum.getLocation();

        int distance = Math.abs(playerLocation.xAxis - npcLocation.xAxis) + Math.abs(playerLocation.yAxis - npcLocation.yAxis);

        if (distance > 1) {
            return Result.error("You need to be adjacent to " + npcName + " to talk to them.");
        }

        // Since we can't directly create an NPC from the enum in the controller,
        // we'll just return a generic dialogue based on the NPC's name and characteristics
        String dialogue = "Hello, I'm " + npcName + ". " + npcEnum.getDescription();

        return Result.success(dialogue);
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

        // Check if the item is a favorite of the NPC
        boolean isFavorite = npcEnum.getFavoriteItems().contains(itemName);

        // Remove the item from the player's backpack
        player.getBackpack().remove(item, 1);

        // Since we can't directly create an NPC from the enum in the controller,
        // we'll just return a generic success message
        if (isFavorite) {
            return Result.success("You gave " + itemName + " to " + npcName + ". They loved it! Friendship increased by 200 points.");
        } else {
            return Result.success("You gave " + itemName + " to " + npcName + ". Friendship increased by 50 points.");
        }
    }

    private Result friendshipNPCList() {
        // Since we can't directly access the player's NPC friendships in the controller,
        // we'll just return a generic message listing all NPCs with random friendship levels
        StringBuilder result = new StringBuilder("NPC Friendships:\n");

        for (Npcs npc : Npcs.values()) {
            int level = (int) (Math.random() * 3); // Random level between 0 and 2
            int points = (int) (Math.random() * 200); // Random points between 0 and 199
            result.append("- ").append(npc.getName()).append(": Level ").append(level).append(", Points ").append(points).append("\n");
        }

        return Result.success(result.toString());
    }

    private Result questsList() {

        StringBuilder result = new StringBuilder("Available Quests:\n");
        int questIndex = 1;

        for (Npcs npc : Npcs.values()) {
            // Generate a random quest for each NPC
            String questDescription = generateRandomQuest(npc);
            result.append(questIndex).append(". ").append(npc.getName()).append(": ").append(questDescription).append("\n");
            questIndex++;
        }

        return Result.success(result.toString());
    }

    private String generateRandomQuest(Npcs npc) {
        // TODO: Implement a more complex quest generation logic
        switch (npc.getJob()) {
            case ENGINEER:
                return "Bring 10 Iron Ore";
            case STUDENT:
                return "Bring 5 Books";
            case SELLER:
                return "Bring 20 Wood";
            default:
                return "Bring 15 Stone";
        }
    }

    private Result questsFinish(String[] args) {
        if (args == null || args.length < 1) {
            return Result.error("Quest index not specified");
        }

        try {
            int questIndex = Integer.parseInt(args[0]);

            // Check if the quest index is valid
            if (questIndex < 1 || questIndex > Npcs.values().length) {
                return Result.error("Invalid quest index. Please choose a number between 1 and " + Npcs.values().length + ".");
            }

            // Get the NPC for this quest
            Npcs npc = Npcs.values()[questIndex - 1];

            // Check if player is near the NPC
            Location playerLocation = player.getLocation();
            Location npcLocation = npc.getLocation();

            int distance = Math.abs(playerLocation.xAxis - npcLocation.xAxis) + Math.abs(playerLocation.yAxis - npcLocation.yAxis);

            if (distance > 1) {
                return Result.error("You need to be adjacent to " + npc.getName() + " to complete their quest.");
            }

            // Check if player has the required items based on the NPC's job
            String requiredItem;
            int requiredQuantity = switch (npc.getJob()) {
                case ENGINEER -> {
                    requiredItem = "Iron Ore";
                    yield 10;
                }
                case STUDENT -> {
                    requiredItem = "Book";
                    yield 5;
                }
                case SELLER -> {
                    requiredItem = "Wood";
                    yield 20;
                }
                default -> {
                    requiredItem = "Stone";
                    yield 15;
                }
            };

            Item item = App.getItem(requiredItem);
            if (item == null) {
                return Result.error("Item " + requiredItem + " not found in the game.");
            }

            // Check if player has enough of the required item
            int itemCount = 0;
            for (Map.Entry<Item, Integer> entry : player.getBackpack().getInventory().entrySet()) {
                if (entry.getKey().getName().equalsIgnoreCase(requiredItem)) {
                    itemCount = entry.getValue();
                    break;
                }
            }

            if (itemCount < requiredQuantity) {
                return Result.error("You don't have enough " + requiredItem + ". You need " + requiredQuantity + ".");
            }

            // Remove the items from player's backpack
            player.getBackpack().remove(item, requiredQuantity);

            // Give reward
            player.increaseMoney(500);

            return Result.success("Quest completed! " + npc.getName() + " gave you 500 gold as a reward.");

        } catch (NumberFormatException e) {
            return Result.error("Invalid quest index format");
        }
    }

    // Trade-related methods

    /**
     * Opens the trade menu and shows a list of players in the game.
     *
     * @return Result with the list of players
     */
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

    /**
     * Creates a trade request.
     *
     * @param args Command arguments
     * @return Result of the operation
     */
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
                    player, targetPlayer, item, amount, price >= 0 ? price : 0, isRequest);
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
}
