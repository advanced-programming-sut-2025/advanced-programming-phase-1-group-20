package org.example.models.Items;


import org.example.models.enums.Ingredients;
import org.example.models.enums.Seasons;

public class Seed extends Item {
    private Seasons[] season;

    public Seed(String name, Seasons[] season, int price) {
        super(name, price);
        this.season = season;
    }

    public Seasons[] getSeason() {
        return season;
    }

    public int getPrice(){
        return super.getPrice();
    }

    public void setPrice(int price) {
        super.setPrice(price);
    }
}
