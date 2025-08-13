package org.example.common.models.entities;

import org.example.client.network.NetworkClient;
import org.example.common.models.App;
import org.example.common.models.Items.Item;
import org.example.common.models.Player.Player;
import org.example.common.models.common.Date;
import org.example.common.models.enums.Types.ItemBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoopQuestManager {
    private static CoopQuestManager instance;
    private final Map<Player, List<CoopQuest>> playerCoopQuests;
    private final Map<Integer, CoopQuest> allCoopQuests;
    private int nextCoopQuestId;

    private CoopQuestManager() {
        playerCoopQuests = new HashMap<>();
        allCoopQuests = new HashMap<>();
        nextCoopQuestId = 1;
        initializeCoopQuests();
    }

    public static CoopQuestManager getInstance() {
        if (instance == null) {
            instance = new CoopQuestManager();
        }
        return instance;
    }

    private void initializeCoopQuests() {
        // Quest 1: Collect 30 Iron by 4 players in 3 days
        Item iron = App.getItem("Iron");
        if (iron != null) {
            Map<Item, Integer> requirements = CoopQuest.createRequirement(iron, 30);
            CoopQuest quest = new CoopQuest(
                    nextCoopQuestId++,
                    "Iron Mining Expedition",
                    "Work together to collect 30 Iron for the village blacksmith. This requires teamwork and coordination.",
                    requirements,
                    500, // 500 gold reward per player
                    App.getItem("Diamond"),
                    2,   // 2 diamonds reward per player
                    4,   // 4 players max
                    3    // 3 days time limit
            );
            allCoopQuests.put(quest.getId(), quest);
        }

        // Quest 2: Collect 50 Wood by 3 players in 2 days
        Item wood = App.getItem("Wood");
        if (wood != null) {
            Map<Item, Integer> requirements = CoopQuest.createRequirement(wood, 50);
            CoopQuest quest = new CoopQuest(
                    nextCoopQuestId++,
                    "Timber Harvest",
                    "Help the carpenter by collecting 50 Wood. Perfect for a small group of friends.",
                    requirements,
                    300, // 300 gold reward per player
                    App.getItem("Hardwood"),
                    10,  // 10 hardwood reward per player
                    3,   // 3 players max
                    2    // 2 days time limit
            );
            allCoopQuests.put(quest.getId(), quest);
        }

        // Quest 3: Collect 20 Corn by 2 players in 4 days
        Item corn = App.getItem("Corn");
        if (corn != null) {
            Map<Item, Integer> requirements = CoopQuest.createRequirement(corn, 20);
            CoopQuest quest = new CoopQuest(
                    nextCoopQuestId++,
                    "Corn Harvest Festival",
                    "Prepare for the harvest festival by collecting 20 Corn. A perfect duo activity.",
                    requirements,
                    400, // 400 gold reward per player
                    App.getItem("Gold Bar"),
                    1,   // 1 gold bar reward per player
                    2,   // 2 players max
                    4    // 4 days time limit
            );
            allCoopQuests.put(quest.getId(), quest);
        }

        // Quest 4: Collect 15 Salmon by 3 players in 5 days
        Item salmon = App.getItem("Salmon");
        if (salmon != null) {
            Map<Item, Integer> requirements = CoopQuest.createRequirement(salmon, 15);
            CoopQuest quest = new CoopQuest(
                    nextCoopQuestId++,
                    "Fishing Expedition",
                    "Join forces to catch 15 Salmon for the village feast. Requires patience and teamwork.",
                    requirements,
                    600, // 600 gold reward per player
                    App.getItem("Iridium Bar"),
                    1,   // 1 iridium bar reward per player
                    3,   // 3 players max
                    5    // 5 days time limit
            );
            allCoopQuests.put(quest.getId(), quest);
        }

        // Quest 5: Collect 100 Stone by 4 players in 2 days
        Item stone = App.getItem("Stone");
        if (stone != null) {
            Map<Item, Integer> requirements = CoopQuest.createRequirement(stone, 100);
            CoopQuest quest = new CoopQuest(
                    nextCoopQuestId++,
                    "Stone Quarry",
                    "Help build the new village wall by collecting 100 Stone. A massive undertaking for a full team.",
                    requirements,
                    800, // 800 gold reward per player
                    App.getItem("Iridium Sprinkler"),
                    2,   // 2 iridium sprinklers reward per player
                    4,   // 4 players max
                    2    // 2 days time limit
            );
            allCoopQuests.put(quest.getId(), quest);
        }

        // Quest 6: Collect 25 Pumpkins by 3 players in 3 days
        Item pumpkin = App.getItem("Pumpkin");
        if (pumpkin != null) {
            Map<Item, Integer> requirements = CoopQuest.createRequirement(pumpkin, 25);
            CoopQuest quest = new CoopQuest(
                    nextCoopQuestId++,
                    "Pumpkin Patch",
                    "Prepare for the autumn festival by collecting 25 Pumpkins. A seasonal challenge for friends.",
                    requirements,
                    450, // 450 gold reward per player
                    App.getItem("Bee House"),
                    3,   // 3 bee houses reward per player
                    3,   // 3 players max
                    3    // 3 days time limit
            );
            allCoopQuests.put(quest.getId(), quest);
        }

        // Quest 7: Collect 40 Wheat by 2 players in 4 days
        Item wheat = App.getItem("Wheat");
        if (wheat != null) {
            Map<Item, Integer> requirements = CoopQuest.createRequirement(wheat, 40);
            CoopQuest quest = new CoopQuest(
                    nextCoopQuestId++,
                    "Wheat Harvest",
                    "Help the miller by collecting 40 Wheat. A classic farming challenge for two.",
                    requirements,
                    350, // 350 gold reward per player
                    App.getItem("Deluxe Scarecrow"),
                    2,   // 2 deluxe scarecrows reward per player
                    2,   // 2 players max
                    4    // 4 days time limit
            );
            allCoopQuests.put(quest.getId(), quest);
        }

        // Quest 8: Collect 60 Coal by 4 players in 3 days
        Item coal = App.getItem("Coal");
        if (coal != null) {
            Map<Item, Integer> requirements = CoopQuest.createRequirement(coal, 60);
            CoopQuest quest = new CoopQuest(
                    nextCoopQuestId++,
                    "Coal Mining",
                    "Supply the blacksmith with 60 Coal for his forge. A challenging mining expedition.",
                    requirements,
                    700, // 700 gold reward per player
                    App.getItem("Gold Bar"),
                    3,   // 3 gold bars reward per player
                    4,   // 4 players max
                    3    // 3 days time limit
            );
            allCoopQuests.put(quest.getId(), quest);
        }
    }

    public List<CoopQuest> getActiveQuestsForPlayer(Player player) {
        if (!playerCoopQuests.containsKey(player)) {
            playerCoopQuests.put(player, new ArrayList<>());
        }

        List<CoopQuest> activeQuests = new ArrayList<>();
        for (CoopQuest quest : playerCoopQuests.get(player)) {
            if (quest.isActive() && !quest.isCompleted()) {
                activeQuests.add(quest);
            }
        }

        return activeQuests;
    }

    public List<CoopQuest> getAvailableCoopQuests() {
        List<CoopQuest> availableQuests = new ArrayList<>();
        for (CoopQuest quest : allCoopQuests.values()) {
            if (quest.isAvailable()) {
                availableQuests.add(quest);
            }
        }
        return availableQuests;
    }

    public CoopQuest getCoopQuestById(int id) {
        return allCoopQuests.get(id);
    }

    public boolean joinCoopQuest(Player player, int questId, Date currentDate) {
        CoopQuest quest = getCoopQuestById(questId);
        if (quest == null) {
            return false;
        }

        boolean success = quest.joinQuest(player, currentDate);
        if (success) {
            // Add to player's quest list
            if (!playerCoopQuests.containsKey(player)) {
                playerCoopQuests.put(player, new ArrayList<>());
            }
            playerCoopQuests.get(player).add(quest);

            // Send notification to other players
            sendQuestJoinNotification(player, quest);

            // Check if quest is ready to start (all required players have joined)
            if (quest.isReadyToStart()) {
                sendQuestStartNotification(quest);
            }
        }

        return success;
    }

    private void sendQuestJoinNotification(Player joiningPlayer, CoopQuest quest) {
        // Only send notifications in multiplayer mode
        if (App.getGame() == null || !App.getGame().isMultiplayer()) {
            return;
        }

        try {
            String playerName = joiningPlayer.getUser() != null ? joiningPlayer.getUser().getUsername() : "Unknown Player";

            if (org.example.client.network.NetworkClient.getInstance() != null) {
                org.example.common.models.Message message = new org.example.common.models.Message();
                message.setType(org.example.common.models.Message.Type.COOP_QUEST_JOIN);
                message.putInBody("questTitle", quest.getTitle());
                message.putInBody("questId", quest.getId());
                message.putInBody("playerName", playerName);
                message.putInBody("timestamp", System.currentTimeMillis());

                NetworkClient.getInstance().sendMessage(message);
            }
        } catch (Exception e) {
            System.err.println("Failed to send co-op quest join notification: " + e.getMessage());
        }
    }

    public boolean contributeToCoopQuest(Player player, int questId, Map<Item, Integer> contribution) {
        CoopQuest quest = getCoopQuestById(questId);
        if (quest == null) {
            return false;
        }

        boolean success = quest.contributeToQuest(player, contribution);
        if (success) {
            // Send notification to other players
            sendQuestContributionNotification(player, quest);
        }

        return success;
    }

        private void sendQuestContributionNotification(Player contributingPlayer, CoopQuest quest) {
        // Only send notifications in multiplayer mode
        if (App.getGame() == null || !App.getGame().isMultiplayer()) {
            return;
        }

        try {
            String playerName = contributingPlayer.getUser() != null ? contributingPlayer.getUser().getUsername() : "Unknown Player";

            // Send message to server
            if (org.example.client.network.NetworkClient.getInstance() != null) {
                org.example.common.models.Message message = new org.example.common.models.Message();
                message.setType(org.example.common.models.Message.Type.COOP_QUEST_CONTRIBUTE);
                message.putInBody("questTitle", quest.getTitle());
                message.putInBody("questId", quest.getId());
                message.putInBody("playerName", playerName);
                message.putInBody("timestamp", System.currentTimeMillis());

                org.example.client.network.NetworkClient.getInstance().sendMessage(message);
            }
        } catch (Exception e) {
            System.err.println("Failed to send co-op quest contribution notification: " + e.getMessage());
        }
    }

    private void sendQuestStartNotification(CoopQuest quest) {
        // Only send notifications in multiplayer mode
        if (App.getGame() == null || !App.getGame().isMultiplayer()) {
            return;
        }

        try {
            // Send message to server
            if (org.example.client.network.NetworkClient.getInstance() != null) {
                org.example.common.models.Message message = new org.example.common.models.Message();
                message.setType(org.example.common.models.Message.Type.COOP_QUEST_START);
                message.putInBody("questTitle", quest.getTitle());
                message.putInBody("questId", quest.getId());
                message.putInBody("participantCount", quest.getCurrentPlayerCount());
                message.putInBody("maxPlayers", quest.getMaxPlayers());
                message.putInBody("timestamp", System.currentTimeMillis());

                org.example.client.network.NetworkClient.getInstance().sendMessage(message);
            }
        } catch (Exception e) {
            System.err.println("Failed to send co-op quest start notification: " + e.getMessage());
        }
    }

    /**
     * Get quests that are ready to start (all required players have joined)
     * @return List of quests ready to start
     */
    public List<CoopQuest> getQuestsReadyToStart() {
        List<CoopQuest> readyQuests = new ArrayList<>();
        for (CoopQuest quest : allCoopQuests.values()) {
            if (quest.isReadyToStart()) {
                readyQuests.add(quest);
            }
        }
        return readyQuests;
    }

    public void updateCoopQuestsForPlayer(Player player, Date currentDate) {
        List<CoopQuest> playerQuests = playerCoopQuests.get(player);
        if (playerQuests == null) {
            return;
        }

        // Remove expired quests
        playerQuests.removeIf(quest -> quest.isExpired() && !quest.isCompleted());
    }

    public void cleanupExpiredQuests() {
        for (CoopQuest quest : allCoopQuests.values()) {
            if (quest.isExpired() && !quest.isCompleted()) {
                // Send expiration notification
                sendQuestExpirationNotification(quest);
            }
        }
    }

        private void sendQuestExpirationNotification(CoopQuest quest) {
        // Only send notifications in multiplayer mode
        if (App.getGame() == null || !App.getGame().isMultiplayer()) {
            return;
        }

        try {
            // Send message to server
            if (org.example.client.network.NetworkClient.getInstance() != null) {
                org.example.common.models.Message message = new org.example.common.models.Message();
                message.setType(org.example.common.models.Message.Type.COOP_QUEST_EXPIRE);
                message.putInBody("questTitle", quest.getTitle());
                message.putInBody("questId", quest.getId());
                message.putInBody("timestamp", System.currentTimeMillis());

                org.example.client.network.NetworkClient.getInstance().sendMessage(message);
            }
        } catch (Exception e) {
            System.err.println("Failed to send co-op quest expiration notification: " + e.getMessage());
        }
    }
}
