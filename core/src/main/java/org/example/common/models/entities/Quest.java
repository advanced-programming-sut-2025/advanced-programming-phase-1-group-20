package org.example.common.models.entities;

import org.example.common.models.App;
import org.example.common.models.Items.Item;
import org.example.common.models.Player.Player;
import org.example.common.models.common.Date;
import org.example.common.models.enums.Npcs;
import org.example.common.models.MapDetails.Village;

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
    private Player takenBy; // Track which player has taken this quest
    private Date takenDate; // When the quest was taken


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
        this.isActive = false; // Quests start as inactive until taken
        this.takenBy = null;
        this.takenDate = null;
    }


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
        this.isActive = false; // Quests start as inactive until taken
        this.takenBy = null;
        this.takenDate = null;
    }

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
        this.isActive = false; // Quests start as inactive until taken
        this.takenBy = null;
        this.takenDate = null;
    }

    public static Map<Item, Integer> createRequirement(Item item, int quantity) {
        Map<Item, Integer> requirements = new HashMap<>();
        requirements.put(item, quantity);
        return requirements;
    }

    public boolean takeQuest(Player player, Date currentDate) {
        if (takenBy != null) {
            return false;
        }

        // Check if player meets requirements
        if (!canActivate(player, currentDate)) {
            return false;
        }

        // Take the quest
        takenBy = player;
        takenDate = currentDate;
        isActive = true;
        activationDate = currentDate;

        return true;
    }

    public boolean isAvailable() {
        return takenBy == null && !isCompleted;
    }


    public boolean isTakenBy(Player player) {
        return takenBy != null && takenBy.equals(player);
    }

    /**
     * Get the player who has taken this quest
     * @return The player who took the quest, or null if not taken
     */
    public Player getTakenBy() {
        return takenBy;
    }

    /**
     * Get when the quest was taken
     * @return The date when the quest was taken, or null if not taken
     */
    public Date getTakenDate() {
        return takenDate;
    }


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
            // Special handling for friendship level rewards
            if (itemReward.getName().equals("Friendship Level")) {
                // Get the NPC from the village instead of creating a new one
                Game game = App.getGame();
                if (game != null && game.getGameMap() != null && game.getGameMap().getVillage() != null) {
                    Village village = game.getGameMap().getVillage();
                    NPC villageNPC = null;

                    // Find the NPC in the village residents
                    for (NPC resident : village.getResidents()) {
                        if (resident.getName().equals(npc.getName())) {
                            villageNPC = resident;
                            break;
                        }
                    }

                    if (villageNPC != null) {
                        NPCFriendship friendship = villageNPC.getFriendship(player);
                        // Increase points to reach the next level (200 points per level)
                        friendship.increasePoints(200 * itemRewardAmount);
                    }
                }
            } else {
                player.getBackpack().add(itemReward, itemRewardAmount);
            }
        }

        isCompleted = true;
        return true;
    }


    public boolean canActivate(Player player, Date currentDate) {
        if (isCompleted) {
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

    public void setActivationDate(Date date) {
        this.activationDate = date;
    }
}
