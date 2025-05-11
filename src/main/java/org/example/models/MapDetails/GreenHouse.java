package org.example.models.MapDetails;

import org.example.models.common.Location;
import org.example.models.entities.Plant;
import org.example.models.enums.Weather;

import java.util.ArrayList;
import java.util.List;


public class GreenHouse {
    private int x;
    private int y;
    private static final int height = 6;
    private static final int width = 5;
    private String name;
    private final List<Plant> greenHousePlants;
    private final Weather weather;
    private final boolean isBuilt;


    public GreenHouse(int x, int y) {
        this.x = x;
        this.y = y;
        this.greenHousePlants = new ArrayList<>();
        this.weather = Weather.GREENHOUSE;
        this.isBuilt = true;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void addPlant(Plant plant) {
        greenHousePlants.add(plant);
    }

    public void removePlant(Plant plant) {
        greenHousePlants.remove(plant);
    }

    public boolean isInside(Location location) {
        return location.xAxis >= x && location.xAxis <= x + width && location.yAxis >= y && location.yAxis <= y + height;
    }

    public Weather getWeather() {
        return weather;
    }

    public boolean isBuilt() {
        return isBuilt;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
