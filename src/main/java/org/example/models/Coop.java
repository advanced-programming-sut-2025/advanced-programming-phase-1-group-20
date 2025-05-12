package org.example.models;

import org.example.models.Items.Item;
import org.example.models.common.Result;
import org.example.models.entities.animal.CoopAnimal;
import org.example.models.enums.Types.Cages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Coop implements Serializable {
    private int capacity;
    private int productPerDay;
    private int buildCost;
    private List<CoopAnimal> animals;
    private boolean hasHeater;
    private boolean hasAutoFeeder;

    public Coop(int capacity, int productPerDay, int buildCost) {
        this.capacity = capacity;
        this.productPerDay = productPerDay;
        this.buildCost = buildCost;
        this.animals = new ArrayList<>();
        this.hasHeater = false;
        this.hasAutoFeeder = false;
    }


    public Result addAnimal(CoopAnimal animal) {
        // Check if there is space
        if (animals.size() >= capacity) {
            return Result.error("The cage is at full capacity (" + capacity + " animals)");
        }

        // Check if this animal can live in this cage type
        Cages cageType = getCageType();
        if (!animal.canLiveIn(cageType)) {
            return Result.error("This animal requires a better cage");
        }

        // Add the animal
        animals.add(animal);
        return Result.success(animal.getName() + " added to the cage");
    }


    public Result removeAnimal(int index) {
        if (index < 0 || index >= animals.size()) {
            return Result.error("Invalid animal index");
        }

        CoopAnimal removed = animals.remove(index);
        return Result.success(removed.getName() + " removed from the cage");
    }

    /**
     * Process a new day - collect products and update animal happiness
     *
     * @return List of products collected
     */
    public List<Item> processDailyProduction() {
        List<Item> products = new ArrayList<>();

        // Update all animals
        for (CoopAnimal animal : animals) {
            // Advance the day for the animal
            animal.advanceDay();

            // Adjust happiness
            if (hasHeater) {
                animal.increaseHappiness(5); // Heated cages make animals happier
            }

            if (hasAutoFeeder) {
                animal.increaseHappiness(3); // Auto feeders make animals slightly happier
            } else {
                animal.decreaseHappiness(5); // Manual feeding is tiring for animals
            }

            // Get product if available
            Item product = animal.getProduct();
            if (product != null) {
                products.add(product);
            }
        }

        return products;
    }


    public int getAnimalCount() {
        return animals.size();
    }


    public int getCapacity() {
        return capacity;
    }


    public int getBuildCost() {
        return buildCost;
    }

    public List<CoopAnimal> getAnimals() {
        return new ArrayList<>(animals);
    }

    public Cages getCageType() {
        if (capacity == 12) {
            return Cages.DELUXE_CAGE;
        } else if (capacity == 8) {
            return Cages.BIG_CAGE;
        } else {
            return Cages.NORMAL_COOP;
        }
    }

    public Result installHeater() {
        if (hasHeater) {
            return Result.error("Heater is already installed");
        }

        hasHeater = true;
        return Result.success("Heater installed successfully");
    }

    public Result installAutoFeeder() {
        if (hasAutoFeeder) {
            return Result.error("Auto feeder is already installed");
        }

        hasAutoFeeder = true;
        return Result.success("Auto feeder installed successfully");
    }


    public boolean hasHeater() {
        return hasHeater;
    }


    public boolean hasAutoFeeder() {
        return hasAutoFeeder;
    }


    public Result upgrade() {
        Cages currentType = getCageType();

        switch (currentType) {
            case NORMAL_COOP:
                // Upgrade to BIG_CAGE
                capacity = Cages.BIG_CAGE.getCapacity();
                productPerDay = Cages.BIG_CAGE.getProductPerDay();
                return Result.success("Upgraded to Big Coop successfully");

            case BIG_CAGE:
                // Upgrade to DELUXE_CAGE
                capacity = Cages.DELUXE_CAGE.getCapacity();
                productPerDay = Cages.DELUXE_CAGE.getProductPerDay();
                return Result.success("Upgraded to Deluxe Coop successfully");

            case DELUXE_CAGE:
                return Result.error("This cage is already at maximum level");

            default:
                return Result.error("Unknown cage type");
        }
    }


    public int getUpgradeCost() {
        Cages currentType = getCageType();

        return switch (currentType) {
            case NORMAL_COOP -> Cages.BIG_CAGE.getBuildCost() - Cages.NORMAL_COOP.getBuildCost();
            case BIG_CAGE -> Cages.DELUXE_CAGE.getBuildCost() - Cages.BIG_CAGE.getBuildCost();
            default -> 0;
        };
    }
}