package models;

import models.enums.Skills;
import models.enums.Sources;

import java.util.HashMap;
import java.util.List;

public class CraftingItem extends Item{
    private HashMap<Item , Integer> ingredients;
    private Sources source;
    private int sellPrice;
    public CraftingItem(String name , HashMap<Item , Integer> ingredients, Sources source, int sellPrice){
        super(name);
        this.ingredients = ingredients;
        this.source = source;
        this.sellPrice = sellPrice;
    }
}
