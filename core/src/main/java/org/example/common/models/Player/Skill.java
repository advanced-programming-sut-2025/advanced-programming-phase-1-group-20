package org.example.common.models.Player;

public class Skill {
    private int maxLevel = 4;
    private String name;
    private int adderUnit;
    private int units;
    private int level = 1;
    private boolean buff;
    private int hours;
    private int currentLevel;

    public Skill(int level, String name, int adderUnit) {
        this.level = level;
        this.name = name;
        this.adderUnit = adderUnit;
        this.units = 0;
        buff = false;
        hours = 0;
        currentLevel = level;
    }

    public Skill() {

    }

    public void updateUnit() {
        units += adderUnit;
        checkLevelUp();
    }

    public void addUnits(int additionalUnits) {
        units += additionalUnits;
        checkLevelUp();
    }

    private void checkLevelUp() {
        // Formula: i * 100 = units needed for level i + 50
        // So for level 1: 1 * 100 = 150 units needed
        // For level 2: 2 * 100 = 250 units needed
        // For level 3: 3 * 100 = 350 units needed
        // For level 4: 4 * 100 = 450 units needed

        int unitsNeededForNextLevel = (level + 1) * 100 + 50;

        while (units >= unitsNeededForNextLevel && level < maxLevel) {
            units -= unitsNeededForNextLevel;
            level++;

            // Recalculate for next level
            unitsNeededForNextLevel = (level + 1) * 100 + 50;
        }
    }

    public void updateLevel() {
        level++;
    }

    public void maxSkill(int hour, int plus) {
        currentLevel = level;
        buff = true;
        level = maxLevel;
        this.hours = hour;
    }

    public void updateState() {
        if (buff) {
            if (hours > 1) {
                hours--;
            } else {
                buff = false;
                level = currentLevel;
            }
        }
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getUnits() {
        return units;
    }

    public int getAdderUnit() {
        return adderUnit;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public boolean isBuff() {
        return buff;
    }

    public int getHours() {
        return hours;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public int getUnitsNeededForNextLevel() {
        if (level >= maxLevel) {
            return 0;
        }
        return (level + 1) * 100 + 50;
    }

    public float getProgressToNextLevel() {
        if (level >= maxLevel) {
            return 1.0f;
        }
        int unitsNeeded = getUnitsNeededForNextLevel();
        return (float) units / unitsNeeded;
    }
}
