package org.example.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.example.common.models.App;
import org.example.common.models.entities.NPC;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class npcAI {
    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final String API_KEY = "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";
    private static final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static String generateDialogue(NPC npc, String context) {
        try {
            String prompt = createPrompt(npc, context);
            return getNpcDialogue(prompt);
        } catch (Exception e) {
            System.err.println("Error generating dialogue: " + e.getMessage());
            return getFallbackDialogue(npc);
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

    public static String getAIResponse(String userMessage) throws Exception {
        JSONObject payload = new JSONObject();
        payload.put("model", "deepseek/deepseek-chat-v3-0324:free");
        JSONArray arr = new JSONArray();
        JSONObject msg = new JSONObject();
        msg.put("role", "user");
        msg.put("content", userMessage);
        arr.put(0, msg);
        payload.put("messages", arr);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(API_URL))
            .header("Authorization", "Bearer " + API_KEY)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JSONObject jsonResponse = new JSONObject(response.body());
        return jsonResponse.getJSONArray("choices")
            .getJSONObject(0).getJSONObject("message").getString("content");
    }

    public static String getNpcDialogue(String message) {
        try {
            String response = getAIResponse(message);
            return response;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Fuck this life.";
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
