package org.example.client.controllers;

import org.example.common.models.Items.Item;
import org.example.common.models.Player.Player;
import org.example.common.models.entities.TradeRequest;

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
        System.out.println("**TRADE_MGR** createTradeRequest id=" + request.getId() + " from=" + sender.getUser().getUsername() + " to=" + receiver.getUser().getUsername() +
            " item=" + item.getName() + " x" + amount + " price=" + price + " total=" + tradeRequests.size());
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
        System.out.println("**TRADE_MGR** createTradeRequest(idEx) id=" + request.getId() + " from=" + sender.getUser().getUsername() + " to=" + receiver.getUser().getUsername() +
            " item=" + item.getName() + " x" + amount + " for " + targetAmount + "x " + targetItem.getName() + " total=" + tradeRequests.size());
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
        List<TradeRequest> res = tradeRequests.stream()
            .filter(request -> request.getSender().equals(player) || request.getReceiver().equals(player))
            .collect(Collectors.toList());
        System.out.println("**TRADE_MGR** getTradeRequestsForPlayer user=" + player.getUser().getUsername() + " count=" + (res != null ? res.size() : 0));
        return res;
    }

    public List<TradeRequest> getPendingTradeRequestsForPlayer(Player player) {
        List<TradeRequest> res = tradeRequests.stream()
            .filter(request -> request.getReceiver().equals(player) &&
                !request.isAccepted() && !request.isRejected())
            .collect(Collectors.toList());
        System.out.println("**TRADE_MGR** getPending for=" + player.getUser().getUsername() + " count=" + (res != null ? res.size() : 0));
        return res;
    }

    public List<TradeRequest> getUnviewedTradeRequestsForPlayer(Player player) {
        return tradeRequests.stream()
            .filter(request -> request.getReceiver().equals(player) && !request.isViewed())
            .collect(Collectors.toList());
    }


    public List<TradeRequest> getTradeHistoryForPlayer(Player player) {
        List<TradeRequest> res = tradeRequests.stream()
            .filter(request -> request.getSender().equals(player) || request.getReceiver().equals(player))
            .collect(Collectors.toList());
        System.out.println("**TRADE_MGR** getHistory for=" + player.getUser().getUsername() + " count=" + (res != null ? res.size() : 0));
        return res;
    }


    public boolean acceptTradeRequest(int id, Player player) {
        TradeRequest request = getTradeRequest(id);
        if (request == null || !request.getReceiver().equals(player)) {
            return false;
        }
        boolean ok = request.accept();
        System.out.println("**TRADE_MGR** accept id=" + id + " by=" + player.getUser().getUsername() + " ok=" + ok);
        return ok;
    }


    public boolean rejectTradeRequest(int id, Player player) {
        TradeRequest request = getTradeRequest(id);
        if (request == null || !request.getReceiver().equals(player)) {
            return false;
        }
        request.reject();
        System.out.println("**TRADE_MGR** reject id=" + id + " by=" + player.getUser().getUsername());
        return true;
    }


    public void markAllTradeRequestsAsViewed(Player player) {
        getUnviewedTradeRequestsForPlayer(player).forEach(TradeRequest::markAsViewed);
    }
}
