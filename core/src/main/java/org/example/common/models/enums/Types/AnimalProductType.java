package org.example.common.models.enums.Types;

import org.example.common.models.Items.Item;

public enum AnimalProductType {
    EGG("Egg", 50, "A regular egg laid by a chicken." , "Egg.png"),
    LARGE_EGG("Large Egg", 100, "A large, high-quality egg laid by a happy chicken." , "Big_Egg.png"),

    DUCK_EGG("Duck Egg", 95, "An egg laid by a duck, larger than chicken eggs." , "Duck_Egg.png"),
    DUCK_FEATHER("Duck Feather", 250, "A soft feather occasionally dropped by ducks." , "Duck_Feather.png"),

    WOOL("Wool", 150, "Soft wool sheared from a rabbit." , "Wool.png"),
    RABBITS_FOOT("Rabbit's Foot", 1000, "A rare lucky charm from a very happy rabbit." , "Rabbit's_Foot.png"),

    DINOSAUR_EGG("Dinosaur Egg", 350, "A rare prehistoric egg from a dinosaur." , "Dinosaur_Egg.png"),

    MILK("Milk", 125, "Fresh milk from a cow." , "Milk.png"),
    LARGE_MILK("Large Milk", 250, "High-quality milk from a very happy cow." , "Big_Milk.png"),

    GOAT_MILK("Goat Milk", 340, "Milk from a goat, richer than cow's milk." , "Goat_Milk.png"),
    LARGE_GOAT_MILK("Large Goat Milk", 680, "High-quality goat milk from a very happy goat." , "Big_Goat_Milk.png"),

    SHEEP_WOOL("Wool", 340, "Wool sheared from a sheep." , "Wool.png"),
    DELUXE_WOOL("Deluxe Wool", 680, "High-quality wool from a very happy sheep." , "Wool.png"),

    TRUFFLE("Truffle", 1250, "A valuable truffle dug up by a pig." , "Truffle.png"),;

    private final String name;
    private final int basePrice;
    private final String description;
    private final String imageFilePath;

    AnimalProductType(String name, int basePrice, String description , String imageFilePath) {
        this.name = name;
        this.basePrice = basePrice;
        this.description = description;
        this.imageFilePath = imageFilePath;
    }

    public Item toItem() {
        return new Item(name, basePrice, "content/Animals/animal_goods/" + imageFilePath);
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
