package org.example.models.enums.Types;

import org.example.models.Items.Tool;
import org.example.models.MapDetails.GameMap;
import org.example.models.Player.Player;
import org.example.models.common.Location;
import org.example.models.enums.PlayerEnums.Skills;

import java.util.function.BiFunction;
import java.util.function.Function;

public enum ToolFunctionality {
    AXE(
            // Use function for Axe
            (tool, params) -> {
                String direction = (String) params[0];
                GameMap gameMap = (GameMap) params[1];
                Player player = (Player) params[2];

                Location playerLocation = player.getLocation();
                int targetX = playerLocation.xAxis;
                int targetY = playerLocation.yAxis;

                switch (direction.toLowerCase()) {
                    case "north" -> targetY--;
                    case "south" -> targetY++;
                    case "east" -> targetX++;
                    case "west" -> targetX--;
                    case "north-east" -> {
                        targetX++;
                        targetY--;
                    }
                    case "north-west" -> {
                        targetX--;
                        targetY--;
                    }
                    case "south-east" -> {
                        targetX++;
                        targetY++;
                    }
                    case "south-west" -> {
                        targetX--;
                        targetY++;
                    }
                    default -> {
                        return false;
                    }
                }

                if (!gameMap.getFarmByPlayer(player).contains(targetX, targetY) || gameMap.isInOtherPlayersFarm(player, targetX, targetY)) {
                    return false;
                }

                TileType tileType = gameMap.getFarmByPlayer(player).getTile(targetX, targetY);

                // 1. Cut down trees for regular wood and some tree essences
                if (tileType == TileType.TREE) {
                    // Change the tile to grass
                    return gameMap.getFarmByPlayer(player).changeTile(targetX, targetY, TileType.GRASS, player);
                }

                // 2. Remove branches on the ground
                Location tile = gameMap.getFarmByPlayer(player).getItem(targetX, targetY);
                if (tile != null) {
                    // Clear the tile
                    gameMap.getFarmByPlayer(player).placeItem(targetX, targetY, null);
                    return true;
                }

                return false;
            },
            // Upgrade function for Axe
            (tool) -> {
                Tool.ToolMaterial currentMaterial = tool.getMaterial();
                return switch (currentMaterial) {
                    case BASIC -> Tool.ToolMaterial.COPPER;
                    case COPPER -> Tool.ToolMaterial.IRON;
                    case IRON -> Tool.ToolMaterial.GOLD;
                    case GOLD -> Tool.ToolMaterial.IRIDIUM;
                    default -> null; // Already at highest material
                };
            },
            // Energy consumption function for Axe
            (material) -> {
                return switch (material) {
                    case BASIC -> 5;
                    case COPPER -> 4;
                    case IRON -> 3;
                    case GOLD -> 2;
                    case IRIDIUM -> 1;
                    default -> 5;
                };
            },
            // Base sell price function for Axe
            (material) -> {
                return switch (material) {
                    case BASIC -> 0;
                    case COPPER -> 2000;
                    case IRON -> 5000;
                    case GOLD -> 10000;
                    case IRIDIUM -> 25000;
                };
            },
            Skills.FORAGING
    ),

    WATERING_CAN(
            // Use function for Watering Can
            (tool, params) -> {
                String direction = (String) params[0];
                GameMap gameMap = (GameMap) params[1];
                Player player = (Player) params[2];

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
                    case "north-east" -> {
                        targetX++;
                        targetY--;
                    }
                    case "north-west" -> {
                        targetX--;
                        targetY--;
                    }
                    case "south-east" -> {
                        targetX++;
                        targetY++;
                    }
                    case "south-west" -> {
                        targetX--;
                        targetY++;
                    }
                    default -> {
                        return false;
                    } // Invalid direction
                }

                // Check if the target tile is valid and not in another player's farm
                if (!gameMap.getFarmByPlayer(player).contains(targetX, targetY) || gameMap.isInOtherPlayersFarm(player, targetX, targetY)) {
                    return false;
                }

                // Check the tile type and perform the appropriate action
                TileType tileType = gameMap.getFarmByPlayer(player).getTile(targetX, targetY);

                // 1. Fill the watering can with water if the tile is water
                if (tileType == TileType.WATER) {
                    // This would need to be handled by the specific WateringCan class
                    return true;
                }

                // 2. Water crops if the tile is tilled soil with a crop
                // Check if the tile is tilled soil
                if (gameMap.getFarmByPlayer(player).isPlowed(targetX, targetY)) {
                    // Water the crop (implementation depends on the game mechanics)
                    return true;
                }

                return false;
            },
            // Upgrade function for Watering Can
            (tool) -> {
                Tool.ToolMaterial currentMaterial = tool.getMaterial();
                switch (currentMaterial) {
                    case BASIC:
                        return Tool.ToolMaterial.COPPER;
                    case COPPER:
                        return Tool.ToolMaterial.IRON;
                    case IRON:
                        return Tool.ToolMaterial.GOLD;
                    case GOLD:
                        return Tool.ToolMaterial.IRIDIUM;
                    default:
                        return null; // Already at highest material
                }
            },
            // Energy consumption function for Watering Can
            (material) -> {
                return switch (material) {
                    case BASIC -> 5;
                    case COPPER -> 4;
                    case IRON -> 3;
                    case GOLD -> 2;
                    case IRIDIUM -> 1;
                };
            },
            // Base sell price function for Watering Can
            (material) -> {
                return switch (material) {
                    case BASIC -> 0;
                    case COPPER -> 2000;
                    case IRON -> 5000;
                    case GOLD -> 10000;
                    case IRIDIUM -> 25000;
                };
            },
            // Associated skill for Watering Can
            Skills.FARMING
    ),

    HOE(
            // Use function for Hoe
            (tool, params) -> {
                String direction = (String) params[0];
                GameMap gameMap = (GameMap) params[1];
                Player player = (Player) params[2];

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
                    case "north-east" -> {
                        targetX++;
                        targetY--;
                    }
                    case "north-west" -> {
                        targetX--;
                        targetY--;
                    }
                    case "south-east" -> {
                        targetX++;
                        targetY++;
                    }
                    case "south-west" -> {
                        targetX--;
                        targetY++;
                    }
                    default -> {
                        return false;
                    } // Invalid direction
                }

                // Check if the target tile is valid and not in another player's farm
                if (!gameMap.getFarmByPlayer(player).contains(targetX, targetY) || gameMap.isInOtherPlayersFarm(player, targetX, targetY)) {
                    return false;
                }

                // Check if the tile is grass and can be tilled
                TileType tileType = gameMap.getFarmByPlayer(player).getTile(targetX, targetY);
                if (tileType == TileType.GRASS) {
                    // Till the soil

                    return gameMap.getFarmByPlayer(player).changeTile(targetX, targetY, TileType.PLOWED, player);
                }

                return false;
            },
            // Upgrade function for Hoe
            (tool) -> {
                Tool.ToolMaterial currentMaterial = tool.getMaterial();
                switch (currentMaterial) {
                    case BASIC:
                        return Tool.ToolMaterial.COPPER;
                    case COPPER:
                        return Tool.ToolMaterial.IRON;
                    case IRON:
                        return Tool.ToolMaterial.GOLD;
                    case GOLD:
                        return Tool.ToolMaterial.IRIDIUM;
                    default:
                        return null; // Already at highest material
                }
            },
            // Energy consumption function for Hoe
            (material) -> {
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
            },
            // Base sell price function for Hoe
            (material) -> {
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
            },
            // Associated skill for Hoe
            Skills.FARMING
    ),

    PICKAXE(
            // Use function for Pickaxe
            (tool, params) -> {
                String direction = (String) params[0];
                GameMap gameMap = (GameMap) params[1];
                Player player = (Player) params[2];

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
                    case "north-east" -> {
                        targetX++;
                        targetY--;
                    }
                    case "north-west" -> {
                        targetX--;
                        targetY--;
                    }
                    case "south-east" -> {
                        targetX++;
                        targetY++;
                    }
                    case "south-west" -> {
                        targetX--;
                        targetY++;
                    }
                    default -> {
                        return false;
                    } // Invalid direction
                }

                // Check if the target tile is valid and not in another player's farm
                if (!gameMap.getFarmByPlayer(player).contains(targetX, targetY) || gameMap.isInOtherPlayersFarm(player, targetX, targetY)) {
                    return false;
                }

                // Check if the tile is a rock or ore
                TileType tileType = gameMap.getFarmByPlayer(player).getTile(targetX, targetY);
                if (tileType == TileType.STONE ||
                        tileType == TileType.IRON_ORE ||
                        tileType == TileType.GOLD_ORE ||
                        tileType == TileType.DIAMOND_ORE ||
                        tileType == TileType.EMERALD_ORE) {

                    // Break the rock/ore
                    return gameMap.getFarmByPlayer(player).changeTile(targetX, targetY, TileType.GRASS, player);
                }


                return false;
            },
            // Upgrade function for Pickaxe
            (tool) -> {
                Tool.ToolMaterial currentMaterial = tool.getMaterial();
                switch (currentMaterial) {
                    case BASIC:
                        return Tool.ToolMaterial.COPPER;
                    case COPPER:
                        return Tool.ToolMaterial.IRON;
                    case IRON:
                        return Tool.ToolMaterial.GOLD;
                    case GOLD:
                        return Tool.ToolMaterial.IRIDIUM;
                    default:
                        return null; // Already at highest material
                }
            },
            // Energy consumption function for Pickaxe
            (material) -> {
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
            },
            // Base sell price function for Pickaxe
            (material) -> {
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
            },
            // Associated skill for Pickaxe
            Skills.MINING
    );

    // Function pointer for tool use functionality
    private final BiFunction<Tool, Object[], Boolean> useFunction;

    // Function pointer for tool upgrade functionality
    private final Function<Tool, Tool.ToolMaterial> upgradeFunction;

    // Function to determine energy consumption based on material
    private final Function<Tool.ToolMaterial, Integer> energyConsumptionFunction;

    // Function to determine base sell price based on material
    private final Function<Tool.ToolMaterial, Integer> baseSellPriceFunction;

    // Associated skill for this tool type
    private final Skills associatedSkill;

    ToolFunctionality(
            BiFunction<Tool, Object[], Boolean> useFunction,
            Function<Tool, Tool.ToolMaterial> upgradeFunction,
            Function<Tool.ToolMaterial, Integer> energyConsumptionFunction,
            Function<Tool.ToolMaterial, Integer> baseSellPriceFunction,
            Skills associatedSkill) {
        this.useFunction = useFunction;
        this.upgradeFunction = upgradeFunction;
        this.energyConsumptionFunction = energyConsumptionFunction;
        this.baseSellPriceFunction = baseSellPriceFunction;
        this.associatedSkill = associatedSkill;
    }

    public boolean use(Tool tool, Object... params) {
        return useFunction.apply(tool, params);
    }

    public Tool.ToolMaterial getUpgradeMaterial(Tool tool) {
        return upgradeFunction.apply(tool);
    }


    public int getEnergyConsumption(Tool.ToolMaterial material) {
        return energyConsumptionFunction.apply(material);
    }


    public int getBaseSellPrice(Tool.ToolMaterial material) {
        return baseSellPriceFunction.apply(material);
    }


    public Skills getAssociatedSkill() {
        return associatedSkill;
    }
}