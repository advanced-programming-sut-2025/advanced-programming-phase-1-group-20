package org.example.models.Player;

import org.example.models.Items.CookingItem;
import org.example.models.Items.CraftingItem;
import org.example.models.Items.Item;
import org.example.models.Items.Tool;
import org.example.models.Items.Tools.*;
import org.example.models.MapDetails.GameMap;
import org.example.models.common.Location;
import org.example.models.entities.Friendship;
import org.example.models.entities.NPC;
import org.example.models.entities.User;
import org.example.models.enums.PlayerEnums.Skills;
import org.example.models.enums.Types.TileType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player {
    private Map<Player, Friendship> friendships;
    private User user;
    private int energy;
    private List<Skill> skills;
    private boolean energyUnlimited;
    private boolean hasCollapsed;
    private Location location;
    private int money;
    private Player spouse;
    private boolean isMarried;
    private List<CraftingItem> craftingItems;
    private List<CookingItem> cookingItems;
    private Backpack backpack;

    private Tool currentTool;

    private boolean energySet = true;

    public Player(User user) {
        this.user = user;
        skills = new ArrayList<Skill>();
        //adding skills:
        skills.add(new Skill(1, "farming", 5));
        skills.add(new Skill(1, "mining", 5));
        skills.add(new Skill(1, "foraging", 5));
        skills.add(new Skill(1, "fishing", 5));

        //initializing crafting items
        craftingItems = new ArrayList<CraftingItem>();
        cookingItems = new ArrayList<CookingItem>();
        backpack = new Backpack();
        this.energy = 200;
        this.hasCollapsed = false;
        this.friendships = new HashMap<>();

        // Initialize basic tools
        backpack.add(new Hoe(), 1);
        backpack.add(new Pickaxe(), 1);
        backpack.add(new Axe(), 1);
        backpack.add(new WateringCan(), 1);
        backpack.add(new Scythe(), 1);
        backpack.add(new TrashCan(), 1);
        this.spouse = null;

        this.isMarried = false;
        equipTool("Basic Hoe");
    }

    public Friendship getFriendship(Player player) {
        if (!friendships.containsKey(player)) {
            Friendship friendship = new Friendship(this, player);
            friendships.put(player, friendship);
            if (!player.friendships.containsKey(this)) {
                player.friendships.put(this, friendship);
            }
        }
        return friendships.get(player);
    }

    public Map<Player, Friendship> getAllFriendships() {
        return friendships;
    }


    public boolean talkTo(Player player, String message) {
        return getFriendship(player).talk(message, this);
    }

    public boolean tradeWith(Player player, boolean success) {
        return getFriendship(player).trade(success);
    }


    public boolean hugMob(Player player) {
        return getFriendship(player).hug(this);
    }

    public boolean giveBouquetTo(Player player) {
        return getFriendship(player).giveBouquet(this);
    }


    public boolean proposeMarriageTo(Player player) {
        return getFriendship(player).proposeMarriage(this);
    }


    public boolean isMarriedTo(Player player) {
        return friendships.containsKey(player) && friendships.get(player).isMarried();
    }

    public void applyDailyDecayToAllFriendships() {
        for (Friendship friendship : friendships.values()) {
            friendship.applyDailyDecay();
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


    public boolean giftNPC(NPC npc, org.example.models.Items.Item item) {
        return false;
    }

    public boolean talkToNPC(NPC npc, String message) {
        return false;
    }

    public boolean hugNPC(NPC npc) {
        return false;
    }

    public boolean giveBouquetToNPC(NPC npc) {
        return false;
    }


    public int getFriendshipXP(NPC npc) {
        // NPCs are not players, so we can't use the friendship system with them
        return 0;
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
        //TODO: چک رو اضافه میکنم که چک کنه و اضافه کنی بهش (taha)
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
        backpack.add(item, 1);
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

    public Location getLocation() {
        return location;
    }

    public boolean equipTool(String toolName) {
        Item item = backpack.getItem(toolName);
        if (item == null || !(item instanceof Tool)) {
            return false;
        }

        if (currentTool != null) {
            currentTool.unequip();
        }

        currentTool = (Tool) item;
        currentTool.equip();
        return true;
    }

    public Tool getCurrentTool() {
        return currentTool;
    }


    public List<Tool> getAvailableTools() {
        List<Tool> tools = new ArrayList<>();
        for (Item item : backpack.getInventory().keySet()) {
            if (item instanceof Tool) {
                tools.add((Tool) item);
            }
        }
        return tools;
    }

    public boolean upgradeTool(String toolName) {
        // Check if the tool is in the backpack
        Item item = backpack.getItem(toolName);
        if (item == null || !(item instanceof Tool)) {
            return false;
        }

        // Check if the player is in a blacksmith
        // TODO: check this (Taha)
        boolean inBlacksmith = true;
        if (!inBlacksmith) {
            return false;
        }


        boolean hasEnoughResources = true;
        if (!hasEnoughResources) {
            return false;
        }

        Tool tool = (Tool) item;
        Tool upgradedTool = tool.upgrade();
        if (upgradedTool == null) {
            return false;
        }

        backpack.remove(tool, 1);

        backpack.add(upgradedTool, 1);

        if (currentTool != null && currentTool.equals(tool)) {
            currentTool = upgradedTool;
            currentTool.equip();
        }

        return true;
    }

    public boolean useTool(String direction) {
        if (currentTool == null) {
            return false;
        }

        int skillLevel = getSkillLevel(currentTool.getAssociatedSkill());
        int energyConsumption = currentTool.getEnergyConsumption(skillLevel);
        if (!energyUnlimited && energy < energyConsumption) {
            return false;
        }

        // Use the tool
        boolean success = currentTool.use(direction);
        if (success && !energyUnlimited) {
            energy -= energyConsumption;
        }

        return success;
    }


    private int getSkillLevel(Skills skill) {
        if (skill == null) {
            return 0;
        }

        for (Skill playerSkill : skills) {
            if (playerSkill.getName().equals(skill.name().toLowerCase())) {
                return playerSkill.getLevel();
            }
        }

        return 0;
    }

    public int getMoney() {
        return money;
    }

    public void increaseMoney(int amount) {
        this.money += amount;
    }

    public void decreaseMoney(int amount) {
        this.money -= amount;
    }

    public boolean isMarried() {
        return isMarried;
    }

    public void marry(Player player) {
        this.isMarried = true;
        this.spouse = player;
    }

    public Player getSpouse() {
        return spouse;
    }
}
