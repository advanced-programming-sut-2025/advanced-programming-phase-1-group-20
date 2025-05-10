package org.example.models.enums.Types;

import org.example.models.Items.Item;
import org.example.models.entities.animal.BarnAnimal;

import java.util.Random;

public enum BarnAnimalTypes {

    COW(
            "Cow",
            15000,
            1,
            "Milk", 125,
            "Big Milk", 190,
            "Cow lives in a barn, and aqupies a capacoty of 4, produces milk every day",
            (animal) -> {
                Random random = new Random();
                int chance = random.nextInt(100);
                boolean produceLargeMilk = chance < (animal.getHappiness() / 2);

                if (produceLargeMilk) {
                    return new Item(animal.getSecondaryProduct(), animal.getSecondaryProductPrice(), "A high-quality large egg");
                } else {
                    return new Item(animal.getPrimaryProduct(), animal.getPrimaryProductPrice(), "A regular egg");
                }
            }
    ),
    GOAT(
            "Goat",
            4500,
            2,
            "Goat Milk",
    );
    private final String name;
    private final int price;
    private final int productionInterval;
    private final String primaryProduct;
    private final int primaryProductPrice;
    private final String secondaryProduct;
    private final int secondaryProductPrice;
    private final String description;
    private final ProductionFunction productionFunction;

    BarnAnimalTypes(String name,
                    int price, int productionInterval,
                    String primaryProduct, int primaryProductPrice,
                    String secondaryProduct, int secondaryProductPrice,
                    String description, ProductionFunction productionFunction) {
        this.name = name;
        this.price = price;
        this.productionInterval = productionInterval;
        this.primaryProduct = primaryProduct;
        this.primaryProductPrice = primaryProductPrice;
        this.secondaryProduct = secondaryProduct;
        this.secondaryProductPrice = secondaryProductPrice;
        this.description = description;
        this.productionFunction = productionFunction;
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
        Item produceItem(BarnAnimal animal);
    }
}
