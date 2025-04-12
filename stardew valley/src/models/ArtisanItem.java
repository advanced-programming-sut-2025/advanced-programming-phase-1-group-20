package models;

import java.util.HashMap;
import java.util.List;

public class ArtisanItem extends Item{
    private int energyCost;
    private int proccessingTime; // "hour input i think"
    private HashMap<Item , Integer> ingredients;
    private int sellPrice;
    public ArtisanItem(String name, HashMap<Item , Integer> ingredients , int energyCost , int proccessingTime , int sellPrice){
        super(name);
        this.proccessingTime = proccessingTime;
        this.energyCost = energyCost;
        this.ingredients = ingredients;
        this.sellPrice = sellPrice;
    }
}
