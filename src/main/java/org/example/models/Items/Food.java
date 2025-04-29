package org.example.models.Items;

public class Food extends Item {
    private int energy;
    public Food(String foodName , int baseSellPrice , int energy) {
        super(foodName, baseSellPrice);
        this.energy = energy;
    }

    public int getEnergy() {
        return energy;
    }
}
