package org.example.models.Items;

public class Fruit extends Item {
    private int energy;
    public Fruit(String name , int price , int energy) {
        super(name , price);
        this.energy = energy;
    }


    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }
}
