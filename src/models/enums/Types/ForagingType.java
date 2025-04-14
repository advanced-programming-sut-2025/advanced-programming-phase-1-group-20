package models.enums.Types;

import models.enums.Seasons;

public enum ForagingType {
    CommonMushroom("Common Mushroom" , "", Seasons.SPRING ,40 , 38 ,true),
    Daffodil("Daffodil"  ,"", Seasons.SPRING, 30, 0 ,true),
    Dandelion("Dandelion" , "",Seasons.SPRING ,40 , 25 ,true),
    Leek("Leek" , "", Seasons.SPRING,60 , 40 ,true),
    Morel("Morel" ,  "",Seasons.SPRING,150 , 20 ,true),
    Salmonberry("Salmonberry"  ,"",Seasons.SPRING ,5 , 25 ,true),
    SpringOnion("Spring Onion"  ,"",Seasons.SPRING , 8, 13 ,true),
    WildHorseradish("Wild Horseradish" , "",Seasons.SPRING , 50, 13 ,true),
    FiddleheadFern("Fiddlehead Fern" , "",Seasons.SUMMER , 90, 25 ,true),
    Grape("Grape" , "", Seasons.SUMMER, 80, 38 ,true),
    RedMushroom("Red Mushroom" ,  "",Seasons.SUMMER, 75, -50 ,true),
    SpiceBerry("Spice Berry" , "", Seasons.SUMMER, 80, 25 ,true),
    SweetPea("Sweet Pea" , "", Seasons.SUMMER, 50, 0 ,true),
    Blackberry("Blackberry" , "", Seasons.AUTUMN, 25, 25 ,true),
    Chanterelle("Chanterelle" ,  "",Seasons.AUTUMN, 160, 75 ,true),
    Hazelnut("Hazelnut",  "", Seasons.AUTUMN, 40, 38,true),
    PurpleMushroom("Purple Mushroom" , "",Seasons.AUTUMN , 90, 30 ,true),
    WildPlum("Wild Plum" , "", Seasons.AUTUMN, 80, 25 ,true),
    Crocus("Crocus"  ,"", Seasons.WINTER, 60, 0 ,true),
    CrystalFruit("Crystal Fruit" ,  "",Seasons.WINTER, 150, 63 ,true),
    Holly("Holly" , "", Seasons.WINTER, 80, 37 ,true),
    SnowYam("Snow Yam" ,"",Seasons.WINTER, 100, -30 ,true),
    WinterRoot("Winter Root" , "", Seasons.WINTER, 70, 25 ,true),
    Quartz("Quartz","A clear crystal commonly found in caves and mines." , null, 25 ,0 , false),
    EarthCrystal("Earth Crystal","A resinous substance found near the surface." , null,50 ,0 , false),
    FrozenTear("Frozen Tear" , "A crystal fabled to be the frozen tears of a yeti.", null, 75,0 , false),
    FireQuartz("Fire Quartz" , "A glowing red crystal commonly found near hot lava.", null,100 ,0 , false),
    Emerald("Emerald" , "A precious stone with a brilliant green color.", null, 250,0 , false),
    Aquamarine("Aquamarine" , "A shimmery blue-green gem.", null,180 ,0 , false),
    Ruby("Ruby" , "A precious stone that is sought after for its rich color and beautiful luster.", null, 250,0 , false),
    Amethyst("Amethyst" , "A purple variant of quartz.", null, 100,0 , false),
    Topaz("Topaz" , "Fairly common but still prized for its beauty.",null ,80 ,0 , false),
    Jade("Jade" , "A pale green ornamental stone.",null , 200,0 , false),
    Diamond("Diamond" , "A rare and valuable gem.",null , 750,0 , false),
    PrismaticShard("Prismatic Shard" , "A very rare and powerful substance with unknown origins.",null ,2000 ,0 , false),
    Copper("Copper" , "A common ore that can be smelted into bars.", null, 5,0 , false),
    Iron("Iron" , "A fairly common ore that can be smelted into bars.", null, 10,0 , false),
    Gold("Gold" , "A precious ore that can be smelted into bars.", null, 25,0 , false),
    Iriduim("Iriduim" , "An exotic ore with many curious properties. Can be smelted into bars.", null, 100,0 , false),
    Coal("Coal" , "A combustible rock that is useful for crafting and smelting.", null,15 ,0 , false),;
    private final String name;
    private final String description;
    private final Seasons season;
    private final int sellPrice;
    private final int energy;
    private final boolean isHarvestable;
    ForagingType(String name , String description, Seasons season, int sellPrice, int energy , boolean isHarvestable) {
        this.name = name;
        this.description = description;
        this.season = season;
        this.sellPrice = sellPrice;
        this.energy = energy;
        this.isHarvestable = isHarvestable;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Seasons getSeason() {
        return season;
    }

    public int getSellPrice() {
        return sellPrice;
    }

    public int getEnergy() {
        return energy;
    }

    public boolean isHarvestable() {
        return isHarvestable;
    }
}
