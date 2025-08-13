package org.example.common.models.entities;

import org.example.client.network.NetworkClient;
import org.example.common.models.App;
import org.example.common.models.Items.Item;
import org.example.common.models.Message;
import org.example.common.models.Player.Player;
import org.example.common.models.common.Date;
import org.example.common.models.enums.Npcs;
import org.example.common.models.enums.Types.ItemBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class QuestManager {
    private static QuestManager instance;
    private final Map<Player, List<Quest>> playerQuests;
    private final Map<Npcs, List<Quest>> npcQuests;
    private final Map<Integer, Quest> allQuests;
    private int nextQuestId;

    private QuestManager() {
        playerQuests = new HashMap<>();
        npcQuests = new HashMap<>();
        allQuests = new HashMap<>();
        nextQuestId = 1;
        initializeQuests();
    }

    public static QuestManager getInstance() {
        if (instance == null) {
            instance = new QuestManager();
        }
        return instance;
    }

    private void initializeQuests() {
        initializeQuestsForSebastian();

        initializeQuestsForAbigail();

        initializeQuestsForHarvey();

        initializeQuestsForLeah();

        initializeQuestsForRobin();
    }


    private void initializeQuestsForSebastian() {
        List<Quest> sebastianQuests = new ArrayList<>();

        // Quest 1: Deliver 50 Iron
        Item iron = App.getItem("Iron");
        if (iron != null) {
            Map<Item, Integer> requirements = Quest.createRequirement(iron, 50);
            Quest quest = new Quest(
                    nextQuestId++,
                    "Iron Delivery",
                    "Sebastian needs 50 Iron for his engineering project.",
                    Npcs.SEBASTIAN,
                    requirements,
                    5000, // 5,000 gold reward
                    App.getItem("Diamond"),
                    2,   // 2 diamonds reward
                    0,   // No friendship level requirement
                    0    // No days passed requirement
            );
            sebastianQuests.add(quest);
            allQuests.put(quest.getId(), quest);
        }

        // Quest 2: Deliver a Pumpkin Pie
        Item pumpkinPie = ItemBuilder.build("Pumpkin Pie");
        if (pumpkinPie != null) {
            Map<Item, Integer> requirements = Quest.createRequirement(pumpkinPie, 1);
            Quest quest = new Quest(
                    nextQuestId++,
                    "Pumpkin Pie Craving",
                    "Sebastian has a craving for a Pumpkin Pie. Can you make one for him?",
                    Npcs.SEBASTIAN,
                    requirements,
                    5000, // 5,000 gold reward
                    App.getItem("Quartz"),
                    50,   // 50 quartz reward
                    0,    // No friendship level requirement
                    0     // No days passed requirement
            );
            sebastianQuests.add(quest);
            allQuests.put(quest.getId(), quest);
        }

        // Quest 3: Deliver 150 Stone
        Item stone = App.getItem("Stone");
        if (stone != null) {
            Map<Item, Integer> requirements = Quest.createRequirement(stone, 150);
            Quest quest = new Quest(
                    nextQuestId++,
                    "Stone Collection",
                    "Sebastian needs 150 Stone for a basement renovation project.",
                    Npcs.SEBASTIAN,
                    requirements,
                    5000, // 5,000 gold reward
                    App.getItem("Quartz"),
                    50,   // 50 quartz reward
                    2,    // No friendship level requirement
                    7     // No days passed requirement
            );
            sebastianQuests.add(quest);
            allQuests.put(quest.getId(), quest);
        }

        npcQuests.put(Npcs.SEBASTIAN, sebastianQuests);
    }

    private void initializeQuestsForAbigail() {
        List<Quest> abigailQuests = new ArrayList<>();

        // Quest 1: Deliver a Gold Bar
        Item goldBar = ItemBuilder.build("Gold Bar");
        if (goldBar != null) {
            Map<Item, Integer> requirements = Quest.createRequirement(goldBar, 1);
            Quest quest = new Quest(
                    nextQuestId++,
                    "Golden Gift",
                    "Abigail wants a Gold Bar for her collection.",
                    Npcs.ABIGAIL,
                    requirements,
                    500, // 500 gold reward
                    App.getItem("Friendship Level"),
                    1,   // 1 friendship level reward
                    0,   // No friendship level requirement
                    0    // No days passed requirement
            );
            abigailQuests.add(quest);
            allQuests.put(quest.getId(), quest);
        }

        // Quest 2: Deliver a Pumpkin
        Item pumpkin = ItemBuilder.build("Pumpkin");
        if (pumpkin != null) {
            Map<Item, Integer> requirements = Quest.createRequirement(pumpkin, 1);
            Quest quest = new Quest(
                    nextQuestId++,
                    "Pumpkin for Carving",
                    "Abigail wants to carve a pumpkin. Can you bring her one?",
                    Npcs.ABIGAIL,
                    requirements,
                    500, // 500 gold reward
                    App.getItem("Iridium Sprinkler"),
                    1,   // 1 iridium sprinkler reward
                    0,   // No friendship level requirement
                    0    // No days passed requirement
            );
            abigailQuests.add(quest);
            allQuests.put(quest.getId(), quest);
        }

        // Quest 3: Deliver 50 Wheat
        Item wheat = ItemBuilder.build("Wheat");
        if (wheat != null) {
            Map<Item, Integer> requirements = Quest.createRequirement(wheat, 50);
            Quest quest = new Quest(
                    nextQuestId++,
                    "Wheat Collection",
                    "Abigail needs 50 Wheat for a special project.",
                    Npcs.ABIGAIL,
                    requirements,
                    500, // 500 gold reward
                    App.getItem("Iridium Sprinkler"),
                    1,   // 1 iridium sprinkler reward
                    2,   // No friendship level requirement
                    5    // No days passed requirement
            );
            abigailQuests.add(quest);
            allQuests.put(quest.getId(), quest);
        }

        npcQuests.put(Npcs.ABIGAIL, abigailQuests);
    }

    private void initializeQuestsForHarvey() {
        List<Quest> harveyQuests = new ArrayList<>();

        // Quest 1: Deliver 12 of any crop
        Item anyPlant = App.getItem("Corn"); // Example crop
        if (anyPlant != null) {
            Map<Item, Integer> requirements = Quest.createRequirement(anyPlant, 12);
            Quest quest = new Quest(
                    nextQuestId++,
                    "Crop Research",
                    "Harvey needs 12 of any crop for his medical research.",
                    Npcs.HARVEY,
                    requirements,
                    750, // 750 gold reward
                    App.getItem("Friendship Level"),
                    1,   // 1 friendship level reward
                    0,   // No friendship level requirement
                    0    // No days passed requirement
            );
            harveyQuests.add(quest);
            allQuests.put(quest.getId(), quest);
        }

        // Quest 2: Deliver a Salmon
        Item salmon = ItemBuilder.build("Salmon");
        if (salmon != null) {
            Map<Item, Integer> requirements = Quest.createRequirement(salmon, 1);
            Quest quest = new Quest(
                    nextQuestId++,
                    "Healthy Fish",
                    "Harvey wants a Salmon for his dinner. Can you catch one for him?",
                    Npcs.HARVEY,
                    requirements,
                    750, // 750 gold reward
                    App.getItem("Friendship Level"),
                    1,   // 1 friendship level reward
                    1,   // No friendship level requirement
                    2    // No days passed requirement
            );
            harveyQuests.add(quest);
            allQuests.put(quest.getId(), quest);
        }

        // Quest 3: Deliver a Bottle of Wine
        Item wine = ItemBuilder.build("Wine");
        if (wine != null) {
            Map<Item, Integer> requirements = Quest.createRequirement(wine, 1);
            Quest quest = new Quest(
                    nextQuestId++,
                    "Wine for Relaxation",
                    "Harvey wants a bottle of Wine to relax after a long day.",
                    Npcs.HARVEY,
                    requirements,
                    750, // 750 gold reward
                    App.getItem("Salad"),
                    5,   // 5 salads reward
                    2,
                    3    // No days passed requirement
            );
            harveyQuests.add(quest);
            allQuests.put(quest.getId(), quest);
        }
        npcQuests.put(Npcs.HARVEY, harveyQuests);
    }

    private void initializeQuestsForLeah() {
        List<Quest> leahQuests = new ArrayList<>();

        // Quest 1: Deliver 10 Hardwood
        Item hardwood = App.getItem("Hardwood");
        if (hardwood != null) {
            Map<Item, Integer> requirements = Quest.createRequirement(hardwood, 10);
            Quest quest = new Quest(
                    nextQuestId++,
                    "Hardwood for Sculptures",
                    "Leah needs 10 Hardwood for her sculptures.",
                    Npcs.LEAH,
                    requirements,
                    500, // 500 gold reward
                    App.getItem("Salmon Dinner"),
                    1,   // 1 salmon dinner recipe reward
                    0,   // No friendship level requirement
                    0    // No days passed requirement
            );
            leahQuests.add(quest);
            allQuests.put(quest.getId(), quest);
        }

        // Quest 2: Deliver a Salmon
        Item salmon = ItemBuilder.build("Salmon");
        if (salmon != null) {
            Map<Item, Integer> requirements = Quest.createRequirement(salmon, 1);
            Quest quest = new Quest(
                    nextQuestId++,
                    "Fish for Dinner",
                    "Leah wants a Salmon for her dinner. Can you catch one for her?",
                    Npcs.LEAH,
                    requirements,
                    500, // 500 gold reward
                    App.getItem("Deluxe Scarecrow"),
                    3,   // 3 deluxe scarecrows reward
                    0,   // No friendship level requirement
                    0    // No days passed requirement
            );
            leahQuests.add(quest);
            allQuests.put(quest.getId(), quest);
        }

        npcQuests.put(Npcs.LEAH, leahQuests);
    }

    private void initializeQuestsForRobin() {
        List<Quest> robinQuests = new ArrayList<>();

        // Quest 1: Deliver 80 Wood
        Item wood = ItemBuilder.build("Wood");
        if (wood != null) {
            Map<Item, Integer> requirements = Quest.createRequirement(wood, 80);
            Quest quest = new Quest(
                    nextQuestId++,
                    "Wood Collection",
                    "Robin needs 80 Wood for her carpentry projects.",
                    Npcs.ROBIN,
                    requirements,
                    1000, // 1,000 gold reward
                    App.getItem("Bee House"),
                    3,    // 3 bee houses reward
                    0,    // No friendship level requirement
                    0     // No days passed requirement
            );
            robinQuests.add(quest);
            allQuests.put(quest.getId(), quest);
        }

        // Quest 2: Deliver 10 Iron Bars
        Item ironBar = ItemBuilder.build("Iron Bar");
        if (ironBar != null) {
            Map<Item, Integer> requirements = Quest.createRequirement(ironBar, 10);
            Quest quest = new Quest(
                    nextQuestId++,
                    "Iron for Tools",
                    "Robin needs 10 Iron Bars to make new tools.",
                    Npcs.ROBIN,
                    requirements,
                    1000, // 1,000 gold reward
                    App.getItem("Bee House"),
                    3,    // 3 bee houses reward
                    0,    // No friendship level requirement
                    0     // No days passed requirement
            );
            robinQuests.add(quest);
            allQuests.put(quest.getId(), quest);
        }

        // Quest 3: Deliver 1000 Wood
        if (wood != null) {
            Map<Item, Integer> requirements = Quest.createRequirement(wood, 1000);
            Quest quest = new Quest(
                    nextQuestId++,
                    "Massive Wood Collection",
                    "Robin needs 1000 Wood for a major construction project.",
                    Npcs.ROBIN,
                    requirements,
                    25000, // 25,000 gold reward
                    null,  // No item reward
                    0,    // No item quantity
                    0,    // No friendship level requirement
                    0     // No days passed requirement
            );
            robinQuests.add(quest);
            allQuests.put(quest.getId(), quest);
        }

        // Quest 4: Advanced Carpentry (requires friendship level 1 and 10 days passed)
        Item spaghetti = ItemBuilder.build("Spaghetti");
        if (spaghetti != null) {
            Map<Item, Integer> requirements = Quest.createRequirement(spaghetti, 15);
            Quest quest = new Quest(
                    nextQuestId++,
                    "Carpenter's Apprentice",
                    "Robin needs 15 Spaghetti for a special project. She's starting to trust you with more important work.",
                    Npcs.ROBIN,
                    requirements,
                    2000, // 2,000 gold reward
                    App.getItem("Friendship Level"),
                    2,   // 2 friendship levels reward
                    1,   // Requires friendship level 1
                    10   // Requires 10 days passed
            );
            robinQuests.add(quest);
            allQuests.put(quest.getId(), quest);
        }

        // Quest 5: Master Carpenter's Challenge (requires friendship level 2 and 21 days passed)
        Item iron = ItemBuilder.build("Iron");
        if (iron != null) {
            Map<Item, Integer> requirements = Quest.createRequirement(iron, 50);
            Quest quest = new Quest(
                    nextQuestId++,
                    "Master Carpenter's Challenge",
                    "Robin needs 50 Iron for a master-level project. This is a challenge for her most trusted friends.",
                    Npcs.ROBIN,
                    requirements,
                    5000, // 5,000 gold reward
                    App.getItem("Iridium Bar"),
                    1,   // 1 iridium bar reward
                    2,   // Requires friendship level 2
                    21   // Requires 21 days passed
            );
            robinQuests.add(quest);
            allQuests.put(quest.getId(), quest);
        }

        npcQuests.put(Npcs.ROBIN, robinQuests);
    }


    public List<Quest> getActiveQuestsForPlayer(Player player) {
        if (!playerQuests.containsKey(player)) {
            playerQuests.put(player, new ArrayList<>());
        }

        List<Quest> activeQuests = new ArrayList<>();
        for (Quest quest : playerQuests.get(player)) {
            if (quest.isActive() && !quest.isCompleted()) {
                activeQuests.add(quest);
            }
        }

        return activeQuests;
    }

    /**
     * Get all available quests that can be taken by any player
     * @return List of quests that are not taken by anyone
     */
    public List<Quest> getAvailableQuests() {
        List<Quest> availableQuests = new ArrayList<>();
        for (Quest quest : allQuests.values()) {
            if (quest.isAvailable()) {
                availableQuests.add(quest);
            }
        }
        return availableQuests;
    }

    /**
     * Get available quests for a specific NPC
     * @param npc The NPC to get quests for
     * @return List of available quests for this NPC
     */
    public List<Quest> getAvailableQuestsForNpc(Npcs npc) {
        List<Quest> availableQuests = new ArrayList<>();
        List<Quest> npcQuestsList = npcQuests.getOrDefault(npc, new ArrayList<>());

        for (Quest quest : npcQuestsList) {
            if (quest.isAvailable()) {
                availableQuests.add(quest);
            }
        }
        return availableQuests;
    }

    /**
     * Get quests taken by other players (for display purposes)
     * @param currentPlayer The current player (to exclude their own quests)
     * @return List of quests taken by other players
     */
    public List<Quest> getQuestsTakenByOthers(Player currentPlayer) {
        List<Quest> takenQuests = new ArrayList<>();
        for (Quest quest : allQuests.values()) {
            if (quest.getTakenBy() != null && !quest.getTakenBy().equals(currentPlayer) && !quest.isCompleted()) {
                takenQuests.add(quest);
            }
        }
        return takenQuests;
    }

    public boolean takeQuest(Player player, int questId, Date currentDate) {
        Quest quest = allQuests.get(questId);
        if (quest == null) {
            return false;
        }

        // Try to take the quest
        if (quest.takeQuest(player, currentDate)) {
            // Add to player's quest list
            if (!playerQuests.containsKey(player)) {
                playerQuests.put(player, new ArrayList<>());
            }
            playerQuests.get(player).add(quest);

            // Send message to server for multiplayer synchronization
            System.out.println("🔍 QuestManager: App.getGame() = " + (App.getGame() != null));
            System.out.println("🔍 QuestManager: isMultiplayer() = " + (App.getGame() != null && App.getGame().isMultiplayer()));
            
            if (App.getGame() != null && App.getGame().isMultiplayer()) {
                Message message = new Message();
                message.setType(Message.Type.TAKE_QUEST);
                message.putInBody("playerUsername", player.getUser().getUsername());
                message.putInBody("questId", questId);
                message.putInBody("currentDate", currentDate.toString());

                try {
                    NetworkClient networkClient = NetworkClient.getInstance();
                    System.out.println("🔍 QuestManager: NetworkClient instance: " + (networkClient != null));
                    System.out.println("🔍 QuestManager: NetworkClient authenticated: " + (networkClient != null && networkClient.isAuthenticated()));
                    
                    if (networkClient != null && networkClient.isAuthenticated()) {
                        System.out.println("+++++++++++++++++++++============++++++++++");
                        System.out.println("sent take quest message to server: " + message.toString());
                        networkClient.sendMessage(message);
                        System.out.println("🔍 QuestManager: Message sent successfully");
                    } else {
                        System.out.println("🔍 QuestManager: Cannot send message - NetworkClient not available or not authenticated");
                    }
                } catch (Exception e) {
                    System.err.println("Failed to send TAKE_QUEST message: " + e.getMessage());
                }
            }
            return true;
        }

        return false;
    }

    public List<Quest> getAllQuestsForPlayer(Player player) {
        if (!playerQuests.containsKey(player)) {
            playerQuests.put(player, new ArrayList<>());
        }
        return new ArrayList<>(playerQuests.get(player));
    }


    public void initializeQuestsWithDate(Date currentDate) {
        for (Quest quest : allQuests.values()) {
            if (quest.getActivationDate() == null) {
                quest.setActivationDate(currentDate);
            }
        }
    }




    public void updateQuestsForPlayer(Player player, Date currentDate) {
        if (!playerQuests.containsKey(player)) {
            playerQuests.put(player, new ArrayList<>());
        }

        // Initialize activation dates for all quests if they haven't been set yet
        for (Map.Entry<Npcs, List<Quest>> entry : npcQuests.entrySet()) {
            for (Quest quest : entry.getValue()) {
                if (quest.getActivationDate() == null) {
                    quest.setActivationDate(currentDate);
                }
            }
        }
    }


    public boolean completeQuest(Player player, int questId) {
        if (!playerQuests.containsKey(player)) {
            return false;
        }

        for (Quest quest : playerQuests.get(player)) {
            if (quest.getId() == questId && quest.isActive() && !quest.isCompleted()) {
                return quest.complete(player);
            }
        }

        return false;
    }


    public Quest getQuest(int questId) {
        return allQuests.get(questId);
    }


    public List<Quest> getQuestsForNpc(Npcs npc) {
        return npcQuests.getOrDefault(npc, new ArrayList<>());
    }

    /**
     * Take a quest for a player by username (used for server synchronization)
     * @param username The username of the player taking the quest
     * @param questId The ID of the quest to take
     * @param currentDate Current game date
     * @return true if the quest was successfully taken, false otherwise
     */
    public boolean takeQuestByUsername(String username, int questId, Date currentDate) {
        if (App.getGame() == null) {
            return false;
        }

        Player player = App.getGame().getPlayerByUsername(username);
        if (player == null) {
            System.err.println("Player not found for username: " + username);
            return false;
        }

        return takeQuest(player, questId, currentDate);
    }
}
