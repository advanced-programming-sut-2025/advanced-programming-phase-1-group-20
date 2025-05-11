package org.example.models.MapDetails;

import org.example.models.common.Location;
import org.example.models.entities.Plant;
import org.example.models.enums.Weather;

import java.util.ArrayList;
import java.util.List;


public class GreenHouse {
    private static final int ROWS = 5; // 5 rows
    private static final int COLS = 6; // 6 columns
    private final Location leftCorner;
    private final Location rightCorner;
    private final List<Plant> greenHousePlants;
    private final Weather weather;
    private final boolean isBuilt;


    public GreenHouse(Location leftCorner, Location rightCorner) {
        this.leftCorner = leftCorner;
        this.rightCorner = rightCorner;
        this.greenHousePlants = new ArrayList<>();
        this.weather = Weather.GREENHOUSE;
        this.isBuilt = true;
    }

    public static int getRows() {
        return ROWS;
    }

    public static int getCols() {
        return COLS;
    }


    public void addPlant(Plant plant) {
        greenHousePlants.add(plant);
    }


    public void removePlant(Plant plant) {
        greenHousePlants.remove(plant);
    }

    public boolean isInside(Location location) {
        return location.xAxis >= leftCorner.xAxis && location.xAxis <= rightCorner.xAxis && location.yAxis >= leftCorner.yAxis && location.yAxis <= rightCorner.yAxis;
    }


    public Weather getWeather() {
        return weather;
    }


    public boolean isBuilt() {
        return isBuilt;
    }


    public Location getLeftCorner() {
        return leftCorner;
    }

    public Location getRightCorner() {
        return rightCorner;
    }
}
