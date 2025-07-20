package org.example.common.models.MapDetails;

import org.example.common.models.enums.Types.TileType;

public class Tile {
    private TileType type;

    //t[x][y]
    //private Item item;
    //private boolean harvestable;

    public Tile(int x, int y, TileType type) {
        this.type = type;
        //harvestable = false;
        //item = null;
    }
}
