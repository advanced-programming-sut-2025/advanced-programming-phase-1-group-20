package org.example.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.example.models.entities.NPC;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;


public class HuggingFaceApiClient {
    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final String API_KEY = "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";
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
        // Prepare the request body
        String body = "{\"model\": \"deepseek/deepseek-chat-v3-0324:free\",\n" +
                "  \"messages\": [\n" +
                "    {\n" +
                "      \"role\": \"user\",\n" +
                "      \"content\": \"" + prompt + "\"\n" +
                "    }\n" +
                "  ]}";

        // Build the HTTP request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        System.out.println("Sending request: " + request);
        // Send the request asynchronously
        CompletableFuture<HttpResponse<String>> responseFuture =
                client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        // Wait for the response
        HttpResponse<String> response = responseFuture.get();

//        System.out.println(response.body());
        // Check if the request was successful
        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new RuntimeException("API call failed with status code: " + response.statusCode() +
                    ", response: " + response.body());
        }

    }

    private static boolean isTestEnvironment() {
        try {
            return org.example.models.App.getGame() == null;
        } catch (Exception e) {
            return true; // If we can't access App.getGame(), assume we're in a test
        }
    }


    private static String processResponse(String responseJson, NPC npc) {
        try {
            // Parse the JSON response
//            System.out.println(responseJson);
            JsonObject json = gson.fromJson(responseJson, JsonObject.class);
            String generatedText = json
                    .getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();
            // Extract the generated text

            // Clean up the text (remove any unwanted artifacts)
            generatedText = generatedText.trim();

            // If the response is empty or too short, use a fallback
            if (generatedText.length() < 5) {
                return getFallbackDialogue(npc);
            }

            return generatedText;
        } catch (Exception e) {
            System.err.println("Error processing API response: " + e.getMessage());
            e.printStackTrace();
            return getFallbackDialogue(npc);
        }
    }


    private static String getFallbackDialogue(NPC npc) {
        return switch (npc.getCharacter()) {
            case KIND -> "It's always a pleasure to see you around!";
            case HARD_WORKING -> "Hard work pays off, don't you think?";
            case LAZY -> "Why rush? Life is meant to be enjoyed slowly.";
            case JEALOUS -> "I see you're doing well for yourself...";
            case GREEDY -> "Got anything valuable to trade today?";
            default -> "Hello there! Nice weather we're having.";
        };
    }
}
