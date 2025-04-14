package models;

import models.enums.Skills;

import java.util.HashMap;

public class CookingItem extends Item{
    private HashMap<Item , Integer> ingredients;
    private HashMap<Skills, Integer> buffer;
    private Skill source;
    private int sellPrice;
    public CookingItem(String name , HashMap<Item , Integer> ingredients, HashMap<Skills, Integer> buffer, Skill source, int sellPrice){
        super(name, );
        this.ingredients = ingredients;
        this.buffer = buffer;
        this.source = source;
        this.sellPrice = sellPrice;
    }
}
