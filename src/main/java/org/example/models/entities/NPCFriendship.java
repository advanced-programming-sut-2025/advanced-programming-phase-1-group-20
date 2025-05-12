package org.example.models.entities;

import org.example.controllers.NPCController;
import org.example.models.Items.Item;
import org.example.models.Player.Player;
import org.example.models.common.Date;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class NPCFriendship {
    private static final int POINTS_FOR_TALKING = 20;
    private static final int POINTS_FOR_GIFT = 50;
    private static final int POINTS_FOR_FAVORITE_GIFT = 200;
    private static final int POINTS_PER_LEVEL = 200;
    private static final int MAX_FRIENDSHIP_POINTS = 799;
    // Controller reference
    private static NPCController controller;
    private Player player;
    private NPC npc;
    private int friendshipPoints;
    private boolean talkedToday;
    private boolean giftedToday;
    private Date lastInteractionDate;
    private List<String> chatHistory;
    private List<GiftRecord> giftHistory;

    public NPCFriendship(Player player, NPC npc) {
        this.player = player;
        this.npc = npc;
        this.friendshipPoints = 0;
        this.talkedToday = false;
        this.giftedToday = false;
        this.lastInteractionDate = null;
        this.chatHistory = new ArrayList<>();
        this.giftHistory = new ArrayList<>();
    }

    public static NPCController getController() {
        return controller;
    }

    public static void setController(NPCController npcController) {
        controller = npcController;
    }

    public int getLevel() {
        return Math.min(3, friendshipPoints / POINTS_PER_LEVEL);
    }

    public int getPoints() {
        return friendshipPoints;
    }

    public String talk(Date currentDate) {
        // Use the controller to handle talking
        return controller.talk(this, currentDate);
    }

    public String giveGift(Item item, Date currentDate) {
        // Use the controller to handle gift giving
        return controller.giveGift(this, item, currentDate);
    }

    private String getRandomResponse(String[] responses) {
        if (responses == null || responses.length == 0) {
            return "Thank you!";
        }
        return responses[new Random().nextInt(responses.length)];
    }

    public Item checkForNPCGift(Date currentDate) {
        // Use the controller to check for NPC gift
        return controller.checkForNPCGift(this, currentDate);
    }

    public void increasePoints(int amount) {
        friendshipPoints = Math.min(MAX_FRIENDSHIP_POINTS, friendshipPoints + amount);
    }

    public NPC getNpc() {
        return npc;
    }

    public Player getPlayer() {
        return player;
    }

    public List<String> getChatHistory() {
        return chatHistory;
    }

    public List<GiftRecord> getGiftHistory() {
        return giftHistory;
    }

    public void resetDailyFlags() {
        talkedToday = false;
        giftedToday = false;
    }

    // Additional getter/setter methods
    public Date getLastInteractionDate() {
        return lastInteractionDate;
    }

    public void setLastInteractionDate(Date lastInteractionDate) {
        this.lastInteractionDate = lastInteractionDate;
    }

    public boolean hasTalkedToday() {
        return talkedToday;
    }

    public void setTalkedToday(boolean talkedToday) {
        this.talkedToday = talkedToday;
    }

    public boolean hasGiftedToday() {
        return giftedToday;
    }

    public void setGiftedToday(boolean giftedToday) {
        this.giftedToday = giftedToday;
    }

    public void addToChatHistory(String chat) {
        chatHistory.add(chat);
    }

    public void addToGiftHistory(GiftRecord giftRecord) {
        giftHistory.add(giftRecord);
    }

    public static class GiftRecord {
        private Item item;
        private Date date;
        private boolean wasFavorite;

        public GiftRecord(Item item, Date date, boolean wasFavorite) {
            this.item = item;
            this.date = date;
            this.wasFavorite = wasFavorite;
        }

        public Item getItem() {
            return item;
        }

        public Date getDate() {
            return date;
        }

        public boolean wasFavorite() {
            return wasFavorite;
        }
    }
}
