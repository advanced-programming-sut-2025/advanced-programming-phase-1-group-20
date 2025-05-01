package org.example.models.Player;

import org.example.models.Items.CookingItem;
import org.example.models.Items.CraftingItem;
import org.example.models.Items.Item;
import org.example.models.MapDetails.GameMap;
import org.example.models.common.Location;
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
    private boolean energyUnlimited;
    private boolean hasCollapsed;
    private Location location;
    // items player has
    private List<CraftingItem> craftingItems;
    private List<CookingItem> cookingItems;
    private Backpack backpack;


    private boolean energySet = true;

    public Player(User user) {
        this.user = user;
        skills = new ArrayList<Skill>();
        //adding skills:
//        Skills.HARVESTING.addSkill();


        //initializing crafting items
        craftingItems = new ArrayList<CraftingItem>();
        cookingItems = new ArrayList<CookingItem>();
        backpack = new Backpack();
        this.energy = 200;
        this.hasCollapsed = false;
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
        //TODO: چک رو اضافه میکنم که چک کنه و اضافه کنی بهش
        TileType tile = TileType.GRASS;
        //etc
        if (tile == TileType.WATER) {
            //implementing func.
        }
        int energyNeeded = GameMap.calculateEnergyNeeded(this.location, new Location(x, y, TileType.GRASS));
        Location furthestCanGo = GameMap.findFurthestCanGo(this.location, new Location(x, y, TileType.GRASS));
        if (energyNeeded > energy) {
            this.hasCollapsed = true;
            this.energy = 0;
            this.location = furthestCanGo;
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


    public Backpack getBackpack() {
        return backpack;
    }

    public void addItem(Item item) {
        backpack.add(item);
    }

    public void increaseEnergy(int amount) {
        this.energy += amount;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public void decreaseEnergy(int amount) {
        this.energy -= amount;
    }

    public void setEnergyUnlimited() {
        this.energyUnlimited = true;
    }

    public boolean isEnergyUnlimited() {
        return energyUnlimited;
    }

    public User getUser() {
        return user;
    }
}
