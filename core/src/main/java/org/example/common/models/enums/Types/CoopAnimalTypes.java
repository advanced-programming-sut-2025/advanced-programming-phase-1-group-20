package org.example.common.models.enums.Types;

import org.example.common.models.Items.Item;
import org.example.common.models.entities.animal.CoopAnimal;

import java.util.Random;

public enum CoopAnimalTypes {
    CHICKEN("Chicken",
            800,
            "Egg", 50,
            "Large Egg", 95,
            Cages.NORMAL_COOP, 1,
            "A friendly chicken that lays eggs daily."),
    DUCK("Duck",
            1200,
            "Duck Egg", 95,
            "Duck Feather", 250,
            Cages.BIG_CAGE, 2,
            "Produces duck eggs every other day. May occasionally drop feathers."),
    RABBIT("Rabbit",
        8000,
        "Wool", 340,
        "Rabbit's Foot", 565,
        Cages.DELUXE_CAGE, 4,
        "Produces wool every 4 days. May rarely produce a rabbit's foot."),
    DINOSAUR("Dinosaur",
        14000,
        "Dinosaur Egg", 350,
        null, 0,
        Cages.BIG_CAGE, 7,
        "A rare dinosaur that lays prehistoric eggs once a week.");

    private final String name;
    private final int price;
    private final String primaryProduct;
    private final int primaryProductPrice;
    private final String secondaryProduct;
    private final int secondaryProductPrice;
    private final Cages minimumCoopSize;
    private final int productionInterval;
    private final String description;

    CoopAnimalTypes(String name, int price, String primaryProduct, int primaryProductPrice,
                    String secondaryProduct, int secondaryProductPrice, Cages minimumCoopSize,
                    int productionInterval, String description) {
        this.name = name;
        this.price = price;
        this.primaryProduct = primaryProduct;
        this.primaryProductPrice = primaryProductPrice;
        this.secondaryProduct = secondaryProduct;
        this.secondaryProductPrice = secondaryProductPrice;
        this.minimumCoopSize = minimumCoopSize;
        this.productionInterval = productionInterval;
        this.description = description;
    }


    public Item determineProduct(CoopAnimal animal) {
        Random random = new Random();

        // Check if animal has enough happiness for secondary product
        if (secondaryProduct != null && animal.getHappinessLevel() >= 100) {
            // Calculate chance of secondary product
            double chance = 1500.0 / (animal.getHappinessLevel() + (150 * (0.5 + random.nextDouble())));

            if (random.nextDouble() < chance) {
                //TODO : adding correct image file path
                return new Item(secondaryProduct, secondaryProductPrice , "");
            }
        }

        // Otherwise return primary product
        //TODO : adding correct image file path
        return new Item(primaryProduct, primaryProductPrice , "");
    }

    // Getters
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

    public Cages getMinimumCoopSize() {
        return minimumCoopSize;
    }

    public int getProductionInterval() {
        return productionInterval;
    }

    public String getDescription() {
        return description;
    }

    public static CoopAnimalTypes fromName(String name) {
        for (CoopAnimalTypes type : CoopAnimalTypes.values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
}
