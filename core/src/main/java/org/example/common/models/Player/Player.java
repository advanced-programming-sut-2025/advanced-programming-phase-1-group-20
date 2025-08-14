package org.example.common.models.Player;
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
import org.example.common.models.entities.FriendShip;
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
import java.util.Objects;

public class Player {
    private List<Skill> skills;
    private List<CraftingItem> craftingItems;
    private List<CraftingItem> placedCraftingItems;
    private List<CookingItem> cookingItems;
    private Backpack backpack;
    private Map<Player, FriendShip> friendships;
    private User user;
    private int energy;
    private boolean energyUnlimited = true;
    private boolean hasCollapsed;
    private Location location;
    private Farm currentFarm;
    private Village currentVillage;
    private boolean isInVillage;
    private int money;
    private Player spouse;
    private boolean isMarried;
    private Tool currentTool;
    private Item currentItem;
    private Date rejectDate;
    private boolean energySet = true;
    private int energyUsedInTurn = 0;
    private String playerColor;

    //graphic ui
    private float posX = 57 * 60;
    private float posY = 69 * 60;
    private float speed;
    private CollisionRect rect;

    // Animation state for multiplayer rendering
    private String currentAnimation = "down";
    private boolean isMoving = false;
    private float animationTimer = 0f;

    public Player() {
    }

    public Player(User user) {
        System.out.println("DEBUG: Player constructor called for user: " + (user != null ? user.getUsername() : "null"));
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
        this.energy = 2000;
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
            Tool.ToolType.SCYTHE, Tool.ToolMaterial.BASIC, 2, Skills.FARMING, ToolFunctionality.SCYTHE), 1);
        backpack.add(new Tool("Basic Trash Can", 0, "content/Tools/Trash_Can_Copper.png", "A basic trash can for disposing of items.",
            Tool.ToolType.TRASH_CAN, Tool.ToolMaterial.BASIC, 0, null, ToolFunctionality.TRASH_CAN), 1);

        // the other tools should be added later, when the player has more money or skills
//        backpack.add(new Tool("Bamboo Pole", 0, "content/Tools/Fishing_Pole/Bamboo_Pole.png", "A basic fishing rod for catching fish.",
//            Tool.ToolType.FISHING_ROD, Tool.ToolMaterial.BASIC, 8, Skills.FISHING, ToolFunctionality.FISHING_ROD), 1);
//        backpack.add(new Tool("Milk Pail", 0, "content/Tools/Milk_Pail.png", "A pail for milking cows.",
//            Tool.ToolType.MILK_PAIL, Tool.ToolMaterial.BASIC, 4, Skills.FARMING, ToolFunctionality.MILK_PAIL), 1);
//        backpack.add(new Tool("Shears", 0, "content/Tools/shears/Shears.png", "Shears for collecting wool from sheep.",
//            Tool.ToolType.SHEARS, Tool.ToolMaterial.BASIC, 4, Skills.FARMING, ToolFunctionality.SHEARS), 1);
        this.spouse = null;

        this.isMarried = false;
        rejectDate = null;

        energyUsedInTurn = 0;
        equipTool("Basic Hoe");


        //graphic ui
        this.speed = 8;

        // Check if we're in a server environment (Gdx.files is null on server)
        boolean isServerEnvironment = false;
        try {
            if (com.badlogic.gdx.Gdx.files == null) {
                isServerEnvironment = true;
            }
        } catch (Exception e) {
            isServerEnvironment = true;
        }

        // Create collision rect using default dimensions
        rect = new CollisionRect(25 * 120, 25 * 120, 64, 64); // Default sprite dimensions


        // TODO: testing!
        this.money = 10000000;

        // Initialize location based on default position
        this.location = new Location((int) (posX / 60), (int) (posY / 60), TileType.Dirt);

        placedCraftingItems = new ArrayList<>();
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
    }

    public void setCurrentVillage(Village currentVillage) {
        this.currentVillage = currentVillage;
    }

    public FriendShip getFriendship(Player player) {
        if (!friendships.containsKey(player)) {
            FriendShip friendship = new FriendShip(this, player);
            friendships.put(player, friendship);
            if (!player.friendships.containsKey(this)) {
                player.friendships.put(this, friendship);
            }
        }
        return friendships.get(player);
    }

    public Map<Player, FriendShip> getAllFriendships() {
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
        for (FriendShip friendship : friendships.values()) {
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
        // Reset collapsed state when energy is restored
        if (hasCollapsed && energy > 0) {
            hasCollapsed = false;
            System.out.println("Player has recovered from collapse!");
        }
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

    public void addEnergyUsedInTurn(int amount) {
        this.energyUsedInTurn += amount;
    }

    public boolean canUseEnergy(int amount) {
        return energyUnlimited || (energy >= amount && energyUsedInTurn + amount <= 50);
    }
    public boolean isOutOfEnergyForTurn() {
        if (energyUnlimited) {
            System.out.println("Player " + getUser().getUsername() + " has unlimited energy");
            return false;
        }

        System.out.println("DEBUG: Checking energy for " + getUser().getUsername() + " - Energy: " + energy + ", Energy used this turn: " + energyUsedInTurn);
        System.out.println("DEBUG: canUseEnergy(1) calculation - energy >= 1: " + (energy >= 1) + ", energyUsedInTurn + 1 <= 50: " + (energyUsedInTurn + 1 <= 50));
        boolean outOfEnergy = !canUseEnergy(1);
        System.out.println("DEBUG: canUseEnergy(1) returned: " + !outOfEnergy);

        if (outOfEnergy) {
            System.out.println("Player " + getUser().getUsername() + " is out of energy for turn. Energy: " + energy + ", Energy used this turn: " + energyUsedInTurn);
        }

        // Check if player can use at least 1 energy unit
        // Most basic actions require at least 1 energy
        return outOfEnergy;
    }

    /**
     * Helper method to check and advance turn if energy is depleted
     */
    private void checkAndAdvanceTurnIfEnergyDepleted() {
        if (App.getGame() != null) {
            App.getGame().checkAndAdvanceTurnIfEnergyDepleted();
        }
    }

    public void setEnergyUnlimited() {
        this.energyUnlimited = true;
    }

    public void setEnergyLimited() {
        this.energyUnlimited = false;
    }

    public boolean isEnergyUnlimited() {
        return energyUnlimited;
    }

    public boolean hasCollapsed() {
        return hasCollapsed;
    }

    public void setCollapsed(boolean collapsed) {
        this.hasCollapsed = collapsed;
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
        if (location != null) {
            location.setxAxis((int) (posX/60));
        } else{
            location = new Location();
            location.setxAxis((int) (posX/60));
        }
        // Update sprite position when position changes
        updatePosition();
    }

    public float getPosY(){
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
        if (location != null) {
            location.setyAxis((int) (posY/60));
        } else {
            location = new Location();
            location.setyAxis((int) (posY/60));
        }
        updatePosition();
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

    public void equipItem(String itemName) {
        Item item = backpack.getItem(itemName);
        if (item == null || !(item instanceof Item)) {
            return;
        }

        setCurrentItem(item);
    }

    public Map<Player, FriendShip> getFriendships() {
        return friendships;
    }


    public void setMoney(int money) {
        this.money = money;
    }

    public Item getCurrentItem() {
        return currentItem;
    }

    public void setCurrentItem(Item currentItem) {
        this.currentItem = currentItem;
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

        // upgrade only active in the blacksmith by default
        boolean inBlacksmith = true;
        if (!inBlacksmith) {
            return false;
        }

        Tool tool = (Tool) item;

        switch (tool.getMaterial()) {
            case BASIC -> {
                int cost = 1_000;
                if (!toolName.toLowerCase().contains("trash can")) {
                    cost = cost * 2;
                }

                if (getMoney() < cost) {
                    return false;
                }

                // Check for Copper Bar (5 required)
                Item copperBar = getBackpack().getItem("Copper Bar");
                if (copperBar == null || getBackpack().getInventory().get(copperBar) < 5) {
                    return false;
                }

                decreaseMoney(cost);
                getBackpack().remove(copperBar, 5);
            }
            case COPPER -> {
                int cost = 2_500;
                if (!toolName.toLowerCase().contains("trash can")) {
                    cost = cost * 2;
                }

                if (getMoney() < cost) {
                    return false;
                }

                // Check for Iron Bar (5 required)
                Item ironBar = getBackpack().getItem("Iron Bar");
                if (ironBar == null || getBackpack().getInventory().get(ironBar) < 5) {
                    return false;
                }

                decreaseMoney(cost);
                getBackpack().remove(ironBar, 5);
            }
            case IRON -> {
                int cost = 5_000;
                if (!toolName.toLowerCase().contains("trash can")) {
                    cost = cost * 2;
                }

                if (getMoney() < cost) {
                    return false;
                }

                // Check for Gold Bar (5 required)
                Item goldBar = getBackpack().getItem("Gold Bar");
                if (goldBar == null || getBackpack().getInventory().get(goldBar) < 5) {
                    return false;
                }

                decreaseMoney(cost);
                getBackpack().remove(goldBar, 5);
            }
            case GOLD -> {
                int cost = 12_500;
                if (!toolName.toLowerCase().contains("trash can")) {
                    cost = cost * 2;
                }

                if (getMoney() < cost) {
                    return false;
                }

                // Check for Iridium Bar (5 required)
                Item iridiumBar = getBackpack().getItem("Iridium Bar");
                if (iridiumBar == null || getBackpack().getInventory().get(iridiumBar) < 5) {
                    return false;
                }

                decreaseMoney(cost);
                getBackpack().remove(iridiumBar, 5);
            }
            case IRIDIUM -> {
                return false; // Already at highest material
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
            // Set collapsed state when player runs out of energy
            setCollapsed(true);
            System.out.println("Player has collapsed due to insufficient energy for tool usage!");
            // Check if player is out of energy and auto-advance turn if needed
            checkAndAdvanceTurnIfEnergyDepleted();
            return false;
        }

        // Check if the player has used too much energy this turn
        if (!canUseEnergy(energyConsumption)) {
            // Set collapsed state when player runs out of energy
            setCollapsed(true);
            System.out.println("Player has collapsed due to insufficient energy for tool usage!");
            // Check if player is out of energy and auto-advance turn if needed
            checkAndAdvanceTurnIfEnergyDepleted();
            return false;
        }

        boolean success;
        if (gameMap != null) {
            System.out.println("================using this=============");
            success = currentTool.use(direction, gameMap, this);
        } else {
            success = currentTool.use(direction);
        }

        if (success && !energyUnlimited) {
            energy -= energyConsumption;
            addEnergyUsedInTurn(energyConsumption);

            // Add skill experience based on tool type
            if (currentTool.getAssociatedSkill() != null) {
                switch (currentTool.getAssociatedSkill()) {
                    case FARMING -> addFarmingExperience();
                    case MINING -> addMiningExperience();
                    case FORAGING -> addForagingExperience();
                    case FISHING -> addFishingExperience();
                }
            }

            // Check if player is out of energy after this action
            checkAndAdvanceTurnIfEnergyDepleted();
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

    // Add skill experience for different activities
    public void addFarmingExperience() {
        // Harvesting crops gives 5 units to farming skill
        for (Skill skill : skills) {
            if (skill.getName().equals("farming")) {
                skill.addUnits(5);
                break;
            }
        }
    }

    public void addMiningExperience() {
        // Breaking rocks/ores gives 10 units to mining skill
        for (Skill skill : skills) {
            if (skill.getName().equals("mining")) {
                skill.addUnits(10);
                break;
            }
        }
    }

    public void addForagingExperience() {
        // Collecting items from nature gives 10 units to foraging skill
        for (Skill skill : skills) {
            if (skill.getName().equals("foraging")) {
                skill.addUnits(10);
                break;
            }
        }
    }

    public void addFishingExperience() {
        // Catching fish gives 5 units to fishing skill
        for (Skill skill : skills) {
            if (skill.getName().equals("fishing")) {
                skill.addUnits(5);
                break;
            }
        }
    }

    // Get skill by name
    public Skill getSkillByName(String skillName) {
        for (Skill skill : skills) {
            if (skill.getName().equals(skillName.toLowerCase())) {
                return skill;
            }
        }
        return null;
    }

    // Get skill by enum
    public Skill getSkill(Skills skillEnum) {
        if (skillEnum == null) {
            return null;
        }
        return getSkillByName(skillEnum.name().toLowerCase());
    }

    // Backpack upgrade methods
    public boolean upgradeBackpack() {
        return backpack.upgradeBackpack();
    }

    public int getBackpackCapacity() {
        return backpack.getCapacity();
    }

    public Backpack.Type getBackpackType() {
        return backpack.getType();
    }

    // Trash can functionality
    public boolean trashItem(String itemName, int amount) {
        Item item = backpack.getItem(itemName);
        if (item == null) {
            return false;
        }

        int currentAmount = backpack.getNumberOfItem(itemName);
        if (currentAmount < amount) {
            return false;
        }

        // Get the current trash can
        Tool trashCan = getCurrentTool();
        if (trashCan == null || trashCan.getType() != Tool.ToolType.TRASH_CAN) {
            return false;
        }

        // Calculate return value based on trash can type
        int itemValue = item.getBaseSellPrice() * amount;
        int returnValue = trashCan.calculateReturnValue(itemValue);

        // Remove the item
        backpack.remove(item, amount);

        // Add money back if the trash can provides returns
        if (returnValue > 0) {
            increaseMoney(returnValue);
        }

        return true;
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

    public boolean cookingExists(String name) {
        return cookingItems.stream().anyMatch(cookingItem -> cookingItem.getName().equals(name));
    }

    public boolean checkTeleportToVillage() {
        int x = getLocation().getX();
        int y = getLocation().getY();
        Farm farm = getCurrentFarm();

        return switch (farm.getFarmIndex()) {
            case 0, 1 -> x >= Farm.width - 1;
            case 2, 3 -> x <= 2;
            default -> false;
        };
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



    public float getSpeed() {
        return speed;
    }

    public void updatePosition() {
        rect.move(posX, posY);
    }

    public void addPlacedCraftingItem(CraftingItem item) {
        this.placedCraftingItems.add(item);
    }

    public void removePlacedCraftingItem(CraftingItem item) {
        this.placedCraftingItems.remove(item);
    }

    public List<CraftingItem> getPlacedCraftingItems() {
        return placedCraftingItems;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(user, player.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }

    // Animation state getters and setters for multiplayer rendering
    public String getCurrentAnimation() {
        return currentAnimation;
    }

    public void setCurrentAnimation(String currentAnimation) {
        this.currentAnimation = currentAnimation;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void setMoving(boolean moving) {
        isMoving = moving;
    }

    public float getAnimationTimer() {
        return animationTimer;
    }

    public void setAnimationTimer(float animationTimer) {
        this.animationTimer = animationTimer;
    }
}
