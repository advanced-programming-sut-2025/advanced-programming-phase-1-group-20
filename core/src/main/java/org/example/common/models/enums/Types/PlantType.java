package org.example.common.models.enums.Types;

import org.example.common.models.enums.Seasons;

import java.util.Arrays;

public enum PlantType {
    BlueJazz("Blue Jazz", "Jazz Seeds", new int[]{1, 2, 2, 2}, 7, true, 0, 50, true, 45, new Seasons[]{Seasons.SPRING}, false, "Blue_Jazz") ,
    Carrot("Carrot", "Carrot Seeds", new int[]{1, 1, 1}, 3, true, 0, 35, true, 75, new Seasons[]{Seasons.SPRING}, false, "Carrot") ,
    Cauliflower("Cauliflower", "Cauliflower Seeds", new int[]{1, 2, 4, 4, 1}, 12, true, 0, 175, true, 75, new Seasons[]{Seasons.SPRING}, true, "Cauliflower") ,
    CoffeeBean("Coffee Bean", "Coffee Bean", new int[]{1, 2, 2, 3, 2}, 10, false, 2, 15, false, 0, new Seasons[]{Seasons.SPRING, Seasons.SUMMER}, false, "Coffee_Bean") ,
    Garlic("Garlic", "Garlic Seeds", new int[]{1, 1, 1, 1}, 4, true, 0, 60, true, 20, new Seasons[]{Seasons.SPRING}, false, "Garlic") ,
    GreenBean("Green Bean", "Bean Starter", new int[]{1, 1, 1, 3, 4}, 10, false, 3, 40, true, 25, new Seasons[]{Seasons.SPRING}, false, "Green_Bean") ,
    Kale("Kale", "Kale Seeds", new int[]{1, 2, 2, 1}, 6, true, 0, 110, true, 50, new Seasons[]{Seasons.SPRING}, false, "Kale") ,
    Parsnip("Parsnip", "Parsnip Seeds", new int[]{1, 1, 1, 1}, 4, true, 0, 35, true, 25, new Seasons[]{Seasons.SPRING}, false, "Parsnip") ,
    Potato("Potato", "Potato Seeds", new int[]{1, 1, 1, 2, 1}, 6, true, 0, 80, true, 25, new Seasons[]{Seasons.SPRING}, false, "Potato") ,
    Rhubarb("Rhubarb", "Rhubarb Seeds", new int[]{2, 2, 2, 3, 4}, 13, true, 0, 220, false, 0, new Seasons[]{Seasons.SPRING}, false, "Rhubarb") ,
    Strawberry("Strawberry", "Strawberry Seeds", new int[]{1, 1, 2, 2, 2}, 8, false, 4, 120, true, 50, new Seasons[]{Seasons.SPRING}, false, "Strawberry") ,
    Tulip("Tulip", "Tulip Bulb", new int[]{1, 1, 2, 2}, 6, true, 0, 30, true, 45, new Seasons[]{Seasons.SPRING}, false, "Tulip") ,
    UnmilledRice("Unmilled Rice", "Rice Shoot", new int[]{1, 2, 2, 3}, 8, true, 0, 30, true, 3, new Seasons[]{Seasons.SPRING}, false, "Unmilled_Rice") ,
    Blueberry("Blueberry", "Blueberry Seeds", new int[]{1, 3, 3, 4, 2}, 13, false, 4, 50, true, 25, new Seasons[]{Seasons.SUMMER}, false, "Blueberry") ,
    Corn("Corn", "Corn Seeds", new int[]{2, 3, 3, 3, 3}, 14, false, 4, 50, true, 25, new Seasons[]{Seasons.SUMMER, Seasons.AUTUMN}, false, "Corn") ,
    Hops("Hops", "Hops Starter", new int[]{1, 1, 2, 3, 4}, 11, false, 1, 25, true, 45, new Seasons[]{Seasons.SUMMER}, false, "Hops") ,
    HotPepper("Hot Pepper", "Pepper Seeds", new int[]{1, 1, 1, 1, 1}, 5, false, 3, 40, true, 13, new Seasons[]{Seasons.SUMMER}, false, "Hot_Pepper") ,
    Melon("Melon", "Melon Seeds", new int[]{1, 2, 3, 3, 3}, 12, true, 0, 250, true, 113, new Seasons[]{Seasons.SUMMER}, true, "Melon") ,
    Poppy("Poppy", "Poppy Seeds", new int[]{1, 2, 2, 2}, 7, true, 0, 140, true, 45, new Seasons[]{Seasons.SUMMER}, false, "Poppy") ,
    Radish("Radish", "Radish Seeds", new int[]{2, 1, 2, 1}, 6, true, 0, 90, true, 45, new Seasons[]{Seasons.SUMMER}, false, "Radish") ,
    RedCabbage("Red Cabbage", "Red Cabbage Seeds", new int[]{2, 1, 2, 2, 2}, 9, true, 0, 260, true, 75, new Seasons[]{Seasons.SUMMER}, false, "Red_Cabbage") ,
    Starfruit("Starfruit", "Starfruit Seeds", new int[]{2, 3, 2, 3, 3}, 13, true, 0, 750, true, 125, new Seasons[]{Seasons.SUMMER}, false, "Starfruit") ,
    SummerSpangle("SUMMER Spangle", "Spangle Seeds", new int[]{1, 2, 3, 1}, 8, true, 0, 90, true, 45, new Seasons[]{Seasons.SUMMER}, false, "Summer_Spangle") ,
    SummerSquash("SUMMER Squash", "SUMMER Squash Seeds", new int[]{1, 1, 1, 2, 1}, 6, false, 3, 45, true, 63, new Seasons[]{Seasons.SUMMER}, false, "Summer_Squash") ,
    Sunflower("Sunflower", "Sunflower Seeds", new int[]{1, 2, 3, 2}, 8, true, 0, 80, true, 45, new Seasons[]{Seasons.SUMMER, Seasons.AUTUMN}, false, "Sunflower") ,
    Tomato("Tomato", "Tomato Seeds", new int[]{2, 2, 2, 2, 3}, 11, false, 4, 60, true, 20, new Seasons[]{Seasons.SUMMER}, false, "Tomato") ,
    Wheat("Wheat", "Wheat Seeds", new int[]{1, 1, 1, 1}, 4, true, 0, 25, false, 0, new Seasons[]{Seasons.SUMMER, Seasons.AUTUMN}, false, "Wheat") ,
    Amaranth("Amaranth", "Amaranth Seeds", new int[]{1, 2, 2, 2}, 7, true, 0, 150, true, 50, new Seasons[]{Seasons.AUTUMN}, false, "Amaranth") ,
    Artichoke("Artichoke", "Artichoke Seeds", new int[]{2, 2, 1, 2, 1}, 8, true, 0, 160, true, 30, new Seasons[]{Seasons.AUTUMN}, false, "Artichoke") ,
    Beet("Beet", "Beet Seeds", new int[]{1, 1, 2, 2}, 6, true, 0, 100, true, 30, new Seasons[]{Seasons.AUTUMN}, false, "Beet") ,
    BokChoy("Bok Choy", "Bok Choy Seeds", new int[]{1, 1, 1, 1}, 4, true, 0, 80, true, 25, new Seasons[]{Seasons.AUTUMN}, false, "Bok_Choy") ,
    Broccoli("Broccoli", "Broccoli Seeds", new int[]{2, 2, 2, 2}, 8, false, 4, 70, true, 63, new Seasons[]{Seasons.AUTUMN}, false, "Broccoli") ,
    Cranberries("Cranberries", "Cranberry Seeds", new int[]{1, 2, 1, 1, 2}, 7, false, 5, 75, true, 38, new Seasons[]{Seasons.AUTUMN}, false, "Cranberries") ,
    Eggplant("Eggplant", "Eggplant Seeds", new int[]{1, 1, 1, 1}, 5, false, 5, 60, true, 20, new Seasons[]{Seasons.AUTUMN}, false, "Cranberries") ,
    FairyRose("Fairy Rose", "Fairy Seeds", new int[]{1, 4, 4, 3}, 12, true, 0, 290, true, 45, new Seasons[]{Seasons.AUTUMN}, false, "Fairy_Rose") ,
    Grape("Grape", "Grape Starter", new int[]{1, 1, 2, 3, 3}, 10, false, 3, 80, true, 38, new Seasons[]{Seasons.AUTUMN}, false, "Grape") ,
    Pumpkin("Pumpkin", "Pumpkin Seeds", new int[]{1, 2, 3, 4, 3}, 13, true, 0, 320, false, 0, new Seasons[]{Seasons.AUTUMN}, true, "Pumpkin") ,
    Yam("Yam", "Yam Seeds", new int[]{1, 3, 3, 3}, 10, true, 0, 160, true, 45, new Seasons[]{Seasons.AUTUMN}, false, "Yam") ,
    SweetGemBerry("Sweet Gem Berry", "Rare Seed", new int[]{2, 4, 6, 6, 6}, 24, true, 0, 3000, false, 0, new Seasons[]{Seasons.AUTUMN}, false, "Sweet_Gem_Berry") ,
    Powdermelon("Powdermelon", "Powdermelon Seeds", new int[]{1, 2, 1, 2, 1}, 7, true, 0, 60, true, 63, new Seasons[]{Seasons.WINTER}, false, "Powdermelon") ,
    AncientFruit("Ancient Fruit", "Ancient Seeds", new int[]{2, 7, 7, 7, 5}, 28, false, 7, 550, false, 0, new Seasons[]{Seasons.SPRING, Seasons.SUMMER, Seasons.AUTUMN}, false, "Ancient_Fruit") ,
    ;
    private final String name;
    private final String seed;
    private int[] stage;
    private final int totalHarvestTime;
    private final boolean oneTimeHarvest;
    private final int regrowthTime;
    private final int baseSellPrice;
    private final boolean isEdible;
    private final int energy;
    private final Seasons[] seasons;
    private final boolean isGiantable;
    private final String imageFilePath;

    PlantType(String name, String seed, int[] stage, int totalHarvestTime
        , boolean oneTimeHarvest, int regrowthTime, int baseSellPrice
        , boolean isEdible, int energy, Seasons[] seasons, boolean isGiantable , String imageFilePath) {
        this.name = name;
        this.seed = seed;
        this.stage = stage;
        this.totalHarvestTime = totalHarvestTime;
        this.oneTimeHarvest = oneTimeHarvest;
        this.regrowthTime = regrowthTime;
        this.baseSellPrice = baseSellPrice;
        this.isEdible = isEdible;
        this.energy = energy;
        this.seasons = seasons;
        this.isGiantable = isGiantable;
        this.imageFilePath = imageFilePath;
    }

    public static PlantType fromName(String name) {
        for (PlantType type : PlantType.values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        return null;
    }

    public static PlantType fromSeed(String seed) {
        for (PlantType type : PlantType.values()) {
            if (type.getSeed().equals(seed)) {
                return type;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String getSeed() {
        return seed;
    }

    public int[] getStage() {
        return stage;
    }

    public void setStage(int[] stage) {
        this.stage = stage;
    }

    public int getTotalHarvestTime() {
        return totalHarvestTime;
    }

    public boolean isOneTimeHarvest() {
        return oneTimeHarvest;
    }

    public int getRegrowthTime() {
        return regrowthTime;
    }

    public int getBaseSellPrice() {
        return baseSellPrice;
    }

    public boolean isEdible() {
        return isEdible;
    }

    public int getEnergy() {
        return energy;
    }

    public Seasons[] getSeasons() {
        return seasons;
    }

    public boolean isGiantable() {
        return isGiantable;
    }

    public String getImageFilePath() {
        return imageFilePath;
    }

    public void showInfo() {
        System.out.println("Name: " + getName());
        System.out.println("Source: " + getSeed());
        System.out.print("Stage: ");
        String stages = Arrays.toString(getStage()).
            replace("[", "").replace("]", "")
            .replace(" ", "");
        System.out.println("Stages: " + stages);
        System.out.println("Total Harvest Time: " + getTotalHarvestTime());
        System.out.println("One Time: " + isOneTimeHarvest());
        System.out.print("Regrowth Time: ");
        if (getRegrowthTime() > 0) {
            System.out.println(getRegrowthTime());
        } else {
            System.out.println();
        }
        System.out.println("Base Sell Price: " + getBaseSellPrice());
        System.out.println("Is Edible: " + isEdible());
        System.out.println("Energy: " + getEnergy());
        System.out.println("Seasons: " + Arrays.toString(getSeasons()));
        System.out.println("Can Become Giant: " + isGiantable());
    }
}
