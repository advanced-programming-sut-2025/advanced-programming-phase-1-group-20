package org.example.models.MapDetails;

import org.example.models.App;
import org.example.models.Barn;
import org.example.models.Coop;
import org.example.models.Items.*;
import org.example.models.Player.Player;
import org.example.models.common.Date;
import org.example.models.common.Location;
import org.example.models.entities.animal.Animal;
import org.example.models.enums.Types.CropType;
import org.example.models.enums.Types.MineralType;
import org.example.models.enums.Types.TileType;
import org.example.models.enums.Types.TreeType;

import java.util.*;

public class Farm {
    public static final int width = 51;
    public static final int height = 51;
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
    private final String name;
    private final Player owner;
    private final boolean farmType;
    private final int farmIndex;
    private final Location[][] tiles;
    private final List<Animal> animals;
    private final Building building;
    private final List<Lake> lakes;
    private final GreenHouse greenHouse;
    private final Quarry quarry;
    private final List<Barn> barns;
    private final List<Coop> coops;
    private final Map<String, Character> symbolMap;
    private final List<ShippingBin> shippingBins;

    public Farm(String name, Player owner, boolean farmType, int farmIndex) {
        this.farmType = farmType;
        this.farmIndex = farmIndex;
        this.name = name;
        this.owner = owner;
        this.symbolMap = new HashMap<>();
        this.tiles = new Location[width][height];
        this.animals = new ArrayList<>();
        this.barns = new ArrayList<>();
        this.coops = new ArrayList<>();
        this.building = createBuilding();
        this.lakes = createLakes();
        this.greenHouse = createGreenHouse();
        this.quarry = createQuarry();
        this.shippingBins = new ArrayList<>();
        owner.setPlayerColor(setOwnerColor());

        initializeFarm();
        initializeSymbols();
        setInitialOwnerLocation();
    }

    public String setOwnerColor() {
        switch (farmIndex) {
            case 0:
                return BG_BLACK;
            case 1:
                return BG_PINK;
            case 2:
                return BG_WHITE;
            case 3:
                return BG_CYAN;
            default:
                return BG_RESET;
        }
    }

    public Building getHouseAt(Location location) {
        int x = location.getX();
        int y = location.getY();
        Building building = getBuilding();

        int[][] directions = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1},          {0, 1},
                {1, -1},  {1, 0}, {1, 1}
        };

        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];

            if (contains(newX, newY)) {
                if (building.contains(newX, newY)) {
                    return building;
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

    public void setInitialOwnerLocation() {
        Location location = tiles[width / 2][height / 2];
        owner.setLocation(location);
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

    public Building getHouse() {
        return building;
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
        symbolMap.put("shipping_bin", 'S');
        symbolMap.put("greenhouse", 'G');
        symbolMap.put("plowed", ' ');
        symbolMap.put("village", 'V');
        symbolMap.put("building", 'H');
        symbolMap.put("coop", 'C');
        symbolMap.put("barn", 'B');
        symbolMap.put("empty", ' ');
    }

    public boolean contains(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public void initializeFarm() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = new Location(x, y, TileType.GRASS);
            }
        }

        markBuildingArea();
        markGreenHouseArea();
        markQuarry();
        markLakes();

        placeRandomObjects("stone", 100);
        placeRandomObjects("tree", 150);
        placeRandomObjects("crop", 100);
    }

    public void addShippingBin() {
        ShippingBin newShippingBin = new ShippingBin();
        shippingBins.add(newShippingBin);
        markShippingBin(newShippingBin);
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
                else if (type.equals("crop")) {
                    tiles[x][y].setTile(TileType.CROP);

                    CropType[] types = CropType.values();
                    CropType randomType = types[rand.nextInt(types.length)];
                    Crop crop = new Crop(randomType);
                    tiles[x][y].setItem(crop);
                }
                else if (type.equals("stone")) {
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

    public Building createBuilding() {
        switch (farmIndex) {
            case 0:
                Building b1 = new Building(width - 5, 0, "house", "house");
                return b1;
            case 1:
                Building b2 = new Building(0, 0, "house", "house");
                return b2;
            case 2:
                Building b3 = new Building(0, height - 5, "house", "house");
                return b3;
            case 3:
                Building b4 = new Building(width - 5, height - 5, "house", "house");
                return b4;
        }
        return null;
    }

    public GreenHouse createGreenHouse() {
        switch (farmIndex) {
            case 0:
                GreenHouse g1 = new GreenHouse(0, 0);
                return g1;
            case 1:
                GreenHouse g2 = new GreenHouse(width - 5, 0);
                return g2;
            case 2:
                GreenHouse g3 = new GreenHouse(width - 5, height - 6);
                return g3;
            case 3:
                GreenHouse g4 = new GreenHouse(1, height - 6);
                return g4;
        }
        return null;
    }

    public Quarry createQuarry() {
        switch (farmIndex) {
            case 0:
                Quarry q1 = new Quarry(width - 4, height - 4);
                return q1;
            case 1:
                Quarry q2 = new Quarry(0, height - 4);
                return q2;
            case 2:
                Quarry q3 = new Quarry(0, 0);
                return q3;
            case 3:
                Quarry q4 = new Quarry(width - 4, 0);
                return q4;
        }
        return null;
    }

    public List<Lake> createLakes() {
        List<Lake> lakes = new ArrayList<>();
        switch (farmIndex) {
            case 0:
                if (farmType) {
                    Lake l11 = new Lake(width / 2 + 2, height / 2 - 13, 6, 6, "lake", Lake.LakeType.RIVER);
                    Lake l12 = new Lake(width / 2 + 7 - 15, height / 2 + 7, 6, 6, "lake", Lake.LakeType.RIVER);
                    lakes.add(l11);
                    lakes.add(l12);
                    return lakes;
                }
                Lake l1 = new Lake(width / 2 + 5, height / 2 - 3, 6, 6, "lake", Lake.LakeType.RIVER);
                lakes.add(l1);
                return lakes;
            case 1:
                if (farmType) {
                    Lake l21 = new Lake(width / 2 - 6, height / 2 - 13, 6, 6, "lake", Lake.LakeType.RIVER);
                    Lake l22 = new Lake(width / 2 - 15, height / 2 + 7, 6, 6, "lake", Lake.LakeType.RIVER);
                    lakes.add(l21);
                    lakes.add(l22);
                    return lakes;
                }
                Lake l2 = new Lake(width / 2 - 6, height / 2 - 3, 6, 6, "lake", Lake.LakeType.RIVER);
                lakes.add(l2);
                return lakes;
            case 2:
                if (farmType) {
                    Lake l31 = new Lake(width / 2 - 6, height / 2, 6, 6, "lake", Lake.LakeType.RIVER);
                    Lake l32 = new Lake(width / 2 - 1, height / 2 + 6, 6, 6, "lake", Lake.LakeType.RIVER);
                    lakes.add(l31);
                    lakes.add(l32);
                    return lakes;
                }
                Lake l3 = new Lake(width / 2 - 6, height / 2, 6, 6, "lake", Lake.LakeType.RIVER);
                lakes.add(l3);
                return lakes;
            case 3:
                if (farmType) {
                    Lake l41 = new Lake(width / 2 + 2, height / 2, 6, 6, "lake", Lake.LakeType.RIVER);
                    Lake l42 = new Lake(width / 2 + 7, height / 2 + 6, 6, 6, "lake", Lake.LakeType.RIVER);
                    lakes.add(l41);
                    lakes.add(l42);
                    return lakes;
                }
                Lake l4 = new Lake(width / 2 + 2, height / 2, 6, 6, "lake", Lake.LakeType.RIVER);
                lakes.add(l4);
                return lakes;
        }
        return null;
    }

    public void markBuildingArea() {
        Building b = getBuilding();
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

    public void markGreenHouseArea() {
        GreenHouse g = getGreenHouse();
        int greenHouseX = g.getX();
        int greenHouseY = g.getY();
        int greenHouseWidth = g.getWidth();
        int greenHouseHeight = g.getHeight();

        for (int y = greenHouseY; y < greenHouseY + greenHouseHeight; y++) {
            for (int x = greenHouseX; x < greenHouseX + greenHouseWidth; x++) {
                tiles[x][y] = new Location(x, y, TileType.GREENHOUSE);
            }
        }
    }

    public void markQuarry() {
        Quarry q = getQuarry();
        int quarryX = q.getX();
        int quarryY = q.getY();
        int quarryWidth = q.getWidth();
        int quarryHeight = q.getHeight();

        for (int y = quarryY; y < quarryY + quarryHeight; y++) {
            for (int x = quarryX; x < quarryX + quarryWidth; x++) {
                tiles[x][y] = new Location(x, y, TileType.QUARRY);
            }
        }
    }

    public void markLakes() {
        for (Lake l : getLakes()) {
            int lakeX = l.getX();
            int lakeY = l.getY();
            int lakeWidth = l.getWidth();
            int lakeHeight = l.getHeight();

            for (int y = lakeY; y < lakeY + lakeHeight; y++) {
                for (int x = lakeX; x < lakeX + lakeWidth; x++) {
                    tiles[x][y] = new Location(x, y, TileType.LAKE);
                }
            }
        }
    }

    public void markBarnArea(Barn barn) {
        int barnX = barn.getX();
        int barnY = barn.getY();
        int barnWidth = barn.getWidth();
        int barnHeight = barn.getHeight();

        for (int y = barnY; y < barnY + barnHeight; y++) {
            for (int x = barnX; x < barnX + barnWidth; x++) {
                tiles[x][y] = new Location(x, y, TileType.BARN);
            }
        }
    }

    public void markCoopArea(Coop coop) {
        int coopX = coop.getX();
        int coopY = coop.getY();
        int coopWidth = coop.getWidth();
        int coopHeight = coop.getHeight();

        for (int y = coopY; y < coopY + coopHeight; y++) {
            for (int x = coopX; x < coopX + coopWidth; x++) {
                tiles[x][y] = new Location(x, y, TileType.COOP);
            }
        }
    }

    public void markShippingBin(ShippingBin shippingBin) {
        Random rand = new Random();
        int x = rand.nextInt(width);
        int y = rand.nextInt(height);
        TileType currentTile = tiles[x][y].getTile();
        while (currentTile != TileType.GRASS) {
            x = rand.nextInt(width);
            y = rand.nextInt(height);
            currentTile = tiles[x][y].getTile();
        }

        tiles[x][y].setTile(TileType.SHIPPING_BIN);
        tiles[x][y].setItem(shippingBin);
    }

    public void addAnimal(Animal animal) {
        animals.add(animal);
    }

    public void removeAnimal(Animal animal) {
        animals.remove(animal);
    }

    public String getName() {
        return name;
    }

    public Player getOwner() {
        return owner;
    }

    public List<Animal> getAnimals() {
        return animals;
    }

    public Building getBuilding() {
        return building;
    }

    public GreenHouse getGreenHouse() {
        return greenHouse;
    }

    public Quarry getQuarry() {
        return quarry;
    }

    public List<Lake> getLakes() {
        return lakes;
    }

    public boolean isBarnsEmpty() {
        return barns.isEmpty();
    }

    public boolean isCoopsEmpty() {
        return coops.isEmpty();
    }

    public List<Barn> getBarns() {
        return barns;
    }

    public List<Coop> getCoops() {
        return coops;
    }

    public void addBarn(Barn barn) {
        markBarnArea(barn);
        barns.add(barn);
    }

    public void addCoop(Coop coop) {
        markCoopArea(coop);
        coops.add(coop);
    }

    public TileType getTile(int x, int y) {
        if (contains(x, y)) {
            Location location = tiles[x][y];
            return location.getTile();
        }
        return null;
    }

    public boolean isPlowed(int x, int y) {
        if (!contains(x, y)) return false;
        return tiles[x][y].getTile() == TileType.PLOWED;
    }

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
        if(item.isGiantable()){
            if(checkFourDirectionsForGiants(x,y,item.getName()) == 1){
                Plant[] plants = new Plant[4];
                for(int i = x-1;i < x;i++){
                    for(int j = y;j < y+1;j++){
                        plants[i] = (Plant) getItem(i,j).getItem();
                    }
                }

                int stage = Math.max(plants[0].getStage(), Math.max(plants[1].getStage() ,Math.max(plants[2].getStage() ,Math.max(plants[3].getStage() ,0)) ));

                for(int i = x-1;i < x;i++){
                    for(int j = y;j < y+1;j++){
                        plants[i].isGiant(stage);
                        plants[i].setDaysCounter(0);
                    }
                }

            }
            else if(checkFourDirectionsForGiants(x,y,item.getName()) == 2){
                Plant[] plants = new Plant[4];
                for(int i = x-1;i < x;i++){
                    for(int j = y-1;j < y;j++){
                        plants[i] = (Plant) getItem(i,j).getItem();
                    }
                }

                int stage = Math.max(plants[0].getStage(), Math.max(plants[1].getStage() ,Math.max(plants[2].getStage() ,Math.max(plants[3].getStage() ,0)) ));

                for(int i = x-1;i < x;i++){
                    for(int j = y-1;j < y;j++){
                        plants[i].isGiant(stage);
                        plants[i].setDaysCounter(0);
                    }
                }
            }else if(checkFourDirectionsForGiants(x,y,item.getName()) == 3){
                Plant[] plants = new Plant[4];
                for(int i = x;i < x+1;i++){
                    for(int j = y-1;j < y;j++){
                        plants[i] = (Plant) getItem(i,j).getItem();
                    }
                }

                int stage = Math.max(plants[0].getStage(), Math.max(plants[1].getStage() ,Math.max(plants[2].getStage() ,Math.max(plants[3].getStage() ,0)) ));

                for(int i = x;i < x+1;i++){
                    for(int j = y-1;j < y;j++){
                        plants[i].isGiant(stage);
                        plants[i].setDaysCounter(0);
                    }
                }
            }else if(checkFourDirectionsForGiants(x,y,item.getName()) == 4){
                Plant[] plants = new Plant[4];
                for(int i = x;i < x+1;i++){
                    for(int j = y;j < y+1;j++){
                        plants[i] = (Plant) getItem(i,j).getItem();
                    }
                }

                int stage = Math.max(plants[0].getStage(), Math.max(plants[1].getStage() ,Math.max(plants[2].getStage() ,Math.max(plants[3].getStage() ,0)) ));

                for(int i = x;i < x+1;i++){
                    for(int j = y;j < y+1;j++){
                        plants[i].isGiant(stage);
                        plants[i].setDaysCounter(0);
                    }
                }
            }
        }
    }

    public int checkFourDirectionsForGiants(int x , int y , String itemName) {
        int[][] DIRECTIONS = {
                {-1, 1},  // 1: NE
                {-1, -1}, // 2: NW
                {1, -1},  // 3: SW
                {1, 1}    // 4: SE
        };

        for(int dir = 0;dir < DIRECTIONS.length;dir++){
            int dx = DIRECTIONS[dir][0];
            int dy = DIRECTIONS[dir][1];

            int x1 = x + dx;
            int y1 = y;
            int x2 = x + dx;
            int y2 = y + dy;
            int x3 = x;
            int y3 = y + dy;

            if (contains(x1,y1) &&
                    contains(x2,y2) &&
                    contains(x3,y3)) {

                if (getItem(x1,y1).getItem().getName() == itemName &&
                        getItem(x2,y2).getItem().getName() == itemName &&
                        getItem(x3,y3).getItem().getName() == itemName) {
                    return dir + 1; // 1 to 4
                }
            }
        }
        return 0;
    }

    public boolean isInOtherPlayersFarm(Player player, int x, int y) {
        for (Farm farm : App.getGame().getGameMap().getFarms()) {
            if (farm.contains(x, y) && !farm.getOwner().equals(player)) {
                return true;
            }
        }
        return false;
    }

    public void updatePlants() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Location tile = tiles[x][y];
                if (tile.getItem() != null) {
                    if (tile.getItem() instanceof Tree) {
                        tile.getItem().updateItem();
                        Tree tree = (Tree) tile.getItem();
                        if (!tree.getMoisture()) {
                            tile.setItem(null);
                        }
                    }
                    else if (tile.getItem() instanceof Plant) {
                        tile.getItem().updateItem();
                        Plant plant = (Plant) tile.getItem();
                        if (!plant.getMoisture()) {
                            tile.setItem(null);
                        }
                    }
                }
            }
        }
    }

    public void updateArtisans() {
        Map<Item, Integer> items = owner.getBackpack().getInventory();
        for (Item item : items.keySet()) {
            if (item instanceof CraftingItem) {
                CraftingItem craftingItem = (CraftingItem) item;
                craftingItem.updateArtisan();
            }
        }
    }

    public boolean isPassable(Location location) {
        TileType type = location.getTile();
        return type == TileType.GRASS || type == TileType.PATH;
    }

    public void printCurrentViewColored(int centerX, int centerY, int viewRadius) {
        int startX = Math.max(0, centerX - viewRadius);
        int endX = Math.min(width - 1, centerX + viewRadius);
        int startY = Math.max(0, centerY - viewRadius);
        int endY = Math.min(height - 1, centerY + viewRadius);

        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                Location tile = tiles[x][y];
                Location ownerLocation = owner.getLocation();
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
                    case "greenhouse" -> BG_BROWN;
                    case "building" -> BG_WHITE;
                    case "plowed" -> BG_BROWN;
                    case "quarry" -> BG_RED;
                    case "village" -> BG_PURPLE;
                    case "shipping_bin" -> BG_CYAN;
                    case "bridge" -> CYAN;
                    case "empty" -> RESET;
                    default -> RESET;
                };
                List<Player> players = App.getGame().getGameMap().getPlayers();
                List<Player> playersInThisFarm = new ArrayList<>();
                for (Player player : players) {
                    if (player.getCurrentFarm().equals(this)) {
                        playersInThisFarm.add(player);
                    }
                }
                for (Player player : playersInThisFarm) {
                    Location location = player.getLocation();
                    String playerColor = player.getPlayerColor();
                    if (x == location.getX() && y == location.getY()) {
                        System.out.print(playerColor + "@ " + RESET);
                    }
                    else {
                        System.out.print(color + symbol + " " + RESET);
                    }
                }

            }
            System.out.println();
        }

    }

    public boolean isInWater(int x, int y) {
        for (Lake lake : lakes) {
            if (lake.contains(x, y)) {
                return true;
            }
        }
        return false;
    }

    public void setScarecrow(int x, int y, int r, boolean key) {
        for (int i = x - r; i <= x + r; i++) {
            for (int j = y - r; j <= y + r; j++) {
                Location tile = tiles[i][j];
                if (tile.getItem() != null) {
                    tile.setScarecrowThere(key);
                }
            }
        }
    }

    public void updateLakeFish() {
        int fishingSkill = owner.getSkillLevel(org.example.models.enums.PlayerEnums.Skills.FISHING);
        for (Lake lake : lakes) {
            lake.updateAvailableFish(org.example.models.App.getGame().getDate().getSeason(), fishingSkill);
        }
    }

    private void checkSeasonChange(org.example.models.common.Date oldDate, Date newDate) {
        if (oldDate.getSeason() != newDate.getSeason()) {
            for (Player player : org.example.models.App.getGame().getPlayers()) {
                updateLakeFish();
            }
        }
    }

    public void sprinkle(int x, int y, int r) {
        for (int i = x - r; i <= x + r; i++) {
            for (int j = y - r; j <= y + r; j++) {
                if (getItem(i, j) != null) {
                    Item check = getItem(i, j).getItem();
                    if (check != null) {
                        if (check instanceof Plant) {
                            Plant plant = (Plant) check;
                            plant.setMoisture(true);
                        }
                        else if (check instanceof Tree) {
                            Tree tree = (Tree) check;
                            tree.setMoisture(true);
                        }
                    }
                }
            }
        }
    }

    public void bomb(int x, int y, int r) {
        for (int i = x - r; i <= x + r; i++) {
            for (int j = y - r; j <= y + r; j++) {
                if (getItem(i, j) != null) {
                    getItem(i, j).setItem(null);
                }
            }
        }
    }

    public boolean canBuild(int x, int y, int width, int height) {
        for (int i = x - width; i <= x + width; i++) {
            for (int j = y - height; j <= y + height; j++) {
                Location tile = tiles[i][j];
                if (tile.getItem() == null) {
                    return false;
                }
                if (tile.getTile() != TileType.GRASS) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isProtectedTile(String type) {
        if (type == null) {
            return false;
        }
        return type.equals("water") || type.equals("village") ||
                type.equals("building") || type.equals("quarry") || type.equals("greenhouse");
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

            if (contains(newX, newY)) {
                Location neighbor = tiles[newX][newY];
                if (isPassable(neighbor)) {
                    result.add(neighbor);
                }
            }
        }

        return result;
    }

    public boolean changeTile(int x, int y, TileType tileType, Player player) {
        if (!contains(x, y)) {
            return false;
        }
//        if (!isValidTileType(newType)) {
//            return false;
//        }

        Location tile = tiles[x][y];

        if (!App.getGame().getGameMap().canPlayerModifyTile(player, x, y)) {
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
        tile.setTile(tileType);
        //handleTileChangeEffects(tile, previousType, newType);

        return true;
    }

//    private void handleTileChangeEffects(Location tile, String previousType, String newType) {
//        if (previousType.equals("tilled_soil") && !newType.equals("tilled_soil")) {
//            //tile.setPlant(null);
//            //kasra
//        }
//
//        if ((previousType.equals("tree") || previousType.equals("stone")) &&
//                (newType.equals("stump") || newType.equals("debris"))) {
//            //player.addItemToInventory(new Item(previousType.equals("tree") ? "wood" : "stone", 1));
//            //kasra
//        }
//    }

    public int walk(int x, int y) {
        Location initialLocation = owner.getLocation();
        Location finalLocation = tiles[x][y];

        if (!contains(x, y)) {
            return -1;
        }

        if (finalLocation.getTile() != TileType.GRASS) {
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
                    {0, -1},          {0, 1},
                    {1, -1},  {1, 0}, {1, 1}
            };

            for (int[] dir : directions) {
                int newX = current.getX() + dir[0];
                int newY = current.getY() + dir[1];

                if (!contains(newX, newY)) {
                    continue;
                }

                Location neighbor = tiles[newX][newY];

                if (neighbor.getTile() == TileType.GRASS && !visited.contains(neighbor)) {
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

            return actualDistance;
        }
        else {
            owner.setEnergy(owner.getEnergy() - requiredEnergy);
            owner.setLocation(finalLocation);
            return totalDistance;
        }
    }

    public Lake lakeAround (Location location) {
        int x = location.getX();
        int y = location.getY();

        int[][] directions = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1},           {0, 1},
                {1, -1},  {1, 0}, {1, 1}
        };

        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];

            if (contains(newX, newY)) {
                for (Lake lake : lakes) {
                    if (lake.contains(newX, newY)) {
                        return lake;
                    }
                }
//                if (tiles[newX][newY].getTile() == TileType.LAKE) {
//                    return lake;
//                }
            }
        }

        return null;
    }

    public ShippingBin getShippingBinNearby (Location location) {
        int x = location.getX();
        int y = location.getY();

        int[][] directions = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1},           {0, 1},
                {1, -1},  {1, 0}, {1, 1}
        };

        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];

            if (contains(newX, newY)) {
                for (ShippingBin shippingBin : shippingBins) {
                    if (location.getItem().equals(shippingBin)) {
                        return shippingBin;
                    }
                }
            }
        }

        return null;
    }

    public Lake getLakeAt(int x, int y) {
        for (Lake lake : lakes) {
            if (lake.contains(x, y)) {
                return lake;
            }
        }
        return null;
    }

    public int getFarmIndex() {
        return farmIndex;
    }

    public void thor(Location location) {
        int x = location.getX();
        int y = location.getY();
        for (int i = x ; i < x + 4 ; i++) {
            for (int j = y ; j < y + 4 ; j++) {
                Location tile = tiles[i][j];
                if (contains(i,j)) {
                    if (tile.getTile() != null) {
                        if (tile.getTile() != TileType.GREENHOUSE) {
                            if (tile.getItem() instanceof Tree) {
                                Tree tree = (Tree) tile.getItem();
                                tile.setItem(tree.burnTree());
                                tiles[i][j].setTile(TileType.GRASS);
                                tiles[i][j].setType("grass");
                            }
                        }
                    }
                }
            }
        }
    }

    public int numberOfPlants() {
        int counter = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (tiles[x][y].getItem() instanceof Tree || tiles[x][y].getItem() instanceof Plant ||
                        tiles[x][y].getItem() instanceof Crop) {
                    if (!tiles[x][y].isScarecrowThere()) {
                        counter++;
                    }
                }
            }
        }
        return counter;
    }

    public void attackOfTheCrows(){
        int numberOfCrows = numberOfPlants()/16;
        for (int i = 0 ; i < numberOfCrows ; i++) {
            attackOfSingleCrow();
        }
    }

    public ArrayList<Location> allItemsForCrows() {
        ArrayList<Location> locations = new ArrayList<>();
        for (int x = 0 ; x < width ; x++) {
            for (int y = 0 ; y < height ; y++) {
                if ((tiles[x][y].getItem() instanceof Tree || tiles[x][y].getItem() instanceof Crop
                        || tiles[x][y].getItem() instanceof Plant) && !tiles[x][y].isScarecrowThere()) {
                    locations.add(tiles[x][y]);
                }
            }
        }
        return locations;
    }

    public void attackOfSingleCrow(){
        Random random = new Random();
        int a = random.nextInt(3);
        if (a == 0) {
            ArrayList<Location> locations = allItemsForCrows();
            int index = random.nextInt(locations.size());
            Location location = locations.get(index);
            if (location.getItem() instanceof Tree) {
                Tree tree = (Tree) location.getItem();
                tree.setStage(0);
                tree.setDaysCounter(0);
            }
            else if (location.getItem() instanceof Crop || location.getItem() instanceof Plant) {
                location.setItem(null);
                location.setTile(TileType.GRASS);
                location.setType("grass");
            }
        }
    }
}