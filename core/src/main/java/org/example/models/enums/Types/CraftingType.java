package org.example.models.enums.Types;

public enum CraftingType {
    CherryBomb("Cherry Bomb", "4 Copper + 1 Coal", "Mining Level 1", 50 ,"Cherry_Bomb" ),
    Bomb("Bomb", "4 Iron + 1 Coal", "Mining Level 2", 50, "Bomb"),
    MegaBomb("Mega Bomb", "4 Gold + 1 Coal", "Mining Level 3", 50, "Mega_Bomb") ,
    Sprinkler("Sprinkler", "1 Copper + 1 Iron", "Farming Level 1", 0, "Sprinkler") ,
    QualitySprinkler("Quality Sprinkler", "1 Iron + 1 Gold", "Farming Level 2", 0, "Quality_Sprinkler") ,
    IridiumSprinkler("Iridium Sprinkler", "1 Gold + 1 Iridium", "Farming Level 3", 0, "Iridium_Sprinkler") ,
    CharcoalKiln("Charcoal Kiln", "20 Wood + 2 Copper", "Foraging Level 1", 0, "Charcoal_Kiln") ,
    Furnace("Furnace", "20 Copper + 25 Stone", "-", 0, "Furnace") ,
    Scarecrow("Scarecrow", "50 Wood + 1 Coal + 20 Fiber", "-", 0, "Scarecrow") ,
    DeluxeScarecrow("Deluxe Scarecrow", "50 Wood + 1 Coal + 20 Fiber + 1 Iridium", "Farming Level 2", 0, "Deluxe_Scarecrow") ,
    BeeHouse("Bee House", "40 wood + 8 coal + 1 Iron", "Farming Level 1", 0, "Bee_House") ,
    CheesePress("Cheese Press", "45 wood + 45 stone + 1 copper bar", "Farming Level 2", 0, "Cheese_Press") ,
    Keg("Keg", "30 wood + 1 copper bar + 1 Iron", "Farming Level 3", 0, "Keg") ,
    Loom("Loom", "60 wood + 30 fiber", "Farming Level 3", 0, "Loom") ,
    MayonnaiseMachine("Mayonnaise Machine", "15 wood + 15 stone + 1 copper bar", "-", 0, "Mayonnaise_Machine") ,
    OilMaker("Oil Maker", "100 wood + 1 gold bar + 1 iron bar", "Farming Level 3", 0, "Oil_Maker") ,
    PreservesJar("Preserves Jar", "50 wood + 40 stone + 8 coal", "Farming Level 2", 0, "Preserves_Jar") ,
    Dehydrator("Dehydrator", "30 wood + 20 stone + 30 fiber", "Pierre's General Store", 0, "Dehydrator") ,
    FishSmoker("Fish Smoker", "50 wood + 3 iron bar + 10 coal", "Fish Shop", 0, "Fish_Smoker") ,
    MysticTreeSeed("Mystic Tree Seed", "5 acorn + 5 maple seed + 5 pine cone + 5 mahogany seed", "Foraging Level 4", 100, "Mystic_Tree_Seed") ,
    ;
    private final String name;
    private final String ingredients;
    private final String source;
    private final int baseSellPrice;
    private final String imageFilepath;

    CraftingType(String name, String ingredients, String source, int baseSellPrice , String imageFilepath) {
        this.name = name;
        this.ingredients = ingredients;
        this.source = source;
        this.baseSellPrice = baseSellPrice;
        this.imageFilepath = imageFilepath;
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

    public String getImageFilepath() {
        return imageFilepath;
    }

    public void showInfo() {
        System.out.println("Name: " + getName());
        System.out.println("Base Sell Price: " + getBaseSellPrice());
        System.out.println("Ingredients: " + getIngredients());
        System.out.println("Source: " + getSource());
    }
}
