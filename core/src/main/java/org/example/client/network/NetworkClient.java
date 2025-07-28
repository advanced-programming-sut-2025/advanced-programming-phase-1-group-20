package org.example.client.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.common.models.Message;
import org.example.common.models.entities.User;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Queue;

public class NetworkClient {
    private static NetworkClient instance;
    private java.net.http.WebSocket webSocket;
    private java.net.http.WebSocket.Builder webSocketBuilder;
    private final Queue<Message> incomingMessages;
    private final Queue<Message> outgoingMessages;
    private final Gson gson;
    private ClientMessageHandler messageHandler;
    private ConnectionState connectionState;
    private User authenticatedUser;
    private String serverHost;
    private int serverPort;
    private String sessionId;
    private Thread networkThread;
    private volatile boolean isRunning = false;

    public enum ConnectionState {
        DISCONNECTED,
        CONNECTING,
        CONNECTED,
        AUTHENTICATED,
        IN_GAME,
        ERROR
    }

    private NetworkClient() {
        this.incomingMessages = new ConcurrentLinkedQueue<>();
        this.outgoingMessages = new ConcurrentLinkedQueue<>();
        this.gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, (com.google.gson.TypeAdapter<LocalDateTime>) new com.google.gson.TypeAdapter<LocalDateTime>() {
                @Override
                public void write(com.google.gson.stream.JsonWriter out, LocalDateTime value) throws IOException {
                    out.value(value.toString());
                }

                @Override
                public LocalDateTime read(com.google.gson.stream.JsonReader in) throws IOException {
                    return LocalDateTime.parse(in.nextString());
                }
            })
            .serializeSpecialFloatingPointValues()
            .create();
        this.connectionState = ConnectionState.DISCONNECTED;
        this.messageHandler = new ClientMessageHandler(this);

        // Default server settings
        this.serverHost = "localhost";
        this.serverPort = 8080;
    }

    public static NetworkClient getInstance() {
        if (instance == null) {
            instance = new NetworkClient();
        }
        return instance;
    }

    public void setServerAddress(String host, int port) {
        this.serverHost = host;
        this.serverPort = port;
    }

    public boolean connect() {
        if (connectionState == ConnectionState.CONNECTED || connectionState == ConnectionState.CONNECTING) {
            return false; // Already connected or connecting
        }

        try {
            connectionState = ConnectionState.CONNECTING;

            // Create WebSocket URI
            String wsUri = "ws://" + serverHost + ":" + serverPort + "/ws/game";
            System.out.println("Attempting WebSocket connection to: " + wsUri);

            // Use Java 11+ WebSocket client
            java.net.http.HttpClient httpClient = java.net.http.HttpClient.newHttpClient();
            webSocketBuilder = httpClient.newWebSocketBuilder();

            // Create WebSocket listener
            java.net.http.WebSocket.Listener listener = new java.net.http.WebSocket.Listener() {
                @Override
                public void onOpen(java.net.http.WebSocket webSocket) {
                    System.out.println("WebSocket connected successfully!");
                    connectionState = ConnectionState.CONNECTED;
                    java.net.http.WebSocket.Listener.super.onOpen(webSocket);
                }

                @Override
                public java.util.concurrent.CompletionStage<?> onText(java.net.http.WebSocket webSocket, CharSequence data, boolean last) {
                    String messageText = data.toString();
                    System.out.println("Received: " + messageText);

                    try {
                        // Check if message is complete and valid JSON
                        if (messageText == null || messageText.trim().isEmpty()) {
                            System.err.println("Received empty message");
                            return java.net.http.WebSocket.Listener.super.onText(webSocket, data, last);
                        }

                        // Try to parse the JSON message
                        Message message = gson.fromJson(messageText, Message.class);
                        
                        // Validate the parsed message
                        if (message == null) {
                            System.err.println("Failed to parse message: result is null");
                            return java.net.http.WebSocket.Listener.super.onText(webSocket, data, last);
                        }
                        
                        if (message.getType() == null) {
                            System.err.println("Failed to parse message: message type is null");
                            return java.net.http.WebSocket.Listener.super.onText(webSocket, data, last);
                        }

                        incomingMessages.offer(message);

                        // Handle session ID from welcome message
                        if (message.getType() == Message.Type.SUCCESS) {
                            String receivedSessionId = message.getFromBody("sessionId");
                            if (receivedSessionId != null) {
                                sessionId = receivedSessionId;
                                System.out.println("Received session ID: " + sessionId);
                            }
                        }
                    } catch (com.google.gson.JsonSyntaxException e) {
                        System.err.println("JSON syntax error in message: " + e.getMessage());
                        System.err.println("Problematic message: " + messageText);
                        // Don't throw the exception, just log it and continue
                    } catch (Exception e) {
                        System.err.println("Failed to parse incoming message: " + e.getMessage());
                        System.err.println("Problematic message: " + messageText);
                        e.printStackTrace();
                        // Don't throw the exception, just log it and continue
                    }

                    return java.net.http.WebSocket.Listener.super.onText(webSocket, data, last);
                }

                @Override
                public void onError(java.net.http.WebSocket webSocket, Throwable error) {
                    String errorMessage = "WebSocket error: " + error.getMessage();
                    System.err.println(errorMessage);
                    setLastErrorMessage(errorMessage);
                    connectionState = ConnectionState.ERROR;
                    java.net.http.WebSocket.Listener.super.onError(webSocket, error);
                }

                @Override
                public java.util.concurrent.CompletionStage<?> onClose(java.net.http.WebSocket webSocket, int statusCode, String reason) {
                    System.out.println("WebSocket closed: " + statusCode + " - " + reason);
                    connectionState = ConnectionState.DISCONNECTED;
                    return java.net.http.WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
                }
            };

            // Connect to WebSocket
            webSocket = webSocketBuilder.buildAsync(URI.create(wsUri), listener)
                .get(5, java.util.concurrent.TimeUnit.SECONDS);

            // Start network thread for processing outgoing messages
            startNetworkThread();

            System.out.println("WebSocket connection established!");
            return true;

        } catch (java.util.concurrent.TimeoutException e) {
            String errorMessage = "Connection timeout: Server did not respond within 5 seconds";
            System.err.println(errorMessage);
            setLastErrorMessage(errorMessage);
            connectionState = ConnectionState.ERROR;
            return false;
        } catch (Exception e) {
            String errorMessage = "Failed to connect to server";
            if (e.getMessage() != null) {
                if (e.getMessage().contains("Connection refused")) {
                    errorMessage = "Connection refused: Server is not running or not accessible at " + serverHost + ":" + serverPort;
                } else if (e.getMessage().contains("Unknown host")) {
                    errorMessage = "Unknown host: Cannot resolve server address " + serverHost;
                } else if (e.getMessage().contains("Network is unreachable")) {
                    errorMessage = "Network is unreachable: Check your network connection and firewall settings";
                } else if (e.getMessage().contains("No route to host")) {
                    errorMessage = "No route to host: The server address " + serverHost + " is not reachable";
                } else {
                    errorMessage = "Connection error: " + e.getMessage();
                }
            }
            System.err.println(errorMessage);
            e.printStackTrace();
            setLastErrorMessage(errorMessage);
            connectionState = ConnectionState.ERROR;
            return false;
        }
    }

    private void startNetworkThread() {
        isRunning = true;
        networkThread = new Thread(() -> {
            while (isRunning) {
                try {
                    processOutgoingMessages();
                    Thread.sleep(50); // Process messages 20 times per second
                } catch (InterruptedException e) {
                    System.out.println("Network thread interrupted");
                    break;
                } catch (Exception e) {
                    System.err.println("Error in network thread: " + e.getMessage());
                }
            }
        }, "NetworkClient-Thread");
        networkThread.setDaemon(true);
        networkThread.start();
    }

    private void processOutgoingMessages() {
        // Send real messages via WebSocket
        while (!outgoingMessages.isEmpty() && webSocket != null) {
            Message message = outgoingMessages.poll();
            if (message != null) {
                try {
                    String messageJson = gson.toJson(message);
                    System.out.println("DEBUG: processOutgoingMessages - Sending: " + messageJson);

                    // Send via WebSocket
                    webSocket.sendText(messageJson, true);
                    System.out.println("DEBUG: processOutgoingMessages - Message sent via WebSocket");

                } catch (Exception e) {
                    System.err.println("DEBUG: processOutgoingMessages - Failed to send message: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendMessage(Message message) {
        System.out.println("DEBUG: sendMessage() called with type: " + message.getType() + ", connectionState: " + connectionState);
        if (connectionState == ConnectionState.CONNECTED ||
            connectionState == ConnectionState.AUTHENTICATED) {
            outgoingMessages.offer(message);
            System.out.println("DEBUG: Message added to outgoing queue. Queue size: " + outgoingMessages.size());
        } else {
            System.out.println("DEBUG: Cannot send message: not connected to server (state: " + connectionState + ")");
            System.err.println("Cannot send message: not connected to server (state: " + connectionState + ")");
        }
    }

    public boolean authenticate(User user, String jwtToken) {
        if (connectionState != ConnectionState.CONNECTED) {
            System.err.println("Cannot authenticate: not connected to server");
            return false;
        }

        // Set authenticated user BEFORE sending message
        this.authenticatedUser = user;

        Message authMessage = new Message();
        authMessage.setType(Message.Type.AUTH_LOGIN);
        authMessage.putInBody("username", user.getUsername());
        authMessage.putInBody("token", jwtToken);

        sendMessage(authMessage);

        System.out.println("Authentication request sent for user: " + user.getUsername());
        return true;
    }

    public void sendPlayerMove(float x, float y) {
        if (connectionState != ConnectionState.AUTHENTICATED) {
            return;
        }

        Message moveMessage = new Message();
        moveMessage.setType(Message.Type.PLAYER_MOVE);
        moveMessage.putInBody("x", x);
        moveMessage.putInBody("y", y);
        moveMessage.putInBody("username", authenticatedUser.getUsername());
        moveMessage.putInBody("timestamp", System.currentTimeMillis());

        sendMessage(moveMessage);
    }

    public void sendChatMessage(String messageText) {
        if (connectionState != ConnectionState.AUTHENTICATED) {
            return;
        }

        Message chatMessage = new Message();
        chatMessage.setType(Message.Type.CHAT);
        chatMessage.putInBody("sender", authenticatedUser.getUsername());
        chatMessage.putInBody("message", messageText);
        chatMessage.putInBody("timestamp", System.currentTimeMillis());

        sendMessage(chatMessage);
    }

    public void sendTradeRequest(String targetPlayer, String item, int quantity) {
        if (connectionState != ConnectionState.AUTHENTICATED) {
            return;
        }

        Message tradeMessage = new Message();
        tradeMessage.setType(Message.Type.TRADE_REQUEST);
        tradeMessage.putInBody("fromPlayer", authenticatedUser.getUsername());
        tradeMessage.putInBody("toPlayer", targetPlayer);
        tradeMessage.putInBody("item", item);
        tradeMessage.putInBody("quantity", quantity);
        tradeMessage.putInBody("timestamp", System.currentTimeMillis());

        sendMessage(tradeMessage);
    }

    public void createGame() {
        if (connectionState != ConnectionState.AUTHENTICATED) {
            return;
        }

        Message createGameMessage = new Message();
        createGameMessage.setType(Message.Type.CREATE_GAME);
        createGameMessage.putInBody("creator", authenticatedUser.getUsername());
        createGameMessage.putInBody("timestamp", System.currentTimeMillis());

        sendMessage(createGameMessage);
    }

    public void createLobby(String lobbyName, boolean isPrivate, boolean isVisible, String password) {
        System.out.println("DEBUG: createLobby() called with name: " + lobbyName + ", isPrivate: " + isPrivate + ", isVisible: " + isVisible);
        if (connectionState != ConnectionState.AUTHENTICATED) {
            System.out.println("DEBUG: Not authenticated, cannot create lobby. ConnectionState: " + connectionState);
            System.err.println("Cannot create lobby: not authenticated (state: " + connectionState + ")");
            return;
        }

        System.out.println("DEBUG: Creating CREATE_LOBBY message");
        Message createLobbyMessage = new Message();
        createLobbyMessage.setType(Message.Type.CREATE_LOBBY);
        createLobbyMessage.putInBody("lobbyName", lobbyName != null ? lobbyName : authenticatedUser.getUsername() + "'s Lobby");
        createLobbyMessage.putInBody("isPrivate", isPrivate);
        createLobbyMessage.putInBody("isVisible", isVisible);
        if (isPrivate && password != null) {
            createLobbyMessage.putInBody("password", password);
        }
        createLobbyMessage.putInBody("timestamp", System.currentTimeMillis());

        System.out.println("DEBUG: About to send CREATE_LOBBY message");
        sendMessage(createLobbyMessage);
        System.out.println("DEBUG: CREATE_LOBBY message sent: " + lobbyName);
    }

    public void joinGame(String gameId) {
        if (connectionState != ConnectionState.AUTHENTICATED) {
            return;
        }

        Message joinGameMessage = new Message();
        joinGameMessage.setType(Message.Type.JOIN_GAME);
        joinGameMessage.putInBody("gameId", gameId);
        joinGameMessage.putInBody("username", authenticatedUser.getUsername());
        joinGameMessage.putInBody("timestamp", System.currentTimeMillis());

        sendMessage(joinGameMessage);
    }

    public void leaveGame() {
        if (connectionState != ConnectionState.AUTHENTICATED) {
            return;
        }

        Message leaveGameMessage = new Message();
        leaveGameMessage.setType(Message.Type.LEAVE_GAME);
        leaveGameMessage.putInBody("username", authenticatedUser.getUsername());
        leaveGameMessage.putInBody("timestamp", System.currentTimeMillis());

        sendMessage(leaveGameMessage);
    }

    public void requestLobbyList() {
        if (connectionState != ConnectionState.AUTHENTICATED) {
            return;
        }

        Message listLobbiesMessage = new Message();
        listLobbiesMessage.setType(Message.Type.LIST_LOBBIES);
        listLobbiesMessage.putInBody("timestamp", System.currentTimeMillis());

        sendMessage(listLobbiesMessage);
        System.out.println("DEBUG: Lobby list request sent");
    }

    public void update() {
        // Process incoming messages on main thread
        while (!incomingMessages.isEmpty()) {
            Message message = incomingMessages.poll();
            if (message != null) {
                messageHandler.handleMessage(message);
            }
        }
    }

    public void disconnect() {
        isRunning = false;
        connectionState = ConnectionState.DISCONNECTED;

        if (networkThread != null) {
            networkThread.interrupt();
            try {
                networkThread.join(1000); // Wait up to 1 second
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        if (webSocket != null) {
            try {
                webSocket.sendClose(java.net.http.WebSocket.NORMAL_CLOSURE, "Client disconnecting");
            } catch (Exception e) {
                System.err.println("Error closing WebSocket: " + e.getMessage());
            }
            webSocket = null;
        }

        System.out.println("Disconnected from server");
    }

    // Getters
    public ConnectionState getConnectionState() {
        return connectionState;
    }

    public void setConnectionState(ConnectionState state) {
        this.connectionState = state;
    }

    public User getAuthenticatedUser() {
        return authenticatedUser;
    }

    public String getSessionId() {
        return sessionId;
    }

    public boolean isConnected() {
        return connectionState == ConnectionState.CONNECTED ||
            connectionState == ConnectionState.AUTHENTICATED ||
            connectionState == ConnectionState.IN_GAME;
    }

    public boolean isAuthenticated() {
        return connectionState == ConnectionState.AUTHENTICATED ||
            connectionState == ConnectionState.IN_GAME;
    }

    public void setMessageHandler(ClientMessageHandler handler) {
        this.messageHandler = handler;
    }

    public ClientMessageHandler getMessageHandler() {
        return messageHandler;
    }

    private String lastErrorMessage = "Network connection error";

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    private void setLastErrorMessage(String errorMessage) {
        this.lastErrorMessage = errorMessage;
    }
}
