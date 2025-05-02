package org.example.models.Items.Tools;

import org.example.models.Items.Tool;
import org.example.models.enums.PlayerEnums.Skills;

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
        // Implementation will depend on the game mechanics
        // For now, just return true to indicate success
        return true;
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