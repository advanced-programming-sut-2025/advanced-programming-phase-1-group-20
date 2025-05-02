package org.example.models.enums;

import org.example.models.Items.Item;

public enum CookingItems {
    FriedEgg("Fried Egg", Ingredients.FriedEgg.getIngridents(), 50, 35),
    BakedFish("Baked Fish", Ingredients.BakedFish.getIngridents(), 75, 100),
    Salad("Salad", Ingredients.Salad.getIngridents(), 113, 110),
    Omelet("Omelet", Ingredients.Olmelet.getIngridents(), 100, 125),
    Pumpkin_pie("pumpkin pie", Ingredients.pumpkinpie.getIngridents(), 225, 385),
    Spaghetti("spaghetti", Ingredients.spaghetti.getIngridents(), 75, 120),
    pizza("pizza", Ingredients.pizza.getIngridents(), 150, 300),
    Tortilla("Tortilla", Ingredients.Tortilla.getIngridents(), 50, 50),
    MakiRoll("Maki Roll", Ingredients.MakiRoll.getIngridents(), 100, 220),
    TripleShotEspresso("Triple Shot Espresso", Ingredients.TripleShotEspresso.getIngridents(), 200, 450),
    Cookie("Cookie", Ingredients.Cookie.getIngridents(), 90, 140),
    hashbrowns("hash browns", Ingredients.hashbrowns.getIngridents(), 90, 120),
    pancakes("pancakes", Ingredients.pancakes.getIngridents(), 90, 80),
    fruitsalad("fruit salad", Ingredients.fruitsalad.getIngridents(), 263, 450),
    redplate("red plate", Ingredients.redplate.getIngridents(), 240, 400),
    bread("bread", Ingredients.bread.getIngridents(), 50, 60),
    salmondinner("salmon dinner", Ingredients.salmondinner.getIngridents(), 125, 300),
    vegetablemedley("vegetable medley", Ingredients.vegetablemedley.getIngridents(), 165, 120),
    farmerslunch("farmer's lunch", Ingredients.farmerslunch.getIngridents(), 200, 150),
    survivalburger("survival burger", Ingredients.survivalburger.getIngridents(), 125, 180),
    dishOtheSea("dish O' the Sea", Ingredients.dishOtheSea.getIngridents(), 150, 220),
    seaformPudding("seaform Pudding", Ingredients.seaformPudding.getIngridents(), 175, 300),
    minerstreat("miner's treat", Ingredients.minerstreat.getIngridents(), 125, 200),
    ;
    private final String name;
    private final Item[] ingredients;

    private final int energy;
    private final int sellPrice;

    CookingItems(String name, Item[] ingredients, int energy, int sellPrice) {
        this.name = name;
        this.ingredients = ingredients;
        this.energy = energy;
        this.sellPrice = sellPrice;
    }

    public Item[] getIngredients() {
        return ingredients;
    }

    public void buffFarming(int hour) {

    }

    public void buffMining(int hour) {

    }

    public int getEnergy() {
        return energy;
    }


    public int getSellPrice() {
        return sellPrice;
    }
}
