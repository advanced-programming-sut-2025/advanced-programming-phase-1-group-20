package org.example.server;

import org.example.common.Lobby.Lobby;
import org.example.common.Lobby.LobbyPlayer;
import org.example.common.Lobby.LobbySettings;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class LobbyManager {
    private static LobbyManager instance;
    private final Map<String, Lobby> lobbies;
    private final Map<String, String> playerToLobbyMap; // playerId -> lobbyId
    private final ScheduledExecutorService scheduler;
    private final ServerConfig config;

    // Constants
    private static final long LOBBY_TIMEOUT_MINUTES = 5;
    private static final long CLEANUP_INTERVAL_SECONDS = 60; // Check every minute

    private LobbyManager() {
        this.lobbies = new ConcurrentHashMap<>();
        this.playerToLobbyMap = new ConcurrentHashMap<>();
        this.config = ServerConfig.getInstance();
        this.scheduler = Executors.newScheduledThreadPool(2);

        // Start cleanup task
        startCleanupTask();

        System.out.println("LobbyManager initialized");
    }

    public static LobbyManager getInstance() {
        if (instance == null) {
            instance = new LobbyManager();
        }
        return instance;
    }

    // =====================
    // LOBBY CRUD OPERATIONS
    // =====================

    public Lobby createLobby(String lobbyName, String creatorId, LobbySettings settings) {
        System.out.println("DEBUG: LobbyManager.createLobby() called with name: " + lobbyName + ", creator: " + creatorId);
        Lobby lobby = new Lobby(lobbyName, creatorId);
        lobby.setSettings(settings);

        // Add creator as first player
        LobbyPlayer creator = new LobbyPlayer(creatorId, creatorId);
        creator.setAdmin(true);
        lobby.addPlayer(creator);

        // Store lobby
        lobbies.put(lobby.getId(), lobby);
        playerToLobbyMap.put(creatorId, lobby.getId());

        System.out.println("DEBUG: LobbyManager - Created lobby: " + lobby.getId() + " (" + lobbyName + ") by " + creatorId);
        System.out.println("DEBUG: LobbyManager - Total lobbies now: " + lobbies.size());
        return lobby;
    }

    public boolean joinLobby(String lobbyId, String playerId, String password) {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby == null) {
            return false;
        }

        // Check if lobby can be joined
        if (!lobby.canJoin()) {
            return false;
        }

        // Check password for private lobbies
        if (lobby.getSettings().requiresPassword()) {
            if (password == null || !lobby.getSettings().getPassword().equals(password)) {
                return false;
            }
        }

        // Remove player from any existing lobby
        leaveLobby(playerId);

        // Add player to lobby
        LobbyPlayer player = new LobbyPlayer(playerId, playerId);
        lobby.addPlayer(player);
        playerToLobbyMap.put(playerId, lobbyId);

        System.out.println("Player " + playerId + " joined lobby " + lobbyId);
        return true;
    }

    public boolean leaveLobby(String playerId) {
        String lobbyId = playerToLobbyMap.get(playerId);
        if (lobbyId == null) {
            return false;
        }

        Lobby lobby = lobbies.get(lobbyId);
        if (lobby == null) {
            playerToLobbyMap.remove(playerId);
            return false;
        }

        // Remove player from lobby
        lobby.removePlayer(playerId);
        playerToLobbyMap.remove(playerId);

        // Handle admin transfer or lobby closure
        handlePlayerLeave(lobby, playerId);

        System.out.println("Player " + playerId + " left lobby " + lobbyId);
        return true;
    }

    private void handlePlayerLeave(Lobby lobby, String playerId) {
        System.out.println("DEBUG: handlePlayerLeave called for lobby: " + lobby.getId() + ", leaving player: " + playerId);
        System.out.println("DEBUG: Was leaving player admin? " + lobby.isAdmin(playerId));
        System.out.println("DEBUG: Players before leave: " + lobby.getPlayers().size());
        
        if (lobby.getPlayers().isEmpty()) {
            // No players left, close lobby
            System.out.println("DEBUG: No players left, closing lobby");
            closeLobby(lobby.getId());
            return;
        }

        // If leaving player was admin, transfer admin to next player
        if (lobby.isAdmin(playerId)) {
            LobbyPlayer newAdmin = lobby.getPlayers().get(0);
            newAdmin.setAdmin(true);
            lobby.setAdminId(newAdmin.getId());
            System.out.println("DEBUG: Transferred admin in lobby " + lobby.getId() + " to " + newAdmin.getId());
            System.out.println("DEBUG: New admin details - ID: " + newAdmin.getId() + ", Username: " + newAdmin.getUsername() + ", IsAdmin: " + newAdmin.isAdmin());
        } else {
            System.out.println("DEBUG: Leaving player was not admin, no transfer needed");
        }
        
        System.out.println("DEBUG: Players after leave: " + lobby.getPlayers().size());
        System.out.println("DEBUG: Current admin ID: " + lobby.getAdminId());
    }

    public void closeLobby(String lobbyId) {
        Lobby lobby = lobbies.remove(lobbyId);
        if (lobby != null) {
            // Remove all players from mapping
            for (LobbyPlayer player : lobby.getPlayers()) {
                playerToLobbyMap.remove(player.getId());
            }

            lobby.setStatus(Lobby.LobbyStatus.CLOSED);
            System.out.println("Closed lobby: " + lobbyId);
        }
    }

    // =====================
    // LOBBY QUERIES
    // =====================

    public List<Lobby> getVisibleLobbies() {
        return lobbies.values().stream()
                .filter(lobby -> lobby.getSettings().isVisible())
                .filter(lobby -> lobby.getStatus() == Lobby.LobbyStatus.WAITING)
                .collect(Collectors.toList());
    }

    public Lobby getLobbyById(String lobbyId) {
        return lobbies.get(lobbyId);
    }

    public Lobby getLobbyByPlayerId(String playerId) {
        String lobbyId = playerToLobbyMap.get(playerId);
        return lobbyId != null ? lobbies.get(lobbyId) : null;
    }

    public List<Lobby> searchLobbiesByName(String searchTerm) {
        String lowerSearchTerm = searchTerm.toLowerCase();
        return getVisibleLobbies().stream()
                .filter(lobby -> lobby.getName().toLowerCase().contains(lowerSearchTerm))
                .collect(Collectors.toList());
    }

    public boolean isPlayerInLobby(String playerId) {
        return playerToLobbyMap.containsKey(playerId);
    }

    // =====================
    // LOBBY ACTIONS
    // =====================

    public boolean setPlayerReady(String playerId, boolean ready) {
        Lobby lobby = getLobbyByPlayerId(playerId);
        if (lobby == null) {
            return false;
        }

        LobbyPlayer player = lobby.getPlayerById(playerId);
        if (player != null) {
            player.setReady(ready);
            lobby.updateActivity();
            return true;
        }

        return false;
    }

    public boolean canStartGame(String lobbyId, String requestingPlayerId) {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby == null) {
            return false;
        }

        // Only admin can start game
        if (!lobby.isAdmin(requestingPlayerId)) {
            return false;
        }

        // Need at least 2 players
        if (lobby.getPlayers().size() < 2) {
            return false;
        }

        return true;
    }

    public boolean startGame(String lobbyId, String requestingPlayerId) {
        if (!canStartGame(lobbyId, requestingPlayerId)) {
            return false;
        }

        Lobby lobby = lobbies.get(lobbyId);
        lobby.setStatus(Lobby.LobbyStatus.IN_GAME);
        lobby.updateActivity();

        System.out.println("Started game in lobby " + lobbyId);
        return true;
    }

    // =====================
    // CLEANUP AND MAINTENANCE
    // =====================

    private void startCleanupTask() {
        scheduler.scheduleAtFixedRate(this::cleanupInactiveLobbies,
                CLEANUP_INTERVAL_SECONDS, CLEANUP_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    private void cleanupInactiveLobbies() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(LOBBY_TIMEOUT_MINUTES);
        List<String> lobbieToClose = new ArrayList<>();

        for (Lobby lobby : lobbies.values()) {
            if (lobby.getStatus() == Lobby.LobbyStatus.WAITING) {
                LocalDateTime lastActivity = LocalDateTime.parse(lobby.getLastActivity());
                if (lastActivity.isBefore(cutoffTime)) {
                    lobbieToClose.add(lobby.getId());
                }
            }
        }

        for (String lobbyId : lobbieToClose) {
            System.out.println("Closing inactive lobby: " + lobbyId);
            closeLobby(lobbyId);
        }
    }

    public void updateLobbyActivity(String lobbyId) {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby != null) {
            lobby.updateActivity();
        }
    }

    // =====================
    // STATISTICS
    // =====================

    public int getTotalLobbies() {
        return lobbies.size();
    }

    public int getActiveLobbies() {
        return (int) lobbies.values().stream()
                .filter(lobby -> lobby.getStatus() == Lobby.LobbyStatus.WAITING)
                .count();
    }

    public int getTotalPlayersInLobbies() {
        return lobbies.values().stream()
                .mapToInt(lobby -> lobby.getPlayers().size())
                .sum();
    }

    // =====================
    // SHUTDOWN
    // =====================

    public void shutdown() {
        scheduler.shutdown();
        lobbies.clear();
        playerToLobbyMap.clear();
        System.out.println("LobbyManager shut down");
    }
}
