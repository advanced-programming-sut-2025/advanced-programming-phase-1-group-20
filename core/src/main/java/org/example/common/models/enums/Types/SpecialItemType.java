package org.example.common.models.enums.Types;

public enum SpecialItemType {
    Bouquet("Bouquet", 100, "content/Bouquet.png", "A carefully arranged and often tied bundle of flowers"),
    WeddingRing("Wedding Ring", 10000, "content/Crafting/Wedding_Ring.png", "It's used to ask for another farmer's hand in marriage. (Unlocked after reaching level 3 friendship with a player)");

    private final String name;
    private final int baseSellPrice;
    private final String imageFilepath;
    private final String description;

    SpecialItemType(String name, int baseSellPrice, String imageFilepath, String description) {
        this.name = name;
        this.baseSellPrice = baseSellPrice;
        this.imageFilepath = imageFilepath;
        this.description = description;
    }

    public static SpecialItemType fromName(String name) {
        for (SpecialItemType specialItemType : SpecialItemType.values()) {
            if (name.equalsIgnoreCase(specialItemType.getName())) {
                return specialItemType;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public int getBaseSellPrice() {
        return baseSellPrice;
    }

    public String getImageFilepath() {
        return imageFilepath;
    }

    public String getDescription() {
        return description;
    }
}
