package org.example.models.MapDetails;

import org.example.models.common.Location;
import org.example.models.entities.Plant;
import org.example.models.enums.Weather;

import java.util.ArrayList;
import java.util.List;

public class GreenHouse {
    Location leftCorner;
    Location rightCorner;
    List<Plant> greenHousePlants;
    Weather weather;

    GreenHouse(Location leftCorner, Location rightCorner) {
        this.leftCorner = leftCorner;
        this.rightCorner = rightCorner;
        this.greenHousePlants = new ArrayList<>();
        this.weather = Weather.GREENHOUSE;
    }

    public void addPlant(Plant plant) {
        greenHousePlants.add(plant);
    }

    public void removePlant(Plant plant) {
        greenHousePlants.remove(plant);
    }
}
