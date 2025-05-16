package org.example.models;

import org.example.models.common.Location;
import org.example.models.common.Result;
import org.example.models.entities.animal.BarnAnimal;
import org.example.models.enums.Types.BarnTypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Barn implements Serializable {
    private int height = 7;
    private int width = 4;
    private BarnTypes type;
    private Location location;
    private List<BarnAnimal> animals;
    private int capacity;
    private int animalCount;
    private String name;
    private int x;
    private int y;

    public Barn(BarnTypes type, Location location, String name) {
        this.type = type;
        this.location = location;
        this.name = name;
        this.animals = new ArrayList<>();

        // Set capacity based on barn type
        switch (type) {
            case NORMAL_BARN:
                this.capacity = 4;
                break;
            case BIG_BARN:
                this.capacity = 8;
                break;
            case DELUXE_BARN:
                this.capacity = 12;
                break;
            default:
                this.capacity = 4;
        }

        this.animalCount = 0;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    /**
     * Add an animal to the barn
     */
    public Result addAnimal(BarnAnimal animal) {
        if (animalCount >= capacity) {
            return Result.error("This barn is full. Cannot add more animals.");
        }

        // Check if animal can live in this type of barn
        if (!animal.canLiveIn(type)) {
            return Result.error("This animal requires a better barn.");
        }

        animals.add(animal);
        animalCount++;
        return Result.success("Added " + animal.getName() + " to " + name);
    }

    /**
     * Remove an animal from the barn
     */
    public Result removeAnimal(String animalName) {
        for (int i = 0; i < animals.size(); i++) {
            if (animals.get(i).getName().equalsIgnoreCase(animalName)) {
                animals.remove(i);
                animalCount--;
                return Result.success(animalName + " has been removed from " + name);
            }
        }
        return Result.error("No animal named " + animalName + " found in this barn.");
    }

    public Result feedAnimal(int index) {
        if (index < 0 || index >= animals.size()) {
            return Result.error("Invalid animal index.");
        }

        BarnAnimal animal = animals.get(index);
        if (animal.isHasBeenFed()) {
            return Result.error(animal.getName() + " has already been fed today.");
        }

        animal.feed();
        return Result.success(animal.getName() + " has been fed.");
    }


    public Result feedAllAnimals() {
        if (animals.isEmpty()) {
            return Result.error("There are no animals in this barn.");
        }

        int fedCount = 0;
        for (BarnAnimal animal : animals) {
            if (!animal.isHasBeenFed()) {
                animal.feed();
                fedCount++;
            }
        }

        if (fedCount == 0) {
            return Result.error("All animals have already been fed today.");
        }

        return Result.success("Fed " + fedCount + " animals in " + name);
    }


    public void nextDay() {
        for (BarnAnimal animal : animals) {
            animal.nextDay();
        }
    }

    public void moveAnimalsInside() {
        for (BarnAnimal animal : animals) {
            if (animal.isOutside()) {
                animal.moveInside();
            }
        }
    }

    public Result upgrade() {
        switch (type) {
            case NORMAL_BARN:
                type = BarnTypes.BIG_BARN;
                capacity = 8;
                return Result.success(name + " upgraded to Big Barn");
            case BIG_BARN:
                type = BarnTypes.DELUXE_BARN;
                capacity = 12;
                return Result.success(name + " upgraded to Deluxe Barn");
            case DELUXE_BARN:
                return Result.error("This barn is already at maximum upgrade level.");
            default:
                return Result.error("Unknown barn type.");
        }
    }

    // Getters and Setters
    public BarnTypes getType() {
        return type;
    }

    public void setType(BarnTypes type) {
        this.type = type;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<BarnAnimal> getAnimals() {
        return animals;
    }

    public void setAnimals(List<BarnAnimal> animals) {
        this.animals = animals;
        this.animalCount = animals.size();
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getAnimalCount() {
        return animalCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}