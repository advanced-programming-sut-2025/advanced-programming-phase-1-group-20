package org.example.models.Player;

import org.example.models.Items.Item;

import java.util.HashMap;
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
    }

    public void showInventory() {
    }

}
