package org.example.controllers;

import org.example.models.App;
import org.example.models.Items.Item;
import org.example.models.Player.Player;
import org.example.models.common.Date;
import org.example.models.common.Location;
import org.example.models.entities.NPC;
import org.example.models.entities.NPCFriendship;
import org.example.models.enums.Charactristic;
import org.example.models.enums.Weather;
import org.example.models.utils.HuggingFaceApiClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NPCController {
    private static final int POINTS_FOR_TALKING = 20;
    private static final int POINTS_FOR_GIFT = 50;
    private static final int POINTS_FOR_FAVORITE_GIFT = 200;
    private static final int POINTS_PER_LEVEL = 200;
    private static final int MAX_FRIENDSHIP_POINTS = 799;

    // Initialize the controller in the NPC and NPCFriendship classes
    public static void initialize() {
        NPC.setController(new NPCController());
        NPCFriendship.setController(NPC.getController());
    }

    // Dialogue generation methods
    public String getDialogue(NPC npc, Date currentDate, int friendshipLevel) {
        // If AI dialogue is enabled, use the Hugging Face API
        if (npc.isUseAiDialogue()) {
            try {
                // Create context information for the AI
                String context = createContextForAi(npc, currentDate, friendshipLevel);

                // Call the AI service to generate dialogue
                return HuggingFaceApiClient.generateDialogue(npc, context);
            } catch (Exception e) {
                System.err.println("Error using AI dialogue, falling back to predefined dialogues: " + e.getMessage());
                // If AI fails, fall back to predefined dialogues
                return getPreDefinedDialogue(npc, currentDate, friendshipLevel);
            }
        } else {
            return getPreDefinedDialogue(npc, currentDate, friendshipLevel);
        }
    }

    private String createContextForAi(NPC npc, Date currentDate, int friendshipLevel) {
        StringBuilder context = new StringBuilder();

        // Add time context
        int hour = currentDate.getHour();
        if (hour >= 6 && hour < 12) {
            context.append("Time: Morning. ");
        } else if (hour >= 12 && hour < 18) {
            context.append("Time: Afternoon. ");
        } else {
            context.append("Time: Evening. ");
        }

        // Add weather context
        try {
            if (App.getGame() != null && App.getGame().getDate() != null) {
                Weather currentWeather = App.getGame().getDate().getWeatherToday();
                context.append("Weather: ").append(currentWeather).append(". ");
            } else {
                context.append("Weather: SUNNY. "); // Default weather
            }
        } catch (Exception e) {
            // In test environment, App.getGame() might be null
            context.append("Weather: SUNNY. "); // Default weather
        }

        // Add friendship level context
        context.append("Friendship Level: ").append(friendshipLevel).append(" out of 3. ");

        // Add character trait context
        context.append("The NPC is ").append(npc.getCharacter()).append(". ");

        return context.toString();
    }

    private String getPreDefinedDialogue(NPC npc, Date currentDate, int friendshipLevel) {
        Random random = new Random();
        List<String> appropriateDialogues = new ArrayList<>();

        // Add time-based dialogues
        int hour = currentDate.getHour();
        if (hour >= 6 && hour < 12) {
            appropriateDialogues.addAll(npc.getMorningDialogues());
        } else if (hour >= 12 && hour < 18) {
            appropriateDialogues.addAll(npc.getAfternoonDialogues());
        } else {
            appropriateDialogues.addAll(npc.getEveningDialogues());
        }

        // Add weather-based dialogues
        try {
            if (App.getGame() != null && App.getGame().getDate() != null &&
                    App.getGame().getDate().getWeatherToday() == Weather.RAINY) {
                appropriateDialogues.addAll(npc.getRainyDialogues());
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not check weather, using default dialogues");
        }

        if (friendshipLevel >= 1) {
            appropriateDialogues.addAll(npc.getLevel1Dialogues());
        }
        if (friendshipLevel >= 2) {
            appropriateDialogues.addAll(npc.getLevel2Dialogues());
        }
        if (friendshipLevel >= 3) {
            appropriateDialogues.addAll(npc.getLevel3Dialogues());
        }

        // If no appropriate dialogues, return a default
        if (appropriateDialogues.isEmpty()) {
            return "Hello there!";
        }

        // Return a random dialogue from the appropriate ones
        return appropriateDialogues.get(random.nextInt(appropriateDialogues.size()));
    }

    // Friendship management methods
    public NPCFriendship getFriendship(NPC npc, Player player) {
        return npc.getFriendship(player);
    }

    public int getFriendshipLevel(NPCFriendship friendship) {
        return friendship.getLevel();
    }

    public int getFriendshipPoints(NPCFriendship friendship) {
        return friendship.getPoints();
    }

    public void increasePoints(NPCFriendship friendship, int amount) {
        friendship.increasePoints(amount);
    }

    // Interaction methods
    public String talk(NPCFriendship friendship, Date currentDate) {
        if (friendship.getLastInteractionDate() == null || !friendship.getLastInteractionDate().equals(currentDate)) {
            // Reset daily flags on a new day
            friendship.resetDailyFlags();
        }

        String response = getDialogue(friendship.getNpc(), currentDate, friendship.getLevel());
        friendship.addToChatHistory(response);
        friendship.setLastInteractionDate(currentDate);

        if (!friendship.hasTalkedToday()) {
            increasePoints(friendship, POINTS_FOR_TALKING);
            friendship.setTalkedToday(true);
        }

        return response;
    }

    public String giveGift(NPCFriendship friendship, Item item, Date currentDate) {
        if (friendship.getLastInteractionDate() == null || !friendship.getLastInteractionDate().equals(currentDate)) {
            // Reset daily flags on a new day
            friendship.resetDailyFlags();
        }

        String response;
        friendship.setLastInteractionDate(currentDate);

        if (!friendship.hasGiftedToday()) {
            boolean isFavorite = isFavoriteItem(friendship.getNpc(), item);

            // Get character trait for personalized responses
            Charactristic trait = friendship.getNpc().getCharacter();

            if (isFavorite) {
                increasePoints(friendship, POINTS_FOR_FAVORITE_GIFT);
                response = getFavoriteGiftResponse(trait);
            } else {
                increasePoints(friendship, POINTS_FOR_GIFT);
                response = getRegularGiftResponse(trait);
            }

            friendship.addToGiftHistory(new NPCFriendship.GiftRecord(item, currentDate, isFavorite));
            friendship.setGiftedToday(true);
        } else {
            response = getAlreadyGiftedResponse(friendship.getNpc().getCharacter());
        }

        return response;
    }

    private String getFavoriteGiftResponse(Charactristic trait) {
        switch (trait) {
            case KIND:
                return getRandomResponse(new String[]{
                        "Oh! This is my favorite! Thank you so much!",
                        "You remembered my favorite! That's so thoughtful of you!",
                        "This is perfect! You really know how to make someone's day!",
                        "I absolutely love this! You're such a dear friend!"
                });
            case HARD_WORKING:
                return getRandomResponse(new String[]{
                        "This is exactly what I needed! Very efficient of you to notice!",
                        "My favorite! This will help me be even more productive!",
                        "You put real effort into finding this for me, didn't you? I appreciate that!",
                        "This is perfect! A quality gift from a quality friend!"
                });
            case LAZY:
                return getRandomResponse(new String[]{
                        "Whoa, my favorite! You saved me the trouble of getting it myself!",
                        "Nice! This is exactly what I wanted, and I didn't even have to drop any hints!",
                        "My favorite! How'd you know? Actually, don't tell me - that would require too much explanation.",
                        "Sweet! This is perfect for enjoying while doing absolutely nothing!"
                });
            case JEALOUS:
                return getRandomResponse(new String[]{
                        "My favorite! I didn't think anyone would give me something this nice...",
                        "Wait, you got this for me? I... wow, I don't know what to say. Thank you.",
                        "This is my favorite! How did you afford this? ...Sorry, I mean thank you!",
                        "I've wanted one of these forever! Yours is probably better though... but this is still amazing!"
                });
            case GREEDY:
                return getRandomResponse(new String[]{
                        "Now THIS is valuable! You have excellent taste in gifts!",
                        "My favorite! Do you know how much this is worth? I mean... thank you!",
                        "What a profitable exchange! I mean... what a wonderful gift! Thank you!",
                        "This must have cost a fortune! I'll treasure it... or maybe sell it for more... just kidding! Maybe."
                });
            default:
                return "Oh! This is my favorite! Thank you so much!";
        }
    }

    private String getRegularGiftResponse(Charactristic trait) {
        switch (trait) {
            case KIND:
                return getRandomResponse(new String[]{
                        "Thank you for the gift! It's so nice of you to think of me!",
                        "How thoughtful of you! I appreciate this very much!",
                        "What a lovely surprise! Thank you for your kindness!",
                        "You didn't have to bring me anything! Thank you!"
                });
            case HARD_WORKING:
                return getRandomResponse(new String[]{
                        "Thank you! I'll put this to good use right away!",
                        "A practical gift! I appreciate your thoughtfulness.",
                        "This will come in handy for my projects. Thanks!",
                        "Thank you for the gift! It's always good to have more resources."
                });
            case LAZY:
                return getRandomResponse(new String[]{
                        "Thanks... you didn't have to go to all that trouble.",
                        "A gift? For me? That's surprisingly energetic of you.",
                        "Cool, thanks. Saves me the effort of getting one myself.",
                        "Nice. I'll add it to my collection of stuff I might use someday."
                });
            case JEALOUS:
                return getRandomResponse(new String[]{
                        "You're giving this to me? I bet you give better gifts to others...",
                        "Thanks... I guess. It's not what I would have chosen, but it's fine.",
                        "A gift for me? That's... unexpected. Thanks, I suppose.",
                        "Well, it's not the best gift I've ever received, but thank you anyway."
                });
            case GREEDY:
                return getRandomResponse(new String[]{
                        "Hmm, what's this worth? I mean... thank you for the gift!",
                        "I suppose I could find a use for this. Thanks.",
                        "Not the most valuable thing, but I appreciate the gesture.",
                        "I'll add this to my collection. Every item has its worth!"
                });
            default:
                return "Thank you for the gift!";
        }
    }

    private String getAlreadyGiftedResponse(Charactristic trait) {
        switch (trait) {
            case KIND:
                return getRandomResponse(new String[]{
                        "I already received a gift from you today, but it's sweet of you to offer another!",
                        "You're too generous! But I can only accept one gift per day.",
                        "That's very kind, but I already have your wonderful gift from earlier."
                });
            case HARD_WORKING:
                return getRandomResponse(new String[]{
                        "I appreciate the offer, but I already logged your gift for today.",
                        "One gift per day is my rule. It keeps things efficient.",
                        "Thanks, but I already received something from you. Let's save this for tomorrow."
                });
            case LAZY:
                return getRandomResponse(new String[]{
                        "Whoa, another gift? That's too much effort... for both of us.",
                        "I already got something from you today. Too much stuff is just... more to manage.",
                        "Can we do this tomorrow? I've already dealt with one gift today."
                });
            case JEALOUS:
                return getRandomResponse(new String[]{
                        "Another gift? Are you trying to make me feel obligated to you?",
                        "I already got your gift today. Are you giving extras to everyone else too?",
                        "One gift per day is enough. I don't want to owe you too much."
                });
            case GREEDY:
                return getRandomResponse(new String[]{
                        "I already accepted one valuable item from you today. Rules are rules!",
                        "Save it for tomorrow! I'll get more value if we space out the gifts.",
                        "Business is business - one transaction per day. Come back tomorrow!"
                });
            default:
                return "I already received a gift from you today.";
        }
    }

    private String getRandomResponse(String[] responses) {
        if (responses == null || responses.length == 0) {
            return "Thank you!";
        }
        return responses[new Random().nextInt(responses.length)];
    }

    public boolean isFavoriteItem(NPC npc, Item item) {
        return npc.getFavoriteItems().contains(item);
    }

    public void addFavoriteItem(NPC npc, Item item) {
        if (!npc.getFavoriteItems().contains(item)) {
            npc.getFavoriteItems().add(item);
        }
    }

    public void addGiftItem(NPC npc, Item item) {
        if (!npc.getGiftItems().contains(item)) {
            npc.getGiftItems().add(item);
        }
    }

    public Item getRandomGiftItem(NPC npc) {
        List<Item> giftItems = npc.getGiftItems();
        if (giftItems.isEmpty()) {
            return null;
        }

        Random random = new Random();
        return giftItems.get(random.nextInt(giftItems.size()));
    }

    public Item checkForNPCGift(NPCFriendship friendship, Date currentDate) {
        if (getFriendshipLevel(friendship) < 3) {
            return null;
        }

        // Check if it's a new day
        if (friendship.getLastInteractionDate() == null || !friendship.getLastInteractionDate().equals(currentDate)) {
            // 50% chance to send a gift
            Random random = new Random();
            if (random.nextDouble() < 0.5) {
                return getRandomGiftItem(friendship.getNpc());
            }
        }

        return null;
    }

    // Location management methods
    public Location getLocation(NPC npc) {
        return npc.getLocation();
    }

    public void setLocation(NPC npc, Location location) {
        npc.setLocation(location);
    }
}