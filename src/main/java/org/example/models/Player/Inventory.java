package org.example.models.Player;

import org.example.models.Items.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Inventory {
    private Map<Item, Integer> inventory;

    public Inventory() {
        inventory = new HashMap<>();
    }

    public void add(Item item) {
        inventory.put(item, inventory.getOrDefault(item, 0) + 1);
    }

    public Map<Item, Integer> getInventory() {
        return inventory;
    }

    public void remove(Item item) {
        //TODO: this must complete.
        
    }

    public void showInventory() {
        for (Map.Entry<Item, Integer> entry : inventory.entrySet()) {
            Item item = entry.getKey();
            int quantity = entry.getValue();
            System.out.println(item.getName() + ": " + quantity);
        }
    }

    public Item getItem(String name) {
        for (Item item : inventory.keySet()) {
            if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }

    // Check if the inventory has all the items in the list
    public boolean hasItems(List<String> names) {
        for (String name : names) {
            if (inventory.containsKey(getItem(name))) {
                continue;
            }
            return false;
        }
        return false;
    }
}
