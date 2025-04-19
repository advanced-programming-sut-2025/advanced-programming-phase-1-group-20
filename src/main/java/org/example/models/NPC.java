package org.example.models;

import models.Item;
import models.Mob;
import models.enums.Charactristic;
import models.enums.Jobs;

import java.util.HashMap;

public class NPC extends Mob {
    private Charactristic character;
    private String name;
    private Jobs jobs;
    private HashMap<Integer, HashMap<Item, Integer>> missions;
    private HashMap<Mob, Integer> friendShips;

    public NPC(Charactristic character, String name, Jobs jobs, HashMap<Integer, HashMap<Item, Integer>> missions) {
        this.character = character;
        this.name = name;
        this.jobs = jobs;
        this.missions = missions;
    }
}
