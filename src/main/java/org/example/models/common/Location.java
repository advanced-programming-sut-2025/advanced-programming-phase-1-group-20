package org.example.models.common;

import org.example.models.Items.Item;
import org.example.models.MapDetails.GameMap;
import org.example.models.enums.Types.TileType;

public class Location {
    public int xAxis;
    public int yAxis;
    private TileType tile;
    private String type;
    private Item item;
    private boolean shokhm;
    private boolean isScarecrowThere;

    public Location(int xAxis, int yAxis, TileType tile) {
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.tile = tile;
        this.item = null;
        this.shokhm = false;
        this.isScarecrowThere = false;
        updateTypeFromTile();
    }

    public void updateTypeFromTile() {
        switch (tile) {
            case GRASS -> this.type = "grass";
            case TREE -> this.type = "tree";
            case STONE -> this.type = "stone";
            case WATER -> this.type = "water";
            case PATH -> this.type = "path";
            case BUILDING -> this.type = "building";
            case CROP -> this.type = "crop";
            case BARN -> this.type = "barn";
            case COOP -> this.type = "coop";
            case GREENHOUSE -> this.type = "greenhouse";
            case LAKE -> this.type = "lake";
            case QUARRY -> this.type = "quarry";
            case VILLAGE -> this.type = "village";
            default -> this.type = "?";
        }
    }


    public TileType getTile() {
        return tile;
    }

    public void setTile(TileType tile) {
        this.tile = tile;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean getShokhm() {
        return shokhm;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public int getX() {
        return xAxis;
    }

    public int getY() {
        return yAxis;
    }

    public boolean isScarecrowThere() {
        return isScarecrowThere;
    }

    public void setScarecrowThere(boolean scarecrowThere) {
        isScarecrowThere = scarecrowThere;
    }

    public String toString() {
        return "Location(x=" + xAxis + ", y=" + yAxis + ", tile=" + tile + ")";
    }
}