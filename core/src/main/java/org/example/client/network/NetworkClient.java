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
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Queue;

public class NetworkClient {
    private static NetworkClient instance;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
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
            
            // Create socket connection
            SocketHints hints = new SocketHints();
            hints.connectTimeout = 10000; // 10 seconds
            
            socket = Gdx.net.newClientSocket(Net.Protocol.TCP, serverHost, serverPort, hints);
            
            // Set up streams
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            
            connectionState = ConnectionState.CONNECTED;
            
            // Start network thread
            startNetworkThread();
            
            System.out.println("Connected to server at " + serverHost + ":" + serverPort);
            return true;
            
        } catch (GdxRuntimeException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
            connectionState = ConnectionState.ERROR;
            return false;
        }
    }
    
    private void startNetworkThread() {
        isRunning = true;
        networkThread = new Thread(this::networkLoop, "NetworkClient-Thread");
        networkThread.setDaemon(true);
        networkThread.start();
    }
    
    private void networkLoop() {
        while (isRunning && connectionState != ConnectionState.DISCONNECTED) {
            try {
                // Process outgoing messages
                processOutgoingMessages();
                
                // Read incoming messages
                if (reader.ready()) {
                    String messageJson = reader.readLine();
                    if (messageJson != null && !messageJson.trim().isEmpty()) {
                        processIncomingMessage(messageJson);
                    }
                }
                
                // Small sleep to prevent busy waiting
                Thread.sleep(10);
                
            } catch (IOException e) {
                System.err.println("Network error: " + e.getMessage());
                connectionState = ConnectionState.ERROR;
                break;
            } catch (InterruptedException e) {
                System.out.println("Network thread interrupted");
                break;
            } catch (Exception e) {
                System.err.println("Unexpected network error: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        cleanup();
    }
    
    private void processOutgoingMessages() {
        while (!outgoingMessages.isEmpty()) {
            Message message = outgoingMessages.poll();
            if (message != null) {
                try {
                    String messageJson = gson.toJson(message);
                    writer.println(messageJson);
                    writer.flush();
                } catch (Exception e) {
                    System.err.println("Failed to send message: " + e.getMessage());
                }
            }
        }
    }
    
    private void processIncomingMessage(String messageJson) {
        try {
            Message message = gson.fromJson(messageJson, Message.class);
            incomingMessages.offer(message);
            
            // Process immediately for certain message types
            if (message.getType() == Message.Type.SUCCESS && 
                message.getFromBody("sessionId") != null) {
                sessionId = message.getFromBody("sessionId");
                System.out.println("Received session ID: " + sessionId);
            }
            
        } catch (Exception e) {
            System.err.println("Failed to parse incoming message: " + e.getMessage());
        }
    }
    
    public void sendMessage(Message message) {
        if (connectionState == ConnectionState.CONNECTED || 
            connectionState == ConnectionState.AUTHENTICATED) {
            outgoingMessages.offer(message);
        } else {
            System.err.println("Cannot send message: not connected to server");
        }
    }
    
    public boolean authenticate(User user, String jwtToken) {
        if (connectionState != ConnectionState.CONNECTED) {
            System.err.println("Cannot authenticate: not connected to server");
            return false;
        }
        
        Message authMessage = new Message();
        authMessage.setType(Message.Type.AUTH_LOGIN);
        authMessage.putInBody("username", user.getUsername());
        authMessage.putInBody("token", jwtToken);
        
        sendMessage(authMessage);
        this.authenticatedUser = user;
        
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
        
        cleanup();
        System.out.println("Disconnected from server");
    }
    
    private void cleanup() {
        try {
            if (writer != null) {
                writer.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (socket != null) {
                socket.dispose();
            }
        } catch (Exception e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
        
        writer = null;
        reader = null;
        socket = null;
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
} 