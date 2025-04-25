package org.example.models.Items;


import org.example.models.enums.Ingredients;
import org.example.models.enums.Seasons;

import java.util.Arrays;

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

    @Override
    public void showInfo() {
        System.out.println("Name: " + this.getName());
        System.out.println("Base Sell Price: " + this.getPrice());
        String seasons = Arrays.toString(season).replace("[", "").replace("]", "")
                .replace(" " , "");
        System.out.println("Seasons: " + seasons);
    }
}
