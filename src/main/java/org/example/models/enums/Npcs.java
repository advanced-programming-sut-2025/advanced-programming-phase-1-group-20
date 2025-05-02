package org.example.models.enums;

import org.example.models.common.Location;
import org.example.models.enums.Types.TileType;

import java.util.Arrays;
import java.util.List;

public enum Npcs {

    SEBASTIAN(
            "Sebastian",
            Charactristic.KIND,
            Jobs.ENGINEER,
            new Location(10, 10, TileType.COBBLED_STONE),
            Arrays.asList("Wool", "Pumpkin Pie", "Pizza"),
            "A reclusive young man who lives in his mom's basement. He's a programmer and enjoys solitude."
    );


    private final String name;
    private final Charactristic characteristic;
    private final Jobs job;
    private final Location location;
    private final String description;
    private final List<String> favoriteItems;

    Npcs(String name, Charactristic characteristic, Jobs job, Location location, List<String> favoriteItems, String description) {
        this.name = name;
        this.characteristic = characteristic;
        this.job = job;
        this.location = location;
        this.description = description;
        this.favoriteItems = favoriteItems;
    }
}
