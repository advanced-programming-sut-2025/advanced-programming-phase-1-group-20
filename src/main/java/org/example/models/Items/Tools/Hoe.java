package org.example.models.Items.Tools;

import org.example.models.Items.Tool;
import org.example.models.enums.PlayerEnums.Skills;

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
        // Implementation will depend on the game mechanics
        // For now, just return true to indicate success
        return true;
    }
}