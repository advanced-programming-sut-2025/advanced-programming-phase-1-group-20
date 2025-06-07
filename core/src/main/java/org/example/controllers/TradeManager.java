package org.example.controllers;

import org.example.models.Items.Item;
import org.example.models.Player.Player;
import org.example.models.entities.TradeRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TradeManager {
    private static TradeManager instance;
    private List<TradeRequest> tradeRequests;

    private TradeManager() {
        tradeRequests = new ArrayList<>();
    }

    public static TradeManager getInstance() {
        if (instance == null) {
            instance = new TradeManager();
        }
        return instance;
    }

    public TradeRequest createTradeRequest(Player sender, Player receiver, Item item, int amount, int price, boolean isRequest) {
        // Validate parameters
        if (sender == null || receiver == null || item == null || amount <= 0 || price < 0) {
            return null;
        }

        // Check if sender has the item if it's an offer
        if (!isRequest && sender.getBackpack().getInventory().getOrDefault(item, 0) < amount) {
            return null;
        }

        TradeRequest request = new TradeRequest(sender, receiver, item, amount, price, isRequest);
        tradeRequests.add(request);
        return request;
    }


    public TradeRequest createTradeRequest(Player sender, Player receiver, Item item, int amount,
                                           Item targetItem, int targetAmount, boolean isRequest) {
        // Validate parameters
        if (sender == null || receiver == null || item == null || targetItem == null ||
                amount <= 0 || targetAmount <= 0) {
            return null;
        }

        // Check if sender has the item if it's an offer
        if (!isRequest && sender.getBackpack().getInventory().getOrDefault(item, 0) < amount) {
            return null;
        }

        TradeRequest request = new TradeRequest(sender, receiver, item, amount, targetItem, targetAmount, isRequest);
        tradeRequests.add(request);
        return request;
    }

    public TradeRequest getTradeRequest(int id) {
        for (TradeRequest request : tradeRequests) {
            if (request.getId() == id) {
                return request;
            }
        }
        return null;
    }

    public List<TradeRequest> getTradeRequestsForPlayer(Player player) {
        return tradeRequests.stream()
                .filter(request -> request.getSender().equals(player) || request.getReceiver().equals(player))
                .collect(Collectors.toList());
    }

    public List<TradeRequest> getPendingTradeRequestsForPlayer(Player player) {
        return tradeRequests.stream()
                .filter(request -> request.getReceiver().equals(player) &&
                        !request.isAccepted() && !request.isRejected())
                .collect(Collectors.toList());
    }

    public List<TradeRequest> getUnviewedTradeRequestsForPlayer(Player player) {
        return tradeRequests.stream()
                .filter(request -> request.getReceiver().equals(player) && !request.isViewed())
                .collect(Collectors.toList());
    }


    public List<TradeRequest> getTradeHistoryForPlayer(Player player) {
        return tradeRequests.stream()
                .filter(request -> request.getSender().equals(player) || request.getReceiver().equals(player))
                .collect(Collectors.toList());
    }


    public boolean acceptTradeRequest(int id, Player player) {
        TradeRequest request = getTradeRequest(id);
        if (request == null || !request.getReceiver().equals(player)) {
            return false;
        }

        return request.accept();
    }


    public boolean rejectTradeRequest(int id, Player player) {
        TradeRequest request = getTradeRequest(id);
        if (request == null || !request.getReceiver().equals(player)) {
            return false;
        }

        request.reject();
        return true;
    }


    public void markAllTradeRequestsAsViewed(Player player) {
        getUnviewedTradeRequestsForPlayer(player).forEach(TradeRequest::markAsViewed);
    }
}