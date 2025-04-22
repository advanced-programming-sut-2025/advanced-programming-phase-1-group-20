package org.example.models.entities;

import java.util.HashMap;

public class Mob {
    private HashMap<Mob, Integer> friendShip;

    public Mob() {
        friendShip = new HashMap<Mob, Integer>();
    }
}
