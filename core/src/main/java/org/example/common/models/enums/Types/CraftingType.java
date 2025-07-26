package org.example.common.models.enums.Types;

public enum CraftingType {
    CherryBomb("Cherry Bomb", "4 Copper + 1 Coal", "Mining Level 1", 50 ,"content/Crafting/Cherry_Bomb.png" ),
    Bomb("Bomb", "4 Iron + 1 Coal", "Mining Level 2", 50, "content/Crafting/Bomb.png"),
    MegaBomb("Mega Bomb", "4 Gold + 1 Coal", "Mining Level 3", 50, "content/Crafting/Mega_Bomb.png") ,
    Sprinkler("Sprinkler", "1 Copper + 1 Iron", "Farming Level 1", 0, "content/Crafting/Sprinkler.png") ,
    QualitySprinkler("Quality Sprinkler", "1 Iron + 1 Gold", "Farming Level 2", 0, "content/Crafting/Quality_Sprinkler.png") ,
    IridiumSprinkler("Iridium Sprinkler", "1 Gold + 1 Iridium", "Farming Level 3", 0, "content/Crafting/Iridium_Sprinkler.png") ,
    CharcoalKiln("Charcoal Kiln", "20 Wood + 2 Copper", "Foraging Level 1", 0, "content/Crafting/Charcoal_Kiln.png") ,
    Furnace("Furnace", "20 Copper + 25 Stone", "-", 0, "content/Crafting/Furnace.png") ,
    Scarecrow("Scarecrow", "50 Wood + 1 Coal + 20 Fiber", "-", 0, "content/Crafting/Scarecrow.png") ,
    DeluxeScarecrow("Deluxe Scarecrow", "50 Wood + 1 Coal + 20 Fiber + 1 Iridium", "Farming Level 2", 0, "content/Crafting/Deluxe_Scarecrow.png") ,
    BeeHouse("Bee House", "40 Wood + 8 Coal + 1 Iron", "Farming Level 1", 0, "content/Crafting/Bee_House.png") ,
    CheesePress("Cheese Press", "45 Wood + 45 Stone + 1 Copper", "Farming Level 2", 0, "content/ArtisanItems/Cheese_Press.png") ,
    Keg("Keg", "30 Wood + 1 Copper + 1 Iron", "Farming Level 3", 0, "Keg") ,
    Loom("Loom", "60 Wood + 30 fiber", "Farming Level 3", 0, "Loom") ,
    MayonnaiseMachine("Mayonnaise Machine", "15 Wood + 15 stone + 1 Copper", "-", 0, "Mayonnaise_Machine") ,
    OilMaker("Oil Maker", "100 Wood + 1 Gold + 1 Iron", "Farming Level 3", 0, "Oil_Maker") ,
    PreservesJar("Preserves Jar", "50 Wood + 40 Stone + 8 Coal", "Farming Level 2", 0, "Preserves_Jar") ,
    Dehydrator("Dehydrator", "30 Wood + 20 Stone + 30 Fiber", "Pierre's General Store", 0, "Dehydrator") ,
    FishSmoker("Fish Smoker", "50 Wood + 3 Iron + 10 Coal", "Fish Shop", 0, "Fish_Smoker") ,
//    MysticTreeSeed("Mystic Tree Seed", "5 acorn + 5 maple seed + 5 pine cone + 5 mahogany seed", "Foraging Level 4", 100, "Mystic_Tree_Seed") ,
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
