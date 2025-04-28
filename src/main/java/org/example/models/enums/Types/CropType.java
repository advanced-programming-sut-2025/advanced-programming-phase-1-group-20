package org.example.models.enums.Types;

import org.example.models.enums.Seasons;

public enum CropType {
    CommonMushroom("Common Mushroom" , new Seasons[]{Seasons.SPRING , Seasons.SUMMER , Seasons.AUTUMN , Seasons.WINTER} , 40 , 38),
    Daffodil("Daffodil", new Seasons[]{Seasons.SPRING}, 30, 0),
    Dandelion("Dandelion", new Seasons[]{Seasons.SPRING}, 40, 25),
    Leek("Leek", new Seasons[]{Seasons.SPRING}, 60, 40),
    Morel("Morel", new Seasons[]{Seasons.SPRING}, 150, 20),
    Salmonberry("Salmonberry", new Seasons[]{Seasons.SPRING}, 5, 25),
    SpringOnion("SPRING Onion", new Seasons[]{Seasons.SPRING}, 8, 13),
    WildHorseradish("Wild Horseradish", new Seasons[]{Seasons.SPRING}, 50, 13),
    FiddleheadFern("Fiddlehead Fern", new Seasons[]{Seasons.SUMMER}, 90, 25),
    Grape("Grape", new Seasons[]{Seasons.SUMMER}, 80, 38),
    RedMushroom("Red Mushroom", new Seasons[]{Seasons.SUMMER}, 75, -50),
    SpiceBerry("Spice Berry", new Seasons[]{Seasons.SUMMER}, 80, 25),
    SweetPea("Sweet Pea", new Seasons[]{Seasons.SUMMER}, 50, 0),
    Blackberry("Blackberry", new Seasons[]{Seasons.AUTUMN}, 25, 25),
    Chanterelle("Chanterelle", new Seasons[]{Seasons.AUTUMN}, 160, 75),
    Hazelnut("Hazelnut", new Seasons[]{Seasons.AUTUMN}, 40, 38),
    PurpleMushroom("Purple Mushroom", new Seasons[]{Seasons.AUTUMN}, 90, 30),
    WildPlum("Wild Plum", new Seasons[]{Seasons.AUTUMN}, 80, 25),
    Crocus("Crocus", new Seasons[]{Seasons.WINTER}, 60, 0),
    CrystalFruit("Crystal Fruit", new Seasons[]{Seasons.WINTER}, 150, 63),
    Holly("Holly", new Seasons[]{Seasons.WINTER}, 80, -37),
    SnowYam("Snow Yam", new Seasons[]{Seasons.WINTER}, 100, 30),
    WinterRoot("Winter Root", new Seasons[]{Seasons.WINTER}, 70, 25),
    ;

    private final String name;
    private final Seasons[] seasons;
    private final int baseSellPrice;
    private final int energy;

    CropType(String name, Seasons[] seasons, int baseSellPrice, int energy) {
        this.name = name;
        this.seasons = seasons;
        this.baseSellPrice = baseSellPrice;
        this.energy = energy;
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
}
