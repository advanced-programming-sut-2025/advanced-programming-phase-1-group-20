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

            // Get character trait for personalized responses
            org.example.models.enums.Charactristic trait = npc.getCharacter();

            if (isFavorite) {
                increasePoints(POINTS_FOR_FAVORITE_GIFT);

                // Personalized favorite gift responses based on character trait
                switch (trait) {
                    case KIND:
                        response = getRandomResponse(new String[]{
                                "Oh! This is my favorite! Thank you so much!",
                                "You remembered my favorite! That's so thoughtful of you!",
                                "This is perfect! You really know how to make someone's day!",
                                "I absolutely love this! You're such a dear friend!"
                        });
                        break;
                    case HARD_WORKING:
                        response = getRandomResponse(new String[]{
                                "This is exactly what I needed! Very efficient of you to notice!",
                                "My favorite! This will help me be even more productive!",
                                "You put real effort into finding this for me, didn't you? I appreciate that!",
                                "This is perfect! A quality gift from a quality friend!"
                        });
                        break;
                    case LAZY:
                        response = getRandomResponse(new String[]{
                                "Whoa, my favorite! You saved me the trouble of getting it myself!",
                                "Nice! This is exactly what I wanted, and I didn't even have to drop any hints!",
                                "My favorite! How'd you know? Actually, don't tell me - that would require too much explanation.",
                                "Sweet! This is perfect for enjoying while doing absolutely nothing!"
                        });
                        break;
                    case JEALOUS:
                        response = getRandomResponse(new String[]{
                                "My favorite! I didn't think anyone would give me something this nice...",
                                "Wait, you got this for me? I... wow, I don't know what to say. Thank you.",
                                "This is my favorite! How did you afford this? ...Sorry, I mean thank you!",
                                "I've wanted one of these forever! Yours is probably better though... but this is still amazing!"
                        });
                        break;
                    case GREEDY:
                        response = getRandomResponse(new String[]{
                                "Now THIS is valuable! You have excellent taste in gifts!",
                                "My favorite! Do you know how much this is worth? I mean... thank you!",
                                "What a profitable exchange! I mean... what a wonderful gift! Thank you!",
                                "This must have cost a fortune! I'll treasure it... or maybe sell it for more... just kidding! Maybe."
                        });
                        break;
                    default:
                        response = "Oh! This is my favorite! Thank you so much!";
                }
            } else {
                increasePoints(POINTS_FOR_GIFT);

                // Personalized regular gift responses based on character trait
                switch (trait) {
                    case KIND:
                        response = getRandomResponse(new String[]{
                                "Thank you for the gift! It's so nice of you to think of me!",
                                "How thoughtful of you! I appreciate this very much!",
                                "What a lovely surprise! Thank you for your kindness!",
                                "You didn't have to bring me anything! Thank you!"
                        });
                        break;
                    case HARD_WORKING:
                        response = getRandomResponse(new String[]{
                                "Thank you! I'll put this to good use right away!",
                                "A practical gift! I appreciate your thoughtfulness.",
                                "This will come in handy for my projects. Thanks!",
                                "Thank you for the gift! It's always good to have more resources."
                        });
                        break;
                    case LAZY:
                        response = getRandomResponse(new String[]{
                                "Thanks... you didn't have to go to all that trouble.",
                                "A gift? For me? That's surprisingly energetic of you.",
                                "Cool, thanks. Saves me the effort of getting one myself.",
                                "Nice. I'll add it to my collection of stuff I might use someday."
                        });
                        break;
                    case JEALOUS:
                        response = getRandomResponse(new String[]{
                                "You're giving this to me? I bet you give better gifts to others...",
                                "Thanks... I guess. It's not what I would have chosen, but it's fine.",
                                "A gift for me? That's... unexpected. Thanks, I suppose.",
                                "Well, it's not the best gift I've ever received, but thank you anyway."
                        });
                        break;
                    case GREEDY:
                        response = getRandomResponse(new String[]{
                                "Hmm, what's this worth? I mean... thank you for the gift!",
                                "I suppose I could find a use for this. Thanks.",
                                "Not the most valuable thing, but I appreciate the gesture.",
                                "I'll add this to my collection. Every item has its worth!"
                        });
                        break;
                    default:
                        response = "Thank you for the gift!";
                }
            }

            giftHistory.add(new GiftRecord(item, currentDate, isFavorite));
            giftedToday = true;
        } else {
            // Personalized responses for already received a gift
            org.example.models.enums.Charactristic trait = npc.getCharacter();

            switch (trait) {
                case KIND:
                    response = getRandomResponse(new String[]{
                            "I already received a gift from you today, but it's sweet of you to offer another!",
                            "You're too generous! But I can only accept one gift per day.",
                            "That's very kind, but I already have your wonderful gift from earlier."
                    });
                    break;
                case HARD_WORKING:
                    response = getRandomResponse(new String[]{
                            "I appreciate the offer, but I already logged your gift for today.",
                            "One gift per day is my rule. It keeps things efficient.",
                            "Thanks, but I already received something from you. Let's save this for tomorrow."
                    });
                    break;
                case LAZY:
                    response = getRandomResponse(new String[]{
                            "Whoa, another gift? That's too much effort... for both of us.",
                            "I already got something from you today. Too much stuff is just... more to manage.",
                            "Can we do this tomorrow? I've already dealt with one gift today."
                    });
                    break;
                case JEALOUS:
                    response = getRandomResponse(new String[]{
                            "Another gift? Are you trying to make me feel obligated to you?",
                            "I already got your gift today. Are you giving extras to everyone else too?",
                            "One gift per day is enough. I don't want to owe you too much."
                    });
                    break;
                case GREEDY:
                    response = getRandomResponse(new String[]{
                            "I already accepted one valuable item from you today. Rules are rules!",
                            "Save it for tomorrow! I'll get more value if we space out the gifts.",
                            "Business is business - one transaction per day. Come back tomorrow!"
                    });
                    break;
                default:
                    response = "I already received a gift from you today.";
            }
        }

        return response;
    }

    private String getRandomResponse(String[] responses) {
        if (responses == null || responses.length == 0) {
            return "Thank you!";
        }
        return responses[new Random().nextInt(responses.length)];
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
