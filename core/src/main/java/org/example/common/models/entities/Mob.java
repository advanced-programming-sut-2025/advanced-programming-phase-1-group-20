package org.example.common.models.entities;

import java.util.HashMap;
import java.util.Map;

public class Mob {
    private Map<Mob, FriendShip> friendships;

    public Mob() {
        friendships = new HashMap<>();
    }
}
