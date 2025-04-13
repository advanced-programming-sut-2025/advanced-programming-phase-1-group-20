package models.enums;

import models.ArtisanItem;
import models.Item;

import java.util.HashMap;

public enum ArtisanItems {
    ,
    ;
    private final String name;
    private final int energyCost;
    private final int proccessingTime; // "hour input i think"
    private final HashMap<Item, Integer> ingredients;
    private final int sellPrice;
    ArtisanItems(String name , int energyCost, int proccessingTime, HashMap<Item, Integer> ingredients, int sellPrice) {
        this.name = name;
        this.energyCost = energyCost;
        this.proccessingTime = proccessingTime;
        this.ingredients = ingredients;
        this.sellPrice = sellPrice;
    }
    public ArtisanItem createArtisanItem() {
        return new ArtisanItem(name , ingredients , energyCost , proccessingTime , sellPrice);
    }
}
