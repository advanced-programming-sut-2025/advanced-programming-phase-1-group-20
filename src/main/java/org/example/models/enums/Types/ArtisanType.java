package org.example.models.enums.Types;

public enum ArtisanType {
    Honey("Honey", "It's a sweet syrup produced by bees.", 75, 4 * 24, "", 350, "Bee House"),
    Cheese("Cheese", "It's your basic cheese.", 100, 3, "1 Milk or Large Milk", 230, "Cheese Press"),
    GoatCheese("Goat Cheese", "Soft cheese made from goat's milk.", 100, 3, "1 Goat Milk or 1 Large Goat Milk", 400, "Cheese Press"),
    Beer("Beer", "Drink in moderation.", 50, 24, "1 Wheat", 200, "Keg"),
    Vinegar("Vinegar", "An aged fermented liquid used in many cooking recipes.", 13, 10, "1 Rice", 100, "Keg"),
    Coffee("Coffee", "It smells delicious. This is sure to give you a boost.", 75, 2, "5 Coffee Bean", 150, "Keg"),
    Juice("Juice", "A sweet, nutritious beverage.", 0, 4 * 24, "1 any Vegetable", 0, "Keg"),
    Mead("Mead", "A fermented beverage made from honey. Drink in moderation.", 100, 10, "1 Honey", 300, "Keg"),
    PaleAle("Pale Ale", "Drink in moderation.", 50, 3 * 24, "1 Hops", 300, "Keg"),
    Wine("Wine", "Drink in moderation.", 0, 7 * 24, "1 any Fruit", 0, "Keg"),
    DriedMushrooms("Dried Mushrooms", "A package of gourmet mushrooms.", 50, 24, "5 any Mushroom", 0, "Dehydrator"),
    DriedFruit("Dried Fruit", "Chewy pieces of dried fruit.", 75, 24, "5 any Fruit except Grapes", 0, "Dehydrator"),
    Raisins("Raisins", "It's said to be the Junimos' favorite food.", 125, 24, "5 Grapes", 600, "Dehydrator"),
    Coal("Coal", "A combustible rock that is useful for crafting and smelting.", 15, 1, "10 Wood", 50, "Charcoal Klin"),
    Pickles("Pickles", "A jar of your home-made pickles.", 0, 6, "1 any Vegetable", 0, "Preserves Jar"),
    Jelly("Jelly", "Gooey.", 0, 3 * 24, "1 any Fruit", 0, "Preserves Jar"),
    SmokedFish("Smoked Fish", "A whole fish, smoked to perfection.", 0, 1, "1 any Fish + Coal", 0, "Fish Smoker"),// TODO : check sell price && energy later "i need taha for this"
    IronBar("Iron bar", "Turns ore and coal into metal bars.", 0, 4, "1 Iron + Coal", 0, "Furnace"),
    CopperBar("Copper Bar", "Turns ore and coal into metal bars.", 0, 4, "1 Copper + Coal", 0, "Furnace"),
    GoldBar("Gold Bar", "Turns ore and coal into metal bars.", 0, 4, "1 Gold + Coal", 0, "Furnace"),
    IridiumBar("Iridium Bar", "Turns ore and coal into metal bars.", 0, 4, "1 Iridium + Coal", 0, "Furnace"),
    Cloth("Cloth" , "A bolt of fine wool cloth." , 0, 4 , "1 Wool" , 470 , "Loom"),
    Mayonnaise("Mayonnaise" , "It looks spreadable." , 50 , 3 , "1 Egg or 1 Large Egg" , 190,"Mayonnaise Machine"),
    DuckMayonnaise("Duck Mayonnaise" , "It's a rich, yellow mayonnaise." , 75 , 3 , "1 Duck Egg" , 37 , "Mayonnaise Machine"),
    DinosaurMayonnaise("Dinosaur Mayonnaise" , "It's thick and creamy, with a vivid green hue. It smells like grass and leather." , 125 , 3 , "1 Dinosaur Egg" , 800 , "Mayonnaise Machine"),
    TruffleOil("Truffle Oil" , "A gourmet cooking ingredient." , 38 , 6 , "1 Truffle" , 1065 , "Oil Maker"),
    Oil("Oil" , "All purpose cooking oil." , 13 , 6 , "1 Corn or 1 Sunflower Seeds or Sunflower" , 100 , "Oil Maker"),

    ;
    private final String name;
    private final String description;
    private int proccessingTime;
    private final String ingridient;
    private final String source;
    private int energy;
    private int baseSellPrice;

    ArtisanType(String name, String description, int energy, int proccessingTime, String ingridient, int baseSellPrice, String source) {
        this.name = name;
        this.description = description;
        this.energy = energy;
        this.proccessingTime = proccessingTime;
        this.ingridient = ingridient;
        this.baseSellPrice = baseSellPrice;
        this.source = source;
    }

    public static ArtisanType fromName(String name) {
        for (ArtisanType artisanType : ArtisanType.values()) {
            if (name.equals(artisanType.getName())) {
                return artisanType;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public int getProccessingTime() {
        return proccessingTime;
    }

    public String getIngridient() {
        return ingridient;
    }

    public int getBaseSellPrice() {
        return baseSellPrice;
    }

    public void setBaseSellPrice(int baseSellPrice) {
        this.baseSellPrice = baseSellPrice;
    }

    public void setProccessingTime(int proccessingTime) {
        this.proccessingTime = proccessingTime;
    }

    public String getSource() {
        return source;
    }
}
