package org.example.models.enums.Types;

import org.example.models.Items.Item;
import org.example.models.entities.animal.BarnAnimal;

import java.util.Random;

public enum BarnAnimalTypes {
    COW("Cow", 1500, "Milk", 125, "Large Milk", 250, BarnTypes.NORMAL_BARN, 1, "A friendly cow that produces milk daily."),
    GOAT("Goat", 4000, "Goat Milk", 340, "Large Goat Milk", 680, BarnTypes.BIG_BARN, 2, "Produces goat milk every other day."),
    SHEEP("Sheep", 8000, "Wool", 340, "Deluxe Wool", 680, BarnTypes.BIG_BARN, 3, "Produces wool every 3 days if sheared with shears."),
    PIG("Pig", 16000, "Truffle", 1250, null, 0, BarnTypes.DELUXE_BARN, 5, "Finds truffles outside on non-rainy days.");

    private final String name;
    private final int price;
    private final String primaryProduct;
    private final int primaryProductPrice;
    private final String secondaryProduct;
    private final int secondaryProductPrice;
    private final BarnTypes minimumBarnType;
    private final int productionInterval;
    private final String description;

    BarnAnimalTypes(String name, int price, String primaryProduct, int primaryProductPrice,
                    String secondaryProduct, int secondaryProductPrice, BarnTypes minimumBarnType,
                    int productionInterval, String description) {
        this.name = name;
        this.price = price;
        this.primaryProduct = primaryProduct;
        this.primaryProductPrice = primaryProductPrice;
        this.secondaryProduct = secondaryProduct;
        this.secondaryProductPrice = secondaryProductPrice;
        this.minimumBarnType = minimumBarnType;
        this.productionInterval = productionInterval;
        this.description = description;
    }

    public Item determineProduct(BarnAnimal animal) {
        Random random = new Random();

        if (secondaryProduct != null && animal.getHappinessLevel() >= 100) {
            double chance = 1500.0 / (animal.getHappinessLevel() + (150 * (0.5 + random.nextDouble())));

            if (random.nextDouble() < chance) {
                return new Item(secondaryProduct, secondaryProductPrice);
            }
        }

        return new Item(primaryProduct, primaryProductPrice);
    }

    public boolean requiresHappiness() {
        return this == PIG;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getPrimaryProduct() {
        return primaryProduct;
    }

    public int getPrimaryProductPrice() {
        return primaryProductPrice;
    }

    public String getSecondaryProduct() {
        return secondaryProduct;
    }

    public int getSecondaryProductPrice() {
        return secondaryProductPrice;
    }

    public BarnTypes getMinimumBarnType() {
        return minimumBarnType;
    }

    public int getProductionInterval() {
        return productionInterval;
    }

    public String getDescription() {
        return description;
    }
}