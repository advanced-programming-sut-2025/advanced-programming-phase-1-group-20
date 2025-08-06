package org.example.common.models.enums.Types;

import org.example.common.models.enums.Seasons;

import java.util.Arrays;

public enum SeedType {
    AmaranthSeeds("Amaranth Seeds", new Seasons[]{Seasons.AUTUMN}, 0, "Amaranth_Seeds") ,
    AncientSeeds("Ancient Seeds", new Seasons[]{Seasons.SPRING, Seasons.SUMMER, Seasons.AUTUMN, Seasons.WINTER}, 0, "Ancient_Seeds") ,
    ArtichokeSeeds("Artichoke Seeds", new Seasons[]{Seasons.AUTUMN}, 0, "Artichoke_Seeds") ,
    BeanStarter("Bean Starter", new Seasons[]{Seasons.SPRING}, 0, "Bean_Starter") ,
    BeetSeeds("Beet Seeds", new Seasons[]{Seasons.AUTUMN}, 0, "Beet_Seeds") ,
    BlueberrySeeds("Blueberry Seeds", new Seasons[]{Seasons.SUMMER}, 0, "Blueberry_Seeds") ,
    BokChoySeeds("Bok Choy Seeds", new Seasons[]{Seasons.AUTUMN}, 0, "Bok_Choy_Seeds") ,
    BroccoliSeeds("Broccoli Seeds", new Seasons[]{Seasons.AUTUMN}, 0, "Broccoli_Seeds") ,
    CarrotSeeds("Carrot Seeds", new Seasons[]{Seasons.SPRING}, 0, "Carrot_Seeds") ,
    CauliflowerSeeds("Cauliflower Seeds", new Seasons[]{Seasons.SPRING}, 0, "Cauliflower_Seeds") ,
    CoffeeBean("Coffee Bean", new Seasons[]{Seasons.SPRING}, 0, "Coffee_Bean") ,
    CornSeeds("Corn Seeds", new Seasons[]{Seasons.SUMMER}, 0, "Corn_Seeds") ,
    CranberrySeeds("Cranberry Seeds", new Seasons[]{Seasons.AUTUMN}, 0, "Cranberry_Seeds") ,
    EggplantSeeds("Eggplant Seeds", new Seasons[]{Seasons.AUTUMN}, 0, "Eggplant_Seeds") ,
    FairySeeds("Fairy Seeds", new Seasons[]{Seasons.AUTUMN}, 0, "Fairy_Seeds") ,
    GarlicSeeds("Garlic Seeds", new Seasons[]{Seasons.SPRING}, 0, "Garlic_Seeds") ,
    GrapeStarter("Grape Starter", new Seasons[]{Seasons.AUTUMN}, 0, "Grape_Starter") ,
    HopsStarter("Hops Starter", new Seasons[]{Seasons.SUMMER}, 0, "Hops_Starter") ,
    JazzSeeds("Jazz Seeds", new Seasons[]{Seasons.SPRING}, 0, "Jazz_Seeds") ,
    KaleSeeds("Kale Seeds", new Seasons[]{Seasons.SPRING}, 0, "Kale_Seeds") ,
    MelonSeeds("Melon Seeds", new Seasons[]{Seasons.SUMMER}, 0, "Melon_Seeds") ,
    MixedSeeds("Mixed Seeds", new Seasons[]{Seasons.SPRING, Seasons.SUMMER, Seasons.AUTUMN, Seasons.WINTER}, 0, "Mixed_Seeds") ,
    ParsnipSeeds("Parsnip Seeds", new Seasons[]{Seasons.SPRING}, 0, "Parsnip_Seeds") ,
    PepperSeeds("Pepper Seeds", new Seasons[]{Seasons.SUMMER}, 0, "Pepper_Seeds") ,
    PoppySeeds("Poppy Seeds", new Seasons[]{Seasons.SUMMER}, 0, "Poppy_Seeds") ,
    PotatoSeeds("Potato Seeds", new Seasons[]{Seasons.SPRING}, 0, "Potato_Seeds") ,
    PowdermelonSeeds("Powdermelon Seeds", new Seasons[]{Seasons.WINTER}, 0, "Powdermelon_Seeds") ,
    PumpkinSeeds("Pumpkin Seeds", new Seasons[]{Seasons.AUTUMN}, 0, "Pumpkin_Seeds") ,
    RadishSeeds("Radish Seeds", new Seasons[]{Seasons.SUMMER}, 0, "Radish_Seeds") ,
    RareSeed("Rare Seed", new Seasons[]{Seasons.AUTUMN}, 0, "Rare_Seed") ,
    RedCabbageSeeds("Red Cabbage Seeds", new Seasons[]{Seasons.SUMMER}, 0, "Red_Cabbage_Seeds") ,
    RhubarbSeeds("Rhubarb Seeds", new Seasons[]{Seasons.SPRING}, 0, "Rhubarb_Seeds") ,
    RiceShoot("Rice Shoot", new Seasons[]{Seasons.SPRING}, 0, "Rice_Shoot") ,
    SpangleSeeds("Spangle Seeds", new Seasons[]{Seasons.SUMMER}, 0, "Spangle_Seeds") ,
    StarfruitSeeds("Starfruit Seeds", new Seasons[]{Seasons.SUMMER}, 0, "Starfruit_Seeds") ,
    StrawberrySeeds("Strawberry Seeds", new Seasons[]{Seasons.SPRING}, 0, "Strawberry_Seeds") ,
    SummerSquashSeeds("Summer Squash Seeds", new Seasons[]{Seasons.SUMMER}, 0, "Summer_Squash_Seeds") ,
    SunflowerSeeds("Sunflower Seeds", new Seasons[]{Seasons.SUMMER}, 0, "Sunflower_Seeds") ,
    TomatoSeeds("Tomato Seeds", new Seasons[]{Seasons.SUMMER}, 0, "Tomato_Seeds") ,
    TulipBulb("Tulip Bulb", new Seasons[]{Seasons.SPRING}, 0, "Tuilp_Bulb") ,
    WheatSeeds("Wheat Seeds", new Seasons[]{Seasons.SUMMER}, 0, "Wheat_Seeds") ,
    YamSeeds("Yam Seeds", new Seasons[]{Seasons.AUTUMN}, 0, "Yam_Seeds") ,

    ;
    private final String name;
    private final Seasons[] seasons;
    private final int baseSellPrice;
    private final String imageFilePath;

    SeedType(String name, Seasons[] seasons, int baseSellPrice , String imageFilePath) {
        this.name = name;
        this.seasons = seasons;
        this.baseSellPrice = baseSellPrice;
        this.imageFilePath = imageFilePath;
    }

    public static SeedType fromName(String name) {
        for (SeedType type : SeedType.values()) {
            if (type.getName().equalsIgnoreCase(name)) return type;
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

    public String getImageFilePath() {
        return imageFilePath;
    }

    public void showInfo() {
        System.out.println("Name: " + this.getName());
        System.out.println("Base Sell Price: " + this.getBaseSellPrice());
//        String seasons = Arrays.toString(season).replace("[", "").replace("]", "")
//                .replace(" " , "");
        System.out.println("Seasons: " + Arrays.toString(getSeasons()));
    }
}
