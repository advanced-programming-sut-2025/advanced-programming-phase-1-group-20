package org.example.models.Items;

import org.example.models.App;
import org.example.models.MapDetails.GameMap;
import org.example.models.Player.Player;
import org.example.models.enums.PlayerEnums.Skills;
import org.example.models.enums.Types.ToolFunctionality;
import org.example.models.enums.Weather;

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
        super(name, baseSellPrice, description);
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
        // For tools with functionality, use the function pointer
        if (functionality != null) {
            return functionality.use(this, direction);
        }

        // For Scythe, always return true
        if (type == ToolType.SCYTHE) {
            return true;
        }

        // For TrashCan, always return false
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
                // Create a new tool with the upgraded material
                return new Tool(
                        upgradeMaterial.name() + " " + type.name(),
                        functionality.getBaseSellPrice(upgradeMaterial),
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
                        "COPPER TRASH_CAN",
                        2000,
                        "A copper trash can for disposing of items.",
                        ToolType.TRASH_CAN,
                        ToolMaterial.COPPER,
                        0,
                        null,
                        null
                );
                case COPPER -> new Tool(
                        "IRON TRASH_CAN",
                        5000,
                        "An iron trash can for disposing of items.",
                        ToolType.TRASH_CAN,
                        ToolMaterial.IRON,
                        0,
                        null,
                        null
                );
                case IRON -> new Tool(
                        "GOLD TRASH_CAN",
                        10000,
                        "A gold trash can for disposing of items.",
                        ToolType.TRASH_CAN,
                        ToolMaterial.GOLD,
                        0,
                        null,
                        null
                );
                case GOLD -> new Tool(
                        "IRIDIUM TRASH_CAN",
                        25000,
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

    public boolean fill() {
        if (type == ToolType.WATERING_CAN) {
            this.currentWater = this.capacity;
            return true;
        }
        return false;
    }

    public int getCurrentWater() {
        return currentWater;
    }

    public int getCapacity() {
        return capacity;
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
