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
import org.example.models.Market;
import org.example.models.enums.Markets;
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
    private static final String LIGHT_GREEN = "\u001B[92m";
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
        symbolMap.put("grass", '.');
        symbolMap.put("tilled_soil", '=');
        symbolMap.put("tree", 'T');
        symbolMap.put("crop", 'C');
        symbolMap.put("stone", 'S');
        symbolMap.put("market", 'M');
        symbolMap.put("path", '#');
        symbolMap.put("lake", '~');
        symbolMap.put("quarry", 'Q');
        symbolMap.put("greenhouse", 'G');
        symbolMap.put("village", 'V');
        symbolMap.put("market", 'M');
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
                {0, -1},          {0, 1},
                {1, -1},  {1, 0}, {1, 1}
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
        placeRandomObjects("crop", 20);
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

        while (placed < count) {
            int x = rand.nextInt(width);
            int y = rand.nextInt(height);
            Location location = tiles[x][y];
            TileType currentTile = location.getTile();

            if (currentTile == TileType.GRASS) {
                Npcs[] types = Npcs.values();
                Npcs npcType = types[rand.nextInt(types.length)];
                Game game = App.getGame();
                if (game != null) {
                    NPC npc = App.getGame().getCurrentPlayer().createNPCFromEnum(npcType);
                    residents.add(npc);
                    npc.setLocation(location);
                    placed++;
                }
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

    public void printCurrentViewColored(int centerX, int centerY, int viewRadius, Player player) {
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
                    case "grass" -> GREEN;
                    case "tilled_soil" -> YELLOW;
                    case "tree" -> GREEN;
                    case "crop" -> LIGHT_GREEN;
                    case "stone" -> GRAY;
                    case "lake" -> BLUE;
                    case "path" -> YELLOW;
                    case "coop" -> PINK;
                    case "barn" -> LIGHT_BLUE;
                    case "greenhouse" -> BROWN;
                    case "quarry" -> RED;
                    case "village" -> PURPLE;
                    case "bridge" -> CYAN;
                    case "empty" -> RESET;
                    default -> RESET;
                };

//                for (NPC npc : residents) {
//                    if (x == npc.getLocation().getX() && y == npc.getLocation().getY()) {
//                        System.out.println(GREEN + "N " + RESET);
//                    }
//                }
                if (x == player.getLocation().getX() && y == player.getLocation().getY()) {
                    System.out.print(RED + "@ " + RESET);
                } else {
                    System.out.print(color + symbol + " " + RESET);
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