package org.example.models.Items.Tools;

import org.example.models.Items.Tool;
import org.example.models.MapDetails.GameMap;
import org.example.models.Player.Player;
import org.example.models.common.Location;
import org.example.models.enums.PlayerEnums.Skills;
import org.example.models.enums.Types.TileType;


public class Axe extends Tool {

    public Axe() {
        super("Basic Axe", 0, "A basic axe for cutting down trees and breaking branches.",
                ToolType.AXE, ToolMaterial.BASIC, 5, Skills.FORAGING);
    }


    public Axe(ToolMaterial material) {
        super(material.name() + " Axe", getBaseSellPrice(material),
                "A " + material.name().toLowerCase() + " axe for cutting down trees and breaking branches.",
                ToolType.AXE, material, getEnergyConsumption(material), Skills.FORAGING);
    }


    private static int getBaseSellPrice(ToolMaterial material) {
        return switch (material) {
            case BASIC -> 0;
            case COPPER -> 2000;
            case IRON -> 5000;
            case GOLD -> 10000;
            case IRIDIUM -> 25000;
        };
    }

    private static int getEnergyConsumption(ToolMaterial material) {
        switch (material) {
            case BASIC:
                return 5;
            case COPPER:
                return 4;
            case IRON:
                return 3;
            case GOLD:
                return 2;
            case IRIDIUM:
                return 1;
            default:
                return 5;
        }
    }


    @Override
    public Tool upgrade() {
        switch (getMaterial()) {
            case BASIC:
                return new Axe(ToolMaterial.COPPER);
            case COPPER:
                return new Axe(ToolMaterial.IRON);
            case IRON:
                return new Axe(ToolMaterial.GOLD);
            case GOLD:
                return new Axe(ToolMaterial.IRIDIUM);
            case IRIDIUM:
                return null; // Already at highest material
            default:
                return null;
        }
    }


    @Override
    public boolean use(String direction) {
        // This method is kept for backward compatibility
        return true;
    }

    @Override
    public boolean use(String direction, GameMap gameMap, Player player) {
        // Get the target tile coordinates based on the player's location and direction
        Location playerLocation = player.getLocation();
        int targetX = playerLocation.xAxis;
        int targetY = playerLocation.yAxis;

        // Adjust coordinates based on direction
        switch (direction.toLowerCase()) {
            case "north" -> targetY--;
            case "south" -> targetY++;
            case "east" -> targetX++;
            case "west" -> targetX--;
            case "north-east" -> { targetX++; targetY--; }
            case "north-west" -> { targetX--; targetY--; }
            case "south-east" -> { targetX++; targetY++; }
            case "south-west" -> { targetX--; targetY++; }
            default -> { return false; } // Invalid direction
        }

        // Check if the target tile is valid and not in another player's farm
        if (!gameMap.isValidCoordinate(targetX, targetY) || gameMap.isInOtherPlayersFarm(player, targetX, targetY)) {
            return false;
        }

        // Check the tile type and perform the appropriate action
        TileType tileType = gameMap.getTile(targetX, targetY);

        // 1. Cut down trees for regular wood and some tree essences
        if (tileType == TileType.TREE) {
            // Add wood to player's inventory (simplified)
            // player.addItem(new Item("Wood", 1));

            // Change the tile to grass
            return gameMap.changeTile(targetX, targetY, "GRASS", player);
        }

        // 2. Remove branches on the ground
        // Since there's no specific branch tile type, we'll assume any item on the ground
        // that's not a tree could be a branch
        Location tile = gameMap.getItem(targetX, targetY);
        if (tile != null) {
            // Remove the item (branch) and add it to the player's inventory
            // player.addItem(new Item("Branch", 1));

            // Clear the tile
            gameMap.placeItem(targetX, targetY, null);
            return true;
        }

        return false;
    }
}
