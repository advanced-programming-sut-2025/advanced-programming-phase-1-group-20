package org.example.common.models.enums.Types;

public enum TileType {
    WATER(false),
    COBBLED_STONE(false),
    Dirt(true),
    SAND(true),
    STONE(false),
    TREE(false),
    IRON_ORE(false),
    BARN(false),
    COOP(false),
    PLOWED(true),
    CROP(false),
    GOLD_ORE(false),
    DIAMOND_ORE(false),
    VILLAGE(true),
    MARKET(false),
    BlackSmith(true),
    JojaMart(true),
    PIERRE_GENERAL_STORE(true),
    CARPENTERS_SHOP(true),
    FISH_SHOP(true),
    MARNIE_SHOP(true),
    STARDROP_SALOON(true),
    PATH(true),
    BUILDING(false),
    LAKE(false),
    QUARRY(true),
    SHIPPING_BIN(false),
    EMERALD_ORE(false),
    IRIDIUM_STONE(false),
    JEWEL_STONE(false),
    GOLD_STONE(false),
    CONSTRUCTED_GREENHOUSE(true),
    BRANCH(true),
    GREENHOUSE(true),
    FENCE(false);

    private final boolean isWalkable;

    TileType(boolean isWalkable) {
        this.isWalkable = isWalkable;
    }

    public boolean isWalkable() {
        return isWalkable;
    }
}
