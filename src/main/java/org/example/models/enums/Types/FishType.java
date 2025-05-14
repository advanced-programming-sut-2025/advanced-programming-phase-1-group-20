package org.example.models.enums.Types;

import org.example.models.enums.Seasons;

/**
 * Enum representing different types of fish available in the game.
 * Each fish has specific properties such as name, price, quality, and season availability.
 */
public enum FishType {
    // Regular Fish by Season
    // Spring Fish
    FLOUNDER("Flounder", 100, new Seasons[]{Seasons.SPRING}, "A flat fish that lives on the bottom of the ocean.", 89),
    LIONFISH("Lionfish", 100, new Seasons[]{Seasons.SPRING}, "A spiny fish with venomous fins. Handle with care!", 53),
    HERRING("Herring", 30, new Seasons[]{Seasons.SPRING}, "A common ocean fish that swims in large schools.", 61),
    GHOSTFISH("Ghostfish", 45, new Seasons[]{Seasons.SPRING}, "A pale, nearly transparent fish that inhabits deep waters.", 70),

    // Summer Fish
    TILAPIA("Tilapia", 75, new Seasons[]{Seasons.SUMMER}, "A popular farm-raised fish with mild flavor.", 97),
    DORADO("Dorado", 100, new Seasons[]{Seasons.SUMMER}, "A fierce carnivore with brilliant golden scales.", 58),
    SUNFISH("Sunfish", 30, new Seasons[]{Seasons.SUMMER}, "A small freshwater fish with a round, flat body.", 85),
    RAINBOW_TROUT("Rainbow Trout", 65, new Seasons[]{Seasons.SUMMER}, "A freshwater trout known for its colorful pattern.", 63),

    // Fall Fish
    SALMON("Salmon", 75, new Seasons[]{Seasons.AUTUMN}, "Swims upstream to lay its eggs. A staple food of bears.", 92),
    SARDINE("Sardine", 40, new Seasons[]{Seasons.AUTUMN}, "A small, oily fish often found in large schools.", 51),
    SHAD("Shad", 60, new Seasons[]{Seasons.AUTUMN}, "A freshwater fish related to herring.", 92),
    BLUE_DISCUS("Blue Discus", 120, new Seasons[]{Seasons.AUTUMN}, "A brightly colored tropical fish popular in aquariums.", 84),

    // Winter Fish
    MIDNIGHT_CARP("Midnight Carp", 150, new Seasons[]{Seasons.WINTER}, "A mysterious fish that only appears at night.", 67),
    SQUID("Squid", 80, new Seasons[]{Seasons.WINTER}, "A deep-sea creature that's technically not a fish.", 76),
    TUNA("Tuna", 100, new Seasons[]{Seasons.WINTER}, "A large fish that lives in the ocean.", 71),
    PERCH("Perch", 55, new Seasons[]{Seasons.WINTER}, "A freshwater fish with spiny fins.", 93),

    // Legendary Fish (only available when fishing skill is maxed)
    CRIMSONFISH("Crimsonfish", 1500, new Seasons[]{Seasons.SUMMER}, "A rare, legendary fish with crimson scales.", 88),
    ANGLER("Angler", 900, new Seasons[]{Seasons.AUTUMN}, "A legendary fish with a glowing appendage on its head.", 60),
    LEGEND("Legend", 5000, new Seasons[]{Seasons.SPRING}, "The king of all fish. Extremely rare and elusive.", 79),
    GLACIERFISH("Glacierfish", 1000, new Seasons[]{Seasons.WINTER}, "A legendary fish that lives in freezing waters.", 56),
    MUTANT_CARP("Mutant Carp", 1200, new Seasons[]{Seasons.SPRING, Seasons.SUMMER, Seasons.AUTUMN, Seasons.WINTER}, "A fish mutated by unknown substances in the water.", 99);

    private final String name;
    private final int basePrice;
    private final Seasons[] seasons;
    private final String description;
    private final int Energy;

    FishType(String name, int basePrice, Seasons[] seasons, String description, int Energy) {
        this.name = name;
        this.basePrice = basePrice;
        this.seasons = seasons;
        this.description = description;
        this.Energy = Energy;
    }

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