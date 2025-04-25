package org.example.models.Items;

import org.example.models.enums.Sources;

import java.util.HashMap;

public class CraftingItem extends Item {
    private String ingredients;
    private Sources source;

    public CraftingItem(String name, String ingredients, Sources source, int baseSellPrice) {
        super(name, baseSellPrice);
        this.ingredients = ingredients;
        this.source = source;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public Sources getSource() {
        return source;
    }

    public void setSource(Sources source) {
        this.source = source;
    }
}
