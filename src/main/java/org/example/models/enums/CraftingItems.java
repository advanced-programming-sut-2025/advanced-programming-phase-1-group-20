package org.example.models.enums;

import org.example.models.Item;
import org.example.models.Items.CraftingItem;

import java.util.HashMap;

public enum CraftingItems {
    ,
    ;
    private final String name;
    private final HashMap<Item, Integer> ingredients;
    private final Sources source;
    private final int sellPrice;

    CraftingItems(String name, HashMap<Item, Integer> ingredients, Sources source, int sellPrice) {
        this.name = name;
        this.ingredients = ingredients;
        this.source = source;
        this.sellPrice = sellPrice;
    }

    public CraftingItem createCraftingItem() {
        return new CraftingItem(name, ingredients, source, sellPrice);
    }

}
