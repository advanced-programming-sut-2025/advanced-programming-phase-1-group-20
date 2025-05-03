package org.example.models.entities;

import org.example.models.Items.Item;
import org.example.models.Player.Player;
import org.example.models.common.Date;
import org.example.models.common.Location;
import org.example.models.enums.Charactristic;
import org.example.models.enums.Jobs;

import java.util.*;

public class NPC extends Mob {
    private Charactristic character;
    private String name;
    private Jobs jobs;
    private HashMap<Integer, HashMap<Item, Integer>> missions;
    private List<Item> favoriteItems;
    private List<Item> giftItems;
    private Map<Player, NPCFriendship> friendships;
    private Location location;
    private String description;

    // Dialogue options based on different conditions
    private List<String> morningDialogues;
    private List<String> afternoonDialogues;
    private List<String> eveningDialogues;
    private List<String> rainyDialogues;
    private List<String> level1Dialogues;
    private List<String> level2Dialogues;
    private List<String> level3Dialogues;

    public NPC(Charactristic character, String name, Jobs jobs, HashMap<Integer, HashMap<Item, Integer>> missions) {
        super();
        this.character = character;
        this.name = name;
        this.jobs = jobs;
        this.missions = missions;
        this.favoriteItems = new ArrayList<>();
        this.giftItems = new ArrayList<>();
        this.friendships = new HashMap<>();

        this.morningDialogues = new ArrayList<>();
        this.afternoonDialogues = new ArrayList<>();
        this.eveningDialogues = new ArrayList<>();
        this.rainyDialogues = new ArrayList<>();
        this.level1Dialogues = new ArrayList<>();
        this.level2Dialogues = new ArrayList<>();
        this.level3Dialogues = new ArrayList<>();

        // Add default dialogues based on character
        initializeDefaultDialogues();
    }

    private void initializeDefaultDialogues() {
        switch (character) {
            case KIND:
                morningDialogues.add("Good morning! It's a beautiful day, isn't it?");
                afternoonDialogues.add("Hello there! How's your day going?");
                eveningDialogues.add("Good evening! I hope you had a productive day.");
                rainyDialogues.add("This rain is so refreshing, don't you think?");
                level1Dialogues.add("I'm glad we're getting to know each other better!");
                level2Dialogues.add("You're such a good friend. I appreciate you!");
                level3Dialogues.add("You're one of my favorite people in the valley!");
                break;
            case HARD_WORKING:
                morningDialogues.add("Up early to get work done? That's the spirit!");
                afternoonDialogues.add("Still working hard? Don't forget to take breaks!");
                eveningDialogues.add("Another day of hard work complete. Well done!");
                rainyDialogues.add("Rain or shine, the work must go on.");
                level1Dialogues.add("I respect your work ethic. We should collaborate sometime.");
                level2Dialogues.add("Your dedication to your craft is inspiring!");
                level3Dialogues.add("I consider you not just a friend, but a valued colleague.");
                break;
            case LAZY:
                morningDialogues.add("*yawn* Why are you up so early?");
                afternoonDialogues.add("Finally, a reasonable hour to be awake.");
                eveningDialogues.add("The best time of day - when work is over!");
                rainyDialogues.add("Perfect weather to stay inside and do nothing.");
                level1Dialogues.add("You're not so bad. At least you don't make me work too hard.");
                level2Dialogues.add("I like hanging out with you. You're easy to be around.");
                level3Dialogues.add("You know what? You're my favorite person to relax with.");
                break;
            case JEALOUS:
                morningDialogues.add("I see you're doing well for yourself this morning.");
                afternoonDialogues.add("Been productive today? Must be nice.");
                eveningDialogues.add("Your farm looks good. Mine could be better though.");
                rainyDialogues.add("I bet your crops love this rain. Mine are probably drowning.");
                level1Dialogues.add("I guess you're not as annoying as I thought.");
                level2Dialogues.add("I actually kind of enjoy having you around.");
                level3Dialogues.add("Don't tell anyone, but you might be my best friend.");
                break;
            case GREEDY:
                morningDialogues.add("Morning! Brought me anything good today?");
                afternoonDialogues.add("Hello! Any valuable finds in your adventures?");
                eveningDialogues.add("Evening! How profitable was your day?");
                rainyDialogues.add("This rain is good for business, you know.");
                level1Dialogues.add("I've got some special deals for friends like you.");
                level2Dialogues.add("You know, I could help you make more money with your farm.");
                level3Dialogues.add("Partners in profit! That's what we are!");
                break;
            default:
                morningDialogues.add("Good morning!");
                afternoonDialogues.add("Hello!");
                eveningDialogues.add("Good evening!");
                rainyDialogues.add("Rainy day, huh?");
                level1Dialogues.add("Nice to see you again!");
                level2Dialogues.add("Always a pleasure to see you!");
                level3Dialogues.add("My day is better now that you're here!");
        }
    }

    public String getDialogue(Date currentDate, int friendshipLevel) {
        Random random = new Random();
        List<String> appropriateDialogues = new ArrayList<>();

        // Add time-based dialogues
        int hour = currentDate.getHour();
        if (hour >= 6 && hour < 12) {
            appropriateDialogues.addAll(morningDialogues);
        } else if (hour >= 12 && hour < 18) {
            appropriateDialogues.addAll(afternoonDialogues);
        } else {
            appropriateDialogues.addAll(eveningDialogues);
        }

        // Add weather-based dialogues (assuming rainy is a property we can check)
        // For now, just randomly add rainy dialogues sometimes
        if (random.nextDouble() < 0.2) {
            appropriateDialogues.addAll(rainyDialogues);
        }

        // Add friendship level-based dialogues
        if (friendshipLevel >= 1) {
            appropriateDialogues.addAll(level1Dialogues);
        }
        if (friendshipLevel >= 2) {
            appropriateDialogues.addAll(level2Dialogues);
        }
        if (friendshipLevel >= 3) {
            appropriateDialogues.addAll(level3Dialogues);
        }

        // If no appropriate dialogues, return a default
        if (appropriateDialogues.isEmpty()) {
            return "Hello there!";
        }

        // Return a random dialogue from the appropriate ones
        return appropriateDialogues.get(random.nextInt(appropriateDialogues.size()));
    }

    public boolean isFavoriteItem(Item item) {
        return favoriteItems.contains(item);
    }

    public Item getRandomGiftItem() {
        if (giftItems.isEmpty()) {
            return null;
        }

        Random random = new Random();
        return giftItems.get(random.nextInt(giftItems.size()));
    }

    public NPCFriendship getFriendship(Player player) {
        if (!friendships.containsKey(player)) {
            friendships.put(player, new NPCFriendship(player, this));
        }
        return friendships.get(player);
    }


    public void addFavoriteItem(Item item) {
        if (!favoriteItems.contains(item)) {
            favoriteItems.add(item);
        }
    }

    public void addGiftItem(Item item) {
        if (!giftItems.contains(item)) {
            giftItems.add(item);
        }
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Item> getFavoriteItems() {
        return favoriteItems;
    }

    public List<Item> getGiftItems() {
        return giftItems;
    }

    public Charactristic getCharacter() {
        return character;
    }

    public String getName() {
        return name;
    }

    public Jobs getJobs() {
        return jobs;
    }

    public HashMap<Integer, HashMap<Item, Integer>> getMissions() {
        return missions;
    }
}
