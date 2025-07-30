package org.example.common.models.Items;

import org.example.common.models.App;
import org.example.common.models.MapDetails.GameMap;
import org.example.common.models.Player.Player;
import org.example.common.models.enums.PlayerEnums.Skills;
import org.example.common.models.enums.Types.ToolFunctionality;
import org.example.common.models.enums.Weather;

public class Tool extends Item {
    private ToolType type;
    private ToolMaterial material;
    private int energyConsumption;
    private Skills associatedSkill;
    private boolean equipped;
    private ToolFunctionality functionality;

    // WateringCan specific fields
    private int capacity;
    private int currentWater;

    // TrashCan specific fields
    private TrashCanType trashCanType;
    private double returnPercentage;

    // Constructor for most tools
    public Tool(String name, int baseSellPrice, String description, ToolType type, ToolMaterial material, int energyConsumption, Skills associatedSkill, ToolFunctionality functionality) {
        super(name, baseSellPrice, "" , description);
        this.type = type;
        this.material = material;
        this.energyConsumption = energyConsumption;
        this.associatedSkill = associatedSkill;
        this.equipped = false;
        this.functionality = functionality;

        // Initialize WateringCan specific fields if this is a watering can
        if (type == ToolType.WATERING_CAN) {
            this.capacity = getWateringCanCapacity(material);
            this.currentWater = 0;
        }

        // Initialize TrashCan specific fields if this is a trash can
        if (type == ToolType.TRASH_CAN) {
            this.trashCanType = getTrashCanType(material);
            this.returnPercentage = getTrashCanReturnPercentage(material);
        }
    }

    // Constructor for tools without functionality (like Scythe)
    public Tool(String name, int baseSellPrice, String description, ToolType type, ToolMaterial material, int energyConsumption, Skills associatedSkill) {
        super(name, baseSellPrice, description);
        this.type = type;
        this.material = material;
        this.energyConsumption = energyConsumption;
        this.associatedSkill = associatedSkill;
        this.equipped = false;
        this.functionality = null;
    }

    // New constructor for tools with image path and description
    public Tool(String name, int baseSellPrice, String imageFilePath, String description, ToolType type, ToolMaterial material, int energyConsumption, Skills associatedSkill, ToolFunctionality functionality) {
        super(name, baseSellPrice, imageFilePath, description);
        this.type = type;
        this.material = material;
        this.energyConsumption = energyConsumption;
        this.associatedSkill = associatedSkill;
        this.equipped = false;
        this.functionality = functionality;
        if (type == ToolType.WATERING_CAN) {
            this.capacity = getWateringCanCapacity(material);
            this.currentWater = 0;
        }
        if (type == ToolType.TRASH_CAN) {
            this.trashCanType = getTrashCanType(material);
            this.returnPercentage = getTrashCanReturnPercentage(material);
        }
    }

    public Tool() {
        super("tool", 0, "A basic tool.");
    }

    private static int getWateringCanCapacity(ToolMaterial material) {
        return switch (material) {
            case BASIC -> 40;
            case COPPER -> 55;
            case IRON -> 70;
            case GOLD -> 85;
            case IRIDIUM -> 100;
        };
    }

    private static TrashCanType getTrashCanType(ToolMaterial material) {
        return switch (material) {
            case BASIC -> TrashCanType.INITIAL;
            case COPPER -> TrashCanType.COPPER;
            case IRON -> TrashCanType.IRON;
            case GOLD -> TrashCanType.GOLD;
            case IRIDIUM -> TrashCanType.IRIDIUM;
        };
    }

    private static double getTrashCanReturnPercentage(ToolMaterial material) {
        return switch (material) {
            case BASIC -> 0.0;
            case COPPER -> 0.15;
            case IRON -> 0.30;
            case GOLD -> 0.45;
            case IRIDIUM -> 0.60;
        };
    }

    public int getEnergyConsumption(int skillLevel) {
        // If the skill is at max level, reduce energy consumption by 1
        // check energy consumption by weather
        if (skillLevel == 4) {
            return Math.max(0, energyConsumption - 1);
        }

        if (App.getGame().getDate().getWeatherToday().equals(Weather.RAINY) ||
                App.getGame().getDate().getWeatherToday().equals(Weather.STORMY)) {
            energyConsumption *= 1.5;
        }
        if (App.getGame().getDate().getWeatherToday().equals(Weather.SNOWY)) {
            energyConsumption *= 2;
        }
        return energyConsumption;
    }

    public ToolType getType() {
        return type;
    }

    public ToolMaterial getMaterial() {
        return material;
    }

    public Skills getAssociatedSkill() {
        return associatedSkill;
    }

    public boolean isEquipped() {
        return equipped;
    }

    public void equip() {
        this.equipped = true;
    }

    public void unequip() {
        this.equipped = false;
    }

    public boolean use(String direction) {
        if (functionality != null) {
            return functionality.use(this, direction);
        }

        if (type == ToolType.SCYTHE) {
            return true;
        }

        if (type == ToolType.TRASH_CAN) {
            return false;
        }

        return false;
    }

    public boolean use(String direction, GameMap gameMap, Player player) {
        // Use the function pointer from the ToolFunctionality enum
        if (functionality != null) {
            return functionality.use(this, direction, gameMap, player);
        }
        return false;
    }

    public Tool upgrade() {
        // For tools with functionality, use the function pointer
        if (functionality != null) {
            ToolMaterial upgradeMaterial = functionality.getUpgradeMaterial(this);
            if (upgradeMaterial != null) {
                // Generate the correct image file path for the upgraded tool
                String imageFilePath = generateImageFilePath(type, upgradeMaterial);
                String toolName = generateToolName(type, upgradeMaterial);

                // Create a new tool with the upgraded material and correct image path
                return new Tool(
                        toolName,
                        functionality.getBaseSellPrice(upgradeMaterial),
                        imageFilePath,
                        "A " + upgradeMaterial.name().toLowerCase() + " " + type.name().toLowerCase() + ".",
                        type,
                        upgradeMaterial,
                        functionality.getEnergyConsumption(upgradeMaterial),
                        functionality.getAssociatedSkill(),
                        functionality
                );
            }
        }

        if (type == ToolType.TRASH_CAN) {
            return switch (material) {
                case BASIC -> new Tool(
                        "Copper Trash Can",
                        2000,
                        "content/Tools/Trash_Can_Copper.png",
                        "A copper trash can for disposing of items.",
                        ToolType.TRASH_CAN,
                        ToolMaterial.COPPER,
                        0,
                        null,
                        null
                );
                case COPPER -> new Tool(
                        "Iron Trash Can",
                        5000,
                        "content/Tools/Trash_Can_Steel.png",
                        "An iron trash can for disposing of items.",
                        ToolType.TRASH_CAN,
                        ToolMaterial.IRON,
                        0,
                        null,
                        null
                );
                case IRON -> new Tool(
                        "Gold Trash Can",
                        10000,
                        "content/Tools/Trash_Can_Gold.png",
                        "A gold trash can for disposing of items.",
                        ToolType.TRASH_CAN,
                        ToolMaterial.GOLD,
                        0,
                        null,
                        null
                );
                case GOLD -> new Tool(
                        "Iridium Trash Can",
                        25000,
                        "content/Tools/Trash_Can_Iridium.png",
                        "An iridium trash can for disposing of items.",
                        ToolType.TRASH_CAN,
                        ToolMaterial.IRIDIUM,
                        0,
                        null,
                        null
                );
                case IRIDIUM -> null; // Already at highest material
            };
        }

        if (type == ToolType.SCYTHE) {
            return null;
        }

        return null;
    }

    private String generateImageFilePath(ToolType toolType, ToolMaterial material) {
        String basePath = "content/Tools/";

        switch (toolType) {
            case HOE -> {
                return switch (material) {
                    case BASIC -> basePath + "Hoe/Hoe.png";
                    case COPPER -> basePath + "Hoe/Copper_Hoe.png";
                    case IRON -> basePath + "Hoe/Steel_Hoe.png";
                    case GOLD -> basePath + "Hoe/Gold_Hoe.png";
                    case IRIDIUM -> basePath + "Hoe/Iridium_Hoe.png";
                };
            }
            case PICKAXE -> {
                return switch (material) {
                    case BASIC -> basePath + "Pickaxe/Pickaxe.png";
                    case COPPER -> basePath + "Pickaxe/Copper_Pickaxe.png";
                    case IRON -> basePath + "Pickaxe/Steel_Pickaxe.png";
                    case GOLD -> basePath + "Pickaxe/Gold_Pickaxe.png";
                    case IRIDIUM -> basePath + "Pickaxe/Iridium_Pickaxe.png";
                };
            }
            case AXE -> {
                return switch (material) {
                    case BASIC -> basePath + "Axe/Axe.png";
                    case COPPER -> basePath + "Axe/Copper_Axe.png";
                    case IRON -> basePath + "Axe/Steel_Axe.png";
                    case GOLD -> basePath + "Axe/Gold_Axe.png";
                    case IRIDIUM -> basePath + "Axe/Iridium_Axe.png";
                };
            }
            case WATERING_CAN -> {
                return switch (material) {
                    case BASIC -> basePath + "Watering_Can/Watering_Can.png";
                    case COPPER -> basePath + "Watering_Can/Copper_Watering_Can.png";
                    case IRON -> basePath + "Watering_Can/Steel_Watering_Can.png";
                    case GOLD -> basePath + "Watering_Can/Gold_Watering_Can.png";
                    case IRIDIUM -> basePath + "Watering_Can/Iridium_Watering_Can.png";
                };
            }
            case SCYTHE -> {
                return basePath + "Scythe.png";
            }
            case MILK_PAIL -> {
                return basePath + "Milk_Pail.png";
            }
            case SHEARS -> {
                return basePath + "Shears.png";
            }
            case FISHING_ROD -> {
                return switch (material) {
                    case BASIC -> basePath + "Fishing_Pole/Bamboo_Pole.png";
                    case COPPER -> basePath + "Fishing_Pole/Fiberglass_Rod.png";
                    case IRON -> basePath + "Fishing_Pole/Iridium_Rod.png";
                    case GOLD -> basePath + "Fishing_Pole/Iridium_Rod.png";
                    case IRIDIUM -> basePath + "Fishing_Pole/Advanced_Iridium_Rod.png";
                };
            }
            default -> {
                return "";
            }
        }
    }

    private String generateToolName(ToolType toolType, ToolMaterial material) {
        String materialName = switch (material) {
            case BASIC -> "Basic";
            case COPPER -> "Copper";
            case IRON -> "Iron";
            case GOLD -> "Gold";
            case IRIDIUM -> "Iridium";
        };

        String toolTypeName = switch (toolType) {
            case HOE -> "Hoe";
            case PICKAXE -> "Pickaxe";
            case AXE -> "Axe";
            case WATERING_CAN -> "Watering Can";
            case FISHING_ROD -> "Fishing Rod";
            case SCYTHE -> "Scythe";
            case MILK_PAIL -> "Milk Pail";
            case SHEARS -> "Shears";
            case TRASH_CAN -> "Trash Can";
        };

        return materialName + " " + toolTypeName;
    }

    public boolean fill() {
        if (type == ToolType.WATERING_CAN) {
            this.currentWater = this.capacity;
            return true;
        }
        return false;
    }

    public boolean fill(int amount) {
        if (type == ToolType.WATERING_CAN) {
            int newAmount = Math.min(this.currentWater + amount, this.capacity);
            this.currentWater = newAmount;
            return true;
        }
        return false;
    }

    public boolean consumeWater(int amount) {
        if (type == ToolType.WATERING_CAN) {
            if (this.currentWater >= amount) {
                this.currentWater -= amount;
                return true;
            }
            return false; // Not enough water
        }
        return false; // Not a watering can
    }

    public int getCurrentWater() {
        return currentWater;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isEmpty() {
        return type == ToolType.WATERING_CAN && currentWater <= 0;
    }

    public boolean isFull() {
        return type == ToolType.WATERING_CAN && currentWater >= capacity;
    }

    public float getWaterPercentage() {
        if (type == ToolType.WATERING_CAN && capacity > 0) {
            return (float) currentWater / capacity;
        }
        return 0.0f;
    }

    public boolean needsRefill() {
        return type == ToolType.WATERING_CAN && currentWater < capacity * 0.1f; // Less than 10% full
    }

    public String getWaterLevelString() {
        if (type == ToolType.WATERING_CAN) {
            return currentWater + "/" + capacity;
        }
        return "";
    }

    // TrashCan specific methods
    public TrashCanType getTrashCanType() {
        return trashCanType;
    }

    public double getReturnPercentage() {
        return returnPercentage;
    }

    public int calculateReturnValue(int itemValue) {
        return (int) (itemValue * returnPercentage);
    }


    public enum ToolType {
        HOE, PICKAXE, AXE, WATERING_CAN, FISHING_ROD, SCYTHE, MILK_PAIL, SHEARS, TRASH_CAN
    }

    public enum ToolMaterial {
        BASIC, COPPER, IRON, GOLD, IRIDIUM
    }

    public enum TrashCanType {
        INITIAL,
        COPPER,
        IRON,
        GOLD,
        IRIDIUM
    }
}
