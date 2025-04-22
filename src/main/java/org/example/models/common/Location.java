package org.example.models.common;

import org.example.models.enums.Types.TileType;

public class Location {
    public int xAxis;
    public int yAxis;
    TileType tile;

    public Location(int xAxis, int yAxis, TileType tile) {
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.tile = tile;
    }
}
