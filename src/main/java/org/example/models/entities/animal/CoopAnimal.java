package org.example.models.entities.animal;

import org.example.models.Items.Item;
import org.example.models.common.Result;
import org.example.models.enums.CoopAnimalTypes;
import org.example.models.enums.Types.Cages;

import java.io.Serializable;


public class CoopAnimal extends Animal implements Serializable {
    private final CoopAnimalTypes type;
    private int happinessLevel;
    private int daysSinceLastProduction;
    private boolean petToday = false;
    private boolean fedToday = false;
    private boolean isOutside = false;
    private String name;

    public CoopAnimal(CoopAnimalTypes type, String name) {
        super(name, type.getPrice());
        this.type = type;
        this.happinessLevel = 50; // Default happiness
        this.daysSinceLastProduction = 0;
    }

    public void moveInside() {
        isOutside = !isOutside;
    }

    public boolean isPetToday() {
        return petToday;
    }

    public void setPetToday(boolean petToday) {
        this.petToday = petToday;
    }

    public boolean isOutside() {
        return isOutside;
    }

    public void setOutside(boolean outside) {
        this.isOutside = outside;
    }

    public Result produceItem() {
        if (daysSinceLastProduction < type.getProductionInterval()) {
            return Result.error("Not ready to produce yet");
        }

        daysSinceLastProduction = 0;
        return Result.success("Item produced successfully");
    }

    public Item getProduct() {
        if (daysSinceLastProduction >= type.getProductionInterval()) {
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

    public void advanceDay() {
        daysSinceLastProduction++;
    }

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

    public CoopAnimalTypes getCoopType() {
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

    public void setName(String name) {
        this.name = name;
    }

    public boolean isHasBeenFed() {
        return fedToday;
    }

    public void setHasBeenFed(boolean hasBeenFed) {
        this.fedToday = hasBeenFed;
    }

    public void feed() {
        fedToday = true;
        increaseHappiness(5);
    }

    public void nextDay() {
        // Increase days since last production
        daysSinceLastProduction++;

        // Apply happiness modifiers
        if (!fedToday) {
            decreaseHappiness(20);
        }

        if (isOutside && !fedToday) {
            // If outside, can eat grass
            fedToday = true;
            increaseHappiness(8);
        }

        if (isOutside) {
            // Penalty for staying outside overnight
            decreaseHappiness(20);
        }

        if (!petToday) {
            // Penalty for not petting, scales with friendship
            int penalty = 10 * happinessLevel / 200;
            decreaseHappiness(penalty);
        }

        // Reset daily flags
        fedToday = false;
        petToday = false;
    }
}