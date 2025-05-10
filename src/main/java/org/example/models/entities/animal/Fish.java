package org.example.models.entities.animal;

import org.example.models.Items.Item;
import org.example.models.enums.Seasons;
import org.example.models.enums.Types.FishType;

import java.io.Serializable;

/**
 * Represents a fish in the game.
 * Extends the Item class, as fish are collectible items that can be stored in inventory.
 */
public class Fish extends Item implements Serializable {
    private final FishType type;
    private final int quality; // 0 = normal, 1 = silver, 2 = gold, 3 = iridium
    private final Seasons season;

    /**
     * Create a new fish with a specific quality.
     *
     * @param type    The type of fish
     * @param quality The quality of the fish (0-3)
     * @param season  The season in which the fish was caught
     */
    public Fish(FishType type, int quality, Seasons season) {
        super(type.getName(), calculatePrice(type.getBasePrice(), quality));
        this.type = type;
        this.quality = quality;
        this.season = season;
        setDescription(type.getDescription());
    }

    /**
     * Create a new fish with normal quality.
     *
     * @param type   The type of fish
     * @param season The season in which the fish was caught
     */
    public Fish(FishType type, Seasons season) {
        this(type, 0, season);
    }

    /**
     * Calculate the price of the fish based on its base price and quality.
     *
     * @param basePrice The base price of the fish
     * @param quality   The quality of the fish (0-3)
     * @return The calculated price
     */
    private static int calculatePrice(int basePrice, int quality) {
        switch (quality) {
            case 1: // Silver
                return (int) (basePrice * 1.25);
            case 2: // Gold
                return basePrice * 2;
            case 3: // Iridium
                return basePrice * 3;
            default: // Normal
                return basePrice;
        }
    }

    /**
     * Get a string representation of the fish's quality.
     *
     * @return A string representing the quality (Normal, Silver, Gold, or Iridium)
     */
    public String getQualityString() {
        switch (quality) {
            case 1:
                return "Silver";
            case 2:
                return "Gold";
            case 3:
                return "Iridium";
            default:
                return "Normal";
        }
    }

    /**
     * Get a symbol representing the fish's quality.
     *
     * @return A symbol representing the quality (★, ★★, or ★★★)
     */
    public String getQualitySymbol() {
        switch (quality) {
            case 1:
                return "★";
            case 2:
                return "★★";
            case 3:
                return "★★★";
            default:
                return "";
        }
    }

    /**
     * Display information about the fish.
     */
    @Override
    public void showInfo() {
        System.out.println("Fish: " + type.getName() + " " + getQualitySymbol());
        System.out.println("Quality: " + getQualityString());
        System.out.println("Value: " + getBaseSellPrice() + "g");
        System.out.println("Description: " + getDescription());
        System.out.println("Season: " + season);
        if (type.isLegendary()) {
            System.out.println("LEGENDARY FISH!");
        }
    }

    // Getters
    public FishType getType() {
        return type;
    }

    public int getQuality() {
        return quality;
    }

    public Seasons getSeason() {
        return season;
    }

    public boolean isLegendary() {
        return type.isLegendary();
    }
}