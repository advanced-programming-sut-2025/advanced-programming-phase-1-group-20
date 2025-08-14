package org.example.common.models.enums.Types;

import org.example.common.models.Items.Item;
import org.example.common.models.Items.Mineral;
import org.example.common.models.Items.Tool;
import org.example.common.models.MapDetails.GameMap;
import org.example.common.models.Player.Player;
import org.example.common.models.common.Location;
import org.example.common.models.enums.PlayerEnums.Skills;

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

                if (!gameMap.getFarmByPlayer(player).contains(targetX, targetY)) {
                    //TODO : removed this condition check it kasra gameMap.isInOtherPlayersFarm(player, targetX, targetY)
                    return false;
                }

                TileType tileType = gameMap.getFarmByPlayer(player).getTile(targetX, targetY);

                // 1. Cut down trees for regular wood and some tree essences
                if (tileType == TileType.TREE) {
                    Item wood = new Mineral(MineralType.Wood);
                    wood.setBaseSellPrice(20);
                    player.getBackpack().add(wood, 100);
                    return gameMap.getFarmByPlayer(player).changeTile(targetX, targetY, TileType.Dirt, player);
                }

                // 2. Remove branches on the ground
                Location tile = gameMap.getFarmByPlayer(player).getItem(targetX, targetY);
                if (tile != null) {
                    gameMap.getFarmByPlayer(player).changeTile(targetX, targetY, TileType.Dirt, null);
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
                    case BASIC -> 3;
                    case COPPER -> 2;
                    case IRON -> 2;
                    case GOLD -> 1;
                    case IRIDIUM -> 1;
                    default -> 3;
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
                if (!gameMap.getFarmByPlayer(player).contains(targetX, targetY)) {
                    //TODO : removed this condition check it kasra gameMap.isInOtherPlayersFarm(player, targetX, targetY)
                    return false;
                }

                // Check the tile type and perform the appropriate action
                TileType tileType = gameMap.getFarmByPlayer(player).getTile(targetX, targetY);

                // 1. Fill the watering can with water if the tile is water or lake
                if (tileType == TileType.WATER || tileType == TileType.LAKE) {
                    // Check if watering can is already full
                    if (tool.isFull()) {
                        return false; // Already full
                    }
                    // Fill the watering can to full capacity
                    return tool.fill();
                }

                // 2. Water crops if the tile is tilled soil with a crop
                // Check if the tile is tilled soil
                if (gameMap.getFarmByPlayer(player).isPlowed(targetX, targetY)) {
                    // Check if watering can has water
                    if (tool.getCurrentWater() > 0) {
                        // Water the crop and consume water
                        gameMap.getFarmByPlayer(player).sprinkle(targetX, targetY, 1);
                        tool.consumeWater(2);
                        return true;
                    }
                    return false; // No water in the can
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
                    case BASIC -> 3;
                    case COPPER -> 2;
                    case IRON -> 2;
                    case GOLD -> 1;
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
                if (!gameMap.getFarmByPlayer(player).contains(targetX, targetY)) {
                    //TODO : removed this condition check it kasra gameMap.isInOtherPlayersFarm(player, targetX, targetY)
                    return false;
                }

                // Check if the tile is grass and can be tilled
                TileType tileType = gameMap.getFarmByPlayer(player).getTile(targetX, targetY);
                if (tileType == TileType.Dirt) {
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
                        return 3;
                    case COPPER:
                        return 2;
                    case IRON:
                        return 2;
                    case GOLD:
                        return 1;
                    case IRIDIUM:
                        return 1;
                    default:
                        return 3;
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
                if (!gameMap.getFarmByPlayer(player).contains(targetX, targetY)) {
                    return false;
                }

                // Check if the tile is a rock or ore
                TileType tileType = gameMap.getFarmByPlayer(player).getTile(targetX, targetY);
                if (tileType == TileType.STONE ||
                        tileType == TileType.IRON_ORE ||
                        tileType == TileType.GOLD_ORE ||
                        tileType == TileType.DIAMOND_ORE ||
                        tileType == TileType.EMERALD_ORE ||
                        tileType == TileType.IRIDIUM_STONE ||
                        tileType == TileType.JEWEL_STONE ||
                        tileType == TileType.GOLD_STONE) {

                    // Give items based on the tile type
                    switch (tileType) {
                        case STONE:
                            // Give Stone item
                            Item stone = new Mineral(MineralType.Stone);
                            player.getBackpack().add(stone, 1);
                            break;
                        case IRON_ORE:
                            // Give Iron item
                            Item iron = new Mineral(MineralType.Iron);
                            player.getBackpack().add(iron, 1);
                            break;
                        case GOLD_ORE:
                            // Give Gold item
                            Item gold = new Mineral(MineralType.Gold);
                            player.getBackpack().add(gold, 1);
                            break;
                        case DIAMOND_ORE:
                            // Give Diamond item
                            Item diamond = new Mineral(MineralType.Diamond);
                            player.getBackpack().add(diamond, 1);
                            break;
                        case EMERALD_ORE:
                            // Give Emerald item
                            Item emerald = new Mineral(MineralType.Emerald);
                            player.getBackpack().add(emerald, 1);
                            break;
                        case IRIDIUM_STONE:
                            // Give Iridium item
                            Item iridium = new Mineral(MineralType.Iridium);
                            player.getBackpack().add(iridium, 1);
                            break;
                        case JEWEL_STONE:
                            // Give a random gem (Ruby, Amethyst, Topaz, Jade, Aquamarine)
                            MineralType[] gems = {MineralType.Ruby, MineralType.Amethyst, MineralType.Topaz, MineralType.Jade, MineralType.Aquamarine};
                            MineralType randomGem = gems[(int)(Math.random() * gems.length)];
                            Item gem = new Mineral(randomGem);
                            player.getBackpack().add(gem, 1);
                            break;
                        case GOLD_STONE:
                            // Give Gold item
                            Item goldFromStone = new Mineral(MineralType.Gold);
                            player.getBackpack().add(goldFromStone, 1);
                            break;
                    }

                    // Break the rock/ore
                    return gameMap.getFarmByPlayer(player).changeTile(targetX, targetY, TileType.Dirt, player);
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
                        return 3;
                    case COPPER:
                        return 2;
                    case IRON:
                        return 2;
                    case GOLD:
                        return 1;
                    case IRIDIUM:
                        return 1;
                    default:
                        return 3;
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
    ),

    FISHING_ROD(
            // Use function for Fishing Rod
            (tool, params) -> {
                // Fishing is handled separately in the fishing controller
                // This is just a placeholder for the tool functionality
                return true;
            },
            // Upgrade function for Fishing Rod
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
            // Energy consumption function for Fishing Rod
            (material) -> {
                switch (material) {
                    case BASIC:
                        return 4;
                    case COPPER:
                        return 3;
                    case IRON:
                        return 2;
                    case GOLD:
                        return 1;
                    case IRIDIUM:
                        return 1;
                    default:
                        return 4;
                }
            },
            // Base sell price function for Fishing Rod
            (material) -> {
                switch (material) {
                    case BASIC:
                        return 500;
                    case COPPER:
                        return 1800;
                    case IRON:
                        return 7500;
                    case GOLD:
                        return 15000;
                    case IRIDIUM:
                        return 30000;
                    default:
                        return 500;
                }
            },
            // Associated skill for Fishing Rod
            Skills.FISHING
    ),

    SCYTHE(
            // Use function for Scythe
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
                    }
                }

                // Check if the target tile is valid
                if (!gameMap.getFarmByPlayer(player).contains(targetX, targetY)) {
                    return false;
                }

                // Check if the tile is dirt (grass) or has crops
                TileType tileType = gameMap.getFarmByPlayer(player).getTile(targetX, targetY);
                if (tileType == TileType.Dirt) {
                    // Cut grass - this is handled by the existing changeTile method
                    return gameMap.getFarmByPlayer(player).changeTile(targetX, targetY, TileType.Dirt, player);
                }

                // Check if there are crops to harvest
                if (gameMap.getFarmByPlayer(player).isPlowed(targetX, targetY)) {
                    // Harvest crops - this will be handled by the existing harvest system
                    // The scythe can be used to harvest, but the actual harvesting logic
                    // is handled in the PlantController
                    return true;
                }

                return false;
            },
            // Upgrade function for Scythe (Scythe doesn't upgrade)
            (tool) -> null,
            // Energy consumption function for Scythe
            (material) -> 2,
            // Base sell price function for Scythe
            (material) -> 0,
            // Associated skill for Scythe
            Skills.FARMING
    ),

    MILK_PAIL(
            // Use function for Milk Pail
            (tool, params) -> {
                // Milk pail functionality is handled in animal controller
                // This is just a placeholder
                return true;
            },
            // Upgrade function for Milk Pail (Milk Pail doesn't upgrade)
            (tool) -> null,
            // Energy consumption function for Milk Pail
            (material) -> 4,
            // Base sell price function for Milk Pail
            (material) -> 1000,
            // Associated skill for Milk Pail
            Skills.FARMING
    ),

    SHEARS(
            // Use function for Shears
            (tool, params) -> {
                // Shears functionality is handled in animal controller
                // This is just a placeholder
                return true;
            },
            // Upgrade function for Shears (Shears don't upgrade)
            (tool) -> null,
            // Energy consumption function for Shears
            (material) -> 4,
            // Base sell price function for Shears
            (material) -> 1000,
            // Associated skill for Shears
            Skills.FARMING
    ),

    TRASH_CAN(
            // Use function for Trash Can
            (tool, params) -> {
                // Trash can functionality is handled in inventory system
                // This is just a placeholder
                return false;
            },
            // Upgrade function for Trash Can
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
            // Energy consumption function for Trash Can
            (material) -> 0, // Trash can doesn't consume energy
            // Base sell price function for Trash Can
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
            // Associated skill for Trash Can (none)
            null
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
