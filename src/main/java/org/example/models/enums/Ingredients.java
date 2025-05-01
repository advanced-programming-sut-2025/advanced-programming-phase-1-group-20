package org.example.models.enums;

import org.example.models.Items.Item;

public enum Ingredients {
    FriedEgg("1 egg"),
    BakedFish("1 Sardine + 1 Salmon + 1 wheat"),
    Salad("1 leek + 1 dandelion"),
    Olmelet("1 egg + 1 milk"),
    pumpkinpie("1 pumpking + 1 wheat flour + " +
            " 1 milk + 1 sugar"),
    spaghetti("1 wheat flour + 1 tomato"),
    pizza("1 wheat flour + 1 tomato + " +
            " 1 cheese"),
    Tortilla("1 corn"),
    MakiRoll("1 any fish + 1 rice + 1 fiber"),
    TripleShotEspresso("3 coffee"),
    Cookie("1 wheat flour + 1 sugar + 1 egg"),
    hashbrowns("1 potato + 1 oil"),
    pancakes("1 wheat flour + 1 egg"),
    fruitsalad("1 blueberry + 1 melon + 1 apricot"),
    redplate("1 red cabbage + 1 radish "),
    bread("1 wheat flour"),
    salmondinner("1 salmon + 1 Amaranth + 1 Kale"),
    vegetablemedley("1 tomato + 1 beet"),
    farmerslunch("1 omelet + 1 parsnip"),
    survivalburger("1 bread + 1 carrot + 1 eggplant"),
    dishOtheSea("2 sardines + 1 hash browns"),
    seaformPudding("1 Flounder + 1 midnight carp "),
    minerstreat("2 carrot + 1 suger +. 1 milk"),
    Honey(""),
    Cheese("1 Milk"),
    LargeCheese("1 Large Milk"),
    GoatCheese("1 Goat Milk "),
    LargeGoatCheese("1 Large Goat Milk"),
    Beer("1 Wheat"),
    Vinegar("1 Vinegar"),
    Coffee("5 Coffee Been"),
    Juice("1 Any Vegetable"),
    Mead("1 Honey"),
    PaleAle("1 Hops"),
    Wine("1 Any Fruit"),
    DriedMushrooms("5 Any Mushroom"),
    DriedFruit("5 of Any Fruit " +
            "(except Grapes)"),
    Raisins("5 Grapes"),
    Coal("10 Wood"),
    Cloth("1 Wool"),
    Mayonnaise("1 Egg or" +
            " 1 Large Egg"),
    DuckMayonnaise("1 Duck Egg"),
    DinosaurMayonnaise("1 Dinosaur Egg"),
    TruffleOil("1 Truffle"),
    Oil("Corn or " +
            "Sunflower Seeds or " +
            "Sunflower"),
    Pickles("1 Any vegetable"),
    Jelly("1 Any fruit"),
    SmokedFish("1 Any Fish + " +
            "1 Coal"),
    IronBar("5 Iron Ore + " +
            "1 Coal"),
    GoldBar("5 Gold Ore + " +
            "1 Coal"),
    CopperBar("5 Copper Ore + " +
            "1 Coal"),
    IridiumBar("5 Iridium Ore + " +
            "1 Coal"),
    DiamondBar("5 Diamond Ore + " +
            "1 Coal"),
    ;
    private final String ingredients;

    Ingredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public Item[] getIngridents() {
        //implementation  later:
        return new Item[]{};
    }

}
