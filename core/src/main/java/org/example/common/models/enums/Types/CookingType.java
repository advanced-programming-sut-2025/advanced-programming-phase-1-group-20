package org.example.common.models.enums.Types;


public enum CookingType {
    FriedEgg("Fried Egg", "1 egg", 50, "", "Starter", 35 , "Recipe/Fried_Egg.png"),
    BakedFish("Baked Fish", "1 Sardine + 1 Salmon + 1 wheat", 75, "", "Starter", 100 , "Recipe/Baked_Fish.png"),
    Salad("Salad", "1 leek + 1 dandelion", 113, "", "Starter", 110 , "Recipe/Salad.png"),
    Omelet("Omelet", "1 egg + 1 milk", 100, "", "Stardrop Saloon", 125 , "Recipe/Omelet.png"),
    PumpkinPie("Pumpkin Pie", "1 pumpking + 1 wheat flour + 1 milk + 1 sugar", 225, "", "Stardrop Saloon", 385 , "Recipe/Pumpkin_Pie.png"),
    Spaghetti("Spaghetti", "1 wheat flour + 1 tomato", 75, "", "Stardrop Saloon", 120 , "Recipe/Spaghetti.png"),
    Pizza("Pizza", "1 wheat flour + 1 tomato + 1 cheese", 150, "", "Stardrop Saloon", 300 , "Recipe/Pizza.png"),
    Tortilla("Tortilla", "1 corn", 50, "", "Stardrop Saloon", 50 , "Recipe/Tortilla.png"),
    MakiRoll("Maki Roll", "1 any fish + 1 rice + 1 fiber", 100, "", "Stardrop Saloon", 220 , "Recipe/Maki_Roll.png"),
    TripleShotEspresso("Triple Shot Espresso", "3 coffee", 200, "Max Energy + 100 (5 hours)", "Stardrop Saloon", 450 , "Recipe/Triple_Shot_Espresso.png"),
    Cookie("Cookie", "1 wheat flour + 1 sugar + 1 egg", 90, "" , "Stardrop Saloon" , 140 , "Recipe/Cookie.png"),
    HashBrowns("hash browns", "1 potato + 1 oil", 90, "Farming (5 hours)" , "Stardrop Saloon" ,  120 , "Recipe/Hashbrowns.png"),
    Pancakes("pancakes", "1 wheat flour + 1 egg", 90,"Foraging (11 hours)" , "Stardrop Saloon" ,  80 , "Recipe/Pancakes.png"),
    FruitSalad("fruit salad", "1 blueberry + 1 melon + 1 apricot", 263, "" , "Stardrop Saloon" ,  450 , "Recipe/Fruit_Salad.png"),
    RedPlate("red plate", "1 Red Cabbage + 1 Radish ", 240, "Max Energy + 50 (3 hours)" , "Stardrop Saloon" , 400 , "Recipe/Red_Plate.png"),
    Bread("bread", "1 wheat flour", 50,"" , "Stardrop Saloon" , 60 , "Recipe/Bread.png"),
    SalmonDinner("salmon dinner", "1 salmon + 1 Amaranth + 1 Kale", 125, "" , "Leah reward" , 300 , "Recipe/Salmon_Dinner.png"),
    VegetableMedley("vegetable medley", "1 tomato + 1 beet", 165,"" , "Foraging Level 2" , 120 , "Recipe/Vegetable_Medley.png"),
    FarmersLunch("farmer's lunch", "1 omelet + 1 parsnip", 200, "Farming (5 hours)" , "Farming level 1" , 150 , "Recipe/Farmer%27s_Lunch.png"),
    SurvivalBurger("survival burger", "1 bread + 1 carrot + 1 eggplant", 125, "Foraging (5 hours)" , "Foraging level 3" , 180 , "Recipe/Survival_Burger.png"),
    DishOTheSea("dish O' the Sea", "2 sardines + 1 hash browns", 150, "Fishing (5 hours)" , "Fishing level 2" , 220 , "Recipe/Dish_O%27_The_Sea.png"),
    SeaFormPudding("seaform Pudding", "1 Flounder + 1 midnight carp ", 175,"Fishing (10 hours)" , "Fishing level 3" ,  300  , "Recipe/Seafoam_Pudding.png"),
    MinersTreat("miner's treat", "2 carrot + 1 suger +. 1 milk", 125, "Mining (5 hours)" , "Mining level 1" , 200 , "Recipe/Miner%27s_Treat.png"),
    ;
    private final String name;
    private final String ingredients;
    private final int energy;
    private final String buffer;
    private final String source;
    private final int baseSellPrice;
    private final String imageFilepath;

    CookingType(String name, String ingredient, int energy, String buffer, String source, int baseSellPrice , String imageFilepath) {
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
            if (type.getName().equals(name)) {
                return type;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String getIngredient() {
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
