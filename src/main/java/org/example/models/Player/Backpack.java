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

    public boolean add(Item item, int quantity) {
        if (type == Type.Initial) {
            if (countItems() == 12) {
                return false;
            }
        } else if (type == Type.Big) {
            if (countItems() == 24) {
                return false;
            }
        }
        inventory.put(item, inventory.getOrDefault(item, 0) + quantity);
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

    public void remove(Item item, int amount) {
        if (inventory.containsKey(item)) {
            int currentAmount = inventory.get(item);
            if (currentAmount <= amount) {
                // Remove the item entirely if we're removing all or more than we have
                inventory.remove(item);
            } else {
                // Otherwise, reduce the amount
                inventory.put(item, currentAmount - amount);
            }
        }
    }

    public void showInventory() {
        System.out.println("~inventory~");
        for (Map.Entry<Item, Integer> entry : inventory.entrySet()) {
            Item item = entry.getKey();
            int quantity = entry.getValue();
            System.out.println(" " + item.getName() + ": " + quantity);
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

    public int getNumberOfItem(String name) {
        for(Item item : inventory.keySet()) {
            if (item.getName().equalsIgnoreCase(name)) {
                return inventory.get(item);
            }
        }
        return 0;

    }

    // Check if the inventory has all the items in the list
    public boolean hasItems(List<String> names) {
        for (String name : names) {
            if (!inventory.containsKey(getItem(name))) {
                return false;
            }
        }
        return true;
    }

    public void trashItem(Item item, int amount) {
        remove(item, amount);
    }

    public boolean isBackPackFull() {
        if (type == Type.Initial) {
            return countItems() >= 12;
        } else if (type == Type.Big) {
            return countItems() >= 24;
        } else if (type == Type.Deluxe) {
            return countItems() == Integer.MAX_VALUE; // Assuming Deluxe has 36 slots
        }
        return countItems() >= 12; // Default to Initial capacity
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public enum Type {
        Initial,
        Big,
        Deluxe
    }
}
