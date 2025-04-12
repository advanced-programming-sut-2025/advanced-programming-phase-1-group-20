package models.enums;

import models.Plants;

public enum AllPlants {
    CARROT("Carrot" , "Carrot Seed" , 1 , 3 , 0 , 0, 35 ,  true,75 , Seasons.SPRING , true , false),
    ;//other plants trees & etc ...

    private final String name;
    private final String seed;
    private final int stage;
    private final int stageCount;
    private final int multipleHarvest;
    private final int restingHarvest;
    private final int baseSell;
    private final boolean eatable;
    private final int energyGet;
    private final Seasons season;
    private final boolean harvestable;
    private final boolean giantable;

    AllPlants(String name, String seed, int stage,
              int stageCount, int multipleHarvest,
              int restingHarvest, int baseSell,
              boolean eatable, int energyGet, Seasons season,
              boolean harvestable, boolean giantable) {
        this.name = name;
        this.seed = seed;
        this.stage = stage;
        this.stageCount = stageCount;
        this.multipleHarvest = multipleHarvest;
        this.restingHarvest = restingHarvest;
        this.baseSell = baseSell;
        this.eatable = eatable;
        this.energyGet = energyGet;
        this.season = season;
        this.harvestable = harvestable;
        this.giantable = giantable;
    }
    public Plants createPlant(){
        return new Plants(name,seed , stage,stageCount,multipleHarvest,restingHarvest,baseSell,eatable,energyGet,season,harvestable,giantable);
    }
}
