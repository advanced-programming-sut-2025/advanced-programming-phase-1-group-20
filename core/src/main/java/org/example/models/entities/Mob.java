package org.example.models.entities;

import java.util.HashMap;
import java.util.Map;

public class Mob {
    private Map<Mob, Friendship> friendships;

    public Mob() {
        friendships = new HashMap<>();
    }
}
