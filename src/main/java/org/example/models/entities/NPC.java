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
    private HashMap<Mob, Integer> friendShips;

    public NPC(Charactristic character, String name, Jobs jobs, HashMap<Integer, HashMap<Item, Integer>> missions) {
        this.character = character;
        this.name = name;
        this.jobs = jobs;
        this.missions = missions;
    }
}
