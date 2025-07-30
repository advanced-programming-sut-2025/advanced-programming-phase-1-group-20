package org.example.common.models.enums.Types;

import org.example.common.models.enums.Seasons;

public enum FishType {
    // Regular Fish by Season

    // Spring Fish
    ANCHOVY("Anchovy", 30, new Seasons[]{Seasons.SPRING}, "A small, oily fish that swims in large schools.", 45, "content/Fish/Anchovy.png"),
    SARDINE("Sardine", 40, new Seasons[]{Seasons.SPRING}, "A small, oily fish often found in large schools.", 51, "content/Fish/Sardine.png"),
    BREAM("Bream", 45, new Seasons[]{Seasons.SPRING}, "A freshwater fish with a deep body and small head.", 55, "content/Fish/Bream.png"),
    FLOUNDER("Flounder", 100, new Seasons[]{Seasons.SPRING}, "A flat fish that lives on the bottom of the ocean.", 89, "content/Fish/Flounder.png"),
    HERRING("Herring", 30, new Seasons[]{Seasons.SPRING}, "A common ocean fish that swims in large schools.", 61, "content/Fish/Herring.png"),
    EEL("Eel", 85, new Seasons[]{Seasons.SPRING}, "A long, snake-like fish that lives in both fresh and salt water.", 75, "content/Fish/Eel.png"),
    LIONFISH("Lionfish", 100, new Seasons[]{Seasons.SPRING}, "A spiny fish with venomous fins. Handle with care!", 53, "content/Fish/Lionfish.png"),
    GHOSTFISH("Ghostfish", 45, new Seasons[]{Seasons.SPRING}, "A pale, nearly transparent fish that inhabits deep waters.", 70, "content/Fish/Ghostfish.png"),

    // Summer Fish
    PUFFERFISH("Pufferfish", 200, new Seasons[]{Seasons.SUMMER}, "A poisonous fish that inflates when threatened.", 80, "content/Fish/Pufferfish.png"),
    RAINBOW_TROUT("Rainbow Trout", 65, new Seasons[]{Seasons.SUMMER}, "A freshwater trout known for its colorful pattern.", 63, "content/Fish/Rainbow_Trout.png"),
    RED_MULLET("Red Mullet", 75, new Seasons[]{Seasons.SUMMER}, "A Mediterranean fish with distinctive red coloring.", 68, "content/Fish/Red_Mullet.png"),
    SUNFISH("Sunfish", 30, new Seasons[]{Seasons.SUMMER}, "A small freshwater fish with a round, flat body.", 85, "content/Fish/Sunfish.png"),
    TILAPIA("Tilapia", 75, new Seasons[]{Seasons.SUMMER}, "A popular farm-raised fish with mild flavor.", 97, "content/Fish/Tilapia.png"),
    DORADO("Dorado", 100, new Seasons[]{Seasons.SUMMER}, "A fierce carnivore with brilliant golden scales.", 58, "content/Fish/Dorado.png"),
    OCTOPUS("Octopus", 150, new Seasons[]{Seasons.SUMMER}, "A highly intelligent sea creature with eight arms.", 65, "content/Fish/Octopus.png"),

    // Fall Fish
    ALBACORE("Albacore", 75, new Seasons[]{Seasons.AUTUMN}, "A type of tuna with white meat.", 72, "content/Fish/Albacore.png"),
    SALMON("Salmon", 75, new Seasons[]{Seasons.AUTUMN}, "Swims upstream to lay its eggs. A staple food of bears.", 92, "content/Fish/Salmon.png"),
    WALLEYE("Walleye", 105, new Seasons[]{Seasons.AUTUMN}, "A freshwater fish with large, glassy eyes.", 78, "content/Fish/Walleye.png"),
    SHAD("Shad", 60, new Seasons[]{Seasons.AUTUMN}, "A freshwater fish related to herring.", 92, "content/Fish/Shad.png"),
    BLUE_DISCUS("Blue Discus", 120, new Seasons[]{Seasons.AUTUMN}, "A brightly colored tropical fish popular in aquariums.", 84, "content/Fish/Blue_Discus.png"),

    // Winter Fish
    BLOBFISH("Blobfish", 500, new Seasons[]{Seasons.WINTER}, "A deep-sea fish that looks very different at surface pressure.", 40, "content/Fish/Blobfish.png"),
    LINGCOD("Lingcod", 120, new Seasons[]{Seasons.WINTER}, "A large predatory fish found in the North Pacific.", 82, "content/Fish/Lingcod.png"),
    MIDNIGHT_CARP("Midnight Carp", 150, new Seasons[]{Seasons.WINTER}, "A mysterious fish that only appears at night.", 67, "content/Fish/Midnight_Carp.png"),
    MIDNIGHT_SQUID("Midnight Squid", 100, new Seasons[]{Seasons.WINTER}, "A deep-sea squid that thrives in darkness.", 73, "content/Fish/Midnight_Squid.png"),
    PERCH("Perch", 55, new Seasons[]{Seasons.WINTER}, "A freshwater fish with spiny fins.", 93, "content/Fish/Perch.png"),
    SQUID("Squid", 80, new Seasons[]{Seasons.WINTER}, "A deep-sea creature that's technically not a fish.", 76, "content/Fish/Squid.png"),
    TUNA("Tuna", 100, new Seasons[]{Seasons.WINTER}, "A large fish that lives in the ocean.", 71, "content/Fish/Tuna.png"),

    // Special Fish (available in all seasons)
    BULLHEAD("Bullhead", 75, new Seasons[]{Seasons.SPRING, Seasons.SUMMER, Seasons.AUTUMN, Seasons.WINTER}, "A catfish with distinctive barbels around its mouth.", 88, "content/Fish/Bullhead.png"),
    CARP("Carp", 30, new Seasons[]{Seasons.SPRING, Seasons.SUMMER, Seasons.AUTUMN, Seasons.WINTER}, "A common freshwater fish that can grow quite large.", 95, "content/Fish/Carp.png"),
    CATFISH("Catfish", 200, new Seasons[]{Seasons.SPRING, Seasons.SUMMER, Seasons.AUTUMN, Seasons.WINTER}, "A bottom-dwelling fish with whisker-like barbels.", 66, "content/Fish/Catfish.png"),
    CHUB("Chub", 50, new Seasons[]{Seasons.SPRING, Seasons.SUMMER, Seasons.AUTUMN, Seasons.WINTER}, "A small freshwater fish found in streams and rivers.", 90, "content/Fish/Chub.png"),
    ICE_PIP("Ice Pip", 500, new Seasons[]{Seasons.SPRING, Seasons.SUMMER, Seasons.AUTUMN, Seasons.WINTER}, "A rare fish that lives in extremely cold waters.", 35, "content/Fish/Ice_Pip.png"),
    LARGEMOUTH_BASS("Largemouth Bass", 100, new Seasons[]{Seasons.SPRING, Seasons.SUMMER, Seasons.AUTUMN, Seasons.WINTER}, "A popular game fish known for its large mouth.", 69, "content/Fish/Largemouth_Bass.png"),
    LAVA_EEL("Lava Eel", 700, new Seasons[]{Seasons.SPRING, Seasons.SUMMER, Seasons.AUTUMN, Seasons.WINTER}, "A mysterious eel that lives in molten lava.", 25, "content/Fish/Lava_Eel.png"),
    PIKE("Pike", 100, new Seasons[]{Seasons.SPRING, Seasons.SUMMER, Seasons.AUTUMN, Seasons.WINTER}, "A fierce predatory fish with sharp teeth.", 64, "content/Fish/Pike.png"),
    RED_SNAPPER("Red Snapper", 50, new Seasons[]{Seasons.SPRING, Seasons.SUMMER, Seasons.AUTUMN, Seasons.WINTER}, "A popular saltwater fish with red coloring.", 87, "content/Fish/Red_Snapper.png"),
    SANDFISH("Sandfish", 75, new Seasons[]{Seasons.SPRING, Seasons.SUMMER, Seasons.AUTUMN, Seasons.WINTER}, "A fish that burrows in sandy ocean floors.", 81, "content/Fish/Sandfish.png"),
    SCORPION_CARP("Scorpion Carp", 150, new Seasons[]{Seasons.SPRING, Seasons.SUMMER, Seasons.AUTUMN, Seasons.WINTER}, "A dangerous fish with venomous spines.", 42, "content/Fish/Scorpion_Carp.png"),
    SEA_CUCUMBER("Sea Cucumber", 75, new Seasons[]{Seasons.SPRING, Seasons.SUMMER, Seasons.AUTUMN, Seasons.WINTER}, "A sea creature that's not actually a fish.", 79, "content/Fish/Sea_Cucumber.png"),
    SLIMEJACK("Slimejack", 100, new Seasons[]{Seasons.SPRING, Seasons.SUMMER, Seasons.AUTUMN, Seasons.WINTER}, "A fish covered in a thick layer of slime.", 48, "content/Fish/Slimejack.png"),
    SMALLMOUTH_BASS("Smallmouth Bass", 50, new Seasons[]{Seasons.SPRING, Seasons.SUMMER, Seasons.AUTUMN, Seasons.WINTER}, "A smaller cousin of the largemouth bass.", 91, "content/Fish/Smallmouth_Bass.png"),
    SPOOK_FISH("Spook Fish", 220, new Seasons[]{Seasons.SPRING, Seasons.SUMMER, Seasons.AUTUMN, Seasons.WINTER}, "A ghostly fish with transparent features.", 38, "content/Fish/Spook_Fish.png"),
    STINGRAY("Stingray", 180, new Seasons[]{Seasons.SPRING, Seasons.SUMMER, Seasons.AUTUMN, Seasons.WINTER}, "A flat fish with a venomous tail spine.", 54, "content/Fish/Stingray.png"),
    STONEFISH("Stonefish", 300, new Seasons[]{Seasons.SPRING, Seasons.SUMMER, Seasons.AUTUMN, Seasons.WINTER}, "The most venomous fish in the world.", 30, "content/Fish/Stonefish.png"),
    STURGEON("Sturgeon", 200, new Seasons[]{Seasons.SPRING, Seasons.SUMMER, Seasons.AUTUMN, Seasons.WINTER}, "A prehistoric fish that produces caviar.", 62, "content/Fish/Sturgeon.png"),
    SUPER_CUCUMBER("Super Cucumber", 250, new Seasons[]{Seasons.SPRING, Seasons.SUMMER, Seasons.AUTUMN, Seasons.WINTER}, "A rare, enhanced version of the sea cucumber.", 45, "content/Fish/Super_Cucumber.png"),
    TIGER_TROUT("Tiger Trout", 150, new Seasons[]{Seasons.SPRING, Seasons.SUMMER, Seasons.AUTUMN, Seasons.WINTER}, "A hybrid trout with distinctive tiger-like stripes.", 59, "content/Fish/Tiger_Trout.png"),
    VOID_SALMON("Void Salmon", 150, new Seasons[]{Seasons.SPRING, Seasons.SUMMER, Seasons.AUTUMN, Seasons.WINTER}, "A salmon corrupted by void energy.", 33, "content/Fish/Void_Salmon.png"),
    WOODSKIP("Woodskip", 75, new Seasons[]{Seasons.SPRING, Seasons.SUMMER, Seasons.AUTUMN, Seasons.WINTER}, "A fish that lives in forest ponds.", 86, "content/Fish/Woodskip.png"),

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
