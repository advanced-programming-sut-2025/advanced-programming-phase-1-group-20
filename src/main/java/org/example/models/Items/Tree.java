package org.example.models.Items;

import org.example.models.enums.Seasons;
import org.example.models.enums.Types.MineralType;
import org.example.models.enums.Types.TreeType;

public class Tree extends Item {
    private TreeType type;
    private int[] stages;
    private int fruitCounter;
    private int fruitCycle;
    private boolean isFruitFinished;
    private int stage;
    private int daysCounter;
    private boolean finished;
    private boolean moisture;
    private int moistureCounter;
    private boolean moistureGod;

    public Tree(TreeType type) {
        super(type.getName(), type.getBaseSellPrice());
        this.type = type;
        stages = new int[]{7, 7, 7, 7};
        this.stage = 0;
        fruitCycle = 4;
        fruitCounter = fruitCycle;
        isFruitFinished = false;
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

    public int[] getStages() {
        return stages;
    }

    public void setDaysCounter(int daysCounter) {
        this.daysCounter = daysCounter;
    }

    @Override
    public void showInfo() {
        type.showInfo();
        System.out.println("is Moisture: " + moisture);
    }

    public void addStage() {
        if (stage < stages.length - 1) {
            stage++;
        } else if (stage == stages.length - 1) {
            finished = true;
            if (fruitCounter < fruitCycle) {
                fruitCounter++;
            } else if (fruitCounter == fruitCycle) {
                isFruitFinished = true;
            }
        }
    }

    public void updateDaysCounter() {
        if (daysCounter < stages[stage]) {
            if(!moistureGod){
                if (!moisture) {
                    if (moistureCounter < 2) {
                        moistureCounter++;
                    }
                }else{
                    moisture = false;
                    moistureCounter = 0;
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

    public void setStage(int stage) {
        this.stage = stage;
    }

    @Override
    public void updateItem() {
        updateDaysCounter();
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

    public boolean isFruitFinished() {
        return isFruitFinished;
    }

    public void setFruitFinished(boolean fruitFinished) {
        isFruitFinished = fruitFinished;
    }

    public int getFruitCycle() {
        return fruitCycle;
    }

    public void setFruitCycle(int fruitCycle) {
        this.fruitCycle = fruitCycle;
    }

    public int getFruitCounter() {
        return fruitCounter;
    }

    public void setFruitCounter(int fruitCounter) {
        this.fruitCounter = fruitCounter;
    }

    public Item burnTree() {
        return new Mineral(MineralType.Coal);
    }

    public Fruit getFruit() {
        if (isFruitFinished) {
            return new Fruit(getFruitName(), getPrice(), getEnergy());
        }
        return null;
    }

    public boolean isMoistureGod() {
        return moistureGod;
    }

    public void setMoistureGod(boolean moistureGod) {
        this.moistureGod = moistureGod;
    }
}
