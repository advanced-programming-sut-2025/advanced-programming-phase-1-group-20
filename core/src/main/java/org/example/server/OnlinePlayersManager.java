package org.example.server;

import org.example.common.models.*;
import org.example.server.GameServers.PlayerConnection;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class OnlinePlayersManager {
    private static OnlinePlayersManager instance;

    // Online players with their connection info
    private final Map<String, OnlinePlayerInfo> onlinePlayers;
    private final Map<String, PlayerConnection> playerConnections;

    private OnlinePlayersManager() {
        this.onlinePlayers = new ConcurrentHashMap<>();
        this.playerConnections = new ConcurrentHashMap<>();
    }

    public static OnlinePlayersManager getInstance() {
        if (instance == null) {
            instance = new OnlinePlayersManager();
        }
        return instance;
    }

    public void playerConnected(String username, PlayerConnection connection) {
        OnlinePlayerInfo playerInfo = new OnlinePlayerInfo(username);
        playerInfo.setConnectionTime(LocalDateTime.now().toString());
        playerInfo.setStatus(OnlinePlayerInfo.PlayerStatus.ONLINE);

        onlinePlayers.put(username, playerInfo);
        playerConnections.put(username, connection);

        System.out.println("Player " + username + " is now online");

        // Notify all other players
        broadcastPlayerStatusUpdate();
    }

    public void playerDisconnected(String username) {
        onlinePlayers.remove(username);
        playerConnections.remove(username);

        System.out.println("Player " + username + " went offline");

        // Notify all remaining players
        broadcastPlayerStatusUpdate();
    }

    public void playerJoinedLobby(String username, String lobbyId, String lobbyName) {
        OnlinePlayerInfo playerInfo = onlinePlayers.get(username);
        if (playerInfo != null) {
            playerInfo.setLobbyId(lobbyId);
            playerInfo.setLobbyName(lobbyName);
            playerInfo.setStatus(OnlinePlayerInfo.PlayerStatus.IN_LOBBY);

            System.out.println("Player " + username + " joined lobby: " + lobbyName);
            broadcastPlayerStatusUpdate();
        }
    }

    public void playerLeftLobby(String username) {
        OnlinePlayerInfo playerInfo = onlinePlayers.get(username);
        if (playerInfo != null) {
            playerInfo.setLobbyId(null);
            playerInfo.setLobbyName(null);
            playerInfo.setStatus(OnlinePlayerInfo.PlayerStatus.ONLINE);

            System.out.println("Player " + username + " left lobby");
            broadcastPlayerStatusUpdate();
        }
    }

    public void playerInGame(String username, String gameSessionId) {
        OnlinePlayerInfo playerInfo = onlinePlayers.get(username);
        if (playerInfo != null) {
            playerInfo.setGameSessionId(gameSessionId);
            playerInfo.setStatus(OnlinePlayerInfo.PlayerStatus.IN_GAME);

            System.out.println("Player " + username + " started game");
            broadcastPlayerStatusUpdate();
        }
    }

    // =====================
    // QUERIES
    // =====================

    public List<OnlinePlayerInfo> getOnlinePlayers() {
        return new ArrayList<>(onlinePlayers.values());
    }

    public int getOnlinePlayerCount() {
        return onlinePlayers.size();
    }

    public boolean isPlayerOnline(String username) {
        return onlinePlayers.containsKey(username);
    }

    public OnlinePlayerInfo getPlayerInfo(String username) {
        return onlinePlayers.get(username);
    }

    // =====================
    // BROADCASTING
    // =====================

    public void broadcastPlayerStatusUpdate() {
        List<OnlinePlayerInfo> playersList = getOnlinePlayers();

        Message updateMessage = new Message();
        updateMessage.setType(Message.Type.ONLINE_PLAYERS_LIST);
        updateMessage.putInBody("players", playersList);
        updateMessage.putInBody("timestamp", System.currentTimeMillis());

        // Send to all connected players
        for (PlayerConnection connection : playerConnections.values()) {
            if (connection != null) {
                connection.sendMessage(updateMessage);
            }
        }

        System.out.println("Broadcasted player list update to " + playerConnections.size() + " players");
    }

    public void sendPlayerListTo(String username) {
        PlayerConnection connection = playerConnections.get(username);
        if (connection != null) {
            List<OnlinePlayerInfo> playersList = getOnlinePlayers();

            Message listMessage = new Message();
            listMessage.setType(Message.Type.ONLINE_PLAYERS_LIST);
            listMessage.putInBody("players", playersList);
            listMessage.putInBody("timestamp", System.currentTimeMillis());

            connection.sendMessage(listMessage);
        }
    }

    public static class OnlinePlayerInfo {
        public enum PlayerStatus {
            ONLINE,      // Connected but not in lobby/game
            IN_LOBBY,    // In a lobby
            IN_GAME      // Currently playing
        }

        private String username;
        private PlayerStatus status;
        private String lobbyId;
        private String lobbyName;
        private String gameSessionId;
        private String connectionTime;

        public OnlinePlayerInfo() {}

        public OnlinePlayerInfo(String username) {
            this.username = username;
            this.status = PlayerStatus.ONLINE;
            this.connectionTime = LocalDateTime.now().toString();
        }

        // Getters and Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public PlayerStatus getStatus() { return status; }
        public void setStatus(PlayerStatus status) { this.status = status; }

        public String getLobbyId() { return lobbyId; }
        public void setLobbyId(String lobbyId) { this.lobbyId = lobbyId; }

        public String getLobbyName() { return lobbyName; }
        public void setLobbyName(String lobbyName) { this.lobbyName = lobbyName; }

        public String getGameSessionId() { return gameSessionId; }
        public void setGameSessionId(String gameSessionId) { this.gameSessionId = gameSessionId; }

        public String getConnectionTime() { return connectionTime; }
        public void setConnectionTime(String connectionTime) { this.connectionTime = connectionTime; }

        @Override
        public String toString() {
            return "OnlinePlayerInfo{" +
                    "username='" + username + '\'' +
                    ", status=" + status +
                    ", lobbyName='" + lobbyName + '\'' +
                    '}';
        }
    }
}
