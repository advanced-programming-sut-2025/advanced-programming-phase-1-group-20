package org.example.models.MapDetails;

import org.example.models.common.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class Village {
    private int centerX;
    private int centerY;
    private int radius;
    private Map<String, Tile> tiles;
    private List<Building> buildings;
    //private List<NPC> residents;
    private String name;
    //private List<Shop> shops;

    public Village(int centerX, int centerY, int radius, String name) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
        this.name = name;
        this.tiles = new HashMap<>();
        this.buildings = new ArrayList<>();
        //this.residents = new ArrayList<>();
        //this.shops = new ArrayList<>();
        initializeVillage();
    }

    private void initializeVillage() {
        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int y = centerY - radius; y <= centerY + radius; y++) {
                if (Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) <= Math.pow(radius, 2)) {
                    String type = "path";

                    if (Math.abs(x - centerX) <= 2 && Math.abs(y - centerY) <= 2) {
                        type = "plaza";
                    }

                    //tiles.put(getTileKey(x, y), new Location(x, y, type));
                    //will be complete
                }
            }
        }

        initializeBuildings();
        initializeNPCs();
        initializeShops();
    }

    private void initializeBuildings() {
        buildings.add(new Building(centerX - 5, centerY - 3, 3, 2, "Town Hall", "public"));
        buildings.add(new Building(centerX + 3, centerY - 4, 2, 2, "Blacksmith", "shop"));
        buildings.add(new Building(centerX - 2, centerY + 4, 3, 2, "General Store", "shop"));
        buildings.add(new Building(centerX + 4, centerY + 2, 3, 2, "Stardrop Saloon", "public"));
    }

    private void initializeNPCs() {
        //...
    }

    private void initializeShops() {
        //...
    }

    private String getTileKey(int x, int y) {
        return x + "," + y;
    }

    public Tile getTile(int x, int y) {
        return tiles.get(getTileKey(x, y));
    }

    public boolean setTile(int x, int y, Tile tile) {
        if (contains(x, y)) {
            tiles.put(getTileKey(x, y), tile);
            return true;
        }
        return false;
    }

    public boolean contains(int x, int y) {
        return Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) <= Math.pow(radius, 2);
    }

    public void addBuilding(Building building) {
        buildings.add(building);
        updateBuildingTiles(building);
    }

    private void updateBuildingTiles(Building building) {
        for (int x = building.getX(); x < building.getX() + building.getWidth(); x++) {
            for (int y = building.getY(); y < building.getY() + building.getHeight(); y++) {
                if (contains(x, y)) {
                    //tiles.put(getTileKey(x, y), new Location(x, y, "building_" + building.getType()));
                    //will be complete
                }
            }
        }
    }

    public List<Building> getBuildings() {
        return buildings;
    }

//    public List<NPC> getResidents() {
//        //...
//    }
//
//    public List<Shop> getShops() {
//        //...
//    }

    public String getName() {
        return name;
    }

    public void printVillageInfo() {
        //...
    }
}