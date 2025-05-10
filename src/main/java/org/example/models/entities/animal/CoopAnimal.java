package org.example.models.entities.animal;

import org.example.models.Items.Item;
import org.example.models.common.Result;
import org.example.models.enums.Cages;
import org.example.models.enums.CoopAnimalTypes;

import java.io.Serializable;

/**
 * Class representing a coop animal
 * Uses the CoopAnimalTypes enum for specific behaviors
 */
public class CoopAnimal extends Animal implements Serializable {
    private final CoopAnimalTypes type;
    private int happinessLevel;
    private int daysSinceLastProduction;


    public CoopAnimal(CoopAnimalTypes type) {
        super(type.getName(), type.getPrice());
        this.type = type;
        this.happinessLevel = 50; // Default happiness
        this.daysSinceLastProduction = 0;
    }

    /**
     * Attempts to produce an item (product) from the animal
     *
     * @return Result containing success/failure and item produced
     */
    public Result produceItem() {
        if (daysSinceLastProduction < type.getProductionInterval()) {
            return Result.error("Not ready to produce yet");
        }

        daysSinceLastProduction = 0;
        return Result.success("Item produced successfully");
    }

    /**
     * Get the product from this animal if available
     *
     * @return The item produced or null if not available
     */
    public Item getProduct() {
        if (daysSinceLastProduction >= type.getProductionInterval()) {
            Item product = type.determineProduct(this);
            daysSinceLastProduction = 0;
            return product;
        }
        return null;
    }

    /**
     * Increase happiness level of the animal
     *
     * @param amount Amount to increase happiness by
     */
    public void increaseHappiness(int amount) {
        happinessLevel = Math.min(100, happinessLevel + amount);
    }

    /**
     * Decrease happiness level of the animal
     *
     * @param amount Amount to decrease happiness by
     */
    public void decreaseHappiness(int amount) {
        happinessLevel = Math.max(0, happinessLevel - amount);
    }

    /**
     * Advance the day counter
     */
    public void advanceDay() {
        daysSinceLastProduction++;
    }

    /**
     * Check if this animal can live in the given coop type
     *
     * @param coopType The coop type to check
     * @return True if the animal can live in this coop type
     */
    public boolean canLiveIn(Cages coopType) {
        // Animals can live in their minimum coop size or better
        Cages minimumCoopSize = type.getMinimumCoopSize();

        switch (minimumCoopSize) {
            case NORMAL_COOP:
                // Can live in any coop
                return true;
            case BIG_CAGE:
                // Can live in big or deluxe
                return coopType == Cages.BIG_CAGE || coopType == Cages.DELUXE_CAGE;
            case DELUXE_CAGE:
                // Can only live in deluxe
                return coopType == Cages.DELUXE_CAGE;
            default:
                return false;
        }
    }

    public CoopAnimalTypes getType() {
        return type;
    }

    public Cages getMinimumCoopSize() {
        return type.getMinimumCoopSize();
    }


    public int getProductionInterval() {
        return type.getProductionInterval();
    }

    public String getPrimaryProduct() {
        return type.getPrimaryProduct();
    }


    public int getPrimaryProductPrice() {
        return type.getPrimaryProductPrice();
    }

    public String getSecondaryProduct() {
        return type.getSecondaryProduct();
    }


    public int getSecondaryProductPrice() {
        return type.getSecondaryProductPrice();
    }

    public int getHappinessLevel() {
        return happinessLevel;
    }


    public String getDescription() {
        return type.getDescription();
    }


    public int getDaysSinceLastProduction() {
        return daysSinceLastProduction;
    }

    @Override
    public String toString() {
        return type.getName() + " (Happiness: " + happinessLevel + "%)";
    }

    public String getName() {
        return type.getName();
    }
}