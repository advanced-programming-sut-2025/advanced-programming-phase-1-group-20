package org.example.models.Items.Tools;

import org.example.models.Items.Tool;
import org.example.models.MapDetails.GameMap;
import org.example.models.Player.Player;
import org.example.models.common.Location;
import org.example.models.enums.PlayerEnums.Skills;
import org.example.models.enums.Types.TileType;


public class WateringCan extends Tool {
    private int capacity;
    private int currentWater;


    public WateringCan() {
        super("Basic Watering Can", 0, "A basic watering can for watering crops.",
                ToolType.WATERING_CAN, ToolMaterial.BASIC, 5, Skills.FARMING);
        this.capacity = 40;
        this.currentWater = 0;
    }


    public WateringCan(ToolMaterial material) {
        super(material.name() + " Watering Can", getBaseSellPrice(material),
                "A " + material.name().toLowerCase() + " watering can for watering crops.",
                ToolType.WATERING_CAN, material, getEnergyConsumption(material), Skills.FARMING);
        this.capacity = getCapacity(material);
        this.currentWater = 0;
    }


    private static int getBaseSellPrice(ToolMaterial material) {
        switch (material) {
            case BASIC:
                return 0;
            case COPPER:
                return 2000;
            case IRON:
                return 5000;
            case GOLD:
                return 10000;
            case IRIDIUM:
                return 25000;
            default:
                return 0;
        }
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

    /**
     * Get the capacity based on the material
     *
     * @param material The material of the watering can
     * @return The capacity
     */
    private static int getCapacity(ToolMaterial material) {
        switch (material) {
            case BASIC:
                return 40;
            case COPPER:
                return 55;
            case IRON:
                return 70;
            case GOLD:
                return 85;
            case IRIDIUM:
                return 100;
            default:
                return 40;
        }
    }


    @Override
    public Tool upgrade() {
        switch (getMaterial()) {
            case BASIC:
                return new WateringCan(ToolMaterial.COPPER);
            case COPPER:
                return new WateringCan(ToolMaterial.IRON);
            case IRON:
                return new WateringCan(ToolMaterial.GOLD);
            case GOLD:
                return new WateringCan(ToolMaterial.IRIDIUM);
            case IRIDIUM:
                return null; // Already at highest material
            default:
                return null;
        }
    }


    @Override
    public boolean use(String direction) {
        // This method is kept for backward compatibility
        // If the watering can is empty, it can't be used to water crops
        if (currentWater <= 0) {
            return false;
        }

        // Decrease the water level by 1
        currentWater--;
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

        // 1. Fill the watering can with water if the tile is water
        if (tileType == TileType.WATER) {
            return fill();
        }

        // 2. Water crops if the tile is tilled soil with a crop
        // If the watering can is empty, it can't be used to water crops
        if (currentWater <= 0) {
            return false;
        }

        // Check if the tile is tilled soil
        if (gameMap.isShokhm(targetX, targetY)) {
            // Decrease the water level by 1
            currentWater--;

            // Water the crop (implementation depends on the game mechanics)
            // For now, just return true to indicate success
            return true;
        }

        return false;
    }

    public boolean fill() {
        // Fill the watering can to capacity
        this.currentWater = this.capacity;
        return true;
    }


    public int getCurrentWater() {
        return currentWater;
    }

    public int getCapacity() {
        return capacity;
    }
}
