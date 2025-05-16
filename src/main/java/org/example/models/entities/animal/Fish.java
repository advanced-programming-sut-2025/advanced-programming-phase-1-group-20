package org.example.models.entities.animal;

import org.example.models.Items.Item;
import org.example.models.enums.Seasons;
import org.example.models.enums.Types.FishType;
import org.example.models.enums.Types.Quality;

import java.io.Serializable;


public class Fish extends Item implements Serializable {
    private final FishType type;
    private final int q; // 0 = normal, 1 = silver, 2 = gold, 3 = iridium
    private final Seasons season;

    public Fish(FishType type, int q, Seasons season) {
        super(type.getName(), calculatePrice(type.getBasePrice(), q));
        this.type = type;
        this.q = q;
        this.season = season;
        setDescription(type.getDescription());
        setQualityViaQ(q);
    }

    public Fish(FishType type, Seasons season) {
        this(type, 0, season);
    }

    private static int calculatePrice(int basePrice, int quality) {
        return switch (quality) {
            case 1 -> // Silver
                    (int) (basePrice * 1.25);
            case 2 -> // Gold
                    basePrice * 2;
            case 3 -> // Iridium
                    basePrice * 3;
            default -> // Normal
                    basePrice;
        };
    }

    private void setQualityViaQ(int q) {
        if (q == 0) {
            setQuality(Quality.Normal);
        } else if (q == 1) {
            setQuality(Quality.Silver);
        } else if (q == 2) {
            setQuality(Quality.Golden);
        } else if (q == 3) {
            setQuality(Quality.Iridium);
        }
    }

    public String getQualityString() {
        return switch (q) {
            case 1 -> "Silver";
            case 2 -> "Gold";
            case 3 -> "Iridium";
            default -> "Normal";
        };
    }

    /**
     * Get a symbol representing the fish's quality.
     *
     * @return A symbol representing the quality (★, ★★, or ★★★)
     */
    public String getQualitySymbol() {
        switch (q) {
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


    public String getInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Fish: ").append(type.getName()).append(" ").append(getQualitySymbol());
        if (type.isLegendary()) {
            info.append(" (**legendary**)");
        }
        info.append("\n");
        info.append("   Quality: ").append(getQualityString()).append("\n");
        info.append("   Value: ").append(getBaseSellPrice()).append("g").append("\n");
        info.append("   Description: ").append(getDescription()).append("\n");
        info.append("   Season: ").append(season);

        return info.toString();
    }

    // Getters
    public FishType getType() {
        return type;
    }

    public int getQ() {
        return q;
    }

    public Seasons getSeason() {
        return season;
    }

    public boolean isLegendary() {
        return type.isLegendary();
    }
}