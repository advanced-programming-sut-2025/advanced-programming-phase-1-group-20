package org.example.models.Player;

import org.example.models.Items.Item;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Refrigerator {
    private Map<Item , Integer> items;

    public Refrigerator() {
        this.items = new HashMap<>();
    }

    public Map<Item , Integer> getItems() {
        return items;
    }

    public void putItem(Item item , int amount) {
        items.putIfAbsent(item, items.getOrDefault(item, 0) + amount);
    }

    public Item pickItem(Item item) {
        for(Map.Entry<Item , Integer> entry : items.entrySet()) {
            if(entry.getKey().equals(item)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public boolean contains(Item item) {
        for(Map.Entry<Item , Integer> entry : items.entrySet()) {
            if(entry.getKey().equals(item)) {
                return true;
            }
        }
        return false;
    }

    public Item getItemByNameOrError(String itemName) {
        for(Map.Entry<Item , Integer> entry : items.entrySet()) {
            if(entry.getKey().getName().equals(itemName)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void removeItem(Item item , int amount) {
        if (items.containsKey(item)) {
            int currentAmount = items.get(item);
            if (currentAmount <= amount) {
                // Remove the item entirely if we're removing all or more than we have
                items.remove(item);
            } else {
                // Otherwise, reduce the amount
                items.put(item, currentAmount - amount);
            }
        }
    }

}

