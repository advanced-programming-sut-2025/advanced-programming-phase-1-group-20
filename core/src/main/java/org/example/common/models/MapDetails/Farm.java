package org.example.common.models.MapDetails;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.example.common.models.Items.*;
import org.example.common.models.enums.PlayerEnums.Skills;
import org.example.common.models.App;
import org.example.common.models.Barn;
import org.example.common.models.Coop;
import org.example.common.models.Player.Player;
import org.example.common.models.common.Date;
import org.example.common.models.common.Location;
import org.example.common.models.entities.animal.Animal;
import org.example.common.models.entities.animal.BarnAnimal;
import org.example.common.models.entities.animal.CoopAnimal;
import org.example.common.models.enums.Types.CropType;
import org.example.common.models.enums.Types.MineralType;
import org.example.common.models.enums.Types.TileType;
import org.example.common.models.enums.Types.TreeType;

import java.util.*;

public class Farm {
    public static final int width = 78;
    public static final int height = 78;
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
    private final Building building = createBuilding();
    private final List<Lake> lakes = createLakes();
    private final GreenHouse greenHouse = createGreenHouse();
    private final Quarry quarry = createQuarry();
    private final List<Barn> barns;
    private final List<Coop> coops;
    private final Map<String, Character> symbolMap;
    private final List<ShippingBin> shippingBins;

    private String background = "content/maps/1.png";
    private Sprite backgroundSprite;

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
        this.shippingBins = new ArrayList<>();
        owner.setPlayerColor(setOwnerColor());
        App.getGame().getPlayer(owner.getUser()).setLocation(owner.getLocation());
        initializeFarm();
        initializeSymbols();
        setInitialOwnerLocation();
        makeFenceAndPaths();
    }

    public String getBackground() {
        return background;
    }

    public Sprite getBackgroundSprite() {
        if(backgroundSprite == null) {
            backgroundSprite = new Sprite(new Texture(getBackground()));
            return backgroundSprite;
        }
        return backgroundSprite;
    }

    public static int calculateEnergyNeeded(Location from, Location to) {
        int distance = Math.abs(from.getX() - to.getX()) + Math.abs(from.getY() - to.getY());

        int baseEnergyCost = 2;

        return distance * baseEnergyCost;
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
            {0, -1}, {0, 1},
            {1, -1}, {1, 0}, {1, 1}
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

    public void setInitialOwnerLocation() {
        // Place player near the house for their farm type
        Location initialPlayerLocation;
        int houseCenterX = building.getX() + building.getWidth() / 2;
        int houseCenterY = building.getY() + building.getHeight() / 2;

        // Try to place player just outside the house, adjust if it's an edge
        int playerStartX = houseCenterX;
        int playerStartY = houseCenterY - 3; // Example: 3 tiles below center of house

        if (playerStartY < 0) { // If it's too close to bottom edge, adjust
            playerStartY = houseCenterY + 3;
        }
        if (playerStartX < 0) { // If it's too close to left edge
            playerStartX = houseCenterX + 3;
        }
        if (playerStartX >= width) { // If it's too close to right edge
            playerStartX = houseCenterX - 3;
        }
        if (playerStartY >= height) { // If it's too close to top edge
            playerStartY = houseCenterY - 3;
        }

        initialPlayerLocation = tiles[playerStartX][playerStartY];
        owner.setLocation(initialPlayerLocation);
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
        symbolMap.put("branch", 'X');
        symbolMap.put("shipping_bin", 'S');
        symbolMap.put("greenhouse", ' ');
        symbolMap.put("plowed", ' ');
        symbolMap.put("constructed_greenhouse", 'G');
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
                tiles[x][y] = new Location(x, y, TileType.Dirt);
            }
        }

        markBuildingArea();
        markGreenHouseArea();
        markQuarry();
        markLakes();

        placeRandomObjects("stone", 50);
        placeRandomObjects("tree", 50);
        placeRandomObjects("crop", 50);
        placeRandomObjects("branch", 50);
    }

    public void makeFenceAndPaths() {
        int fenceThickness = 1; // Thickness of the fence boundary
        int pathWidth = 3;      // Width of the path opening (should be odd for proper centering)

        int primaryExitEdgeX = -1, primaryExitEdgeY = -1; // Coordinate along the edge for primary exit
        int secondaryExitEdgeX = -1, secondaryExitEdgeY = -1; // Coordinate along the edge for secondary exit

        // Calculate the center of the path opening
        int pathCenterOffset = pathWidth / 2;

        switch (farmIndex) {
            case 0: // Bottom-Right Farm (House: width-5, 0)
                // Primary exit to the left boundary of the farm (to connect to Farm 1 or 2)
                primaryExitEdgeX = 0;
                primaryExitEdgeY = height / 2;
                // Secondary exit to the top boundary of the farm (to connect to Farm 3)
                secondaryExitEdgeX = width / 2;
                secondaryExitEdgeY = height - 1;
                break;
            case 1: // Bottom-Left Farm (House: 0, 0)
                // Primary exit to the right boundary of the farm (to connect to Farm 0 or 3)
                primaryExitEdgeX = width - 1;
                primaryExitEdgeY = height / 2;
                // Secondary exit to the top boundary of the farm (to connect to Farm 2)
                secondaryExitEdgeX = width / 2;
                secondaryExitEdgeY = height - 1;
                break;
            case 2: // Top-Left Farm (House: 0, height-5)
                // Primary exit to the right boundary of the farm (to connect to Farm 3)
                primaryExitEdgeX = width - 1;
                primaryExitEdgeY = height / 2;
                // Secondary exit to the bottom boundary of the farm (to connect to Farm 0 or 1)
                secondaryExitEdgeX = width / 2;
                secondaryExitEdgeY = 0;
                break;
            case 3: // Top-Right Farm (House: width-5, height-5)
                // Primary exit to the left boundary of the farm (to connect to Farm 2)
                primaryExitEdgeX = 0;
                primaryExitEdgeY = height / 2;
                // Secondary exit to the bottom boundary of the farm (to connect to Farm 0 or 1)
                secondaryExitEdgeX = width / 2;
                secondaryExitEdgeY = 0;
                break;
        }

        // 1. Draw Fences
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                boolean isBoundary = (i < fenceThickness || i >= width - fenceThickness ||
                    j < fenceThickness || j >= height - fenceThickness);

                boolean isPrimaryExitArea = false;
                if (primaryExitEdgeX != -1 && primaryExitEdgeY != -1) {
                    if ((i == primaryExitEdgeX && Math.abs(j - primaryExitEdgeY) <= pathCenterOffset) ||
                        (j == primaryExitEdgeY && Math.abs(i - primaryExitEdgeX) <= pathCenterOffset)) {
                        isPrimaryExitArea = true;
                    }
                }

                boolean isSecondaryExitArea = false;
                if (secondaryExitEdgeX != -1 && secondaryExitEdgeY != -1) {
                    if ((i == secondaryExitEdgeX && Math.abs(j - secondaryExitEdgeY) <= pathCenterOffset) ||
                        (j == secondaryExitEdgeY && Math.abs(i - secondaryExitEdgeX) <= pathCenterOffset)) {
                        isSecondaryExitArea = true;
                    }
                }


                if (isBoundary && !(isPrimaryExitArea || isSecondaryExitArea)) {
                    TileType currentTile = tiles[i][j].getTile();
                    if (currentTile != TileType.BUILDING && currentTile != TileType.GREENHOUSE &&
                        currentTile != TileType.CONSTRUCTED_GREENHOUSE && currentTile != TileType.QUARRY &&
                        currentTile != TileType.LAKE && currentTile != TileType.WATER && currentTile != TileType.SHIPPING_BIN) {
                        changeTile(i, j, TileType.FENCE, owner);
                    }
                }
            }
        }

        Location houseLoc = owner.getLocation();
        if (houseLoc == null) {
            houseLoc = new Location(building.getX() + building.getWidth() / 2, building.getY() + building.getHeight() / 2, TileType.BUILDING);
        }

        int farmCenterX = width / 2;
        int farmCenterY = height / 2;

        for (int i = Math.min(houseLoc.getX(), farmCenterX); i <= Math.max(houseLoc.getX(), farmCenterX); i++) {
            if (tiles[i][houseLoc.getY()].getTile() != TileType.BUILDING) {
                changeTile(i, houseLoc.getY(), TileType.PATH, owner);
            }
        }
        // Vertical segment
        for (int j = Math.min(houseLoc.getY(), farmCenterY); j <= Math.max(houseLoc.getY(), farmCenterY); j++) {
            if (tiles[farmCenterX][j].getTile() != TileType.BUILDING && tiles[farmCenterX][j].getTile() != TileType.STONE) {
                changeTile(farmCenterX, j, TileType.PATH, owner);
            }
        }

        if (primaryExitEdgeX != -1 && primaryExitEdgeY != -1) {
            if (Math.abs(primaryExitEdgeX - farmCenterX) > Math.abs(primaryExitEdgeY - farmCenterY)) { // More horizontal movement
                for (int i = Math.min(farmCenterX, primaryExitEdgeX); i <= Math.max(farmCenterX, primaryExitEdgeX); i++) {
                    if (tiles[i][farmCenterY].getTile() != TileType.STONE) {
                        changeTile(i, farmCenterY, TileType.PATH, owner);
                    }
                }
            } else { // More vertical movement
                for (int j = Math.min(farmCenterY, primaryExitEdgeY); j <= Math.max(farmCenterY, primaryExitEdgeY); j++) {
                    if (tiles[farmCenterX][j].getTile() != TileType.STONE) {
                        changeTile(farmCenterX, j, TileType.PATH, owner);
                    }
                }
            }
        }

        // Extend path from farm center towards secondary exit point
        if (secondaryExitEdgeX != -1 && secondaryExitEdgeY != -1) {
            // Adjust path direction based on exit location
            if (Math.abs(secondaryExitEdgeX - farmCenterX) > Math.abs(secondaryExitEdgeY - farmCenterY)) { // More horizontal movement
                for (int i = Math.min(farmCenterX, secondaryExitEdgeX); i <= Math.max(farmCenterX, secondaryExitEdgeX); i++) {
                    if (tiles[i][farmCenterY].getTile() != TileType.STONE) {
                        changeTile(i, farmCenterY, TileType.PATH, owner);
                    }
                }
            } else { // More vertical movement
                for (int j = Math.min(farmCenterY, secondaryExitEdgeY); j <= Math.max(farmCenterY, secondaryExitEdgeY); j++) {
                    if (tiles[farmCenterX][j].getTile() != TileType.STONE) {
                        changeTile(farmCenterX, j, TileType.PATH, owner);
                    }
                }
            }
        }
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
                else if (type.equals("branch")) {
                    tiles[x][y].setTile(TileType.BRANCH);
                }

                placed++;
            }
        }
    }

    public Building createBuilding() {
        switch (farmIndex) {
            case 0:
                Building b1 = new Building(55, 70, "house", "house");
                return b1;
            case 1:
                Building b2 = new Building(55, 70, "house", "house");
                return b2;
            case 2:
                Building b3 = new Building(55, 70, "house", "house");
                return b3;
            case 3:
                Building b4 = new Building(55, 70, "house", "house");
                return b4;
        }
        return null;
    }

    public GreenHouse createGreenHouse() {
        switch (farmIndex) {
            case 0:
                GreenHouse g1 = new GreenHouse(22, 70);
                return g1;
            case 1:
                GreenHouse g2 = new GreenHouse(22, 70);
                return g2;
            case 2:
                GreenHouse g3 = new GreenHouse(22, 70);
                return g3;
            case 3:
                GreenHouse g4 = new GreenHouse(22, 70);
                return g4;
        }
        return null;
    }

    public Quarry createQuarry() {
        switch (farmIndex) {
            case 0:
                Quarry q1 = new Quarry(3, 1);
                return q1;
            case 1:
                Quarry q2 = new Quarry(3, 1);
                return q2;
            case 2:
                Quarry q3 = new Quarry(3, 1);
                return q3;
            case 3:
                Quarry q4 = new Quarry(3, 1);
                return q4;
        }
        return null;
    }

    public List<Lake> createLakes() {
        List<Lake> lakes = new ArrayList<>();
        // Stardew-like 6x6 mask (oval/blob)
        boolean[][] stardewLakeMask = new boolean[][]{
            {false, true,  true,  true,  true, false},
            {true,  true,  true,  true,  true, true },
            {true,  true,  true,  true,  true, true },
            {true,  true,  true,  true,  true, true },
            {true,  true,  true,  true,  true, true },
            {false, true,  true,  true,  true, false}
        };
        if (farmType) {
            Lake l11 = new Lake(20, 30, 6, 6, "lake", Lake.LakeType.RIVER, stardewLakeMask);
            Lake l12 = new Lake(50, 30, 4, 4, "lake", Lake.LakeType.RIVER); // fallback, still rectangular
            lakes.add(l11);
            lakes.add(l12);
            return lakes;
        }
        Lake l1 = new Lake(20, 30, 6, 6, "lake", Lake.LakeType.RIVER, stardewLakeMask);
        lakes.add(l1);
        return lakes;
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

    public void markConstructedGreenHouseArea() {
        GreenHouse g = getGreenHouse();
        g.setIsConstructed();
        int greenHouseX = g.getX();
        int greenHouseY = g.getY();
        int greenHouseWidth = g.getWidth();
        int greenHouseHeight = g.getHeight();

        for (int y = greenHouseY; y < greenHouseY + greenHouseHeight; y++) {
            for (int x = greenHouseX; x < greenHouseX + greenHouseWidth; x++) {
                tiles[x][y] = new Location(x, y, TileType.CONSTRUCTED_GREENHOUSE);
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
            boolean[][] mask = l.getMask();
            for (int y = lakeY; y < lakeY + lakeHeight; y++) {
                for (int x = lakeX; x < lakeX + lakeWidth; x++) {
                    if (mask == null || mask[y - lakeY][x - lakeX]) {
                        tiles[x][y] = new Location(x, y, TileType.LAKE);
                    }
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

    public void cheatShippingBin(ShippingBin shippingBin, int x, int y) {
        shippingBins.add(shippingBin);
        tiles[x][y].setTile(TileType.SHIPPING_BIN);
        tiles[x][y].updateTypeFromTile();
        tiles[x][y].setItem(shippingBin);
    }

    public void markShippingBin(ShippingBin shippingBin) {
        Random rand = new Random();
        int x = rand.nextInt(width);
        int y = rand.nextInt(height);
        TileType currentTile = tiles[x][y].getTile();
        while (currentTile != TileType.Dirt) {
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
        return this.building;
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
        if (item.isGiantable()) {
            if (checkFourDirectionsForGiants(x, y, item.getName()) == 1) {
                Plant[] plants = new Plant[4];
                for (int i = x - 1; i < x; i++) {
                    for (int j = y; j < y + 1; j++) {
                        plants[i] = (Plant) getItem(i, j).getItem();
                    }
                }

                int stage = Math.max(plants[0].getStage(), Math.max(plants[1].getStage(), Math.max(plants[2].getStage(), Math.max(plants[3].getStage(), 0))));

                for (int i = x - 1; i < x; i++) {
                    for (int j = y; j < y + 1; j++) {
                        plants[i].isGiant(stage);
                        plants[i].setDaysCounter(0);
                    }
                }

            } else if (checkFourDirectionsForGiants(x, y, item.getName()) == 2) {
                Plant[] plants = new Plant[4];
                for (int i = x - 1; i < x; i++) {
                    for (int j = y - 1; j < y; j++) {
                        plants[i] = (Plant) getItem(i, j).getItem();
                    }
                }

                int stage = Math.max(plants[0].getStage(), Math.max(plants[1].getStage(), Math.max(plants[2].getStage(), Math.max(plants[3].getStage(), 0))));

                for (int i = x - 1; i < x; i++) {
                    for (int j = y - 1; j < y; j++) {
                        plants[i].isGiant(stage);
                        plants[i].setDaysCounter(0);
                    }
                }
            } else if (checkFourDirectionsForGiants(x, y, item.getName()) == 3) {
                Plant[] plants = new Plant[4];
                for (int i = x; i < x + 1; i++) {
                    for (int j = y - 1; j < y; j++) {
                        plants[i] = (Plant) getItem(i, j).getItem();
                    }
                }

                int stage = Math.max(plants[0].getStage(), Math.max(plants[1].getStage(), Math.max(plants[2].getStage(), Math.max(plants[3].getStage(), 0))));

                for (int i = x; i < x + 1; i++) {
                    for (int j = y - 1; j < y; j++) {
                        plants[i].isGiant(stage);
                        plants[i].setDaysCounter(0);
                    }
                }
            } else if (checkFourDirectionsForGiants(x, y, item.getName()) == 4) {
                Plant[] plants = new Plant[4];
                for (int i = x; i < x + 1; i++) {
                    for (int j = y; j < y + 1; j++) {
                        plants[i] = (Plant) getItem(i, j).getItem();
                    }
                }

                int stage = Math.max(plants[0].getStage(), Math.max(plants[1].getStage(), Math.max(plants[2].getStage(), Math.max(plants[3].getStage(), 0))));

                for (int i = x; i < x + 1; i++) {
                    for (int j = y; j < y + 1; j++) {
                        plants[i].isGiant(stage);
                        plants[i].setDaysCounter(0);
                    }
                }
            }
        }
    }

    public int checkFourDirectionsForGiants(int x, int y, String itemName) {
        int[][] DIRECTIONS = {
            {-1, 1},
            {-1, -1},
            {1, -1},
            {1, 1}
        };

        for (int dir = 0; dir < DIRECTIONS.length; dir++) {
            int dx = DIRECTIONS[dir][0];
            int dy = DIRECTIONS[dir][1];

            int x1 = x + dx;
            int y1 = y;
            int x2 = x + dx;
            int y2 = y + dy;
            int x3 = x;
            int y3 = y + dy;

            if (contains(x1, y1) &&
                contains(x2, y2) &&
                contains(x3, y3)) {

                if (getItem(x1, y1).getItem().getName() == itemName &&
                    getItem(x2, y2).getItem().getName() == itemName &&
                    getItem(x3, y3).getItem().getName() == itemName) {
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
                            if(tree.getMoistureCounter() >= 2){
                                tile.setItem(null);
                                tiles[x][y].setTile(TileType.Dirt);
                                tiles[x][y].setType("grass");
                            }
                        }
                    } else if (tile.getItem() instanceof Plant) {
                        tile.getItem().updateItem();
                        Plant plant = (Plant) tile.getItem();
                        if (!plant.getMoisture()) {
                            if(plant.getMoistureCounter() >= 2){
                                tile.setItem(null);
                                tiles[x][y].setTile(TileType.Dirt);
                                tiles[x][y].setType("grass");
                            }
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
        return type == TileType.Dirt || type == TileType.PATH;
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
                    case "branch" -> BG_BROWN;
                    case "barn" -> BG_LIGHT_BLUE;
                    case "greenhouse" -> BG_BROWN;
                    case "constructed_greenhouse" -> BG_BROWN;
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
                    } else {
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
        int fishingSkill = owner.getSkillLevel(Skills.FISHING);
        for (Lake lake : lakes) {
            lake.updateAvailableFish(App.getGame().getDate().getSeason(), fishingSkill);
        }
    }

    private void checkSeasonChange(Date oldDate, Date newDate) {
        if (oldDate.getSeason() != newDate.getSeason()) {
            for (Player player : App.getGame().getPlayers()) {
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
                        } else if (check instanceof Tree) {
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
                    getItem(i, j).setTile(TileType.Dirt);
                    getItem(i, j).setType("grass");
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
                if (tile.getTile() != TileType.Dirt) {
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
            type.equals("building") || type.equals("quarry") || type.equals("greenhouse") ||
            type.equals("barn") || type.equals("coop") || type.equals("shipping_bin"); // Added protected types
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

        // Ensure App.getGame() and GameMap are not null before calling canPlayerModifyTile
        if (App.getGame() != null && App.getGame().getGameMap() != null) {
            if (!App.getGame().getGameMap().canPlayerModifyTile(player, x, y)) {
                return false;
            }
        } else {
            // If game or gameMap is null, allow modification during initialization,
            // or handle as an error depending on expected behavior.
            // For fence/path generation, we assume it's okay.
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
        tile.setItem(null);
        tile.setType(tileType.name().toLowerCase()); // Ensure type string is updated
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

        if (finalLocation.getTile() != TileType.Dirt && finalLocation.getTile() != TileType.PATH) { // Allow walking on path
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

                if ((neighbor.getTile() == TileType.Dirt || neighbor.getTile() == TileType.PATH) && !visited.contains(neighbor)) { // Allow walking on path
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

    public Lake lakeAround(Location location) {
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

    public ShippingBin getShippingBinNearby(Location location) {
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
                for (ShippingBin shippingBin : shippingBins) {
                    if (location.getItem() != null && location.getItem().equals(shippingBin)) { // Added null check for getItem()
                        return shippingBin;
                    }
                }
            }
        }

        return null;
    }

    public Barn getBarnAround(Location location) {
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
                for (Barn barn : barns) {
                    if (barn.contains(newX, newY)) {
                        return barn;
                    }
                }
//                if (tiles[newX][newY].getTile() == TileType.BARN) {
//                    return lake;
//                }
            }
        }

        return null;
    }

    public Coop getCoopAround(Location location) {
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
                for (Coop coop : coops) {
                    if (coop.contains(newX, newY)) {
                        return coop;
                    }
                }
//                if (tiles[newX][newY].getTile() == TileType.COO) {
//                    return lake;
//                }
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
        for (int i = x; i < x + 4; i++) {
            for (int j = y; j < y + 4; j++) {
                Location tile = tiles[i][j];
                if (contains(i, j)) {
                    if (tile.getTile() != null) {
                        if (tile.getTile() != TileType.GREENHOUSE) {
                            if (tile.getItem() instanceof Tree) {
                                Tree tree = (Tree) tile.getItem();
                                tile.setItem(tree.burnTree());
                                tiles[i][j].setTile(TileType.Dirt);
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

    public void attackOfTheCrows() {
        int numberOfCrows = numberOfPlants() / 16;
        for (int i = 0; i < numberOfCrows; i++) {
            attackOfSingleCrow();
        }
    }

    public ArrayList<Location> allItemsForCrows() {
        ArrayList<Location> locations = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if ((tiles[x][y].getItem() instanceof Tree || tiles[x][y].getItem() instanceof Crop
                    || tiles[x][y].getItem() instanceof Plant) && !tiles[x][y].isScarecrowThere()) {
                    locations.add(tiles[x][y]);
                }
            }
        }
        return locations;
    }

    public void attackOfSingleCrow() {
        Random random = new Random();
        int a = random.nextInt(3);
        if (a == 0) {
            ArrayList<Location> locations = allItemsForCrows();
            if (!locations.isEmpty()) { // Add null check for empty locations list
                int index = random.nextInt(locations.size());
                Location location = locations.get(index);
                if (location.getItem() instanceof Tree) {
                    Tree tree = (Tree) location.getItem();
                    tree.setStage(0);
                    tree.setDaysCounter(0);
                } else if (location.getItem() instanceof Crop || location.getItem() instanceof Plant) {
                    location.setItem(null);
                    location.setTile(TileType.Dirt);
                    location.setType("grass");
                }
            }
        }
    }

    public void setMoistureForRainyDays() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (tiles[x][y].getItem() instanceof Tree) {
                    Tree tree = (Tree) tiles[x][y].getItem();
                    tree.setMoisture(true);
                } else if (tiles[x][y].getItem() instanceof Plant) {
                    Plant plant = (Plant) tiles[x][y].getItem();
                    plant.setMoisture(true);
                }
            }
        }
    }

    public Barn getBarnByAnimal(BarnAnimal animal) {
        for (Barn barn : barns) {
            if (barn.getCapacity() > barn.getAnimalCount()) {
                if (animal.getBarnType().equals(barn.getType())) {
                    return barn;
                }
            }
        }
        return null;
    }

    public Coop getCoopByAnimal(CoopAnimal animal) {
        for (Coop coop : coops) {
            if (coop.getCapacity() > coop.getAnimalCount()) {
                if (animal.getCoopType().equals(coop.getType())) {
                    coop.addAnimal(animal);
                }
                return coop;
            }
        }
        return null;
    }

}
