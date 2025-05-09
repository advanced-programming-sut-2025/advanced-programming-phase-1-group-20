package org.example.models.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.example.models.entities.NPC;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * Client for interacting with the Hugging Face API to generate AI-powered NPC dialogues.
 */
public class HuggingFaceApiClient {
    private static final String API_URL = "https://api-inference.huggingface.co/models/gpt2";
    private static final String API_KEY = "hf_...jmzX"; // Mock API key for testing
    private static final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private static final Gson gson = new Gson();


    public static String generateDialogue(NPC npc, String context) {
        try {
            String prompt = createPrompt(npc, context);
            String response = callHuggingFaceApi(prompt);
            return processResponse(response, npc);
        } catch (Exception e) {
            System.err.println("Error generating dialogue: " + e.getMessage());
            return "Hello there! [AI generation failed]";
        }
    }

    private static String createPrompt(NPC npc, String context) {
        StringBuilder promptBuilder = new StringBuilder();

        // Add NPC information to the prompt
        promptBuilder.append("NPC Name: ").append(npc.getName()).append("\n");
        promptBuilder.append("Personality: ").append(npc.getCharacter()).append("\n");
        promptBuilder.append("Job: ").append(npc.getJobs()).append("\n");
        promptBuilder.append("Context: ").append(context).append("\n\n");

        // Add instruction for the AI
        promptBuilder.append("Generate a short, friendly dialogue line for this NPC to say: ");

        return promptBuilder.toString();
    }

    private static String callHuggingFaceApi(String prompt) throws Exception {
        // Always use mock responses until we have a valid API key
        return createMockResponse(prompt);

        /* The following code is commented out until we have a valid API key
        // Check if we're in a test environment
        boolean isTestEnvironment = isTestEnvironment();

        if (isTestEnvironment) {
            // Return a mock response for testing
            return createMockResponse(prompt);
        }

        // Prepare the request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("inputs", prompt);
        requestBody.put("parameters", Map.of(
                "max_length", 50,
                "temperature", 0.7,
                "top_p", 0.9,
                "return_full_text", false
        ));

        String requestBodyJson = gson.toJson(requestBody);

        // Build the HTTP request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
                .build();

        // Send the request asynchronously
        CompletableFuture<HttpResponse<String>> responseFuture =
                client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        // Wait for the response
        HttpResponse<String> response = responseFuture.get();

        // Check if the request was successful
        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new RuntimeException("API call failed with status code: " + response.statusCode() +
                    ", response: " + response.body());
        }
        */
    }

    private static boolean isTestEnvironment() {
        try {
            return org.example.models.App.getGame() == null;
        } catch (Exception e) {
            return true; // If we can't access App.getGame(), assume we're in a test
        }
    }

    private static String createMockResponse(String prompt) {
        // Extract NPC information from the prompt
        String personality = "";
        if (prompt.contains("Personality: KIND")) {
            personality = "KIND";
        } else if (prompt.contains("Personality: HARD_WORKING")) {
            personality = "HARD_WORKING";
        } else if (prompt.contains("Personality: LAZY")) {
            personality = "LAZY";
        } else if (prompt.contains("Personality: JEALOUS")) {
            personality = "JEALOUS";
        } else if (prompt.contains("Personality: GREEDY")) {
            personality = "GREEDY";
        }

        // Extract time of day from the prompt
        String timeOfDay = "";
        if (prompt.contains("Time: Morning")) {
            timeOfDay = "Morning";
        } else if (prompt.contains("Time: Afternoon")) {
            timeOfDay = "Afternoon";
        } else if (prompt.contains("Time: Evening")) {
            timeOfDay = "Evening";
        }

        // Extract weather from the prompt
        String weather = "";
        if (prompt.contains("Weather: RAINY")) {
            weather = "rainy";
        } else if (prompt.contains("Weather: SUNNY")) {
            weather = "sunny";
        } else if (prompt.contains("Weather: SNOWY")) {
            weather = "snowy";
        } else {
            weather = "nice"; // Default
        }

        // Extract friendship level from the prompt
        int friendshipLevel = 1;
        if (prompt.contains("Friendship Level: 2")) {
            friendshipLevel = 2;
        } else if (prompt.contains("Friendship Level: 3")) {
            friendshipLevel = 3;
        }

        // Generate a varied response based on personality, time of day, weather, and friendship level
        String generatedText;

        switch (personality) {
            case "KIND":
                if (timeOfDay.equals("Morning")) {
                    generatedText = "Good morning! It's such a " + weather + " day today. I hope you have a wonderful day ahead!";
                } else if (timeOfDay.equals("Afternoon")) {
                    generatedText = "Hello there! How's your day going? I was just thinking about how nice it is to see friendly faces around town.";
                } else { // Evening
                    generatedText = "Good evening! The stars are beautiful tonight. It's always a pleasure to see you.";
                }
                break;

            case "HARD_WORKING":
                if (timeOfDay.equals("Morning")) {
                    generatedText = "Up early and ready to work? That's the spirit! I've already been up for hours getting things done.";
                } else if (timeOfDay.equals("Afternoon")) {
                    generatedText = "Taking a break from your farm? Good, but don't rest too long - there's always more work to be done!";
                } else { // Evening
                    generatedText = "Another productive day completed? Nothing feels better than a day of hard work well done.";
                }
                break;

            case "LAZY":
                if (timeOfDay.equals("Morning")) {
                    generatedText = "*yawn* Why are you up so early? The day doesn't really need to start until noon, you know.";
                } else if (timeOfDay.equals("Afternoon")) {
                    generatedText = "Hey there. Just taking my third break of the day. Life's too short to work all the time, right?";
                } else { // Evening
                    generatedText = "Evening already? Perfect time to relax and do absolutely nothing. Care to join me?";
                }
                break;

            case "JEALOUS":
                if (timeOfDay.equals("Morning")) {
                    generatedText = "I see your farm is doing well this " + weather + " morning. Must be nice to have everything work out so perfectly for you.";
                } else if (timeOfDay.equals("Afternoon")) {
                    generatedText = "Your crops are growing well. Mine would too if I had your advantages. Some people have all the luck.";
                } else { // Evening
                    generatedText = "Another successful day for the valley's favorite farmer, I see. Meanwhile, I worked twice as hard with half the results.";
                }
                break;

            case "GREEDY":
                if (timeOfDay.equals("Morning")) {
                    generatedText = "Morning! Brought me anything valuable today? Early bird gets the gold, as they say!";
                } else if (timeOfDay.equals("Afternoon")) {
                    generatedText = "Hello! Found any rare items on your adventures today? I'd be willing to make you an offer... for the right price.";
                } else { // Evening
                    generatedText = "Evening! How profitable was your day? Mine was quite lucrative, if I do say so myself.";
                }
                break;

            default:
                generatedText = "Hello! It's a " + weather + " " + timeOfDay.toLowerCase() + " today, isn't it?";
        }

        // Add friendship level flavor
        if (friendshipLevel >= 2) {
            generatedText += " You know, I'm really glad we've become friends.";
        }
        if (friendshipLevel >= 3) {
            generatedText += " You're one of my favorite people in this valley!";
        }

        // Add AI-generated tag for testing purposes
        generatedText += " [AI-generated dialogue for " + personality + " NPC at " + timeOfDay + "]";

        // Create a JSON response similar to what the API would return
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("generated_text", generatedText);

        return gson.toJson(jsonResponse);
    }

    /**
     * Processes the API response to extract the generated dialogue.
     */
    private static String processResponse(String responseJson, NPC npc) {
        try {
            // Parse the JSON response
            JsonObject jsonResponse = gson.fromJson(responseJson, JsonObject.class);

            // Extract the generated text
            String generatedText = jsonResponse.get("generated_text").getAsString();

            // Clean up the text (remove any unwanted artifacts)
            generatedText = generatedText.trim();

            // If the response is empty or too short, use a fallback
            if (generatedText.isEmpty() || generatedText.length() < 5) {
                return getFallbackDialogue(npc);
            }

            return generatedText;
        } catch (Exception e) {
            System.err.println("Error processing API response: " + e.getMessage());
            return getFallbackDialogue(npc);
        }
    }

    /**
     * Provides a fallback dialogue if the API call fails or returns invalid data.
     */
    private static String getFallbackDialogue(NPC npc) {
        switch (npc.getCharacter()) {
            case KIND:
                return "It's always a pleasure to see you around!";
            case HARD_WORKING:
                return "Hard work pays off, don't you think?";
            case LAZY:
                return "Why rush? Life is meant to be enjoyed slowly.";
            case JEALOUS:
                return "I see you're doing well for yourself...";
            case GREEDY:
                return "Got anything valuable to trade today?";
            default:
                return "Hello there! Nice weather we're having.";
        }
    }
}
