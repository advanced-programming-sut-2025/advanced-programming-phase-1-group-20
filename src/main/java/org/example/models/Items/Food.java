package org.example.models.Items;

public class Food extends Item {
    private int energy;
    private String buffer;

    public Food(String foodName, int baseSellPrice, int energy , String buffer) {
        super(foodName, baseSellPrice);
        this.energy = energy;
        this.buffer = buffer;
    }

    public int getEnergy() {
        return energy;
    }
}
