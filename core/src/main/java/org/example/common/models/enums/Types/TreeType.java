package org.example.common.models.enums.Types;

import org.example.common.models.enums.Seasons;

import java.util.Arrays;

public enum TreeType {
    ApricotTree("Apricot Tree", "Apricot Sapling", "Apricot", 59, true, 38, new Seasons[]{Seasons.SPRING}, "Apricot") ,
    CherryTree("Cherry Tree", "Cherry Sapling", "Cherry", 80, true, 38, new Seasons[]{Seasons.SPRING}, "Cherry") ,
    BananaTree("Banana Tree", "Banana Sapling", "Banana", 150, true, 75, new Seasons[]{Seasons.SUMMER}, "Banana") ,
    MangoTree("Mango Tree", "Mango Sapling", "Mango", 130, true, 100, new Seasons[]{Seasons.SUMMER}, "Mango") ,
    OrangeTree("Orange Tree", "Orange Sapling", "Orange", 100, true, 38, new Seasons[]{Seasons.SUMMER}, "Orange") ,
    PeachTree("Peach Tree", "Peach Sapling", "Peach", 140, true, 38, new Seasons[]{Seasons.SUMMER}, "Peach") ,
    AppleTree("Apple Tree", "Apple Sapling", "Apple", 100, true, 38, new Seasons[]{Seasons.AUTUMN}, "Apple") ,
    PomegranateTree("Pomegranate Tree", "Pomegranate Sapling", "Pomegranate", 140, true, 38, new Seasons[]{Seasons.AUTUMN}, "Pomegranate") ,
    OakTree("Oak Tree", "Acorns", "Oak Resin", 150, false, 0, new Seasons[]{Seasons.SPRING, Seasons.SUMMER, Seasons.AUTUMN, Seasons.WINTER}, "Oak") ,
    MapleTree("Maple Tree", "Maple Seeds", "Maple Syrup", 200, false, 0, new Seasons[]{Seasons.SPRING, Seasons.SUMMER, Seasons.AUTUMN, Seasons.WINTER}, "Maple") ,
    PineTree("Pine Tree", "Pine Cones", "Pine Tar", 100, false, 0, new Seasons[]{Seasons.SPRING, Seasons.SUMMER, Seasons.AUTUMN, Seasons.WINTER}, "Pine") ,
    MahoganyTree("Mahogany Tree", "Mahogany Seeds", "Sap", 2, true, -2, new Seasons[]{Seasons.SPRING, Seasons.SUMMER, Seasons.AUTUMN, Seasons.WINTER}, "Mahogany") ,
    MushroomTree("Mushroom Tree", "Mushroom Tree Seeds", "Common Mushroom", 40, true, 38, new Seasons[]{Seasons.SPRING, Seasons.SUMMER, Seasons.AUTUMN, Seasons.WINTER}, "MushroomTree") ,
    MysticTree("Mystic Tree", "Mystic Tree Seeds", "Mystic Syrup", 1000, true, 500, new Seasons[]{Seasons.SPRING, Seasons.SUMMER, Seasons.AUTUMN, Seasons.WINTER}, "Mystic_Tree") ,
    ;
    private final String name;
    private final String source;
    private final String fruit;
    private final int baseSellPrice;
    private final boolean isEdible;
    private final int energy;
    private final Seasons[] seasons;
    private final String imageFilePath;

    TreeType(String name, String source, String fruit, int baseSellPrice, boolean isEdible,
             int energy, Seasons[] seasons , String imageFilePath) {
        this.name = name;
        this.source = source;
        this.fruit = fruit;
        this.baseSellPrice = baseSellPrice;
        this.isEdible = isEdible;
        this.energy = energy;
        this.seasons = seasons;
        this.imageFilePath = imageFilePath;
    }

    public static TreeType fromName(String name) {
        for (TreeType treeType : TreeType.values()) {
            if (treeType.getName().equals(name)) {
                return treeType;
            }
        }
        return null;
    }

    public static TreeType getType(String name) {
        for (TreeType type : TreeType.values()) {
            if (type.getName().equals(name)) return type;
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String getSource() {
        return source;
    }

    public String getFruit() {
        return fruit;
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

    public String getImageFilePath() {
        return imageFilePath;
    }

    public void showInfo() {
        System.out.println("Name: " + getName());
        System.out.println("Source: " + getSource());
        String stages = Arrays.toString(getSeasons()).replace("[", "").replace("]", "")
                .replace(" ", "");
        System.out.println("Stages: " + stages);
        System.out.println("Fruit: " + getFruit());
        System.out.println("Base Sell Price: " + getBaseSellPrice());
        System.out.println("Is Edible: " + isEdible());
        System.out.println("Energy: " + getEnergy());
//        String season = Arrays.toString(seasons)
//                .replace("[", "").replace("]", "")
//                .replace(" " , "");
        System.out.println("Season: " + Arrays.toString(getSeasons()));
    }
}
