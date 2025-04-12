package models;

import models.enums.Tile;

public class GameMap {
    private Tile[][] map;
    public GameMap() {
        initializeMap();
    }

    private void initializeMap() {
        //initializing the wanted map:


        //generating random objects:
        generateRandomObjects();
    }

    private void generateRandomObjects() {}

    public void changeTile(Tile tile , int x, int y) {
        map[x][y] = tile;
    }
    public Tile getTile(int x, int y) {
        return map[x][y];
    }


}
