package org.example.models.Items;

import org.example.models.enums.Sources;

import java.util.HashMap;

public class CraftingItem extends Item {
    private HashMap<Item, Integer> ingredients;
    private Sources source;
    private int sellPrice;

    public CraftingItem(String name, HashMap<Item, Integer> ingredients, Sources source, int sellPrice) {
        super(name, sellPrice);
        this.ingredients = ingredients;
        this.source = source;
        this.sellPrice = sellPrice;
    }
}
