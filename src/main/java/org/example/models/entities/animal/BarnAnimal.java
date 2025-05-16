package org.example.models.entities.animal;

import org.example.models.Items.Item;
import org.example.models.enums.Types.BarnAnimalTypes;
import org.example.models.enums.Types.BarnTypes;
import org.example.models.enums.Types.Quality;

import java.io.Serializable;
import java.util.Random;

public class BarnAnimal extends Animal implements Serializable {
    private final BarnAnimalTypes type;
    private int happinessLevel;
    private int daysSinceLastProduction;
    private boolean hasBeenFed;
    private boolean hasBeenPetToday;
    private boolean isOutside;
    private Random random = new Random();

    public BarnAnimal(BarnAnimalTypes type, String name) {
        super(name, type.getPrice());
        this.type = type;
        this.happinessLevel = 50; // Default happiness
        this.daysSinceLastProduction = 0;
        this.hasBeenFed = false;
        this.hasBeenPetToday = false;
        this.isOutside = false;
    }


    public boolean canProduce() {
        return daysSinceLastProduction >= type.getProductionInterval() && hasBeenFed;
    }

    public Item getProduct() {
        if (!canProduce()) {
            return null;
        }

        // Determine if animal produces special product
        boolean producesSpecial = false;
        if (happinessLevel >= 100 && type.getSecondaryProduct() != null) {
            // Calculate chance based on happiness
            double chance = 1500.0 / (happinessLevel + (150 * (0.5 + random.nextDouble())));
            producesSpecial = random.nextDouble() < chance;
        }

        // Get primary or secondary product
        String productName;
        int productPrice;

        if (producesSpecial) {
            productName = type.getSecondaryProduct();
            productPrice = type.getSecondaryProductPrice();
        } else {
            productName = type.getPrimaryProduct();
            productPrice = type.getPrimaryProductPrice();
        }

        // Determine quality based on happiness
        double qualityValue = (happinessLevel / 1000.0) * (0.5 + 0.5 * random.nextDouble());
        Quality quality;
        double priceMultiplier;

        if (qualityValue >= 0.9) {
            quality = Quality.Iridium;
            priceMultiplier = 2.0;
        } else if (qualityValue >= 0.7) {
            quality = Quality.Golden;
            priceMultiplier = 1.5;
        } else if (qualityValue >= 0.5) {
            quality = Quality.Silver;
            priceMultiplier = 1.25;
        } else {
            quality = Quality.Normal;
            priceMultiplier = 1.0;
        }

        // Create the product
        Item product = new Item(productName, (int) (productPrice * priceMultiplier));
        product.setQuality(quality);

        // Reset production counter
        daysSinceLastProduction = 0;

        return product;
    }


    public void increaseHappiness(int amount) {
        happinessLevel = Math.min(1000, happinessLevel + amount);
    }


    public void decreaseHappiness(int amount) {
        happinessLevel = Math.max(0, happinessLevel - amount);
    }


    public void feed() {
        this.hasBeenFed = true;
        increaseHappiness(5);
    }


    public void nextDay() {
        // Increase days since last production
        daysSinceLastProduction++;

        // Apply happiness modifiers
        if (!hasBeenFed) {
            decreaseHappiness(20);
        }

        if (isOutside && !hasBeenFed) {
            // If outside, can eat grass
            hasBeenFed = true;
            increaseHappiness(8);
        }

        if (isOutside) {
            // Penalty for staying outside overnight
            decreaseHappiness(20);
        }

        if (!hasBeenPetToday) {
            // Penalty for not petting, scales with friendship
            int penalty = 10 * happinessLevel / 200;
            decreaseHappiness(penalty);
        }

        // Reset daily flags
        hasBeenFed = false;
        hasBeenPetToday = false;
    }


    public void pet() {
        hasBeenPetToday = true;
        increaseHappiness(15);
    }

    public void moveOutside() {
        isOutside = true;
    }

    public void moveInside() {
        isOutside = false;
    }

    // Getters and Setters
    public BarnAnimalTypes getType() {
        return type;
    }

    public int getHappinessLevel() {
        return happinessLevel;
    }

    public int getDaysSinceLastProduction() {
        return daysSinceLastProduction;
    }

    public boolean isHasBeenFed() {
        return hasBeenFed;
    }

    public void setHasBeenFed(boolean hasBeenFed) {
        this.hasBeenFed = hasBeenFed;
    }

    public boolean isHasBeenPetToday() {
        return hasBeenPetToday;
    }

    public void setHasBeenPetToday(boolean hasBeenPetToday) {
        this.hasBeenPetToday = hasBeenPetToday;
    }

    public boolean isOutside() {
        return isOutside;
    }

    public void setOutside(boolean outside) {
        isOutside = outside;
    }

    @Override
    public String toString() {
        return getName() + " (" + type.getName() + ") - Happiness: " + happinessLevel;
    }

    public boolean isPetToday() {
        return hasBeenPetToday;
    }

    public void setPetToday(boolean petToday) {
        this.hasBeenPetToday = petToday;
    }

    public boolean hasBeenFedToday() {
        return hasBeenFed;
    }

    public boolean canLiveIn(BarnTypes barnType) {
        // Animals can live in their minimum coop size or better
        BarnTypes minimumBarnSize = type.getMinimumBarnType();

        switch (minimumBarnSize) {
            case NORMAL_BARN:
                // Can live in any coop
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

    public void advanceDay() {
        
    }
}