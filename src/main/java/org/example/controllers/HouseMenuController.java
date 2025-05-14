package org.example.controllers;

import org.example.models.App;
import org.example.models.Items.*;
import org.example.models.MapDetails.Building;
import org.example.models.MapDetails.GameMap;
import org.example.models.Player.Player;
import org.example.models.common.Location;
import org.example.models.common.Result;
import org.example.models.enums.Types.CraftingType;
import org.example.models.enums.Types.ItemBuilder;
import org.example.models.enums.commands.HouseMenuCommands;
import org.example.views.AppView;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HouseMenuController implements Controller {
    private AppView appView;
    private App app;
    private MarketController controller;
    private Player player;
    private Building house;
    private GameMap gMap;

    public HouseMenuController(AppView appView, App app, MarketController controller , Player player, Building house , GameMap gMap) {
        this.appView = appView;
        this.app = app;
        this.controller = controller;
        this.player = player;
        this.house = house;
        this.gMap = gMap;
    }



    @Override
    public Result update(String input) {
        HouseMenuCommands command = HouseMenuCommands.getCommand(input);
        String[] args = command.parseInput(input);
        Result result = null;

        switch (command) {
            //crafting related commands
            case CraftingShowRecipes -> craftingShowRecipes();
            case CraftingCraft -> result = craftItem(args);
            case PlaceItem -> result = placeItem(args);
            case AddItem -> result = addItem(args);


            //cooking related commands
            case AddRefrigerator -> result = addRefrigerator(args);
            case CookingShowRecipes -> cookingShowRecipes();
            case CookingPrepare -> result = cookingPrepare(args);

            //artisan-related commands
            case ArtisanUse -> result = artisanUse(args);
            case ArtisanGet -> result = artisanGet(args);

            case EatFood -> result = eatFood(args);
            case None -> result = Result.error("Invalid input");
        }



        appView.handleResult(result , command);
        return result;
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
        return null;
    }


    //this method is completed
    private void craftingShowRecipes() {
        List<CraftingItem> craftingItems = player.getCraftingItems();
        for (CraftingItem craftingItem : craftingItems) {
            craftingItem.showInfo();
        }
    }


    private Result craftItem(String[] args) {
        String itemName = args[0];
        boolean exists = player.craftingExists(itemName);

        if (exists) {
            return Result.error("Crafting item: " + itemName + " does not exist");
        }

        CraftingType type = CraftingType.fromName(itemName);
        CraftingItem craftedItem = new CraftingItem(type);
        if (!craftedItem.canCraft(player.getBackpack())) {
            return Result.error("You don't have enough items for  this item");
        }

        if (player.getBackpack().isBackPackFull()) {
            return Result.error("Your backpack is full");
        }


        player.getBackpack().add(craftedItem, 1);
        player.decreaseEnergy(2);
        return Result.success("Item " + itemName + " has been crafted");
    }


    private Result placeItem(String[] args) {
        String itemName = args[0];
        String direction = args[1];
        int[] dir = getDirection(direction);

        if (dir == null) {
            return Result.error("Invalid direction");
        }

        Location loc = player.getLocation();
        int x = loc.getX() + dir[1];
        int y = loc.getY() + dir[0];
        Item item = player.getBackpack().getItem(itemName);
        if (item == null) {
            return Result.error("Item " + itemName + " does not exist in backpack");
        }
        if (gMap.getFarmByPlayer(player).getItem(x, y) != null) {
            return Result.error("there is Item already in the ground!");
        }
        if (!item.isPlacable()) {
            return Result.error("Item " + itemName + " is not a placeable item");
        }

        gMap.getFarmByPlayer(player).placeItem(x, y, item);


        if (item instanceof CraftingItem) {
            //it will be replaced as item.place() like a function pointer.
            switch (item.getName()) {
                case "Cherry Bomb" -> {
                    gMap.getFarmByPlayer(player).bomb(x, y, 3);
                }
                case "Bomb" -> {
                    gMap.getFarmByPlayer(player).bomb(x, y, 5);
                }
                case "Mega Bomb" -> {
                    gMap.getFarmByPlayer(player).bomb(x, y, 7);
                }
                case "Sprinkler" -> {
                    gMap.getFarmByPlayer(player).sprinkle(x, y, 4);
                }
                case "Quality Sprinkler" -> {
                    gMap.getFarmByPlayer(player).sprinkle(x, y, 8);
                }
                case "Iridium Sprinkler" -> {
                    gMap.getFarmByPlayer(player).sprinkle(x, y, 24);
                }
                case "Scarecrow" -> {
                    gMap.getFarmByPlayer(player).setScarecrow(x, y, 8, true);
                }
                case "Deluxe Scarecrow" -> {
                    gMap.getFarmByPlayer(player).setScarecrow(x, y, 12, true);
                }
                case "Bee House" -> {
                    //TODO : bee house.
                }
            }
        }

        return Result.success("Item " + itemName + " has been placed on " + "(" + x + "," + y + ")");
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


    //cooking related
    private Result addRefrigerator(String[] args) {
        String key = args[0];
        String itemName = args[1];
        Item item = App.getItem(itemName);
        if (item == null) {
            return Result.error(itemName + "does not exist");
        }
        switch (key) {
            case "put":
                if (!player.getBackpack().hasItems(Collections.singletonList(key))) {
                    return Result.error("Backpack doesn't contain item");
                }
                 house.getRefrigerator().putItem(item , 1);
                break;
            case "pick":
                Item item1 = house.getRefrigerator().pickItem(item);
                if(item1 == null){
                    return Result.error("Item not found");
                }
                player.getBackpack().add(item1, 1);
                break;
        }
        return Result.success("Item " + itemName + " has been " + key + "ed");
    }

    private void cookingShowRecipes() {
        for (CookingItem cookingItem : player.getCookingItems()) {
            cookingItem.showInfo();
        }
    }

    private Result cookingPrepare(String[] args) {
        String name = args[0];
        Item item = ItemBuilder.build(name);


        if (player.getBackpack().isBackPackFull()) {
            return Result.error("Backpack is full");
        }
        if (item == null) {
            return Result.error(name + " does not exist in the game.");
        }
        if (!isCooking(item)) {
            return Result.error("Item is not a cooking item");
        }
        if (!(player.getBackpack().hasItems(Collections.singletonList(name)) || house.getRefrigerator().contains(item))) {
            return Result.error("You don't have the item in this house(and your back pack).");
        }


        CookingItem cookingItem = (CookingItem) item;
        player.decreaseEnergy(3);
        Food food = cookingItem.cook(player.getBackpack());

        if(player.getBackpack().hasItems(Collections.singletonList(name))){
            player.getBackpack().remove(item , 1);
        }else{
            house.getRefrigerator().removeItem(item , 1);
        }

        player.getBackpack().add(food, 1);
        return Result.success("Food " + food.getName() + " cooked");
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
        if(item instanceof ArtisanItem){
            ArtisanItem artisanItem = (ArtisanItem) item;
            if(artisanItem.getEnergy() > 0){
                player.increaseEnergy(artisanItem.getEnergy());
                player.getBackpack().remove(item , 1);
                return Result.success("Food " + foodName + " eaten");
            }else{
                return Result.success("Artisan item is not a food.");
            }
        }
        Food food = (Food) item;
        player.increaseEnergy(food.getEnergy());
        player.getBackpack().remove(item, 1);
        //TODO : adding buffer.
        return Result.success("Food " + foodName + " eaten");
    }


    //artisan related
    public Result artisanUse(String[] args) {
        String artisanName = args[0];
        String items = args[1];
        Item item = ItemBuilder.build(artisanName);
        if (item == null) {
            return Result.error(artisanName + " does not exist");
        }
        if (!player.getBackpack().hasItems(Collections.singletonList(artisanName))) {
            return Result.error(artisanName + " does not exist in backpack");
        }
        if (!(item instanceof CraftingItem)) {
            return Result.error(artisanName + " is not a CraftingItem");
        }

        CraftingItem craftingItem = (CraftingItem) item;
        if (!checkItems(items, craftingItem)) {
            return Result.error("You don't have enough items for crafting");
        }
        craftingItem.proccessItem(items);
        ArtisanItem artisanItem = (ArtisanItem) craftingItem.getProccessingItem();
        return Result.success(artisanItem.getName() + " is now in process estimated turns : " + artisanItem.getProcessingTime());
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

    public Result artisanGet(String[] args) {
        String artisanName = args[0];
        CraftingItem craftingItem = (CraftingItem) ItemBuilder.build(artisanName);
        if (craftingItem == null) {
            return Result.error(artisanName + " does not exist");
        }
        Item item = craftingItem.getFinishedItem();
        if (item == null) {
            return Result.error(artisanName + " is not in queue!");
        }
        player.getBackpack().add(item, 1);
        return Result.success("Artisan item " + item.getName() + " arrived");
    }

}
