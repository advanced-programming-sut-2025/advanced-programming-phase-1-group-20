package org.example.models.Items;

import org.example.models.App;
import org.example.models.Player.Backpack;
import org.example.models.enums.Types.CookingType;
import org.example.models.enums.Types.ItemBuilder;

import java.util.Map;

public class CookingItem extends Item {
    private CookingType type;

    public CookingItem(CookingType type) {
        super(type.getName(), type.getBaseSellPrice());
        this.type = type;
    }

    public String getIngredients() {
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
        String[] parts = type.getIngredient().split("\\+");
        for (String part : parts) {
            part = part.trim();
            String[] itemData = part.split(" ", 2);

            int requiredItem = Integer.parseInt(itemData[0]);
            String itemName = itemData[1];
            if (itemName.startsWith("any")) {
                itemName = itemName.replace("any ", "");
                //checking fishes list (only time that this happens)
            }
            else {
                itemName = itemName.trim();
                Item item = App.getItem(itemName);
                if (!items.containsKey(item) || requiredItem > items.get(item)) {
                    return false;
                }
            }
        }
        return true;
    }

    public Food cook(Backpack inventory) {
        ItemBuilder builder = new ItemBuilder();
        Map<Item, Integer> items = inventory.getInventory();
        String[] parts = type.getIngredient().split("\\+");
        for (String part : parts) {
            part = part.trim();
            String[] itemData = part.split(" ", 2);
            int requiredItem = Integer.parseInt(itemData[0]);
            String itemName = itemData[1];
            if (itemName.startsWith("any")) {
                itemName = itemName.replace("any ", "");
                //checking fishes list (only time that this happens)

                //TODO : removing items from inventory
            }
            else {
                itemName = itemName.trim();
                Item item = ItemBuilder.build(itemName);
                inventory.remove(item, 1);
            }
        }
        return new Food(getName(), getBaseSellPrice(), getEnergy());
    }


    public void showInfo() {
        type.showInfo();
    }
}
