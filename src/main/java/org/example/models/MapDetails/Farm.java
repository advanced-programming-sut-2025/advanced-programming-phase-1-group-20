package org.example.models.MapDetails;

import org.example.models.Barn;
import org.example.models.Coop;
import org.example.models.Player.Player;
import org.example.models.common.Location;
import org.example.models.entities.animal.Animal;
import org.example.models.enums.Types.TileType;

import java.util.ArrayList;
import java.util.List;

public class Farm {
    private final int startX;
    private final int startY;
    private final int width;
    private final int height;
    private final String name;
    private final Player owner;
    private final List<Animal> animals;
    private final Building building;
    private final Lake lake;
    private final GreenHouse greenHouse;
    private final Quarry quarry;
    private final List<Barn> barns;
    private final List<Coop> coops;


    public Farm(int startX, int startY, int width, int height, String name, Player owner) {
        this.startX = startX;
        this.startY = startY;
        this.width = width;
        this.height = height;
        this.name = name;
        this.owner = owner;
        this.animals = new ArrayList<>();
        this.barns = new ArrayList<>();
        this.coops = new ArrayList<>();
        this.building = createBuilding();
        this.lake = createLake();
        this.greenHouse = createGreenHouse();
        this.quarry = createQuarry();
    }

    public boolean contains(int x, int y) {
        return x >= startX && x < startX + width && y >= startY && y < startY + height;
    }

    public Building createBuilding() {
        switch (name) {
            case "Up Right Farm":
                Building b1 = new Building(startX + width - 4, startY, "house", "house");
                return b1;
            case "Up Left Farm":
                Building b2 = new Building(startX, startY, "house", "house");
                return b2;
            case "Down Left Farm":
                Building b3 = new Building(startX, startY + height - 4, "house", "house");
                return b3;
            case "Down Right Farm":
                Building b4 = new Building(startX + width - 4, startY + height - 4, "house", "house");
                return b4;
        }
        return null;
    }

    public GreenHouse createGreenHouse() {
        switch (name) {
            case "Up Right Farm":
                GreenHouse g1 = new GreenHouse(startX + 1, startY);
                return g1;
            case "Up Left Farm":
                GreenHouse g2 = new GreenHouse(startX + width - 5, startY);
                return g2;
            case "Down Left Farm":
                GreenHouse g3 = new GreenHouse(startX + width - 5, startY + height - 5);
                return g3;
            case "Down Right Farm":
                GreenHouse g4 = new GreenHouse(startX + 1, startY + height - 5);
                return g4;
        }
        return null;
    }

    public Quarry createQuarry() {
        switch (name) {
            case "Up Right Farm":
                Quarry q1 = new Quarry(startX + width - 3, startY + height - 4);
                return q1;
            case "Up Left Farm":
                Quarry q2 = new Quarry(startX, startY + height - 4);
                return q2;
            case "Down Left Farm":
                Quarry q3 = new Quarry(startX, startY + 1);
                return q3;
            case "Down Right Farm":
                Quarry q4 = new Quarry(startX + width - 3, startY + 1);
                return q4;
        }
        return null;
    }

    public Lake createLake() {
        switch (name) {
            case "Up Right Farm":
                Lake l1 = new Lake(startX + width / 2 + 2, startY + height / 2 - 3, 5, 5, "lake", Lake.LakeType.RIVER);
                return l1;
            case "Up Left Farm":
                Lake l2 = new Lake(startX + width / 2 - 6, startY + height / 2 - 3, 5, 5, "lake", Lake.LakeType.RIVER);
                return l2;
            case "Down Left Farm":
                Lake l3 = new Lake(startX + width / 2 - 6, startY + height / 2, 5, 5, "lake", Lake.LakeType.RIVER);
                return l3;
            case "Down Right Farm":
                Lake l4 = new Lake(startX + width / 2 + 2, startY + height / 2, 5, 5, "lake", Lake.LakeType.RIVER);
                return l4;
        }
        return null;
    }

    public void markBuildingArea(Location[][] tiles) {
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

    public void markGreenHouseArea(Location[][] tiles) {
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

    public void markQuarry(Location[][] tiles) {
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

    public void markLake(Location[][] tiles) {
        Lake l = getLake();
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

    public void markBarnArea(Location[][] tiles, Barn barn) {
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

    public void markCoopArea(Location[][] tiles, Coop coop) {
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

    public void addAnimal(Animal animal) {
        animals.add(animal);
    }

    public void removeAnimal(Animal animal) {
        animals.remove(animal);
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
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

    public Lake getLake() {
        return lake;
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

    public void addBarn(Location[][] tiles, Barn barn) {
        markBarnArea(tiles, barn);
        barns.add(barn);
    }

    public void addCoop(Location[][] tiles, Coop coop) {
        markCoopArea(tiles, coop);
        coops.add(coop);
    }

}