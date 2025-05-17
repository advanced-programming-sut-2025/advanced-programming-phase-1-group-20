package org.example.models.entities;

import org.example.models.App;
import org.example.models.Items.Item;
import org.example.models.Player.Player;
import org.example.models.common.Date;
import org.example.models.common.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Friendship {
    // Friendship levels
    public static final int LEVEL_0 = 0; // Initial level can talk and trade
    public static final int LEVEL_1 = 1; // Can gift
    public static final int LEVEL_2 = 2; // Can hug
    public static final int LEVEL_3 = 3; // Can propose marriage
    public static final int LEVEL_4 = 4; // Married, shared resources

    // XP requirements for each level
    public static final int XP_LEVEL_0_TO_1 = 100;
    public static final int XP_LEVEL_1_TO_2 = 200;
    public static final int XP_LEVEL_2_TO_3 = 300;
    public static final int XP_LEVEL_3_TO_4 = 400;

    // XP changes for interactions
    public static final int XP_TALK = 20;
    public static final int XP_TRADE_SUCCESS = 50;
    public static final int XP_TRADE_FAILURE = -30;
    public static final int XP_HUG = 60;
    public static final int XP_DAILY_DECAY = -10;

    private Player player1;
    private Player player2;
    private int level;
    private int xp;
    private Map<String, Long> lastInteractionTimes;
    private List<GiftRecord> giftHistory;
    private List<String> chatHistory;
    private boolean bouquetGiven;
    private boolean married;
    private boolean energyBoostedToday = false;
    private Map<Long, int[]> lastInteractionDates = new HashMap<>();

    public Friendship(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.level = LEVEL_0;
        this.xp = 0;
        this.lastInteractionTimes = new HashMap<>();
        this.giftHistory = new ArrayList<>();
        this.chatHistory = new ArrayList<>();
        this.bouquetGiven = false;
        this.married = false;
    }

    public Friendship() {

    }

    public int getLevel() {
        return level;
    }

    public int getXp() {
        return xp;
    }

    public int getXpRequiredForNextLevel() {
        return 100 * (1 + level);
    }

    public int getMaxXpForCurrentLevel() {
        switch (level) {
            case LEVEL_0:
                return XP_LEVEL_0_TO_1;
            case LEVEL_1:
                return XP_LEVEL_1_TO_2;
            case LEVEL_2:
                return XP_LEVEL_2_TO_3;
            case LEVEL_3:
                return XP_LEVEL_3_TO_4;
            default:
                return Integer.MAX_VALUE;
        }
    }

    public boolean increaseXp(int amount) {
        int oldLevel = level;
        xp += amount;

        if (xp >= getMaxXpForCurrentLevel() && level < LEVEL_4) {
            if (level == LEVEL_2 && !bouquetGiven) {
                xp = getMaxXpForCurrentLevel();
                return false;
            }

            level++;
            xp = 0;

            if (level == LEVEL_4) {
                married = true;
            }

            return true;
        }

        return false;
    }

    public boolean decreaseXp(int amount) {
        int oldLevel = level;
        xp -= amount;

        if (xp < 0) {
            if (level > 0) {
                level--;
                xp = getMaxXpForCurrentLevel() - 10;
            } else {
                xp = 0;
            }

            return oldLevel != level;
        }

        return false;
    }

    public boolean talk(String message, Player sender) {
        if (!areAdjacent(player1, player2)) {
            return false;
        }

        String key = "talk";
        if (hasInteractedToday(key)) {
            return false;
        }

        lastInteractionTimes.put(key, System.currentTimeMillis());
        chatHistory.add(sender.equals(player1) ? player1.getUser().getUsername() + ": " + message : player2.getUser().getUsername() + ": " + message);

        increaseXp(XP_TALK);

        if (married) {
            giveMarriageEnergyBoost();
        }

        return true;
    }

    public boolean trade(boolean success) {
        if (!areAdjacent(player1, player2)) {
            return false;
        }

        String key = "trade";
        if (hasInteractedToday(key)) {
            return false;
        }

        lastInteractionTimes.put(key, System.currentTimeMillis());

        if (success) {
            increaseXp(XP_TRADE_SUCCESS);
        } else {
            decreaseXp(XP_TRADE_FAILURE);
        }

        if (married) {
            giveMarriageEnergyBoost();
        }

        return true;
    }

    public boolean gift(Item item, Player sender, int amount) {
        if (!areAdjacent(player1, player2)) {
            return false;
        }

        String key = "gift";
        if (hasInteractedToday(key)) {
            return false;
        }

        if (level < LEVEL_1) {
            return false;
        }

        lastInteractionTimes.put(key, System.currentTimeMillis());
        giftHistory.add(new GiftRecord(item, sender, null, amount));

        App.getGame().getCurrentPlayer().getBackpack().remove(item, amount);
        Player targetPlayer = (sender.equals(player1) ? player2 : player1);
        targetPlayer.getBackpack().add(item, amount);

        if (married) {
            giveMarriageEnergyBoost();
        }

        return true;
    }

    public boolean rateGift(int giftIndex, int rating) {
        if (giftIndex < 0 || giftIndex >= giftHistory.size()) {
            return false;
        }

        if (rating < 1 || rating > 5) {
            return false;
        }

        GiftRecord gift = giftHistory.get(giftIndex);
        if (gift.getRating() != null) {
            return false;
        }

        gift.setRating(rating);

        int xpChange = 15 + 30 * (rating - 3);
        increaseXp(xpChange);

        return true;
    }

    public boolean hug(Player initiator) {
        if (!areAdjacent(player1, player2)) {
            return false;
        }

        String key = "hug";
        if (hasInteractedToday(key)) {
            return false;
        }

        if (level < LEVEL_2) {
            return false;
        }

        lastInteractionTimes.put(key, System.currentTimeMillis());

        increaseXp(XP_HUG);

        if (married) {
            giveMarriageEnergyBoost();
        }

        return true;
    }

    public boolean giveBouquet(Player giver) {
        if (!areAdjacent(player1, player2)) {
            return false;
        }

        if (level != LEVEL_2 || xp < getMaxXpForCurrentLevel()) {
            return false;
        }

        bouquetGiven = true;

        level = LEVEL_3;
        xp = 0;

        return true;
    }

    public boolean proposeMarriage(Player proposer) {
        if (!areAdjacent(player1, player2)) {
            return false;
        }

        if (level != LEVEL_3 || xp < getMaxXpForCurrentLevel()) {
            return false;
        }

        level = LEVEL_4;
        xp = 0;
        married = true;

        energyBoostedToday = false;

        return true;
    }

    public boolean giveMarriageEnergyBoost() {
        if (!married || energyBoostedToday) {
            return false;
        }

        player1.increaseEnergy(50);
        player2.increaseEnergy(50);

        energyBoostedToday = true;

        return true;
    }

    public void resetDailyEnergyBoost() {
        energyBoostedToday = false;
    }

    public boolean isMarried() {
        return married;
    }

    public List<String> getChatHistory() {
        return chatHistory;
    }

    public List<GiftRecord> getGiftHistory() {
        return giftHistory;
    }

    public boolean applyDailyDecay() {
        boolean interactedToday = false;
        for (String key : lastInteractionTimes.keySet()) {
            if (isToday(lastInteractionTimes.get(key))) {
                interactedToday = true;
                break;
            }
        }

        resetDailyEnergyBoost();

        if (!interactedToday) {
            return decreaseXp(XP_DAILY_DECAY);
        }

        return false;
    }

    private boolean areAdjacent(Player player1, Player player2) {
        if (player1 == null || player2 == null) {
            return false;
        }

        if (player1.getCurrentFarm() != player2.getCurrentFarm() && !(player1.getIsInVillage() && player2.getIsInVillage())) {
            return false;
        }


        Location loc1 = player1.getLocation();
        Location loc2 = player2.getLocation();

        if (loc1 == null || loc2 == null) {
            return false;
        }

        int distance = Math.abs(loc1.xAxis - loc2.xAxis) + Math.abs(loc1.yAxis - loc2.yAxis);

        return distance <= 2;
    }

    private boolean isToday(long time) {
        if (!lastInteractionDates.containsKey(time)) {
            Date currentDate = App.getGame().getDate();
            lastInteractionDates.put(time, new int[]{currentDate.getDay(), currentDate.getSeason().ordinal()});
            return true;
        }

        Date currentDate = App.getGame().getDate();

        int[] storedDate = lastInteractionDates.get(time);
        int storedDay = storedDate[0];
        int storedSeason = storedDate[1];

        return storedDay == currentDate.getDay() &&
                storedSeason == currentDate.getSeason().ordinal();
    }

    private boolean hasInteractedToday(String key) {
        return lastInteractionTimes.containsKey(key) && isToday(lastInteractionTimes.get(key));
    }

    public Player getTheOtherPlayer(Player player) {
        return (player.equals(player1) ? player2 : player1);
    }

    public void increaseFriendShipLevel(int amount) {
        level += amount;
    }

    public static class GiftRecord {
        private Item item;
        private Player sender;
        private Integer rating;
        private int amount;

        public GiftRecord(Item item, Player sender, Integer rating, int amount) {
            this.item = item;
            this.sender = sender;
            this.rating = rating;
            this.amount = amount;
        }

        public Item getItem() {
            return item;
        }


        public Player getSender() {
            return sender;
        }

        public Integer getRating() {
            return rating;
        }

        public void setRating(int rating) {
            this.rating = rating;
        }

        public int getAmount() {
            return amount;
        }
    }
}
