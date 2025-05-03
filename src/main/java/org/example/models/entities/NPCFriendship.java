package org.example.models.entities;

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

    public int getLevel() {
        return Math.min(3, friendshipPoints / POINTS_PER_LEVEL);
    }


    public int getPoints() {
        return friendshipPoints;
    }


    public String talk(Date currentDate) {
        if (lastInteractionDate == null || !lastInteractionDate.equals(currentDate)) {
            // Reset daily flags on a new day
            talkedToday = false;
            giftedToday = false;
        }

        String response = npc.getDialogue(currentDate, getLevel());
        chatHistory.add(response);
        lastInteractionDate = currentDate;

        if (!talkedToday) {
            increasePoints(POINTS_FOR_TALKING);
            talkedToday = true;
        }

        return response;
    }

    public String giveGift(Item item, Date currentDate) {
        if (lastInteractionDate == null || !lastInteractionDate.equals(currentDate)) {
            // Reset daily flags on a new day
            talkedToday = false;
            giftedToday = false;
        }

        String response;
        lastInteractionDate = currentDate;

        if (!giftedToday) {
            boolean isFavorite = npc.isFavoriteItem(item);

            if (isFavorite) {
                increasePoints(POINTS_FOR_FAVORITE_GIFT);
                response = "Oh! This is my favorite! Thank you so much!";
            } else {
                increasePoints(POINTS_FOR_GIFT);
                response = "Thank you for the gift!";
            }

            giftHistory.add(new GiftRecord(item, currentDate, isFavorite));
            giftedToday = true;
        } else {
            response = "I already received a gift from you today.";
        }

        return response;
    }

    public Item checkForNPCGift(Date currentDate) {
        if (getLevel() < 3) {
            return null;
        }

        // Check if it's a new day
        if (lastInteractionDate == null || !lastInteractionDate.equals(currentDate)) {
            // 50% chance to send a gift
            Random random = new Random();
            if (random.nextDouble() < 0.5) {
                return npc.getRandomGiftItem();
            }
        }

        return null;
    }

    private void increasePoints(int amount) {
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