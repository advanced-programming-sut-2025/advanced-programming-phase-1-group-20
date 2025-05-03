package org.example.models.Items.Tools;

import org.example.models.Items.Tool;
import org.example.models.MapDetails.GameMap;
import org.example.models.Player.Player;
import org.example.models.common.Location;
import org.example.models.enums.PlayerEnums.Skills;
import org.example.models.enums.Types.TileType;

public class Hoe extends Tool {

    public Hoe() {
        super("Basic Hoe", 0, "A basic hoe for tilling soil.", ToolType.HOE, ToolMaterial.BASIC, 5, Skills.FARMING);
    }

    public Hoe(ToolMaterial material) {
        super(material.name() + " Hoe", getBaseSellPrice(material),
                "A " + material.name().toLowerCase() + " hoe for tilling soil.",
                ToolType.HOE, material, getEnergyConsumption(material), Skills.FARMING);
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
        return switch (material) {
            case BASIC -> 5;
            case COPPER -> 4;
            case IRON -> 3;
            case GOLD -> 2;
            case IRIDIUM -> 1;
        };
    }

    @Override
    public Tool upgrade() {
        return switch (getMaterial()) {
            case BASIC -> new Hoe(ToolMaterial.COPPER);
            case COPPER -> new Hoe(ToolMaterial.IRON);
            case IRON -> new Hoe(ToolMaterial.GOLD);
            case GOLD -> new Hoe(ToolMaterial.IRIDIUM);
            case IRIDIUM -> null; // Already at highest material
        };
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

        // Check if the tile is grass (can be tilled)
        TileType tileType = gameMap.getTile(targetX, targetY);
        if (tileType != TileType.GRASS) {
            return false;
        }

        // Till the soil (change the tile to tilled soil)
        return gameMap.changeTile(targetX, targetY, "TILLED_SOIL", player);
    }
}
