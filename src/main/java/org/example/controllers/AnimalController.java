package org.example.controllers;

import org.example.models.App;
import org.example.models.Barn;
import org.example.models.Coop;
import org.example.models.Items.Item;
import org.example.models.MapDetails.Farm;
import org.example.models.Player.Backpack;
import org.example.models.Player.Player;
import org.example.models.common.Result;
import org.example.models.entities.animal.BarnAnimal;
import org.example.models.entities.animal.CoopAnimal;
import org.example.models.enums.Types.Quality;
import org.example.models.enums.Types.TileType;
import org.example.models.enums.Weather;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AnimalController {
    private Random random = new Random();

    public Result petAnimal(String[] args) {
        String animalName = args[0];
        Player player = App.getGame().getCurrentPlayer();
        Farm farm = player.getCurrentFarm();

        // Check for barn animals
        for (Barn barn : farm.getBarns()) {
            for (BarnAnimal animal : barn.getAnimals()) {
                if (animal.getName().equalsIgnoreCase(animalName)) {
                    animal.increaseHappiness(15);
                    return Result.success("You pet " + animalName + " and it seems happier!");
                }
            }
        }

        // Check for coop animals
        for (Coop coop : farm.getCoops()) {
            for (CoopAnimal animal : coop.getAnimals()) {
                if (animal.getName().equalsIgnoreCase(animalName)) {
                    animal.increaseHappiness(15);
                    return Result.success("You pet " + animalName + " and it seems happier!");
                }
            }
        }

        return Result.error("No animal found with the name: " + animalName);
    }

    public Result shepherdAnimals(String[] args) {
        String animalName = args[0];
        int x = Integer.parseInt(args[1]);
        int y = Integer.parseInt(args[2]);

        Player player = App.getGame().getCurrentPlayer();
        Farm farm = player.getCurrentFarm();

        // Check weather conditions
        Weather currentWeather = App.getGame().getDate().getWeatherToday();
        if (currentWeather == Weather.RAINY || currentWeather == Weather.SNOWY || currentWeather == Weather.STORMY) {
            return Result.error("Animals cannot go outside in " + currentWeather.toString().toLowerCase() + " weather.");
        }

        // Validate target location
        if (!farm.contains(x, y)) {
            return Result.error("The specified location is outside the farm boundaries.");
        }

        if (farm.getTile(x, y) != TileType.GRASS) {
            return Result.error("Animals can only be moved to grass tiles.");
        }

        BarnAnimal barnAnimal = null;
        CoopAnimal coopAnimal = null;
        Barn animalBarn = null;
        Coop animalCoop = null;

        // Find the animal in barns
        for (Barn barn : farm.getBarns()) {
            for (BarnAnimal animal : barn.getAnimals()) {
                if (animal.getName().equalsIgnoreCase(animalName)) {
                    barnAnimal = animal;
                    animalBarn = barn;
                    break;
                }
            }
            if (barnAnimal != null) break;
        }

        // If not found in barns, check coops
        if (barnAnimal == null) {
            for (Coop coop : farm.getCoops()) {
                for (CoopAnimal animal : coop.getAnimals()) {
                    if (animal.getName().equalsIgnoreCase(animalName)) {
                        coopAnimal = animal;
                        animalCoop = coop;
                        break;
                    }
                }
                if (coopAnimal != null) break;
            }
        }

        if (barnAnimal == null && coopAnimal == null) {
            return Result.error("No animal found with the name: " + animalName);
        }

        // Move the animal and increase happiness
        if (barnAnimal != null) {
            barnAnimal.increaseHappiness(8);
            return Result.success(animalName + " has been moved to the specified location and is grazing happily.");
        } else {
            coopAnimal.increaseHappiness(8);
            return Result.success(animalName + " has been moved to the specified location and is grazing happily.");
        }
    }

    public Result feedHay(String[] args) {
        String animalName = args[0];
        Player player = App.getGame().getCurrentPlayer();
        Farm farm = player.getCurrentFarm();

        // Check if player has hay
        Backpack backpack = player.getBackpack();
        Item hayItem = backpack.getItem("Hay");
        if (hayItem == null) {
            return Result.error("You don't have any hay in your inventory.");
        }

        BarnAnimal barnAnimal = null;
        CoopAnimal coopAnimal = null;
        Barn animalBarn = null;
        Coop animalCoop = null;
        int animalIndex = -1;

        // Find the animal in barns
        for (Barn barn : farm.getBarns()) {
            for (int i = 0; i < barn.getAnimals().size(); i++) {
                BarnAnimal animal = barn.getAnimals().get(i);
                if (animal.getName().equalsIgnoreCase(animalName)) {
                    barnAnimal = animal;
                    animalBarn = barn;
                    animalIndex = i;
                    break;
                }
            }
            if (barnAnimal != null) break;
        }

        // If not found in barns, check coops
        if (barnAnimal == null) {
            for (Coop coop : farm.getCoops()) {
                for (int i = 0; i < coop.getAnimals().size(); i++) {
                    CoopAnimal animal = coop.getAnimals().get(i);
                    if (animal.getName().equalsIgnoreCase(animalName)) {
                        coopAnimal = animal;
                        animalCoop = coop;
                        animalIndex = i;
                        break;
                    }
                }
                if (coopAnimal != null) break;
            }
        }

        if (barnAnimal == null && coopAnimal == null) {
            return Result.error("No animal found with the name: " + animalName);
        }

        // Remove hay from inventory
        backpack.remove(hayItem, 1);

        // Feed the animal
        if (barnAnimal != null) {
            Result feedResult = animalBarn.feedAnimal(animalIndex);
            if (feedResult.success()) {
                return Result.success("You fed " + animalName + " with hay.");
            } else {
                return feedResult;
            }
        } else {
            coopAnimal.increaseHappiness(5);
            return Result.success("You fed " + animalName + " with hay.");
        }
    }

    public Result collectProduce(String[] args) {
        String animalName = args[0];
        Player player = App.getGame().getCurrentPlayer();
        Farm farm = player.getCurrentFarm();

        BarnAnimal barnAnimal = null;
        CoopAnimal coopAnimal = null;

        // Find the animal in barns
        for (Barn barn : farm.getBarns()) {
            for (BarnAnimal animal : barn.getAnimals()) {
                if (animal.getName().equalsIgnoreCase(animalName)) {
                    barnAnimal = animal;
                    break;
                }
            }
            if (barnAnimal != null) break;
        }

        // If not found in barns, check coops
        if (barnAnimal == null) {
            for (Coop coop : farm.getCoops()) {
                for (CoopAnimal animal : coop.getAnimals()) {
                    if (animal.getName().equalsIgnoreCase(animalName)) {
                        coopAnimal = animal;
                        break;
                    }
                }
                if (coopAnimal != null) break;
            }
        }

        if (barnAnimal == null && coopAnimal == null) {
            return Result.error("No animal found with the name: " + animalName);
        }

        // Check if the animal has produce to collect
        if (barnAnimal != null) {
            Item product = barnAnimal.getProduct();
            if (product == null) {
                return Result.error(animalName + " doesn't have any produce ready to collect.");
            }

            // For milk and wool, check if player has required tools
            String animalType = barnAnimal.getType().getName().toLowerCase();
            if ((animalType.contains("cow") || animalType.contains("goat")) &&
                    player.getCurrentTool() == null || !player.getCurrentTool().getName().equals("Milk Pail")) {
                return Result.error("You need to equip a Milk Pail to collect milk.");
            }

            if (animalType.contains("sheep") &&
                    player.getCurrentTool() == null || !player.getCurrentTool().getName().equals("Shears")) {
                return Result.error("You need to equip Shears to collect wool.");
            }

            // Add product to player's inventory
            player.getBackpack().add(product, 1);
            barnAnimal.increaseHappiness(5);

            return Result.success("You collected " + product.getName() + " from " + animalName + ".");
        } else {
            Item product = coopAnimal.getProduct();
            if (product == null) {
                return Result.error(animalName + " doesn't have any produce ready to collect.");
            }

            // Add product to player's inventory
            player.getBackpack().add(product, 1);
            coopAnimal.increaseHappiness(5);

            return Result.success("You collected " + product.getName() + " from " + animalName + ".");
        }
    }

    public Result checkProduces() {
        Player player = App.getGame().getCurrentPlayer();
        Farm farm = player.getCurrentFarm();
        List<String> readyAnimals = new ArrayList<>();

        // Check barn animals
        for (Barn barn : farm.getBarns()) {
            for (BarnAnimal animal : barn.getAnimals()) {
                Item product = animal.getProduct();
                if (product != null) {
                    Quality quality = product.getQuality();
                    readyAnimals.add(animal.getName() + ": " + product.getName() + " (Quality: " + quality + ")");
                }
            }
        }

        // Check coop animals
        for (Coop coop : farm.getCoops()) {
            for (CoopAnimal animal : coop.getAnimals()) {
                Item product = animal.getProduct();
                if (product != null) {
                    Quality quality = product.getQuality();
                    readyAnimals.add(animal.getName() + ": " + product.getName() + " (Quality: " + quality + ")");
                }
            }
        }

        if (readyAnimals.isEmpty()) {
            return Result.success("No animals have produce ready to collect.");
        }

        StringBuilder result = new StringBuilder("Animals with produce ready to collect:\n");
        for (String animalInfo : readyAnimals) {
            result.append("- ").append(animalInfo).append("\n");
        }

        return Result.success(result.toString());
    }

    public Result showAnimals() {
        Player player = App.getGame().getCurrentPlayer();
        Farm farm = player.getCurrentFarm();
        List<String> animalList = new ArrayList<>();

        // Get barn animals
        for (Barn barn : farm.getBarns()) {
            for (BarnAnimal animal : barn.getAnimals()) {
                String fedStatus = animal.isHasBeenFed() ? "Fed" : "Not Fed";
                animalList.add(animal.getName() + " (Barn) - Friendship: " + animal.getHappinessLevel() +
                        " - " + fedStatus);
            }
        }

        // Get coop animals
        for (Coop coop : farm.getCoops()) {
            for (CoopAnimal animal : coop.getAnimals()) {
                animalList.add(animal.getName() + " (Coop) - Friendship: " + animal.getHappinessLevel());
            }
        }

        if (animalList.isEmpty()) {
            return Result.success("You don't have any animals.");
        }

        StringBuilder result = new StringBuilder("Your animals:\n");
        for (String animalInfo : animalList) {
            result.append("- ").append(animalInfo).append("\n");
        }

        return Result.success(result.toString());
    }

    public Result sellAnimal(String[] args) {
        String animalName = args[0];
        Player player = App.getGame().getCurrentPlayer();
        Farm farm = player.getCurrentFarm();

        BarnAnimal barnAnimal = null;
        CoopAnimal coopAnimal = null;
        Barn animalBarn = null;
        Coop animalCoop = null;

        // Find the animal in barns
        for (Barn barn : farm.getBarns()) {
            for (BarnAnimal animal : barn.getAnimals()) {
                if (animal.getName().equalsIgnoreCase(animalName)) {
                    barnAnimal = animal;
                    animalBarn = barn;
                    break;
                }
            }
            if (barnAnimal != null) break;
        }

        // If not found in barns, check coops
        if (barnAnimal == null) {
            for (Coop coop : farm.getCoops()) {
                for (CoopAnimal animal : coop.getAnimals()) {
                    if (animal.getName().equalsIgnoreCase(animalName)) {
                        coopAnimal = animal;
                        animalCoop = coop;
                        break;
                    }
                }
                if (coopAnimal != null) break;
            }
        }

        if (barnAnimal == null && coopAnimal == null) {
            return Result.error("No animal found with the name: " + animalName);
        }

        // Calculate selling price based on friendship
        int basePrice;
        int friendshipLevel;

        if (barnAnimal != null) {
            basePrice = barnAnimal.getPrice();
            friendshipLevel = barnAnimal.getHappinessLevel();
            animalBarn.getAnimals().remove(barnAnimal);
        } else {
            basePrice = coopAnimal.getPrice();
            friendshipLevel = coopAnimal.getHappinessLevel();
            animalCoop.getAnimals().remove(coopAnimal);
        }

        // Apply friendship bonus: Price × (Friendship/1000 + 0.3)
        int sellingPrice = (int) (basePrice * (friendshipLevel / 1000.0 + 0.3));

        // Add money to player
        player.increaseMoney(sellingPrice);

        return Result.success("You sold " + animalName + " for " + sellingPrice + " gold.");
    }

    public Result setFriendship(String[] args) {
        String animalName = args[0];
        int amount = Integer.parseInt(args[1]);

        Player player = App.getGame().getCurrentPlayer();
        Farm farm = player.getCurrentFarm();

        BarnAnimal barnAnimal = null;
        CoopAnimal coopAnimal = null;

        // Find the animal in barns
        for (Barn barn : farm.getBarns()) {
            for (BarnAnimal animal : barn.getAnimals()) {
                if (animal.getName().equalsIgnoreCase(animalName)) {
                    barnAnimal = animal;
                    break;
                }
            }
            if (barnAnimal != null) break;
        }

        // If not found in barns, check coops
        if (barnAnimal == null) {
            for (Coop coop : farm.getCoops()) {
                for (CoopAnimal animal : coop.getAnimals()) {
                    if (animal.getName().equalsIgnoreCase(animalName)) {
                        coopAnimal = animal;
                        break;
                    }
                }
                if (coopAnimal != null) break;
            }
        }

        if (barnAnimal == null && coopAnimal == null) {
            return Result.error("No animal found with the name: " + animalName);
        }

        // Set friendship level (capped between 0-1000)
        int clamped = Math.max(0, Math.min(1000, amount));

        if (barnAnimal != null) {
            while (barnAnimal.getHappinessLevel() < clamped) {
                barnAnimal.increaseHappiness(1);
            }
            while (barnAnimal.getHappinessLevel() > clamped) {
                barnAnimal.decreaseHappiness(1);
            }
        } else {
            while (coopAnimal.getHappinessLevel() < clamped) {
                coopAnimal.increaseHappiness(1);
            }
            while (coopAnimal.getHappinessLevel() > clamped) {
                coopAnimal.decreaseHappiness(1);
            }
        }

        return Result.success("Set " + animalName + "'s friendship level to " + clamped);
    }
}