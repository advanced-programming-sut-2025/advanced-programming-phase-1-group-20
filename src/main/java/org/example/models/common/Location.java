package org.example.models.common;

import org.example.models.MapDetails.GameMap;
import org.example.models.enums.Types.TileType;

public class Location {
    public int xAxis;
    public int yAxis;
    TileType tile;

    public Location(int xAxis, int yAxis, TileType tile) {
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.tile = tile;

        if (tile == null) {
            try {
                this.tile = GameMap.getTile(xAxis, yAxis);
            } catch (Exception e) {
                this.tile = TileType.GRASS;
            }
        }
    }

    public TileType getTile() {
        return tile;
    }

    public void setTile(TileType tile) {
        this.tile = tile;
    }

    @Override
    public String toString() {
        return "Location(x=" + xAxis + ", y=" + yAxis + ", tile=" + tile + ")";
    }
}