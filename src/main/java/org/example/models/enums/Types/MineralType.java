package org.example.models.enums.Types;

public enum MineralType {
    Quartz("Quartz", "A clear crystal commonly found in caves and mines.", 25),
    EarthCrystal("Earth Crystal", "A resinous substance found near the surface.", 50),
    FrozenTear("Frozen Tear", "A crystal fabled to be the frozen tears of a yeti.", 75),
    FireQuartz("Fire Quartz", "A glowing red crystal commonly found near hot lava.", 100),
    Emerald("Emerald", "A precious stone with a brilliant green color.", 250),
    Aquamarine("Aquamarine", "A shimmery blue-green gem.", 180),
    Ruby("Ruby", "A precious stone that is sought after for its rich color and beautiful luster.", 250),
    Amethyst("Amethyst", "A purple variant of quartz.", 100),
    Topaz("Topaz", "Fairly common but still prized for its beauty.", 80),
    Jade("Jade", "A pale green ornamental stone.", 200),
    Diamond("Diamond", "A rare and valuable gem.", 750),
    PrismaticShard("Prismatic Shard", "A very rare and powerful substance with unknown origins.", 2000),
    Copper("Copper", "A common ore that can be smelted into bars.", 5),
    Iron("Iron", "A fairly common ore that can be smelted into bars.", 10),
    Gold("Gold", "A precious ore that can be smelted into bars.", 25),
    Iridium("Iridium", "An exotic ore with many curious properties. Can be smelted into bars.", 100),
    Coal("Coal", "A combustible rock that is useful for crafting and smelting.", 15),
    ;
    private final String name;
    private final String description;
    private final int baseSellPrice;

    MineralType(String name, String description, int baseSellPrice) {
        this.name = name;
        this.description = description;
        this.baseSellPrice = baseSellPrice;
    }

    public static MineralType fromName(String name) {
        for (MineralType type : MineralType.values()) {
            if (type.getName().equals(name)) {
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

    public void showInfo() {
        System.out.println("Name: " + getName());
        System.out.println("Base Sell Price: " + getBaseSellPrice());
        System.out.println("Description: " + getDescription());
    }
}
