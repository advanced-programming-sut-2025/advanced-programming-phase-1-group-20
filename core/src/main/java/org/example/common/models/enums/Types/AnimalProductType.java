package org.example.common.models.enums.Types;

import org.example.common.models.Items.Item;

public enum AnimalProductType {
    EGG("Egg", 50, "A regular egg laid by a chicken."),
    LARGE_EGG("Large Egg", 100, "A large, high-quality egg laid by a happy chicken."),

    DUCK_EGG("Duck Egg", 95, "An egg laid by a duck, larger than chicken eggs."),
    DUCK_FEATHER("Duck Feather", 250, "A soft feather occasionally dropped by ducks."),

    WOOL("Wool", 150, "Soft wool sheared from a rabbit."),
    RABBITS_FOOT("Rabbit's Foot", 1000, "A rare lucky charm from a very happy rabbit."),

    DINOSAUR_EGG("Dinosaur Egg", 350, "A rare prehistoric egg from a dinosaur."),

    MILK("Milk", 125, "Fresh milk from a cow."),
    LARGE_MILK("Large Milk", 250, "High-quality milk from a very happy cow."),

    GOAT_MILK("Goat Milk", 340, "Milk from a goat, richer than cow's milk."),
    LARGE_GOAT_MILK("Large Goat Milk", 680, "High-quality goat milk from a very happy goat."),

    SHEEP_WOOL("Wool", 340, "Wool sheared from a sheep."),
    DELUXE_WOOL("Deluxe Wool", 680, "High-quality wool from a very happy sheep."),

    TRUFFLE("Truffle", 1250, "A valuable truffle dug up by a pig.");

    private final String name;
    private final int basePrice;
    private final String description;

    AnimalProductType(String name, int basePrice, String description) {
        this.name = name;
        this.basePrice = basePrice;
        this.description = description;
    }

    public Item toItem() {
        // TODO: Add correct image file path
        return new Item(name, basePrice, "");
    }

    public String getName() {
        return name;
    }

    public int getBasePrice() {
        return basePrice;
    }

    public String getDescription() {
        return description;
    }

    public static AnimalProductType fromName(String name) {
        for (AnimalProductType type : AnimalProductType.values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
}
