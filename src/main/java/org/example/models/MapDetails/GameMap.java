package org.example.models.MapDetails;

import org.example.models.Items.CraftingItem;
import org.example.models.Items.Item;
import org.example.models.Items.Plant;
import org.example.models.Items.Tree;
import org.example.models.Market;
import org.example.models.Player.Backpack;
import org.example.models.Player.Player;
import org.example.models.common.Date;
import org.example.models.common.Location;
import org.example.models.enums.Markets;
import org.example.models.enums.Types.TileType;
import org.example.models.enums.Types.TreeType;

import java.util.*;

public class GameMap {
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String BLUE = "\u001B[34m";
    private static final String RED = "\u001B[31m";
    private static final String GRAY = "\u001B[37m";
    private static final String CYAN = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BROWN = "\u001B[38;5;94m";
    private static final String PURPLE = "\u001B[35m";
    private static final String ORANGE = "\u001B[38;5;208m";
    private static final String PINK = "\u001B[38;5;200m";
    private static final String LIGHT_BLUE = "\u001B[94m";
    private static final String DARK_GRAY = "\u001B[90m";
    private static final String LIGHT_GREEN = "\u001B[92m";
    private static final String LIGHT_RED = "\u001B[91m";
    private static final String GOLD = "\u001B[38;5;220m";


    private final int width;
    private final int height;
    private final Location[][] tiles;
    private final Market[] markets;
    private final Farm[] farms;
    private Village village;
    private int currentFarmIndex;
    private final Map<String, Character> symbolMap;
    private final Player currentPlayer;
    private List<Lake> lakes;

    public GameMap(int width, int height, Player player) {
        this.width = width;
        this.height = height;
        this.tiles = new Location[width][height];
        this.farms = new Farm[4];
        this.markets = new Market[7];
        this.currentFarmIndex = 0;
        this.currentPlayer = player;
        this.symbolMap = new HashMap<>();

        initializeSymbols();
        initializeMap();
        initializeMarkets();
        //initializeLakes();
    }

    public static int calculateEnergyNeeded(Location from, Location to) {
        // Calculate Manhattan distance (|x1 - x2| + |y1 - y2|)
        int distance = Math.abs(from.getX() - to.getX()) + Math.abs(from.getY() - to.getY());

        // Base energy cost per tile
        int baseEnergyCost = 2;

        return distance * baseEnergyCost;
    }

    /**
     * Finds the furthest location a player can go with their remaining energy.
     * This is used when a player doesn't have enough energy to reach their destination.
     *
     * @param from The starting location
     * @param to   The destination location
     * @return The furthest location the player can reach with their remaining energy
     */
    public static Location findFurthestCanGo(Location from, Location to) {
        // Calculate the direction vector
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();

        // Normalize the direction vector
        double length = Math.sqrt(dx * dx + dy * dy);
        if (length == 0) {
            return from; // Already at destination
        }

        double nx = dx / length;
        double ny = dy / length;

        // Assume the player can go 50% of the way (this is a simplification)
        // In a real implementation, you would calculate this based on the player's energy
        int maxDistance = (int) (length * 0.5);

        // Calculate the new position
        int newX = from.getX() + (int) (nx * maxDistance);
        int newY = from.getY() + (int) (ny * maxDistance);

        // Create a new location with the same tile type as the starting location
        return new Location(newX, newY, from.getTile());
    }

    private void initializeSymbols() {
        symbolMap.put("grass", '.');
        symbolMap.put("tilled_soil", '=');
        symbolMap.put("tree", 'T');
        symbolMap.put("stone", 'S');
        symbolMap.put("water", '~');
        symbolMap.put("path", '#');
        symbolMap.put("lake", 'L');
        symbolMap.put("quarry", 'Q');
        symbolMap.put("greenhouse", 'G');
        symbolMap.put("village", 'V');
        symbolMap.put("building", 'B');
        symbolMap.put("empty", ' ');
    }

    private void initializeMap() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Default to grass
                TileType tileType = TileType.GRASS;

                // If the tile is within a lake, make it water
//                if (isInWater(x, y)) {
//                    tileType = TileType.WATER;
//                }

                // Create the tile with the appropriate type
                tiles[x][y] = new Location(x, y, tileType);
            }
        }

        initializeVillage();
        initializeFarms();
        connectFarmsToVillage();

        placeRandomObjects("stone", 30);
        placeRandomObjects("tree", 30);

    }

    private void placeRandomObjects(String type, int count) {
        Random rand = new Random();
        int placed = 0;

        while (placed < count) {
            int x = rand.nextInt(width);
            int y = rand.nextInt(height);
            TileType currentTile = tiles[x][y].getTile();

            if (currentTile == TileType.GRASS) {
                tiles[x][y].setType(type);

                if (type.equals("tree")) {
                    tiles[x][y].setTile(TileType.TREE);

                    TreeType[] types = TreeType.values();
                    TreeType randomType = types[rand.nextInt(types.length)];
                    Tree tree = new Tree(randomType);
                    tiles[x][y].setItem(tree);
                }
                else if (type.equals("stone")) {
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
//                    tiles[x][y] = new Location(x, y, TileType.GRASS);
                    tiles[x][y] = new Location(x, y, TileType.VILLAGE);
                }
            }
        }
    }

    private void initializeFarms() {
        int farmWidth = width / 2;
        int farmHeight = height / 2;

        farms[0] = new Farm(0, 0, farmWidth, farmHeight, "Up Left Farm", currentPlayer);
        farms[1] = new Farm(width / 2, 0, farmWidth, farmHeight, "Up Right Farm", currentPlayer);
        farms[2] = new Farm(0, height / 2, farmWidth, farmHeight, "Down Right Farm", currentPlayer);
        farms[3] = new Farm(width / 2, height / 2, farmWidth, farmHeight, "Down Left Farm", currentPlayer);

        for (Farm farm : farms) {
            initializeFarmTiles(farm);
        }
    }

    private void initializeFarmTiles(Farm farm) {
        for (int x = farm.getStartX(); x < farm.getStartX() + farm.getWidth(); x++) {
            for (int y = farm.getStartY(); y < farm.getStartY() + farm.getHeight(); y++) {
                Location tile = tiles[x][y];
                if (x >= 0 && x < width && y >= 0 && y < height) {
                    if (tile.getTile() != TileType.VILLAGE) {
                        tiles[x][y] = new Location(x, y, TileType.GRASS);
                    }
//                    else {
//                        tiles[x][y] = new Location(x, y, TileType.GRASS);
//                    }
                }
            }
        }

        farm.markBuildingArea(tiles);
        farm.markGreenHouseArea(tiles);
        farm.markQuarry(tiles);
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

    private boolean isProtectedTile(String type) {
        if (type == null) {
            return false;
        }
        return type.equals("water") || type.equals("village") || type.equals("house");
    }

    public Building getBuildingContainingPlayer(Location playerLocation) {
        int x = playerLocation.getX();
        int y = playerLocation.getY();

//        for (Building building : buildings) {
//            if (building.contains(x, y)) {
//                return building;
//            }
//        }
        return null;
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
        if (!isValidCoordinate(x, y)) return false;
        return tiles[x][y].getShokhm();
    }

    public Location getItem(int x, int y) {
        if (!isValidCoordinate(x, y)) return null;
        return tiles[x][y];
    }

    public void placeItem(int x, int y, Item item) {
        Location tile = tiles[x][y];
        tile.setItem(item);
    }

    public List<Location> getPassableNeighbors(Location location) {
        List<Location> result = new ArrayList<>();
        int x = location.getX();
        int y = location.getY();

        int[][] directions = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1}, {0, 1},
                {1, -1}, {1, 0}, {1, 1}
        };

        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];

            if (isInBounds(newX, newY)) {
                Location neighbor = tiles[newX][newY];
                if (isPassable(neighbor)) {
                    result.add(neighbor);
                }
            }
        }

        return result;
    }

    public boolean isPassable(Location location) {
        TileType type = location.getTile();
        return type == TileType.GRASS || type == TileType.PATH;
    }

    public boolean isInBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < width && y < height;
    }

    public boolean isInOtherPlayersFarm(Player player, int x, int y) {
        for (Farm farm : farms) {
            if (farm.contains(x, y) && !farm.getOwner().equals(player)) {
                return true;
            }
        }
        return false;
    }

    public void updatePlants(){
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

    public void updateArtisans(Player player){
        Map<Item , Integer> items = player.getBackpack().getInventory();
        for(Item item : items.keySet()){
            if(item instanceof CraftingItem){
                CraftingItem craftingItem = (CraftingItem) item;
                craftingItem.updateArtisan();
            }
        }
    }

    /**
     * Adds a greenhouse to the game map.
     * A greenhouse is a special area where players can plant crops regardless of the season.
     *
     * @param leftCorner  The top-left corner of the greenhouse
     * @param rightCorner The bottom-right corner of the greenhouse
     */
    public void addGreenhouse(Location leftCorner, Location rightCorner) {
        int startX = leftCorner.getX();
        int startY = leftCorner.getY();
        int endX = rightCorner.getX();
        int endY = rightCorner.getY();

        // Set all tiles in the greenhouse area to GREENHOUSE type
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                if (isValidCoordinate(x, y)) {
                    tiles[x][y].setTile(TileType.GREENHOUSE);
                }
            }
        }
    }

    public void printCurrentViewColored(int centerX, int centerY, int viewRadius) {
        int startX = Math.max(0, centerX - viewRadius);
        int endX = Math.min(width - 1, centerX + viewRadius);
        int startY = Math.max(0, centerY - viewRadius);
        int endY = Math.min(height - 1, centerY + viewRadius);

        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                Location tile = tiles[x][y];
                String type = tile.getType();
                char symbol = symbolMap.getOrDefault(type, '?');

                String color = switch (type) {
                    case "grass" -> GREEN;
                    case "tilled_soil" -> YELLOW;
                    case "tree" -> GREEN;
                    case "stone" -> GRAY;
                    case "water" -> BLUE;
                    case "path" -> YELLOW;
                    case "greenhouse" -> BROWN;
                    case "quarry" -> RED;
                    case "village" -> PURPLE;
                    case "bridge" -> CYAN;
                    case "empty" -> RESET;
                    default -> RESET;
                };

                if (x == centerX && y == centerY) {
                    System.out.print(RED + "@ " + RESET);
                } else {
                    System.out.print(color + symbol + " " + RESET);
                }
            }
            System.out.println();
        }
    }

//    private void initializeLakes() {
//        // Create different lakes in the map
//        // TODO: make it one (TAha)
//        // Ocean on the southern edge of the map
//        lakes.add(new Lake(0, 90, 100, 10, "Ocean", Lake.LakeType.RIVER));
//
//        // River running through the center of the map
//        lakes.add(new Lake(45, 0, 10, 100, "River", Lake.LakeType.RIVER));
//
//        // Mountain lake in the northwest
//        lakes.add(new Lake(10, 10, 20, 15, "Mountain Lake", Lake.LakeType.RIVER));
//
//        // Farm pond in the east
//        lakes.add(new Lake(70, 40, 15, 15, "Farm Pond", Lake.LakeType.RIVER));
//
//        // Secret lake in the forest
//        lakes.add(new Lake(20, 60, 10, 10, "Forest Pond", Lake.LakeType.RIVER));
//
//        // Winter lake (only appears in winter)
//        lakes.add(new Lake(60, 15, 25, 20, "Winter Lake", Lake.LakeType.RIVER));
//
//        // Sewers (special location for Mutant Carp)
//        lakes.add(new Lake(35, 80, 15, 5, "Sewers", Lake.LakeType.RIVER));
//
//        // Initialize fish in lakes based on the current season
//        for (Lake lake : lakes) {
//            lake.updateAvailableFish(org.example.models.App.getGame().getDate().getSeason(),
//                    1); // Default to level 1 fishing skill initially
//        }
//    }

    /**
     * Check if a location is in water (part of a lake).
     *
     * @param x The x-coordinate to check
     * @param y The y-coordinate to check
     * @return true if the location is in water, false otherwise
     */
    public boolean isInWater(int x, int y) {
        for (Lake lake : lakes) {
            if (lake.contains(x, y)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Find the lake at a specific location.
     *
     * @param x The x-coordinate to check
     * @param y The y-coordinate to check
     * @return The lake at the location, or null if no lake is found
     */
    public Lake getLakeAt(int x, int y) {
        for (Lake lake : lakes) {
            if (lake.contains(x, y)) {
                return lake;
            }
        }
        return null;
    }

    public void updateLakeFish(Player player) {
        int fishingSkill = player.getSkillLevel(org.example.models.enums.PlayerEnums.Skills.FISHING);
        for (Lake lake : lakes) {
            lake.updateAvailableFish(org.example.models.App.getGame().getDate().getSeason(), fishingSkill);
        }
    }

    /*
     * 4. MODIFY YOUR TILE INITIALIZATION CODE
     * When creating tiles, set the TileType to WATER for any tile within a lake:
     */

    /*
     * 5. ADD THIS TO YOUR ADVANCEDATE METHOD
     * To ensure lakes update their fish when the season changes:
     */

    // Inside advanceDate or wherever you handle season changes:
    private void checkSeasonChange(org.example.models.common.Date oldDate, Date newDate) {
        if (oldDate.getSeason() != newDate.getSeason()) {
            // Season has changed, update lakes
            for (Player player : org.example.models.App.getGame().getPlayers()) {
                updateLakeFish(player);
            }
        }
    }



    //Initializing markets.
    private void initializeMarkets(){
        markets[0] = Markets.BLACKS_SMITH.createMarket();
        markets[1] = Markets.JOJA_MART.createMarket();
        markets[2] = Markets.PIERRE_GENERAL_STORE.createMarket();
        markets[3] = Markets.CARPENTERS_SHOP.createMarket();
        markets[4] = Markets.FISH_SHOP.createMarket();
        markets[5] = Markets.MARNIE_SHOP.createMarket();
        markets[6] = Markets.STARDROP_SALOON.createMarket();
    }
}
