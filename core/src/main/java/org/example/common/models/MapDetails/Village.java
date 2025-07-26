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
    private final Market[] markets = new Market[7];
    private Location[][] tiles;
    private List<Building> buildings;
    private Building townHall;
    private String name;
    //private List<Shop> shops;
    private Map<String, Character> symbolMap;
    private List<NPC> residents;

    public Village(String name) {
        this.name = name;
        this.tiles = new Location[width][height];
        this.buildings = new ArrayList<>();
        this.symbolMap = new HashMap<>();
        this.residents = new ArrayList<>();
        //this.shops = new ArrayList<>();
        initializeVillage();
        initializeSymbols();
        initializeTownHall();
        initializeMarkets();
        markMarketAreas();
        markTownHall();
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

    private void initializeMarkets() {
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

    public Market[] getMarkets() {
        return markets;
    }

    public Building getTownHall() {
        return townHall;
    }

    private void initializeVillage() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = new Location(x, y, TileType.Dirt);
            }
        }

        //initializeBuildings();
        //markBuildings();
//        initializeNPCs();
//        initializeShops();

        placeRandomObjects("stone", 100);
        placeRandomObjects("tree", 100);
        //TODO: درخت و سنگ داره یا نه؟

        // Create paths connecting farms to village center
        createVillagePaths();
    }

    private void createVillagePaths() {
        // Create paths from village boundaries to center
        // These paths will connect to the farm entrances

        int centerX = width / 2;
        int centerY = height / 2;

        // Path from left edge (Farms 0 and 1) to center
        createPathFromEdgeToCenter(0, centerY, centerX, centerY);

        // Path from right edge (Farms 2 and 3) to center
        createPathFromEdgeToCenter(width - 1, centerY, centerX, centerY);

        // Path from top edge (Farms 1 and 2) to center
        createPathFromEdgeToCenter(centerX, height - 1, centerX, centerY);

        // Path from bottom edge (Farms 0 and 3) to center
        createPathFromEdgeToCenter(centerX, 0, centerX, centerY);
    }

    private void createPathFromEdgeToCenter(int startX, int startY, int endX, int endY) {
        // Create a path from the edge to the center using a simple line algorithm
        int x = startX;
        int y = startY;

        while (x != endX || y != endY) {
            // Mark current position as path
            if (contains(x, y)) {
                tiles[x][y] = new Location(x, y, TileType.PATH);
            }

            // Move towards center
            if (x < endX) x++;
            else if (x > endX) x--;

            if (y < endY) y++;
            else if (y > endY) y--;
        }

        // Mark the center point as path
        if (contains(endX, endY)) {
            tiles[endX][endY] = new Location(endX, endY, TileType.PATH);
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

    public int walk(int x, int y) {
        Player owner = App.getGame().getCurrentPlayer();
        Location initialLocation = owner.getLocation();
        Location finalLocation = tiles[x][y];

        if (!contains(x, y)) {
            return -1;
        }

        if (finalLocation.getTile() != TileType.Dirt) {
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

                if (neighbor.getTile() == TileType.Dirt && !visited.contains(neighbor)) {
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
        
        // Initialize residents list if it's null
        if (this.residents == null) {
            this.residents = new ArrayList<>();
        }

        // Get the game instance once to avoid repeated calls
        Game game = App.getGame();
        if (game == null || game.getCurrentPlayer() == null) {
            System.err.println("Warning: Game or current player is null, NPCs not initialized");
            return;
        }

        // Create NPCs with their sprites
        createNPCWithSprite("Abigail", 20, 50, Charactristic.HARD_WORKING, Jobs.STUDENT);
        createNPCWithSprite("Pierre", 80, 80, Charactristic.GREEDY, Jobs.SELLER);
        createNPCWithSprite("Sebastian", 40, 120, Charactristic.LAZY, Jobs.ENGINEER);
        createNPCWithSprite("Leah", 120, 40, Charactristic.JEALOUS, Jobs.STUDENT);
        createNPCWithSprite("Willy", 60, 200, Charactristic.KIND, Jobs.FISHER);
        createNPCWithSprite("Jojo", 140, 160, Charactristic.HARD_WORKING, Jobs.SELLER);
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
