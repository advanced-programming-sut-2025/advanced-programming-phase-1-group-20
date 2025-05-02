package org.example.models.Player;

import org.example.models.Items.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Backpack {
    private Map<Item, Integer> inventory;

    private Type type;

    public Backpack() {
        inventory = new HashMap<>();
    }

    public boolean add(Item item) {
        if (type == Type.Initial) {
            if (countItems() == 12) {
                return false;
            }
        } else if (type == Type.Big) {
            if (countItems() == 24) {
                return false;
            }
        }
        inventory.put(item, inventory.getOrDefault(item, 0) + 1);
        return true;
    }

    public int countItems() {
        int count = 0;
        for (Item item : inventory.keySet()) {
            count += inventory.get(item);
        }
        return count;
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

    public void trashItem(Item item, int amount) {
        this.inventory.remove(item, amount);

    }

    public boolean isBackPackFull() {
        if (type == Type.Initial) {
            if (countItems() == 12) {
                return false;
            }
        } else if (type == Type.Big) {
            if (countItems() == 24) {
                return false;
            }
        }
        return true;
    }

    enum Type {
        Initial,
        Big,
        Deluxe
    }
}
