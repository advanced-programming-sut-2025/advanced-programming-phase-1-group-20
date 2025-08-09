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
        promptBuilder.append("You are ").append(npc.getName()).append(", an NPC in a farming game similar to Stardew Valley.\n");
        promptBuilder.append("Your personality: ").append(npc.getCharacter()).append("\n");
        promptBuilder.append("Your job: ").append(npc.getJobs()).append("\n\n");
        
        // Add detailed context
        promptBuilder.append("Current Context:\n");
        promptBuilder.append(context).append("\n\n");
        
        // Add conversation guidelines
        promptBuilder.append("Instructions:\n");
        promptBuilder.append("- Generate ONE short, natural dialogue response (1-2 sentences)\n");
        promptBuilder.append("- Stay in character based on your personality\n");
        promptBuilder.append("- Reference the current context (time, weather, season) when appropriate\n");
        promptBuilder.append("- Use the COMPLETE conversation history to inform your response\n");
        promptBuilder.append("- Remember ALL previous conversations and topics discussed\n");
        promptBuilder.append("- Reference specific past interactions when relevant\n");
        promptBuilder.append("- Show how your relationship has evolved over time\n");
        promptBuilder.append("- Build upon ANY previous topics, stories, or shared experiences\n");
        promptBuilder.append("- Be friendly but maintain your unique personality\n");
        promptBuilder.append("- Don't repeat the same phrases from previous conversations\n");
        promptBuilder.append("- Make each response feel like part of an ongoing relationship\n\n");
        
        promptBuilder.append("Your response: ");

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

        // Debug: Print the response to see the actual structure
        System.out.println("API Response: " + response.body());
        System.out.println("Status Code: " + response.statusCode());

        JSONObject jsonResponse = new JSONObject(response.body());

        // Check if the response contains choices
        if (jsonResponse.has("choices")) {
            return jsonResponse.getJSONArray("choices")
                .getJSONObject(0).getJSONObject("message").getString("content");
        } else {
            // Handle different response structure or error
            if (jsonResponse.has("error")) {
                throw new Exception("API Error: " + jsonResponse.getJSONObject("error").getString("message"));
            } else {
                throw new Exception("Unexpected response format: " + response.body());
            }
        }
    }

    public static String getNpcDialogue(String message) {
        try {
            String response = getAIResponse(message);
            return response;
        } catch (Exception e) {
            System.err.println("Error getting NPC dialogue: " + e.getMessage());
            e.printStackTrace();
            return "Hello there! Nice to see you.";
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
