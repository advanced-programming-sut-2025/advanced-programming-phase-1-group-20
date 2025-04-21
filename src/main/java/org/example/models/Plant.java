package org.example.models;

import org.example.models.enums.Seasons;
import org.example.models.enums.Types.TileType;

public class Plant extends Item {
    private String source;
    private int[] stage;
    private int totalHarvest;
    private boolean oneTimeHarvest;
    private int multipleHarvest;
    private int baseSellPrice;
    private boolean edible;
    private int energyGet;
    private int healthGet;
    private Seasons[] seasons;
    private boolean harvestable;
    private boolean giantable;


    public Plant(String name, int price, String source , int[] stage, int totalHarvest,
                 boolean oneTimeHarvest, int multipleHarvest, boolean edible,
                 int energyGet, int healthGet, Seasons[] season,
                 boolean harvestable, boolean giantable) {
        super(name, price);
        this.stage = stage;
        this.totalHarvest = totalHarvest;
        this.oneTimeHarvest = oneTimeHarvest;
        this.multipleHarvest = multipleHarvest;
        this.edible = edible;
        this.energyGet = energyGet;
        this.healthGet = healthGet;
        this.seasons = season;
        this.harvestable = harvestable;
        this.giantable = giantable;
    }

    public String getName(){
        return super.getName();
    }


    public void showPlant() {
        System.out.println("Name: " + this.name);
        System.out.println("Source: " + this.source);
        System.out.printf("Stages: ");
        for(int i = 0; i < this.stage.length ; i++){
            if(i < this.stage.length - 1){
                System.out.printf(i + "-");
            }else{
                System.out.printf(i + "");
            }
        }
        System.out.println();
        System.out.println(this.totalHarvest);
        System.out.println(this.oneTimeHarvest);
        System.out.println("Regrowth time: ");
        System.out.println("Base Sell Price: " + this.price);
        System.out.println("Is Edible: " + this.edible);
        System.out.println("Base Energy: " + this.energyGet);
        System.out.println("Base Health: " + this.healthGet);

    }

    public void putPlant(Location location) {
        if(!this.giantable){
            //handling adding objects into map :: waiting for taha
        }else{
            //for example
            Location rightCorner = new Location(location.xAxis + 1 , location.yAxis + 1 , TileType.GRASS);
            //handling adding one obj into four squares.
        }
    }

    public void harvestPlant(Location location) {
        if(!this.giantable){
            //handling removing objects into map :: waiting for taha
            if(this.oneTimeHarvest){

            }else{

            }
        }else{
            //for example
            Location rightCorner = new Location(location.xAxis + 1 , location.yAxis + 1 , TileType.GRASS);
            //handling removing one obj into four squares.
            if(this.oneTimeHarvest){

            }else{

            }
        }
    }

    private int getBaseSell() {
        return this.baseSellPrice;
    }

    private boolean isEdible() {
        return this.edible;
    }

    private int getHealth() {
        return this.healthGet;
    }

    private Seasons[] getSeasons() {
        return this.seasons;
    }

    private boolean isGiantable() {
        return this.giantable;
    }

    private void showPlants(Location location) {
        //loading the object from location
        System.out.println("Name: ");
    }




}
