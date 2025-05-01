package org.example.models.enums.Types;

public enum ArtisanType {
    Honey("Honey" , "It's a sweet syrup produced by bees." , 75 , 4 * 24 , "" , 350),
    Cheese("Cheese" , "It's your basic cheese." , 100 , 3 , "1 Milk or Large Milk" , 230),
    GoatCheese("Goat Cheese" , "Soft cheese made from goat's milk.",100,3,"1 Goat Milk or 1 Large Goat Milk" , 400),
    Beer("Beer", "Drink in moderation.", 50, 24, "1 Wheat", 200),
    Vinegar("Vinegar", "An aged fermented liquid used in many cooking recipes.", 13, 10, "1 Rice", 100),
    Coffee("Coffee", "It smells delicious. This is sure to give you a boost.", 75, 2, "5 Coffee Bean", 150),
    Juice("Juice", "A sweet, nutritious beverage.", 0 , 4 * 24, "1 any Vegetable", 0), // TODO : check sell price && energy later
    Mead("Mead", "A fermented beverage made from honey. Drink in moderation.", 100, 10, "1 Honey", 300),
    PaleAle("Pale Ale", "Drink in moderation.", 50, 3 * 24, "1 Hops", 300),
    Wine("Wine", "Drink in moderation.", 0 , 7 * 24, "1 any Fruit", 0), // TODO : check sell price && energy later
    DriedMushrooms("Dried Mushrooms", "A package of gourmet mushrooms.", 50, 24, "5 any Mushroom", 0), // TODO : check sell price
    DriedFruit("Dried Fruit", "Chewy pieces of dried fruit.", 75, 24, "5 any Fruit except Grapes", 0), // TODO : check sell price
    Raisins("Raisins", "It's said to be the Junimos' favorite food.", 125, 24, "5 Grapes", 600),
    Coal("Coal", "A combustible rock that is useful for crafting and smelting.", 15, 1, "10 Wood", 50),
    Pickles("Pickles", "A jar of your home-made pickles.", 0, 6, "1 any Vegetable", 0), // TODO : check sell price && energy later
    Jelly("Jelly", "Gooey.", 0 , 3 * 24, "1 any Fruit",  0 ), // TODO : check sell price && energy later
    SmokedFish("Smoked Fish", "A whole fish, smoked to perfection.", 0 , 1, "1 any Fish + Coal", 0 ),// TODO : check sell price && energy later
    IronBar("Iron bar" , "Turns ore and coal into metal bars." , 0 , 4 , "1 any Iron + Coal", 0), // TODO : check sell price
    CopperBar("Copper Bar", "Turns ore and coal into metal bars.", 0, 4, "1 any Copper + Coal", 0), // TODO: check sell price
    GoldBar("Gold Bar", "Turns ore and coal into metal bars.", 0, 4, "1 any Gold + Coal", 0), // TODO: check sell price
    IridiumBar("Iridium Bar", "Turns ore and coal into metal bars.", 0, 4, "1 any Iridium + Coal", 0), // TODO: check sell price
    ;
    private final String name;
    private final String description;
    private int energy;
    private final int proccessingTime;
    private final String ingridient;
    private int baseSellPrice;
    ArtisanType(String name , String description, int energy, int proccessingTime, String ingridient, int baseSellPrice) {
        this.name = name;
        this.description = description;
        this.energy = energy;
        this.proccessingTime = proccessingTime;
        this.ingridient = ingridient;
        this.baseSellPrice = baseSellPrice;
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
}
