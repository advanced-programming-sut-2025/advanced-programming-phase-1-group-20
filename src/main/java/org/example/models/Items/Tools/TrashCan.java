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


    private static TrashCanType getTrashCanType(ToolMaterial material) {
        switch (material) {
            case BASIC:
                return TrashCanType.INITIAL;
            case COPPER:
                return TrashCanType.COPPER;
            case IRON:
                return TrashCanType.IRON;
            case GOLD:
                return TrashCanType.GOLD;
            case IRIDIUM:
                return TrashCanType.IRIDIUM;
            default:
                return TrashCanType.INITIAL;
        }
    }

    private static double getReturnPercentage(ToolMaterial material) {
        switch (material) {
            case BASIC:
                return 0.0;
            case COPPER:
                return 0.15;
            case IRON:
                return 0.30;
            case GOLD:
                return 0.45;
            case IRIDIUM:
                return 0.60;
            default:
                return 0.0;
        }
    }


    @Override
    public Tool upgrade() {
        switch (getMaterial()) {
            case BASIC:
                return new TrashCan(ToolMaterial.COPPER);
            case COPPER:
                return new TrashCan(ToolMaterial.IRON);
            case IRON:
                return new TrashCan(ToolMaterial.GOLD);
            case GOLD:
                return new TrashCan(ToolMaterial.IRIDIUM);
            case IRIDIUM:
                return null; // Already at highest material
            default:
                return null;
        }
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