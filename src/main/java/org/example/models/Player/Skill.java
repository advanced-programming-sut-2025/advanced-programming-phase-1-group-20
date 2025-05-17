package org.example.models.Player;

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
        buff = false;
        hours = 0;
        currentLevel = level;
    }

    public Skill() {

    }

    public void updateUnit() {
        units += adderUnit;
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
}
