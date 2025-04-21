package org.example.models.enums.Types;

import org.example.models.enums.Seasons;

public enum SeedType {
    Jazz("Jazz Seeds" , Seasons.SPRING , 0),
    Carrot("Carrot Seeds" , Seasons.SPRING , 0),
    Cauliflower("Cauliflower Seeds" , Seasons.SPRING , 0),
    CoffeeBean("Coffee Bean" , Seasons.SPRING , 0),
    Garlic("Garlic Seeds" , Seasons.SPRING , 0),
    BeanStarter("Bean Starter" ,Seasons.SPRING , 0),
    Kale("Kale Seeds" , Seasons.SPRING , 0),
    Parsnip("Parsnip Seeds" , Seasons.SPRING , 0),
    Potato("Potato Seeds" , Seasons.SPRING , 0),
    Rhubarb("Rhubarb Seeds" , Seasons.SPRING , 0),
    Strawberry("Strawberry Seeds" , Seasons.SPRING , 0),
    TulipBulb("Tulip Bulb" , Seasons.SPRING , 0),
    RiceShoot("Rice Shoot" , Seasons.SPRING , 0),
    Blueberry("Blueberry Seeds" , Seasons.SUMMER , 0),
    Corn("Corn Seeds" , Seasons.SUMMER , 0),
    HopsStarter("Hops Starter" , Seasons.SUMMER , 0),
    Pepper("Pepper Seeds" , Seasons.SUMMER , 0),
    Melon("Melon Seeds" , Seasons.SUMMER , 0),
    Poppy("Poppy Seeds" , Seasons.SUMMER , 0),
    Radish("Radish Seeds" , Seasons.SUMMER , 0),
    RedCabbage("Red Cabbage Seeds" , Seasons.SUMMER , 0),
    Starfruit("Starfruit Seeds" , Seasons.SUMMER , 0),
    Spangle("Spangle Seeds" , Seasons.SUMMER , 0),
    SummerSquash("Summer Squash" , Seasons.SUMMER , 0),
    Sunflower("Sunflower Seeds" , Seasons.SUMMER , 0),
    Tomato("Tomato Seeds" , Seasons.SUMMER , 0),
    Wheat("Wheat Seeds" , Seasons.SUMMER , 0),
    Amaranth("Amaranth Seeds" , Seasons.AUTUMN , 0),
    Artichoke("Artichoke Seeds" , Seasons.AUTUMN , 0),
    Beet("Beet Seeds" , Seasons.AUTUMN , 0),
    BokChoy("Bok Choy Seeds" , Seasons.AUTUMN , 0),
    Broccoli("Broccoli Seeds" , Seasons.AUTUMN, 0),
    Cranberry("Cranberry Seeds" , Seasons.AUTUMN, 0),
    Eggplant("Eggplant Seeds" , Seasons.AUTUMN, 0),
    Fairy("Fairy Seeds" , Seasons.AUTUMN, 0),
    GrapeStarter("Grape Starter" , Seasons.AUTUMN, 0),
    Pumpkin("Pumpkin Seeds" , Seasons.AUTUMN, 0),
    Yam("Yam Seeds" , Seasons.AUTUMN, 0),
    Rare("Rare Seeds" , Seasons.AUTUMN, 0),
    PowderMelon("Powder melon Seeds" , Seasons.AUTUMN, 0),
    Ancient("Ancient Seeds" , Seasons.AUTUMN, 0),
    Mixed("Mixed Seeds" , Seasons.AUTUMN, 0),
    ;
    private final String name;
    private final Seasons season;
    private final int price;
    SeedType(String name, Seasons season, int price) {
        this.name = name;
        this.season = season;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public Seasons getSeason() {
        return season;
    }
}
