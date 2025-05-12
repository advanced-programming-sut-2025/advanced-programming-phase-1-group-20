package org.example.models.entities;

import org.example.models.Items.Item;
import org.example.models.Player.Player;
import org.example.models.common.Date;
import org.example.models.enums.Npcs;

import java.util.HashMap;
import java.util.Map;


public class Quest {
    private final int id;
    private final String title;
    private final String description;
    private final Npcs npc;
    private final Map<Item, Integer> requirements;
    private final int goldReward;
    private final Item itemReward;
    private final int itemRewardQuantity;
    private final int requiredFriendshipLevel;
    private final int requiredDaysPassed;
    private boolean isCompleted;
    private boolean isActive;
    private Date activationDate;


    public Quest(int id, String title, String description, Npcs npc, Map<Item, Integer> requirements,
                 int goldReward, int requiredFriendshipLevel, int requiredDaysPassed) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.npc = npc;
        this.requirements = requirements;
        this.goldReward = goldReward;
        this.itemReward = null;
        this.itemRewardQuantity = 0;
        this.requiredFriendshipLevel = requiredFriendshipLevel;
        this.requiredDaysPassed = requiredDaysPassed;
        this.isCompleted = false;
        this.isActive = requiredFriendshipLevel == 0 && requiredDaysPassed == 0; // Active from start if no requirements
    }

    /**
     * Constructor for a quest with item reward.
     */
    public Quest(int id, String title, String description, Npcs npc, Map<Item, Integer> requirements,
                 Item itemReward, int itemRewardQuantity, int requiredFriendshipLevel, int requiredDaysPassed) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.npc = npc;
        this.requirements = requirements;
        this.goldReward = 0;
        this.itemReward = itemReward;
        this.itemRewardQuantity = itemRewardQuantity;
        this.requiredFriendshipLevel = requiredFriendshipLevel;
        this.requiredDaysPassed = requiredDaysPassed;
        this.isCompleted = false;
        this.isActive = requiredFriendshipLevel == 0 && requiredDaysPassed == 0; // Active from start if no requirements
    }

    /**
     * Constructor for a quest with both gold and item rewards.
     */
    public Quest(int id, String title, String description, Npcs npc, Map<Item, Integer> requirements,
                 int goldReward, Item itemReward, int itemRewardQuantity,
                 int requiredFriendshipLevel, int requiredDaysPassed) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.npc = npc;
        this.requirements = requirements;
        this.goldReward = goldReward;
        this.itemReward = itemReward;
        this.itemRewardQuantity = itemRewardQuantity;
        this.requiredFriendshipLevel = requiredFriendshipLevel;
        this.requiredDaysPassed = requiredDaysPassed;
        this.isCompleted = false;
        this.isActive = requiredFriendshipLevel == 0 && requiredDaysPassed == 0; // Active from start if no requirements
    }

    /**
     * Creates a requirement map from a single item and quantity.
     */
    public static Map<Item, Integer> createRequirement(Item item, int quantity) {
        Map<Item, Integer> requirements = new HashMap<>();
        requirements.put(item, quantity);
        return requirements;
    }

    /**
     * Checks if the player has the required items for this quest.
     */
    public boolean hasRequiredItems(Player player) {
        for (Map.Entry<Item, Integer> requirement : requirements.entrySet()) {
            Item requiredItem = requirement.getKey();
            int requiredQuantity = requirement.getValue();

            int playerQuantity = 0;
            for (Map.Entry<Item, Integer> playerItem : player.getBackpack().getInventory().entrySet()) {
                if (playerItem.getKey().getName().equalsIgnoreCase(requiredItem.getName())) {
                    playerQuantity = playerItem.getValue();
                    break;
                }
            }

            if (playerQuantity < requiredQuantity) {
                return false;
            }
        }
        return true;
    }

    /**
     * Completes the quest, removing required items from player's inventory and giving rewards.
     */
    public boolean complete(Player player) {
        if (!isActive || isCompleted || !hasRequiredItems(player)) {
            return false;
        }

        // Remove required items
        for (Map.Entry<Item, Integer> requirement : requirements.entrySet()) {
            player.getBackpack().remove(requirement.getKey(), requirement.getValue());
        }

        // Get friendship level from player's NPC friendships
        Map<String, String> friendships = player.getNPCFriendships();
        String friendshipInfo = friendships.get(npc.getName());
        int friendshipLevel = 0;
        if (friendshipInfo != null && friendshipInfo.startsWith("Level: ")) {
            try {
                friendshipLevel = Integer.parseInt(friendshipInfo.substring(7, 8));
            } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                // Default to level 0 if parsing fails
            }
        }

        int goldRewardAmount = goldReward;
        int itemRewardAmount = itemRewardQuantity;

        // Double rewards if friendship level is 2 or higher
        if (friendshipLevel >= 2) {
            goldRewardAmount *= 2;
            itemRewardAmount *= 2;
        }

        if (goldRewardAmount > 0) {
            player.increaseMoney(goldRewardAmount);
        }

        if (itemReward != null && itemRewardAmount > 0) {
            player.getBackpack().add(itemReward, itemRewardAmount);
        }

        isCompleted = true;
        return true;
    }

    /**
     * Checks if the quest can be activated based on friendship level and days passed.
     */
    public boolean canActivate(Player player, Date currentDate) {
        if (isActive || isCompleted) {
            return false;
        }

        // Get friendship level from player's NPC friendships
        Map<String, String> friendships = player.getNPCFriendships();
        String friendshipInfo = friendships.get(npc.getName());
        int friendshipLevel = 0;
        if (friendshipInfo != null && friendshipInfo.startsWith("Level: ")) {
            try {
                friendshipLevel = Integer.parseInt(friendshipInfo.substring(7, 8));
            } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                // Default to level 0 if parsing fails
            }
        }

        // Check friendship level requirement
        if (friendshipLevel < requiredFriendshipLevel) {
            return false;
        }

        // Check days passed requirement
        if (requiredDaysPassed > 0 && activationDate != null) {
            // Cast long to int (safe for game days which won't exceed int range)
            int daysPassed = (int) currentDate.getDaysPassed(activationDate);
            return daysPassed >= requiredDaysPassed;
        }

        return true;
    }

    /**
     * Activates the quest if it meets the requirements.
     */
    public boolean activate(Player player, Date currentDate) {
        if (canActivate(player, currentDate)) {
            isActive = true;
            return true;
        }
        return false;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Npcs getNpc() {
        return npc;
    }

    public Map<Item, Integer> getRequirements() {
        return requirements;
    }

    public int getGoldReward() {
        return goldReward;
    }

    public Item getItemReward() {
        return itemReward;
    }

    public int getItemRewardQuantity() {
        return itemRewardQuantity;
    }

    public int getRequiredFriendshipLevel() {
        return requiredFriendshipLevel;
    }

    public int getRequiredDaysPassed() {
        return requiredDaysPassed;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public boolean isActive() {
        return isActive;
    }

    public Date getActivationDate() {
        return activationDate;
    }

    /**
     * Sets the activation date for time-based quests.
     */
    public void setActivationDate(Date date) {
        this.activationDate = date;
    }
}
