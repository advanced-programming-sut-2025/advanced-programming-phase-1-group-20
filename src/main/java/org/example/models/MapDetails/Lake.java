package org.example.models.MapDetails;

import org.example.models.entities.animal.Fish;
import org.example.models.enums.Seasons;
import org.example.models.enums.Types.FishType;
import org.example.models.enums.Weather;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Lake implements Serializable {
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final String name;
    private final LakeType type;
    private final List<Fish> availableFish;
    private final Random random;

    public Lake(int x, int y, int width, int height, String name, LakeType type) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.name = name;
        this.type = type;
        this.availableFish = new ArrayList<>();
        this.random = new Random();
    }

    public boolean contains(int checkX, int checkY) {
        return checkX >= x && checkX < x + width && checkY >= y && checkY < y + height;
    }

    public void updateAvailableFish(Seasons currentSeason, int fishingSkill) {
        availableFish.clear();
        FishType[] seasonalFish = FishType.getAvailableFish(currentSeason, true, fishingSkill);

        for (FishType fishType : seasonalFish) {

            if (!fishType.isLegendary() || canHaveLegendaryFish(fishType)) {
                availableFish.add(new Fish(fishType, currentSeason));
            }

        }
    }

    private boolean canHaveLegendaryFish(FishType fishType) {
        if (fishType == FishType.CRIMSONFISH && name.equals("Ocean")) {
            return true;
        }
        else if (fishType == FishType.ANGLER && name.equals("Mountain Lake")) {
            return true;
        }
        else if (fishType == FishType.LEGEND && name.equals("Mountain Lake")) {
            return true;
        }
        else if (fishType == FishType.GLACIERFISH && name.equals("Winter Lake")) {
            return true;
        }
        else if (fishType == FishType.MUTANT_CARP && name.equals("Sewers")) {
            return true;
        }
        return false;
    }

    public List<Fish> fish(int fishingSkill, double rodMultiplier, Weather currentWeather) {
        List<Fish> caughtFish = new ArrayList<>();

        double weatherMultiplier;
        if (currentWeather == Weather.SUNNY) {
            weatherMultiplier = 1.5;
        }
        else if (currentWeather == Weather.RAINY) {
            weatherMultiplier = 1.2;
        }
        else if (currentWeather == Weather.STORMY) {
            weatherMultiplier = 0.5;
        }
        else {
            weatherMultiplier = 1.0;
        }

        double r = random.nextDouble();
        int fishCount = (int) Math.ceil((2 + r * weatherMultiplier * (fishingSkill + 1)));
        fishCount = Math.min(fishCount, 6);

        for (int i = 0; i < fishCount; i++) {
            if (!availableFish.isEmpty()) {
                Fish baseFish = availableFish.get(random.nextInt(availableFish.size()));

                int qualityScore = (int) ((random.nextDouble() * (fishingSkill + 2) * rodMultiplier) / (fishCount - 7));
                int quality;
                if (qualityScore >= 5) {
                    quality = 3;
                }
                else if (qualityScore >= 3) {
                    quality = 2;
                }
                else if (qualityScore >= 1) {
                    quality = 1;
                }
                else {
                    quality = 0;
                }

                caughtFish.add(new Fish(baseFish.getType(), quality, baseFish.getSeason()));
            }
        }

        return caughtFish;
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

    public String getName() {
        return name;
    }

    public LakeType getType() {
        return type;
    }

    public List<Fish> getAvailableFish() {
        return new ArrayList<>(availableFish);
    }

    public enum LakeType {
        RIVER
    }
}