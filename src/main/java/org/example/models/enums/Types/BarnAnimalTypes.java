package org.example.models.enums.Types;

import org.example.models.Items.Item;
import org.example.models.entities.animal.BarnAnimal;

import java.util.Random;

public enum BarnAnimalTypes {
    COW(
            "Cow",
            15000,
            1, // Daily production
            BarnTypes.NORMAL_BARN,
            "Milk", 125,
            "Large Milk", 190,
            "Cows live in a barn and produce milk every day. You need a Milk Pail to collect it.",
            false, // Doesn't require happiness check
            (animal) -> {
                Random random = new Random();
                int chance = random.nextInt(100);
                boolean produceLargeMilk = chance < (animal.getHappinessLevel() / 2);

                if (produceLargeMilk) {
                    return new Item(animal.getSecondaryProduct(), animal.getSecondaryProductPrice(), "A high-quality large milk");
                } else {
                    return new Item(animal.getPrimaryProduct(), animal.getPrimaryProductPrice(), "A regular milk");
                }
            }
    ),

    GOAT(
            "Goat",
            4000,
            2, // Every 2 days
            BarnTypes.BIG_BARN,
            "Goat Milk", 225,
            "Large Goat Milk", 345,
            "Goats produce milk every 2 days when fed. You need a Milk Pail to collect it.",
            false, // Doesn't require happiness check
            (animal) -> {
                Random random = new Random();
                int chance = random.nextInt(100);
                boolean produceLargeMilk = chance < (animal.getHappinessLevel() / 2);

                if (produceLargeMilk) {
                    return new Item(animal.getSecondaryProduct(), animal.getSecondaryProductPrice(), "A high-quality large goat milk");
                } else {
                    return new Item(animal.getPrimaryProduct(), animal.getPrimaryProductPrice(), "A regular goat milk");
                }
            }
    ),

    SHEEP(
            "Sheep",
            8000,
            3, // Every 3 days
            BarnTypes.DELUXE_BARN,
            "Wool", 340,
            null, 0, // No secondary product
            "Sheep produce wool every 3 days when fed and if their happiness is at least 70%. You need Shears to collect it.",
            true, // Requires happiness check
            (animal) -> {
                // Sheep only produce one type of wool
                return new Item(animal.getPrimaryProduct(), animal.getPrimaryProductPrice(), "Soft sheep wool");
            }
    ),

    PIG(
            "Pig",
            16000,
            1, // Daily in non-winter seasons
            BarnTypes.DELUXE_BARN,
            "Truffle", 625,
            null, 0, // No secondary product
            "Pigs find truffles when they leave the barn. They don't produce during winter when they stay inside.",
            false, // Doesn't require happiness check
            (animal) -> {
                // Check if it's winter - would need to be implemented in a full system
                // For now, just return the truffle
                return new Item(animal.getPrimaryProduct(), animal.getPrimaryProductPrice(), "A rare and valuable truffle");
            }
    );

    private final String name;
    private final int price;
    private final int productionInterval;
    private final BarnTypes minimumBarnType;
    private final String primaryProduct;
    private final int primaryProductPrice;
    private final String secondaryProduct;
    private final int secondaryProductPrice;
    private final String description;
    private final boolean requiresHappiness;
    private final ProductionFunction productionFunction;

    BarnAnimalTypes(
            String name,
            int price,
            int productionInterval,
            BarnTypes minimumBarnType,
            String primaryProduct,
            int primaryProductPrice,
            String secondaryProduct,
            int secondaryProductPrice,
            String description,
            boolean requiresHappiness,
            ProductionFunction productionFunction) {
        this.name = name;
        this.price = price;
        this.productionInterval = productionInterval;
        this.minimumBarnType = minimumBarnType;
        this.primaryProduct = primaryProduct;
        this.primaryProductPrice = primaryProductPrice;
        this.secondaryProduct = secondaryProduct;
        this.secondaryProductPrice = secondaryProductPrice;
        this.description = description;
        this.requiresHappiness = requiresHappiness;
        this.productionFunction = productionFunction;
    }

    public static BarnAnimalTypes fromName(String name) {
        for (BarnAnimalTypes type : BarnAnimalTypes.values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

    public BarnAnimal createAnimal() {
        return new BarnAnimal(this);
    }

    public Item determineProduct(BarnAnimal animal) {
        return productionFunction.produceItem(animal);
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getProductionInterval() {
        return productionInterval;
    }

    public BarnTypes getMinimumBarnType() {
        return minimumBarnType;
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

    public String getDescription() {
        return description;
    }

    public boolean requiresHappiness() {
        return requiresHappiness;
    }

    @FunctionalInterface
    public interface ProductionFunction {
        Item produceItem(BarnAnimal animal);
    }
}