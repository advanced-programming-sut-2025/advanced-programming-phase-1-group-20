package org.example.models.Items.Tools;

import org.example.models.Items.Tool;
import org.example.models.MapDetails.GameMap;
import org.example.models.Player.Player;
import org.example.models.common.Location;
import org.example.models.enums.PlayerEnums.Skills;
import org.example.models.enums.Types.TileType;

public class Pickaxe extends Tool {

    public Pickaxe() {
        super("Basic Pickaxe", 0, "A basic pickaxe for breaking rocks and mining ores.",
                ToolType.PICKAXE, ToolMaterial.BASIC, 5, Skills.MINING);
    }


    public Pickaxe(ToolMaterial material) {
        super(material.name() + " Pickaxe", getBaseSellPrice(material),
                "A " + material.name().toLowerCase() + " pickaxe for breaking rocks and mining ores.",
                ToolType.PICKAXE, material, getEnergyConsumption(material), Skills.MINING);
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
            case BASIC -> new Pickaxe(ToolMaterial.COPPER);
            case COPPER -> new Pickaxe(ToolMaterial.IRON);
            case IRON -> new Pickaxe(ToolMaterial.GOLD);
            case GOLD -> new Pickaxe(ToolMaterial.IRIDIUM);
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

        // Check the tile type and perform the appropriate action
        TileType tileType = gameMap.getTile(targetX, targetY);

        // 1. Remove the effect of the hoe on soil (check if the tile is tilled)
        if (gameMap.isShokhm(targetX, targetY)) {
            return gameMap.changeTile(targetX, targetY, "GRASS", player);
        }

        // 2. Break stones and ores in the mine
        if (tileType == TileType.STONE || 
            tileType == TileType.IRON_ORE || 
            tileType == TileType.GOLD_ORE || 
            tileType == TileType.DIAMOND_ORE || 
            tileType == TileType.EMERALD_ORE) {

            // Check if the pickaxe can break this type of ore
            String oreType;
            if (tileType == TileType.STONE) {
                oreType = "stone";
            } else if (tileType == TileType.IRON_ORE) {
                oreType = "iron";
            } else if (tileType == TileType.GOLD_ORE) {
                oreType = "gold";
            } else {
                oreType = "gem"; // For diamond and emerald
            }

            if (canBreakOre(oreType)) {
                return gameMap.changeTile(targetX, targetY, "GRASS", player);
            }
            return false;
        }

        // 3. Remove items that the player has placed on the ground
        // Since there's no getItem method in Location, we need to use getItem from GameMap
        Location tile = gameMap.getItem(targetX, targetY);
        // We can't directly check if the tile has an item, so we'll just try to place null
        // and assume it worked
        if (tile != null) {
            gameMap.placeItem(targetX, targetY, null);
            return true;
        }

        return false;
    }


    public boolean canBreakOre(String oreType) {
        // Basic pickaxe can break regular stones
        if (getMaterial() == ToolMaterial.BASIC && oreType.equals("stone")) {
            return true;
        }

        // Copper pickaxe can break copper and iron ores
        if (getMaterial() == ToolMaterial.COPPER &&
                (oreType.equals("stone") || oreType.equals("copper") || oreType.equals("iron"))) {
            return true;
        }

        // Iron pickaxe can break copper, iron, and gold ores, as well as gems
        if (getMaterial() == ToolMaterial.IRON &&
                (oreType.equals("stone") || oreType.equals("copper") ||
                        oreType.equals("iron") || oreType.equals("gold") || oreType.equals("gem"))) {
            return true;
        }

        // Gold and Iridium pickaxes can break all ore types
        if ((getMaterial() == ToolMaterial.GOLD || getMaterial() == ToolMaterial.IRIDIUM)) {
            return true;
        }

        return false;
    }
}
