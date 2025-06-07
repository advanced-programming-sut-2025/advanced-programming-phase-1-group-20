package org.example.models.enums.Types;

import org.example.models.enums.Seasons;

import java.util.Arrays;

public enum SeedType {
    JazzSeeds("Jazz Seeds", new Seasons[]{Seasons.SPRING}, 0),
    CarrotSeeds("Carrot Seeds", new Seasons[]{Seasons.SPRING}, 0),
    CauliflowerSeeds("Cauliflower Seeds", new Seasons[]{Seasons.SPRING}, 0),
    CoffeeBean("Coffee Bean", new Seasons[]{Seasons.SPRING}, 0),
    GarlicSeeds("Garlic Seeds", new Seasons[]{Seasons.SPRING}, 0),
    BeanStarter("Bean Starter", new Seasons[]{Seasons.SPRING}, 0),
    KaleSeeds("Kale Seeds", new Seasons[]{Seasons.SPRING}, 0),
    ParsnipSeeds("Parsnip Seeds", new Seasons[]{Seasons.SPRING}, 0),
    PotatoSeeds("Potato Seeds", new Seasons[]{Seasons.SPRING}, 0),
    RhubarbSeeds("Rhubarb Seeds", new Seasons[]{Seasons.SPRING}, 0),
    StrawberrySeeds("Strawberry Seeds", new Seasons[]{Seasons.SPRING}, 0),
    TulipBulb("Tulip Bulb", new Seasons[]{Seasons.SPRING}, 0),
    RiceShoot("Rice Shoot", new Seasons[]{Seasons.SPRING}, 0),
    BlueberrySeeds("Blueberry Seeds", new Seasons[]{Seasons.SUMMER}, 0),
    CornSeeds("Corn Seeds", new Seasons[]{Seasons.SUMMER}, 0),
    HopsStarter("Hops Starter", new Seasons[]{Seasons.SUMMER}, 0),
    PepperSeeds("Pepper Seeds", new Seasons[]{Seasons.SUMMER}, 0),
    MelonSeeds("Melon Seeds", new Seasons[]{Seasons.SUMMER}, 0),
    PoppySeeds("Poppy Seeds", new Seasons[]{Seasons.SUMMER}, 0),
    RadishSeeds("Radish Seeds", new Seasons[]{Seasons.SUMMER}, 0),
    RedCabbageSeeds("Red Cabbage Seeds", new Seasons[]{Seasons.SUMMER}, 0),
    StarfruitSeeds("Starfruit Seeds", new Seasons[]{Seasons.SUMMER}, 0),
    SpangleSeeds("Spangle Seeds", new Seasons[]{Seasons.SUMMER}, 0),
    SummerSquashSeeds("SUMMER Squash Seeds", new Seasons[]{Seasons.SUMMER}, 0),
    SunflowerSeeds("Sunflower Seeds", new Seasons[]{Seasons.SUMMER}, 0),
    TomatoSeeds("Tomato Seeds", new Seasons[]{Seasons.SUMMER}, 0),
    WheatSeeds("Wheat Seeds", new Seasons[]{Seasons.SUMMER}, 0),
    AmaranthSeeds("Amaranth Seeds", new Seasons[]{Seasons.AUTUMN}, 0),
    ArtichokeSeeds("Artichoke Seeds", new Seasons[]{Seasons.AUTUMN}, 0),
    BeetSeeds("Beet Seeds", new Seasons[]{Seasons.AUTUMN}, 0),
    BokChoySeeds("Bok Choy Seeds", new Seasons[]{Seasons.AUTUMN}, 0),
    BroccoliSeeds("Broccoli Seeds", new Seasons[]{Seasons.AUTUMN}, 0),
    CranberrySeeds("Cranberry Seeds", new Seasons[]{Seasons.AUTUMN}, 0),
    EggplantSeeds("Eggplant Seeds", new Seasons[]{Seasons.AUTUMN}, 0),
    FairySeeds("Fairy Seeds", new Seasons[]{Seasons.AUTUMN}, 0),
    GrapeStarter("Grape Starter", new Seasons[]{Seasons.AUTUMN}, 0),
    PumpkinSeeds("Pumpkin Seeds", new Seasons[]{Seasons.AUTUMN}, 0),
    YamSeeds("Yam Seeds", new Seasons[]{Seasons.AUTUMN}, 0),
    RareSeed("Rare Seed", new Seasons[]{Seasons.AUTUMN}, 0),
    PowdermelonSeeds("Powdermelon Seeds", new Seasons[]{Seasons.WINTER}, 0),
    AncientSeeds("Ancient Seeds", new Seasons[]{Seasons.SPRING, Seasons.SUMMER, Seasons.AUTUMN, Seasons.WINTER}, 0),
    MixedSeeds("Mixed Seeds", new Seasons[]{Seasons.SPRING, Seasons.SUMMER, Seasons.AUTUMN, Seasons.WINTER}, 0),
    ;
    private final String name;
    private final Seasons[] seasons;
    private final int baseSellPrice;

    SeedType(String name, Seasons[] seasons, int baseSellPrice) {
        this.name = name;
        this.seasons = seasons;
        this.baseSellPrice = baseSellPrice;
    }

    public static SeedType fromName(String name) {
        for (SeedType type : SeedType.values()) {
            if (type.getName().equals(name)) return type;
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

    public void showInfo() {
        System.out.println("Name: " + this.getName());
        System.out.println("Base Sell Price: " + this.getBaseSellPrice());
//        String seasons = Arrays.toString(season).replace("[", "").replace("]", "")
//                .replace(" " , "");
        System.out.println("Seasons: " + Arrays.toString(getSeasons()));
    }
}
