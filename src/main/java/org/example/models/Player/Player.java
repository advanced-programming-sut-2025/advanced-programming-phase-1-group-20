package org.example.models.Player;

import org.example.models.Items.CookingItem;
import org.example.models.Items.CraftingItem;
import org.example.models.MapDetails.GameMap;
import org.example.models.entities.Mob;
import org.example.models.entities.NPC;
import org.example.models.entities.User;
import org.example.models.enums.Types.TileType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Player extends Mob {
    private HashMap<Mob, Integer> friendShip;
    private User user;
    private int energy;
    private List<Skill> skills;

    // items player has
    private List<CraftingItem> craftingItems;
    private List<CookingItem> cookingItems;


    private boolean energySet = true;

    public Player(User user) {
        this.user = user;
        skills = new ArrayList<Skill>();
        //adding skills:
//        Skills.HARVESTING.addSkill();


        //initializing crafting items
        craftingItems = new ArrayList<CraftingItem>();
        cookingItems  = new ArrayList<CookingItem>();
    }

    //decreasing energy:
    private void decreaseEnergy() {
        if (energySet) {
        }
    }

    public void cheatEnergy() {
        energySet = false;
    }

    public void fishingRod(GameMap gMap, int x, int y) {
        //checking the Tile around.
        TileType tile = gMap.getTile(x + 1, y);
        //etc
        if (tile == TileType.WATER) {
            //implementing func.
        }
    }

    public void doMission() {
        //checking around for NPC's , and doing missions.
    }

    public void giftNPC(NPC npc) {
        //implementing func. leveling up npc's friendShip for both.
    }

    public void showCraftingItems() {

    }

    public boolean checkSkill(Skill skill) {
        //checking player.skills with wanted skill
        return false;
    }

    public void showCookingItems() {
    }

    public void showArtisanItems() {
    }

    public void move(int x, int y) {
        //checking the Tile around.
        TileType tile = GameMap.getTile(x + 1, y);
        //etc
        if (tile == TileType.WATER) {
            //implementing func.
        }
    }

    public void addCraftingItem(CraftingItem craftingItem) {
        craftingItems.add(craftingItem);
    }

    public List<CraftingItem> getCraftingItems() {
        return craftingItems;
    }

    public void addCookingItem(CookingItem cookingItem) {
        cookingItems.add(cookingItem);
    }

    public List<CookingItem> getCookingItems() {
        return cookingItems;
    }
}
