package org.example.common.models.MapDetails;

import org.example.common.models.App;
import org.example.common.models.Items.*;
import org.example.common.models.Market;
import org.example.common.models.Player.Player;
import org.example.common.models.common.Location;
import org.example.common.models.entities.Game;
import org.example.common.models.entities.NPC;
import org.example.common.models.enums.Markets;
import org.example.common.models.enums.Npcs;
import org.example.common.models.enums.Types.CropType;
import org.example.common.models.enums.Types.MineralType;
import org.example.common.models.enums.Types.TileType;
import org.example.common.models.enums.Types.TreeType;
import org.example.common.models.enums.Charactristic;
import org.example.common.models.enums.Jobs;
import org.example.common.models.Items.Item;

import java.util.*;

public class Village {

    public static final int width = 78;   // Center village width
    public static final int height = 156; // Center village height
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String BLUE = "\u001B[34m";
    private static final String RED = "\u001B[31m";
    private static final String GRAY = "\u001B[37m";
    private static final String CYAN = "\u001B[36m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BROWN = "\u001B[38;5;94m";
    private static final String PURPLE = "\u001B[35m";
    private static final String PINK = "\u001B[38;5;200m";
    private static final String LIGHT_BLUE = "\u001B[94m";
    private static final String BG_WHITE = "\u001B[47m";
    private static final String LIGHT_GREEN = "\u001B[92m";
    private static final String BG_RESET = "\u001B[0m";
    private static final String BG_GREEN = "\u001B[42m";
    private static final String BG_BLUE = "\u001B[44m";
    private static final String BG_BLACK = "\u001B[40m";
    private static final String BG_BRIGHT_BLACK = "\u001B[100m";
    private static final String BG_RED = "\u001B[41m";
    private static final String BG_GRAY = "\u001B[47m";
    private static final String BG_CYAN = "\u001B[46m";
    private static final String BG_YELLOW = "\u001B[43m";
    private static final String BG_PURPLE = "\u001B[45m";
    private static final String BG_BROWN = "\u001B[48;5;94m";
    private static final String BG_PINK = "\u001B[48;5;200m";
    private static final String BG_LIGHT_BLUE = "\u001B[48;5;39m";
    private static final String BG_LIGHT_GREEN = "\u001B[48;5;120m";
    private Market[] markets = new Market[7];
    private Location[][] tiles;
    private List<Building> buildings;
    private Building townHall;
    private Building goldClock;
    private String name;
    private Map<String, Character> symbolMap;
    private List<NPC> residents;

    public Village(String name) {
        this.name = name;
        this.tiles = new Location[width][height];
        this.buildings = new ArrayList<>();
        this.symbolMap = new HashMap<>();
        this.residents = new ArrayList<>();
        initializeVillage();
        initializeSymbols();
        initializeTownHall();
        initializeGoldClock();
        initializeMarkets();
        initializeNPCHouses();
        initializeVillageBuildings();
        markMarketAreas();
        markTownHall();
        markGoldClock();
        markNPCHouses();
        markVillageBuildings();
    }

    public Village() {

    }

    private void initializeSymbols() {
        symbolMap.put("grass", ' ');
        symbolMap.put("tilled_soil", '=');
        symbolMap.put("tree", ' ');
        symbolMap.put("crop", ' ');
        symbolMap.put("stone", ' ');
        symbolMap.put("path", '#');
        symbolMap.put("lake", ' ');
        symbolMap.put("quarry", 'Q');
        symbolMap.put("market", 'M');
        symbolMap.put("shipping_bin", 'S');
        symbolMap.put("greenhouse", 'G');
        symbolMap.put("village", 'V');
        symbolMap.put("building", 'H');
        symbolMap.put("coop", 'C');
        symbolMap.put("barn", 'B');
        symbolMap.put("town_hall", 'T');
        symbolMap.put("gold_clock", 'C');
        symbolMap.put("npc_house", 'N');
        symbolMap.put("village_building", 'B');
        symbolMap.put("empty", ' ');
    }

    public void markMarketAreas() {
        for (Market market : markets) {
            if (market != null) {
                markMarketArea(market);
            }
        }
    }

    private void markMarketArea(Market market) {
        int marketX = market.getX();
        int marketY = market.getY();
        int marketWidth = 3;
        int marketHeight = 3;

        for (int y = marketY; y < marketY + marketHeight; y++) {
            for (int x = marketX; x < marketX + marketWidth; x++) {
                if (contains(x, y)) {
                    tiles[x][y] = new Location(x, y, market.getTileType());
                }
            }
        }
    }

    public Market getMarketAt(Location location) {
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

            if (contains(newX, newY)) {
                for (Market market : markets) {
                    if (market != null && isInMarketArea(market, newX, newY)) {
                        return market;
                    }
                }
            }
        }

        return null;
    }

    private boolean isInMarketArea(Market market, int x, int y) {
        int marketX = market.getX();
        int marketY = market.getY();
        int marketWidth = 3;
        int marketHeight = 3;

        return x >= marketX && x < marketX + marketWidth &&
            y >= marketY && y < marketY + marketHeight;
    }

    public void initializeMarkets() {
        markets[0] = Markets.BLACKS_SMITH.createMarket();
        markets[1] = Markets.JOJA_MART.createMarket();
        markets[2] = Markets.PIERRE_GENERAL_STORE.createMarket();
        markets[3] = Markets.CARPENTERS_SHOP.createMarket();
        markets[4] = Markets.FISH_SHOP.createMarket();
        markets[5] = Markets.MARNIE_SHOP.createMarket();
        markets[6] = Markets.STARDROP_SALOON.createMarket();
    }

    private void initializeTownHall() {
        // Town hall at the center of the village (156x156)
        int townHallX = width / 2 - 2; // Center the 5x5 building
        int townHallY = height / 2 - 2;
        this.townHall = new Building(townHallX, townHallY, "Town Hall", "public");
        buildings.add(townHall);
    }

    private void initializeGoldClock() {
        // Gold Clock at the exact center of the village
        int clockX = width / 2; // Exact center X
        int clockY = height / 2; // Exact center Y
        this.goldClock = new Building(clockX, clockY, "Gold Clock", "public");
        buildings.add(goldClock);
    }

    private void initializeNPCHouses() {
        // Add 5 NPC houses at the bottom of the village
        // Position them in a row at the bottom, with some spacing
        int houseWidth = 5;
        int houseHeight = 5;
        int bottomY = 5; // 5 tiles from bottom edge (Y=0 is bottom, Y=height-1 is top)
        int startX = 10; // Start 10 tiles from left edge
        int spacing = 8; // Space between houses

        for (int i = 0; i < 5; i++) {
            int houseX = startX + (i * (houseWidth + spacing));
            String houseName = "NPC House " + (i + 1);
            Building npcHouse = new Building(houseX, bottomY, houseName, "npc_house");
            // Set the sprite path for each NPC house
            npcHouse.setSpritePath("content/map_elements/npc_house" + (i + 1) + ".png");
            buildings.add(npcHouse);
        }
    }

    private void initializeVillageBuildings() {
        // Add the new village buildings: mayor house, fish pond, museum, and town hall
        // Position them strategically around the village

        // Mayor House - positioned near the top of the village
        Building mayorHouse = new Building(15, 120, "Mayor House", "public");
        mayorHouse.setSpritePath("content/Buildings/mayor_house.png");
        buildings.add(mayorHouse);

        // Fish Pond - positioned near the center but to the right
        Building fishPond = new Building(55, 80, "Fish Pond", "public");
        fishPond.setSpritePath("content/Buildings/fish_pond.png");
        buildings.add(fishPond);

        // Museum - positioned near the center but to the left
        Building museum = new Building(10, 80, "Museum", "public");
        museum.setSpritePath("content/Buildings/museum.png");
        buildings.add(museum);

        // Note: Town Hall is already initialized in initializeTownHall() method
        // We just need to set its sprite path here
        if (townHall != null) {
            townHall.setSpritePath("content/Buildings/town_hall.png");
        }
    }

    private void markTownHall() {
        if (townHall != null) {
            int buildingX = townHall.getX();
            int buildingY = townHall.getY();
            int buildingWidth = townHall.getWidth();
            int buildingHeight = townHall.getHeight();

            for (int y = buildingY; y < buildingY + buildingHeight; y++) {
                for (int x = buildingX; x < buildingX + buildingWidth; x++) {
                    if (contains(x, y)) {
                        tiles[x][y] = new Location(x, y, TileType.BUILDING);
                        tiles[x][y].setType("town_hall");
                    }
                }
            }
        }
    }

    private void markGoldClock() {
        if (goldClock != null) {
            int clockX = goldClock.getX();
            int clockY = goldClock.getY();

            // Mark a 3x5 area for the clock (narrower width, shorter height)
            for (int y = clockY - 2; y <= clockY + 2; y++) {
                for (int x = clockX - 1; x <= clockX + 1; x++) {
                    if (contains(x, y)) {
                        tiles[x][y] = new Location(x, y, TileType.BUILDING);
                        tiles[x][y].setType("gold_clock");
                    }
                }
            }
        }
    }

    private void markNPCHouses() {
        // Mark all NPC houses on the map
        for (Building building : buildings) {
            if (building.getType().equals("npc_house")) {
                int buildingX = building.getX();
                int buildingY = building.getY();
                int buildingWidth = building.getWidth();
                int buildingHeight = building.getHeight();

                for (int y = buildingY; y < buildingY + buildingHeight; y++) {
                    for (int x = buildingX; x < buildingX + buildingWidth; x++) {
                        if (contains(x, y)) {
                            tiles[x][y] = new Location(x, y, TileType.BUILDING);
                            tiles[x][y].setType("npc_house");
                        }
                    }
                }
            }
        }
    }

    private void markVillageBuildings() {
        // Mark all village buildings (mayor house, fish pond, museum) on the map
        for (Building building : buildings) {
            if (building.getType().equals("public") &&
                (building.getName().equals("Mayor House") ||
                 building.getName().equals("Fish Pond") ||
                 building.getName().equals("Museum"))) {

                int buildingX = building.getX();
                int buildingY = building.getY();
                int buildingWidth = building.getWidth();
                int buildingHeight = building.getHeight();

                for (int y = buildingY; y < buildingY + buildingHeight; y++) {
                    for (int x = buildingX; x < buildingX + buildingWidth; x++) {
                        if (contains(x, y)) {
                            tiles[x][y] = new Location(x, y, TileType.BUILDING);
                            tiles[x][y].setType("village_building");
                        }
                    }
                }
            }
        }
    }

    public Market[] getMarkets() {
        return markets;
    }

    public void setMarkets(Market[] markets) {
        this.markets = markets;
    }

    public Building getTownHall() {
        return townHall;
    }

    public Building getGoldClock() {
        return goldClock;
    }

    private void initializeVillage() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = new Location(x, y, TileType.Dirt);
            }
        }

        //initializeBuildings();
        //markBuildings();
        initializeNPCs();
//        initializeShops();

        // Create paths connecting farms to village center
        createVillagePaths();
    }

    private void createVillagePaths() {
        // Create paths from village center to farm entrances
        // Following the same pattern as farm paths for consistency
        // Using coordinates that match the actual farm exit points

        int centerX = width / 2;
        int centerY = height / 2;

        // Define farm entrance points based on actual farm exit coordinates
        // Farms 0 and 1 exit at right edge of farm (x=77, y=39), so they enter village at left edge
        // Farms 2 and 3 exit at left edge of farm (x=0, y=39), so they enter village at right edge
        int farm0EntranceX = 0;  // Left edge of village (for farms 0 and 1)
        int farm0EntranceY = height / 2;  // Center Y coordinate
        int farm1EntranceX = 0;  // Left edge of village (for farms 0 and 1)
        int farm1EntranceY = height / 2;  // Center Y coordinate
        int farm2EntranceX = width - 1;  // Right edge of village (for farms 2 and 3)
        int farm2EntranceY = height / 2;  // Center Y coordinate
        int farm3EntranceX = width - 1;  // Right edge of village (for farms 2 and 3)
        int farm3EntranceY = height / 2;  // Center Y coordinate

        // Create main paths from center to each farm entrance
        // Since farms 0&1 and 2&3 share the same entrance points, we only need 2 paths
        createPathFromCenterToEntrance(centerX, centerY, farm0EntranceX, farm0EntranceY);  // Left entrance
        createPathFromCenterToEntrance(centerX, centerY, farm2EntranceX, farm2EntranceY);  // Right entrance

        // Create additional connecting paths for better navigation
        // Horizontal path connecting left and right entrances
        createHorizontalPath(farm0EntranceY, farm2EntranceY);

        // Create secondary paths for better access to different parts of the village
        // These follow the same branching pattern as farm paths
        createSecondaryPaths(centerX, centerY);

        // Create paths to markets and NPC houses
        createPathsToBuildings(centerX, centerY);
    }

    private void createSecondaryPaths(int centerX, int centerY) {
        // Create additional paths branching from the center, similar to farm path system
        // This creates a more sophisticated path network

        // Path to upper village area
        createPathFromCenterToEntrance(centerX, centerY, centerX, height - 1);

        // Path to lower village area
        createPathFromCenterToEntrance(centerX, centerY, centerX, 0);

        // Diagonal paths for better connectivity
        createDiagonalPath(centerX, centerY, 0, height - 1);  // Upper left
        createDiagonalPath(centerX, centerY, width - 1, height - 1);  // Upper right
        createDiagonalPath(centerX, centerY, 0, 0);  // Lower left
        createDiagonalPath(centerX, centerY, width - 1, 0);  // Lower right
    }

    private void createDiagonalPath(int centerX, int centerY, int targetX, int targetY) {
        // Create a diagonal path from center to target, following the farm path pattern
        // This creates L-shaped paths like the farm system

        // First horizontal segment
        if (centerX != targetX) {
            int startX = Math.min(centerX, targetX);
            int endX = Math.max(centerX, targetX);
            for (int x = startX; x <= endX; x++) {
                if (contains(x, centerY) && tiles[x][centerY].getTile() == TileType.Dirt) {
                    tiles[x][centerY] = new Location(x, centerY, TileType.PATH);
                }
            }
        }

        // Then vertical segment
        if (centerY != targetY) {
            int startY = Math.min(centerY, targetY);
            int endY = Math.max(centerY, targetY);
            for (int y = startY; y <= endY; y++) {
                if (contains(targetX, y) && tiles[targetX][y].getTile() == TileType.Dirt) {
                    tiles[targetX][y] = new Location(targetX, y, TileType.PATH);
                }
            }
        }
    }

    private void createPathFromCenterToEntrance(int centerX, int centerY, int entranceX, int entranceY) {
        // Create a path from village center to farm entrance
        // Similar to farm path logic but adapted for village

        // First, create horizontal path from center to entrance X coordinate
        if (centerX != entranceX) {
            int startX = Math.min(centerX, entranceX);
            int endX = Math.max(centerX, entranceX);
            for (int x = startX; x <= endX; x++) {
                if (contains(x, centerY) && tiles[x][centerY].getTile() == TileType.Dirt) {
                    tiles[x][centerY] = new Location(x, centerY, TileType.PATH);
                }
            }
        }

        // Then, create vertical path from the horizontal path to entrance Y coordinate
        if (centerY != entranceY) {
            int startY = Math.min(centerY, entranceY);
            int endY = Math.max(centerY, entranceY);
            for (int y = startY; y <= endY; y++) {
                if (contains(entranceX, y) && tiles[entranceX][y].getTile() == TileType.Dirt) {
                    tiles[entranceX][y] = new Location(entranceX, y, TileType.PATH);
                }
            }
        }
    }

    private void createHorizontalPath(int leftY, int rightY) {
        // Create horizontal path connecting left and right entrances at similar Y levels
        int pathY = (leftY + rightY) / 2;  // Average Y coordinate

        for (int x = 0; x < width; x++) {
            if (contains(x, pathY) && tiles[x][pathY].getTile() == TileType.Dirt) {
                tiles[x][pathY] = new Location(x, pathY, TileType.PATH);
            }
        }
    }

    private void createVerticalPath(int edgeX, int upperY, int lowerY) {
        // Create vertical path connecting upper and lower entrances at the edge
        for (int y = upperY; y <= lowerY; y++) {
            if (contains(edgeX, y) && tiles[edgeX][y].getTile() == TileType.Dirt) {
                tiles[edgeX][y] = new Location(edgeX, y, TileType.PATH);
            }
        }
    }

    private void createPathsToBuildings(int centerX, int centerY) {
        // Create paths to all markets
        createPathToMarket(centerX, centerY, 25, 130); // Blacksmith
        createPathToMarket(centerX, centerY, 50, 130); // Joja Mart
        createPathToMarket(centerX, centerY, 20, 125); // Pierre General Store
        createPathToMarket(centerX, centerY, 55, 125); // Carpenter's Shop
        createPathToMarket(centerX, centerY, 15, 135); // Marnie Shop
        createPathToMarket(centerX, centerY, 60, 135); // Star Drop Saloon
        createPathToMarket(centerX, centerY, 37, 140); // Fish Shop

        // Create paths to NPC houses at the bottom
        createPathsToNPCHouses(centerX, centerY);
    }

    private void createPathToMarket(int centerX, int centerY, int marketX, int marketY) {
        // Create a path from the market to the middle path in the village
        // Markets are 3x3, so we target the center of the market

        // Connect directly to the middle path (centerX, centerY)
        // First create horizontal path from market to center X
        createPathFromCenterToEntrance(marketX, marketY, centerX, marketY);

        // Then create vertical path from that point to center Y
        createPathFromCenterToEntrance(centerX, marketY, centerX, centerY);
    }

    private void createPathsToNPCHouses(int centerX, int centerY) {
        // NPC houses are positioned at the bottom in a row
        int houseWidth = 5;
        int houseHeight = 5;
        int bottomY = 5; // 5 tiles from bottom edge
        int startX = 10; // Start 10 tiles from left edge
        int spacing = 8; // Space between houses

        for (int i = 0; i < 5; i++) {
            int houseX = startX + (i * (houseWidth + spacing)) + houseWidth / 2; // Center of house
            int houseY = bottomY + houseHeight / 2; // Center of house

            // Create path from house to the middle path
            // First create horizontal path from house to center X
            createPathFromCenterToEntrance(houseX, houseY, centerX, houseY);

            // Then create vertical path from that point to center Y
            createPathFromCenterToEntrance(centerX, houseY, centerX, centerY);
        }
    }

    private int findNearestPathPoint(int targetX, int targetY, boolean isX) {
        // Find the nearest point on existing paths
        // This is a simplified approach - we'll use the center of the village as reference
        int centerX = width / 2;
        int centerY = height / 2;

        if (isX) {
            // For X coordinate, use the center X or the target X if it's closer to an edge
            if (targetX < centerX) {
                return Math.max(0, targetX - 5); // Path from left side
            } else {
                return Math.min(width - 1, targetX + 5); // Path from right side
            }
        } else {
            // For Y coordinate, use the center Y or the target Y if it's closer to an edge
            if (targetY < centerY) {
                return Math.max(0, targetY - 5); // Path from bottom
            } else {
                return Math.min(height - 1, targetY + 5); // Path from top
            }
        }
    }

    private void placeRandomObjects(String type, int count) {
        Random rand = new Random();
        int placed = 0;

        while (placed < count) {
            int x = rand.nextInt(width);
            int y = rand.nextInt(height);
            if ((x == 0 && y == 0) ||
                (x == width - 1 && y == 0) ||
                (x == 0 && y == height - 1) ||
                (x == width - 1 && y == height - 1)) {
                continue;
            }
            TileType currentTile = tiles[x][y].getTile();

            if (currentTile == TileType.Dirt) {
                tiles[x][y].setType(type);

                if (type.equals("tree")) {
                    tiles[x][y].setTile(TileType.TREE);

                    TreeType[] types = TreeType.values();
                    TreeType randomType = types[rand.nextInt(types.length)];
                    Tree tree = new Tree(randomType);
                    tiles[x][y].setItem(tree);
                } else if (type.equals("crop")) {
                    tiles[x][y].setTile(TileType.CROP);

                    CropType[] types = CropType.values();
                    CropType randomType = types[rand.nextInt(types.length)];
                    Crop crop = new Crop(randomType);
                    tiles[x][y].setItem(crop);
                } else if (type.equals("stone")) {
                    tiles[x][y].setTile(TileType.STONE);

                    MineralType[] types = MineralType.values();
                    MineralType randomType = types[rand.nextInt(types.length)];
                    Mineral stone = new Mineral(randomType);
                    tiles[x][y].setItem(stone);
                }

                placed++;
            }
        }
    }

    private void initializeBuildings() {
        buildings.add(new Building(1, 0, "Town Hall", "public"));
        buildings.add(new Building(width - 6, 0, "Blacksmith", "shop"));
        buildings.add(new Building(1, height - 5, "General Store", "shop"));
        buildings.add(new Building(width - 6, height - 5, "Stardrop Saloon", "public"));
    }

    public void markBuildings() {
        for (Building b : buildings) {
            int buildingX = b.getX();
            int buildingY = b.getY();
            int buildingWidth = b.getWidth();
            int buildingHeight = b.getHeight();

            for (int y = buildingY; y < buildingY + buildingHeight; y++) {
                for (int x = buildingX; x < buildingX + buildingWidth; x++) {
                    tiles[x][y] = new Location(x, y, TileType.BUILDING);
                }
            }
        }
    }

    public Building getHouseAt(Location location) {
        int x = location.getX();
        int y = location.getY();
        List<Building> buildings = getBuildings();

        int[][] directions = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1}, {0, 1},
            {1, -1}, {1, 0}, {1, 1}
        };

        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];

            if (contains(newX, newY)) {
                for (Building building : buildings) {
                    if (building.contains(newX, newY)) {
                        return building;
                    }
                }
            }
        }

        return null;
    }

    public static int calculateEnergyNeeded(Location from, Location to) {
        int distance = Math.abs(from.getX() - to.getX()) + Math.abs(from.getY() - to.getY());
        int baseEnergyCost = 2;
        return distance * baseEnergyCost;
    }

    public int walk(int x, int y) {
        Player owner = App.getGame().getCurrentPlayer();
        Location initialLocation = owner.getLocation();
        Location finalLocation = tiles[x][y];

        if (!contains(x, y)) {
            return -1;
        }

        if (finalLocation.getTile() != TileType.Dirt && finalLocation.getTile() != TileType.PATH) {
            return -1;
        }

        Queue<Location> queue = new LinkedList<>();
        Map<Location, Location> parentMap = new HashMap<>();
        Map<Location, Integer> distanceMap = new HashMap<>();
        Set<Location> visited = new HashSet<>();

        queue.add(initialLocation);
        visited.add(initialLocation);
        distanceMap.put(initialLocation, 0);
        boolean found = false;

        while (!queue.isEmpty()) {
            Location current = queue.poll();

            if (current.getX() == x && current.getY() == y) {
                found = true;
                break;
            }

            int[][] directions = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1}, {0, 1},
                {1, -1}, {1, 0}, {1, 1}
            };

            for (int[] dir : directions) {
                int newX = current.getX() + dir[0];
                int newY = current.getY() + dir[1];

                if (!contains(newX, newY)) {
                    continue;
                }

                Location neighbor = tiles[newX][newY];

                if ((neighbor.getTile() == TileType.Dirt || neighbor.getTile() == TileType.PATH) && !visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parentMap.put(neighbor, current);
                    distanceMap.put(neighbor, distanceMap.get(current) + 1);
                    queue.add(neighbor);
                }
            }
        }

        if (!found) {
            return -1;
        }

        int totalDistance = distanceMap.get(finalLocation);
        int requiredEnergy = (int) Math.ceil(totalDistance / 20.0);

        if (owner.getEnergy() < requiredEnergy || !owner.isEnergyUnlimited()) {

            Location current = finalLocation;
            int remainingEnergy = owner.getEnergy();
            Location furthestReachable = initialLocation;

            while (current != initialLocation && remainingEnergy > 0) {
                int currentDistance = distanceMap.get(current);
                int currentEnergyNeeded = (int) Math.ceil(currentDistance / 20.0);

                if (currentEnergyNeeded <= remainingEnergy) {
                    furthestReachable = current;
                    break;
                }

                current = parentMap.get(current);
            }

            int actualDistance = distanceMap.get(furthestReachable);
            int energyUsed = (int) Math.ceil(actualDistance / 20.0);

            owner.setEnergy(owner.getEnergy() - energyUsed);
            owner.setLocation(furthestReachable);
            App.getGame().getCurrentPlayer().setEnergy(owner.getEnergy() - energyUsed);
            App.getGame().getCurrentPlayer().setLocation(furthestReachable);

            return actualDistance;
        } else {
            owner.setEnergy(owner.getEnergy() - requiredEnergy);
            owner.setLocation(finalLocation);
            return totalDistance;
        }
    }

    public void initializeNPCs() {
        Random rand = new Random();

        System.out.println("Village.initializeNPCs(): Starting NPC initialization...");

        // Initialize residents list if it's null
        if (this.residents == null) {
            this.residents = new ArrayList<>();
            System.out.println("Village.initializeNPCs(): Created new residents list");
        } else {
            System.out.println("Village.initializeNPCs(): Residents list already exists with " + residents.size() + " NPCs");
        }

        // Get the game instance once to avoid repeated calls
        Game game = App.getGame();
        if (game == null || game.getCurrentPlayer() == null) {
            System.err.println("Warning: Game or current player is null, NPCs not initialized");
            return;
        }

        System.out.println("Village.initializeNPCs(): Game and current player are valid");

        // Create NPCs using the enum data for NPCs that have sprites available
        // Available sprites: Abigail, Pierre, Sebastian, Leah, Willy, Jojo, Harvey, Robin
        System.out.println("Village.initializeNPCs(): Creating NPCs...");
        createNPCFromEnum("Sebastian");
        createNPCFromEnum("Abigail");
        createNPCFromEnum("Pierre");
        createNPCFromEnum("Leah");
        createNPCFromEnum("Willy");
        createNPCFromEnum("Jojo");
        createNPCFromEnum("Harvey");
        createNPCFromEnum("Robin");
        System.out.println("Village.initializeNPCs(): NPCs initialized. Total residents: " + residents.size());

        // Print details of each NPC
        for (NPC npc : residents) {
            System.out.println("Village.initializeNPCs(): NPC " + npc.getName() + " at position (" + npc.getPosX() + ", " + npc.getPosY() + ") with sprite " + npc.getSpriteName());
        }

        // Print house positions for reference
        System.out.println("Village.initializeNPCs(): House positions:");
        for (Building building : buildings) {
            if (building.getType().equals("npc_house")) {
                System.out.println("Village.initializeNPCs(): House at (" + building.getX() + ", " + building.getY() + ") - " + building.getName());
            }
        }
    }

    private void createNPCWithSprite(String npcName, int x, int y, Charactristic characteristic, Jobs job) {
        // Create NPC with missions
        HashMap<Integer, HashMap<Item, Integer>> missions = new HashMap<>();
        NPC npc = new NPC(characteristic, npcName, job, missions);

        // Set sprite name for rendering
        npc.setSpriteName(npcName);

        // Set position
        Location location = new Location(x, y, TileType.VILLAGE);
        npc.setLocation(location);
        npc.setPosX(x * 60f); // Convert tile coordinates to pixel coordinates
        npc.setPosY(y * 60f);

        // Set description based on NPC
        setNPCDescription(npc, npcName);

        // Add to residents list
        residents.add(npc);
    }

    private void createNPCFromEnum(String npcName) {
        // Get NPC data from enum
        org.example.common.models.enums.Npcs npcEnum = org.example.common.models.enums.Npcs.fromName(npcName);
        if (npcEnum == null) {
            System.err.println("Warning: NPC enum not found for " + npcName);
            return;
        }

        // Create NPC with missions
        HashMap<Integer, HashMap<Item, Integer>> missions = new HashMap<>();
        NPC npc = new NPC(npcEnum.getCharacteristic(), npcEnum.getName(), npcEnum.getJob(), missions);

        // Set sprite name for rendering
        npc.setSpriteName(npcEnum.getName());

        // Position NPC at their current routine location based on time
        int npcX = 15, npcY = 15; // Default values
        try {
            // Get current time and routine
            org.example.common.models.common.Date currentDate = org.example.common.models.App.getGame().getDate();
            if (currentDate != null) {
                int currentHour = currentDate.getHour();
                org.example.common.models.enums.NPCRoutine routine = org.example.common.models.enums.NPCRoutine.fromNpcName(npcName);

                if (routine != null) {
                    // Find current routine point
                    for (org.example.common.models.enums.NPCRoutine.RoutinePoint point : routine.getRoutinePoints()) {
                        if (point.isActiveAt(currentHour)) {
                            npcX = point.getLocation().getX();
                            npcY = point.getLocation().getY();
                            System.out.println("NPC " + npcName + " positioned at routine location (" + npcX + ", " + npcY + ") for hour " + currentHour);
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting routine position for " + npcName + ", using default: " + e.getMessage());
        }

        // Fallback to default positions if routine system fails
        switch (npcName) {
            case "Abigail":
                npcX = 15; // Default home position
                npcY = 15;
                break;
            case "Pierre":
                npcX = 35; // Default home position
                npcY = 15;
                break;
            case "Sebastian":
                npcX = 10; // Default home position
                npcY = 10;
                break;
            case "Leah":
                npcX = 25; // Default home position
                npcY = 25;
                break;
            case "Willy":
                npcX = 5; // Default home position
                npcY = 35;
                break;
            case "Jojo":
                npcX = 40; // Default home position
                npcY = 20;
                break;
            case "Harvey":
                npcX = 20; // Default home position
                npcY = 20;
                break;
            case "Robin":
                npcX = 30; // Default home position
                npcY = 30;
                break;
            default:
                npcX = 15;
                npcY = 15;
                break;
        }

        // Set position under house (using local village coordinates)
        Location location = new Location(npcX, npcY, org.example.common.models.enums.Types.TileType.VILLAGE);
        npc.setLocation(location);

        // Convert to pixel coordinates and add village offset to get world coordinates
        float pixelX = npcX * 60f; // Local village pixel coordinates
        float pixelY = npcY * 60f;

        // Add village offset to get global world coordinates
        float worldX = (GameMap.VILLAGE_X * 60f) + pixelX; // 4680 + pixelX
        float worldY = (GameMap.VILLAGE_Y * 60f) + pixelY; // 0 + pixelY

        System.out.println("Village.createNPCFromEnum(): Setting " + npcName + " to world coordinates (" + worldX + ", " + worldY + ") from local (" + npcX + ", " + npcY + ")");

        npc.setPosX(worldX);
        npc.setPosY(worldY);

        // Ensure NPC is in proper idle state (not moving, idle animation)
        npc.forceIdleState();

        System.out.println("NPC " + npcName + " positioned at local (" + npcX + ", " + npcY + ") -> world (" + worldX + ", " + worldY + ")");

        // Set description from enum
        npc.setDescription(npcEnum.getDescription());

        // Add favorite items from enum
        for (String itemName : npcEnum.getFavoriteItems()) {
            // TODO: Convert string item names to actual Item objects
            // For now, we'll just use the string names
        }

        // Add to residents list
        residents.add(npc);

        System.out.println("Village.createNPCFromEnum(): Positioned " + npcName + " at (" + npcX + ", " + npcY + ")");
    }

    private void setNPCDescription(NPC npc, String npcName) {
        switch (npcName) {
            case "Abigail":
                npc.setDescription("A spirited young woman with a love for adventure and the supernatural. She enjoys exploring caves and playing the flute.");
                break;
            case "Pierre":
                npc.setDescription("The owner of the local general store who is always looking to make a profit. He's constantly worried about competition.");
                break;
            case "Sebastian":
                npc.setDescription("A reclusive young man who lives in his mom's basement. He's a programmer and enjoys solitude.");
                break;
            case "Leah":
                npc.setDescription("An artist who lives in a small cabin near the river. She loves nature and creates sculptures from foraged materials.");
                break;
            case "Willy":
                npc.setDescription("An old fisherman who runs the fishing shop on the pier. He has a weathered face and always smells of the sea.");
                break;
            case "Jojo":
                npc.setDescription("A hardworking merchant who runs a shop in the village. He's always looking for good deals.");
                break;
            case "Harvey":
                npc.setDescription("The town's doctor who runs the local clinic. He's caring and concerned about everyone's health.");
                break;
            case "Robin":
                npc.setDescription("The local carpenter who runs the carpentry shop. She's skilled at building and loves working with wood.");
                break;
            default:
                npc.setDescription("A friendly villager who lives in the town.");
                break;
        }
    }

//    private void initializeShops() {
//        //...
//    }

    private String getTileKey(int x, int y) {
        return x + "," + y;
    }

    public TileType getTile(int x, int y) {
        if (contains(x, y)) {
            Location location = tiles[x][y];
            return location.getTile();
        }
        return null;
    }

//    public boolean setTile(int x, int y, Tile tile) {
//        if (contains(x, y)) {
//            tiles.put(getTileKey(x, y), tile);
//            return true;
//        }
//        return false;
//    }

    private boolean isValidTileType(String type) {
        return symbolMap.containsKey(type);
    }

    public Location getItem(int x, int y) {
        if (!contains(x, y)) return null;
        return tiles[x][y];
    }

    public void placeItem(int x, int y, Item item) {
        Location tile = tiles[x][y];
        tile.setItem(item);
    }

    public boolean isPassable(Location location) {
        TileType type = location.getTile();
        return type == TileType.Dirt || type == TileType.PATH;
    }

    public boolean contains(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
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

    public List<NPC> getResidents() {
        return residents;
    }

    public void setResidents(List<NPC> residents) {
        this.residents = residents;
    }

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

    public static Location findFurthestCanGo(Location from, Location to) {
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();

        double length = Math.sqrt(dx * dx + dy * dy);
        if (length == 0) {
            return from;
        }

        double nx = dx / length;
        double ny = dy / length;

        int maxDistance = (int) (length * 0.5);

        int newX = from.getX() + (int) (nx * maxDistance);
        int newY = from.getY() + (int) (ny * maxDistance);

        return new Location(newX, newY, from.getTile());
    }

    public void printCurrentViewColored(int centerX, int centerY, int viewRadius) {
        int startX = 0;
        int endX = Math.min(width - 1, centerX + viewRadius);
        int startY = 0;
        int endY = Math.min(height - 1, centerY + viewRadius);

        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                Location tile = tiles[x][y];
                String type = tile.getType();
                char symbol = symbolMap.getOrDefault(type, '?');

                String color = switch (type) {
                    case "grass" -> BG_LIGHT_GREEN;
                    case "tilled_soil" -> YELLOW;
                    case "tree" -> BG_GREEN;
                    case "crop" -> BG_PINK;
                    case "stone" -> BG_BRIGHT_BLACK;
                    case "lake" -> BG_BLUE;
                    case "path" -> BG_YELLOW;
                    case "coop" -> BG_PINK;
                    case "barn" -> BG_LIGHT_BLUE;
                    case "market" -> BG_WHITE;
                    case "greenhouse" -> BG_BROWN;
                    case "building" -> BG_WHITE;
                    case "town_hall" -> BG_PURPLE;
                    case "quarry" -> BG_RED;
                    case "village" -> BG_PURPLE;
                    case "shipping_bin" -> BG_CYAN;
                    case "bridge" -> CYAN;
                    case "empty" -> RESET;
                    default -> RESET;
                };

//                for (NPC npc : residents) {
//                    if (x == npc.getLocation().getX() && y == npc.getLocation().getY()) {
//                        System.out.println(GREEN + "N " + RESET);
//                    }
//                }
                List<Player> players = App.getGame().getGameMap().getPlayers();
                List<Player> playersInVillage = new ArrayList<>();
                for (Player p : players) {
                    if (p.getIsInVillage()) {
                        playersInVillage.add(p);
                    }
                }
                for (Player p : playersInVillage) {
                    Location location = p.getLocation();
                    String playerColor = p.getPlayerColor();
                    if (x == location.getX() && y == location.getY()) {
                        System.out.print(playerColor + "@ " + RESET);
                    } else {
                        System.out.print(color + symbol + " " + RESET);
                    }
                }
            }
            System.out.println();
        }
    }

    public void updateShippingBin(Player player) {
        ShippingBin[] bins = new ShippingBin[4];
        bins[0] = new ShippingBin();
        bins[1] = new ShippingBin();
        bins[2] = new ShippingBin();
        bins[3] = new ShippingBin();

        player.increaseMoney(bins[0].getIncome(player));
        bins[1].updateShippingBin(player);

        player.increaseMoney(bins[1].getIncome(player));
        bins[1].updateShippingBin(player);

        player.increaseMoney(bins[2].getIncome(player));
        bins[2].updateShippingBin(player);

        player.increaseMoney(bins[3].getIncome(player));
        bins[3].updateShippingBin(player);
    }

    public Location[][] getTiles() {
        return tiles;
    }

}
