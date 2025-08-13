package org.example.server;

import com.google.gson.Gson;
import org.example.common.models.ChatMessage;
import org.example.common.models.ChatRoom;
import org.example.common.models.Message;
import org.example.common.models.entities.User;
import org.example.server.GameServers.PlayerConnection;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatManager {
    private static ChatManager instance;
    private final MessageHandler messageHandler;
    private final Gson gson = new Gson();
    
    // Chat rooms storage
    private final Map<String, ChatRoom> chatRooms = new ConcurrentHashMap<>();
    private final List<ChatMessage> publicChatHistory = new CopyOnWriteArrayList<>();
    private final Map<String, List<ChatMessage>> privateChatHistory = new ConcurrentHashMap<>();
    
    // Player connections storage
    private final Map<String, PlayerConnection> playerConnections = new ConcurrentHashMap<>();
    
    // Default public chat room
    private static final String PUBLIC_CHAT_ROOM_ID = "public";
    private static final String PUBLIC_CHAT_ROOM_NAME = "Public Chat";
    
    private ChatManager(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
        // Create default public chat room
        createChatRoom(PUBLIC_CHAT_ROOM_ID, PUBLIC_CHAT_ROOM_NAME, "System");
    }
    
    public static ChatManager getInstance(MessageHandler messageHandler) {
        if (instance == null) {
            instance = new ChatManager(messageHandler);
        }
        return instance;
    }
    
    public void registerPlayer(String username, PlayerConnection connection) {
        // Store connection for chat functionality
        playerConnections.put(username, connection);
        // Add player to public chat room
        ChatRoom publicRoom = chatRooms.get(PUBLIC_CHAT_ROOM_ID);
        if (publicRoom != null) {
            publicRoom.addParticipant(username);
        }
        // System.out.println("[CHAT] Registered player: " + username);
    }
    
    public void unregisterPlayer(String username) {
        playerConnections.remove(username);
        // Remove player from all chat rooms
        for (ChatRoom room : chatRooms.values()) {
            room.removeParticipant(username);
        }
        // System.out.println("[CHAT] Unregistered player: " + username);
    }
    
    public void handlePublicChat(String sender, String content) {
        ChatMessage message = new ChatMessage(sender, content, ChatMessage.ChatType.PUBLIC);
        publicChatHistory.add(message);
        
        // Keep only last 100 messages
        if (publicChatHistory.size() > 100) {
            publicChatHistory.remove(0);
        }
        
        // Send to all online players
        broadcastMessage(message);
        
        // System.out.println("[PUBLIC CHAT] " + sender + ": " + content);
    }
    
    public void handlePrivateChat(String sender, String recipient, String content) {
        ChatMessage message = new ChatMessage(sender, content, ChatMessage.ChatType.PRIVATE, recipient);
        
        // Store in private chat history
        String chatKey = getPrivateChatKey(sender, recipient);
        privateChatHistory.computeIfAbsent(chatKey, k -> new CopyOnWriteArrayList<>()).add(message);
        
        // Keep only last 50 messages per private chat
        List<ChatMessage> history = privateChatHistory.get(chatKey);
        if (history.size() > 50) {
            history.remove(0);
        }
        
        // Send to recipient
        PlayerConnection recipientConnection = playerConnections.get(recipient);
        if (recipientConnection != null) {
            messageHandler.sendPrivateMessage(recipientConnection, message);
        }
        
        // Send back to sender for confirmation
        PlayerConnection senderConnection = playerConnections.get(sender);
        if (senderConnection != null) {
            messageHandler.sendPrivateMessage(senderConnection, message);
        }
        
        // System.out.println("[PRIVATE CHAT] " + sender + " -> " + recipient + ": " + content);
    }
    
    public void handleRoomChat(String sender, String roomId, String content) {
        ChatRoom room = chatRooms.get(roomId);
        if (room == null) {
            return;
        }
        
        if (!room.hasParticipant(sender)) {
            return;
        }
        
        ChatMessage message = new ChatMessage(sender, content, ChatMessage.ChatType.ROOM, null, roomId);
        room.addMessage(message);
        
        // Send to all participants in the room
        for (String participant : room.getParticipants()) {
            PlayerConnection connection = playerConnections.get(participant);
            if (connection != null) {
                messageHandler.sendRoomMessage(connection, message);
            }
        }
        
        // System.out.println("[ROOM CHAT] [" + roomId + "] " + sender + ": " + content);
    }
    
    public void createChatRoom(String roomId, String roomName, String owner) {
        if (chatRooms.containsKey(roomId)) {
            return;
        }
        
        ChatRoom room = new ChatRoom(roomId, roomName, owner);
        chatRooms.put(roomId, room);
        
        // Notify all players about new room
        broadcastRoomCreated(room);
        
        // System.out.println("[CHAT ROOM] Created: " + roomName + " (ID: " + roomId + ") by " + owner);
    }
    
    public void joinChatRoom(String username, String roomId) {
        ChatRoom room = chatRooms.get(roomId);
        if (room == null) {
            return;
        }
        
        room.addParticipant(username);
        
        // Send room history to the joining player
        PlayerConnection connection = playerConnections.get(username);
        if (connection != null) {
            messageHandler.sendRoomHistory(connection, room);
        }
        
        // System.out.println("[CHAT ROOM] " + username + " joined " + room.getRoomName());
    }
    
    public void leaveChatRoom(String username, String roomId) {
        ChatRoom room = chatRooms.get(roomId);
        if (room == null) {
            return;
        }
        
        room.removeParticipant(username);
        
        // System.out.println("[CHAT ROOM] " + username + " left " + room.getRoomName());
    }
    
    public List<ChatMessage> getPublicChatHistory() {
        return new ArrayList<>(publicChatHistory);
    }
    
    public List<ChatMessage> getPrivateChatHistory(String user1, String user2) {
        String chatKey = getPrivateChatKey(user1, user2);
        List<ChatMessage> history = privateChatHistory.get(chatKey);
        return history != null ? new ArrayList<>(history) : new ArrayList<>();
    }
    
    public List<ChatRoom> getAvailableRooms() {
        return new ArrayList<>(chatRooms.values());
    }
    
    public List<String> getOnlinePlayers() {
        // Get players from both ChatManager and OnlinePlayersManager
        List<String> usernames = new ArrayList<>();
        
        // Add players from ChatManager's own connections
        for (String username : playerConnections.keySet()) {
            if (playerConnections.get(username) != null) {
                usernames.add(username);
            }
        }
        
        // Also add players from OnlinePlayersManager to ensure we get all online players
        OnlinePlayersManager onlinePlayersManager = OnlinePlayersManager.getInstance();
        List<OnlinePlayersManager.OnlinePlayerInfo> onlinePlayers = onlinePlayersManager.getOnlinePlayers();
        for (OnlinePlayersManager.OnlinePlayerInfo playerInfo : onlinePlayers) {
            if (!usernames.contains(playerInfo.getUsername())) {
                usernames.add(playerInfo.getUsername());
            }
        }
        
        return usernames;
    }
    
    private void broadcastMessage(ChatMessage message) {
        Message networkMessage = new Message();
        networkMessage.setType(Message.Type.CHAT);
        networkMessage.putInBody("sender", message.getSender());
        networkMessage.putInBody("content", message.getContent());
        networkMessage.putInBody("timestamp", System.currentTimeMillis());
        networkMessage.putInBody("type", message.getType().toString());
        
        for (PlayerConnection connection : playerConnections.values()) {
            connection.sendMessage(networkMessage);
        }
    }
    
    private void broadcastRoomCreated(ChatRoom room) {
        for (PlayerConnection connection : playerConnections.values()) {
            messageHandler.sendRoomCreatedNotification(connection, room);
        }
    }
    
    private String getPrivateChatKey(String user1, String user2) {
        // Create a consistent key for private chat regardless of sender/recipient order
        return user1.compareTo(user2) < 0 ? user1 + "_" + user2 : user2 + "_" + user1;
    }
}
