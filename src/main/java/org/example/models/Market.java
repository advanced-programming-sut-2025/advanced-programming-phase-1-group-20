package org.example.models;

import org.example.models.Items.Item;

import java.util.HashMap;

public class Market {
    private HashMap<Item, Double> permanentStock;
    private HashMap<Item, Double> springStock;
    private HashMap<Item, Double> summerStock;
    private HashMap<Item, Double> autumnStock;
    private HashMap<Item, Double> winterStock;
    private int startHour;
    private int endHour;
    private String[] menu;

    public Market(HashMap<Item, Double> permanentStock , HashMap<Item, Double> springStock ,HashMap<Item, Double> summerStock , HashMap<Item, Double> autumnStock , HashMap<Item, Double> winterStock , int startHour, int endHour, String[] menu) {
        this.permanentStock = permanentStock;
        this.springStock = springStock;
        this.summerStock = summerStock;
        this.autumnStock = autumnStock;
        this.winterStock = winterStock;
        this.startHour = startHour;
        this.endHour = endHour;
        this.menu = menu;
    }
}
