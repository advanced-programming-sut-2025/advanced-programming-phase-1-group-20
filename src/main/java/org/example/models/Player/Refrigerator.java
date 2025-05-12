package org.example.models.Player;

import org.example.models.Items.Item;
import java.util.ArrayList;
import java.util.List;

public class Refrigerator {
    private List<Item> items;

    public Refrigerator() {
        this.items = new ArrayList<>();
    }

    public List<Item> getItems() {
        return items;
    }

    public void putItem(Item item) {
        items.add(item);
    }

    public Item pickItem(Item item) {
        for (Item i : items) {
            if (i.equals(item)) {
                items.remove(i);
                return i;
            }
        }
        return null;
    }

    public boolean contains(Item item) {
        return items.contains(item);
    }

    public Item getItemByNameOrError(String itemName) {
        for (Item item : items) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                return item;
            }
        }
        return null;
    }

}

