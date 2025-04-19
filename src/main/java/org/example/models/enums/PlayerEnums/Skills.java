package org.example.models.enums.PlayerEnums;

import org.example.models.Player.Skill;

public enum Skills {
    HARVESTING("harvest", 5),
    ;//other skills
    private final int adderUnit;
    private final String name;

    Skills(String name, int adderUnit) {
        this.name = name;
        this.adderUnit = adderUnit;
    }

    public Skill creatSkill(int level) {
        //this 3 getter constructor is for making arbitrary Skill for checking player skills during the game
        //ر.ک :
        // player.checkSkill()
        return new Skill(level, name, adderUnit);
    }

    public void addSkill(Skill skill) {

    }
}
