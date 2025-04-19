package org.example.models.enums;

import org.example.models.Item;
import org.example.models.Mob;
import org.example.models.NPC;

import java.util.HashMap;

public enum Npcs {
    ,
    ;
    private final Charactristic character;
    private final String name;
    private final Jobs jobs;
    private final HashMap<Integer, HashMap<Item, Integer>> missions;
    private final HashMap<Mob, Integer> friendShips;

    Npcs(Charactristic character, String name, Jobs jobs, HashMap<Integer, HashMap<Item, Integer>> missions, HashMap<Mob, Integer> friendShips) {
        this.character = character;
        this.name = name;
        this.jobs = jobs;
        this.missions = missions;
        this.friendShips = friendShips;
    }

    public NPC createNPC() {
        return new NPC(character, name, jobs, missions);
    }

}
