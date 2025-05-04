package org.example.models.Player;

public class Skill {
    private int units;
    private int maxLevel = 4;
    private int level = 1;
    private int adderUnit;
    private String name;

    public Skill(int level, String name, int adderUnit) {
        this.level = level;
        this.name = name;
        this.adderUnit = adderUnit;
    }

    public void updateUnit() {
        units += adderUnit;
    }

    public void updateLevel() {
    }

    public void maxSkill(int hour, int plus) {
    }


    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }
}
