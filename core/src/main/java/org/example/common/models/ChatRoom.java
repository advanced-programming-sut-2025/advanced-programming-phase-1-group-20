package org.example.common.models;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatRoom {
    private String roomId;
    private String roomName;
    private List<String> participants;
    private List<ChatMessage> messageHistory;
    private boolean isPrivate;
    private String owner;

    public ChatRoom(String roomId, String roomName, String owner) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.owner = owner;
        this.participants = new CopyOnWriteArrayList<>();
        this.messageHistory = new CopyOnWriteArrayList<>();
        this.isPrivate = false;
        addParticipant(owner);
    }

    public ChatRoom(String roomId, String roomName, String owner, boolean isPrivate) {
        this(roomId, roomName, owner);
        this.isPrivate = isPrivate;
    }

    public void addParticipant(String username) {
        if (!participants.contains(username)) {
            participants.add(username);
        }
    }

    public void removeParticipant(String username) {
        participants.remove(username);
    }

    public void addMessage(ChatMessage message) {
        messageHistory.add(message);
        // Keep only last 100 messages to prevent memory issues
        if (messageHistory.size() > 100) {
            messageHistory.remove(0);
        }
    }

    public boolean hasParticipant(String username) {
        return participants.contains(username);
    }

    // Getters and Setters
    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public List<String> getParticipants() {
        return new ArrayList<>(participants);
    }

    public void setParticipants(List<String> participants) {
        this.participants = new CopyOnWriteArrayList<>(participants);
    }

    public List<ChatMessage> getMessageHistory() {
        return new ArrayList<>(messageHistory);
    }

    public void setMessageHistory(List<ChatMessage> messageHistory) {
        this.messageHistory = new CopyOnWriteArrayList<>(messageHistory);
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getParticipantCount() {
        return participants.size();
    }
}

