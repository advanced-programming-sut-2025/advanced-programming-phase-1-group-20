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


    @Override
    public Tool upgrade() {
        switch (getMaterial()) {
            case BASIC:
                return new Pickaxe(ToolMaterial.COPPER);
            case COPPER:
                return new Pickaxe(ToolMaterial.IRON);
            case IRON:
                return new Pickaxe(ToolMaterial.GOLD);
            case GOLD:
                return new Pickaxe(ToolMaterial.IRIDIUM);
            case IRIDIUM:
                return null; // Already at highest material
            default:
                return null;
        }
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