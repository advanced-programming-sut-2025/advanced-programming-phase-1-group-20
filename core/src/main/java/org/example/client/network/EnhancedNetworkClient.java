package org.example.client.network;

import com.badlogic.gdx.Gdx;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.common.network.events.EventDispatcher;
import org.example.common.network.events.Notification;
import org.example.common.network.requests.AuthenticationRequest;
import org.example.common.network.requests.ChatRequest;
import org.example.common.network.requests.PlayerMoveRequest;
import org.example.common.network.requests.Request;
import org.example.common.network.responses.Response;
import org.example.common.models.entities.User;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CompletionStage;
import java.io.IOException;
import java.util.function.Consumer;


public class EnhancedNetworkClient {
    private static EnhancedNetworkClient instance;
    private WebSocket webSocket;
    private final HttpClient httpClient;
    private final Gson gson;
    private final ConcurrentLinkedQueue<Request> outgoingRequests;
    private final ConcurrentHashMap<String, CompletableFuture<Response>> pendingRequests;
    private final EventDispatcher eventDispatcher;
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

    private EnhancedNetworkClient() {
        this.httpClient = HttpClient.newHttpClient();
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
        this.outgoingRequests = new ConcurrentLinkedQueue<>();
        this.pendingRequests = new ConcurrentHashMap<>();
        this.eventDispatcher = EventDispatcher.getInstance();
        this.connectionState = ConnectionState.DISCONNECTED;
        this.serverHost = "localhost";
        this.serverPort = 8080;
    }

    public static EnhancedNetworkClient getInstance() {
        if (instance == null) {
            instance = new EnhancedNetworkClient();
        }
        return instance;
    }

    public void setServerAddress(String host, int port) {
        this.serverHost = host;
        this.serverPort = port;
    }

    public boolean connect() {
        if (connectionState != ConnectionState.DISCONNECTED) {
            System.err.println("Already connected or connecting");
            return false;
        }

        connectionState = ConnectionState.CONNECTING;

        try {
            String wsUrl = "ws://" + serverHost + ":" + serverPort + "/ws/game";
            WebSocket.Builder builder = httpClient.newWebSocketBuilder();

            webSocket = builder.buildAsync(URI.create(wsUrl), new WebSocket.Listener() {
                @Override
                public void onOpen(WebSocket webSocket) {
                    connectionState = ConnectionState.CONNECTED;
                    System.out.println("Connected to server");
                    startNetworkThread();
                }

                @Override
                public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
                    handleIncomingMessage(data.toString());
                    return null;
                }

                @Override
                public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
                    connectionState = ConnectionState.DISCONNECTED;
                    System.out.println("Disconnected from server: " + reason);
                    return null;
                }

                @Override
                public void onError(WebSocket webSocket, Throwable error) {
                    connectionState = ConnectionState.ERROR;
                    System.err.println("WebSocket error: " + error.getMessage());
                }
            }).join();

            return true;
        } catch (Exception e) {
            connectionState = ConnectionState.ERROR;
            System.err.println("Failed to connect: " + e.getMessage());
            return false;
        }
    }

    private void handleIncomingMessage(String messageJson) {
        try {
            // Try to parse as a response first
            Response response = gson.fromJson(messageJson, Response.class);
            if (response != null) {
                handleResponse(response);
                return;
            }

            // Try to parse as a notification
            Notification notification = gson.fromJson(messageJson, Notification.class);
            if (notification != null) {
                handleNotification(notification);
                return;
            }

            System.err.println("Unknown message format: " + messageJson);
        } catch (Exception e) {
            System.err.println("Error parsing incoming message: " + e.getMessage());
        }
    }

    private void handleResponse(Response response) {
        CompletableFuture<Response> future = pendingRequests.remove(response.getRequestId());
        if (future != null) {
            future.complete(response);
        } else {
            System.err.println("No pending request found for response: " + response.getRequestId());
        }
    }

    private void handleNotification(Notification notification) {
        // Dispatch to event system
        eventDispatcher.dispatch(notification);

        // Also handle in main thread for UI updates
        Gdx.app.postRunnable(() -> {
            // Handle notification in UI thread
            System.out.println("Received notification: " + notification.getNotificationType());
        });
    }

    public CompletableFuture<Response> sendRequest(Request request) {
        if (connectionState != ConnectionState.CONNECTED &&
            connectionState != ConnectionState.AUTHENTICATED &&
            connectionState != ConnectionState.IN_GAME) {
            CompletableFuture<Response> future = new CompletableFuture<>();
            future.completeExceptionally(new RuntimeException("Not connected to server"));
            return future;
        }

        CompletableFuture<Response> future = new CompletableFuture<>();
        pendingRequests.put(request.getRequestId(), future);

        String requestJson = gson.toJson(request);
        webSocket.sendText(requestJson, true);

        return future;
    }

    public CompletableFuture<Response> authenticate(String username, String token) {
        AuthenticationRequest request = new AuthenticationRequest(username, token,
                                                                AuthenticationRequest.AuthType.LOGIN,
                                                                username);
        return sendRequest(request).thenApply(response -> {
            if (response instanceof org.example.common.network.responses.AuthenticationResponse) {
                org.example.common.network.responses.AuthenticationResponse authResponse =
                    (org.example.common.network.responses.AuthenticationResponse) response;
                if (authResponse.isSuccess()) {
                    this.authenticatedUser = authResponse.getUser();
                    this.sessionId = authResponse.getSessionId();
                    this.connectionState = ConnectionState.AUTHENTICATED;
                }
            }
            return response;
        });
    }

    public CompletableFuture<Response> sendPlayerMove(float x, float y) {
        if (authenticatedUser == null) {
            CompletableFuture<Response> future = new CompletableFuture<>();
            future.completeExceptionally(new RuntimeException("Not authenticated"));
            return future;
        }

        PlayerMoveRequest request = new PlayerMoveRequest(authenticatedUser.getUsername(), x, y,
                                                        authenticatedUser.getUsername());
        return sendRequest(request);
    }

    public CompletableFuture<Response> sendChatMessage(String message, String recipient) {
        if (authenticatedUser == null) {
            CompletableFuture<Response> future = new CompletableFuture<>();
            future.completeExceptionally(new RuntimeException("Not authenticated"));
            return future;
        }

        ChatRequest request = new ChatRequest(authenticatedUser.getUsername(), message, recipient,
                                            authenticatedUser.getUsername());
        return sendRequest(request);
    }

    public void addNotificationListener(Consumer<Notification> listener) {
        eventDispatcher.addBroadcastListener(listener);
    }

    public void removeNotificationListener(Consumer<Notification> listener) {
        eventDispatcher.removeBroadcastListener(listener);
    }

    private void startNetworkThread() {
        isRunning = true;
        networkThread = new Thread(() -> {
            while (isRunning) {
                try {
                    // Process outgoing requests
                    Request request = outgoingRequests.poll();
                    if (request != null) {
                        sendRequest(request);
                    }

                    Thread.sleep(16); // ~60 FPS
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    System.err.println("Network thread error: " + e.getMessage());
                }
            }
        });
        networkThread.start();
    }

    public void disconnect() {
        isRunning = false;
        connectionState = ConnectionState.DISCONNECTED;

        if (networkThread != null) {
            networkThread.interrupt();
            try {
                networkThread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        if (webSocket != null) {
            webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "Client disconnecting");
            webSocket = null;
        }

        pendingRequests.clear();
        outgoingRequests.clear();

        System.out.println("Disconnected from server");
    }

    // Getters
    public ConnectionState getConnectionState() {
        return connectionState;
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
}
