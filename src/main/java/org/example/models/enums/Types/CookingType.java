package org.example.models.enums.Types;


public enum CookingType {
    FriedEgg("Fried Egg", "1 egg", 50, "", "Starter", 35),
    BakedFish("Baked Fish", "1 Sardine + 1 Salmon + 1 wheat", 75, "", "Starter", 100),
    Salad("Salad", "1 leek + 1 dandelion", 113, "", "Starter", 110),
    Omelet("Omelet", "1 egg + 1 milk", 100, "", "Stardrop Saloon", 125),
    PumpkinPie("Pumpkin Pie", "1 pumpking + 1 wheat flour + 1 milk + 1 sugar", 225, "", "Stardrop Saloon", 385),
    Spaghetti("Spaghetti", "1 wheat flour + 1 tomato", 75, "", "Stardrop Saloon", 120),
    Pizza("Pizza", "1 wheat flour + 1 tomato + 1 cheese", 150, "", "Stardrop Saloon", 300),
    Tortilla("Tortilla", "1 corn", 50, "", "Stardrop Saloon", 50),
    MakiRoll("Maki Roll", "1 any fish + 1 rice + 1 fiber", 100, "", "Stardrop Saloon", 220),
    TripleShotEspresso("Triple Shot Espresso", "3 coffee", 200, "Max Energy + 100 (5 hours)", "Stardrop Saloon", 450),
    Cookie("Cookie", "1 wheat flour + 1 sugar + 1 egg", 90, "" , "Stardrop Saloon" , 140),
    HashBrowns("hash browns", "1 potato + 1 oil", 90, "Farming (5 hours)" , "Stardrop Saloon" ,  120),
    Pancakes("pancakes", "1 wheat flour + 1 egg", 90,"Foraging (11 hours)" , "Stardrop Saloon" ,  80),
    FruitSalad("fruit salad", "1 blueberry + 1 melon + 1 apricot", 263, "" , "Stardrop Saloon" ,  450),
    RedPlate("red plate", "1 Red Cabbage + 1 Radish ", 240, "Max Energy + 50 (3 hours)" , "Stardrop Saloon" , 400),
    Bread("bread", "1 wheat flour", 50,"" , "Stardrop Saloon" , 60),
    SalmonDinner("salmon dinner", "1 salmon + 1 Amaranth + 1 Kale", 125, "" , "Leah reward" , 300),
    VegetableMedley("vegetable medley", "1 tomato + 1 beet", 165,"" , "Foraging Level 2" , 120),
    FarmersLunch("farmer's lunch", "1 omelet + 1 parsnip", 200, "Farming (5 hours)" , "Farming level 1" , 150),
    SurvivalBurger("survival burger", "1 bread + 1 carrot + 1 eggplant", 125, "Foraging (5 hours)" , "Foraging level 3" , 180),
    DishOTheSea("dish O' the Sea", "2 sardines + 1 hash browns", 150, "Fishing (5 hours)" , "Fishing level 2" , 220),
    SeaFormPudding("seaform Pudding", "1 Flounder + 1 midnight carp ", 175,"Fishing (10 hours)" , "Fishing level 3" ,  300),
    MinersTreat("miner's treat", "2 carrot + 1 suger +. 1 milk", 125, "Mining (5 hours)" , "Mining level 1" , 200),
    ;
    private final String name;
    private final String ingredients;
    private final int energy;
    private final String buffer;
    private final String source;
    private final int baseSellPrice;

    CookingType(String name, String ingredient, int energy, String buffer, String source, int baseSellPrice) {
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
}
