package org.example.models.Items;


import java.util.HashMap;

public class CraftingItem extends Item {
    private String ingredients;
    private String source;

    public CraftingItem(String name, String ingredients, String source, int baseSellPrice) {
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public void showInfo(){
        System.out.println("Name: " + this.getName());
        System.out.println("Base Sell Price: " + this.getBaseSellPrice());
        System.out.println("Ingredients: " + ingredients);
        System.out.println("Source: " + source);
    }
}
