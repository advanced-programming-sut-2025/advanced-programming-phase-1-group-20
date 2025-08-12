package org.example.server;

import org.example.common.models.ChatMessage;
import org.example.common.models.ChatRoom;
import org.example.common.models.Notification;
import org.example.server.GameServers.PlayerConnection;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatManager {
    private static ChatManager instance;
    private final Map<String, ChatRoom> chatRooms;
    private final Map<String, List<ChatMessage>> publicChatHistory;
    private final Map<String, List<ChatMessage>> privateChatHistory;
    private final MessageHandler messageHandler;
    private final OnlinePlayersManager onlinePlayersManager;
    private final Map<String, PlayerConnection> playerConnections;
    private static final String PUBLIC_CHAT_ID = "public";
    private static final int MAX_PUBLIC_MESSAGES = 200;
    private static final int MAX_PRIVATE_MESSAGES = 100;

    private ChatManager(MessageHandler messageHandler) {
        this.chatRooms = new ConcurrentHashMap<>();
        this.publicChatHistory = new ConcurrentHashMap<>();
        this.privateChatHistory = new ConcurrentHashMap<>();
        this.messageHandler = messageHandler;
        this.onlinePlayersManager = OnlinePlayersManager.getInstance();
        this.playerConnections = new ConcurrentHashMap<>();
        
        // Initialize public chat
        initializePublicChat();
    }

    public static ChatManager getInstance(MessageHandler messageHandler) {
        if (instance == null) {
            instance = new ChatManager(messageHandler);
        }
        return instance;
    }

    private void initializePublicChat() {
        publicChatHistory.put(PUBLIC_CHAT_ID, new ArrayList<>());
    }

    public void handlePublicChat(String sender, String message) {
        ChatMessage chatMessage = new ChatMessage(sender, message, ChatMessage.ChatType.PUBLIC);
        
        // Add to public chat history
        List<ChatMessage> history = publicChatHistory.get(PUBLIC_CHAT_ID);
        history.add(chatMessage);
        
        // Keep only last MAX_PUBLIC_MESSAGES
        if (history.size() > MAX_PUBLIC_MESSAGES) {
            history.remove(0);
        }

        // Broadcast to all online players
        broadcastPublicMessage(chatMessage);
        
        // Send notification to all players
        sendChatNotification(chatMessage);
    }

    public void handlePrivateChat(String sender, String recipient, String message) {
        ChatMessage chatMessage = new ChatMessage(sender, message, ChatMessage.ChatType.PRIVATE, recipient);
        
        // Store in private chat history
        String chatKey = getPrivateChatKey(sender, recipient);
        List<ChatMessage> history = privateChatHistory.computeIfAbsent(chatKey, k -> new ArrayList<>());
        history.add(chatMessage);
        
        // Keep only last MAX_PRIVATE_MESSAGES
        if (history.size() > MAX_PRIVATE_MESSAGES) {
            history.remove(0);
        }

        // Send to recipient if online
        PlayerConnection recipientConnection = playerConnections.get(recipient);
        if (recipientConnection != null) {
            messageHandler.sendPrivateMessage(recipientConnection, chatMessage);
            
            // Send notification
            Notification notification = new Notification(
                "Private Message",
                sender + ": " + message,
                Notification.NotificationType.PRIVATE_MESSAGE,
                Notification.NotificationPriority.HIGH,
                recipient
            );
            notification.setSender(sender);
            messageHandler.sendNotification(recipientConnection, notification);
        }

        // Send back to sender for confirmation
        PlayerConnection senderConnection = playerConnections.get(sender);
        if (senderConnection != null) {
            messageHandler.sendPrivateMessage(senderConnection, chatMessage);
        }
    }

    public void handleRoomChat(String sender, String roomId, String message) {
        ChatRoom room = chatRooms.get(roomId);
        if (room != null && room.hasParticipant(sender)) {
            ChatMessage chatMessage = new ChatMessage(sender, message, ChatMessage.ChatType.ROOM, null, roomId);
            room.addMessage(chatMessage);
            
            // Broadcast to all participants
            broadcastRoomMessage(chatMessage, room);
        }
    }

    public void createChatRoom(String roomId, String roomName, String owner) {
        ChatRoom room = new ChatRoom(roomId, roomName, owner);
        chatRooms.put(roomId, room);
        
        // Notify owner
        PlayerConnection ownerConnection = playerConnections.get(owner);
        if (ownerConnection != null) {
            messageHandler.sendRoomCreatedNotification(ownerConnection, room);
        }
    }

    public void joinChatRoom(String username, String roomId) {
        ChatRoom room = chatRooms.get(roomId);
        if (room != null) {
            room.addParticipant(username);
            
            // Send room history to new participant
            PlayerConnection connection = playerConnections.get(username);
            if (connection != null) {
                messageHandler.sendRoomHistory(connection, room);
            }
        }
    }

    public void leaveChatRoom(String username, String roomId) {
        ChatRoom room = chatRooms.get(roomId);
        if (room != null) {
            room.removeParticipant(username);
        }
    }

    public List<ChatMessage> getPublicChatHistory() {
        return new ArrayList<>(publicChatHistory.get(PUBLIC_CHAT_ID));
    }

    public List<ChatMessage> getPrivateChatHistory(String user1, String user2) {
        String chatKey = getPrivateChatKey(user1, user2);
        List<ChatMessage> history = privateChatHistory.get(chatKey);
        return history != null ? new ArrayList<>(history) : new ArrayList<>();
    }

    public List<ChatRoom> getAvailableRooms() {
        return new ArrayList<>(chatRooms.values());
    }

    private void broadcastPublicMessage(ChatMessage message) {
        for (String player : playerConnections.keySet()) {
            PlayerConnection connection = playerConnections.get(player);
            if (connection != null) {
                messageHandler.sendChatMessage(connection, message);
            }
        }
    }

    private void broadcastRoomMessage(ChatMessage message, ChatRoom room) {
        for (String participant : room.getParticipants()) {
            PlayerConnection connection = playerConnections.get(participant);
            if (connection != null) {
                messageHandler.sendRoomMessage(connection, message);
            }
        }
    }

    private void sendChatNotification(ChatMessage message) {
        // Send notification to all online players for public chat
        for (String player : playerConnections.keySet()) {
            if (!player.equals(message.getSender())) {
                Notification notification = new Notification(
                    "New Chat Message",
                    message.getSender() + ": " + message.getContent(),
                    Notification.NotificationType.CHAT_MESSAGE,
                    Notification.NotificationPriority.LOW,
                    player
                );
                notification.setSender(message.getSender());
                
                PlayerConnection connection = playerConnections.get(player);
                if (connection != null) {
                    messageHandler.sendNotification(connection, notification);
                }
            }
        }
    }

    private String getPrivateChatKey(String user1, String user2) {
        // Create a consistent key for private chat history
        return user1.compareTo(user2) < 0 ? user1 + "_" + user2 : user2 + "_" + user1;
    }
}
