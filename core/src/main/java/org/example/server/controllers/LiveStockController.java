package org.example.server.controllers;

import org.example.common.models.Market;
import org.example.common.models.Player.Player;
import org.example.common.models.Product;
import org.example.common.models.common.Result;
import org.example.server.GameSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class LiveStockController {
    private final GameSession gameSession;
    // Map<MarketName, Market>
    private final Map<String, Market> marketStates;

    public LiveStockController(GameSession gameSession) {
        this.gameSession = gameSession;
        this.marketStates = new ConcurrentHashMap<>();
        // initializeMarkets(); // This line is removed
    }

    public void initializeMarkets() { // This method is now public
        if (gameSession.getGameInstance() != null && gameSession.getGameInstance().getGameMap() != null) {
            for (Market market : gameSession.getGameInstance().getGameMap().getVillage().getMarkets()) {
                if (market != null) {
                    marketStates.put(market.getName(), market);
                }
            }
        }
    }


    public Result handlePurchase(String username, String marketName, String itemName, int quantity) {
        Player player = gameSession.getGameInstance().getPlayerByUsername(username);
        if (player == null) {
            return Result.error("Player not found.");
        }

        Market market = marketStates.get(marketName);
        if (market == null) {
            return Result.error("Market not found." + marketName);
        }

        Product product = market.getProduct(itemName);
        if (product == null) {
            return Result.error("Item '" + itemName + "' not found in " + marketName + ".");
        }

        // Server-side validation
        if (product.getAmount() != Double.POSITIVE_INFINITY && product.getAmount() < quantity) {
            return Result.error("Not enough stock for '" + itemName + "'.");
        }
        if (player.getMoney() < product.getItem().getPrice() * quantity) {
            return Result.error("You don't have enough money.");
        }
        if (product.getIngredient() != null && !product.getIngredient().checkRecipe(player.getBackpack())) {
            return Result.error("Player does not have the required ingredients for this purchase.");
        }

        // Execute the purchase using the market's checkout method to handle special items properly
        try {
            // Use the market's checkout method which handles special items correctly
            market.checkOut(player, product.getItem(), quantity);
        } catch (Exception e) {
            System.err.println("Error during purchase: " + e.getMessage());
            return Result.error("Purchase failed: " + e.getMessage());
        }

        // Trigger a broadcast of the stock update to all players in the session
        System.out.println("🛒 BROADCASTING MARKET UPDATE: " + marketName + " - " + itemName + " stock now: " + product.getAmount());
        gameSession.broadcastMarketUpdate(marketName, itemName, product.getAmount());

        return Result.success("Purchase successful.");
    }
}
