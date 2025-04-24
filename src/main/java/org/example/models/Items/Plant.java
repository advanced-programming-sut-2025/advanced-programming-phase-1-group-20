package org.example.models.Items;

import org.example.models.enums.Seasons;

public class Plant extends Item {
    private Seed seed;
    private int[] stage;
    private int totalHarvestTime;
    private boolean oneTimeHarvest;
    private int regrowthTime;
    private boolean isEdible;
    private int energy;
    private Seasons[] season;
    private boolean isGiantable;
    public Plant(String name, int price , Seed seed , int[] stage , int totalHarvestTime ,
                 boolean oneTimeHarvest , int regrowthTime , boolean isEdible , int energy ,
                 Seasons[] seasons , boolean isGiantable) {
        super(name, price);
        this.seed = seed;
        this.stage = stage;
        this.totalHarvestTime = totalHarvestTime;
        this.oneTimeHarvest = oneTimeHarvest;
        this.regrowthTime = regrowthTime;
        this.isEdible = isEdible;
        this.energy = energy;
        this.season = seasons;
        this.isGiantable = isGiantable;
    }

    public Seed getSeed() {
        return seed;
    }

    public int[] getStage() {
        return stage;
    }

    public int getTotalHarvestTime() {
        return totalHarvestTime;
    }

    public boolean isOneTimeHarvest() {
        return oneTimeHarvest;
    }

    public int getRegrowthTime() {
        return regrowthTime;
    }

    public boolean isEdible() {
        return isEdible;
    }

    public int getEnergy() {
        return energy;
    }

    public Seasons[] getSeason() {
        return season;
    }

    public boolean isGiantable() {
        return isGiantable;
    }

    public int getPrice(){
        return super.getPrice();
    }

    public void setPrice(int price) {
        super.setPrice(price);
    }
}
