package org.example.common.models.entities;

import org.example.common.models.App;
import org.example.common.models.Items.Item;
import org.example.common.models.Player.Player;
import org.example.common.models.common.Date;
import org.example.common.models.enums.Types.ItemBuilder;

import java.util.*;

public class CoopQuest {
    private final int id;
    private final String title;
    private final String description;
    private final Map<Item, Integer> requirements;
    private final int goldReward;
    private final Item itemReward;
    private final int itemRewardQuantity;
    private final int maxPlayers;
    private final int timeLimitDays;
    private Date startDate;
    private Date endDate;
    private final List<Player> participants;
    private final Map<Player, Integer> playerProgress; // Track individual progress
    private final Map<Player, Date> joinDates;
    private boolean isCompleted;
    private boolean isActive;
    private int totalProgress; // Overall quest progress

    public CoopQuest(int id, String title, String description, Map<Item, Integer> requirements,
                     int goldReward, Item itemReward, int itemRewardQuantity,
                     int maxPlayers, int timeLimitDays) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.requirements = requirements;
        this.goldReward = goldReward;
        this.itemReward = itemReward;
        this.itemRewardQuantity = itemRewardQuantity;
        this.maxPlayers = maxPlayers;
        this.timeLimitDays = timeLimitDays;
        this.startDate = null; // Will be set when quest becomes active
        this.endDate = null; // Will be calculated when quest starts
        this.participants = new ArrayList<>();
        this.playerProgress = new HashMap<>();
        this.joinDates = new HashMap<>();
        this.isCompleted = false;
        this.isActive = false;
        this.totalProgress = 0;
    }

    public static Map<Item, Integer> createRequirement(Item item, int quantity) {
        Map<Item, Integer> requirements = new HashMap<>();
        requirements.put(item, quantity);
        return requirements;
    }

    public boolean joinQuest(Player player, Date currentDate) {
        // Check if quest is available
        if (!isAvailable()) {
            return false;
        }

        // Check if player can join (less than 3 active quests)
        if (getActiveQuestsCount(player) >= 3) {
            return false;
        }

        // Check if quest is full
        if (participants.size() >= maxPlayers) {
            return false;
        }

        // Check if player is already participating
        if (participants.contains(player)) {
            return false;
        }

        // Join the quest
        participants.add(player);
        playerProgress.put(player, 0);
        joinDates.put(player, currentDate);

        // Only activate the quest when the required number of players have joined
        if (participants.size() >= maxPlayers) {
            activateQuest(currentDate);
        }

        return true;
    }

    private void activateQuest(Date currentDate) {
        this.isActive = true;
        this.startDate = currentDate;

        // Calculate end date based on time limit
        this.endDate = new Date();
        // Copy current date values to end date
        this.endDate.setDay(currentDate.getDay());
        this.endDate.setSeason(currentDate.getSeason().ordinal());
        this.endDate.setYear(currentDate.getYear());

        // Advance days to set end date
        for (int i = 0; i < timeLimitDays; i++) {
            this.endDate.advanceDays(1, null);
        }
    }

    public boolean contributeToQuest(Player player, Map<Item, Integer> contribution) {
        if (!isActive || isCompleted || !participants.contains(player)) {
            return false;
        }

        // Check if quest has expired
        if (isExpired()) {
            return false;
        }

        // Calculate contribution value
        int contributionValue = calculateContributionValue(contribution);
        if (contributionValue <= 0) {
            return false;
        }

        // Update progress
        int currentPlayerProgress = playerProgress.getOrDefault(player, 0);
        playerProgress.put(player, currentPlayerProgress + contributionValue);
        totalProgress += contributionValue;

        // Remove items from player's backpack
        for (Map.Entry<Item, Integer> entry : contribution.entrySet()) {
            player.getBackpack().remove(entry.getKey(), entry.getValue());
        }

        // Check if quest is completed
        if (totalProgress >= getTotalRequiredProgress()) {
            completeQuest();
        }

        return true;
    }

    private int calculateContributionValue(Map<Item, Integer> contribution) {
        int totalValue = 0;
        for (Map.Entry<Item, Integer> entry : contribution.entrySet()) {
            Item item = entry.getKey();
            int quantity = entry.getValue();

            // Check if this item is required for the quest
            Integer requiredQuantity = requirements.get(item);
            if (requiredQuantity != null) {
                // Calculate how much of this requirement is fulfilled
                int fulfilled = Math.min(quantity, requiredQuantity);
                totalValue += fulfilled;
            }
        }
        return totalValue;
    }

    private int getTotalRequiredProgress() {
        int total = 0;
        for (Integer quantity : requirements.values()) {
            total += quantity;
        }
        return total;
    }

    private void completeQuest() {
        this.isCompleted = true;
        this.isActive = false;

        // Distribute rewards to all participants
        for (Player participant : participants) {
            // Give gold reward
            if (goldReward > 0) {
                participant.increaseMoney(goldReward);
            }

            // Give item reward
            if (itemReward != null && itemRewardQuantity > 0) {
                participant.getBackpack().add(itemReward, itemRewardQuantity);
            }
        }

        // Send completion notification
        sendQuestCompletionNotification();
    }

    private void sendQuestCompletionNotification() {
        // Only send notifications in multiplayer mode
        if (App.getGame() == null || !App.getGame().isMultiplayer()) {
            return;
        }

        try {
            // Send message to server
            if (org.example.client.network.NetworkClient.getInstance() != null) {
                org.example.common.models.Message message = new org.example.common.models.Message();
                message.setType(org.example.common.models.Message.Type.COOP_QUEST_COMPLETE);
                message.putInBody("questTitle", this.title);
                message.putInBody("questId", this.id);
                message.putInBody("timestamp", System.currentTimeMillis());
                
                org.example.client.network.NetworkClient.getInstance().sendMessage(message);
            }
        } catch (Exception e) {
            System.err.println("Failed to send co-op quest completion notification: " + e.getMessage());
        }
    }

    public boolean isAvailable() {
        return !isActive && !isCompleted && participants.size() < maxPlayers;
    }

    /**
     * Check if the quest is ready to start (all required players have joined)
     * @return true if the quest has the required number of participants but hasn't started yet
     */
    public boolean isReadyToStart() {
        return !isActive && !isCompleted && participants.size() >= maxPlayers;
    }

    /**
     * Get the quest status for display purposes
     * @return String describing the current quest status
     */
    public String getStatus() {
        if (isCompleted()) {
            return "Completed";
        } else if (isActive()) {
            return "Active";
        } else if (isReadyToStart()) {
            return "Ready to Start";
        } else if (participants.size() > 0) {
            return "Waiting for Players (" + participants.size() + "/" + maxPlayers + ")";
        } else {
            return "Available";
        }
    }

    public boolean isExpired() {
        if (endDate == null) {
            return false;
        }
        Date currentDate = App.getGame().getDate();
        return currentDate.getDay() > endDate.getDay() ||
               currentDate.getSeason().ordinal() > endDate.getSeason().ordinal() ||
               currentDate.getYear() > endDate.getYear();
    }

    public boolean isParticipating(Player player) {
        return participants.contains(player);
    }

    public int getPlayerProgress(Player player) {
        return playerProgress.getOrDefault(player, 0);
    }

    public double getCompletionPercentage() {
        if (getTotalRequiredProgress() == 0) {
            return 0.0;
        }
        return (double) totalProgress / getTotalRequiredProgress() * 100.0;
    }

    public int getRemainingDays() {
        if (endDate == null) {
            return timeLimitDays;
        }
        Date currentDate = App.getGame().getDate();
        // Simple calculation - can be improved
        int currentDay = currentDate.getDay() + currentDate.getSeason().ordinal() * 28 + currentDate.getYear() * 112;
        int endDay = endDate.getDay() + endDate.getSeason().ordinal() * 28 + endDate.getYear() * 112;
        return Math.max(0, endDay - currentDay);
    }

    private int getActiveQuestsCount(Player player) {
        CoopQuestManager manager = CoopQuestManager.getInstance();
        return manager.getActiveQuestsForPlayer(player).size();
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Map<Item, Integer> getRequirements() { return requirements; }
    public int getGoldReward() { return goldReward; }
    public Item getItemReward() { return itemReward; }
    public int getItemRewardQuantity() { return itemRewardQuantity; }
    public int getMaxPlayers() { return maxPlayers; }
    public int getTimeLimitDays() { return timeLimitDays; }
    public Date getStartDate() { return startDate; }
    public Date getEndDate() { return endDate; }
    public List<Player> getParticipants() { return participants; }
    
    public List<String> getParticipantNames() {
        List<String> names = new ArrayList<>();
        for (Player participant : participants) {
            String name = participant.getUser() != null ? participant.getUser().getUsername() : "Unknown Player";
            names.add(name);
        }
        return names;
    }
    public Map<Player, Integer> getPlayerProgress() { return playerProgress; }
    public boolean isCompleted() { return isCompleted; }
    public boolean isActive() { return isActive; }
    public int getTotalProgress() { return totalProgress; }
    public int getCurrentPlayerCount() { return participants.size(); }
}
