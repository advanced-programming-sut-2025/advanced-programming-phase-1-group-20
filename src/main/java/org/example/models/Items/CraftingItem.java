package org.example.models.Items;


import org.example.models.App;
import org.example.models.Player.Inventory;

import java.util.HashMap;
import java.util.Map;

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

    public boolean canCraft(Inventory inventory) {
        Map<Item, Integer> items = inventory.getInventory();
        String[] parts = ingredients.split("\\+");
        for(String part : parts) {
            part = part.trim();
            String[] itemData = part.split(" " , 2);

            int requiredItem = Integer.parseInt(itemData[0]);
            String itemName = itemData[1];
            itemName = itemName.trim();
            Item item = App.getItem(itemName);
            if(!items.containsKey(item) || requiredItem > items.get(item)) {
                return false;
            }
        }
        return true;
    }


    @Override
    public void showInfo(){
        System.out.println("Name: " + this.getName());
        System.out.println("Base Sell Price: " + this.getBaseSellPrice());
        System.out.println("Ingredients: " + ingredients);
        System.out.println("Source: " + source);
    }
}
