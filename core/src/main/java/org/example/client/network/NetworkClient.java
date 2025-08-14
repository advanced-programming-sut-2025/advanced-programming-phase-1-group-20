package org.example.client.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.common.models.Message;
import org.example.common.models.entities.User;

import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Queue;
import java.util.Map;

public class NetworkClient {
    private static NetworkClient instance;
    private WebSocket webSocket;
    private WebSocket.Builder webSocketBuilder;
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

    // Add message buffer for handling fragmented WebSocket messages
    private StringBuilder messageBuffer = new StringBuilder();
    private boolean isInJsonObject = false;
    private int braceCount = 0;

    // Enhanced reconnection logic
    private long disconnectTime = 0;
    private static final long RECONNECTION_TIMEOUT_MS = 120000; // 2 minutes
    private static final long RECONNECTION_ATTEMPT_DELAY_MS = 2000; // 2 seconds between attempts
    private Thread reconnectionThread;
    private volatile boolean isReconnecting = false;
    private String lastGameSessionId = null; // Store game session for reconnection
    private boolean wasInGame = false; // Track if player was in game when disconnected

    public enum ConnectionState {
        DISCONNECTED,
        CONNECTING,
        CONNECTED,
        AUTHENTICATED,
        IN_GAME,
        RECONNECTING,
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
        this.serverPort = 8008;
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

            System.out.println("Creating HTTP client...");
            HttpClient httpClient = HttpClient.newHttpClient();
            webSocketBuilder = httpClient.newWebSocketBuilder();
            System.out.println("WebSocket builder created successfully");

            // Add WebSocket headers for better compatibility
            webSocketBuilder.header("User-Agent", "StardewValley-Client/1.0");
            System.out.println("WebSocket headers configured");

            // Create WebSocket listener
            WebSocket.Listener listener = new WebSocket.Listener() {
                @Override
                public void onOpen(WebSocket webSocket) {
                    System.out.println("WebSocket connected successfully!");
                    connectionState = ConnectionState.CONNECTED;
                    WebSocket.Listener.super.onOpen(webSocket);
                }

                @Override
                public java.util.concurrent.CompletionStage<?> onText(java.net.http.WebSocket webSocket, CharSequence data, boolean last) {
                    String messageFragment = data.toString();
                    System.out.println("NETWORK: Received message fragment: " + messageFragment);

                    // Add fragment to buffer
                    messageBuffer.append(messageFragment);

                    // Check if we have a complete JSON message
                    String completeMessage = messageBuffer.toString();
                    if (isCompleteJsonMessage(completeMessage)) {
                        System.out.println("NETWORK: Complete message received: " + completeMessage);

                        try {
                            Message receivedMessage = gson.fromJson(completeMessage, Message.class);
                            System.out.println("NETWORK: Successfully parsed message of type: " + receivedMessage.getType());

                            // Add specific debug for PLAYER_DATA_UPDATE
                            if (receivedMessage.getType() == Message.Type.PLAYER_DATA_UPDATE) {
                                System.out.println("NETWORK: PLAYER_DATA_UPDATE message detected!");
                                Object playersData = receivedMessage.getFromBody("players");
                                if (playersData != null) {
                                    System.out.println("NETWORK: PLAYER_DATA_UPDATE contains players data: " + playersData);
                                }
                            }

                            if (receivedMessage.getType() == Message.Type.SUCCESS) {
                                String messageText = receivedMessage.getFromBody("message");
                                if (messageText != null && messageText.contains("Authentication successful")) {
                                    connectionState = ConnectionState.AUTHENTICATED;
                                    System.out.println("NETWORK: Authentication successful, state set to: " + connectionState);
                                }
                            }

                            incomingMessages.offer(receivedMessage);
                            System.out.println("🔄 NETWORK: Added message to incoming queue, queue size: " + incomingMessages.size());
                        } catch (Exception e) {
                            System.err.println("Error processing incoming message: " + e.getMessage());
                            e.printStackTrace();
                        }

                        // Reset buffer after processing complete message
                        messageBuffer.setLength(0);
                        isInJsonObject = false;
                        braceCount = 0;
                    } else {
                        System.out.println("📥 NETWORK: Message fragment buffered, waiting for complete message...");
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
                    System.out.println("NETWORK: WebSocket closed - Status: " + statusCode + ", Reason: " + reason);

                    if (statusCode != 1000) { // Not a normal closure
                        System.out.println("NETWORK: Abnormal WebSocket closure, starting enhanced reconnection process...");
                        connectionState = ConnectionState.DISCONNECTED;

                        // Start enhanced reconnection process with 2-minute timeout
                        startReconnectionProcess();
                    } else {
                        connectionState = ConnectionState.DISCONNECTED;
                    }

                    return java.net.http.WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
                }
            };

            // Connect to WebSocket with longer timeout
            System.out.println("Building WebSocket connection...");
            webSocket = webSocketBuilder.buildAsync(URI.create(wsUri), listener)
                .get(10, java.util.concurrent.TimeUnit.SECONDS);

            // Start network thread for processing outgoing messages
            startNetworkThread();

            System.out.println("WebSocket connection established!");
            return true;

        } catch (java.util.concurrent.TimeoutException e) {
            String errorMessage = "Connection timeout: Server did not respond within 10 seconds";
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

    private void startReconnectionProcess() {
        if (isReconnecting) {
            System.out.println("NETWORK: Reconnection already in progress");
            return;
        }

        disconnectTime = System.currentTimeMillis();
        isReconnecting = true;

        // Check if player was in game before disconnection
        if (connectionState == ConnectionState.IN_GAME) {
            wasInGame = true;
            System.out.println("🎮 NETWORK: Player was in game, will attempt to restore game session");
        }

        connectionState = ConnectionState.RECONNECTING;

        System.out.println("NETWORK: Starting reconnection process with 2-minute timeout");

        reconnectionThread = new Thread(() -> {
            int attemptCount = 0;

            while (isReconnecting && (System.currentTimeMillis() - disconnectTime) < RECONNECTION_TIMEOUT_MS) {
                attemptCount++;
                System.out.println("NETWORK: Reconnection attempt " + attemptCount + " (time remaining: " +
                    ((RECONNECTION_TIMEOUT_MS - (System.currentTimeMillis() - disconnectTime)) / 1000) + "s)");

                try {
                    // Attempt to reconnect
                    boolean success = attemptReconnection();

                    if (success) {
                        System.out.println("NETWORK: Reconnection successful!");
                        isReconnecting = false;

                        // Restore game state if needed
                        if (wasInGame && lastGameSessionId != null) {
                            System.out.println("NETWORK: Restoring game session: " + lastGameSessionId);
                            restoreGameSession();
                        }

                        return;
                    } else {
                        System.out.println("NETWORK: Reconnection attempt " + attemptCount + " failed");
                    }

                    // Wait before next attempt
                    Thread.sleep(RECONNECTION_ATTEMPT_DELAY_MS);

                } catch (InterruptedException e) {
                    System.out.println("NETWORK: Reconnection thread interrupted");
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println("NETWORK: Error during reconnection attempt: " + e.getMessage());
                }
            }

            // Reconnection timeout or failed
            if (isReconnecting) {
                System.out.println("NETWORK: Reconnection timeout after 2 minutes");
                handleReconnectionTimeout();
            }
        }, "NetworkClient-Reconnection");

        reconnectionThread.setDaemon(true);
        reconnectionThread.start();
    }

    /**
     * Attempt to reconnect to the server
     */
    private boolean attemptReconnection() {
        try {
            // Set server address and attempt connection
            setServerAddress(serverHost, serverPort);
            boolean connected = connect();

            if (connected && authenticatedUser != null) {
                // Re-authenticate the user
                System.out.println("NETWORK: Re-authenticating user: " + authenticatedUser.getUsername());
                boolean authenticated = authenticate(authenticatedUser, authenticatedUser.getJwtToken());

                if (authenticated) {
                    System.out.println("NETWORK: Re-authentication successful");
                    return true;
                } else {
                    System.out.println("NETWORK: Re-authentication failed");
                    return false;
                }
            }

            return connected;
        } catch (Exception e) {
            System.err.println("NETWORK: Reconnection attempt failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Restore game session after successful reconnection
     */
    private void restoreGameSession() {
        if (lastGameSessionId != null) {
            // Send a rejoin game message to the server
            Message rejoinMessage = new Message();
            rejoinMessage.setType(Message.Type.REJOIN_GAME);
            rejoinMessage.putInBody("gameSessionId", lastGameSessionId);
            rejoinMessage.putInBody("username", authenticatedUser.getUsername());
            rejoinMessage.putInBody("timestamp", System.currentTimeMillis());

            sendMessage(rejoinMessage);
            System.out.println("NETWORK: Sent rejoin game message for session: " + lastGameSessionId);
        }
    }

    private void handleReconnectionTimeout() {
        isReconnecting = false;
        connectionState = ConnectionState.ERROR;
        wasInGame = false;
        lastGameSessionId = null;

        System.out.println("NETWORK: Reconnection timeout - returning to main menu");

        // Notify the UI about the timeout
        if (messageHandler != null) {
            messageHandler.onReconnectionTimeout();
        }
    }


    public void setGameSessionId(String gameSessionId) {
        this.lastGameSessionId = gameSessionId;
        System.out.println("🎮 NETWORK: Stored game session ID for reconnection: " + gameSessionId);
    }

    public boolean isReconnecting() {
        return isReconnecting;
    }


    public long getRemainingReconnectionTime() {
        if (!isReconnecting) {
            return 0;
        }
        long remaining = RECONNECTION_TIMEOUT_MS - (System.currentTimeMillis() - disconnectTime);
        return Math.max(0, remaining / 1000);
    }


    public void cancelReconnection() {
        isReconnecting = false;
        wasInGame = false;
        lastGameSessionId = null;

        if (reconnectionThread != null && reconnectionThread.isAlive()) {
            reconnectionThread.interrupt();
        }

        System.out.println("NETWORK: Reconnection cancelled by user");
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

                    // Send via WebSocket
                    if (message.getType() != null) {
                        String t = message.getType().name();
                        if (t.startsWith("TRADE_")) {
                            System.out.println("**CLIENT WS SEND** type=" + message.getType() + " json=" + messageJson);
                        } else if (t.startsWith("RADIO_")) {
                            System.out.println("**CLIENT WS SEND** type=" + message.getType() + " json=" + messageJson);
                        }
                    }
                    webSocket.sendText(messageJson, true);

                } catch (Exception e) {
                    System.err.println("Failed to send message: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendMessage(Message message) {
        if (webSocket == null) {
            System.err.println("❌ NETWORK: Cannot send message - WebSocket is null");
            return;
        }

        if (!webSocket.isOutputClosed()) {
            try {
                String messageJson = gson.toJson(message);
//                System.out.println("DEBUG: Sending message JSON: " + messageJson);
                outgoingMessages.offer(message);
                if (message.getType() != null) {
                    String t = message.getType().name();
                    if (t.startsWith("TRADE_")) {
                        System.out.println("**CLIENT QUEUE** type=" + message.getType() + " json=" + messageJson);
                    } else if (t.startsWith("RADIO_")) {
                        System.out.println("**CLIENT QUEUE** type=" + message.getType() + " json=" + messageJson);
                    }
                }
//                System.out.println("DEBUG: Message added to outgoing queue");
            } catch (Exception e) {
                System.err.println("NETWORK: Failed to serialize message: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println("NETWORK: Cannot send message - WebSocket output is closed");
        }
    }

    public boolean authenticate(User user, String jwtToken) {
        if (connectionState != ConnectionState.CONNECTED) {
            System.err.println("Cannot authenticate: not connected to server (state: " + connectionState + ")");
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
        if (connectionState != ConnectionState.AUTHENTICATED && connectionState != ConnectionState.IN_GAME) {
            System.out.println("NETWORK: Cannot send movement - not authenticated. State: " + connectionState);
            return;
        }

        if (authenticatedUser == null) {
            System.out.println("NETWORK: Cannot send movement - no authenticated user");
            return;
        }

        Message moveMessage = new Message();
        moveMessage.setType(Message.Type.PLAYER_MOVE);
        moveMessage.putInBody("x", x);
        moveMessage.putInBody("y", y);
        moveMessage.putInBody("username", authenticatedUser.getUsername());
        moveMessage.putInBody("timestamp", System.currentTimeMillis());

        sendMessage(moveMessage);
        System.out.println("NETWORK: Sent PLAYER_MOVE message to server - Position: (" + x + ", " + y + ") for user: " + authenticatedUser.getUsername());
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

    public void sendPublicChatMessage(String messageText) {
        if (connectionState != ConnectionState.AUTHENTICATED && connectionState != ConnectionState.IN_GAME) {
            return;
        }

        Message chatMessage = new Message();
        chatMessage.setType(Message.Type.CHAT_PUBLIC);
        chatMessage.putInBody("content", messageText);
        chatMessage.putInBody("timestamp", System.currentTimeMillis());

        System.out.println("**[CHAT][PUBLIC][CLIENT][SEND] user=" + (authenticatedUser != null ? authenticatedUser.getUsername() : "null") + " content=\"" + messageText + "\"**");
        sendMessage(chatMessage);
    }

    public void sendPrivateChatMessage(String recipient, String messageText) {
        if (connectionState != ConnectionState.AUTHENTICATED && connectionState != ConnectionState.IN_GAME) {
            return;
        }

        Message chatMessage = new Message();
        chatMessage.setType(Message.Type.CHAT_PRIVATE);
        chatMessage.putInBody("recipient", recipient);
        chatMessage.putInBody("content", messageText);
        chatMessage.putInBody("timestamp", System.currentTimeMillis());

        System.out.println("**[CHAT][PRIVATE][CLIENT][SEND] user=" + (authenticatedUser != null ? authenticatedUser.getUsername() : "null") + " to=" + recipient + " content=\"" + messageText + "\"**");
        sendMessage(chatMessage);
    }

    public void startVoteKick(String targetUsername) {
        if (connectionState != ConnectionState.AUTHENTICATED && connectionState != ConnectionState.IN_GAME) {
            return;
        }

        Message msg = new Message();
        msg.setType(Message.Type.VOTE_START);
        msg.putInBody("voteType", "KICK");
        msg.putInBody("target", targetUsername);
        msg.putInBody("timestamp", System.currentTimeMillis());
        sendMessage(msg);
    }

    public void startVoteTerminate() {
        if (connectionState != ConnectionState.AUTHENTICATED && connectionState != ConnectionState.IN_GAME) {
            return;
        }

        Message msg = new Message();
        msg.setType(Message.Type.VOTE_START);
        msg.putInBody("voteType", "TERMINATE");
        msg.putInBody("timestamp", System.currentTimeMillis());
        sendMessage(msg);
    }

    public void castVote(boolean yes) {
        if (connectionState != ConnectionState.AUTHENTICATED && connectionState != ConnectionState.IN_GAME) {
            return;
        }

        Message msg = new Message();
        msg.setType(Message.Type.VOTE_CAST);
        msg.putInBody("vote", yes);
        msg.putInBody("timestamp", System.currentTimeMillis());
        sendMessage(msg);
    }

    public void sendRoomChatMessage(String roomId, String messageText) {
        if (connectionState != ConnectionState.AUTHENTICATED) {
            return;
        }

        Message chatMessage = new Message();
        chatMessage.setType(Message.Type.CHAT);
        chatMessage.putInBody("roomId", roomId);
        chatMessage.putInBody("content", messageText);
        chatMessage.putInBody("timestamp", System.currentTimeMillis());

        sendMessage(chatMessage);
    }

    public void createChatRoom(String roomName) {
        if (connectionState != ConnectionState.AUTHENTICATED) {
            return;
        }

        Message chatMessage = new Message();
        chatMessage.setType(Message.Type.CHAT_ROOM_CREATE);
        chatMessage.putInBody("roomName", roomName);
        chatMessage.putInBody("roomId", "room_" + System.currentTimeMillis());
        chatMessage.putInBody("timestamp", System.currentTimeMillis());

        sendMessage(chatMessage);
    }

    public void joinChatRoom(String roomId) {
        if (connectionState != ConnectionState.AUTHENTICATED) {
            return;
        }

        Message chatMessage = new Message();
        chatMessage.setType(Message.Type.CHAT_ROOM_JOIN);
        chatMessage.putInBody("roomId", roomId);
        chatMessage.putInBody("timestamp", System.currentTimeMillis());

        sendMessage(chatMessage);
    }

    public void leaveChatRoom(String roomId) {
        if (connectionState != ConnectionState.AUTHENTICATED) {
            return;
        }

        Message chatMessage = new Message();
        chatMessage.setType(Message.Type.CHAT_ROOM_LEAVE);
        chatMessage.putInBody("roomId", roomId);
        chatMessage.putInBody("timestamp", System.currentTimeMillis());

        sendMessage(chatMessage);
    }

    public void requestChatHistory(String roomId) {
        if (connectionState != ConnectionState.AUTHENTICATED) {
            return;
        }

        Message chatMessage = new Message();
        chatMessage.setType(Message.Type.CHAT_HISTORY_REQUEST);
        chatMessage.putInBody("roomId", roomId);
        chatMessage.putInBody("timestamp", System.currentTimeMillis());

        sendMessage(chatMessage);
    }

    public void sendTradeRequest(String targetPlayer, String item, int quantity) {
        if (connectionState != ConnectionState.AUTHENTICATED && connectionState != ConnectionState.IN_GAME) {
            System.out.println("**CLIENT WARN** sendTradeRequest blocked: state=" + connectionState + ", toPlayer=" + targetPlayer);
            return;
        }

        Message tradeMessage = new Message();
        tradeMessage.setType(Message.Type.TRADE_REQUEST);
        tradeMessage.putInBody("fromPlayer", authenticatedUser.getUsername());
        tradeMessage.putInBody("toPlayer", targetPlayer);
        tradeMessage.putInBody("targetPlayer", targetPlayer); // For server routing compatibility
        tradeMessage.putInBody("item", item);
        tradeMessage.putInBody("quantity", quantity);
        tradeMessage.putInBody("timestamp", System.currentTimeMillis());

        System.out.println("**CLIENT SEND** TRADE_REQUEST -> toPlayer=" + targetPlayer + ", item=" + item + ", qty=" + quantity + ", from=" + authenticatedUser.getUsername());

        sendMessage(tradeMessage);
    }

    public void sendTradeRequest(String targetPlayer, String item, int quantity, int price) {
        if (connectionState != ConnectionState.AUTHENTICATED && connectionState != ConnectionState.IN_GAME) {
            System.out.println("**CLIENT WARN** sendTradeRequest(price) blocked: state=" + connectionState + ", toPlayer=" + targetPlayer);
            return;
        }

        Message tradeMessage = new Message();
        tradeMessage.setType(Message.Type.TRADE_REQUEST);
        tradeMessage.putInBody("fromPlayer", authenticatedUser.getUsername());
        tradeMessage.putInBody("toPlayer", targetPlayer);
        tradeMessage.putInBody("targetPlayer", targetPlayer); // For server routing compatibility
        tradeMessage.putInBody("item", item);
        tradeMessage.putInBody("quantity", quantity);
        tradeMessage.putInBody("price", price);
        tradeMessage.putInBody("timestamp", System.currentTimeMillis());

        System.out.println("**CLIENT SEND** TRADE_REQUEST -> toPlayer=" + targetPlayer + ", item=" + item + ", qty=" + quantity + ", price=" + price + ", from=" + authenticatedUser.getUsername());

        sendMessage(tradeMessage);
    }

    public void sendTradeAccept(String targetPlayer, Map<String, Object> tradeItems) {
        if (connectionState != ConnectionState.AUTHENTICATED) {
            System.out.println("**CLIENT WARN** sendTradeAccept blocked: state=" + connectionState + ", toPlayer=" + targetPlayer);
            return;
        }

        Message tradeMessage = new Message();
        tradeMessage.setType(Message.Type.TRADE_ACCEPT);
        tradeMessage.putInBody("fromPlayer", authenticatedUser.getUsername());
        tradeMessage.putInBody("toPlayer", targetPlayer);
        tradeMessage.putInBody("tradeItems", tradeItems);
        tradeMessage.putInBody("timestamp", System.currentTimeMillis());

        System.out.println("**CLIENT SEND** TRADE_ACCEPT -> toPlayer=" + targetPlayer + ", from=" + authenticatedUser.getUsername() + ", items=" + (tradeItems != null ? tradeItems.keySet() : "{}"));

        sendMessage(tradeMessage);
    }

    public void sendNotification(org.example.common.network.events.Notification notification) {
        if (connectionState != ConnectionState.AUTHENTICATED && connectionState != ConnectionState.IN_GAME) {
            return;
        }

        Message notificationMessage = new Message();
        notificationMessage.setType(Message.Type.CHAT);
        notificationMessage.putInBody("notificationType", notification.getNotificationType().toString());
        notificationMessage.putInBody("message", getNotificationMessage(notification));
        notificationMessage.putInBody("timestamp", System.currentTimeMillis());
        notificationMessage.putInBody("sourceId", notification.getSourceId());
        notificationMessage.putInBody("targetId", notification.getTargetId());

        sendMessage(notificationMessage);
    }

    private String getNotificationMessage(org.example.common.network.events.Notification notification) {
        return "System notification";
    }

    public void sendTradeDecline(String targetPlayer) {
        if (connectionState != ConnectionState.AUTHENTICATED) {
            System.out.println("**CLIENT WARN** sendTradeDecline blocked: state=" + connectionState + ", toPlayer=" + targetPlayer);
            return;
        }

        Message tradeMessage = new Message();
        tradeMessage.setType(Message.Type.TRADE_DECLINE);
        tradeMessage.putInBody("fromPlayer", authenticatedUser.getUsername());
        tradeMessage.putInBody("toPlayer", targetPlayer);
        tradeMessage.putInBody("timestamp", System.currentTimeMillis());

        System.out.println("**CLIENT SEND** TRADE_DECLINE -> toPlayer=" + targetPlayer + ", from=" + authenticatedUser.getUsername());

        sendMessage(tradeMessage);
    }

    public void requestTradeHistory() {
        if (connectionState != ConnectionState.AUTHENTICATED) {
            return;
        }

        Message historyMessage = new Message();
        historyMessage.setType(Message.Type.TRADE_REQUEST);
        historyMessage.putInBody("action", "getHistory");
        historyMessage.putInBody("player", authenticatedUser.getUsername());
        historyMessage.putInBody("timestamp", System.currentTimeMillis());

        sendMessage(historyMessage);
    }

    public void requestPendingTradeRequests() {
        if (connectionState != ConnectionState.AUTHENTICATED) {
            return;
        }

        Message pendingMessage = new Message();
        pendingMessage.setType(Message.Type.TRADE_REQUEST);
        pendingMessage.putInBody("action", "getPending");
        pendingMessage.putInBody("player", authenticatedUser.getUsername());
        pendingMessage.putInBody("timestamp", System.currentTimeMillis());

        sendMessage(pendingMessage);
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
        if (connectionState != ConnectionState.AUTHENTICATED) {
            System.err.println("Cannot create lobby: not authenticated (state: " + connectionState + ")");
            return;
        }

        Message createLobbyMessage = new Message();
        createLobbyMessage.setType(Message.Type.CREATE_LOBBY);
        createLobbyMessage.putInBody("lobbyName", lobbyName != null ? lobbyName : authenticatedUser.getUsername() + "'s Lobby");
        createLobbyMessage.putInBody("isPrivate", isPrivate);
        createLobbyMessage.putInBody("isVisible", isVisible);
        if (isPrivate && password != null) {
            createLobbyMessage.putInBody("password", password);
        }
        createLobbyMessage.putInBody("timestamp", System.currentTimeMillis());

        sendMessage(createLobbyMessage);
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
    }

    public void startLobbyGame() {
        if (connectionState != ConnectionState.AUTHENTICATED) {
            return;
        }

        Message startGameMessage = new Message();
        startGameMessage.setType(Message.Type.START_LOBBY_GAME);
        startGameMessage.putInBody("timestamp", System.currentTimeMillis());

        sendMessage(startGameMessage);
    }

    public void selectFarm(int farmIndex) {
        if (connectionState != ConnectionState.AUTHENTICATED && connectionState != ConnectionState.IN_GAME) {
            return;
        }

        Message selectFarmMessage = new Message();
        selectFarmMessage.setType(Message.Type.SELECT_FARM);
        selectFarmMessage.putInBody("farmIndex", farmIndex);
        selectFarmMessage.putInBody("timestamp", System.currentTimeMillis());

        sendMessage(selectFarmMessage);
    }

    public void joinLobby(String lobbyId, String password) {
        if (connectionState != ConnectionState.AUTHENTICATED) {
            return;
        }

        Message joinLobbyMessage = new Message();
        joinLobbyMessage.setType(Message.Type.JOIN_LOBBY);
        joinLobbyMessage.putInBody("lobbyId", lobbyId);
        if (password != null && !password.trim().isEmpty()) {
            joinLobbyMessage.putInBody("password", password);
        }
        joinLobbyMessage.putInBody("timestamp", System.currentTimeMillis());

        sendMessage(joinLobbyMessage);
        System.out.println("DEBUG: JOIN_LOBBY message sent for lobby: " + lobbyId);
    }

    public void leaveLobby() {
        if (connectionState != ConnectionState.AUTHENTICATED) {
            return;
        }

        Message leaveLobbyMessage = new Message();
        leaveLobbyMessage.setType(Message.Type.LEAVE_LOBBY);
        leaveLobbyMessage.putInBody("timestamp", System.currentTimeMillis());

        sendMessage(leaveLobbyMessage);
    }

    public void setPlayerReady(boolean ready) {
        if (connectionState != ConnectionState.AUTHENTICATED) {
            return;
        }

        Message readyMessage = new Message();
        readyMessage.setType(Message.Type.PLAYER_READY);
        readyMessage.putInBody("ready", ready);
        readyMessage.putInBody("timestamp", System.currentTimeMillis());

        sendMessage(readyMessage);
    }

    public void loadGame(String saveName, List<String> playerUsernames) {
        if (connectionState != ConnectionState.AUTHENTICATED) {
            System.err.println("Cannot load game: not authenticated.");
            return;
        }

        Message loadGameMessage = new Message();
        loadGameMessage.setType(Message.Type.LOAD_GAME);
        loadGameMessage.putInBody("saveName", saveName);
        loadGameMessage.putInBody("playerUsernames", playerUsernames);
        loadGameMessage.putInBody("requester", authenticatedUser.getUsername());
        loadGameMessage.putInBody("timestamp", System.currentTimeMillis());

        sendMessage(loadGameMessage);
    }

    public void update() {
        // Process incoming messages on main thread
        int messageCount = 0;
        while (!incomingMessages.isEmpty()) {
            Message message = incomingMessages.poll();
            if (message != null) {
                messageCount++;
                System.out.println("NETWORK: Processing message " + messageCount + " of type: " + message.getType());
                if (messageHandler != null) {
                    messageHandler.handleMessage(message);
                } else {
                    System.err.println("NETWORK: No message handler set!");
                }
            }
        }
        if (messageCount > 0) {
            System.out.println("NETWORK: Processed " + messageCount + " messages");
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

    private boolean isCompleteJsonMessage(String json) {
        int braceCount = 0;
        boolean inString = false;
        boolean escaped = false;

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);

            if (escaped) {
                escaped = false;
                continue;
            }

            if (c == '\\') {
                escaped = true;
                continue;
            }

            if (c == '"' && !escaped) {
                inString = !inString;
                continue;
            }

            if (!inString) {
                if (c == '{') {
                    braceCount++;
                } else if (c == '}') {
                    braceCount--;
                    if (braceCount == 0) {
                        return true; // Found a complete JSON object
                    }
                }
            }
        }
        return false; // Not a complete JSON object yet
    }
}
