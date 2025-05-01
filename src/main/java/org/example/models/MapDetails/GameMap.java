package org.example.models.MapDetails;

import org.example.models.Items.Item;
import org.example.models.Player.Player;
import org.example.models.common.Location;
import org.example.models.enums.Types.TileType;
import java.util.HashMap;
import java.util.Map;

public class GameMap {
    private int width;
    private int height;
    private Location[][] tiles;
    private Farm[] farms;
    private Village village;
    private int currentFarmIndex;
    private Map<String, Character> symbolMap;
    private Player currentPlayer;

    public GameMap(int width, int height, Player player) {
        this.width = width;
        this.height = height;
        this.tiles = new Location[width][height];
        this.farms = new Farm[4];
        this.currentFarmIndex = 0;
        this.currentPlayer = player;
        this.symbolMap = new HashMap<>();

        initializeSymbols();
        initializeMap();
    }

    private void initializeSymbols() {
        symbolMap.put("grass", '.');
        symbolMap.put("tilled_soil", '=');
        symbolMap.put("tree", 'T');
        symbolMap.put("stone", 'S');
        symbolMap.put("water", '~');
        symbolMap.put("path", '#');
        symbolMap.put("house", 'H');
        symbolMap.put("village", 'V');
        symbolMap.put("bridge", 'B');
        symbolMap.put("empty", ' ');
    }

    private void initializeMap() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = new Location(x, y, TileType.GRASS);
            }
        }
        initializeVillage();
        initializeFarms();
        connectFarmsToVillage();
    }

    private void initializeVillage() {
        int centerX = width / 2;
        int centerY = height / 2;
        int villageRadius = Math.min(width, height) / 4;

        for (int x = centerX - villageRadius; x <= centerX + villageRadius; x++) {
            for (int y = centerY - villageRadius; y <= centerY + villageRadius; y++) {
                if (Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) <= Math.pow(villageRadius, 2)) {
                    tiles[x][y] = new Location(x, y, TileType.GRASS);
                    // TileType.VILLAGE
                }
            }
        }
    }

    private void initializeFarms() {
        int farmSize = Math.min(width, height) / 2;

        farms[0] = new Farm(width / 2 - farmSize / 2, 0, farmSize, farmSize, "North Farm", currentPlayer);
        farms[1] = new Farm(width - farmSize, height / 2 - farmSize / 2, farmSize, farmSize, "East Farm", currentPlayer);
        farms[2] = new Farm(width / 2 - farmSize / 2, height - farmSize, farmSize, farmSize, "South Farm", currentPlayer);
        farms[3] = new Farm(0, height / 2 - farmSize / 2, farmSize, farmSize, "West Farm", currentPlayer);

        for (Farm farm : farms) {
            initializeFarmTiles(farm);
        }
    }

    private void initializeFarmTiles(Farm farm) {
        for (int x = farm.getStartX(); x < farm.getStartX() + farm.getWidth(); x++) {
            for (int y = farm.getStartY(); y < farm.getStartY() + farm.getHeight(); y++) {
                if (x >= 0 && x < width && y >= 0 && y < height) {
                    if (x == farm.getStartX() + farm.getWidth() / 2 &&
                            y == farm.getStartY() + farm.getHeight() / 2) {
                        tiles[x][y] = new Location(x, y, TileType.GRASS);
                        farm.setHousePosition(x, y);
                    }
                    else if (Math.random() < 0.1) {
                        //tiles[x][y] = new Location(x, y, Math.random() < 0.5 ? "tree" : "stone");
                    }
                }
            }
        }
    }

    private void connectFarmsToVillage() {
        int villageCenterX = width / 2;
        int villageCenterY = height / 2;

        for (Farm farm : farms) {
            int farmCenterX = farm.getStartX() + farm.getWidth() / 2;
            int farmCenterY = farm.getStartY() + farm.getHeight() / 2;

            int currentX = farmCenterX;
            int currentY = farmCenterY;

            while (currentX != villageCenterX || currentY != villageCenterY) {
                if (currentX < villageCenterX) {
                    currentX++;
                }
                else if (currentX > villageCenterX) {
                    currentX--;
                }

                if (currentY < villageCenterY) {
                    currentY++;
                }
                else if (currentY > villageCenterY) {
                    currentY--;
                }

                tiles[currentX][currentY] = new Location(currentX, currentY, TileType.PATH);
            }
        }
    }

    public boolean changeTile(int x, int y, String newType, Player player) {
        if (!isValidCoordinate(x, y)) {
            return false;
        }
        if (!isValidTileType(newType)) {
            return false;
        }

        Location tile = tiles[x][y];

        if (!canPlayerModifyTile(player, x, y)) {
            return false;
        }
        if (isProtectedTile(tile.getType())) {
            return false;
        }
//        if (requiresTool(tile.getType(), newType) && !player.hasRequiredTool(getRequiredTool(tile.getType(), newType))) {
//            return false;
//        }
        //kasra

        String previousType = tile.getType();
        tile.setType(newType);
        handleTileChangeEffects(tile, previousType, newType, player);

        return true;
    }

    public boolean isValidCoordinate(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    private boolean isValidTileType(String type) {
        return symbolMap.containsKey(type);
    }

    private boolean canPlayerModifyTile(Player player, int x, int y) {
        for (Farm farm : farms) {
            if (farm.contains(x, y) && farm.getOwner().equals(player)) {
                return true;
            }
        }

        return tiles[x][y].getType().equals("path");
    }

    private boolean
    isProtectedTile(String type) {
        return type.equals("water") || type.equals("village") || type.equals("house");
    }

    //TODO : later it should be only items not strings
//    private boolean requiresTool(String currentType, String newType) {
//        return (currentType.equals("tree") && newType.equals("stump")) ||
//                (currentType.equals("stone") && newType.equals("debris")) ||
//                (currentType.equals("grass") && newType.equals("tilled_soil"));
//    }
//
//    private String getRequiredTool(String currentType, String newType) {
//        if (currentType.equals("tree") && newType.equals("stump")) return "axe";
//        if (currentType.equals("stone") && newType.equals("debris")) return "pickaxe";
//        if (currentType.equals("grass") && newType.equals("tilled_soil")) return "hoe";
//        return "";
//    }

    private void handleTileChangeEffects(Location tile, String previousType, String newType, Player player) {
        if (previousType.equals("tilled_soil") && !newType.equals("tilled_soil")) {
            //tile.setPlant(null);
            //kasra
        }

        if ((previousType.equals("tree") || previousType.equals("stone")) &&
                (newType.equals("stump") || newType.equals("debris"))) {
            //player.addItemToInventory(new Item(previousType.equals("tree") ? "wood" : "stone", 1));
            //kasra
        }
    }

    public void printCurrentView(int centerX, int centerY, int viewRadius) {
        int startX = Math.max(0, centerX - viewRadius);
        int endX = Math.min(width - 1, centerX + viewRadius);
        int startY = Math.max(0, centerY - viewRadius);
        int endY = Math.min(height - 1, centerY + viewRadius);

        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                if (x == centerX && y == centerY) {
                    System.out.print("@ ");
                }
                else {

                    System.out.print(symbolMap.get(tiles[x][y].getType()) + " ");
                }
            }
            System.out.println();
        }
    }

    public Farm getCurrentFarm() {
        return farms[currentFarmIndex];
    }

    public void setCurrentFarm(int index) {
        if (index >= 0 && index < farms.length) {
            currentFarmIndex = index;
        }
    }

    public TileType getTile(int x, int y) {
        if (isValidCoordinate(x, y)) {
            Location location = tiles[x][y];
            return location.getTile();
        }
        return null;
    }

    public boolean isShokhm(int x, int y) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Location tile = tiles[x][y];
                boolean isShokhm = tile.getShokhm();
                if (isShokhm) {
                    return true;
                }
            }
        }
        return false;
    }

    public Location getItem(int x, int y) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Location tile = tiles[x][y];
                return tile;
            }
        }
        return null;
    }

    public void placeItem(int x, int y, Item item) {
        Location tile = tiles[x][y];
        tile.setItem(item);
    }



    // TODO : colision - add item to refrigerator - get inventory

}