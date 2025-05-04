package org.example.models.enums.Types;

public enum CraftingType {
    CherryBomb("Cherry Bomb", "4 copper ore + 1 coal", "Mining Level 1", 50),
    Bomb("Bomb", "4 iron ore + 1 coal", "Mining Level 2", 50),
    MegaBomb("Mega Bomb", "4 gold ore + 1 coal", "Mining Level 3", 50),
    Sprinkler("Sprinkler", "1 copper bar + 1 iron bar", "Farming Level 1", 0),
    QualitySprinkler("Quality Sprinkler", "1 iron bar + 1 gold bar", "Farming Level 2", 0),
    IridiumSprinkler("Iridium Sprinkler", "1 gold bar + 1 iridium bar", "Farming Level 3", 0),
    CharcoalKlin("Charcoal Klin", "20 wood + 2 copper bar", "Foraging Level 1", 0),
    Furnace("Furnace", "20 copper ore + 25 stone", "-", 0),
    Scarecrow("Scarecrow", "50 wood + 1 coal + 20 fiber", "-", 0),
    DeluxeScarecrow("Deluxe Scarecrow", "50 wood + 1 coal + 20 fiber + 1 iridium ore", "Farming Level 2", 0),
    BeeHouse("Bee House", "40 wood + 8 coal + 1 iron bar", "Farming Level 1", 0),
    CheesePress("Cheese Press", "45 wood + 45 stone + 1 copper bar", "Farming Level 2", 0),
    Keg("Keg", "30 wood + 1 copper bar + 1 iron bar", "Farming Level 3", 0),
    Loom("Loom", "60 wood + 30 fiber", "Farming Level 3", 0),
    MayonnaiseMachine("Mayonnaise Machine", "15 wood + 15 stone + 1 copper bar", "-", 0),
    OilMaker("Oil Maker", "100 wood + 1 gold bar + 1 iron bar", "Farming Level 3", 0),
    PreservesJar("Preserves Jar", "50 wood + 40 stone + 8 coal", "Farming Level 2", 0),
    Dehydrator("Dehydrator", "30 wood + 20 stone + 30 fiber", "Pierre's General Store", 0),
    FishSmoker("Fish Smoker", "50 wood + 3 iron bar + 10 coal", "Fish Shop", 0),
    MysticTreeSeed("Mystic Tree Seed", "5 acorn + 5 maple seed + 5 pine cone + 5 mahogany seed", "Foraging Level 4", 100),
    ;
    private final String name;
    private final String ingredients;
    private final String source;
    private final int baseSellPrice;

    CraftingType(String name, String ingredients, String source, int baseSellPrice) {
        this.name = name;
        this.ingredients = ingredients;
        this.source = source;
        this.baseSellPrice = baseSellPrice;
    }

    public static CraftingType fromName(String name) {
        for (CraftingType type : CraftingType.values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getSource() {
        return source;
    }

    public int getBaseSellPrice() {
        return baseSellPrice;
    }

    public void showInfo() {
        System.out.println("Name: " + getName());
        System.out.println("Base Sell Price: " + getBaseSellPrice());
        System.out.println("Ingredients: " + getIngredients());
        System.out.println("Source: " + getSource());
    }
}
