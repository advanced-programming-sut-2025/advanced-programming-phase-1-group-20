package org.example.common.Lobby;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Lobby {
    public enum LobbyStatus {
        WAITING,    // Waiting for players
        IN_GAME,    // Game is active
        CLOSED      // Lobby closed
    }

    private String id;                    // Random numeric ID
    private String name;                  // User-defined name
    private String adminId;               // Current admin player ID
    private List<LobbyPlayer> players;         // Current players (max 4)
    private LobbySettings settings;       // Privacy and visibility settings
    private String createdAt;      // Creation timestamp
    private String lastActivity;   // Last activity timestamp
    private LobbyStatus status;           // Current lobby status
    private String currentGameId;         // ID of current game if in progress

    // Default constructor
    public Lobby() {
        this.id = generateRandomId();
        this.players = new ArrayList<>();
        this.settings = new LobbySettings();
        this.createdAt = LocalDateTime.now().toString();
        this.lastActivity = LocalDateTime.now().toString();
        this.status = LobbyStatus.WAITING;
    }

    // Constructor with name and admin
    public Lobby(String name, String adminId) {
        this();
        this.name = name;
        this.adminId = adminId;
    }

    @JsonCreator
    public Lobby(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("adminId") String adminId,
        @JsonProperty("players") List<LobbyPlayer> players,
        @JsonProperty("settings") LobbySettings settings,
        @JsonProperty("createdAt") String createdAt,
        @JsonProperty("lastActivity") String lastActivity,
        @JsonProperty("status") LobbyStatus status,
        @JsonProperty("currentGameId") String currentGameId
    ) {
        this.id = id != null ? id : generateRandomId();
        this.name = name;
        this.adminId = adminId;
        this.players = players != null ? players : new ArrayList<>();
        this.settings = settings != null ? settings : new LobbySettings();
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now().toString();
        this.lastActivity = lastActivity != null ? lastActivity : LocalDateTime.now().toString();
        this.status = status != null ? status : LobbyStatus.WAITING;
        this.currentGameId = currentGameId;
    }

    // Generate random 6-digit numeric ID
    private static String generateRandomId() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    // Business logic methods
    public boolean canJoin() {
        return status == LobbyStatus.WAITING && players.size() < settings.getMaxPlayers();
    }

    public boolean isAdmin(String playerId) {
        return Objects.equals(adminId, playerId);
    }

    public LobbyPlayer getPlayerById(String playerId) {
        return players.stream()
                .filter(p -> Objects.equals(p.getId(), playerId))
                .findFirst()
                .orElse(null);
    }

    public void addPlayer(LobbyPlayer player) {
        if (canJoin()) {
            players.add(player);
            updateActivity();
        }
    }

    public void removePlayer(String playerId) {
        players.removeIf(p -> Objects.equals(p.getId(), playerId));

        // Transfer admin if needed
        if (Objects.equals(adminId, playerId) && !players.isEmpty()) {
            adminId = players.get(0).getId();
            players.get(0).setAdmin(true);
        }

        updateActivity();
    }

    public boolean isEmpty() {
        return players.isEmpty();
    }

    public int getPlayerCount() {
        return players.size();
    }

    public boolean canStartGame() {
        return status == LobbyStatus.WAITING && players.size() >= 2;
    }

    public void updateActivity() {
        this.lastActivity = LocalDateTime.now().toString();
    }

    public boolean isInactive(int timeoutMinutes) {
        return LocalDateTime.parse(lastActivity).isBefore(LocalDateTime.now().minusMinutes(timeoutMinutes));
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAdminId() { return adminId; }
    public void setAdminId(String adminId) { this.adminId = adminId; }

    public List<LobbyPlayer> getPlayers() { return players; }
    public void setPlayers(List<LobbyPlayer> players) { this.players = players; }

    public LobbySettings getSettings() { return settings; }
    public void setSettings(LobbySettings settings) { this.settings = settings; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getLastActivity() { return lastActivity; }
    public void setLastActivity(String lastActivity) { this.lastActivity = lastActivity; }

    public LobbyStatus getStatus() { return status; }
    public void setStatus(LobbyStatus status) { this.status = status; }

    public String getCurrentGameId() { return currentGameId; }
    public void setCurrentGameId(String currentGameId) { this.currentGameId = currentGameId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lobby lobby = (Lobby) o;
        return Objects.equals(id, lobby.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Lobby{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", adminId='" + adminId + '\'' +
                ", playerCount=" + players.size() +
                ", status=" + status +
                '}';
    }
}
