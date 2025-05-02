package org.example.models.Items.Tools;

import org.example.models.Items.Tool;


public class TrashCan extends Tool {
    private TrashCanType trashCanType;
    private double returnPercentage;


    public TrashCan() {
        super("Initial Trash Can", 0, "A basic trash can for disposing of items.",
                ToolType.TRASH_CAN, ToolMaterial.BASIC, 0, null);
        this.trashCanType = TrashCanType.INITIAL;
        this.returnPercentage = 0.0;
    }


    public TrashCan(ToolMaterial material) {
        super(material.name() + " Trash Can", getBaseSellPrice(material),
                "A " + material.name().toLowerCase() + " trash can for disposing of items.",
                ToolType.TRASH_CAN, material, 0, null);
        this.trashCanType = getTrashCanType(material);
        this.returnPercentage = getReturnPercentage(material);
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


    private static TrashCanType getTrashCanType(ToolMaterial material) {
        return switch (material) {
            case BASIC -> TrashCanType.INITIAL;
            case COPPER -> TrashCanType.COPPER;
            case IRON -> TrashCanType.IRON;
            case GOLD -> TrashCanType.GOLD;
            case IRIDIUM -> TrashCanType.IRIDIUM;
        };
    }

    private static double getReturnPercentage(ToolMaterial material) {
        return switch (material) {
            case BASIC -> 0.0;
            case COPPER -> 0.15;
            case IRON -> 0.30;
            case GOLD -> 0.45;
            case IRIDIUM -> 0.60;
        };
    }


    @Override
    public Tool upgrade() {
        return switch (getMaterial()) {
            case BASIC -> new TrashCan(ToolMaterial.COPPER);
            case COPPER -> new TrashCan(ToolMaterial.IRON);
            case IRON -> new TrashCan(ToolMaterial.GOLD);
            case GOLD -> new TrashCan(ToolMaterial.IRIDIUM);
            case IRIDIUM -> null; // Already at highest material
        };
    }


    @Override
    public boolean use(String direction) {
        return false;
    }


    public TrashCanType getTrashCanType() {
        return trashCanType;
    }


    public double getReturnPercentage() {
        return returnPercentage;
    }


    public int calculateReturnValue(int itemValue) {
        return (int) (itemValue * returnPercentage);
    }


    public enum TrashCanType {
        INITIAL,
        COPPER,
        IRON,
        GOLD,
        IRIDIUM
    }
}