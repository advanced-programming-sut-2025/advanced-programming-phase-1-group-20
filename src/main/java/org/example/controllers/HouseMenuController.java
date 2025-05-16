package org.example.controllers;

import org.example.models.App;
import org.example.models.Items.*;
import org.example.models.MapDetails.Building;
import org.example.models.Player.Player;
import org.example.models.common.Result;
import org.example.models.enums.Types.CraftingType;
import org.example.models.enums.Types.ItemBuilder;
import org.example.models.enums.commands.HouseMenuCommands;
import org.example.views.AppView;
import org.example.views.GameMenu;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HouseMenuController implements Controller {
    private AppView appView;
    private Player player;
    private Building house;

    public HouseMenuController(AppView appView, Player player, Building house) {
        this.appView = appView;
        this.player = player;
        this.house = house;
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
            case AddItem -> result = addItem(args);


            //cooking related commands
            case AddRefrigerator -> result = addRefrigerator(args);
            case CookingShowRecipes -> cookingShowRecipes();
            case CookingPrepare -> result = cookingPrepare(args);

            //artisan-related commands
            case ArtisanUse -> result = artisanUse(args);
            case ArtisanGet -> result = artisanGet(args);

            case EatFood -> result = eatFood(args);

            case GetOut -> getOut();
            case None -> result = Result.error("Invalid input");
        }


        appView.handleResult(result, command);
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
        if (!craftingItems.isEmpty()) {
            for (CraftingItem craftingItem : craftingItems) {
                craftingItem.showInfo();
            }
        } else {
            System.out.println("There is no crafting items for the player");
        }
    }


    private Result craftItem(String[] args) {
        String itemName = args[0];

        CraftingType type = CraftingType.fromName(itemName);
        assert type != null;
        CraftingItem craftedItem = new CraftingItem(type);
        if(!player.getCraftingItems().contains(craftedItem)) {
            return Result.error("You cannot craft this item because you don't have the recipe!");
        }
        if (!craftedItem.canCraft(player.getBackpack())) {
            return Result.error("You don't have enough items for  this item");
        }

        if (!player.getBackpack().add(craftedItem, 1)) {
            return Result.error("Your backpack is full");
        }


        player.decreaseEnergy(2);
        return Result.success("Item " + itemName + " has been crafted");
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
                house.getRefrigerator().putItem(item, 1);
                break;
            case "pick":
                Item item1 = house.getRefrigerator().pickItem(item);
                if (item1 == null) {
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

        if (player.getBackpack().hasItems(Collections.singletonList(name))) {
            player.getBackpack().remove(item, 1);
        } else {
            house.getRefrigerator().removeItem(item, 1);
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
        if (!(item instanceof Food || item instanceof ArtisanItem || item instanceof Fruit)) {
            return Result.error("Item is not a Food or ArtisanItem");
        }
        if (item instanceof ArtisanItem artisanItem) {
            if (artisanItem.getEnergy() > 0) {
                player.increaseEnergy(artisanItem.getEnergy());
                player.getBackpack().remove(item, 1);
                return Result.success("Food " + foodName + " eaten");
            } else {
                return Result.success("Artisan item is not a food.");
            }
        }
        if (item instanceof Fruit fruit) {
            player.increaseEnergy(fruit.getEnergy());
            player.getBackpack().remove(item, 1);
            return Result.success("Food " + foodName + " eaten");
        }
        Food food = (Food) item;
        player.increaseEnergy(food.getEnergy());
        player.getBackpack().remove(item, 1);
        food.setBuffer(player);
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


    public void getOut() {
        appView.navigateMenu(new GameMenu(appView, player.getUser(), player));
    }

}
