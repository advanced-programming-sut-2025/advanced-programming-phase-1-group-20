package org.example.models;

import org.example.models.common.Location;
import org.example.models.common.Result;
import org.example.models.entities.animal.CoopAnimal;
import org.example.models.enums.Types.Cages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Coop implements Serializable {
    private Cages type;
    private Location location;
    private List<CoopAnimal> animals;
    private int capacity;
    private int animalCount;
    private String name;
    private int x;
    private int y;
    private int width = 3;
    private int height = 6;

    public Coop(Cages type, Location location, String name) {
        this.type = type;
        this.location = location;
        this.name = name;
        this.animals = new ArrayList<>();

        // Set capacity based on coop type
        switch (type) {
            case NORMAL_COOP:
                this.capacity = 4;
                break;
            case BIG_CAGE:
                this.capacity = 8;
                break;
            case DELUXE_CAGE:
                this.capacity = 12;
                break;
            default:
                this.capacity = 4;
        }

        this.animalCount = 0;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean contains(int checkX, int checkY) {
        return checkX >= x && checkX < x + width && checkY >= y && checkY < y + height;
    }

    public Result addAnimal(CoopAnimal animal) {
        if (animalCount >= capacity) {
            return Result.error("This coop is full. Cannot add more animals.");
        }

        if (!animal.canLiveIn(type)) {
            return Result.error("This animal requires a better coop.");
        }

        animals.add(animal);
        animalCount++;
        return Result.success("Added " + animal.getName() + " to " + name);
    }


    public Result removeAnimal(String animalName) {
        for (int i = 0; i < animals.size(); i++) {
            if (animals.get(i).getName().equalsIgnoreCase(animalName)) {
                animals.remove(i);
                animalCount--;
                return Result.success(animalName + " has been removed from " + name);
            }
        }
        return Result.error("No animal named " + animalName + " found in this coop.");
    }

    /**
     * Feed an animal by index
     */
    public Result feedAnimal(int index) {
        if (index < 0 || index >= animals.size()) {
            return Result.error("Invalid animal index.");
        }

        CoopAnimal animal = animals.get(index);
        if (animal.isHasBeenFed()) {
            return Result.error(animal.getName() + " has already been fed today.");
        }

        animal.feed();
        return Result.success(animal.getName() + " has been fed.");
    }

    /**
     * Feed all animals in the coop
     */
    public Result feedAllAnimals() {
        if (animals.isEmpty()) {
            return Result.error("There are no animals in this coop.");
        }

        int fedCount = 0;
        for (CoopAnimal animal : animals) {
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
        for (CoopAnimal animal : animals) {
            animal.nextDay();
        }
    }


    public void moveAnimalsInside() {
        for (CoopAnimal animal : animals) {
            if (animal.isOutside()) {
                animal.moveInside();
            }
        }
    }

    public Result upgrade() {
        switch (type) {
            case NORMAL_COOP:
                type = Cages.BIG_CAGE;
                capacity = 8;
                return Result.success(name + " upgraded to Big Coop");
            case BIG_CAGE:
                type = Cages.DELUXE_CAGE;
                capacity = 12;
                return Result.success(name + " upgraded to Deluxe Coop");
            case DELUXE_CAGE:
                return Result.error("This coop is already at maximum upgrade level.");
            default:
                return Result.error("Unknown coop type.");
        }
    }

    // Getters and Setters
    public Cages getType() {
        return type;
    }

    public void setType(Cages type) {
        this.type = type;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<CoopAnimal> getAnimals() {
        return animals;
    }

    public void setAnimals(List<CoopAnimal> animals) {
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