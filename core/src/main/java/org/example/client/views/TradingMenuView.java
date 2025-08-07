package org.example.client.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.client.Main;
import org.example.client.controllers.TradingMenuController;
import org.example.common.models.Items.Item;
import org.example.common.models.Player.Player;
import org.example.common.models.entities.TradeRequest;
import org.example.common.models.common.Result;

import java.util.List;
import java.util.Map;

public class TradingMenuView implements Screen {
    private Stage stage;
    private TradingMenuController controller;
    private Skin skin;
    private Table mainTable;
    private Table tradeHistoryTable;
    private Table pendingRequestsTable;
    private Table activeTradeTable;
    private Table playerSelectionTable;
    private Screen previousScreen;

    // Current view state
    private enum ViewState {
        MAIN_MENU,
        PLAYER_SELECTION,
        PENDING_REQUESTS,
        TRADE_HISTORY,
        ACTIVE_TRADE
    }

    private ViewState currentState = ViewState.MAIN_MENU;

    public TradingMenuView(TradingMenuController controller, Skin skin, Screen previousScreen) {
        this.controller = controller;
        this.skin = skin;
        this.previousScreen = previousScreen;
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        initializeUI();
    }

    private void initializeUI() {
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.pad(20);

        showMainMenu();
    }

    private void showMainMenu() {
        mainTable.clear();
        currentState = ViewState.MAIN_MENU;

        // Title
        Label titleLabel = new Label("Trading System", skin);
        mainTable.add(titleLabel).colspan(2).padBottom(20).row();

        // Start Trade Button
        TextButton startTradeButton = new TextButton("Start Trading", skin);
        startTradeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showPlayerSelection();
            }
        });
        mainTable.add(startTradeButton).width(200).height(50).pad(10).row();

        // View Pending Requests Button
        List<TradeRequest> pendingRequests = controller.getPendingTradeRequests();
        String pendingText = pendingRequests.isEmpty() ?
            "View Pending Requests" :
            "View Pending Requests (" + pendingRequests.size() + ")";
        TextButton pendingButton = new TextButton(pendingText, skin);
        pendingButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showPendingRequests();
            }
        });
        mainTable.add(pendingButton).width(200).height(50).pad(10).row();

        // View Trade History Button
        TextButton historyButton = new TextButton("View Trade History", skin);
        historyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showTradeHistory();
            }
        });
        mainTable.add(historyButton).width(200).height(50).pad(10).row();

        // Back Button
        TextButton backButton = new TextButton("Back to Game", skin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Return to game view
                if (previousScreen != null) {
                    Main.getGame().setScreen(previousScreen);
                }
                dispose();
            }
        });
        mainTable.add(backButton).width(200).height(50).pad(10).row();

        stage.addActor(mainTable);
    }

    private void showPlayerSelection() {
        mainTable.clear();
        currentState = ViewState.PLAYER_SELECTION;

        // Title
        Label titleLabel = new Label("Select Player to Trade With", skin);
        mainTable.add(titleLabel).colspan(2).padBottom(20).row();

        // Available players list
        List<Player> availablePlayers = controller.getAvailablePlayers();
        if (availablePlayers.isEmpty()) {
            Label noPlayersLabel = new Label("No other players available for trading", skin);
            mainTable.add(noPlayersLabel).colspan(2).pad(10).row();
        } else {
            for (Player player : availablePlayers) {
                TextButton playerButton = new TextButton(player.getUser().getUsername(), skin);
                playerButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        showTradeRequestDialog(player.getUser().getUsername());
                    }
                });
                mainTable.add(playerButton).width(200).height(40).pad(5).row();
            }
        }

        // Back Button
        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showMainMenu();
            }
        });
        mainTable.add(backButton).width(200).height(50).pad(10).row();

        stage.addActor(mainTable);
    }

    private void showPendingRequests() {
        mainTable.clear();
        currentState = ViewState.PENDING_REQUESTS;

        // Title
        Label titleLabel = new Label("Pending Trade Requests", skin);
        mainTable.add(titleLabel).colspan(2).padBottom(20).row();

        // Pending requests list
        List<TradeRequest> pendingRequests = controller.getPendingTradeRequests();
        if (pendingRequests.isEmpty()) {
            Label noRequestsLabel = new Label("No pending trade requests", skin);
            mainTable.add(noRequestsLabel).colspan(2).pad(10).row();
        } else {
            for (TradeRequest request : pendingRequests) {
                Table requestTable = new Table();

                Label requestLabel = new Label(request.toString(), skin);
                requestTable.add(requestLabel).width(400).pad(5);

                TextButton acceptButton = new TextButton("Accept", skin);
                acceptButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        Result result = controller.acceptTradeRequest(request.getId());
                        if (result.success()) {
                            showActiveTrade();
                        } else {
                            showError(result.message());
                        }
                    }
                });
                requestTable.add(acceptButton).width(80).height(30).pad(5);

                TextButton rejectButton = new TextButton("Reject", skin);
                rejectButton.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        Result result = controller.rejectTradeRequest(request.getId());
                        if (result.success()) {
                            showPendingRequests(); // Refresh the list
                        } else {
                            showError(result.message());
                        }
                    }
                });
                requestTable.add(rejectButton).width(80).height(30).pad(5);

                mainTable.add(requestTable).colspan(2).pad(5).row();
            }
        }

        // Back Button
        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showMainMenu();
            }
        });
        mainTable.add(backButton).width(200).height(50).pad(10).row();

        stage.addActor(mainTable);
    }

    private void showTradeHistory() {
        mainTable.clear();
        currentState = ViewState.TRADE_HISTORY;

        // Title
        Label titleLabel = new Label("Trade History", skin);
        mainTable.add(titleLabel).colspan(2).padBottom(20).row();

        // Trade history list
        List<TradeRequest> tradeHistory = controller.getTradeHistory();
        if (tradeHistory.isEmpty()) {
            Label noHistoryLabel = new Label("No trade history available", skin);
            mainTable.add(noHistoryLabel).colspan(2).pad(10).row();
        } else {
            ScrollPane scrollPane = new ScrollPane(createTradeHistoryTable(tradeHistory), skin);
            scrollPane.setFadeScrollBars(false);
            mainTable.add(scrollPane).width(600).height(400).pad(10).row();
        }

        // Back Button
        TextButton backButton = new TextButton("Back", skin);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showMainMenu();
            }
        });
        mainTable.add(backButton).width(200).height(50).pad(10).row();

        stage.addActor(mainTable);
    }

    private Table createTradeHistoryTable(List<TradeRequest> tradeHistory) {
        Table table = new Table();

        for (TradeRequest request : tradeHistory) {
            Label requestLabel = new Label(request.toString(), skin);
            requestLabel.setWrap(true);
            table.add(requestLabel).width(580).pad(5).row();
        }

        return table;
    }

    private void showActiveTrade() {
        mainTable.clear();
        currentState = ViewState.ACTIVE_TRADE;

        // Title
        Label titleLabel = new Label("Active Trade Session", skin);
        mainTable.add(titleLabel).colspan(2).padBottom(20).row();

        // Trade partner info
        Player targetPlayer = controller.getTargetPlayer();
        if (targetPlayer != null) {
            Label partnerLabel = new Label("Trading with: " + targetPlayer.getUser().getUsername(), skin);
            mainTable.add(partnerLabel).colspan(2).pad(10).row();
        }

        // Current player's inventory
        Label inventoryLabel = new Label("Your Inventory:", skin);
        mainTable.add(inventoryLabel).colspan(2).pad(10).row();

        Map<Item, Integer> inventory = controller.getCurrentPlayerInventory();
        if (inventory.isEmpty()) {
            Label emptyLabel = new Label("Your inventory is empty", skin);
            mainTable.add(emptyLabel).colspan(2).pad(5).row();
        } else {
            ScrollPane inventoryPane = new ScrollPane(createInventoryTable(inventory), skin);
            inventoryPane.setFadeScrollBars(false);
            mainTable.add(inventoryPane).width(300).height(200).pad(10);
        }

        // Trade actions
        Table actionsTable = new Table();

        TextButton confirmButton = new TextButton("Confirm Trade", skin);
        confirmButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Result result = controller.confirmTrade();
                if (result.success()) {
                    showMainMenu();
                } else {
                    showError(result.message());
                }
            }
        });
        actionsTable.add(confirmButton).width(150).height(40).pad(5);

        TextButton cancelButton = new TextButton("Cancel Trade", skin);
        cancelButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Result result = controller.cancelTrade();
                if (result.success()) {
                    showMainMenu();
                } else {
                    showError(result.message());
                }
            }
        });
        actionsTable.add(cancelButton).width(150).height(40).pad(5);

        mainTable.add(actionsTable).colspan(2).pad(10).row();

        stage.addActor(mainTable);
    }

    private Table createInventoryTable(Map<Item, Integer> inventory) {
        Table table = new Table();

        for (Map.Entry<Item, Integer> entry : inventory.entrySet()) {
            Item item = entry.getKey();
            Integer amount = entry.getValue();

            Table itemTable = new Table();

            Label itemLabel = new Label(item.getName() + " x" + amount, skin);
            itemTable.add(itemLabel).width(200).pad(2);

            TextButton addButton = new TextButton("Add", skin);
            addButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Result result = controller.addItemToTrade(item.getName(), 1);
                    if (!result.success()) {
                        showError(result.message());
                    }
                }
            });
            itemTable.add(addButton).width(60).height(25).pad(2);

            table.add(itemTable).pad(2).row();
        }

        return table;
    }

    private void showTradeRequestDialog(String targetUsername) {
        Dialog dialog = new Dialog("Send Trade Request", skin);

        Table content = new Table();
        content.pad(10);

        // Item selection with dropdown
        Label itemLabel = new Label("Item:", skin);
        content.add(itemLabel).pad(5);

        // Get available items from inventory
        Map<Item, Integer> inventory = controller.getCurrentPlayerInventory();
        String[] itemNames = inventory.keySet().stream()
                .map(Item::getName)
                .toArray(String[]::new);

        SelectBox<String> itemSelectBox = new SelectBox<>(skin);
        itemSelectBox.setItems(itemNames);
        content.add(itemSelectBox).width(200).pad(5).row();

        // Amount selection
        Label amountLabel = new Label("Amount:", skin);
        content.add(amountLabel).pad(5);

        TextField amountField = new TextField("1", skin);
        content.add(amountField).width(100).pad(5).row();

        // Price selection
        Label priceLabel = new Label("Price (gold):", skin);
        content.add(priceLabel).pad(5);

        TextField priceField = new TextField("0", skin);
        content.add(priceField).width(100).pad(5).row();

        dialog.getContentTable().add(content);

        dialog.button("Send", new Runnable() {
            @Override
            public void run() {
                try {
                    String itemName = itemSelectBox.getSelected();
                    int amount = Integer.parseInt(amountField.getText());
                    int price = Integer.parseInt(priceField.getText());

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

                    Result result = controller.sendTradeRequest(targetUsername, itemName, amount, price);
                    if (result.success()) {
                        showPlayerSelection(); // Refresh the list
                    } else {
                        showError(result.message());
                    }
                } catch (NumberFormatException e) {
                    showError("Please enter valid numbers for amount and price");
                }
            }
        });

        dialog.button("Cancel");
        dialog.show(stage);
    }

    private void showError(String message) {
        Dialog dialog = new Dialog("Error", skin);
        dialog.text(message);
        dialog.button("OK");
        dialog.show(stage);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
