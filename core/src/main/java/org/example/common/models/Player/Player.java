package org.example.common.models.Player;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.example.common.models.enums.Npcs;
import org.example.common.models.App;
import org.example.common.models.CollisionRect;
import org.example.common.models.Items.CookingItem;
import org.example.common.models.Items.CraftingItem;
import org.example.common.models.Items.Item;
import org.example.common.models.Items.Tool;
import org.example.common.models.MapDetails.Farm;
import org.example.common.models.MapDetails.GameMap;
import org.example.common.models.MapDetails.Village;
import org.example.common.models.Market;
import org.example.common.models.common.Date;
import org.example.common.models.common.Location;
import org.example.common.models.entities.Friendship;
import org.example.common.models.entities.NPC;
import org.example.common.models.entities.NPCFriendship;
import org.example.common.models.entities.User;
import org.example.common.models.enums.PlayerEnums.Skills;
import org.example.common.models.enums.Types.TileType;
import org.example.common.models.enums.Types.ToolFunctionality;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player {
    private List<Skill> skills;
    private List<CraftingItem> craftingItems;
    private List<CookingItem> cookingItems;
    private Backpack backpack;
    private Map<Player, Friendship> friendships;
    private User user;
    private int energy;
    private boolean energyUnlimited;
    private boolean hasCollapsed;
    private Location location;
    private Farm currentFarm;
    private Village currentVillage;
    private boolean isInVillage;
    private int money;
    private Player spouse;
    private boolean isMarried;
    private Tool currentTool;
    private Date rejectDate;
    private boolean energySet = true;
    private int energyUsedInTurn = 0;
    private String playerColor;
    private Texture textureSheet;

    //graphic ui
    private float posX = 57 * 60;
    private float posY = 69 * 60;
    private float speed;
    private CollisionRect rect;
    private String spriteFileLocation = "content/PlayerSprites/Abigail.png";
    private Sprite playerSprite;

    public Player() {
    }

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
        this.isInVillage = false;

        // Initialize basic tools
        backpack.add(new Tool("Basic Hoe", 0, "content/Tools/Hoe/Hoe.png", "A basic hoe for tilling soil.",
            Tool.ToolType.HOE, Tool.ToolMaterial.BASIC, 5, Skills.FARMING, ToolFunctionality.HOE), 1);
        backpack.add(new Tool("Basic Pickaxe", 0, "content/Tools/Pickaxe/Pickaxe.png", "A basic pickaxe for breaking rocks and mining ores.",
            Tool.ToolType.PICKAXE, Tool.ToolMaterial.BASIC, 5, Skills.MINING, ToolFunctionality.PICKAXE), 1);
        backpack.add(new Tool("Basic Axe", 0, "content/Tools/Axe/Axe.png", "A basic axe for cutting down trees and breaking branches.",
            Tool.ToolType.AXE, Tool.ToolMaterial.BASIC, 5, Skills.FORAGING, ToolFunctionality.AXE), 1);
        backpack.add(new Tool("Basic Watering Can", 0, "content/Tools/Watering_Can/Watering_Can.png", "A basic watering can for watering crops.",
            Tool.ToolType.WATERING_CAN, Tool.ToolMaterial.BASIC, 5, Skills.FARMING, ToolFunctionality.WATERING_CAN), 1);
        backpack.add(new Tool("Scythe", 0, "content/Tools/Scythe.png", "A tool for harvesting crops and cutting grass.",
            Tool.ToolType.SCYTHE, Tool.ToolMaterial.BASIC, 2, null, null), 1);
        backpack.add(new Tool("Initial Trash Can", 0, "content/Tools/Trash_Can_Copper.png", "A basic trash can for disposing of items.",
            Tool.ToolType.TRASH_CAN, Tool.ToolMaterial.BASIC, 0, null, null), 1);
        this.spouse = null;

        this.isMarried = false;
        rejectDate = null;

        energyUsedInTurn = 0;
        equipTool("Basic Hoe");


        //graphic ui
        this.speed = 5;
        rect = new CollisionRect(25 * 120, 25 * 120, getPlayerSprite().getWidth(), getPlayerSprite().getHeight());


        // TODO: delete
        this.money = 10000000;
    }

    public String getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(String color) {
        playerColor = color;
    }

    public boolean getIsInVillage() {
        return isInVillage;
    }

    public void setIsInVillage(boolean isInVillage) {
        this.isInVillage = isInVillage;
    }

    public Farm getCurrentFarm() {
        return currentFarm;
    }

    public void setCurrentFarm(Farm currentFarm) {
        this.currentFarm = currentFarm;
         this.textureSheet = switch (currentFarm.getFarmIndex()) {
            case 0 -> new Texture("sprites/Alex.png");
            case 1 -> new Texture("sprites/Birdie.png");
            case 2 -> new Texture("sprites/Gus.png");
            case 3 -> new Texture("sprites/Leah.png");
            default -> throw new IllegalArgumentException("Invalid farm index: " + currentFarm.getFarmIndex());
        };
    }

    public Texture getTextureSheet() {
        return textureSheet;
    }

    public void setCurrentVillage(Village currentVillage) {
        this.currentVillage = currentVillage;
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


    public void doMission() {
        //checking around for NPC's , and doing missions.
    }

    public boolean giftNPC(NPC npc, Item item) {
        if (item instanceof Tool) {
            return false;
        }

        Date currentDate = App.getGame().getDate();

        NPCFriendship friendship = npc.getFriendship(this);

        String response = friendship.giveGift(item, currentDate);

        backpack.remove(item, 1);

        System.out.println(npc.getName() + ": " + response);

        return true;
    }


    public String meetNPC(NPC npc) {
        if (!isNearby(npc)) {
            return "You are too far away from " + npc.getName() + " to talk.";
        }
        Date currentDate = App.getGame().getDate();

        NPCFriendship friendship = npc.getFriendship(this);

        String response = friendship.talk(currentDate);

        return response;
    }

    private boolean isNearby(NPC npc) {
        // Check if the NPC is within a certain distance from the player
        int distance = Math.abs(npc.getLocation().getX() - this.location.getX()) +
            Math.abs(npc.getLocation().getY() - this.location.getY());
        return distance <= 1;
    }

    public Map<String, String> getNPCFriendships() {
        Map<String, String> friendships = new HashMap<>();

        for (Npcs npcEnum : Npcs.values()) {
            // Create an NPC instance from the enum
            NPC npc = createNPCFromEnum(npcEnum);

            // Get the friendship with this NPC
            NPCFriendship friendship = npc.getFriendship(this);
            int level = friendship.getLevel();
            int points = friendship.getPoints();
            friendships.put(npc.getName(), "Level: " + level + ", Points: " + points);
        }

        return friendships;
    }

    public NPC createNPCFromEnum(Npcs npcEnum) {
        HashMap<Integer, HashMap<Item, Integer>> missions = new HashMap<>();
        NPC npc = new NPC(npcEnum.getCharacteristic(), npcEnum.getName(), npcEnum.getJob(), missions);

        // Add favorite items from the enum
        for (String favoriteItemName : npcEnum.getFavoriteItems()) {
            Item item = App.getItem(favoriteItemName);
            if (item != null) {
                npc.addFavoriteItem(item);
            }
        }

        npc.setLocation(npcEnum.getLocation());
        npc.setDescription(npcEnum.getDescription());

        return npc;
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

//    public void move(int x, int y) {
//        //checking the Tile around.
//        //TODO: چک رو اضافه میکنم که چک کنه و اضافه کنی بهش (taha)
//        //TODO: اضافه کردم تابع چک رو اضافه کن به تابع
//        TileType tile = TileType.GRASS;
//        //etc
//        if (tile == TileType.WATER) {
//            //implementing func.
//        }
//        //TODO: باید مشخص کنیم که تایل آب رو واتر بذاریم یا لیک
////        int energyNeeded = GameMap.calculateEnergyNeeded(this.location, new Location(x, y, TileType.GRASS));
////        Location furthestCanGo = GameMap.findFurthestCanGo(this.location, new Location(x, y, TileType.GRASS));
//        int energyNeeded = 10;
//        if (10 > energy) {
//            this.hasCollapsed = true;
//            this.energy = 0;

    /// /            this.location = furthestCanGo;
//        } else {
//            // Update the player's location
//            this.location = new Location(x, y, TileType.GRASS);
//            //TODO: توی لوکیشن جدید باید تایلش رو گرس بدیم؟
//
//            // Consume energy
//            if (!energyUnlimited) {
//                this.energy -= energyNeeded;
//                this.energyUsedInTurn += energyNeeded;
//            }
//        }
//    }
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

    public int getEnergyUsedInTurn() {
        return energyUsedInTurn;
    }

    public void resetEnergyUsedInTurn() {
        this.energyUsedInTurn = 0;
    }

    public boolean canUseEnergy(int amount) {
        return energyUnlimited || (energyUsedInTurn + amount <= 50);
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

    public void setLocation(Location location) {
        this.location = location;
    }

    public float getPosX(){
        return posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
        location.setxAxis((int) (posX/60));
    }

    public float getPosY(){
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
        location.setyAxis((int) (posY/60));
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

    public boolean upgradeTool(String toolName, Market market) {
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

        switch (tool.getMaterial()) {
            case BASIC -> {
                int cost = 1_000;
                Item cooper;
                if (!toolName.equalsIgnoreCase("Trash Can")) {
                    cooper = new Item("Cooper Tool", 5_000 , "");
                    cost = cost * 2;
                } else {
                    cooper = new Item("Copper Trash Can", 1_000 , "");
                }

                if (!market.checkItem(this, cooper, 1)) {
                    return false;
                }

                if (getMoney() < cost) {
                    return false;
                }
                if (getBackpack().getItem("Cooper Bar") == null) {
                    return false;
                } else {
                    Item item1 = getBackpack().getItem("Cooper Bar");
                    if (getBackpack().getInventory().get(item1) < 5) {
                        return false;
                    }
                }
                decreaseMoney(getMoney() - cost);
                getBackpack().remove(tool, 5);
            }
            case COPPER -> {
                Item iron;
                int cost = 2_500;
                if (!toolName.equalsIgnoreCase("Trash Can")) {
                    //TODO : adding correct image file path
                    iron = new Item("Iron Tool", 5_000 , "");
                    cost = cost * 2;
                } else {
                    //TODO : adding correct image file path
                    iron = new Item("Iron Trash Can", 2_500 , "");
                }

                if (!market.checkItem(this, iron, 1)) {
                    return false;
                }

                if (getMoney() < cost) {
                    return false;
                }
                if (getBackpack().getItem("Iron") == null) {
                    return false;
                } else {
                    Item item1 = getBackpack().getItem("Iron");
                    if (getBackpack().getInventory().get(item1) < 5) {
                        return false;
                    }
                }
                decreaseMoney(getMoney() - cost);
                getBackpack().remove(tool, 5);
            }
            case IRON -> {
                Item gold;
                int cost = 5_000;
                if (!toolName.equalsIgnoreCase("Trash Can")) {
                    gold = new Item("Gold Tool", 10_000 , "");
                    cost = cost * 2;
                } else {
                    gold = new Item("Gold Trash Can", 5_000 , "");
                }

                if (!market.checkItem(this, gold, 1)) {
                    return false;
                }

                if (getMoney() < cost) {
                    return false;
                }
                if (getBackpack().getItem("Gold Bar") == null) {
                    return false;
                } else {
                    Item item1 = getBackpack().getItem("Gold Bar");
                    if (getBackpack().getInventory().get(item1) < 5) {
                        return false;
                    }
                }
                decreaseMoney(getMoney() - cost);
                getBackpack().remove(tool, 5);
            }
            case GOLD -> {
                Item iridium;
                int cost = 12_500;
                if (!toolName.equalsIgnoreCase("Trash Can")) {
                    //TODO : adding correct image file path
                    iridium = new Item("Iridium Tool", 25_000 , "");
                    cost = cost * 2;
                } else {
                    //TODO : adding correct image file path
                    iridium = new Item("Iridium Trash Can", 12_500 , "");
                }

                if (!market.checkItem(this, iridium, 1)) {
                    return false;
                }

                if (getMoney() < cost) {
                    return false;
                }
                if (getBackpack().getItem("Iridium Bar") == null) {
                    return false;
                } else {
                    Item item1 = getBackpack().getItem("Iridium Bar");
                    if (getBackpack().getInventory().get(item1) < 5) {
                        return false;
                    }
                }
                decreaseMoney(getMoney() - cost);
                getBackpack().remove(tool, 5);
            }
            case IRIDIUM -> {
                return false;
            }
        }

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
        return useTool(direction, null);
    }

    public boolean useTool(String direction, GameMap gameMap) {
        if (currentTool == null) {
            return false;
        }

        int skillLevel = getSkillLevel(currentTool.getAssociatedSkill());
        int energyConsumption = currentTool.getEnergyConsumption(skillLevel);

        // Check if the player has enough energy
        if (!energyUnlimited && energy < energyConsumption) {
            return false;
        }

        // Check if the player has used too much energy this turn
        if (!canUseEnergy(energyConsumption)) {
            return false;
        }

        // Use the tool
        boolean success;
        if (gameMap != null) {
            success = currentTool.use(direction, gameMap, this);
        } else {
            success = currentTool.use(direction);
        }

        if (success && !energyUnlimited) {
            energy -= energyConsumption;
            energyUsedInTurn += energyConsumption;
        }

        return success;
    }

    public int getSkillLevel(Skills skill) {
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
        if (isMarried) {
            this.money += amount / 2;
            this.spouse.increaseMoneyBySpouse(amount / 2);
            return;
        }
        this.money += amount;
    }

    private void increaseMoneyBySpouse(int amount) {
        this.money += amount;
    }

    public void decreaseMoney(int amount) {
        if (isMarried) {
            this.money -= amount / 2;
            this.spouse.decreaseMoneyBySpouse(amount / 2);
            return;
        }
        this.money -= amount;
    }

    private void decreaseMoneyBySpouse(int amount) {
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

    public void setRejectDate() {
        this.rejectDate = App.getGame().getDate();
    }

    public void updateRejectDate() {
        if (rejectDate != null) {
            long daysPassed = App.getGame().getDate().getDaysPassed(rejectDate);
            if (daysPassed >= 7) {
                rejectDate = null;
            }
        }
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public boolean craftingExists(String name) {
        return craftingItems.stream().anyMatch(craftingItem -> craftingItem.getName().equals(name));
    }

    public boolean checkTeleportToVillage() {
        int x = getLocation().getX();
        int y = getLocation().getY();
        Farm farm = getCurrentFarm();

        switch (farm.getFarmIndex()) {
            case 0, 1:
                return x >= Farm.width - 3;
            case 2, 3:
                return x <= 2;
            default:
                return false;
        }
    }

    private void teleportToVillage() {
        // This method is deprecated - use walking instead
        System.out.println("Teleportation is disabled. Please walk to the village.");
    }

    // DEPRECATED: Teleportation removed in favor of walking
    public boolean checkTeleportToFarm() {
        // This method is deprecated - use walking instead
        System.out.println("Teleportation is disabled. Please walk to farms.");
        return false;
    }

    // DEPRECATED: Teleportation removed in favor of walking
    private void teleportToFarm(int farmIndex) {
        // This method is deprecated - use walking instead
        System.out.println("Teleportation is disabled. Please walk to farms.");
    }

    /**
     * Check if player can walk to village from current farm
     */
    public boolean canWalkToVillage() {
        if (isInVillage) return false;

        Farm farm = getCurrentFarm();
        if (farm == null) return false;

        // Check if player is near farm exit path
        Location playerLoc = getLocation();
        int farmX = playerLoc.getX();
        int farmY = playerLoc.getY();

        // Farm exit paths are at the edges closest to village
        switch (farm.getFarmIndex()) {
            case 0: // Farm index 0 - exit at right edge
                return farmX >= Farm.width - 3; // Within 3 tiles of right edge
            case 1: // Farm index 1 - exit at right edge
                return farmX >= Farm.width - 3; // Within 3 tiles of right edge
            case 2: // Farm index 2 - exit at left edge
                return farmX <= 2; // Within 3 tiles of left edge
            case 3: // Farm index 3 - exit at left edge
                return farmX <= 2; // Within 3 tiles of left edge
            default:
                return false;
        }
    }

    /**
     * Walk to village from current farm
     */
    public boolean walkToVillage() {
        if (!canWalkToVillage()) {
            System.out.println("You need to be near the farm exit to walk to the village.");
            return false;
        }

        Farm farm = getCurrentFarm();
        if (farm == null) return false;

        // Calculate village entrance position based on farm
        int villageX, villageY;
        switch (farm.getFarmIndex()) {
            case 0: // Farm index 0 - enter at left edge of village (from right edge of farm)
                villageX = GameMap.VILLAGE_X + 5; // Left edge of village
                villageY = GameMap.VILLAGE_Y + 5; // Near top of village
                break;
            case 1: // Farm index 1 - enter at left edge of village (from right edge of farm)
                villageX = GameMap.VILLAGE_X + 5; // Left edge of village
                villageY = GameMap.VILLAGE_Y + Village.height - 5; // Near bottom of village
                break;
            case 2: // Farm index 2 - enter at right edge of village (from left edge of farm)
                villageX = GameMap.VILLAGE_X + Village.width - 5; // Right edge of village
                villageY = GameMap.VILLAGE_Y + 5; // Near top of village
                break;
            case 3: // Farm index 3 - enter at right edge of village (from left edge of farm)
                villageX = GameMap.VILLAGE_X + Village.width - 5; // Right edge of village
                villageY = GameMap.VILLAGE_Y + Village.height - 5; // Near bottom of village
                break;
            default:
                return false;
        }

        // Create village location and set player position
        Location villageLocation = new Location(villageX, villageY, TileType.VILLAGE);
        setLocation(villageLocation);
        setIsInVillage(true);

        System.out.println("You have walked to the village!");
        return true;
    }

    public boolean canWalkToFarm(int farmIndex) {
        if (!isInVillage) return false;

        Location playerLoc = getLocation();
        int villageX = playerLoc.getX();
        int villageY = playerLoc.getY();

        // Check if player is near village exit to the specific farm
        switch (farmIndex) {
            case 0: // Farm index 0 - exit from left edge of village
                return villageX <= GameMap.VILLAGE_X + 5 &&
                       villageY <= GameMap.VILLAGE_Y + 10;
            case 1: // Farm index 1 - exit from left edge of village
                return villageX <= GameMap.VILLAGE_X + 5 &&
                       villageY >= GameMap.VILLAGE_Y + Village.height - 10;
            case 2: // Farm index 2 - exit from right edge of village
                return villageX >= GameMap.VILLAGE_X + Village.width - 5 &&
                       villageY <= GameMap.VILLAGE_Y + 10;
            case 3: // Farm index 3 - exit from right edge of village
                return villageX >= GameMap.VILLAGE_X + Village.width - 5 &&
                       villageY >= GameMap.VILLAGE_Y + Village.height - 10;
            default:
                return false;
        }
    }

    /**
     * Walk to farm from village
     */
    public boolean walkToFarm(int farmIndex) {
        if (!canWalkToFarm(farmIndex)) {
            System.out.println("You need to be near the village exit to walk to the farm.");
            return false;
        }

        Farm farm = App.getGame().getGameMap().getFarmByIndex(farmIndex);
        if (farm == null) return false;

        // Calculate farm entrance position
        int farmX, farmY;
        switch (farmIndex) {
            case 0: // Farm index 0 - enter at right edge of farm
                farmX = Farm.width - 5; // Right edge of farm (closest to village)
                farmY = 5; // Near top of farm
                break;
            case 1: // Farm index 1 - enter at right edge of farm
                farmX = Farm.width - 5; // Right edge of farm (closest to village)
                farmY = Farm.height - 5; // Near bottom of farm
                break;
            case 2: // Farm index 2 - enter at left edge of farm
                farmX = 5; // Left edge of farm (closest to village)
                farmY = 5; // Near top of farm
                break;
            case 3: // Farm index 3 - enter at left edge of farm
                farmX = 5; // Left edge of farm (closest to village)
                farmY = Farm.height - 5; // Near bottom of farm
                break;
            default:
                return false;
        }

        // Create farm location and set player position
        Location farmLocation = new Location(farmX, farmY, TileType.Dirt);
        setLocation(farmLocation);
        setIsInVillage(false);
        setCurrentFarm(farm);

        System.out.println("You have walked to Farm " + farmIndex + "!");
        return true;
    }

    public String getSpriteFileLocation() {
        return spriteFileLocation;
    }

    public void setSpriteFileLocation(String spriteFileLocation) {
        this.spriteFileLocation = spriteFileLocation;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void updatePosition() {
        getPlayerSprite().setPosition(posX, posY);
        rect.move(posX, posY);
    }

    public Sprite getPlayerSprite() {
        if(playerSprite == null) {
            playerSprite = new Sprite(new Texture(spriteFileLocation));
            return playerSprite;
        }
        return playerSprite;
    }
}
