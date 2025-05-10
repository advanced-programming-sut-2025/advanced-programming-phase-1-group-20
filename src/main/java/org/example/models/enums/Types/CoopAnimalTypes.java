package org.example.models.enums;

import org.example.models.Items.Item;
import org.example.models.entities.animal.CoopAnimal;
import org.example.models.enums.Types.Cages;

import java.util.Random;


public enum CoopAnimalTypes {
    CHICKEN(
            "Chicken",
            800,
            1, // Every day
            Cages.NORMAL_COOP,
            "Egg", 50, // Primary product
            "Large Egg", 95, // Secondary product
            "Lives in a COOP which has a capacity of 4. Chickens produce a product every morning if they are well taken care of. Note that chickens can also live in upgraded coops.",
            (animal) -> {
                // Happiness directly correlates to chance of large egg
                // At 100 happiness, 50% chance of large egg
                // At 0 happiness, 0% chance of large egg
                Random random = new Random();
                int chance = random.nextInt(100);
                boolean produceLargeEgg = chance < (animal.getHappinessLevel() / 2);

                if (produceLargeEgg) {
                    return new Item(animal.getSecondaryProduct(), animal.getSecondaryProductPrice(), "A high-quality large egg");
                } else {
                    return new Item(animal.getPrimaryProduct(), animal.getPrimaryProductPrice(), "A regular egg");
                }
            }
    ),

    DUCK(
            "Duck",
            1200,
            2, // Every 2 days
            Cages.BIG_CAGE,
            "Duck Egg", 95, // Primary product
            "Duck Feather", 250, // Secondary product
            "Requires a BIG COOP to live in, which has a capacity of 8. Ducks produce a product every 2 days. Happier ducks are more likely to produce feathers. Note that ducks can also live in DELUXE COOP.",
            (animal) -> {
                // Higher happiness increases chance of feather
                // At 100 happiness, 40% chance of feather
                // At 0 happiness, 0% chance of feather
                Random random = new Random();
                int chance = random.nextInt(100);
                boolean produceFeather = chance < (animal.getHappinessLevel() * 0.4);

                if (produceFeather) {
                    return new Item(animal.getSecondaryProduct(), animal.getSecondaryProductPrice(), "A beautiful duck feather");
                } else {
                    return new Item(animal.getPrimaryProduct(), animal.getPrimaryProductPrice(), "A nutritious duck egg");
                }
            }
    ),

    RABBIT(
            "Rabbit",
            8000,
            4, // Every 4 days
            Cages.DELUXE_CAGE,
            "Wool", 340, // Primary product
            "Rabbit's Foot", 565, // Secondary product
            "Requires a DELUXE COOP to live in, which has a capacity of 12. Rabbits produce a product every 4 days. Rabbit happiness increases the chance that its product will be a rabbit's foot.",
            (animal) -> {
                // Higher happiness increases chance of rabbit's foot
                // At 100 happiness, 25% chance of rabbit's foot
                // At 0 happiness, 0% chance of rabbit's foot
                Random random = new Random();
                int chance = random.nextInt(100);
                boolean produceRabbitFoot = chance < (animal.getHappinessLevel() * 0.25);

                if (produceRabbitFoot) {
                    return new Item(animal.getSecondaryProduct(), animal.getSecondaryProductPrice(), "A lucky rabbit's foot");
                } else {
                    return new Item(animal.getPrimaryProduct(), animal.getPrimaryProductPrice(), "Soft rabbit wool");
                }
            }
    ),

    DINOSAUR(
            "Dinosaur",
            14000,
            7, // Every 7 days
            Cages.BIG_CAGE,
            "Dinosaur Egg", 350, // Primary product
            null, 0, // No secondary product
            "Requires a BIG COOP to live in, which has a capacity of 8. Dinosaurs produce an egg every 7 days.",
            (animal) -> {
                // Dinosaurs only produce dinosaur eggs
                return new Item(animal.getPrimaryProduct(), animal.getPrimaryProductPrice(), "A rare dinosaur egg");
            }
    );

    private final String name;
    private final int price;
    private final int productionInterval;
    private final Cages minimumCoopSize;
    private final String primaryProduct;
    private final int primaryProductPrice;
    private final String secondaryProduct;
    private final int secondaryProductPrice;
    private final String description;
    private final ProductionFunction productionFunction;

    CoopAnimalTypes(String name, int price, int productionInterval,
                    Cages minimumCoopSize, String primaryProduct, int primaryProductPrice,
                    String secondaryProduct, int secondaryProductPrice, String description,
                    ProductionFunction productionFunction) {
        this.name = name;
        this.price = price;
        this.productionInterval = productionInterval;
        this.minimumCoopSize = minimumCoopSize;
        this.primaryProduct = primaryProduct;
        this.primaryProductPrice = primaryProductPrice;
        this.secondaryProduct = secondaryProduct;
        this.secondaryProductPrice = secondaryProductPrice;
        this.description = description;
        this.productionFunction = productionFunction;
    }


    public CoopAnimal createAnimal() {
        return new CoopAnimal(this);
    }


    public Item determineProduct(CoopAnimal animal) {
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

    public Cages getMinimumCoopSize() {
        return minimumCoopSize;
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


    @FunctionalInterface
    public interface ProductionFunction {
        Item produceItem(CoopAnimal animal);
    }
}