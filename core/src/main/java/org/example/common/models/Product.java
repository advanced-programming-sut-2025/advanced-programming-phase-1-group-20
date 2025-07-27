package org.example.common.models;

import org.example.common.models.Items.Item;

public class Product {
    private Item item;
    private double amount;
    public Product(Item item, double amount) {
        this.item = item;
        this.amount = amount;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
