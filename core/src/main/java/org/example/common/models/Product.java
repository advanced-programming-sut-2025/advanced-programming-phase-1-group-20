package org.example.common.models;

import org.example.common.models.Items.Item;
import org.example.common.models.Player.Backpack;
import org.example.common.models.enums.Ingredients;

public class Product {
    private Item item;
    private double amount;
    private Ingredients ingredient;
    public Product(Item item, double amount) {
        this.item = item;
        this.amount = amount;
        this.ingredient = Ingredients.NoSpecialItem;
    }

    public Product(Item item, double amount , Ingredients ingredient ) {
        this.item = item;
        this.amount = amount;
        this.ingredient = ingredient;
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

    public Ingredients getIngredient() {
        return ingredient;
    }

    public boolean checkIngredient(Backpack inventory) {
        return ingredient.checkRecipe(inventory);
    }
}
