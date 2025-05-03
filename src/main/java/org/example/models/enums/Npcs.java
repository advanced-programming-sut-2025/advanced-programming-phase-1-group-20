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
    ),

    ABIGAIL(
            "Abigail",
            Charactristic.HARD_WORKING,
            Jobs.STUDENT,
            new Location(15, 15, TileType.COBBLED_STONE),
            Arrays.asList("Stone", "Iron Ore", "Coffee"),
            "A spirited young woman with a love for adventure and the supernatural. She enjoys exploring caves and playing the flute."
    ),

    HARVEY(
            "Harvey",
            Charactristic.KIND,
            Jobs.ENGINEER,
            new Location(20, 20, TileType.COBBLED_STONE),
            Arrays.asList("Coffee", "Pickles", "Wine"),
            "The town's doctor who runs the local clinic. He's caring and concerned about everyone's health."
    ),

    LEAH(
            "Leah",
            Charactristic.HARD_WORKING,
            Jobs.STUDENT,
            new Location(25, 25, TileType.COBBLED_STONE),
            Arrays.asList("Salad", "Grape", "Wine"),
            "An artist who lives in a small cabin near the river. She loves nature and creates sculptures from foraged materials."
    ),

    ROBIN(
            "Robin",
            Charactristic.HARD_WORKING,
            Jobs.SELLER,
            new Location(30, 30, TileType.COBBLED_STONE),
            Arrays.asList("Spaghetti", "Wood", "Iron Bar"),
            "The local carpenter who runs the carpentry shop. She's skilled at building and loves working with wood."
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

    public String getName() {
        return name;
    }

    public Charactristic getCharacteristic() {
        return characteristic;
    }

    public Jobs getJob() {
        return job;
    }

    public Location getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getFavoriteItems() {
        return favoriteItems;
    }

    public static Npcs fromName(String name) {
        for (Npcs npc : values()) {
            if (npc.getName().equalsIgnoreCase(name)) {
                return npc;
            }
        }
        return null;
    }
}
