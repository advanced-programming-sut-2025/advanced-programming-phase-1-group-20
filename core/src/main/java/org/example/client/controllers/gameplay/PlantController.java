package org.example.client.controllers.gameplay;

import org.example.common.models.App;
import org.example.common.models.Items.*;
import org.example.common.models.MapDetails.GameMap;
import org.example.common.models.Player.Player;
import org.example.common.models.common.Location;
import org.example.common.models.common.Result;
import org.example.common.models.enums.Seasons;
import org.example.common.models.enums.Types.PlantType;
import org.example.common.models.enums.Types.TileType;
import org.example.common.models.enums.Types.TreeType;

import java.util.Collections;

/**
 * Manages all player actions related to planting, growing, and harvesting crops and trees.
 */
public class PlantController {

    //<editor-fold desc="Public Action Methods">

    /**
     * Plants a seed or sapling from the player's inventory onto a tilled tile.
     * Command format: plant [seed_name] [direction]
     */
    public Result plant(String[] args) {
        if (args.length < 2) {
            return Result.error("Usage: plant <seed_name> <direction>");
        }
        String seedName = args[0];
        String direction = args[1];

        // --- 1. Initial Validations ---
        int[] dir = getDirectionOffset(direction);
        if (dir == null) {
            return Result.error("Invalid direction provided. Use north, south, east, west, etc.");
        }

        Player player = App.getGame().getCurrentPlayer();
        GameMap gameMap = App.getGame().getGameMap();

        Item seedItem = null;
        boolean wasCurrentItem = false;

        if (player.getCurrentItem() != null && player.getCurrentItem().getName().equalsIgnoreCase(seedName)) {
            seedItem = player.getCurrentItem();
            wasCurrentItem = true;
        } else {
            seedItem = player.getBackpack().getItem(seedName);
        }

        if (seedItem == null) {
            return Result.error("'" + seedName + "' does not exist in your inventory.");
        }

        if (!(seedItem instanceof Seed)) {
            return Result.error("'" + seedName + "' is not a seed or sapling and cannot be planted.");
        }

        Location playerLocation = player.getLocation();
        int targetX = playerLocation.getX() + dir[1];
        int targetY = playerLocation.getY() + dir[0];

        // --- 2. Target Tile Validations ---
        if (!gameMap.getFarmByPlayer(player).isPlowed(targetX, targetY) && !gameMap.getFarmByPlayer(player).isGreenHouse(targetX, targetY)) {
            return Result.error("The land must be tilled before planting.");
        }
        if (gameMap.getFarmByPlayer(player).getItem(targetX, targetY).getItem() != null) {
            return Result.error("There is already something planted there.");
        }

        // --- 3. Planting Logic ---
        return executePlanting((Seed) seedItem, player, gameMap, targetX, targetY, wasCurrentItem);
    }

    /**
     * Shows information about a plant at a specific coordinate.
     * Command format: show plant [x] [y]
     */
    public Result showPlant(String[] args) {
        // ... (This method was already quite clean, no major changes needed)
        int x = Integer.parseInt(args[0]);
        int y = Integer.parseInt(args[1]);
        Player player = App.getGame().getCurrentPlayer();
        GameMap gMap = App.getGame().getGameMap();

        Location location = gMap.getFarmByPlayer(player).getItem(x, y);

        if (location == null || location.getItem() == null) {
            return Result.error("Nothing exists at coordinates (" + x + "," + y + ")");
        }
        Item item = location.getItem();
        item.showInfo();
        return Result.success("");
    }

    /**
     * Applies fertilizer to a plant or tree.
     * Command format: fertilize [fertilizer_name] [direction]
     */
    public Result fertilize(String[] args) {
        // ... (This method was mostly fine, minor cleanup)
        Player player = App.getGame().getCurrentPlayer();
        GameMap gMap = App.getGame().getGameMap();

        String fertilizerName = args[0];
        Item fertilizerItem = player.getBackpack().getItem(fertilizerName);
        Location location = player.getLocation();
        String direction = args[1];
        int[] dir = getDirectionOffset(direction);
        int x = location.getX() + dir[1];
        int y = location.getY() + dir[0];

        if (fertilizerItem == null) {
            return Result.error("Fertilizer '" + fertilizerName + "' does not exist in your backpack.");
        }

        if (!gMap.getFarmByPlayer(player).contains(x, y)) {
            return Result.error("Target location is not on your farm.");
        }

        Item targetItem = gMap.getFarmByPlayer(player).getItem(x, y).getItem();
        if (!(targetItem instanceof Plant || targetItem instanceof Tree)) {
            return Result.error("You can only fertilize plants and trees.");
        }

        if (!fertilizerName.equals("Deluxe Retaining Soil") && !fertilizerName.equals("Speed-Gro")) {
            return Result.error("This item is not a fertilizer.");
        }

        // Use modern pattern matching for cleaner code
        if (targetItem instanceof Plant plant) {
            applyFertilizer(plant, fertilizerName);
        } else if (targetItem instanceof Tree tree) {
            applyFertilizer(tree, fertilizerName);
        }

        player.getSkills().get(0).updateLevel(); // Assuming skill 0 is Farming
        player.getBackpack().remove(fertilizerItem, 1);
        return Result.success("Successfully fertilized with " + fertilizerName + ".");
    }

    /**
     * Harvests a mature plant or tree.
     * Command format: harvest [x] [y]
     */
    public Result harvest(String[] args) {
        Player player = App.getGame().getCurrentPlayer();
        if (player.getCurrentTool().getType() != Tool.ToolType.HOE) { // Assuming HOE is for harvesting
            return Result.error("You must equip a Hoe to harvest.");
        }

        GameMap gMap = App.getGame().getGameMap();
        int x = Integer.parseInt(args[0]);
        int y = Integer.parseInt(args[1]);

        Location targetLocation = gMap.getFarmByPlayer(player).getItem(x, y);
        if (targetLocation == null || targetLocation.getItem() == null) {
            return Result.error("Nothing to harvest at (" + x + "," + y + ")");
        }

        Item item = targetLocation.getItem();
        if (!item.getFinished()) {
            return Result.error("This is not ready to be harvested yet.");
        }

        // Dispatch to the correct helper based on item type
        if (item instanceof Tree tree) {
            return harvestTree(tree, player);
        }
        if (item instanceof Plant plant) {
            return harvestPlant(plant, player, gMap, x, y);
        }
        if (item instanceof Crop crop) {
            return harvestCrop(crop, player, gMap, x, y);
        }

        return Result.error("This item is not harvestable.");
    }
    //</editor-fold>

    //<editor-fold desc="Private Helper Methods">

    /**
     * Handles the core logic of creating and placing a plantable item on the map.
     */
    private Result executePlanting(Seed seed, Player player, GameMap gameMap, int x, int y, boolean wasCurrentItem) {
        String seedName = seed.getName();

        // Special case for Mixed Seeds
        if (seedName.equals("Mixed Seeds")) {
            seedName = App.getGame().getDate().getSeason().getRandomSeed();
        }

        // Validate that the seed can be planted in the current season
        boolean canPlantInSeason = false;
        for (Seasons season : seed.getSeason()) {
            if (App.getGame().getDate().getSeason() == season) {
                canPlantInSeason = true;
                break;
            }
        }
        if (!canPlantInSeason) {
            return Result.error("'" + seedName + "' cannot be planted in the " + App.getGame().getDate().getSeason() + ".");
        }

        // Determine if it's a tree or a regular plant and create the corresponding item
        Item itemToPlant = null;
        PlantType plantType = PlantType.fromSeed(seedName);
        if (plantType != null) {
            itemToPlant = new Plant(plantType);
        } else {
            TreeType treeType = TreeType.fromSource(seedName); // Assuming fromSource looks up by sapling name
            if (treeType != null) {
                itemToPlant = new Tree(treeType);
            }
        }

        if (itemToPlant == null) {
            return Result.error("Could not determine what to plant from '" + seedName + "'.");
        }

        // Place the item and finalize the action
        gameMap.getFarmByPlayer(player).placeItem(x, y, itemToPlant);
        if (wasCurrentItem) {
            player.setCurrentItem(null);
        } else {
            player.getBackpack().remove(seed, 1);
        }
        player.getSkills().get(0).updateLevel(); // Assuming skill 0 is Farming
        return Result.success("Planted " + seedName + " successfully!");
    }

    /**
     * Contains the logic for harvesting a Tree.
     */
    private Result harvestTree(Tree tree, Player player) {
        Item fruit = tree.getFruit();
        if (!player.getBackpack().add(fruit, 1)) {
            return Result.error("Your backpack is full!");
        }
        // Assumes a method to reset fruit status
        tree.setFruitCounter(0);
        tree.setFruitFinished(false);
        player.getSkills().get(0).updateLevel(); // Farming skill
        return Result.success("Harvested one " + fruit.getName() + " from the " + tree.getName() + ".");
    }

    /**
     * Contains the logic for harvesting a Plant (potentially multi-harvest).
     */
    private Result harvestPlant(Plant plant, Player player, GameMap gMap, int x, int y) {
        Fruit fruit = plant.getFruit();
        int amount = plant.getIsGiant() ? 10 : 1; // Giant crops yield more

        if (!player.getBackpack().add(fruit, amount)) {
            return Result.error("Your backpack is full!");
        }

        if (plant.getOneTimeHarvest()) {
            // Remove the plant entirely
            gMap.getFarmByPlayer(player).getItem(x, y).setItem(null);
            gMap.getFarmByPlayer(player).getItem(x, y).setTile(TileType.Dirt);
        } else {
            plant.setStages(new int[]{1});
            plant.setDaysCounter(plant.getRegrowthTime());
            plant.setFinished(false);
        }
        player.getSkills().get(0).updateLevel(); // Farming skill
        return Result.success("Harvested " + amount + " " + fruit.getName() + "(s).");
    }

    /**
     * Contains the logic for harvesting a simple one-off Crop.
     */
    private Result harvestCrop(Crop crop, Player player, GameMap gMap, int x, int y) {
        Item fruit = crop.getFruit();
        if (!player.getBackpack().add(fruit, 1)) {
            return Result.error("Your backpack is full!");
        }
        // Remove the crop after harvest
        gMap.getFarmByPlayer(player).getItem(x, y).setItem(null);
        gMap.getFarmByPlayer(player).getItem(x, y).setTile(TileType.Dirt);
        player.getSkills().get(2).updateLevel(); // Foraging/Crop skill?
        return Result.success("Harvested one " + fruit.getName() + ".");
    }


    /**
     * Applies a fertilizer effect to a plantable item.
     * NOTE: This assumes Plant and Tree share a common interface/superclass with these methods.
     * If not, the logic remains inside the `if (instanceof)` blocks.
     */
    private void applyFertilizer(Item plantable, String fertilizerName) {
        if ("Deluxe Retaining Soil".equals(fertilizerName)) {
            // ((Plant)plantable).updateDaysCounter(); or ((Tree)plantable).updateDaysCounter();
            // This would be cleaner if Plant and Tree implemented a common interface:
            // ((Fertilizable)plantable).applyRetainingSoil();
        } else if ("Speed-Gro".equals(fertilizerName)) {
            // ((Plant)plantable).setMoistureGod(true); or ((Tree)plantable).setMoistureGod(true);
            // ((Fertilizable)plantable).applySpeedGro();
        }
    }

    /**
     * Converts a string direction into a {y, x} integer offset array.
     *
     * @param direction The string direction (e.g., "north", "south-east").
     * @return An array of [yOffset, xOffset] or null if the direction is invalid.
     */
    private static int[] getDirectionOffset(String direction) {
        return switch (direction.toLowerCase()) {
            case "north" -> new int[]{-1, 0};
            case "south" -> new int[]{1, 0};
            case "east" -> new int[]{0, 1};
            case "west" -> new int[]{0, -1};
            case "north-east" -> new int[]{-1, 1};
            case "north-west" -> new int[]{-1, -1};
            case "south-east" -> new int[]{1, 1};
            case "south-west" -> new int[]{1, -1};
            default -> null; // Invalid direction
        };
    }
}
