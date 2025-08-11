package org.example.common.models.Items;

import org.example.common.models.Player.Player;

import java.util.HashMap;
import java.util.Map;

public class ShippingBin extends Item {
    private Map<String , Integer> playerIntegerMap;
    public ShippingBin() {
        super("Shipping Bin", 250 , "content/Buildings/Shipping_Bin.png");
        playerIntegerMap = new HashMap<>();
    }

    public int getIncome(Player player) {
        return playerIntegerMap.getOrDefault(player.getUser().getUsername() , 0);
    }

    public void setIncome(int income , Player player) {
        playerIntegerMap.putIfAbsent(player.getUser().getUsername(), income);
    }

    public void increaseIncome(int amount , Player player) {
        playerIntegerMap.putIfAbsent(player.getUser().getUsername(), playerIntegerMap.getOrDefault(player , 0) + amount);
    }

    public void updateShippingBin(Player player) {
        playerIntegerMap.putIfAbsent(player.getUser().getUsername(), 0);
    }

    public Map<String, Integer> getPlayerIntegerMap() {
        return playerIntegerMap;
    }

    public void setPlayerIntegerMap(Map<String, Integer> playerIntegerMap) {
        this.playerIntegerMap = playerIntegerMap;
    }
}
