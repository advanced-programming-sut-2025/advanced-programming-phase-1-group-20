package org.example.models.Player;

public class Skill {
    private int units;
    private final int maxLevel = 4;
    private int level = 1;
    private final int adderUnit;
    private final String name;
    private boolean buff;
    private int hours;

    public Skill(int level, String name, int adderUnit) {
        this.level = level;
        this.name = name;
        this.adderUnit = adderUnit;
        buff = false;
        hours = 0;
    }

    public void updateUnit() {
        units += adderUnit;
    }

    public void updateLevel() {
        level++;
    }

    public void maxSkill(int hour, int plus) {
        buff = true;
        level = maxLevel;
        this.hours = hour;
    }

    public void updateState(){
        if(buff){
            if(hours > 1){
                hours--;
            }else{
                buff = false;
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
