package org.example.models.Items.Tools;

import org.example.models.Items.Tool;
import org.example.models.enums.PlayerEnums.Skills;


public class Axe extends Tool {

    public Axe() {
        super("Basic Axe", 0, "A basic axe for cutting down trees and breaking branches.",
                ToolType.AXE, ToolMaterial.BASIC, 5, Skills.FORAGING);
    }


    public Axe(ToolMaterial material) {
        super(material.name() + " Axe", getBaseSellPrice(material),
                "A " + material.name().toLowerCase() + " axe for cutting down trees and breaking branches.",
                ToolType.AXE, material, getEnergyConsumption(material), Skills.FORAGING);
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
                return new Axe(ToolMaterial.COPPER);
            case COPPER:
                return new Axe(ToolMaterial.IRON);
            case IRON:
                return new Axe(ToolMaterial.GOLD);
            case GOLD:
                return new Axe(ToolMaterial.IRIDIUM);
            case IRIDIUM:
                return null; // Already at highest material
            default:
                return null;
        }
    }


    @Override
    public boolean use(String direction) {
        // TODO: add using
        return true;
    }
}