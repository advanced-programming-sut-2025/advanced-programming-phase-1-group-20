package org.example.models;

import models.Item;

import java.util.HashMap;

public class Market {
    private HashMap<Item, Integer> items;
    private int startHour;
    private int endHour;
    private String[] menu;

    public Market(HashMap<Item, Integer> items, int startHour, int endHour, String[] menu) {
        this.items = items;
        this.startHour = startHour;
        this.endHour = endHour;
        this.menu = menu;
    }
}
