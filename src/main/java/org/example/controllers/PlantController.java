package org.example.controllers;

import org.example.models.App;
import org.example.models.Items.*;
import org.example.models.MapDetails.GameMap;
import org.example.models.Player.Player;
import org.example.models.common.Location;
import org.example.models.common.Result;
import org.example.models.enums.Seasons;
import org.example.models.enums.Types.ItemBuilder;
import org.example.models.enums.Types.PlantType;
import org.example.models.enums.Types.TileType;

import java.util.Collections;

public class PlantController {
    public Result plant(String[] args) {
        String seedName = args[0];
        String direction = args[1];

        int[] dir = getDirection(direction);
        if (dir == null) {
            return Result.error("Invalid direction");
        }

        Player player = App.getGame().getCurrentPlayer();
        GameMap gMap = App.getGame().getGameMap();
        Item item = App.getGame().getCurrentPlayer().getBackpack().getItem(seedName);
        Location loc = player.getLocation();
        int x = loc.getX() + dir[1];
        int y = loc.getY() + dir[0];

        if (item == null) {
            return Result.error(seedName + " does not exist in backpack.");
        }
        if (!player.getBackpack().hasItems(Collections.singletonList(seedName))) {
            return Result.error("Backpack does not contain " + seedName);
        }
        if (gMap.getFarmByPlayer(player).contains(x, y)) {
            return Result.error("this is not your farm!!!");
        }
        if (!gMap.getFarmByPlayer(player).isPlowed(x, y)) {
            return Result.error("the land is not plowed!");
        }
        if (gMap.getFarmByPlayer(player).getItem(x, y) != null) {
            return Result.error("there is an item on the ground");
        }
        if (!(item instanceof Plant || item instanceof Tree || item instanceof Seed)) {
            return Result.error("item is not plant");
        }


        if (item instanceof Seed) {
            if (seedName.equals("Mixed Seeds")) {
                seedName = App.getGame().getDate().getSeason().getRandomSeed();
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
                if (App.getGame().getDate().getSeason() == season) {
                    counter++;
                }
            }

            if (counter == 0) {
                return Result.error("This seed is not for this season.");
            }


            gMap.getFarmByPlayer(player).placeItem(x, y, item);
        } else if (item instanceof Tree tree) {
            Seasons[] seasons = tree.getSeasons();
            int counter = 0;
            for (Seasons season : seasons) {
                if (App.getGame().getDate().getSeason() == season) {
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
        return null;
    }

    public Result showPlant(String[] args) {
        int x = Integer.parseInt(args[0]);
        int y = Integer.parseInt(args[1]);
        Player player = App.getGame().getCurrentPlayer();
        GameMap gMap = App.getGame().getGameMap();

        Location location = gMap.getFarmByPlayer(player).getItem(x, y);

        if (location == null || location.getItem() == null) {
            return Result.error("Item does not exist in " + "(" + x + "," + y + ")");
        }
        Item item = location.getItem();
        item.showInfo();
        player.getSkills().get(0).updateLevel();
        return Result.success("");
    }

    public Result fertilize(String[] args) {
        Player player = App.getGame().getCurrentPlayer();
        GameMap gMap = App.getGame().getGameMap();

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

        if (!gMap.getFarmByPlayer(player).contains(x, y)) {
            return Result.error("You are not in the farm");
        }

        Location targetLocation = gMap.getFarmByPlayer(player).getItem(x, y);
        if (!(targetLocation.getItem() instanceof Plant || targetLocation.getItem() instanceof Tree)) {
            return Result.error("Targeted location is not a plant");
        }
        if (!item.getName().equals("Deluxe Retaining Soil") && !item.getName().equals("Speed-Gro")) {
            return Result.error("This item is not a fertilizer.");
        }
        Item targetItem = targetLocation.getItem();
        if (targetItem instanceof Plant plantItem) {
            if (item.getName().equals("Deluxe Retaining Soil")) {
                plantItem.updateDaysCounter();
            } else if (item.getName().equals("Speed-Gro")) {
                plantItem.setMoistureGod(true);
            }
        } else if (targetItem instanceof Tree treeItem) {
            if (item.getName().equals("Deluxe Retaining Soil")) {
                treeItem.updateDaysCounter();
            } else if (item.getName().equals("Speed-Gro")) {
                treeItem.setMoistureGod(true);
            }
        }
        player.getSkills().get(0).updateLevel();
        return Result.success("fertilized successfully with" + fertilizer);
    }

    public Result harvest(String[] args) {
        Player player = App.getGame().getCurrentPlayer();
        GameMap gMap = App.getGame().getGameMap();
        int x = Integer.parseInt(args[0]);
        int y = Integer.parseInt(args[1]);

        Location targetLocation = gMap.getFarmByPlayer(player).getItem(x, y);
        if (targetLocation == null || targetLocation.getItem() == null) {
            return Result.error("Plant does not exist in " + "(" + x + "," + y + ")");
        }

        if (player.getCurrentTool().getType() != Tool.ToolType.HOE) {
            return Result.error("You must equip HOE first");
        }

        Item item = targetLocation.getItem();
        if (!((item instanceof Plant) || (item instanceof Tree) || (item instanceof Crop))) {
            return Result.error("Item is not harvestable");
        }
        if (!item.getFinished()) {
            return Result.error("Plant is not ready yet");
        }

        if (item instanceof Tree tree) {
            Item fruit = tree.getFruit();
            if (fruit == null) {
                return Result.error("fruit is not ready yet");
            }
            if (!player.getBackpack().add(fruit, 1)) {
                return Result.error("Backpack is full!");
            }
            tree.setFruitCounter(0);
            tree.setFruitFinished(false);
            player.getSkills().get(0).updateLevel();
        }
        if (item instanceof Plant plant) {
            Fruit fruit = plant.getFruit();
            int amount = 1;
            if (plant.getIsGiant()) {
                amount = 10;
                fruit.setEnergy(fruit.getEnergy() * 4);
            }
            if (fruit == null) {
                return Result.error("fruit is not ready yet");
            }
            if (!player.getBackpack().add(fruit, amount)) {
                return Result.error("Backpack is full!");
            }
            if (plant.getOneTimeHarvest()) {
                gMap.getFarmByPlayer(player).getItem(x, y).setItem(null);
                gMap.getFarmByPlayer(player).getItem(x, y).setTile(TileType.GRASS);
                gMap.getFarmByPlayer(player).getItem(x, y).setType("grass");
            } else {
                plant.setStages(new int[]{1});
                plant.setDaysCounter(plant.getRegrowthTime());
                plant.setFinished(false);
            }
            player.getSkills().get(0).updateLevel();
        }
        if (item instanceof Crop crop) {
            Item fruit = crop.getFruit();
            if (fruit == null) {
                return Result.error("fruit is not ready yet");
            }
            if (!player.getBackpack().add(fruit, 1)) {
                return Result.error("Backpack is full!");
            }
            gMap.getFarmByPlayer(player).getItem(x, y).setItem(null);
            gMap.getFarmByPlayer(player).getItem(x, y).setTile(TileType.GRASS);
            gMap.getFarmByPlayer(player).getItem(x, y).setType("grass");
            player.getSkills().get(2).updateLevel();
        }
        return Result.success("Plant has been harvested!");
    }


}
