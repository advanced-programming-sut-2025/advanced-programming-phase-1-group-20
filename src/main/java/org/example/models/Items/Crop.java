package org.example.models.Items;

import org.example.models.enums.Seasons;

import java.util.Arrays;

public class Crop extends Item {
    private Seasons[] seasons;
    private int energy;
    public Crop(String name, int baseSellPrice , Seasons[] seasons, int energy) {
        super(name, baseSellPrice);
        this.seasons = seasons;
        this.energy = energy;
    }

    public Seasons[] getSeasons() {
        return seasons;
    }

    public void setSeasons(Seasons[] seasons) {
        this.seasons = seasons;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    @Override
    public void showInfo(){
        System.out.println("Name: " + this.getName());
        System.out.println("Base Sell Price: " + this.getBaseSellPrice());
        String season = Arrays.toString(seasons).replace("[", "").replace("]", "")
                .replace(" " , "");
        System.out.println("Season: " + season);
        System.out.println("Energy: " + energy);
    }
}
