package org.example.models.Items;

import org.example.models.enums.Seasons;

import java.util.Arrays;

public class Tree extends Item{
    private String seedName;
    private String fruitName;
    private boolean isEdible;
    private int energy;
    private Seasons[] seasons;
    public Tree(String name, int baseSellPrice , String seedName, String fruitName,
                boolean isEdible, int energy , Seasons[] seasons) {
        super(name, baseSellPrice);
        this.seedName = seedName;
        this.fruitName = fruitName;
        this.isEdible = isEdible;
        this.energy = energy;
        this.seasons = seasons;
    }

    public String getSeed() {
        return seedName;
    }

    public void setSeed(String seed) {
        this.seedName = seed;
    }

    public String getFruitName() {
        return fruitName;
    }

    public void setFruitName(String fruitName) {
        this.fruitName = fruitName;
    }

    public boolean isEdible() {
        return isEdible;
    }

    public void setEdible(boolean edible) {
        isEdible = edible;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public Seasons[] getSeasons() {
        return seasons;
    }

    public void setSeasons(Seasons[] seasons) {
        this.seasons = seasons;
    }

    @Override
    public void showInfo(){
        System.out.println("Name: " + this.getName());
        System.out.println("Source: " + seedName);
        String stages = Arrays.toString(seasons).replace("[", "").replace("]", "")
                .replace(" " , "");
        System.out.println("Stages: " + stages);
        System.out.println("Fruit: " + fruitName);
        System.out.println("Base Sell Price: " + this.getPrice());
        System.out.println("Is Edible: " + this.isEdible);
        System.out.println("Energy: " + energy);
        String season = Arrays.toString(seasons)
                .replace("[", "").replace("]", "")
                .replace(" " , "");
        System.out.println("Season: " + season);
    }
}
