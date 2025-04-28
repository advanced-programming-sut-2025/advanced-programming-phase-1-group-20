package org.example.models.Items;


import org.example.models.App;
import org.example.models.Player.Inventory;
import org.example.models.enums.Types.CraftingType;

import java.util.Map;

public class CraftingItem extends Item {
    private CraftingType type;

    public CraftingItem(CraftingType type) {
        super(type.getName(), type.getBaseSellPrice());
        this.type = type;
    }

    public String getIngredients() {
        return type.getIngredients();
    }


    public String getSource() {
        return type.getSource();
    }


    public boolean canCraft(Inventory inventory) {
        Map<Item, Integer> items = inventory.getInventory();
        String[] parts = type.getIngredients().split("\\+");
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
        System.out.println("Ingredients: " + getIngredients());
        System.out.println("Source: " + getSource());
    }
}
