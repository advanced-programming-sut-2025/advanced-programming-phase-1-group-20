package org.example.models.MapDetails;

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
    //private final Lake lake;
    private final GreenHouse greenHouse;
    private final Quarry quarry;


    public Farm(int startX, int startY, int width, int height, String name, Player owner) {
        this.startX = startX;
        this.startY = startY;
        this.width = width;
        this.height = height;
        this.name = name;
        this.owner = owner;
        this.animals = new ArrayList<>();
        this.building = createBuilding();
        //this.lake = createLake();
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
            case "Down Right Farm":
                Building b3 = new Building(startX, startY + height - 4, "house", "house");
                return b3;
            case "Down Left Farm":
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
            case "Down Right Farm":
                GreenHouse g3 = new GreenHouse(startX + width - 5, startY + height - 5);
                return g3;
            case "Down Left Farm":
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
            case "Down Right Farm":
                Quarry q3 = new Quarry(startX, startY + 1);
                return q3;
            case "Down Left Farm":
                Quarry q4 = new Quarry(startX + width - 3, startY + 1);
                return q4;
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

}