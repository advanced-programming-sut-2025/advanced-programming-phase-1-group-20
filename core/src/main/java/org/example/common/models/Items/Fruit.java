package org.example.common.models.Items;

public class Fruit extends Item {
    private int energy;
    public Fruit(String name , int price , int energy , String imageFilepath) {
        super(name , price , imageFilepath);
        this.energy = energy;
    }


    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }
}
