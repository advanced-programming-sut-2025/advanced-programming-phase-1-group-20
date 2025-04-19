package org.example.models;

import org.example.models.enums.Types.TileType;

public class Location {
    int xAxis;
    int yAxis;
    TileType tile;

    Location(int xAxis, int yAxis, TileType tile) {
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.tile = tile;
    }
}
