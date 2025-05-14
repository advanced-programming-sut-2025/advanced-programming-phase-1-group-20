package org.example.models.Items;

import org.example.models.Player.Player;

import java.util.HashMap;
import java.util.Map;

public class ShippingBin extends Item{
    private Map<Player , Integer> playerIntegerMap;
    public ShippingBin() {
        super("Shipping Bin", 250);
        playerIntegerMap = new HashMap<Player , Integer>();
    }

    public int getIncome(Player player) {
        return playerIntegerMap.get(player);
    }

    public void setIncome(int income , Player player) {
        playerIntegerMap.putIfAbsent(player, income);
    }

    public void increaseIncome(int amount , Player player) {
        playerIntegerMap.putIfAbsent(player, playerIntegerMap.get(player) + amount);
    }

    public void updateShippingBin(Player player) {
        playerIntegerMap.putIfAbsent(player, 0);
    }
}
