package org.example.models.Items;

import org.example.models.enums.Seasons;
import org.example.models.enums.Types.PlantType;

import java.util.Arrays;

public class Plant extends Item {
    private PlantType type;
    private int stage;
    private int daysCounter;
    private boolean finished;
    public Plant(PlantType type) {
        super(type.getName(), type.getBaseSellPrice());
        this.type = type;
        this.stage = 0;
        daysCounter = 0;
        finished = false;
    }

    public String getSeed() {
        return type.getSeed();
    }

    public int[] getStages() {
        return type.getStage();
    }

    public int getTotalHarvestTime() {
        return type.getTotalHarvestTime();
    }

    public boolean getOneTimeHarvest() {
        return type.isOneTimeHarvest();
    }

    public int getRegrowthTime() {
        return type.getRegrowthTime();
    }

    public boolean isEdible() {
        return type.isEdible();
    }

    public int getEnergy() {
        return type.getEnergy();
    }

    public Seasons[] getSeason() {
        return type.getSeasons();
    }

    public boolean isGiantable() {
        return type.isGiantable();
    }

    public int getPrice(){
        return super.getPrice();
    }

    public void setPrice(int price) {
        super.setPrice(price);
    }

    @Override
    public void showInfo() {
        type.showInfo();
    }



    public void addStage(){
        if(stage < getStages().length){
            stage++;
        }else if(stage == getStages().length){
            finished = true;
        }
    }

    public void updateDaysCounter(){
        int[] stages = getStages();
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
