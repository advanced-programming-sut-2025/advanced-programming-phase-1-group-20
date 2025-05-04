package org.example.models.MapDetails;

import org.example.models.Items.Item;
import org.example.models.Items.Plant;
import org.example.models.Items.Tree;
import org.example.models.Player.Player;
import org.example.models.common.Location;
import org.example.models.enums.Types.TileType;
import org.example.models.enums.Types.TreeType;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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

    public static int calculateEnergyNeeded(Location location1, Location location2) {
        int distanceInTiles = Math.abs(location1.xAxis - location2.xAxis) +
                Math.abs(location1.yAxis - location2.yAxis);

        int numberOfTurns = calculateNumberOfTurns(location1, location2);

        int energyNeeded = (int) Math.ceil((distanceInTiles + 10 * numberOfTurns) / 20.0);

        return energyNeeded;
    }

    private static int calculateNumberOfTurns(Location location1, Location location2) {
        if (location1.xAxis == location2.xAxis && location1.yAxis == location2.yAxis) {
            return 0;
        }

        if (location1.xAxis == location2.xAxis || location1.yAxis == location2.yAxis) {
            return 0;
        }

        return calculateMinimumTurns(location1, location2);
    }

    private static int calculateMinimumTurns(Location location1, Location location2) {
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

        int minTurns = Integer.MAX_VALUE;

        boolean[][] visited = new boolean[1000][1000]; // Assuming map size is less than 1000x1000

        for (int dirIndex = 0; dirIndex < 4; dirIndex++) {
            for (int i = 0; i < visited.length; i++) {
                for (int j = 0; j < visited[0].length; j++) {
                    visited[i][j] = false;
                }
            }

            int turns = dfsCalculateTurns(location1.xAxis, location1.yAxis,
                    location2.xAxis, location2.yAxis,
                    dirIndex, 0, directions, visited);
            minTurns = Math.min(minTurns, turns);
        }

        if (minTurns == Integer.MAX_VALUE) {
            return 2;
        }

        return minTurns;
    }

    private static int dfsCalculateTurns(int x, int y, int destX, int destY,
                                         int currentDir, int turns, int[][] directions, boolean[][] visited) {
        if (x == destX && y == destY) {
            return turns;
        }

        visited[x + 500][y + 500] = true; // Offset to handle negative coordinates

        int minTurns = Integer.MAX_VALUE;

        for (int newDir = 0; newDir < 4; newDir++) {
            int newX = x + directions[newDir][0];
            int newY = y + directions[newDir][1];

            if (visited[newX + 500][newY + 500]) {
                continue;
            }

            // Check if the new position is walkable (GRASS)
            if (!isWalkable(newX, newY)) {
                continue; // Skip non-walkable tiles
            }

            if (Math.abs(newX - destX) + Math.abs(newY - destY) < Math.abs(x - destX) + Math.abs(y - destY)) {
                int newTurns = turns;
                if (newDir != currentDir) {
                    newTurns++;
                }

                int result = dfsCalculateTurns(newX, newY, destX, destY, newDir, newTurns, directions, visited);
                minTurns = Math.min(minTurns, result);
            }
        }

        visited[x + 500][y + 500] = false;

        return minTurns;
    }

    public static String getPathWithTurns(Location location1, Location location2) {
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        String[] dirNames = {"up", "right", "down", "left"};

        int minTurns = Integer.MAX_VALUE;
        int bestStartDir = 0;

        boolean[][] visited = new boolean[1000][1000]; // Assuming map size is less than 1000x1000

        for (int dirIndex = 0; dirIndex < 4; dirIndex++) {
            for (int i = 0; i < visited.length; i++) {
                for (int j = 0; j < visited[0].length; j++) {
                    visited[i][j] = false;
                }
            }

            int turns = dfsCalculateTurns(location1.xAxis, location1.yAxis,
                    location2.xAxis, location2.yAxis,
                    dirIndex, 0, directions, visited);
            if (turns < minTurns) {
                minTurns = turns;
                bestStartDir = dirIndex;
            }
        }

        StringBuilder path = new StringBuilder();
        path.append("Start at (").append(location1.xAxis).append(", ").append(location1.yAxis).append(")\n");
        path.append("Initial direction: ").append(dirNames[bestStartDir]).append("\n");

        int x = location1.xAxis;
        int y = location1.yAxis;
        int currentDir = bestStartDir;
        int turnCount = 0;

        // Reset visited array for path reconstruction
        for (int i = 0; i < visited.length; i++) {
            for (int j = 0; j < visited[0].length; j++) {
                visited[i][j] = false;
            }
        }

        visited[x + 500][y + 500] = true;

        int maxSteps = Math.abs(location2.xAxis - location1.xAxis) + Math.abs(location2.yAxis - location1.yAxis) + 10;
        int steps = 0;

        while ((x != location2.xAxis || y != location2.yAxis) && steps < maxSteps) {
            boolean moved = false;
            steps++;

            for (int newDir = 0; newDir < 4; newDir++) {
                int newX = x + directions[newDir][0];
                int newY = y + directions[newDir][1];

                // Skip if already visited
                if (visited[newX + 500][newY + 500]) {
                    continue;
                }

                if (!isWalkable(newX, newY)) {
                    continue;
                }

                if (Math.abs(newX - location2.xAxis) + Math.abs(newY - location2.yAxis) <
                        Math.abs(x - location2.xAxis) + Math.abs(y - location2.yAxis)) {

                    if (newDir != currentDir) {
                        turnCount++;
                        path.append("Turn to ").append(dirNames[newDir]).append(" at (")
                                .append(x).append(", ").append(y).append(")\n");
                    }

                    x = newX;
                    y = newY;
                    currentDir = newDir;
                    visited[x + 500][y + 500] = true;
                    moved = true;
                    break;
                }
            }

            if (!moved) {
                path.append("No valid move found from (").append(x).append(", ").append(y).append(")\n");
                break;
            }
        }

        if (steps >= maxSteps) {
            path.append("Path reconstruction exceeded maximum steps. Possible infinite loop.\n");
        }

        path.append("Arrive at (").append(location2.xAxis).append(", ").append(location2.yAxis).append(")\n");
        path.append("Total turns: ").append(turnCount);

        return path.toString();
    }

    public static Location findFurthestCanGo(Location location1, Location location2) {
        if (location1.xAxis == location2.xAxis && location1.yAxis == location2.yAxis) {
            return location1;
        }

        if (location2.getTile() != TileType.GRASS) {
            return location1; // Can't walk on non-GRASS tiles
        }

        // Calculate the direction vector
        int dx = Integer.compare(location2.xAxis - location1.xAxis, 0);
        int dy = Integer.compare(location2.yAxis - location1.yAxis, 0);

        int totalDistance = Math.abs(location1.xAxis - location2.xAxis) +
                Math.abs(location1.yAxis - location2.yAxis);


        int maxDistance = totalDistance; // Assuming player has enough energy

        int furthestX = location1.xAxis + dx * Math.min(maxDistance, Math.abs(location2.xAxis - location1.xAxis));
        int furthestY = location1.yAxis + dy * Math.min(maxDistance - Math.min(maxDistance, Math.abs(location2.xAxis - location1.xAxis)),
                Math.abs(location2.yAxis - location1.yAxis));

        return new Location(furthestX, furthestY, TileType.GRASS);
    }

    public static boolean isWalkable(int x, int y) {
        if (x < 0 || y < 0) {
            return false;
        }

        GameMap tempMap = new GameMap(1000, 1000, null);
        TileType tileType = tempMap.getTile(x, y);

        return tileType == TileType.GRASS;
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

        placeRandomObjects("tree", 30);
        placeRandomObjects("stone", 30);
    }

    private void placeRandomObjects(String type, int count) {
        Random rand = new Random();
        int placed = 0;

        while (placed < count) {
            int x = rand.nextInt(width);
            int y = rand.nextInt(height);
            String currentType = tiles[x][y].getType();

            if (!isProtectedTile(currentType) && !currentType.equals("tree") && !currentType.equals("stone")) {
                tiles[x][y].setType(type);

                if (type.equals("tree")) {
                    tiles[x][y].setTile(TileType.TREE);

                    TreeType[] types = TreeType.values();
                    TreeType randomType = types[rand.nextInt(types.length)];
                    Tree tree = new Tree(randomType);
                    tiles[x][y].setItem(tree);
                } else if (type.equals("stone")) {
                    tiles[x][y].setTile(TileType.STONE);
                }

                placed++;
            }
        }
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

    private void initializeFarmTiles(Farm farm) {
        for (int x = farm.getStartX(); x < farm.getStartX() + farm.getWidth(); x++) {
            for (int y = farm.getStartY(); y < farm.getStartY() + farm.getHeight(); y++) {
                if (x >= 0 && x < width && y >= 0 && y < height) {
                    if (x == farm.getStartX() + farm.getWidth() / 2 &&
                            y == farm.getStartY() + farm.getHeight() / 2) {
                        tiles[x][y] = new Location(x, y, TileType.GRASS);
                        farm.setHousePosition(x, y);
                    } else if (Math.random() < 0.1) {
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
                } else if (currentX > villageCenterX) {
                    currentX--;
                }

                if (currentY < villageCenterY) {
                    currentY++;
                } else if (currentY > villageCenterY) {
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

    // TODO : check shokhm - collision - get item from location - add item to refrigerator - get inventory - place item

    public void printCurrentView(int centerX, int centerY, int viewRadius) {
        int startX = Math.max(0, centerX - viewRadius);
        int endX = Math.min(width - 1, centerX + viewRadius);
        int startY = Math.max(0, centerY - viewRadius);
        int endY = Math.min(height - 1, centerY + viewRadius);

        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                if (x == centerX && y == centerY) {
                    System.out.print("@ ");
                } else {

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

    public Item getItem(int x, int y) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Location tile = tiles[x][y];
                return tile.getItem();
            }
        }
        return null;
    }

    public void placeItem(int x, int y, Item item) {
        Location tile = tiles[x][y];
        tile.setItem(item);
    }

    public boolean isInOtherPlayersFarm(Player player, int x, int y) {
        for (Farm farm : farms) {
            if (farm.contains(x, y) && !farm.getOwner().equals(player)) {
                return true;
            }
        }
        return false;
    }

    public void addGreenhouse(Location leftCorner, Location rightCorner) {
        GreenHouse greenhouse = new GreenHouse(leftCorner, rightCorner);

        for (int x = leftCorner.xAxis; x <= rightCorner.xAxis; x++) {
            for (int y = leftCorner.yAxis; y <= rightCorner.yAxis; y++) {
                if (isValidCoordinate(x, y)) {
                    tiles[x][y].setTile(TileType.GREENHOUSE);
                }
            }
        }
    }


    public void updateItems(){
        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                Location tile = tiles[x][y];
                if(tile.getItem() != null){
                    if(tile.getItem() instanceof Tree){
                        tile.getItem().updateItem();
                        Tree tree = (Tree) tile.getItem();
                        if(!tree.getMoisture()){
                            tile.setItem(null);
                        }
                    }else if(tile.getItem() instanceof Plant){
                        tile.getItem().updateItem();
                        Plant plant = (Plant) tile.getItem();
                        if(!plant.getMoisture()){
                            tile.setItem(null);
                        }
                    }
                }
            }
        }
    }

    public void thor(Location location) {
        int x = location.getX();
        int y = location.getY();
        for(int i = x ; i < x + 4 ; i++){
            for(int j = y ; j < y + 4 ; j++){
                Location tile = tiles[x][y];
                if(tile.getTile() != null){
                    if(tile.getItem() instanceof Tree){
                        Tree tree = (Tree) tile.getItem();
                        tile.setItem(tree.burnTree());
                    }
                }
            }
        }
    }

    public void attackOfTheCrows(){

    }
}