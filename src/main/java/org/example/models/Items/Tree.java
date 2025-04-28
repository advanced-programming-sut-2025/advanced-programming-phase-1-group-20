package org.example.models.Items;

import org.example.models.enums.Seasons;
import org.example.models.enums.Types.TreeType;

public class Tree extends Item {
    private TreeType type;
    private int[] stages;
    private int stage;
    private int daysCounter;
    private boolean finished;

    public Tree(TreeType type) {
        super(type.getName(), type.getBaseSellPrice());
        this.type = type;
        stages = new int[]{7, 7, 7, 7};
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
            daysCounter++;
        } else if (daysCounter == stages[stage]) {
            addStage();
            daysCounter = 0;
        }
    }

    public int getStage() {
        return stage;
    }

    public void updateTree() {
        if (!finished) {
            updateDaysCounter();
        }
    }

    public boolean getFinished() {
        return finished;
    }

}
