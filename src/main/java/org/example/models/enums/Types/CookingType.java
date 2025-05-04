package org.example.models.enums.Types;

public enum CookingType {
    FriedEgg("Fried Egg", "1 egg", 50, "", "Starter", 35),
    BakedFish("Baked Fish", "1 Sardine + 1 Salmon + 1 wheat", 75, "", "Starter", 100),
    Salad("Salad", "1 leek + 1 dandelion", 113, "", "Starter", 110),
    Omelet("Omelet", "1 egg + 1 milk", 100, "", "Stardrop Saloon", 125),
    PumpkinPie("Pumpkin Pie", "1 pumpkin + 1 wheat flour + 1 milk + 1 sugar", 225, "", "Stardrop Saloon", 385),
    Spaghetti("Spaghetti", "1 wheat flour + 1 tomato", 75, "", "Stardrop Saloon", 120),
    Pizza("Pizza", "1 wheat flour + 1 tomato + 1 cheese", 150, "", "Stardrop Saloon", 300),
    Tortilla("Tortilla", "1 corn", 50, "", "Stardrop Saloon", 50),
    MakiRoll("Maki Roll", "1 any fish + 1 rice + 1 fiber", 100, "", "Stardrop Saloon", 220),
    TripleShotEspresso("Triple Shot Espresso", "3 coffee", 200, "Max Energy + 100 (5 hours)", "Stardrop Saloon", 450),
    ;
    private final String name;
    private final String ingredient;
    private final int energy;
    private final String buffer;
    private final String source;
    private final int baseSellPrice;

    CookingType(String name, String ingredient, int energy, String buffer, String source, int baseSellPrice) {
        this.name = name;
        this.ingredient = ingredient;
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
        return ingredient;
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
