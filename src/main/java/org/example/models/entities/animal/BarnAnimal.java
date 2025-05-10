package org.example.models.entities.animal;

import org.example.models.Items.Item;
import org.example.models.common.Result;
import org.example.models.enums.Types.BarnAnimalTypes;
import org.example.models.enums.Types.BarnTypes;

import java.io.Serializable;

public class BarnAnimal extends Animal implements Serializable {
    private final BarnAnimalTypes type;
    private int happinessLevel;
    private int daysSinceLastProduction;
    private boolean hasBeenFed;

    public BarnAnimal(BarnAnimalTypes type) {
        super(type.getName(), type.getPrice());
        this.type = type;
        this.happinessLevel = 50; // Default happiness
        this.daysSinceLastProduction = 0;
        this.hasBeenFed = false;
    }

    public Result produceItem() {
        if (daysSinceLastProduction < type.getProductionInterval()) {
            return Result.error("Not ready to produce yet");
        }

        if (!hasBeenFed) {
            return Result.error("Animal needs to be fed first");
        }

        // For some animals like sheep, we need sufficient happiness
        if (type.requiresHappiness() && happinessLevel < 70) {
            return Result.error("Animal needs to be happier to produce");
        }

        daysSinceLastProduction = 0;
        return Result.success("Item produced successfully");
    }

    public Item getProduct() {
        if (daysSinceLastProduction >= type.getProductionInterval() && hasBeenFed) {
            // For animals that need happiness check
            if (type.requiresHappiness() && happinessLevel < 70) {
                return null;
            }

            Item product = type.determineProduct(this);
            daysSinceLastProduction = 0;
            return product;
        }
        return null;
    }

    public void increaseHappiness(int amount) {
        happinessLevel = Math.min(100, happinessLevel + amount);
    }

    public void decreaseHappiness(int amount) {
        happinessLevel = Math.max(0, happinessLevel - amount);
    }

    public void feed() {
        this.hasBeenFed = true;
    }

    public void advanceDay() {
        daysSinceLastProduction++;

        // Reset feeding status for the next day
        if (daysSinceLastProduction % type.getProductionInterval() == 0) {
            hasBeenFed = false;
        }
    }

    public boolean canLiveIn(BarnTypes barnType) {
        // Animals can live in their minimum barn size or better
        BarnTypes minimumBarnType = type.getMinimumBarnType();

        switch (minimumBarnType) {
            case NORMAL_BARN:
                // Can live in any barn
                return true;
            case BIG_BARN:
                // Can live in big or deluxe
                return barnType == BarnTypes.BIG_BARN || barnType == BarnTypes.DELUXE_BARN;
            case DELUXE_BARN:
                // Can only live in deluxe
                return barnType == BarnTypes.DELUXE_BARN;
            default:
                return false;
        }
    }

    public BarnAnimalTypes getType() {
        return type;
    }

    public BarnTypes getMinimumBarnType() {
        return type.getMinimumBarnType();
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

    public boolean isHasBeenFed() {
        return hasBeenFed;
    }

    @Override
    public String toString() {
        return type.getName() + " (Happiness: " + happinessLevel + "%)";
    }

    public String getName() {
        return type.getName();
    }
}