package org.example.models.Items;

import org.example.models.MapDetails.GameMap;
import org.example.models.Player.Player;
import org.example.models.enums.PlayerEnums.Skills;

public class Tool extends Item {
    private ToolType type;
    private ToolMaterial material;
    private int energyConsumption;
    private Skills associatedSkill;
    private boolean equipped;


    public Tool(String name, int baseSellPrice, String description, ToolType type,
                ToolMaterial material, int energyConsumption, Skills associatedSkill) {
        super(name, baseSellPrice, description);
        this.type = type;
        this.material = material;
        this.energyConsumption = energyConsumption;
        this.associatedSkill = associatedSkill;
        this.equipped = false;
    }


    public int getEnergyConsumption(int skillLevel) {
        // If the skill is at max level, reduce energy consumption by 1
        if (skillLevel == 4) {
            return Math.max(0, energyConsumption - 1);
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
        return false;
    }

    public boolean use(String direction, GameMap gameMap, Player player) {
        return false;
    }


    public Tool upgrade() {
        // To be implemented by subclasses
        return null;
    }


    public enum ToolType {
        HOE,
        PICKAXE,
        AXE,
        WATERING_CAN,
        FISHING_ROD,
        SCYTHE,
        MILK_PAIL,
        SHEARS,
        TRASH_CAN
    }


    public enum ToolMaterial {
        BASIC,
        COPPER,
        IRON,
        GOLD,
        IRIDIUM
    }
}
