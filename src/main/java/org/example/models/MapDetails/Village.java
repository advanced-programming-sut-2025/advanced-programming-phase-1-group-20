package org.example.models.MapDetails;

import org.example.models.App;
import org.example.models.Items.*;
import org.example.models.Market;
import org.example.models.Player.Player;
import org.example.models.common.Location;
import org.example.models.entities.Game;
import org.example.models.entities.NPC;
import org.example.models.enums.Markets;
import org.example.models.enums.Npcs;
import org.example.models.enums.Types.CropType;
import org.example.models.enums.Types.MineralType;
import org.example.models.enums.Types.TileType;
import org.example.models.enums.Types.TreeType;

import java.util.*;

public class Village {

    public static final int width = 50;
    public static final int height = 50;
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
    private final Location[][] tiles;
    private final List<Building> buildings;
    private final String name;
    //private List<Shop> shops;
    private final Map<String, Character> symbolMap;
    private final Market[] markets = new Market[7];
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
        initializeMarkets();
        markMarketAreas();
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
                    tiles[x][y] = new Location(x, y, TileType.MARKET);
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

    public Market[] getMarkets() {
        return markets;
    }

    private void initializeVillage() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = new Location(x, y, TileType.GRASS);
            }
        }

        initializeBuildings();
        markBuildings();
//        initializeNPCs();
//        initializeShops();

        placeRandomObjects("stone", 20);
        placeRandomObjects("tree", 20);
        //TODO: درخت و سنگ داره یا نه؟
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
        buildings.add(new Building(width - 5, 0, "Blacksmith", "shop"));
        buildings.add(new Building(1, height - 5, "General Store", "shop"));
        buildings.add(new Building(width - 5, height - 5, "Stardrop Saloon", "public"));
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

    public void initializeNPCs() {
        Random rand = new Random();
        int count = 5;
        int placed = 0;
        int attempts = 0;

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

        // First, collect all valid grass locations
        List<Location> validLocations = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (tiles[x][y] != null && tiles[x][y].getTile() == TileType.GRASS) {
                    validLocations.add(tiles[x][y]);
                }
            }
        }

        // If we don't have enough valid locations, log a warning and use what we have
        if (validLocations.size() < count) {
            System.out.println("Warning: Not enough grass tiles for NPCs. Found: " + validLocations.size());
            count = Math.max(validLocations.size(), 1); // At least try to place one NPC
        }

        // If we have no valid locations at all, return early
        if (validLocations.isEmpty()) {
            System.err.println("Error: No valid grass tiles found for NPC placement");
            return;
        }

        // Shuffle the valid locations to get random placement
        Collections.shuffle(validLocations, rand);

        // Place NPCs on the shuffled locations
        for (int i = 0; i < count && i < validLocations.size(); i++) {
            Location location = validLocations.get(i);
            Npcs[] types = Npcs.values();
            Npcs npcType = types[rand.nextInt(types.length)];

            try {
                NPC npc = game.getCurrentPlayer().createNPCFromEnum(npcType);
                if (npc != null) {
                    npc.setLocation(location);
                    residents.add(npc);
                    placed++;
                }
            } catch (Exception e) {
                System.err.println("Error creating NPC: " + e.getMessage());
            }
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
        return type == TileType.GRASS || type == TileType.PATH;
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
                    case "market" -> BG_PINK;
                    case "greenhouse" -> BG_BROWN;
                    case "building" -> BG_WHITE;
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
                    }
                    else {
                        System.out.print(color + symbol + " " + RESET);
                    }
                }
            }
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