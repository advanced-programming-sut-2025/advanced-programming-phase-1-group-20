package org.example.models.enums.Types;

import org.example.models.Plant;
import org.example.models.enums.Seasons;

public enum PlantType {
    CARROT("Carrot", "Carrot Seed", new int[]{1, 1, 1}, 3, true, 0, 35, true, 75, new Seasons[]{Seasons.SPRING}, true, false),
    BlueJazz("Blue Jazz", "Jazz Seeds", new int[]{1, 2, 2, 2}, 7, true, 0, 50, true, 45, new Seasons[]{Seasons.SPRING}, true, false),
    Cauliflower("Cauliflower", "Cauliflower Seeds", new int[]{1, 2, 4, 4, 1}, 12, true, 0, 175, true, 75, new Seasons[]{Seasons.SPRING}, true, true),
    CoffeeBean("Coffee Bean", "Coffee Bean", new int[]{1, 2, 2, 3, 2}, 10, false, 2, 15, false, 0, new Seasons[]{Seasons.SPRING, Seasons.SUMMER}, true, false),
    Garlic("Garlic", "Garlic Seeds", new int[]{1, 1, 1, 1}, 4, true, 0, 60, true, 20, new Seasons[]{Seasons.SPRING}, true, false),
    GreenBean("Green Bean", "Bean Starter", new int[]{1, 1, 1, 3, 4}, 10, false, 3, 40, true, 20, new Seasons[]{Seasons.SPRING}, true, false),
    Kale("Kale", "Kale Seeds", new int[]{1, 2, 2, 1}, 6, true, 0, 110, true, 50, new Seasons[]{Seasons.SPRING}, true, false),
    Parsnip("Parsnip", "Parsnip Seeds", new int[]{1, 1, 1, 1}, 4, true, 0, 35, true, 25, new Seasons[]{Seasons.SPRING}, true, false),
    Potato("Potato", "Potato Seeds", new int[]{1, 1, 2, 1}, 6, true, 0, 80, true, 25, new Seasons[]{Seasons.SPRING}, true, false),
    Rhubarb("Rhubarb", "Rhubarb Seeds", new int[]{2, 2, 2, 3, 4}, 13, true, 0, 220, false, 0, new Seasons[]{Seasons.SPRING}, true, false),
    Strawberry("Strawberry", "Strawberry Seeds", new int[]{1, 1, 2, 2, 2}, 8, false, 4, 120, true, 50, new Seasons[]{Seasons.SPRING}, true, false),
    Tulip("Tulip", "Tulip Bulb", new int[]{1, 1, 2, 2}, 6, true, 0, 30, true, 45, new Seasons[]{Seasons.SPRING}, true, false),
    UnmilledRice("Unmilled Rice", "Rice Shoot", new int[]{1, 2, 2, 3}, 8, true, 0, 30, true, 3, new Seasons[]{Seasons.SPRING}, true, false),
    Blueberry("Blueberry", "Blueberry Seeds", new int[]{1, 3, 3, 4, 2}, 13, false, 4, 50, true, 25, new Seasons[]{Seasons.SUMMER}, true, false),
    Corn("Corn", "Corn Seeds", new int[]{2, 3, 3, 3, 3}, 14, false, 4, 50, true, 25, new Seasons[]{Seasons.SUMMER, Seasons.AUTUMN}, true, false),
    Hops("Hops", "Hops Starter", new int[]{1, 1, 2, 3, 4}, 11, false, 1, 25, true, 45, new Seasons[]{Seasons.SUMMER}, true, false),
    HotPepper("Hot Pepper", "Pepper Seeds", new int[]{1, 1, 1, 1, 1}, 5, false, 3, 40, true, 13, new Seasons[]{Seasons.SUMMER}, true, false),
    Melon("Melon", "Melon Seeds", new int[]{1, 2, 3, 3, 3}, 12, true, 0, 250, true, 113, new Seasons[]{Seasons.SUMMER}, true, true),
    Poppy("Poppy", "Poppy Seeds", new int[]{1, 2, 2, 2}, 7, true, 0, 140, true, 45, new Seasons[]{Seasons.SUMMER}, true, false),
    Radish("Radish", "Radish Seeds", new int[]{2, 1, 2, 1}, 6, true, 0, 90, true, 45, new Seasons[]{Seasons.SUMMER}, true, false),
    RedCabbage("Red Cabbage", "Red Cabbage Seeds", new int[]{2, 1, 2, 2, 2}, 9, true, 0, 260, true, 75, new Seasons[]{Seasons.SUMMER}, true, false),
    Starfruit("Starfruit", "Starfruit Seeds", new int[]{2, 3, 2, 3, 3}, 13, true, 0, 750, true, 125, new Seasons[]{Seasons.SUMMER}, true, false),
    SummerSpangle("Summer Spangle", "Spangle Seeds", new int[]{1, 2, 3, 1}, 8, true, 0, 90, true, 45, new Seasons[]{Seasons.SUMMER}, true, false),
    SummerSquash("Summer Squash", "Summer Squash Seeds", new int[]{1, 1, 1, 2, 1}, 6, false, 3, 45, true, 63, new Seasons[]{Seasons.SUMMER}, true, false),
    Sunflower("Sunflower", "Sunflower Seeds", new int[]{1, 2, 3, 2}, 8, true, 0, 80, true, 45, new Seasons[]{Seasons.SUMMER, Seasons.AUTUMN}, true, false),
    Tomato("Tomato", "Tomato Seeds", new int[]{2, 2, 2, 2, 3}, 11, false, 4, 60, true, 20, new Seasons[]{Seasons.SUMMER}, true, false),
    Wheat("Wheat", "Wheat Seeds", new int[]{1, 1, 1, 1}, 4, true, 0, 25, false, 0, new Seasons[]{Seasons.SUMMER}, true, false),
    Amaranth("Amaranth", "Amaranth Seeds", new int[]{1, 2, 2, 2}, 7, true, 0, 150, true, 50, new Seasons[]{Seasons.AUTUMN}, true, false),
    Artichoke("Artichoke", "Artichoke Seeds", new int[]{2, 2, 1, 2, 1}, 8, true, 0, 160, true, 30, new Seasons[]{Seasons.AUTUMN}, true, false),
    Beet("Beet", "Beet Seeds", new int[]{1, 1, 2, 2}, 6, true, 0, 100, true, 30, new Seasons[]{Seasons.AUTUMN}, true, false),
    BokChoy("Bok Choy", "Bok Choy Seeds", new int[]{1, 1, 1, 1}, 4, true, 0, 80, true, 25, new Seasons[]{Seasons.AUTUMN}, true, false),
    Broccoli("Broccoli", "Broccoli Seeds", new int[]{2, 2, 2, 2}, 8, false, 4, 70, true, 63, new Seasons[]{Seasons.AUTUMN}, true, false),
    Cranberries("Cranberries", "Cranberry Seeds", new int[]{1, 2, 1, 1, 2}, 7, false, 5, 75, true, 38, new Seasons[]{Seasons.AUTUMN}, true, false),
    Eggplant("Eggplant", "Eggplant Seeds", new int[]{1, 1, 1, 1}, 5, false, 5, 60, true, 20, new Seasons[]{Seasons.AUTUMN}, true, false),
    FairyRose("Fairy Rose", "Fairy Seeds", new int[]{1, 4, 4, 3}, 12, true, 0, 290, true, 45, new Seasons[]{Seasons.AUTUMN}, true, false),
    Grape("Grape", "Grape Starter", new int[]{1, 1, 2, 3, 3}, 10, false, 3, 80, true, 38, new Seasons[]{Seasons.AUTUMN}, true, false),
    Pumpkin("Pumpkin", "Pumpkin Seeds", new int[]{1, 2, 3, 4, 3}, 13, true, 0, 320, false, 0, new Seasons[]{Seasons.AUTUMN}, true, true),
    Yam("Yam", "Yam Seeds", new int[]{1, 3, 3, 3}, 10, true, 0, 160, true, 45, new Seasons[]{Seasons.AUTUMN}, true, false),
    SweetGemBerry("Sweet Gem Berry", "Rare Seed", new int[]{2, 4, 6, 6, 6}, 24, true, 0, 3000, false, 0, new Seasons[]{Seasons.AUTUMN}, true, false),
    Powdermelon("Powdermelon", "Powdermelon Seeds", new int[]{1, 2, 1, 2, 1}, 7, true, 0, 60, true, 63, new Seasons[]{Seasons.WINTER}, true, true),
    AncientFruit("Ancient Fruit", "Ancient Seeds", new int[]{2, 7, 7, 7, 5}, 28, false, 7, 550, false, 0, new Seasons[]{Seasons.SPRING, Seasons.SUMMER, Seasons.AUTUMN}, true, false),
    ApricotTree("Apricot", "Apricot Sapling", new int[]{7, 7, 7, 7}, 28, true, 1, 59, true, 38, new Seasons[]{Seasons.SPRING}, true, false),
    CherryTree("Cherry", "Cherry Sapling", new int[]{7, 7, 7, 7}, 28, true, 1, 59, true, 38, new Seasons[]{Seasons.SPRING}, true, false),
    BananaTree("Banana", "Banana Sapling", new int[]{7, 7, 7, 7}, 28, true, 1, 150, true, 75, new Seasons[]{Seasons.SUMMER}, true, false),
    MangoTree("Mango", "Mango Sapling", new int[]{7, 7, 7, 7}, 28, true, 1, 130, true, 100, new Seasons[]{Seasons.SUMMER}, true, false),
    OrangeTree("Orange", "Orange Sapling", new int[]{7, 7, 7, 7}, 28, true, 1, 100, true, 38, new Seasons[]{Seasons.SUMMER}, true, false),
    PeachTree("Peach", "Peach Sapling", new int[]{7, 7, 7, 7}, 28, true, 1, 140, true, 38, new Seasons[]{Seasons.SUMMER}, true, false),
    AppleTree("Apple", "Apple Sapling", new int[]{7, 7, 7, 7}, 28, true, 1, 100, true, 38, new Seasons[]{Seasons.AUTUMN}, true, false),
    PomegranateTree("Pomegranate", "Pomegranate Sapling", new int[]{7, 7, 7, 7}, 28, true, 1, 140, true, 38, new Seasons[]{Seasons.AUTUMN}, true, false),
    OakTree("Oak Resin ", "Acorns", new int[]{7, 7, 7, 7}, 28, false, 7, 150, false, 0, new Seasons[]{}, true, false),
    MapleTree("Maple Syrup", "Maple Seeds", new int[]{7, 7, 7, 7}, 28, false, 9, 200, false, 0, new Seasons[]{}, true, false),
    PineTree("Pine Tar", "Pine Cones", new int[]{7, 7, 7, 7}, 28, false, 5, 100, false, 0, new Seasons[]{}, true, false),
    MahoganyTree("Sap", "Mahogany Seeds", new int[]{7, 7, 7, 7}, 28, true, 1, 2, true, -2, new Seasons[]{}, true, false),
    MushroomTree("Common Mushroom", "Mushroom Tree Seeds", new int[]{7, 7, 7, 7}, 28, true, 1, 40, true, 38, new Seasons[]{}, true, false),
    MysticTre("Mystic Syrup", "Mystic Tree Seeds", new int[]{7, 7, 7, 7}, 28, false, 7, 1000, true, 500, new Seasons[]{}, true, false),
    ;//other plants trees & etc ...

    private final String name;
    private final String seed;
    private final int[] stage;
    private final int totalHarvestTime;
    private final boolean oneTimeHarvest;
    private final int multipleHarvest;
    private final int baseSell;
    private final boolean edible;
    private final int energyGet;
    private final Seasons[] season;
    private final boolean harvestable;
    private final boolean giantable;

    PlantType(String name, String seed, int stage[],
              int totalHarvestTime, boolean oneTimeHarvest,
              int multipleHarvest, int baseSell,
              boolean edible, int energyGet, Seasons[] season,
              boolean harvestable, boolean giantable) {
        this.name = name;
        this.seed = seed;
        this.stage = stage;
        this.totalHarvestTime = totalHarvestTime;
        this.oneTimeHarvest = oneTimeHarvest;
        this.multipleHarvest = multipleHarvest;
        this.baseSell = baseSell;
        this.edible = edible;
        this.energyGet = energyGet;
        this.season = season;
        this.harvestable = harvestable;
        this.giantable = giantable;
    }

    public Plant createPlant() {
        return new Plant(this);
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

    public int getTotalHarvestTime() {
        return totalHarvestTime;
    }

    public boolean isOneTimeHarvest() {
        return oneTimeHarvest;
    }

    public int getMultipleHarvest() {
        return multipleHarvest;
    }

    public int getBaseSell() {
        return baseSell;
    }

    public boolean isEdible() {
        return edible;
    }

    public int getEnergyGet() {
        return energyGet;
    }

    public Seasons[] getSeason() {
        return season;
    }

    public boolean isHarvestable() {
        return harvestable;
    }

    public boolean isGiantable() {
        return giantable;
    }
}
