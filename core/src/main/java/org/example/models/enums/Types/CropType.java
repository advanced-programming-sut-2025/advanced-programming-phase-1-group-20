package org.example.models.enums.Types;

import org.example.models.enums.Seasons;

import java.util.Arrays;

public enum CropType {
    CommonMushroom("Common Mushroom", new Seasons[]{Seasons.SPRING, Seasons.SUMMER, Seasons.AUTUMN, Seasons.WINTER}, 40, 38, "Common_Mushroom") ,
    Daffodil("Daffodil", new Seasons[]{Seasons.SPRING}, 30, 0, "Daffodil") ,
    Dandelion("Dandelion", new Seasons[]{Seasons.SPRING}, 40, 25, "Dandelion") ,
    Leek("Leek", new Seasons[]{Seasons.SPRING}, 60, 40, "Leek") ,
    Morel("Morel", new Seasons[]{Seasons.SPRING}, 150, 20, "Morel") ,
    Salmonberry("Salmonberry", new Seasons[]{Seasons.SPRING}, 5, 25, "Salmonberry") ,
    SpringOnion("SPRING Onion", new Seasons[]{Seasons.SPRING}, 8, 13, "Spring_Onion") ,
    WildHorseradish("Wild Horseradish", new Seasons[]{Seasons.SPRING}, 50, 13, "Wild_Horseradish") ,
    FiddleheadFern("Fiddlehead Fern", new Seasons[]{Seasons.SUMMER}, 90, 25, "Fiddlehead_Fern") ,
    Grape("Grape", new Seasons[]{Seasons.SUMMER}, 80, 38, "Grape") ,
    RedMushroom("Red Mushroom", new Seasons[]{Seasons.SUMMER}, 75, -50, "Red_Mushroom") ,
    SpiceBerry("Spice Berry", new Seasons[]{Seasons.SUMMER}, 80, 25, "Spice_Berry") ,
    SweetPea("Sweet Pea", new Seasons[]{Seasons.SUMMER}, 50, 0, "Sweet_Pea") ,
    Blackberry("Blackberry", new Seasons[]{Seasons.AUTUMN}, 25, 25, "Blackberry") ,
    Chanterelle("Chanterelle", new Seasons[]{Seasons.AUTUMN}, 160, 75, "Chanterelle") ,
    Hazelnut("Hazelnut", new Seasons[]{Seasons.AUTUMN}, 40, 38, "Hazelnut") ,
    PurpleMushroom("Purple Mushroom", new Seasons[]{Seasons.AUTUMN}, 90, 30, "Purple_Mushroom") ,
    WildPlum("Wild Plum", new Seasons[]{Seasons.AUTUMN}, 80, 25, "Wild_Plum") ,
    Crocus("Crocus", new Seasons[]{Seasons.WINTER}, 60, 0, "Crocus") ,
    CrystalFruit("Crystal Fruit", new Seasons[]{Seasons.WINTER}, 150, 63, "Crystal_Fruit") ,
    Holly("Holly", new Seasons[]{Seasons.WINTER}, 80, -37, "Holly") ,
    SnowYam("Snow Yam", new Seasons[]{Seasons.WINTER}, 100, 30, "Snow_Yam") ,
    WinterRoot("Winter Root", new Seasons[]{Seasons.WINTER}, 70, 25, "Winter_Root") ,
    ;

    private final String name;
    private final Seasons[] seasons;
    private final int baseSellPrice;
    private final int energy;
    private final String imageFilePath;

    CropType(String name, Seasons[] seasons, int baseSellPrice, int energy , String imageFilePath) {
        this.name = name;
        this.seasons = seasons;
        this.baseSellPrice = baseSellPrice;
        this.energy = energy;
        this.imageFilePath = imageFilePath;
    }

    public static CropType fromName(String name) {
        for (CropType cropType : CropType.values()) {
            if (cropType.getName().equals(name)) {
                return cropType;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public Seasons[] getSeasons() {
        return seasons;
    }

    public int getBaseSellPrice() {
        return baseSellPrice;
    }

    public int getEnergy() {
        return energy;
    }

    public String getImageFilePath() {
        return imageFilePath;
    }

    public void showInfo() {
        System.out.println("Name: " + getName());
        System.out.println("Base Sell Price: " + getBaseSellPrice());
        String season = Arrays.toString(getSeasons()).replace("[", "").replace("]", "")
                .replace(" ", "");
        System.out.println("Season: " + season);
        System.out.println("Energy: " + getEnergy());
    }
}
