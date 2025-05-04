package org.example.models.entities;

import org.example.models.App;
import org.example.models.Items.Item;
import org.example.models.Player.Player;
import org.example.models.common.Date;
import org.example.models.common.Location;
import org.example.models.enums.Charactristic;
import org.example.models.enums.Jobs;
import org.example.models.enums.Weather;
import org.example.models.utils.HuggingFaceApiClient;

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

    // Flag to control whether to use AI-generated dialogues
    private boolean useAiDialogue = true;

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
                // Morning dialogues
                morningDialogues.add("Good morning! It's a beautiful day, isn't it?");
                morningDialogues.add("Rise and shine! I hope you slept well.");
                morningDialogues.add("What a lovely morning! I'm so happy to see you.");
                morningDialogues.add("Good morning! I was just thinking about you.");

                // Afternoon dialogues
                afternoonDialogues.add("Hello there! How's your day going?");
                afternoonDialogues.add("It's so nice to see you this afternoon!");
                afternoonDialogues.add("I hope your day has been as wonderful as you are.");
                afternoonDialogues.add("Taking a break from your farm work? It's good to rest sometimes.");

                // Evening dialogues
                eveningDialogues.add("Good evening! I hope you had a productive day.");
                eveningDialogues.add("The stars are beautiful tonight, just like your company.");
                eveningDialogues.add("Evenings are my favorite time of day, especially when I get to see friends.");
                eveningDialogues.add("Winding down for the day? Me too. I'm glad we ran into each other.");

                // Rainy dialogues
                rainyDialogues.add("This rain is so refreshing, don't you think?");
                rainyDialogues.add("I love the sound of raindrops. It's so peaceful.");
                rainyDialogues.add("Rain is nature's way of watering your crops for you!");
                rainyDialogues.add("Even on rainy days, seeing you brightens my mood.");

                // Friendship level dialogues
                level1Dialogues.add("I'm glad we're getting to know each other better!");
                level1Dialogues.add("You know, you're really easy to talk to.");
                level1Dialogues.add("I enjoy our conversations. We should chat more often.");

                level2Dialogues.add("You're such a good friend. I appreciate you!");
                level2Dialogues.add("I was just telling someone the other day how kind you are.");
                level2Dialogues.add("Friends like you make life in this valley so much better.");

                level3Dialogues.add("You're one of my favorite people in the valley!");
                level3Dialogues.add("I'm so thankful our paths crossed. You've made such a positive impact on my life.");
                level3Dialogues.add("I can always count on you to brighten my day. Thank you for being you.");
                break;

            case HARD_WORKING:
                // Morning dialogues
                morningDialogues.add("Up early to get work done? That's the spirit!");
                morningDialogues.add("Morning! I've already been up for hours working.");
                morningDialogues.add("Early bird gets the worm! Ready for a productive day?");
                morningDialogues.add("Good morning! Nothing like starting the day with some hard work.");

                // Afternoon dialogues
                afternoonDialogues.add("Still working hard? Don't forget to take breaks!");
                afternoonDialogues.add("Halfway through the day and still going strong!");
                afternoonDialogues.add("Your farm is looking great. I can tell you've been putting in the effort.");
                afternoonDialogues.add("A good day's work makes for a good night's sleep, that's what I always say.");

                // Evening dialogues
                eveningDialogues.add("Another day of hard work complete. Well done!");
                eveningDialogues.add("Evening! Finished all your tasks for today?");
                eveningDialogues.add("Time to rest and recharge for tomorrow's work.");
                eveningDialogues.add("I admire your dedication. Don't work too late, though!");

                // Rainy dialogues
                rainyDialogues.add("Rain or shine, the work must go on.");
                rainyDialogues.add("This rain is perfect for indoor projects. I've got a list a mile long!");
                rainyDialogues.add("Don't let the rain slow you down. There's always work to be done.");
                rainyDialogues.add("I use rainy days to plan my next big projects. What about you?");

                // Friendship level dialogues
                level1Dialogues.add("I respect your work ethic. We should collaborate sometime.");
                level1Dialogues.add("You're not afraid of hard work. I like that about you.");
                level1Dialogues.add("I've noticed how much effort you put into your farm. It's paying off.");

                level2Dialogues.add("Your dedication to your craft is inspiring!");
                level2Dialogues.add("I've learned a lot from watching you work. You're quite skilled.");
                level2Dialogues.add("We make a good team, you and I. Both willing to put in the effort.");

                level3Dialogues.add("I consider you not just a friend, but a valued colleague.");
                level3Dialogues.add("Your hard work has truly transformed this valley. I'm proud to know you.");
                level3Dialogues.add("If I ever needed help with a big project, you'd be the first person I'd call.");
                break;

            case LAZY:
                // Morning dialogues
                morningDialogues.add("*yawn* Why are you up so early?");
                morningDialogues.add("Morning already? Five more minutes...");
                morningDialogues.add("You're way too energetic for this hour. How do you do it?");
                morningDialogues.add("I'm not really a morning person. Can we talk later?");

                // Afternoon dialogues
                afternoonDialogues.add("Finally, a reasonable hour to be awake.");
                afternoonDialogues.add("Hey there. Just taking my third break of the day.");
                afternoonDialogues.add("Life's too short to work all day, don't you think?");
                afternoonDialogues.add("I was just contemplating the perfect nap spot. Any suggestions?");

                // Evening dialogues
                eveningDialogues.add("The best time of day - when work is over!");
                eveningDialogues.add("Evening! Time to relax and do absolutely nothing.");
                eveningDialogues.add("If you're looking for me tomorrow, I'll be right here, doing as little as possible.");
                eveningDialogues.add("Ah, nighttime. When I can finally stop pretending to be busy.");

                // Rainy dialogues
                rainyDialogues.add("Perfect weather to stay inside and do nothing.");
                rainyDialogues.add("Rain is nature's way of telling us to take a day off.");
                rainyDialogues.add("I was planning to do nothing today, and now I have the perfect excuse!");
                rainyDialogues.add("Days like this were made for staying in bed. Why are you even out?");

                // Friendship level dialogues
                level1Dialogues.add("You're not so bad. At least you don't make me work too hard.");
                level1Dialogues.add("You know what? You're pretty chill. I can appreciate that.");
                level1Dialogues.add("Most people try to motivate me. I'm glad you just let me be.");

                level2Dialogues.add("I like hanging out with you. You're easy to be around.");
                level2Dialogues.add("If I had to exert energy for anyone, it would be for you.");
                level2Dialogues.add("You understand the art of doing nothing. That's rare these days.");

                level3Dialogues.add("You know what? You're my favorite person to relax with.");
                level3Dialogues.add("I've never met someone who gets me like you do. It's nice not having to pretend.");
                level3Dialogues.add("If I ever win the lottery, you're the first person I'm inviting to my 'do nothing' palace.");
                break;

            case JEALOUS:
                // Morning dialogues
                morningDialogues.add("I see you're doing well for yourself this morning.");
                morningDialogues.add("Your farm is looking nice. Mine would look better if I had your resources.");
                morningDialogues.add("Morning. I bet you slept well in that big farmhouse of yours.");
                morningDialogues.add("How do you always look so refreshed in the morning? It's annoying.");

                // Afternoon dialogues
                afternoonDialogues.add("Been productive today? Must be nice.");
                afternoonDialogues.add("I see you've got new equipment. Some people have all the luck.");
                afternoonDialogues.add("Your crops are growing well. Mine would too if I had your soil quality.");
                afternoonDialogues.add("Everyone's always talking about how great your farm is. It's not that special.");

                // Evening dialogues
                eveningDialogues.add("Your farm looks good. Mine could be better though.");
                eveningDialogues.add("Another successful day for the valley's favorite farmer, I see.");
                eveningDialogues.add("I worked twice as hard as you today, but no one will notice that.");
                eveningDialogues.add("Must be nice to end the day with so much accomplished. Some of us struggle more.");

                // Rainy dialogues
                rainyDialogues.add("I bet your crops love this rain. Mine are probably drowning.");
                rainyDialogues.add("Your farm is designed perfectly for this weather. Must be nice to have everything work out.");
                rainyDialogues.add("Even the rain seems to favor your land over mine.");
                rainyDialogues.add("Watch your farm thrive in this rain while the rest of us struggle.");

                // Friendship level dialogues
                level1Dialogues.add("I guess you're not as annoying as I thought.");
                level1Dialogues.add("For someone so successful, you're surprisingly down to earth.");
                level1Dialogues.add("I might have misjudged you. You're not just showing off all the time.");

                level2Dialogues.add("I actually kind of enjoy having you around.");
                level2Dialogues.add("You know, you could have let success go to your head, but you didn't. I respect that.");
                level2Dialogues.add("Maybe I could learn a thing or two from you. Not that I need much help.");

                level3Dialogues.add("Don't tell anyone, but you might be my best friend.");
                level3Dialogues.add("I used to be jealous of you, but now I just admire you. Don't let it go to your head though.");
                level3Dialogues.add("You've shown me that success isn't just about luck. Thanks for being patient with me.");
                break;

            case GREEDY:
                // Morning dialogues
                morningDialogues.add("Morning! Brought me anything good today?");
                morningDialogues.add("Early bird gets the gold! What treasures are you hunting today?");
                morningDialogues.add("Good morning! I hope your pockets are as full as your potential.");
                morningDialogues.add("Rise and shine! Time is money, and you're wasting both standing here.");

                // Afternoon dialogues
                afternoonDialogues.add("Hello! Any valuable finds in your adventures?");
                afternoonDialogues.add("Afternoon! I hope your day has been profitable so far.");
                afternoonDialogues.add("I've been counting my earnings all day. How about you?");
                afternoonDialogues.add("The best deals happen in the afternoon. Looking to trade?");

                // Evening dialogues
                eveningDialogues.add("Evening! How profitable was your day?");
                eveningDialogues.add("Another day, another dollar. Or hopefully many dollars in your case!");
                eveningDialogues.add("Night time is prime time for rare item hunting. Got any leads?");
                eveningDialogues.add("End of day tallies are my favorite. Let's compare earnings sometime.");

                // Rainy dialogues
                rainyDialogues.add("This rain is good for business, you know.");
                rainyDialogues.add("Rain means rare mushrooms are growing. There's money to be made!");
                rainyDialogues.add("Bad weather often leads to good fortune for those willing to brave it.");
                rainyDialogues.add("While others stay inside, the smart ones are out making money in this rain.");

                // Friendship level dialogues
                level1Dialogues.add("I've got some special deals for friends like you.");
                level1Dialogues.add("You've got a good eye for value. We could do business together.");
                level1Dialogues.add("Not everyone appreciates the finer things in life like we do.");

                level2Dialogues.add("You know, I could help you make more money with your farm.");
                level2Dialogues.add("I've been saving my best deals for someone I can trust. Interested?");
                level2Dialogues.add("Between you and me, there's a fortune to be made if we work together.");

                level3Dialogues.add("Partners in profit! That's what we are!");
                level3Dialogues.add("You're the only one I trust with my most valuable secrets. Let's get rich together.");
                level3Dialogues.add("I never thought I'd value a friendship more than gold, but here we are. Still, gold is nice too.");
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
        // If AI dialogue is enabled, use the Hugging Face API
        if (useAiDialogue) {
            try {
                // Create context information for the AI
                String context = createContextForAi(currentDate, friendshipLevel);

                // Call the AI service to generate dialogue
                return HuggingFaceApiClient.generateDialogue(this, context);
            } catch (Exception e) {
                System.err.println("Error using AI dialogue, falling back to predefined dialogues: " + e.getMessage());
                // If AI fails, fall back to predefined dialogues
                return getPreDefinedDialogue(currentDate, friendshipLevel);
            }
        } else {
            return getPreDefinedDialogue(currentDate, friendshipLevel);
        }
    }


    private String createContextForAi(Date currentDate, int friendshipLevel) {
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
        context.append("The NPC is ").append(character).append(". ");

        return context.toString();
    }


    private String getPreDefinedDialogue(Date currentDate, int friendshipLevel) {
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

        // Add weather-based dialogues
        try {
            if (App.getGame() != null && App.getGame().getDate() != null &&
                    App.getGame().getDate().getWeatherToday() == Weather.RAINY) {
                appropriateDialogues.addAll(rainyDialogues);
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not check weather, using default dialogues");
        }

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


    public boolean isUseAiDialogue() {
        return useAiDialogue;
    }


    public void setUseAiDialogue(boolean useAiDialogue) {
        this.useAiDialogue = useAiDialogue;
    }
}
