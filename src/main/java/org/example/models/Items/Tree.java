package org.example.models.Items;

import org.example.models.enums.Seasons;
import org.example.models.enums.Types.MineralType;
import org.example.models.enums.Types.TreeType;

public class Tree extends Item {
    private TreeType type;
    private int[] stages;
    private int stage;
    private int daysCounter;
    private boolean finished;
    private boolean moisture;
    private int moistureCounter;

    public Tree(TreeType type) {
        super(type.getName(), type.getBaseSellPrice());
        this.type = type;
        stages = new int[]{7, 7, 7, 7};
        this.stage = 0;
        this.daysCounter = 0;
        this.finished = false;
        moisture = true;
        moistureCounter = 0;
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
    public void showInfo() {

    }

    public void addStage() {
        if (stage < stages.length) {
            stage++;
        } else if (stage == stages.length) {
            finished = true;
        }
    }

    public void updateDaysCounter() {
        if (daysCounter < stages[stage]) {
            if(!moisture){
                if(moistureCounter < 2){
                    moistureCounter++;
                }
            }
            daysCounter++;
        } else if (daysCounter == stages[stage]) {
            addStage();
            daysCounter = 0;
        }
    }

    public int getStage() {
        return stage;
    }

    @Override
    public void updateItem() {
        if (!finished) {
            updateDaysCounter();
        }
    }

    public boolean getFinished() {
        return finished;
    }

    public boolean getMoisture() {
        return moisture;
    }

    public void setMoisture(boolean moisture) {
        this.moisture = moisture;
    }

    public int getMoistureCounter() {
        return moistureCounter;
    }

    public void setMoistureCounter(int moistureCounter) {
        this.moistureCounter = moistureCounter;
    }


    public Item burnTree() {
        return new Mineral(MineralType.Coal);
    }

}
