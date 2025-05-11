package org.example.models;

import org.example.models.Items.Item;
import org.example.models.common.Result;
import org.example.models.entities.animal.BarnAnimal;
import org.example.models.enums.Seasons;
import org.example.models.enums.Types.BarnAnimalTypes;
import org.example.models.enums.Types.BarnTypes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Barn implements Serializable {
    private int capacity;
    private int productPerDay;
    private int buildCost;
    private List<BarnAnimal> animals;
    private boolean hasHeater;
    private boolean hasAutoFeeder;

    public Barn(int capacity, int productPerDay, int buildCost) {
        this.capacity = capacity;
        this.productPerDay = productPerDay;
        this.buildCost = buildCost;
        this.animals = new ArrayList<>();
        this.hasHeater = false;
        this.hasAutoFeeder = false;
    }

    public Result addAnimal(BarnAnimal animal) {
        // Check if there is space
        if (animals.size() >= capacity) {
            return Result.error("The barn is at full capacity (" + capacity + " animals)");
        }

        // Check if this animal can live in this barn type
        BarnTypes barnType = getBarnType();
        if (!animal.canLiveIn(barnType)) {
            return Result.error("This animal requires a better barn");
        }

        // Add the animal
        animals.add(animal);
        return Result.success(animal.getName() + " added to the barn");
    }

    public Result removeAnimal(int index) {
        if (index < 0 || index >= animals.size()) {
            return Result.error("Invalid animal index");
        }

        BarnAnimal removed = animals.remove(index);
        return Result.success(removed.getName() + " removed from the barn");
    }


    public List<Item> processDailyProduction(Seasons currentSeason) {
        List<Item> products = new ArrayList<>();

        // Update all animals
        for (BarnAnimal animal : animals) {
            // Skip pig production in winter
            if (animal.getType() == BarnAnimalTypes.PIG && currentSeason == Seasons.WINTER) {
                continue;
            }

            // Advance the day for the animal
            animal.advanceDay();

            // Auto-feed animals if auto-feeder is installed
            if (hasAutoFeeder) {
                animal.feed();
                animal.increaseHappiness(3); // Auto feeders make animals slightly happier
            }

            // Adjust happiness
            if (hasHeater) {
                animal.increaseHappiness(5); // Heated barns make animals happier
            } else if (currentSeason == Seasons.WINTER) {
                animal.decreaseHappiness(2); // Cold barns make animals unhappy in winter
            }

            // Get product if available
            Item product = animal.getProduct();
            if (product != null) {
                products.add(product);
            }
        }

        return products;
    }

    public Result feedAnimal(int index) {
        if (index < 0 || index >= animals.size()) {
            return Result.error("Invalid animal index");
        }

        BarnAnimal animal = animals.get(index);
        animal.feed();
        animal.increaseHappiness(5); // Feeding makes animals happier

        return Result.success(animal.getName() + " has been fed");
    }

    public Result petAnimal(int index) {
        if (index < 0 || index >= animals.size()) {
            return Result.error("Invalid animal index");
        }

        BarnAnimal animal = animals.get(index);
        animal.increaseHappiness(10); // Petting makes animals significantly happier

        return Result.success(animal.getName() + " has been petted and is happier");
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

    public List<BarnAnimal> getAnimals() {
        return new ArrayList<>(animals); // Return a copy to prevent modification
    }

    public BarnTypes getBarnType() {
        if (capacity == 12) {
            return BarnTypes.DELUXE_BARN;
        } else if (capacity == 8) {
            return BarnTypes.BIG_BARN;
        } else {
            return BarnTypes.NORMAL_BARN;
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
        BarnTypes currentType = getBarnType();

        switch (currentType) {
            case NORMAL_BARN:
                // Upgrade to BIG_BARN
                capacity = BarnTypes.BIG_BARN.getCapacity();
                productPerDay = BarnTypes.BIG_BARN.getProductPerDay();
                return Result.success("Upgraded to Big Barn successfully");

            case BIG_BARN:
                // Upgrade to DELUXE_BARN
                capacity = BarnTypes.DELUXE_BARN.getCapacity();
                productPerDay = BarnTypes.DELUXE_BARN.getProductPerDay();
                return Result.success("Upgraded to Deluxe Barn successfully");

            case DELUXE_BARN:
                return Result.error("This barn is already at maximum level");

            default:
                return Result.error("Unknown barn type");
        }
    }

    public int getUpgradeCost() {
        BarnTypes currentType = getBarnType();

        switch (currentType) {
            case NORMAL_BARN:
                return BarnTypes.BIG_BARN.getBuildCost() - BarnTypes.NORMAL_BARN.getBuildCost();

            case BIG_BARN:
                return BarnTypes.DELUXE_BARN.getBuildCost() - BarnTypes.BIG_BARN.getBuildCost();

            case DELUXE_BARN:
            default:
                return 0; // Already at maximum level
        }
    }


    public void displayBarnInfo() {
        System.out.println("Barn Information:");
        System.out.println("Type: " + getBarnType().getDisplayName());
        System.out.println("Capacity: " + animals.size() + "/" + capacity);
        System.out.println("Heater: " + (hasHeater ? "Installed" : "Not Installed"));
        System.out.println("Auto Feeder: " + (hasAutoFeeder ? "Installed" : "Not Installed"));

        if (animals.isEmpty()) {
            System.out.println("No animals in this barn.");
        } else {
            System.out.println("\nAnimals:");
            for (int i = 0; i < animals.size(); i++) {
                BarnAnimal animal = animals.get(i);
                System.out.println((i + 1) + ". " + animal.getName() +
                        " - Happiness: " + animal.getHappinessLevel() + "%" +
                        (animal.isHasBeenFed() ? " (Fed)" : " (Hungry)"));
            }
        }
    }
}