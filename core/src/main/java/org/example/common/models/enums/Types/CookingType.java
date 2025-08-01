package org.example.common.models.enums.Types;


import org.example.common.models.enums.Ingredients;

public enum CookingType {
    FriedEgg("Fried Egg", Ingredients.FriedEgg, 50, "", "Starter", 35 , "Fried_Egg"),
    BakedFish("Baked Fish", Ingredients.BakedFish, 75, "", "Starter", 100 , "Baked_Fish"),
    Salad("Salad", Ingredients.Salad, 113, "", "Starter", 110 , "Salad"),
    Omelet("Omelet", Ingredients.Omelet, 100, "", "Stardrop Saloon", 125 , "Omelet"),
    PumpkinPie("Pumpkin Pie", Ingredients.PumpkinPie, 225, "", "Stardrop Saloon", 385 , "Pumpkin_Pie"),
    Spaghetti("Spaghetti", Ingredients.Spaghetti, 75, "", "Stardrop Saloon", 120 , "Spaghetti"),
    Pizza("Pizza", Ingredients.Pizza, 150, "", "Stardrop Saloon", 300 , "Pizza"),
    Tortilla("Tortilla", Ingredients.Tortilla, 50, "", "Stardrop Saloon", 50 , "Tortilla"),
    MakiRoll("Maki Roll", Ingredients.MakiRoll, 100, "", "Stardrop Saloon", 220 , "Maki_Roll"),
    TripleShotEspresso("Triple Shot Espresso", Ingredients.TripleShotEspresso, 200, "Max Energy + 100 (5 hours)", "Stardrop Saloon", 450 , "Triple_Shot_Espresso"),
    Cookie("Cookie", Ingredients.Cookie, 90, "" , "Stardrop Saloon" , 140 , "Cookie"),
    HashBrowns("Hash browns", Ingredients.Hashbrowns, 90, "Farming (5 hours)" , "Stardrop Saloon" ,  120 , "Hashbrowns"),
    Pancakes("Pancakes", Ingredients.Pancakes, 90,"Foraging (11 hours)" , "Stardrop Saloon" ,  80 , "Pancakes"),
    FruitSalad("Fruit Salad", Ingredients.FruitSalad, 263, "" , "Stardrop Saloon" ,  450 , "Fruit_Salad"),
    RedPlate("Red Plate", Ingredients.RedPlate, 240, "Max Energy + 50 (3 hours)" , "Stardrop Saloon" , 400 , "Red_Plate"),
    Bread("Bread", Ingredients.Bread, 50,"" , "Stardrop Saloon" , 60 , "Bread"),
    SalmonDinner("Salmon Dinner", Ingredients.SalmonDinner, 125, "" , "Leah reward" , 300 , "Salmon_Dinner"),
    VegetableMedley("Vegetable Medley", Ingredients.VegetableMedley, 165,"" , "Foraging Level 2" , 120 , "Vegetable_Medley"),
    FarmersLunch("Farmers Lunch", Ingredients.FarmersLunch, 200, "Farming (5 hours)" , "Farming level 1" , 150 , "Farmers_Lunch"),
    SurvivalBurger("Survival Burger", Ingredients.SurvivalBurger, 125, "Foraging (5 hours)" , "Foraging level 3" , 180 , "Survival_Burger"),
    DishOTheSea("Dish O The Sea", Ingredients.DishOTheSea, 150, "Fishing (5 hours)" , "Fishing level 2" , 220 , "Dish_O_The_Sea"),
    SeaFormPudding("Seafoam Pudding", Ingredients.SeafoamPudding, 175,"Fishing (10 hours)" , "Fishing level 3" ,  300  , "Seafoam_Pudding"),
    MinersTreat("Miners Treat", Ingredients.MinersTreat, 125, "Mining (5 hours)" , "Mining level 1" , 200 , "Miners_Treat"),
    ;
    private final String name;
    private final Ingredients ingredients;
    private final int energy;
    private final String buffer;
    private final String source;
    private final int baseSellPrice;
    private final String imageFilepath;

    CookingType(String name, Ingredients ingredient, int energy, String buffer, String source, int baseSellPrice , String imageFilepath) {
        this.name = name;
        this.ingredients = ingredient;
        this.energy = energy;
        this.buffer = buffer;
        this.source = source;
        this.baseSellPrice = baseSellPrice;
        this.imageFilepath = imageFilepath;
    }

    public static CookingType fromName(String name) {
        for (CookingType type : CookingType.values()) {
            if (type.getName().equalsIgnoreCase(name)) {
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

    public String getImageFilepath() {
        return imageFilepath;
    }
}
