package org.example.models.Items;

import org.example.models.App;
import org.example.models.Player.Backpack;
import org.example.models.entities.animal.Fish;
import org.example.models.enums.Ingredients;
import org.example.models.enums.Types.CookingType;
import org.example.models.enums.Types.ItemBuilder;

import java.util.List;
import java.util.Map;

public class CookingItem extends Item {
    private CookingType type;

    public CookingItem(CookingType type) {
        super(type.getName(), type.getBaseSellPrice());
        this.type = type;
    }

    public Ingredients getIngredients() {
        return type.getIngredient();
    }

    public int getEnergy() {
        return type.getEnergy();
    }


    public String getBuffer() {
        return type.getBuffer();
    }


    public String getSource() {
        return type.getSource();
    }


    public boolean canCook(Backpack inventory) {
        Map<Item, Integer> items = inventory.getInventory();
        String[] parts = new String[]{type.getIngredient().toString()};
        System.out.println(parts);
        for (String part : parts) {
            part = part.trim();
            String[] itemData = part.split(" ", 2);

            int requiredItem = Integer.parseInt(itemData[0]);
            String itemName = itemData[1];
            if (itemName.startsWith("any")) {
                itemName = itemName.replace("any ", "");
                Item item = ItemBuilder.build(itemName);
                if(!(item instanceof Fish)) {
                    return false;
                }
                if(inventory.getItem(itemName) == null || requiredItem > inventory.getNumberOfItem(itemName)) {
                    return false;
                }
            } else {
                itemName = itemName.trim();
                if (inventory.getItem(itemName) == null || requiredItem > inventory.getNumberOfItem(itemName)) {
                    return false;
                }
                items.putIfAbsent(inventory.getItem(itemName), requiredItem);
            }
        }
        for(Item item : items.keySet()) {
            inventory.remove(item , items.get(item));
        }
        return true;
    }

    public Food cook(Backpack inventory) {
        return new Food(getName(), getBaseSellPrice(), getEnergy() , getBuffer());
    }

    public Food getFood(){
        return new Food(getName(), getBaseSellPrice(), getEnergy() , getBuffer());
    }


    public void showInfo() {
        type.showInfo();
    }
}
