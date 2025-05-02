package org.example.models.Items.Tools;

import org.example.models.Items.Tool;
import org.example.models.enums.PlayerEnums.Skills;

public class FishingRod extends Tool {
    private FishingRodType rodType;

    public FishingRod() {
        super("Training Rod", 25, "A basic fishing rod for beginners.",
                ToolType.FISHING_ROD, ToolMaterial.BASIC, 8, Skills.FISHING);
        this.rodType = FishingRodType.TRAINING;
    }


    public FishingRod(FishingRodType rodType) {
        super(getRodName(rodType), getRodPrice(rodType), getRodDescription(rodType),
                ToolType.FISHING_ROD, ToolMaterial.BASIC, getEnergyConsumption(rodType), Skills.FISHING);
        this.rodType = rodType;
    }


    private static String getRodName(FishingRodType rodType) {
        return switch (rodType) {
            case TRAINING -> "Training Rod";
            case BAMBOO -> "Bamboo Pole";
            case FIBERGLASS -> "Fiberglass Rod";
            case IRIDIUM -> "Iridium Rod";
        };
    }

    private static int getRodPrice(FishingRodType rodType) {
        return switch (rodType) {
            case TRAINING -> 25;
            case BAMBOO -> 500;
            case FIBERGLASS -> 1800;
            case IRIDIUM -> 7500;
        };
    }


    private static String getRodDescription(FishingRodType rodType) {
        return switch (rodType) {
            case TRAINING -> "A basic fishing rod for beginners. Can only catch the cheapest fish of each season.";
            case BAMBOO -> "A bamboo fishing pole. Can catch any fish.";
            case FIBERGLASS ->
                    "A fiberglass fishing rod. Can catch any fish and uses less energy than the bamboo pole.";
            case IRIDIUM -> "An iridium fishing rod. Can catch any fish and uses the least amount of energy.";
        };
    }


    private static int getEnergyConsumption(FishingRodType rodType) {
        switch (rodType) {
            case TRAINING:
                return 8;
            case BAMBOO:
                return 8;
            case FIBERGLASS:
                return 6;
            case IRIDIUM:
                return 4;
            default:
                return 8;
        }
    }


    @Override
    public Tool upgrade() {
        switch (rodType) {
            case TRAINING:
                return new FishingRod(FishingRodType.BAMBOO);
            case BAMBOO:
                return new FishingRod(FishingRodType.FIBERGLASS);
            case FIBERGLASS:
                return new FishingRod(FishingRodType.IRIDIUM);
            case IRIDIUM:
                return null; // Already at highest type
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


    public FishingRodType getRodType() {
        return rodType;
    }

    public boolean canCatchFish(String fishName, int fishValue, String season) {
        // Training rod can only catch the least expensive fish of each season
        if (rodType == FishingRodType.TRAINING) {
            // Implementation will depend on the game mechanics
            // For now, just return true for simplicity
            return true;
        }

        // Other rods can catch any fish
        return true;
    }

    public enum FishingRodType {
        TRAINING,
        BAMBOO,
        FIBERGLASS,
        IRIDIUM
    }
}