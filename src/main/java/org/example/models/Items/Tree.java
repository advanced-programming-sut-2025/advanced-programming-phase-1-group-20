package org.example.models.Items;

import org.example.models.enums.Seasons;

public class Tree extends Item{
    private Seed seed;
    private String fruitName;
    private boolean isEdible;
    private int energy;
    private Seasons[] seasons;
    public Tree(String name, int baseSellPrice , Seed seed, String fruitName,
                boolean isEdible, int energy , Seasons[] seasons) {
        super(name, baseSellPrice);
        this.seed = seed;
        this.fruitName = fruitName;
        this.isEdible = isEdible;
        this.energy = energy;
        this.seasons = seasons;
    }

    public Seed getSeed() {
        return seed;
    }

    public void setSeed(Seed seed) {
        this.seed = seed;
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
}
