package org.example.models.Items;

import org.example.models.enums.Seasons;
import org.example.models.enums.Types.TreeType;

import java.util.Arrays;

public class Tree extends Item{
    private TreeType type;
    private int[] stages;
    private int stage;
    private int daysCounter;
    private boolean finished;
    public Tree(TreeType type) {
        super(type.getName(), type.getBaseSellPrice());
        this.type = type;
        stages = new int[]{7,7,7,7};
        this.stage = 0;
        this.daysCounter = 0;
        this.finished = false;
    }

    public String getSeed() {
        return type.getSource();
    }

    public String getFruitName() {
        return type.getFruit();
    }


    public boolean isEdible() {
        return type.isEdible();
    }


    public int getEnergy() {
        return type.getEnergy();
    }


    public Seasons[] getSeasons() {
        return type.getSeasons();
    }


    @Override
    public void showInfo(){
        System.out.println("Name: " + this.getName());
        System.out.println("Source: " + type.getSource());
        String stages = Arrays.toString(type.getSeasons()).replace("[", "").replace("]", "")
                .replace(" " , "");
        System.out.println("Stages: " + stages);
        System.out.println("Fruit: " + type.getFruit());
        System.out.println("Base Sell Price: " + this.getPrice());
        System.out.println("Is Edible: " + isEdible());
        System.out.println("Energy: " + getEnergy());
//        String season = Arrays.toString(seasons)
//                .replace("[", "").replace("]", "")
//                .replace(" " , "");
        System.out.println("Season: " + Arrays.toString(type.getSeasons()));
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

    public void updateTree(){
        if(!finished){
            updateDaysCounter();
        }
    }

    public boolean getFinished(){
        return finished;
    }

}
