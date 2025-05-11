package org.example.models.MapDetails;

import org.example.models.Player.Player;
import org.example.models.entities.animal.Animal;

import java.util.ArrayList;
import java.util.List;

public class Farm {
    private final int startX;
    private final int startY;
    private final int width;
    private final int height;
    private final String name;
    private final Player owner;
    private int houseX;
    private int houseY;
    private final List<Animal> animals;
    private final Building building;

    public Farm(int startX, int startY, int width, int height, String name, Player owner) {
        this.startX = startX;
        this.startY = startY;
        this.width = width;
        this.height = height;
        this.name = name;
        this.owner = owner;
        this.animals = new ArrayList<>();
        this.building = new Building(startX, startY, "house", "house");
    }

    public boolean contains(int x, int y) {
        return x >= startX && x < startX + width && y >= startY && y < startY + height;
    }

    public void addAnimal(Animal animal) {
        animals.add(animal);
    }

    public void removeAnimal(Animal animal) {
        animals.remove(animal);
    }

    public void setHousePosition(int x, int y) {
        this.houseX = x;
        this.houseY = y;
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

    public int getHouseX() {
        return houseX;
    }

    public int getHouseY() {
        return houseY;
    }

    public List<Animal> getAnimals() {
        return animals;
    }

    public Building getBuilding() {
        return building;
    }

}