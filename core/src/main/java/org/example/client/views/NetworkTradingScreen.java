package org.example.client.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.client.Main;
import org.example.client.network.NetworkClient;
import org.example.client.network.ClientMessageHandler;
import org.example.common.models.App;
import org.example.common.models.Items.Item;
import org.example.common.models.Player.Player;
import org.example.common.models.entities.TradeRequest;
import org.example.common.models.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NetworkTradingScreen implements Screen, Disposable, ClientMessageHandler.TradeRequestListener {
    private Stage stage;
    private Skin skin;
    private Screen previousScreen;
    private NetworkClient networkClient;
    private ClientMessageHandler messageHandler;

    // UI Components
    private Table mainTable;

    // Current state
    private enum ViewState {
        MAIN_MENU,
        PLAYER_SELECTION,
        TRADE_HISTORY,
        PENDING_REQUESTS
    }

    private ViewState currentState = ViewState.MAIN_MENU;

    // Trade data
    private Map<String, TradeRequest> pendingTradeRequests = new ConcurrentHashMap<>();
    private List<TradeRequest> tradeHistory = new ArrayList<>();
    private Player currentPlayer;
    private Map<Item, Integer> currentPlayerItems = new HashMap<>();

    // Background
    private Texture backgroundTexture;

    public NetworkTradingScreen(Skin skin, Screen previousScreen) {
        this.skin = skin;
        this.previousScreen = previousScreen;
        this.stage = new Stage(new ScreenViewport());
        this.networkClient = NetworkClient.getInstance();
        this.messageHandler = networkClient.getMessageHandler();
        this.currentPlayer = App.getGame().getCurrentPlayer();

        // Set up trade listener
        messageHandler.setTradeListener(this);
        System.out.println("**CLIENT UI** NetworkTradingScreen initialized; listener registered; user=" + (currentPlayer != null && currentPlayer.getUser()!=null ? currentPlayer.getUser().getUsername() : "null"));

        // Create background
        createBackgroundTexture();

        // Initialize UI
        initializeUI();

        // Load initial data
        loadTradeData();
    }

    private void createBackgroundTexture() {
        try {
            backgroundTexture = new Texture(Gdx.files.internal("content/crafting_background.png"));
        } catch (Exception e) {
            com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
            pixmap.setColor(0.94f, 0.94f, 0.94f, 1f);
            pixmap.fill();
            backgroundTexture = new Texture(pixmap);
            pixmap.dispose();
        }
    }

    private void initializeUI() {
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.pad(20);
        System.out.println("**CLIENT UI** Trading UI initializeUI()");
        showMainMenu();
    }

    private void loadTradeData() {
        if (currentPlayer != null) {
            currentPlayerItems = currentPlayer.getBackpack().getInventory();
        }

        // Load trade history from server
        loadTradeHistory();
    }

    private void loadTradeHistory() {
        // Request trade history from server
        networkClient.requestTradeHistory();

        // For now, add some sample data for testing
        // In a real implementation, this would come from the server
        if (tradeHistory.isEmpty() && currentPlayer != null) {
            // Add sample trade history entries
            addSampleTradeHistory();
        }
    }

    private void addSampleTradeHistory() {
        // This is temporary sample data for demonstration
        // In production, this would come from the server
        try {
            // Create sample trade requests for demonstration
            Player samplePlayer = App.getGame().getPlayers().stream()
                .filter(p -> !p.equals(currentPlayer))
                .findFirst()
                .orElse(null);

            if (samplePlayer != null) {
                // Sample accepted trade
                Item sampleItem = App.getItem("Wheat");
                if (sampleItem != null) {
                    TradeRequest acceptedTrade = new TradeRequest(
                        currentPlayer, samplePlayer, sampleItem, 5, 100, false);
                    acceptedTrade.accept(); // Mark as accepted
                    tradeHistory.add(acceptedTrade);
                }

                // Sample rejected trade
                Item sampleItem2 = App.getItem("Corn");
                if (sampleItem2 != null) {
                    TradeRequest rejectedTrade = new TradeRequest(
                        samplePlayer, currentPlayer, sampleItem2, 3, 50, false);
                    rejectedTrade.reject(); // Mark as rejected
                    tradeHistory.add(rejectedTrade);
                }
            }
        } catch (Exception e) {
            System.err.println("Error adding sample trade history: " + e.getMessage());
        }
    }

    private void showMainMenu() {
        mainTable.clear();
        currentState = ViewState.MAIN_MENU;
        System.out.println("**CLIENT UI** showMainMenu()");

        // Title
        Label titleLabel = new Label("Network Trading System", skin);
        titleLabel.setFontScale(2.0f);
        titleLabel.setColor(Color.BLACK);
        mainTable.add(titleLabel).colspan(2).padBottom(30).row();

        // Start Trade Button
        TextButton startTradeButton = new TextButton("Start Trading", skin);
        startTradeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("**CLIENT UI** Click: Start Trading");
                showPlayerSelection();
            }
        });
        mainTable.add(startTradeButton).width(250).height(60).pad(10).row();

        // View Pending Requests Button
        int pendingCount = 0;
        try {
            java.util.List<TradeRequest> pending = org.example.client.controllers.TradeManager.getInstance()
                .getPendingTradeRequestsForPlayer(currentPlayer);
            pendingCount = pending != null ? pending.size() : 0;
        } catch (Exception ignored) {}
        String pendingText = pendingCount == 0 ?
            "View Pending Requests" :
            "View Pending Requests (" + pendingCount + ")";
        TextButton pendingButton = new TextButton(pendingText, skin);
        pendingButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("**CLIENT UI** Click: View Pending Requests");
                showPendingRequests();
            }
        });
        mainTable.add(pendingButton).width(250).height(60).pad(10).row();

        // View Trade History Button
        TextButton historyButton = new TextButton("View Trade History", skin);
        historyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("**CLIENT UI** Click: View Trade History");
                showTradeHistory();
            }
        });
        mainTable.add(historyButton).width(250).height(60).pad(10).row();

        // Back Button
        TextButton backButton = new TextButton("Back to Game", skin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                close();
            }
        });
        mainTable.add(backButton).width(250).height(60).pad(10).row();

        stage.addActor(mainTable);
    }

    private void showPlayerSelection() {
        mainTable.clear();
        currentState = ViewState.PLAYER_SELECTION;
        System.out.println("**CLIENT UI** showPlayerSelection()");

        // Title
        Label titleLabel = new Label("Select Player to Trade With", skin);
        titleLabel.setFontScale(1.8f);
        titleLabel.setColor(Color.BLACK);
        mainTable.add(titleLabel).colspan(2).padBottom(20).row();

        // Available players list
        List<Player> availablePlayers = getAvailablePlayers();
        if (availablePlayers.isEmpty()) {
            Label noPlayersLabel = new Label("No other players available for trading", skin);
            noPlayersLabel.setColor(Color.RED);
            mainTable.add(noPlayersLabel).colspan(2).pad(10).row();
        } else {
            for (Player player : availablePlayers) {
                Table playerRow = new Table();

                Label playerLabel = new Label(player.getUser().getUsername(), skin);
                playerLabel.setFontScale(1.2f);
                playerRow.add(playerLabel).width(200).pad(5);

                TextButton tradeButton = new TextButton("Trade", skin);
                tradeButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        System.out.println("**CLIENT UI** Click: Select Player to trade -> " + player.getUser().getUsername());
                        showTradeRequestDialog(player.getUser().getUsername());
                    }
                });
                playerRow.add(tradeButton).width(100).height(40).pad(5);

                mainTable.add(playerRow).colspan(2).pad(5).row();
            }
        }

        // Back Button
        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("**CLIENT UI** Click: Back from Player Selection");
                showMainMenu();
            }
        });
        mainTable.add(backButton).width(200).height(50).pad(10).row();

        stage.addActor(mainTable);
    }

    private void showPendingRequests() {
        mainTable.clear();
        currentState = ViewState.PENDING_REQUESTS;
        System.out.println("**CLIENT UI** showPendingRequests()");

        Label titleLabel = new Label("Pending Trade Requests", skin);
        titleLabel.setFontScale(1.8f);
        titleLabel.setColor(Color.BLACK);
        mainTable.add(titleLabel).colspan(3).padBottom(20).row();

        java.util.List<TradeRequest> pendingRequests = org.example.client.controllers.TradeManager.getInstance()
            .getPendingTradeRequestsForPlayer(currentPlayer);
        if (pendingRequests == null || pendingRequests.isEmpty()) {
            Label noRequestsLabel = new Label("No pending trade requests", skin);
            noRequestsLabel.setColor(Color.GRAY);
            mainTable.add(noRequestsLabel).colspan(3).pad(10).row();
        } else {
            for (TradeRequest request : pendingRequests) {
                Table requestTable = new Table();
                requestTable.pad(10);

                String requestText = String.format("From: %s | Item: %s x%d | Price: %d gold",
                    request.getSender().getUser().getUsername(),
                    request.getItem().getName(),
                    request.getAmount(),
                    request.getPrice());

                Label requestLabel = new Label(requestText, skin);
                requestLabel.setWrap(true);
                requestTable.add(requestLabel).width(400).pad(5);

                TextButton acceptButton = new TextButton("Accept", skin);
                acceptButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        System.out.println("**CLIENT UI** Click: Accept Trade id=" + request.getId());
                        acceptTradeRequest(request);
                    }
                });
                requestTable.add(acceptButton).width(80).height(30).pad(5);

                TextButton rejectButton = new TextButton("Reject", skin);
                rejectButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        System.out.println("**CLIENT UI** Click: Reject Trade id=" + request.getId());
                        rejectTradeRequest(request);
                    }
                });
                requestTable.add(rejectButton).width(80).height(30).pad(5);

                mainTable.add(requestTable).colspan(3).pad(5).row();
            }
        }

        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("**CLIENT UI** Click: Back from Pending Requests");
                showMainMenu();
            }
        });
        mainTable.add(backButton).width(200).height(50).pad(10).row();

        stage.addActor(mainTable);
    }

    private void showTradeHistory() {
        mainTable.clear();
        currentState = ViewState.TRADE_HISTORY;
        System.out.println("**CLIENT UI** showTradeHistory()");

        Label titleLabel = new Label("Trade History", skin);
        titleLabel.setFontScale(1.8f);
        titleLabel.setColor(Color.BLACK);
        mainTable.add(titleLabel).colspan(2).padBottom(20).row();

        // Pull fresh history from TradeManager to include network-created requests
        this.tradeHistory.clear();
        try {
            java.util.List<TradeRequest> hist = org.example.client.controllers.TradeManager.getInstance()
                .getTradeHistoryForPlayer(currentPlayer);
            if (hist != null) this.tradeHistory.addAll(hist);
        } catch (Exception ignored) {}

        if (this.tradeHistory.isEmpty()) {
            Label noHistoryLabel = new Label("No trade history available", skin);
            noHistoryLabel.setColor(Color.GRAY);
            mainTable.add(noHistoryLabel).colspan(2).pad(10).row();
        } else {
            ScrollPane scrollPane = new ScrollPane(createTradeHistoryTable(), skin);
            scrollPane.setFadeScrollBars(false);
            mainTable.add(scrollPane).width(700).height(400).pad(10).row();
        }

        // Add buttons row
        Table buttonsTable = new Table();

        TextButton refreshButton = new TextButton("Refresh", skin);
        refreshButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("**CLIENT UI** Click: Refresh Trade History");
                loadTradeHistory();
                showTradeHistory(); // Refresh the display
            }
        });
        buttonsTable.add(refreshButton).width(100).height(50).pad(10);

        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("**CLIENT UI** Click: Back from Trade History");
                showMainMenu();
            }
        });
        buttonsTable.add(backButton).width(100).height(50).pad(10);

        mainTable.add(buttonsTable).colspan(2).pad(10).row();

        stage.addActor(mainTable);
    }

    private Table createTradeHistoryTable() {
        Table table = new Table();

        // Add header
        Table headerTable = new Table();
        headerTable.add(new Label("Status", skin)).width(100).pad(5);
        headerTable.add(new Label("From", skin)).width(120).pad(5);
        headerTable.add(new Label("To", skin)).width(120).pad(5);
        headerTable.add(new Label("Item", skin)).width(150).pad(5);
        headerTable.add(new Label("Amount", skin)).width(80).pad(5);
        headerTable.add(new Label("Price", skin)).width(100).pad(5);
        table.add(headerTable).row();

        // Add separator
        table.add().height(2).fillX().pad(5).row();

        for (TradeRequest request : tradeHistory) {
            String status = request.isAccepted() ? "ACCEPTED" :
                request.isRejected() ? "REJECTED" : "PENDING";

            Table rowTable = new Table();

            // Status with color
            Label statusLabel = new Label(status, skin);
            statusLabel.setColor(request.isAccepted() ? Color.GREEN :
                request.isRejected() ? Color.RED : Color.ORANGE);
            rowTable.add(statusLabel).width(100).pad(5);

            // From player
            rowTable.add(new Label(request.getSender().getUser().getUsername(), skin)).width(120).pad(5);

            // To player
            rowTable.add(new Label(request.getReceiver().getUser().getUsername(), skin)).width(120).pad(5);

            // Item name
            rowTable.add(new Label(request.getItem().getName(), skin)).width(150).pad(5);

            // Amount
            rowTable.add(new Label(String.valueOf(request.getAmount()), skin)).width(80).pad(5);

            // Price
            rowTable.add(new Label(request.getPrice() + " gold", skin)).width(100).pad(5);

            table.add(rowTable).row();

            // Add separator between rows
            table.add().height(1).fillX().pad(2).row();
        }

        return table;
    }

    private void showTradeRequestDialog(String targetUsername) {
        System.out.println("**CLIENT UI** showTradeRequestDialog(target=" + targetUsername + ")");

        // Build controls first so the dialog's result() can reference them
        Table content = new Table();
        content.pad(20);

        Label itemLabel = new Label("Item:", skin);
        content.add(itemLabel).pad(5);

        String[] itemNames = currentPlayerItems.keySet().stream()
            .map(Item::getName)
            .toArray(String[]::new);

        final SelectBox<String> itemSelectBox = new SelectBox<>(skin);
        itemSelectBox.setItems(itemNames);
        content.add(itemSelectBox).width(200).pad(5).row();

        Label amountLabel = new Label("Amount:", skin);
        content.add(amountLabel).pad(5);

        final TextField amountField = new TextField("1", skin);
        content.add(amountField).width(100).pad(5).row();

        Label priceLabel = new Label("Price (gold):", skin);
        content.add(priceLabel).pad(5);

        final TextField priceField = new TextField("0", skin);
        content.add(priceField).width(100).pad(5).row();

        Dialog dialog = new Dialog("Send Trade Request", skin) {
            @Override
            protected void result(Object obj) {
                if (Boolean.TRUE.equals(obj)) {
                    try {
                        String itemName = itemSelectBox.getSelected();
                        int amount = Integer.parseInt(amountField.getText());
                        int price = Integer.parseInt(priceField.getText());

                        System.out.println("**CLIENT UI** Click: Send Trade -> to=" + targetUsername + ", item=" + itemName + ", amount=" + amount + ", price=" + price);
                        if (itemName == null || itemName.isEmpty()) {
                            showError("Please select an item");
                            return;
                        }

                        if (amount <= 0) {
                            showError("Amount must be greater than 0");
                            return;
                        }

                        if (price < 0) {
                            showError("Price must be non-negative");
                            return;
                        }

                        sendTradeRequest(targetUsername, itemName, amount, price);
                    } catch (NumberFormatException e) {
                        System.out.println("**CLIENT UI** ERROR: Invalid numbers in trade dialog");
                        showError("Please enter valid numbers for amount and price");
                    }
                }
            }
        };

        dialog.setModal(true);
        dialog.getContentTable().add(content);
        dialog.button("Send", true);
        dialog.button("Cancel", false);
        dialog.show(stage);
    }

    private void showError(String message) {
        Dialog dialog = new Dialog("Error", skin);
        dialog.text(message);
        dialog.button("OK");
        dialog.show(stage);
    }

    private void showNotification(String message) {
        Dialog dialog = new Dialog("Notification", skin);
        dialog.text(message);
        dialog.button("OK");
        dialog.show(stage);
    }

    // Network methods
    private void sendTradeRequest(String targetUsername, String itemName, int amount, int price) {
        // Send over network
        networkClient.sendTradeRequest(targetUsername, itemName, amount, price);

        // Add locally to sender's history immediately
        try {
            Player target = App.getGame().getPlayerByUsername(targetUsername);
            Item itemObj = App.getItem(itemName);
            if (target != null && itemObj != null) {
                TradeRequest request = org.example.client.controllers.TradeManager.getInstance()
                    .createTradeRequest(currentPlayer, target, itemObj, amount, price, false);
                if (request != null) {
                    tradeHistory.add(request);
                    if (currentState == ViewState.TRADE_HISTORY) {
                        showTradeHistory();
                    }
                }
            }
        } catch (Exception ignored) {}

        showNotification("Trade request sent to " + targetUsername);
    }

    private void acceptTradeRequest(TradeRequest request) {
        // Responder is current player (receiver); notify the original sender
        String responder = currentPlayer.getUser().getUsername();
        String originalSender = request.getSender().getUser().getUsername();
        messageHandler.sendTradeResponse(responder, originalSender, true);

        // Apply trade locally for responder
        request.accept();
        tradeHistory.add(request);

        // Remove from pending requests
        pendingTradeRequests.remove(String.valueOf(request.getId()));

        showNotification("Trade accepted! Starting trade session.");
    }

    private void rejectTradeRequest(TradeRequest request) {
        // Responder is current player (receiver); notify the original sender
        String responder = currentPlayer.getUser().getUsername();
        String originalSender = request.getSender().getUser().getUsername();
        messageHandler.sendTradeResponse(responder, originalSender, false);

        // Add to trade history
        request.reject();
        tradeHistory.add(request);

        // Remove from pending requests
        pendingTradeRequests.remove(String.valueOf(request.getId()));
        showPendingRequests();

        showNotification("Trade request rejected.");
    }

    private List<Player> getAvailablePlayers() {
        List<Player> availablePlayers = new ArrayList<>();
        if (App.getGame() != null) {
            for (Player player : App.getGame().getPlayers()) {
                if (!player.equals(currentPlayer)) {
                    availablePlayers.add(player);
                }
            }
        }
        return availablePlayers;
    }

    // TradeRequestListener implementation
    @Override
    public void onTradeRequest(String fromPlayer, String toPlayer, String item, int quantity) {
        Gdx.app.postRunnable(() -> {
            Player sender = App.getGame().getPlayerByUsername(fromPlayer);
            Player receiver = App.getGame().getPlayerByUsername(toPlayer);

            if (sender != null && receiver != null) {
                Item itemObj = App.getItem(item);
                if (itemObj != null) {
                    TradeRequest request = new TradeRequest(sender, receiver, itemObj, quantity, 0, false);
                    pendingTradeRequests.put(String.valueOf(request.getId()), request);

                    showNotification("New trade request from " + fromPlayer + " for " + quantity + "x " + item);

                    if (currentState == ViewState.PENDING_REQUESTS) {
                        showPendingRequests();
                    }
                }
            }
        });
    }

    @Override
    public void onTradeResponse(String fromPlayer, String toPlayer, boolean accepted) {
        Gdx.app.postRunnable(() -> {
            if (accepted) {
                showNotification("Trade request accepted by " + fromPlayer);
            } else {
                showNotification("Trade request rejected by " + fromPlayer);
            }
        });
    }

    // Method to handle trade history updates from server
    public void updateTradeHistory(List<TradeRequest> newHistory) {
        Gdx.app.postRunnable(() -> {
            tradeHistory.clear();
            tradeHistory.addAll(newHistory);

            // Refresh the UI if currently viewing trade history
            if (currentState == ViewState.TRADE_HISTORY) {
                showTradeHistory();
            }
        });
    }

    public void close() {
        if (previousScreen != null) {
            Main.getGame().setScreen(previousScreen);
        }
        dispose();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.getBatch().begin();
        stage.getBatch().draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.getBatch().end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
    }
}
