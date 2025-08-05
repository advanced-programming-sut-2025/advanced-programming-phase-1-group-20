package org.example.server.controllers;

import org.example.common.models.Market;
import org.example.common.models.Player.Player;
import org.example.common.models.Product;
import org.example.common.models.common.Result;
import org.example.server.GameSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages the live stock and transactions for all markets within a game session.
 * This controller ensures that all market activities are synchronized across all players.
 */
public class LiveStockController {
    private final GameSession gameSession;
    // Map<MarketName, Market>
    private final Map<String, Market> marketStates;

    public LiveStockController(GameSession gameSession) {
        this.gameSession = gameSession;
        this.marketStates = new ConcurrentHashMap<>();
        initializeMarkets();
    }

    /**
     * Initializes the market states from the game map's village markets.
     */
    private void initializeMarkets() {
        if (gameSession.getGameInstance() != null && gameSession.getGameInstance().getGameMap() != null) {
            for (Market market : gameSession.getGameInstance().getGameMap().getVillage().getMarkets()) {
                if (market != null) {
                    marketStates.put(market.getName(), market);
                }
            }
        }
    }

    /**
     * Handles a purchase request from a player. It validates the request,
     * updates the game state, and broadcasts changes.
     *
     * @param username The username of the player making the purchase.
     * @param marketName The name of the market where the purchase is made.
     * @param itemName The name of the item being purchased.
     * @param quantity The quantity of the item to purchase.
     * @return A Result object indicating success or failure.
     */
    public Result handlePurchase(String username, String marketName, String itemName, int quantity) {
        Player player = gameSession.getGameInstance().getPlayerByUsername(username);
        if (player == null) {
            return Result.error("Player not found.");
        }

        Market market = marketStates.get(marketName);
        if (market == null) {
            return Result.error("Market not found.");
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

        // Execute the purchase
        player.decreaseMoney((int) (product.getItem().getPrice() * quantity));
        player.getBackpack().add(product.getItem(), quantity);

        // Deduct stock if it's not infinite
        if (product.getAmount() != Double.POSITIVE_INFINITY) {
            product.setAmount(product.getAmount() - quantity);
        }

        // Trigger a broadcast of the stock update to all players in the session
        gameSession.broadcastMarketUpdate(marketName, itemName, product.getAmount());

        return Result.success("Purchase successful.");
    }
}
