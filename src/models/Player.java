package models;

import models.enums.Skills;
import models.enums.TileType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Player extends Mob {
    private HashMap<Mob , Integer> friendShip;
    private User user;
    private int energy;
    private List<Skill> skills;
    private boolean energySet = true;
    public Player(User user) {
        this.user = user;
        skills = new ArrayList<Skill>();
        //adding skills:
        Skills.HARVESTING.addSkill(skills);
    }

    //decreasing energy:
    private void decreaseEnergy(){
        if(energySet){}
    }

    public void cheatEnergy(){
        energySet = false;
    }

    public void fishingRod(GameMap gMap , int x , int y){
        //checking the Tile around.
        Tile tile = gMap.getTile(x+1,y);
        //etc
        if(tile == Tile.WATER){
            //implementing func.
        }
    }

    public void doMission(){
        //checking around for NPC's , and doing missions.
    }

    public void giftNPC(NPC npc){
        //implementing func. leveling up npc's friendShip for both.
    }

    public void showCraftingItems(){

    }

    public boolean checkSkill(Skill skill){
        //checking player.skills with wanted skill
        return false;
    }

    public void showCookingItems(){}

    public void showArtisanItems(){}


}
