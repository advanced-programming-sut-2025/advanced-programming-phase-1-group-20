package models.enums;

import models.Items.ArtisanItem;
import models.Item;

public enum ArtisanItems {
    HONEY("Honey", 75, 96, Ingredients.Honey.getIngridents(), 350),
    CHEESE("Cheese", 100, 3, Ingredients.Cheese.getIngridents(), 230),
    CHEESE_LARGE("Cheese", 100, 3, Ingredients.LargeCheese.getIngridents(), 345),
    GOAT_CHEESE("Goat Cheese", 100, 3, Ingredients.GoatCheese.getIngridents(), 400),
    GOAT_CHEESE_LARGE("Goat Cheese", 100, 3, Ingredients.LargeGoatCheese.getIngridents(), 600),
    BEER("Beer", 50, 24, Ingredients.Beer.getIngridents(), 200),
    VINEGAR("Vinegar", 13, 10, Ingredients.Vinegar.getIngridents(), 100),
    COFFEE("Coffee", 75, 2, Ingredients.Coffee.getIngridents(), 150),
    JUICE("Juice", 0, 96, Ingredients.Juice.getIngridents(), 0),
    MEAD("Mead", 100, 10, Ingredients.Mead.getIngridents(), 300),
    PALE_ALE("Pale Ale", 50, 72, Ingredients.PaleAle.getIngridents(), 300),
    WINE("Wine", 0, 168, Ingredients.Wine.getIngridents(), 0),
    DRIED_MUSHROOMS("Dried Mushrooms", 50, 8,Ingredients.DriedMushrooms.getIngridents(), 0),
    DRIED_FRUIT("Dried Fruit", 75, 8, Ingredients.DriedFruit.getIngridents(), 0),
    RAISINS("Raisins", 125, 8, Ingredients.Raisins.getIngridents(), 600),
    COAL("Coal", 0, 1, Ingredients.Coal.getIngridents(), 50)
    ;
    private final String name;
    private final int energyCost;
    private final int proccessingTime; // "hour input i think"
    private final Item[] ingredients;
    private final int sellPrice;
    ArtisanItems(String name , int energyCost, int proccessingTime, Item[] ingredients, int sellPrice) {
        this.name = name;
        this.energyCost = energyCost;
        this.proccessingTime = proccessingTime;
        this.ingredients = ingredients;
        this.sellPrice = sellPrice;
    }
    public ArtisanItem createArtisanItem() {
        return new ArtisanItem(this);
    }

    public String getName() {
        return name;
    }

    public int getEnergyCost() {
        return energyCost;
    }

    public int getProccessingTime() {
        return proccessingTime;
    }

    public Item[] getIngredients() {
        return ingredients;
    }

    public int getSellPrice() {
        return sellPrice;
    }
}
