package models;

public class Skill {
    private int units;
    private int maxLevel = 4;
    private int level = 1;
    private int adderUnit;
    private String name;
    public Skill(String name , int adderUnit) {
        this.units = 0;
        this.level = 0;
        this.name = name;
        this.adderUnit = adderUnit;
    }

    public void updateUnit(){
        units+=adderUnit;
    }
    public void updateLevel(){}
}
