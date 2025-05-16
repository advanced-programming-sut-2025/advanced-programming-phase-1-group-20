package org.example.models.entities;

import org.example.models.App;
import org.example.models.Items.Item;
import org.example.models.Player.Player;
import org.example.models.common.Date;
import org.example.models.enums.Npcs;

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

        // Quest 1: Deliver 50 Iron (
        Item ironOre = App.getItem("Iron Ore");
        if (ironOre != null) {
            Map<Item, Integer> requirements = Quest.createRequirement(ironOre, 50);
            Quest quest = new Quest(
                    nextQuestId++,
                    "Iron Delivery",
                    "Sebastian needs 50 Iron Ore for his engineering project.",
                    Npcs.SEBASTIAN,
                    requirements,
                    5000,
                    0, //  friendship level requirement
                    0  //  days passed requirement
            );
            sebastianQuests.add(quest);
            allQuests.put(quest.getId(), quest);
        }

        // Quest 2: Deliver a Pumpkin Pie
        Item pumpkinPie = App.getItem("Pumpkin Pie");
        if (pumpkinPie != null) {
            Map<Item, Integer> requirements = Quest.createRequirement(pumpkinPie, 1);
            Quest quest = new Quest(
                    nextQuestId++,
                    "Pumpkin Pie Craving",
                    "Sebastian has a craving for a Pumpkin Pie. Can you make one for him?",
                    Npcs.SEBASTIAN,
                    requirements,
                    500, // 500 gold reward
                    App.getItem("Friendship Level"),
                    1,   // 1 friendship level reward
                    1,   // Requires friendship level 1
                    0    // No days passed requirement
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
                    500, // 500 gold reward
                    App.getItem("Quartz"),
                    50,  // 50 quartz reward
                    0,   // No friendship level requirement
                    28   // Requires 28 days (1 season) to pass
            );
            sebastianQuests.add(quest);
            allQuests.put(quest.getId(), quest);
        }

        npcQuests.put(Npcs.SEBASTIAN, sebastianQuests);
    }

    private void initializeQuestsForAbigail() {
        List<Quest> abigailQuests = new ArrayList<>();

        // Quest 1: Deliver a Gold Bar
        Item goldBar = App.getItem("Gold Bar");
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
        Item pumpkin = App.getItem("Pumpkin");
        if (pumpkin != null) {
            Map<Item, Integer> requirements = Quest.createRequirement(pumpkin, 1);
            Quest quest = new Quest(
                    nextQuestId++,
                    "Pumpkin for Carving",
                    "Abigail wants to carve a pumpkin. Can you bring her one?",
                    Npcs.ABIGAIL,
                    requirements,
                    1000, // 1,000 gold reward
                    App.getItem("Automatic Watering Can"),
                    1,    // 1 automatic watering can reward
                    1,    // Requires friendship level 1
                    0     // No days passed requirement
            );
            abigailQuests.add(quest);
            allQuests.put(quest.getId(), quest);
        }

        // Quest 3: Deliver 50 Wheat
        Item wheat = App.getItem("Wheat");
        if (wheat != null) {
            Map<Item, Integer> requirements = Quest.createRequirement(wheat, 50);
            Quest quest = new Quest(
                    nextQuestId++,
                    "Wheat Collection",
                    "Abigail needs 50 Wheat for a special project.",
                    Npcs.ABIGAIL,
                    requirements,
                    0, // No gold reward
                    App.getItem("Diamond"),
                    2,  // 2 diamonds reward
                    0,  // No friendship level requirement
                    35  // Requires 35 days to pass
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
                    500, // 500 gold reward
                    App.getItem("Friendship Level"),
                    1,   // 1 friendship level reward
                    0,   // No friendship level requirement
                    0    // No days passed requirement
            );
            harveyQuests.add(quest);
            allQuests.put(quest.getId(), quest);
        }

        // Quest 2: Deliver a Salmon
        Item salmon = App.getItem("Salmon");
        if (salmon != null) {
            Map<Item, Integer> requirements = Quest.createRequirement(salmon, 1);
            Quest quest = new Quest(
                    nextQuestId++,
                    "Healthy Fish",
                    "Harvey wants a Salmon for his dinner. Can you catch one for him?",
                    Npcs.HARVEY,
                    requirements,
                    750, // 750 gold reward
                    App.getItem("Iridium Watering Can"),
                    1,   // 1 iridium watering can reward
                    1,   // Requires friendship level 1
                    0    // No days passed requirement
            );
            harveyQuests.add(quest);
            allQuests.put(quest.getId(), quest);
        }

        // Quest 3: Deliver a Bottle of Wine
        Item wine = App.getItem("Wine");
        if (wine != null) {
            Map<Item, Integer> requirements = Quest.createRequirement(wine, 1);
            Quest quest = new Quest(
                    nextQuestId++,
                    "Wine for Relaxation",
                    "Harvey wants a bottle of Wine to relax after a long day.",
                    Npcs.HARVEY,
                    requirements,
                    750, // 750 gold reward
                    App.getItem("Friendship Level"),
                    1,   // 1 friendship level reward
                    0,   // No friendship level requirement
                    42   // Requires 42 days (1.5 seasons) to pass
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
                    App.getItem("Salmon Recipe"),
                    1,   // 1 salmon recipe reward
                    0,   // No friendship level requirement
                    0    // No days passed requirement
            );
            leahQuests.add(quest);
            allQuests.put(quest.getId(), quest);
        }

        // Quest 2: Deliver a Salmon
        Item salmon = App.getItem("Salmon");
        if (salmon != null) {
            Map<Item, Integer> requirements = Quest.createRequirement(salmon, 1);
            Quest quest = new Quest(
                    nextQuestId++,
                    "Fish for Dinner",
                    "Leah wants a Salmon for her dinner. Can you catch one for her?",
                    Npcs.LEAH,
                    requirements,
                    500, // 500 gold reward
                    App.getItem("Scarecrow"),
                    3,   // 3 scarecrows reward
                    1,   // Requires friendship level 1
                    0    // No days passed requirement
            );
            leahQuests.add(quest);
            allQuests.put(quest.getId(), quest);
        }

        // Quest 3: Deliver a Bottle of Wine
        Item wine = App.getItem("Wine");
        if (wine != null) {
            Map<Item, Integer> requirements = Quest.createRequirement(wine, 1);
            Quest quest = new Quest(
                    nextQuestId++,
                    "Wine for Inspiration",
                    "Leah wants a bottle of Wine to help with her artistic inspiration.",
                    Npcs.LEAH,
                    requirements,
                    500, // 500 gold reward
                    App.getItem("Salad"),
                    5,   // 5 salads reward
                    0,   // No friendship level requirement
                    49   // Requires 49 days to pass
            );
            leahQuests.add(quest);
            allQuests.put(quest.getId(), quest);
        }

        npcQuests.put(Npcs.LEAH, leahQuests);
    }

    private void initializeQuestsForRobin() {
        List<Quest> robinQuests = new ArrayList<>();

        // Quest 1: Deliver 1000 Wood
        Item wood = App.getItem("Wood");
        if (wood != null) {
            Map<Item, Integer> requirements = Quest.createRequirement(wood, 1000);
            Quest quest = new Quest(
                    nextQuestId++,
                    "Wood Collection",
                    "Robin needs 1000 Wood for her carpentry projects.",
                    Npcs.ROBIN,
                    requirements,
                    25000, // 25,000 gold reward
                    0,     // No friendship level requirement
                    0      // No days passed requirement
            );
            robinQuests.add(quest);
            allQuests.put(quest.getId(), quest);
        }

        // Quest 2: Deliver 80 Wood
        if (wood != null) {
            Map<Item, Integer> requirements = Quest.createRequirement(wood, 80);
            Quest quest = new Quest(
                    nextQuestId++,
                    "More Wood Needed",
                    "Robin needs 80 more Wood for a special project.",
                    Npcs.ROBIN,
                    requirements,
                    1000, // 1,000 gold reward
                    App.getItem("Bee House"),
                    3,    // 3 beehives reward
                    1,    // Requires friendship level 1
                    0     // No days passed requirement
            );
            robinQuests.add(quest);
            allQuests.put(quest.getId(), quest);
        }

        // Quest 3: Deliver 10 Irons
        Item ironBar = App.getItem("Iron");
        if (ironBar != null) {
            Map<Item, Integer> requirements = Quest.createRequirement(ironBar, 10);
            Quest quest = new Quest(
                    nextQuestId++,
                    "Iron for Tools",
                    "Robin needs 10 Irons to make new tools.",
                    Npcs.ROBIN,
                    requirements,
                    1000, // 1,000 gold reward
                    App.getItem("Deluxe Cooking Recipe"),
                    1,    // 1 deluxe cooking recipe reward
                    0,    // No friendship level requirement
                    56    // Requires 56 days (2 seasons) to pass
            );
            robinQuests.add(quest);
            allQuests.put(quest.getId(), quest);
        }

        npcQuests.put(Npcs.ROBIN, robinQuests);
    }


    public List<Quest> getActiveQuestsForPlayer(Player player) {
        if (!playerQuests.containsKey(player)) {
            playerQuests.put(player, new ArrayList<>());
            activateInitialQuests(player);
        }

        List<Quest> activeQuests = new ArrayList<>();
        for (Quest quest : playerQuests.get(player)) {
            if (quest.isActive() && !quest.isCompleted()) {
                activeQuests.add(quest);
            }
        }

        return activeQuests;
    }


    private void activateInitialQuests(Player player) {
        for (Map.Entry<Npcs, List<Quest>> entry : npcQuests.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                Quest initialQuest = entry.getValue().get(0);
                if (initialQuest.getRequiredFriendshipLevel() == 0 && initialQuest.getRequiredDaysPassed() == 0) {
                    initialQuest.setActivationDate(App.getGame().getDate());
                    initialQuest.activate(player, App.getGame().getDate());
                    playerQuests.get(player).add(initialQuest);
                }
            }
        }
    }

    public void updateQuestsForPlayer(Player player, Date currentDate) {
        if (!playerQuests.containsKey(player)) {
            playerQuests.put(player, new ArrayList<>());
            activateInitialQuests(player);
        }

        for (Map.Entry<Npcs, List<Quest>> entry : npcQuests.entrySet()) {
            for (Quest quest : entry.getValue()) {
                if (!playerQuests.get(player).contains(quest) && !quest.isCompleted()) {
                    if (quest.getActivationDate() == null) {
                        quest.setActivationDate(currentDate);
                    }

                    if (quest.canActivate(player, currentDate)) {
                        quest.activate(player, currentDate);
                        playerQuests.get(player).add(quest);
                    }
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
}
