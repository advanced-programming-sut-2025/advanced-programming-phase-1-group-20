package org.example.models.MapDetails;

import org.example.models.enums.Types.TileType;

// TODO: map making
public class GameMap {
    private static TileType[][] map;

    public GameMap() {
        initializeMap();
    }

    public static TileType getTile(int x, int y) {
        return map[x][y];
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


}
