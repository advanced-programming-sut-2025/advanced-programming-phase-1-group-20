package org.example.common.Lobby;

import org.example.common.models.Message;

import java.time.LocalDateTime;

public class LobbyMessage extends Message {
    public enum LobbyMessageType {
        // Lobby management
        LOBBY_CREATED,
        LOBBY_JOINED,
        LOBBY_LEFT,
        LOBBY_UPDATED,
        LOBBY_CLOSED,

        // Player actions
        PLAYER_JOINED,
        PLAYER_LEFT,
        PLAYER_READY,
        PLAYER_NOT_READY,
        PLAYER_KICKED,

        // Admin actions
        ADMIN_CHANGED,
        GAME_STARTING,
        GAME_STARTED,

        // Errors
        JOIN_FAILED,
        INVALID_PASSWORD,
        LOBBY_FULL,
        LOBBY_NOT_FOUND
    }

    // Constructor with lobby-specific data
    public LobbyMessage(LobbyMessageType lobbyType, String lobbyId, String playerId) {
        super();
        putInBody("lobbyType", lobbyType.name());
        putInBody("lobbyId", lobbyId);
        putInBody("playerId", playerId);
        putInBody("timestamp", LocalDateTime.now().toString());
    }

    // Default constructor
    public LobbyMessage() {
        super();
    }

    // Factory methods for common message types
    public static LobbyMessage lobbyCreated(String lobbyId, String playerId, Lobby lobby) {
        LobbyMessage message = new LobbyMessage(LobbyMessageType.LOBBY_CREATED, lobbyId, playerId);
        message.setType(Type.CREATE_GAME); // Use existing enum
        message.putInBody("lobby", lobby);
        return message;
    }

    public static LobbyMessage playerJoined(String lobbyId, String playerId, LobbyPlayer player) {
        LobbyMessage message = new LobbyMessage(LobbyMessageType.PLAYER_JOINED, lobbyId, playerId);
        message.setType(Type.JOIN_GAME); // Use existing enum
        message.putInBody("player", player);
        return message;
    }

    public static LobbyMessage playerLeft(String lobbyId, String playerId) {
        LobbyMessage message = new LobbyMessage(LobbyMessageType.PLAYER_LEFT, lobbyId, playerId);
        message.setType(Type.LEAVE_GAME); // Use existing enum
        return message;
    }

    public static LobbyMessage playerReady(String lobbyId, String playerId, boolean ready) {
        LobbyMessage message = new LobbyMessage(
            ready ? LobbyMessageType.PLAYER_READY : LobbyMessageType.PLAYER_NOT_READY,
            lobbyId, playerId
        );
        message.setType(Type.PLAYER_UPDATE); // Use existing enum
        message.putInBody("ready", ready);
        return message;
    }

    public static LobbyMessage adminChanged(String lobbyId, String newAdminId) {
        LobbyMessage message = new LobbyMessage(LobbyMessageType.ADMIN_CHANGED, lobbyId, newAdminId);
        message.setType(Type.GAME_STATE_UPDATE); // Use existing enum
        message.putInBody("newAdminId", newAdminId);
        return message;
    }

    public static LobbyMessage gameStarting(String lobbyId, String adminId) {
        LobbyMessage message = new LobbyMessage(LobbyMessageType.GAME_STARTING, lobbyId, adminId);
        message.setType(Type.START_GAME); // Use existing enum
        return message;
    }

    public static LobbyMessage lobbyUpdated(String lobbyId, Lobby lobby) {
        LobbyMessage message = new LobbyMessage(LobbyMessageType.LOBBY_UPDATED, lobbyId, null);
        message.setType(Type.GAME_STATE_UPDATE); // Use existing enum
        message.putInBody("lobby", lobby);
        return message;
    }

    public static LobbyMessage error(String lobbyId, String playerId, LobbyMessageType errorType, String reason) {
        LobbyMessage message = new LobbyMessage(errorType, lobbyId, playerId);
        message.setType(Type.ERROR); // Use existing enum
        message.putInBody("error", reason);
        return message;
    }

    public static LobbyMessage ping(String lobbyId, String playerId) {
        LobbyMessage message = new LobbyMessage(LobbyMessageType.LOBBY_UPDATED, lobbyId, playerId);
        message.setType(Type.PING); // Use existing enum
        return message;
    }

    public static LobbyMessage pong(String lobbyId, String playerId) {
        LobbyMessage message = new LobbyMessage(LobbyMessageType.LOBBY_UPDATED, lobbyId, playerId);
        message.setType(Type.PONG); // Use existing enum
        return message;
    }

    // Convenience getters that work with the body HashMap
    public String getLobbyType() {
        return getFromBody("lobbyType");
    }

    public String getLobbyId() {
        return getFromBody("lobbyId");
    }

    public String getPlayerId() {
        return getFromBody("playerId");
    }

    public Lobby getLobbyData() {
        return getFromBody("lobby");
    }

    public LobbyPlayer getPlayerData() {
        return getFromBody("player");
    }

    public String getReason() {
        return getFromBody("error");
    }

    public boolean isReady() {
        Boolean ready = getFromBody("ready");
        return ready != null ? ready : false;
    }

    public String getNewAdminId() {
        return getFromBody("newAdminId");
    }

    @Override
    public String toString() {
        return "LobbyMessage{" +
                "type=" + getType() +
                ", lobbyType='" + getLobbyType() + '\'' +
                ", lobbyId='" + getLobbyId() + '\'' +
                ", playerId='" + getPlayerId() + '\'' +
                ", body=" + getBody() +
                '}';
    }
}
