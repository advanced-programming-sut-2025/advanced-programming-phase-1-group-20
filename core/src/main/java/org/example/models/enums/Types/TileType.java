package org.example.models.enums.Types;

public enum TileType {
    WATER(false),
    COBBLED_STONE(false),
    GRASS(true),
    SAND(true),
    STONE(true),
    TREE(false),
    IRON_ORE(false),
    BARN(false),
    COOP(false),
    PLOWED(true),
    CROP(true),
    GOLD_ORE(false),
    DIAMOND_ORE(false),
    VILLAGE(false),
    MARKET(false),
    PATH(true),
    BUILDING(false),
    LAKE(false),
    QUARRY(false),
    SHIPPING_BIN(false),
    EMERALD_ORE(false),
    CONSTRUCTED_GREENHOUSE(false),
    BRANCH(true),
    GREENHOUSE(false);

    private final boolean isWalkable;

    TileType(boolean isWalkable) {
        this.isWalkable = isWalkable;
    }

    public boolean isWalkable() {
        return isWalkable;
    }
}
