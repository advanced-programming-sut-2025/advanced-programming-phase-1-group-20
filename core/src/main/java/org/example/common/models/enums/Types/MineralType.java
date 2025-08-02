package org.example.common.models.enums.Types;

public enum MineralType {
    Quartz("Quartz", "A clear crystal commonly found in caves and mines.", 25, "Quartz") ,
    EarthCrystal("Earth Crystal", "A resinous substance found near the surface.", 50, "Earth_Crystal") ,
    FrozenTear("Frozen Tear", "A crystal fabled to be the frozen tears of a yeti.", 75, "Frozen_Tear") ,
    FireQuartz("Fire Quartz", "A glowing red crystal commonly found near hot lava.", 100, "Fire_Quartz") ,
    Emerald("Emerald", "A precious stone with a brilliant green color.", 250, "Emerald") ,
    Aquamarine("Aquamarine", "A shimmery blue-green gem.", 180, "Aquamarine") ,
     Ruby("Ruby", "A precious stone that is sought after for its rich color and beautiful luster.", 250, "Ruby") ,
    Amethyst("Amethyst", "A purple variant of quartz.", 100, "Amethyst") ,
    Topaz("Topaz", "Fairly common but still prized for its beauty.", 80, "Topaz") ,
    Jade("Jade", "A pale green ornamental stone.", 200, "Jade") ,
    Diamond("Diamond", "A rare and valuable gem.", 750, "Diamond") ,
    PrismaticShard("Prismatic Shard", "A very rare and powerful substance with unknown origins.", 2000, "Prismatic_Shard") ,
    Copper("Copper", "A common ore that can be smelted into bars.", 5, "Copper") ,
    Iron("Iron", "A fairly common ore that can be smelted into bars.", 10, "Iron") ,
    Gold("Gold", "A precious ore that can be smelted into bars.", 25, "Gold") ,
    Iridium("Iridium", "An exotic ore with many curious properties. Can be smelted into bars.", 100, "Iridium") ,
    Coal("Coal", "A combustible rock that is useful for crafting and smelting.", 15, "Coal") ,
    Wood("Wood" , "A sturdy, yet flexible plant material with a wide variety of uses." , 10, "Wood") ,
    Hardwood("Hardwood", "A dense, durable wood that's perfect for crafting high-quality items.", 25, "Hardwood") ,
    Wool("Wool", "Soft, fluffy fiber obtained from sheep. Used for crafting cloth.", 340, "Wool") ,
    Stone("Stone" , "A common material with many uses in crafting and building." , 15, "Stone") ,
    ;
    private final String name;
    private final String description;
    private final int baseSellPrice;
    private final String imageFilepath;

    MineralType(String name, String description, int baseSellPrice , String imageFilepath) {
        this.name = name;
        this.description = description;
        this.baseSellPrice = baseSellPrice;
        this.imageFilepath = imageFilepath;
    }

    public static MineralType fromName(String name) {
        for (MineralType type : MineralType.values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getBaseSellPrice() {
        return baseSellPrice;
    }

    public String getImageFilepath() {
        return imageFilepath;
    }

    public void showInfo() {
        System.out.println("Name: " + getName());
        System.out.println("Base Sell Price: " + getBaseSellPrice());
        System.out.println("Description: " + getDescription());
    }
}
