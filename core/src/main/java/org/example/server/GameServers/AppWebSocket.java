package org.example.server.GameServers;

import java.util.concurrent.ConcurrentHashMap;

public class AppWebSocket {
    private static ConcurrentHashMap<String, PlayerConnection> connectedPlayers = new ConcurrentHashMap<>();

}
