package org.example.models;

import org.example.models.enums.Types.TileType;

public class GameMap {
    private TileType[][] map;

    public GameMap() {
        initializeMap();
    }

    private void initializeMap() {
        //initializing the wanted map:


        //generating random objects:
        generateRandomObjects();
    }

    private void generateRandomObjects() {
    }

    public void changeTile(TileType tile, int x, int y) {
        map[x][y] = tile;
    }

    public TileType getTile(int x, int y) {
        return map[x][y];
    }


}
