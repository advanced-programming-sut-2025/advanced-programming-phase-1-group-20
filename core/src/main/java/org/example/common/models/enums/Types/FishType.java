package org.example.common.models.enums.Types;

import org.example.common.models.enums.Seasons;

public enum FishType {
    // Regular Fish by Season

    // Spring Fish
    FLOUNDER("Flounder", 100, new Seasons[]{Seasons.SPRING}, "A flat fish that lives on the bottom of the ocean.", 89, "content/Fish/Flounder.png"),
    LIONFISH("Lionfish", 100, new Seasons[]{Seasons.SPRING}, "A spiny fish with venomous fins. Handle with care!", 53, "content/Fish/Lionfish.png"),
    HERRING("Herring", 30, new Seasons[]{Seasons.SPRING}, "A common ocean fish that swims in large schools.", 61, "content/Fish/Herring.png"),
    GHOSTFISH("Ghostfish", 45, new Seasons[]{Seasons.SPRING}, "A pale, nearly transparent fish that inhabits deep waters.", 70, "content/Fish/Ghostfish.png"),

    // Summer Fish
    TILAPIA("Tilapia", 75, new Seasons[]{Seasons.SUMMER}, "A popular farm-raised fish with mild flavor.", 97, "content/Fish/Tilapia.png"),
    DORADO("Dorado", 100, new Seasons[]{Seasons.SUMMER}, "A fierce carnivore with brilliant golden scales.", 58, "content/fishes/Dorado.png"),
    SUNFISH("Sunfish", 30, new Seasons[]{Seasons.SUMMER}, "A small freshwater fish with a round, flat body.", 85, "content/Fish/Sunfish.png"),
    RAINBOW_TROUT("Rainbow Trout", 65, new Seasons[]{Seasons.SUMMER}, "A freshwater trout known for its colorful pattern.", 63, "content/Fish/Rainbow_Trout.png"),

    // Fall Fish
    SALMON("Salmon", 75, new Seasons[]{Seasons.AUTUMN}, "Swims upstream to lay its eggs. A staple food of bears.", 92, "content/Fish/Salmon.png"),
    SARDINE("Sardine", 40, new Seasons[]{Seasons.AUTUMN}, "A small, oily fish often found in large schools.", 51, "content/Fish/Sardine.png"),
    SHAD("Shad", 60, new Seasons[]{Seasons.AUTUMN}, "A freshwater fish related to herring.", 92, "content/fishes/Shad.png"),
    BLUE_DISCUS("Blue Discus", 120, new Seasons[]{Seasons.AUTUMN}, "A brightly colored tropical fish popular in aquariums.", 84, "content/Fish/Blue_Discus.png"),

    // Winter Fish
    MIDNIGHT_CARP("Midnight Carp", 150, new Seasons[]{Seasons.WINTER}, "A mysterious fish that only appears at night.", 67, "content/Fish/Midnight_Carp.png"),
    SQUID("Squid", 80, new Seasons[]{Seasons.WINTER}, "A deep-sea creature that's technically not a fish.", 76, "content/Fish/Squid.png"),
    TUNA("Tuna", 100, new Seasons[]{Seasons.WINTER}, "A large fish that lives in the ocean.", 71, "content/Fish/Tuna.png"),
    PERCH("Perch", 55, new Seasons[]{Seasons.WINTER}, "A freshwater fish with spiny fins.", 93, "content/Fish/Perch.png"),

    // Legendary Fish (only available when fishing skill is maxed)
    CRIMSONFISH("Crimsonfish", 1500, new Seasons[]{Seasons.SUMMER}, "A rare, legendary fish with crimson scales.", 88, "content/Fish/Crimsonfish.png"),
    ANGLER("Angler", 900, new Seasons[]{Seasons.AUTUMN}, "A legendary fish with a glowing appendage on its head.", 60, "content/Fish/Angler.png"),
    LEGEND("Legend", 5000, new Seasons[]{Seasons.SPRING}, "The king of all fish. Extremely rare and elusive.", 79, "content/Fish/Legend.png"),
    GLACIERFISH("Glacierfish", 1000, new Seasons[]{Seasons.WINTER}, "A legendary fish that lives in freezing waters.", 56, "content/Fish/Glacierfish.png"),
    MUTANT_CARP("Mutant Carp", 1200, new Seasons[]{Seasons.SPRING, Seasons.SUMMER, Seasons.AUTUMN, Seasons.WINTER}, "A fish mutated by unknown substances in the water.", 99, "content/Fish/Mutant_Carp.png");

    private final String name;
    private final int basePrice;
    private final Seasons[] seasons;
    private final String description;
    private final int Energy;
    private final String imageFilePath;

    FishType(String name, int basePrice, Seasons[] seasons, String description, int Energy, String imageFilePath) {
        this.name = name;
        this.basePrice = basePrice;
        this.seasons = seasons;
        this.description = description;
        this.Energy = Energy;
        this.imageFilePath = imageFilePath;
    }

    public static FishType fromName(String name) {
        for (FishType type : FishType.values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        return null;
    }

    public static FishType[] getAvailableFish(Seasons season, boolean includeLegenday, int fishingSkill) {
        // Count fish available in this season
        int count = 0;
        for (FishType fish : FishType.values()) {
            boolean isLegendary = fish.ordinal() >= CRIMSONFISH.ordinal();
            if (fish.isAvailableInSeason(season) && (!isLegendary || (includeLegenday && fishingSkill >= 4))) {
                count++;
            }
        }

        // Create array of available fish
        FishType[] availableFish = new FishType[count];
        int index = 0;
        for (FishType fish : FishType.values()) {
            boolean isLegendary = fish.ordinal() >= CRIMSONFISH.ordinal();
            if (fish.isAvailableInSeason(season) && (!isLegendary || (includeLegenday && fishingSkill >= 4))) {
                availableFish[index++] = fish;
            }
        }

        return availableFish;
    }

    public boolean isAvailableInSeason(Seasons season) {
        for (Seasons s : seasons) {
            if (s == season) {
                return true;
            }
        }
        return false;
    }


    public boolean isLegendary() {
        return this.ordinal() >= CRIMSONFISH.ordinal();
    }

    public String getName() {
        return name;
    }

    public int getBasePrice() {
        return basePrice;
    }

    public Seasons[] getSeasons() {
        return seasons;
    }

    public String getDescription() {
        return description;
    }

    public String getImageFilePath() {
        return imageFilePath;
    }
}
