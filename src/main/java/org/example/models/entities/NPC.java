package org.example.models.entities;

import org.example.models.Items.Item;
import org.example.models.enums.Charactristic;
import org.example.models.enums.Jobs;

import java.util.HashMap;

public class NPC extends Mob {
    private Charactristic character;
    private String name;
    private Jobs jobs;
    private HashMap<Integer, HashMap<Item, Integer>> missions;

    public NPC(Charactristic character, String name, Jobs jobs, HashMap<Integer, HashMap<Item, Integer>> missions) {
        super();
        this.character = character;
        this.name = name;
        this.jobs = jobs;
        this.missions = missions;
    }

    public Charactristic getCharacter() {
        return character;
    }

    public String getName() {
        return name;
    }

    public Jobs getJobs() {
        return jobs;
    }

    public HashMap<Integer, HashMap<Item, Integer>> getMissions() {
        return missions;
    }
}
