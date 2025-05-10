package org.example.models.enums.Types;

import org.example.models.enums.Seasons;

/**
 * Enum representing different types of fish available in the game.
 * Each fish has specific properties such as name, price, quality, and season availability.
 */
public enum FishType {
    // Regular Fish by Season
    // Spring Fish
    FLOUNDER("Flounder", 100, new Seasons[]{Seasons.SPRING}, "A flat fish that lives on the bottom of the ocean."),
    LIONFISH("Lionfish", 100, new Seasons[]{Seasons.SPRING}, "A spiny fish with venomous fins. Handle with care!"),
    HERRING("Herring", 30, new Seasons[]{Seasons.SPRING}, "A common ocean fish that swims in large schools."),
    GHOSTFISH("Ghostfish", 45, new Seasons[]{Seasons.SPRING}, "A pale, nearly transparent fish that inhabits deep waters."),

    // Summer Fish
    TILAPIA("Tilapia", 75, new Seasons[]{Seasons.SUMMER}, "A popular farm-raised fish with mild flavor."),
    DORADO("Dorado", 100, new Seasons[]{Seasons.SUMMER}, "A fierce carnivore with brilliant golden scales."),
    SUNFISH("Sunfish", 30, new Seasons[]{Seasons.SUMMER}, "A small freshwater fish with a round, flat body."),
    RAINBOW_TROUT("Rainbow Trout", 65, new Seasons[]{Seasons.SUMMER}, "A freshwater trout known for its colorful pattern."),

    // Fall Fish
    SALMON("Salmon", 75, new Seasons[]{Seasons.AUTUMN}, "Swims upstream to lay its eggs. A staple food of bears."),
    SARDINE("Sardine", 40, new Seasons[]{Seasons.AUTUMN}, "A small, oily fish often found in large schools."),
    SHAD("Shad", 60, new Seasons[]{Seasons.AUTUMN}, "A freshwater fish related to herring."),
    BLUE_DISCUS("Blue Discus", 120, new Seasons[]{Seasons.AUTUMN}, "A brightly colored tropical fish popular in aquariums."),

    // Winter Fish
    MIDNIGHT_CARP("Midnight Carp", 150, new Seasons[]{Seasons.WINTER}, "A mysterious fish that only appears at night."),
    SQUID("Squid", 80, new Seasons[]{Seasons.WINTER}, "A deep-sea creature that's technically not a fish."),
    TUNA("Tuna", 100, new Seasons[]{Seasons.WINTER}, "A large fish that lives in the ocean."),
    PERCH("Perch", 55, new Seasons[]{Seasons.WINTER}, "A freshwater fish with spiny fins."),

    // Legendary Fish (only available when fishing skill is maxed)
    CRIMSONFISH("Crimsonfish", 1500, new Seasons[]{Seasons.SUMMER}, "A rare, legendary fish with crimson scales."),
    ANGLER("Angler", 900, new Seasons[]{Seasons.AUTUMN}, "A legendary fish with a glowing appendage on its head."),
    LEGEND("Legend", 5000, new Seasons[]{Seasons.SPRING}, "The king of all fish. Extremely rare and elusive."),
    GLACIERFISH("Glacierfish", 1000, new Seasons[]{Seasons.WINTER}, "A legendary fish that lives in freezing waters."),
    MUTANT_CARP("Mutant Carp", 1200, new Seasons[]{Seasons.SPRING, Seasons.SUMMER, Seasons.AUTUMN, Seasons.WINTER}, "A fish mutated by unknown substances in the water.");

    private final String name;
    private final int basePrice;
    private final Seasons[] seasons;
    private final String description;

    FishType(String name, int basePrice, Seasons[] seasons, String description) {
        this.name = name;
        this.basePrice = basePrice;
        this.seasons = seasons;
        this.description = description;
    }

    /**
     * Get a fish type by its name.
     *
     * @param name The name of the fish to retrieve
     * @return The FishType with the given name, or null if not found
     */
    public static FishType fromName(String name) {
        for (FishType type : FishType.values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        return null;
    }

    /**
     * Get all fish available in a specific season.
     *
     * @param season          The season to check
     * @param includeLegenday Whether to include legendary fish
     * @param fishingSkill    The player's fishing skill level (legendary fish require max skill)
     * @return Array of fish types available in the given season
     */
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

    /**
     * Check if this fish is available in the given season.
     *
     * @param season The season to check
     * @return true if the fish is available in the season, false otherwise
     */
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

    // Getters
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
}