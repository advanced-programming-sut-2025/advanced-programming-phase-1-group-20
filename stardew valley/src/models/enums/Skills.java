package models.enums;

import models.Skill;

import java.util.List;

public enum Skills {
    HARVESTING("harvest" , 5),
    ;//other skills
    private final int adderUnit;
    private final String name;
    Skills(String name, int adderUnit) {
        this.name = name;
        this.adderUnit = adderUnit;
    }
    public void addSkill(List<Skill> skills){
        skills.add(new Skill(name,adderUnit));
    }
}
