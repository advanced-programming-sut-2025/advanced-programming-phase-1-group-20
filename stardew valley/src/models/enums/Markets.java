package models.enums;

import models.Item;
import models.Market;

import java.util.HashMap;

public enum Markets {
    BLACKS_SMITH(new HashMap<>() , 9 , 16 , new String[]{"salam", "emrooz donbal chi hasti ghahreman"}),
    ;//other markets
    private final HashMap<Item, Integer> items;
    private final int startHour;
    private final int endHour;
    private final String[] menu;
    Markets(HashMap<Item , Integer> items, int startHour, int endHour , String[] menu) {
        this.items = items;
        this.startHour = startHour;
        this.endHour = endHour;
        this.menu = menu;
    }
    public Market createMarket() {
        return new Market(items , startHour , endHour , menu);
    }

}
