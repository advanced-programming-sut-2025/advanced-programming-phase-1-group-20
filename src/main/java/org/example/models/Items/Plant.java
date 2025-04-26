package org.example.models.Items;

import org.example.models.enums.Seasons;

import java.util.Arrays;

public class Plant extends Item {
    private String seedName;
    private int[] stages;
    private int totalHarvestTime;
    private boolean oneTimeHarvest;
    private int regrowthTime;
    private boolean isEdible;
    private int energy;
    private Seasons[] season;
    private boolean isGiantable;
    private int stage;
    private int daysCounter;
    private boolean finished;
    public Plant(String name, int price , String seedName , int[] stage , int totalHarvestTime ,
                 boolean oneTimeHarvest , int regrowthTime , boolean isEdible , int energy ,
                 Seasons[] seasons , boolean isGiantable) {
        super(name, price);
        this.seedName = seedName;
        this.stages = stage;
        this.totalHarvestTime = totalHarvestTime;
        this.oneTimeHarvest = oneTimeHarvest;
        this.regrowthTime = regrowthTime;
        this.isEdible = isEdible;
        this.energy = energy;
        this.season = seasons;
        this.isGiantable = isGiantable;
        this.stage = 0;
        daysCounter = 0;
        finished = false;
    }

    public String getSeed() {
        return seedName;
    }

    public int[] getStages() {
        return stages;
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

    @Override
    public void showInfo() {
        System.out.println("Name: " + this.getName());
        System.out.println("Source: " + seedName);
        System.out.print("Stage: ");
        String stages = Arrays.toString(this.stages).
                replace("[", "").replace("]", "")
                .replace(" " , "");
        System.out.println("Stages: " + stages);
        System.out.println("Total Harvest Time: " + totalHarvestTime);
        System.out.println("One Time: " + oneTimeHarvest);
        System.out.print("Regrowth Time: ");
        if(regrowthTime > 0){
            System.out.println(regrowthTime);
        }else{
            System.out.println();
        }
        System.out.println("Base Sell Price: " + this.getPrice());
        System.out.println("Is Edible: " + isEdible);
        System.out.println("Energy: " + energy);
        String seasons = Arrays.toString(season)
                .replace("[", "").replace("]", "")
                .replace(" " , "");
        System.out.println("Seasons: " + seasons);
        System.out.println("Can Become Giant: " + isGiantable);
    }



    public void addStage(){
        if(stage < stages.length){
            stage++;
        }else if(stage == stages.length){
            finished = true;
        }
    }

    public void updateDaysCounter(){
        if(daysCounter < stages[stage]){
            daysCounter++;
        }else if(daysCounter == stages[stage]){
            addStage();
            daysCounter = 0;
        }
    }

    public int getStage(){
        return stage;
    }

    public void updatePlant(){
        if(!finished){
            updateDaysCounter();
        }
    }

    public boolean getFinished(){
        return finished;
    }
}
