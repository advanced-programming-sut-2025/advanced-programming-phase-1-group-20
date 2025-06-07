package org.example.models.enums;

import org.example.models.common.Location;
import org.example.models.enums.Types.TileType;

import java.util.Arrays;
import java.util.List;

public enum Npcs {
    SEBASTIAN(
            "Sebastian",
            Charactristic.LAZY,
            Jobs.ENGINEER,
            new Location(10, 10, TileType.VILLAGE),
            Arrays.asList("Wool", "Pumpkin Pie", "Pizza"),
            "A reclusive young man who lives in his mom's basement. He's a programmer and enjoys solitude. " +
                    "Despite his talent, he prefers to work at his own pace and often stays up late coding while sleeping in until noon. " +
                    "He dreams of moving to the city someday but lacks the motivation to make concrete plans."
    ),

    ABIGAIL(
            "Abigail",
            Charactristic.HARD_WORKING,
            Jobs.STUDENT,
            new Location(15, 15, TileType.VILLAGE),
            Arrays.asList("Stone", "Iron Ore", "Coffee"),
            "A spirited young woman with a love for adventure and the supernatural. She enjoys exploring caves and playing the flute. " +
                    "When she's not studying, she's practicing sword fighting or searching for rare minerals in the mines. " +
                    "Her parents worry about her unconventional interests, but her determination is unmatched."
    ),

    HARVEY(
            "Harvey",
            Charactristic.KIND,
            Jobs.ENGINEER,
            new Location(20, 20, TileType.VILLAGE),
            Arrays.asList("Coffee", "Pickles", "Wine"),
            "The town's doctor who runs the local clinic. He's caring and concerned about everyone's health. " +
                    "In his spare time, he builds model airplanes and dreams of flying, though he's afraid of heights. " +
                    "He moved to the valley to help the small community, often providing free care to those who can't afford it."
    ),

    LEAH(
            "Leah",
            Charactristic.JEALOUS,
            Jobs.STUDENT,
            new Location(25, 25, TileType.VILLAGE),
            Arrays.asList("Salad", "Grape", "Wine"),
            "An artist who lives in a small cabin near the river. She loves nature and creates sculptures from foraged materials. " +
                    "She left the city to pursue her art, but often compares her work unfavorably to other artists. " +
                    "She's particularly envious of successful artists who receive recognition, something she desperately craves for her own work."
    ),

    ROBIN(
            "Robin",
            Charactristic.HARD_WORKING,
            Jobs.SELLER,
            new Location(30, 30, TileType.VILLAGE),
            Arrays.asList("Spaghetti", "Wood", "Iron"),
            "The local carpenter who runs the carpentry shop. She's skilled at building and loves working with wood. " +
                    "She built her mountain home with her own hands and takes great pride in her craftsmanship. " +
                    "She works from dawn till dusk and believes there's no problem that can't be fixed with the right tools and enough effort."
    ),

    WILLY(
            "Willy",
            Charactristic.KIND,
            Jobs.FISHER,
            new Location(5, 35, TileType.VILLAGE),
            Arrays.asList("Fish", "Trout", "Fishing Rod"),
            "An old fisherman who runs the fishing shop on the pier. He has a weathered face and always smells of the sea. " +
                    "He's been fishing these waters for over 40 years and knows every fish species by sight. " +
                    "He's always willing to share fishing tips with newcomers and believes the ocean connects all living things."
    ),

    PIERRE(
            "Pierre",
            Charactristic.GREEDY,
            Jobs.SELLER,
            new Location(35, 15, TileType.VILLAGE),
            Arrays.asList("Gold Bar", "Diamond", "Emerald"),
            "The owner of the local general store who is always looking to make a profit. " +
                    "He's constantly worried about competition from the big chain stores and will do anything to increase his sales. " +
                    "He charges premium prices for 'locally sourced' goods and is known to haggle aggressively when buying from local farmers."
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

    public static Npcs fromName(String name) {
        for (Npcs npc : values()) {
            if (npc.getName().equalsIgnoreCase(name)) {
                return npc;
            }
        }
        return null;
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
}
