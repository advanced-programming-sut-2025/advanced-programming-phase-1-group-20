package org.example.models.enums.Types;

import org.example.models.enums.Ingredients;

public enum CookingType {
    FriedEgg("Fried Egg", Ingredients.FriedEgg, 50, "", "Starter", 35),
    BakedFish("Baked Fish", Ingredients.BakedFish, 75, "", "Starter", 100),
    Salad("Salad", Ingredients.Salad, 113, "", "Starter", 110),
    Omelet("Omelet", Ingredients.Omelet, 100, "", "Stardrop Saloon", 125),
    PumpkinPie("Pumpkin Pie", Ingredients.pumpkinpie, 225, "", "Stardrop Saloon", 385),
    Spaghetti("Spaghetti", Ingredients.spaghetti, 75, "", "Stardrop Saloon", 120),
    Pizza("Pizza", Ingredients.pizza, 150, "", "Stardrop Saloon", 300),
    Tortilla("Tortilla", Ingredients.Tortilla, 50, "", "Stardrop Saloon", 50),
    MakiRoll("Maki Roll", Ingredients.MakiRoll, 100, "", "Stardrop Saloon", 220),
    TripleShotEspresso("Triple Shot Espresso", Ingredients.TripleShotEspresso, 200, "Max Energy + 100 (5 hours)", "Stardrop Saloon", 450),
    Cookie("Cookie", Ingredients.Cookie, 90, "" , "Stardrop Saloon" , 140),
    HashBrowns("hash browns", Ingredients.hashbrowns, 90, "Farming (5 hours)" , "Stardrop Saloon" ,  120),
    Pancakes("pancakes", Ingredients.pancakes, 90,"Foraging (11 hours)" , "Stardrop Saloon" ,  80),
    FruitSalad("fruit salad", Ingredients.fruitsalad, 263, "" , "Stardrop Saloon" ,  450),
    RedPlate("red plate", Ingredients.redplate, 240, "Max Energy + 50 (3 hours)" , "Stardrop Saloon" , 400),
    Bread("bread", Ingredients.bread, 50,"" , "Stardrop Saloon" , 60),
    SalmonDinner("salmon dinner", Ingredients.salmondinner, 125, "" , "Leah reward" , 300),
    VegetableMedley("vegetable medley", Ingredients.vegetablemedley, 165,"" , "Foraging Level 2" , 120),
    FarmersLunch("farmer's lunch", Ingredients.farmerslunch, 200, "Farming (5 hours)" , "Farming level 1" , 150),
    SurvivalBurger("survival burger", Ingredients.survivalburger, 125, "Foraging (5 hours)" , "Foraging level 3" , 180),
    DishOTheSea("dish O' the Sea", Ingredients.dishOtheSea, 150, "Fishing (5 hours)" , "Fishing level 2" , 220),
    SeaFormPudding("seaform Pudding", Ingredients.seaformPudding, 175,"Fishing (10 hours)" , "Fishing level 3" ,  300),
    MinersTreat("miner's treat", Ingredients.minerstreat, 125, "Mining (5 hours)" , "Mining level 1" , 200),
    ;
    private final String name;
    private final Ingredients ingredients;
    private final int energy;
    private final String buffer;
    private final String source;
    private final int baseSellPrice;

    CookingType(String name, Ingredients ingredient, int energy, String buffer, String source, int baseSellPrice) {
        this.name = name;
        this.ingredients = ingredient;
        this.energy = energy;
        this.buffer = buffer;
        this.source = source;
        this.baseSellPrice = baseSellPrice;
    }

    public static CookingType fromName(String name) {
        for (CookingType type : CookingType.values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public Ingredients getIngredient() {
        return ingredients;
    }

    public int getEnergy() {
        return energy;
    }

    public String getBuffer() {
        return buffer;
    }

    public String getSource() {
        return source;
    }

    public int getBaseSellPrice() {
        return baseSellPrice;
    }

    public void showInfo() {
        System.out.println("Name: " + getName());
        System.out.println("Ingredients: " + getIngredient());
        System.out.println("Base Sell Price: " + getBaseSellPrice());
        System.out.println("Energy: " + getEnergy());
        System.out.println("Buffer: " + getBuffer());
        System.out.println("Source: " + getSource());
    }
}
