package org.example.common.Lobby;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Objects;

public class LobbyPlayer {
    public enum PlayerStatus {
        CONNECTED,      // Player is online and connected
        DISCONNECTED,   // Player is offline/disconnected
        IN_GAME,        // Player is currently in a game
        READY,          // Player is ready to start
        NOT_READY       // Player is not ready
    }

    private String id;                    // Unique player ID
    private String username;              // Display name
    private boolean isReady;              // Ready to start game
    private boolean isAdmin;              // Is lobby admin
    private PlayerStatus status;          // Current connection status
    private LocalDateTime lastSeen;       // Last activity timestamp
    private String lobbyId;               // Current lobby ID (if in lobby)
    private String gameId;                // Current game ID (if in game)

    // Default constructor
    public LobbyPlayer() {
        this.isReady = false;
        this.isAdmin = false;
        this.status = PlayerStatus.CONNECTED;
        this.lastSeen = LocalDateTime.now();
    }

    // Constructor with basic info
    public LobbyPlayer(String id, String username) {
        this();
        this.id = id;
        this.username = username;
    }

    @JsonCreator
    public LobbyPlayer(
        @JsonProperty("id") String id,
        @JsonProperty("username") String username,
        @JsonProperty("isReady") boolean isReady,
        @JsonProperty("isAdmin") boolean isAdmin,
        @JsonProperty("status") PlayerStatus status,
        @JsonProperty("lastSeen") LocalDateTime lastSeen,
        @JsonProperty("lobbyId") String lobbyId,
        @JsonProperty("gameId") String gameId
    ) {
        this.id = id;
        this.username = username;
        this.isReady = isReady;
        this.isAdmin = isAdmin;
        this.status = status != null ? status : PlayerStatus.CONNECTED;
        this.lastSeen = lastSeen != null ? lastSeen : LocalDateTime.now();
        this.lobbyId = lobbyId;
        this.gameId = gameId;
    }

    // Business logic methods
    public boolean isOnline() {
        return status == PlayerStatus.CONNECTED || status == PlayerStatus.READY || status == PlayerStatus.NOT_READY;
    }

    public boolean isInLobby() {
        return lobbyId != null && !lobbyId.isEmpty();
    }

    public boolean isInGame() {
        return status == PlayerStatus.IN_GAME || (gameId != null && !gameId.isEmpty());
    }

    public void updateActivity() {
        this.lastSeen = LocalDateTime.now();
        if (this.status == PlayerStatus.DISCONNECTED) {
            this.status = PlayerStatus.CONNECTED;
        }
    }

    public void setReady(boolean ready) {
        this.isReady = ready;
        this.status = ready ? PlayerStatus.READY : PlayerStatus.NOT_READY;
        updateActivity();
    }

    public void joinLobby(String lobbyId) {
        this.lobbyId = lobbyId;
        this.isReady = false;
        this.status = PlayerStatus.NOT_READY;
        updateActivity();
    }

    public void leaveLobby() {
        this.lobbyId = null;
        this.isReady = false;
        this.isAdmin = false;
        this.status = PlayerStatus.CONNECTED;
        updateActivity();
    }

    public void startGame(String gameId) {
        this.gameId = gameId;
        this.status = PlayerStatus.IN_GAME;
        updateActivity();
    }

    public void endGame() {
        this.gameId = null;
        this.status = isInLobby() ? PlayerStatus.NOT_READY : PlayerStatus.CONNECTED;
        updateActivity();
    }

    public void disconnect() {
        this.status = PlayerStatus.DISCONNECTED;
        this.lastSeen = LocalDateTime.now();
    }

    public boolean isInactive(int timeoutMinutes) {
        return lastSeen.isBefore(LocalDateTime.now().minusMinutes(timeoutMinutes));
    }

    // Create a copy for safe sharing
    public LobbyPlayer copy() {
        return new LobbyPlayer(id, username, isReady, isAdmin, status, lastSeen, lobbyId, gameId);
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public boolean isReady() { return isReady; }

    public boolean isAdmin() { return isAdmin; }
    public void setAdmin(boolean admin) { this.isAdmin = admin; }

    public PlayerStatus getStatus() { return status; }
    public void setStatus(PlayerStatus status) { this.status = status; }

    public LocalDateTime getLastSeen() { return lastSeen; }
    public void setLastSeen(LocalDateTime lastSeen) { this.lastSeen = lastSeen; }

    public String getLobbyId() { return lobbyId; }
    public void setLobbyId(String lobbyId) { this.lobbyId = lobbyId; }

    public String getGameId() { return gameId; }
    public void setGameId(String gameId) { this.gameId = gameId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LobbyPlayer player = (LobbyPlayer) o;
        return Objects.equals(id, player.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "LobbyPlayer{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", isReady=" + isReady +
                ", isAdmin=" + isAdmin +
                ", status=" + status +
                ", lobbyId='" + lobbyId + '\'' +
                '}';
    }
}
