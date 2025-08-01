package org.example.common.models.enums.Types;

import org.example.common.models.Items.ArtisanItem;
import org.example.common.models.Items.Item;
import org.example.common.models.enums.Ingredients;

import java.util.List;
import java.util.Map;

public enum CraftingType {
    // Each entry now references the Ingredients enum
    CherryBomb("Cherry Bomb", Ingredients.CherryBomb, "Mining Level 1", 50,
        "Cherry_Bomb" , List.of()),


    Bomb("Bomb", Ingredients.Bomb, "Mining Level 2", 50,
        "Bomb" , List.of()),


    MegaBomb("Mega Bomb", Ingredients.MegaBomb, "Mining Level 3", 50,
        "Mega_Bomb" , List.of()),


    Sprinkler("Sprinkler", Ingredients.Sprinkler, "Farming Level 1", 0,
        "Sprinkler" , List.of()),


    QualitySprinkler("Quality Sprinkler", Ingredients.QualitySprinkler, "Farming Level 2", 0,
        "Quality_Sprinkler" , List.of()),


    IridiumSprinkler("Iridium Sprinkler", Ingredients.IridiumSprinkler, "Farming Level 3", 0,
        "Iridium_Sprinkler" , List.of()),


    Scarecrow("Scarecrow", Ingredients.Scarecrow, "-", 0,
        "Scarecrow" , List.of()),


    DeluxeScarecrow("Deluxe Scarecrow", Ingredients.DeluxeScarecrow, "Farming Level 2", 0,
        "Deluxe_Scarecrow", List.of()),


    BeeHouse("Bee House", Ingredients.BeeHouse, "Farming Level 1", 0,
        "Bee_House" , List.of(new ArtisanItem(ArtisanType.Honey))),


    CharcoalKiln("Charcoal Kiln", Ingredients.CharcoalKiln, "Foraging Level 1", 0,
        "Charcoal_Kiln" , List.of(new ArtisanItem(ArtisanType.Coal))),

    Furnace("Furnace", Ingredients.Furnace, "-", 0, "Furnace" ,
        List.of(new ArtisanItem(ArtisanType.IronBar) , new ArtisanItem(ArtisanType.GoldBar) ,
            new ArtisanItem(ArtisanType.CopperBar) , new ArtisanItem(ArtisanType.IridiumBar)) ),


    CheesePress("Cheese Press", Ingredients.CheesePress, "Farming Level 2", 0, "Cheese_Press" ,
        List.of(new ArtisanItem(ArtisanType.Cheese) , new ArtisanItem(ArtisanType.GoatCheese)) ),


    Keg("Keg", Ingredients.Keg, "Farming Level 3", 0, "Keg"
    , List.of(new ArtisanItem(ArtisanType.Beer) , new ArtisanItem(ArtisanType.Vinegar) , new ArtisanItem(ArtisanType.Coffee) ,
        new ArtisanItem(ArtisanType.Juice) , new ArtisanItem(ArtisanType.Mead) , new ArtisanItem(ArtisanType.PaleAle) ,
        new ArtisanItem(ArtisanType.Wine))),

    Loom("Loom", Ingredients.Loom, "Farming Level 3", 0, "Loom" ,
        List.of(new ArtisanItem(ArtisanType.Cloth))),


    MayonnaiseMachine("Mayonnaise Machine", Ingredients.MayonnaiseMachine, "-", 0, "Mayonnaise_Machine" ,
        List.of(new ArtisanItem(ArtisanType.Mayonnaise) , new ArtisanItem(ArtisanType.DinosaurMayonnaise) ,
            new ArtisanItem(ArtisanType.DuckMayonnaise))),


    OilMaker("Oil Maker", Ingredients.OilMaker, "Farming Level 3", 0, "Oil_Maker",
        List.of(new ArtisanItem(ArtisanType.TruffleOil) , new ArtisanItem(ArtisanType.Oil))),


    PreservesJar("Preserves Jar", Ingredients.PreservesJar, "Farming Level 2", 0, "Preserves_Jar" ,
        List.of(new ArtisanItem(ArtisanType.Jelly) , new ArtisanItem(ArtisanType.Pickles))),


    Dehydrator("Dehydrator", Ingredients.Dehydrator, "Pierre's General Store", 0, "Dehydrator" ,
        List.of(new ArtisanItem(ArtisanType.DriedMushrooms) , new ArtisanItem(ArtisanType.DriedFruit)
            , new ArtisanItem(ArtisanType.Raisins))),


    FishSmoker("Fish Smoker", Ingredients.FishSmoker, "Fish Shop", 0, "Fish_Smoker" ,
        List.of(new ArtisanItem(ArtisanType.SmokedFish)));

    private final String name;
    // --- MODIFIED FIELD ---
    private final Ingredients ingredients;
    private final String source;
    private final int baseSellPrice;
    private final String imageFilepath;
    private final List<ArtisanItem> artisanItems;


    // --- MODIFIED CONSTRUCTOR ---
    CraftingType(String name, Ingredients ingredients, String source, int baseSellPrice, String imageFilepath, List<ArtisanItem> artisanItems) {
        this.name = name;
        this.ingredients = ingredients;
        this.source = source;
        this.baseSellPrice = baseSellPrice;
        this.imageFilepath = imageFilepath;
        this.artisanItems = artisanItems;
    }

    // --- NEW GETTER ---
    public Ingredients getIngredients() {
        return this.ingredients;
    }

    // Other getters remain the same...
    public String getName() { return name; }
    public String getSource() { return source; }
    public int getBaseSellPrice() { return baseSellPrice; }
    public String getImageFilepath() { return imageFilepath; }

    public static CraftingType fromName(String name) {
        for (CraftingType type : CraftingType.values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

    public void showInfo() {
        System.out.println("Name: " + getName());
        System.out.println("Base Sell Price: " + getBaseSellPrice());
        System.out.println("Ingredients: " + getIngredients());
        System.out.println("Source: " + getSource());
    }

    public List<ArtisanItem> getArtisanItems() {
        return artisanItems;
    }
}
