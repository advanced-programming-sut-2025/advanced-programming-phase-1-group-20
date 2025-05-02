package org.example.models.Items.Tools;

import org.example.models.Items.Tool;
import org.example.models.enums.PlayerEnums.Skills;

/**
 * Hoe tool for tilling soil for planting crops.
 */
public class Hoe extends Tool {
    /**
     * Constructor for Hoe with BASIC material
     */
    public Hoe() {
        super("Basic Hoe", 0, "A basic hoe for tilling soil.", ToolType.HOE, ToolMaterial.BASIC, 5, Skills.FARMING);
    }

    /**
     * Constructor for Hoe with specific material
     *
     * @param material The material of the hoe
     */
    public Hoe(ToolMaterial material) {
        super(material.name() + " Hoe", getBaseSellPrice(material),
                "A " + material.name().toLowerCase() + " hoe for tilling soil.",
                ToolType.HOE, material, getEnergyConsumption(material), Skills.FARMING);
    }

    /**
     * Get the base sell price based on the material
     *
     * @param material The material of the hoe
     * @return The base sell price
     */
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

    /**
     * Get the energy consumption based on the material
     *
     * @param material The material of the hoe
     * @return The energy consumption
     */
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

    /**
     * Upgrade the hoe to the next material
     *
     * @return The upgraded hoe, or null if the hoe is already at the highest material
     */
    @Override
    public Tool upgrade() {
        switch (getMaterial()) {
            case BASIC:
                return new Hoe(ToolMaterial.COPPER);
            case COPPER:
                return new Hoe(ToolMaterial.IRON);
            case IRON:
                return new Hoe(ToolMaterial.GOLD);
            case GOLD:
                return new Hoe(ToolMaterial.IRIDIUM);
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
}