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
import org.example.models.enums.Types.TileType;
import org.example.models.enums.Weather;

public class AnimalController {
    public Result petAnimal(String[] args) {
        String animalName = args[0];
        Player player = App.getGame().getCurrentPlayer();
        Farm farm = player.getCurrentFarm();

        for (Barn barn : farm.getBarns()) {
            for (BarnAnimal animal : barn.getAnimals()) {
                if (animal.getName().equalsIgnoreCase(animalName)) {
                    animal.increaseHappiness(15);
                    return Result.success("You pet " + animalName + " and it seems happier!");
                }
            }
        }

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

        Weather currentWeather = App.getGame().getDate().getWeatherToday();
        if (currentWeather == Weather.RAINY || currentWeather == Weather.SNOWY || currentWeather == Weather.STORMY) {
            return Result.error("Animals cannot go outside in " + currentWeather.toString().toLowerCase() + " weather.");
        }

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

        backpack.remove(hayItem, 1);

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
}
