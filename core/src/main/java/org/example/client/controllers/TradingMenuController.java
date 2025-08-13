package org.example.client.controllers;

import org.example.common.models.App;
import org.example.common.models.Items.Item;
import org.example.common.models.Player.Player;
import org.example.common.models.entities.TradeRequest;
import org.example.common.models.entities.Game;
import org.example.common.models.common.Result;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class TradingMenuController {
    private Player currentPlayer;
    private Player targetPlayer;
    private boolean isTradeActive = false;
    private boolean isInitiator = false;

    public TradingMenuController(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public Result startTrade(String targetUsername) {
        Game game = App.getGame();
        if (game == null) {
            return Result.error("No active game");
        }

        // Find target player
        Player target = null;
        for (Player p : game.getPlayers()) {
            if (p.getUser().getUsername().equals(targetUsername)) {
                target = p;
                break;
            }
        }

        if (target == null) {
            return Result.error("Player " + targetUsername + " not found");
        }

        if (target.equals(currentPlayer)) {
            return Result.error("You cannot trade with yourself");
        }

        this.targetPlayer = target;
        this.isInitiator = true;
        this.isTradeActive = true;

        return Result.success("Trade session started with " + targetUsername);
    }

    public Result sendTradeRequest(String targetUsername, String itemName, int amount, int price) {
        Game game = App.getGame();
        if (game == null) {
            return Result.error("No active game");
        }

        // Find target player
        Player target = null;
        for (Player p : game.getPlayers()) {
            if (p.getUser().getUsername().equals(targetUsername)) {
                target = p;
                break;
            }
        }

        if (target == null) {
            return Result.error("Player " + targetUsername + " not found");
        }

        if (target.equals(currentPlayer)) {
            return Result.error("You cannot trade with yourself");
        }

        // Get the item
        Item item = currentPlayer.getBackpack().getItem(itemName);
        if (item == null) {
            return Result.error("Item " + itemName + " not found in inventory");
        }

        // Check if the amount is valid
        if (amount <= 0) {
            return Result.error("Amount must be greater than 0");
        }

        // Check if the player has the item
        if (currentPlayer.getBackpack().getInventory().getOrDefault(item, 0) < amount) {
            return Result.error("You don't have enough " + itemName);
        }

        // Create the trade request locally (for history/UI)
        TradeRequest request = TradeManager.getInstance().createTradeRequest(
            currentPlayer, target, item, amount, price, false);

        if (request == null) {
            return Result.error("Failed to create trade request");
        }

        // Also send over network so the other player receives it
        try {
            org.example.client.network.NetworkClient networkClient = org.example.client.network.NetworkClient.getInstance();
            if (networkClient != null) {
                networkClient.sendTradeRequest(targetUsername, itemName, amount, price);
            }
        } catch (Exception e) {
            System.err.println("Failed to send trade request over network: " + e.getMessage());
        }

        return Result.success("Trade request sent to " + targetUsername);
    }

    public Result acceptTradeRequest(int requestId) {
        TradeRequest request = TradeManager.getInstance().getTradeRequest(requestId);
        if (request == null) {
            return Result.error("Trade request not found");
        }

        if (!request.getReceiver().equals(currentPlayer)) {
            return Result.error("This trade request is not for you");
        }

        boolean success = TradeManager.getInstance().acceptTradeRequest(requestId, currentPlayer);
        if (success) {
            // Start trade session
            this.targetPlayer = request.getSender();
            this.isInitiator = false;
            this.isTradeActive = true;
            return Result.success("Trade accepted. Starting trade session.");
        } else {
            return Result.error("Failed to accept trade request");
        }
    }

    public Result rejectTradeRequest(int requestId) {
        TradeRequest request = TradeManager.getInstance().getTradeRequest(requestId);
        if (request == null) {
            return Result.error("Trade request not found");
        }

        if (!request.getReceiver().equals(currentPlayer)) {
            return Result.error("This trade request is not for you");
        }

        boolean success = TradeManager.getInstance().rejectTradeRequest(requestId, currentPlayer);
        if (success) {
            return Result.success("Trade request rejected");
        } else {
            return Result.error("Failed to reject trade request");
        }
    }

    public Result addItemToTrade(String itemName, int amount) {
        if (!isTradeActive) {
            return Result.error("No active trade session");
        }

        Item item = currentPlayer.getBackpack().getItem(itemName);
        if (item == null) {
            return Result.error("Item " + itemName + " not found in inventory");
        }

        if (currentPlayer.getBackpack().getInventory().getOrDefault(item, 0) < amount) {
            return Result.error("You don't have enough " + itemName);
        }

        // Add item to trade offer (this would be handled by the trade session)
        return Result.success("Added " + amount + " " + itemName + " to trade offer");
    }

    public Result removeItemFromTrade(String itemName, int amount) {
        if (!isTradeActive) {
            return Result.error("No active trade session");
        }

        return Result.success("Removed " + amount + " " + itemName + " from trade offer");
    }

    public Result confirmTrade() {
        if (!isTradeActive) {
            return Result.error("No active trade session");
        }

        if (!isInitiator) {
            return Result.error("Only the trade initiator can confirm the trade");
        }

        // Execute the trade
        isTradeActive = false;
        return Result.success("Trade completed successfully");
    }

    public Result cancelTrade() {
        if (!isTradeActive) {
            return Result.error("No active trade session");
        }

        isTradeActive = false;
        return Result.success("Trade cancelled");
    }

    public List<TradeRequest> getPendingTradeRequests() {
        return TradeManager.getInstance().getPendingTradeRequestsForPlayer(currentPlayer);
    }

    public List<TradeRequest> getTradeHistory() {
        return TradeManager.getInstance().getTradeHistoryForPlayer(currentPlayer);
    }

    public List<Player> getAvailablePlayers() {
        Game game = App.getGame();
        if (game == null) {
            return new ArrayList<>();
        }

        return game.getPlayers().stream()
                .filter(p -> !p.equals(currentPlayer))
                .collect(java.util.stream.Collectors.toList());
    }

    public Map<Item, Integer> getCurrentPlayerInventory() {
        return currentPlayer.getBackpack().getInventory();
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Player getTargetPlayer() {
        return targetPlayer;
    }

    public boolean isTradeActive() {
        return isTradeActive;
    }

    public boolean isInitiator() {
        return isInitiator;
    }
}
