package models;

import models.enums.Seasons;

public class Plants {
    private String name;
    private String seed;
    private int stage;
    private int stageCount;
    private int multipleHarvest;
    private int restingHarvest;
    private int baseSell;
    private boolean eatable;
    private int energyGet;
    private Seasons season;
    private boolean harvestable;
    private boolean giantable;

    public Plants(String name, String seed, int stage,
                  int stageCount, int multipleHarvest, int restingHarvest,
                  int baseSell, boolean eatable, int energyGet, Seasons season,
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
}
