package org.example.common.models.Items;

public class Fruit extends Item {
    private int energy;
    private String image;
    public Fruit(String name , int price , int energy , String imageFilepath) {
        super(name , price , imageFilepath);
        image = imageFilepath;
        this.energy = energy;
    }


    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    @Override
    public String getImageFilepath() {
        return "content/Trees/" + image + ".png";
    }
}
