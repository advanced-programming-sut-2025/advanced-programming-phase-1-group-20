package org.example.client.controllers;

import org.example.common.models.Items.*;
import org.example.common.models.MapDetails.Building;
import org.example.common.models.Player.Player;
import org.example.common.models.common.Result;
import org.example.common.models.enums.Types.CraftingType;
import org.example.common.models.enums.Types.ItemBuilder;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HouseMenuController implements Controller {
    private Player player;
    private Building house;

    public HouseMenuController(Player player, Building house) {
        this.player = player;
        this.house = house;
    }

    @Override
    public void setupListeners() {}

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


    public Result craftItem(String[] args) {
        String itemName = args[0];

        CraftingType type = CraftingType.fromName(itemName);
        assert type != null;
        CraftingItem craftedItem = new CraftingItem(type);
        if (!craftedItem.canCraft(player.getBackpack())) {
            return Result.error("You don't have enough items for  this item");
        }

        if (!player.getBackpack().add(craftedItem, 1)) {
            return Result.error("Your backpack is full");
        }

        player.addCraftingItem(craftedItem);
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
        Item item = player.getBackpack().getItem(itemName);
        if (item == null) {
            return Result.error(itemName + " does not exist");
        }
        switch (key) {
            case "put":
                if (!player.getBackpack().hasItems(Collections.singletonList(itemName))) {
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

    public Result cookingPrepare(String[] args) {
        String name = args[0];
        Item item = ItemBuilder.build(name);


        if (player.getBackpack().isBackPackFull()) {
            return Result.error("Backpack is full");
        }
        if (item == null) {
            return Result.error(name + " does not exist in the game.");
        }
        if (!isCooking(item)) {
            return Result.error("Item is not a recipe");
        }


        CookingItem cookingItem = (CookingItem) item;
        Food food = cookingItem.cook(player.getBackpack());
        if (!cookingItem.canCook(player.getBackpack())) {
            return Result.error("You don't have enough cooking item or correct recipe");
        }
        if (!player.getBackpack().add(food, 1)) {
            return Result.error("You don't have enough space in your backpack");
        }

        player.decreaseEnergy(3);
        player.addCookingItem(cookingItem);
        return Result.success("Food " + food.getName() + " cooked");
    }

    private boolean isCooking(Item item) {
        return item instanceof CookingItem;
    }

    //this method is completed now
    private Result eatFood(String[] args) {
        String foodName = args[0];
        Item item = player.getBackpack().getItem(foodName);
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
        String craftingItemName = args[0];
        String artisanItemName = args[1];
        Item item = player.getBackpack().getItem(craftingItemName);
        if (item == null) {
            return Result.error(craftingItemName + " does not exist");
        }
        if (!player.getBackpack().hasItems(Collections.singletonList(craftingItemName))) {
            return Result.error(craftingItemName + " does not exist in backpack");
        }
        if (!(item instanceof CraftingItem)) {
            return Result.error(craftingItemName + " is not a CraftingItem");
        }

        CraftingItem craftingItem = (CraftingItem) item;
        ArtisanItem artisanItem = (ArtisanItem) ItemBuilder.build(artisanItemName);
        if(!craftingItem.processItem(player.getBackpack() , artisanItem)){
            return Result.error("There is item processing on " + craftingItem.getName() + "!");
        }

        ArtisanItem processingItem = (ArtisanItem) craftingItem.getProccessingItem();
        if(processingItem == null){
            return Result.error("You didn't have enough recipe for " + artisanItem.getName() + "!");
        }

        return Result.success(artisanItem.getName() + " is now in process estimated turns : " + artisanItem.getProcessingTime());
    }




    public Result artisanGet(String[] args) {
        String artisanName = args[0];
        CraftingItem craftingItem = (CraftingItem) player.getBackpack().getItem(artisanName);
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
