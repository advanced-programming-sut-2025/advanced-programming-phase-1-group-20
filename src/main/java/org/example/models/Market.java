package org.example.models;

import org.example.models.Items.Item;
import org.example.models.enums.Seasons;

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

    public Market(HashMap<Item, Double> permanentStock, HashMap<Item, Double> springStock, HashMap<Item, Double> summerStock, HashMap<Item, Double> autumnStock, HashMap<Item, Double> winterStock, int startHour, int endHour, String[] menu) {
        this.permanentStock = permanentStock;
        this.springStock = springStock;
        this.summerStock = summerStock;
        this.autumnStock = autumnStock;
        this.winterStock = winterStock;
        this.startHour = startHour;
        this.endHour = endHour;
        this.menu = menu;
    }


    public void showAllProducts() {
        System.out.println("Permanent Stock");
        showProducts(permanentStock);

        System.out.println("Spring Stock");
        showProducts(springStock);

        System.out.println("Summer Stock");
        showProducts(summerStock);

        System.out.println("Autumn Stock");
        showProducts(autumnStock);

        System.out.println("Winter Stock");
        showProducts(winterStock);
    }

    public void showAvailableProducts(Seasons season) {
        System.out.println("Permanent Stock");
        showProducts(permanentStock);
        switch (season) {
            case SPRING:
                System.out.println("Spring Stock");
                showProducts(springStock);
                break;
            case SUMMER:
                System.out.println("Summer Stock");
                showProducts(summerStock);
                break;
            case AUTUMN:
                System.out.println("Autumn Stock");
                showProducts(autumnStock);
                break;
            case WINTER:
                System.out.println("Winter Stock");
                showProducts(winterStock);
                break;
        }
    }

    public void showProducts(HashMap<Item, Double> permanentStock) {
        int c = 1;
        if (!permanentStock.isEmpty()) {
            for (Item item : permanentStock.keySet()) {
                System.out.println("Item Code " + c + " : ");
                System.out.println("Name        : " + item.getName());
                System.out.println("Description : " + item.getDescription());
                System.out.println("Price       : " + item.getPrice());
                String formatedStock = String.format("%.0f", permanentStock.get(item));
                System.out.println("Stock       : " + formatedStock);
                c++;
            }
        } else {
            System.out.println("------------------------------");
            System.out.println();
            System.out.println("------------------------------");
        }
    }


    public boolean containsItem(Item item, Double count, Seasons season) {
        HashMap<Item, Double> totalStock = permanentStock;
        switch (season) {
            case SPRING:
                totalStock.putAll(springStock);
                break;
            case SUMMER:
                totalStock.putAll(summerStock);
                break;
            case AUTUMN:
                totalStock.putAll(autumnStock);
                break;
            case WINTER:
                totalStock.putAll(winterStock);
                break;
        }
        if (totalStock.containsKey(item)) {
            if (totalStock.get(item) >= count) {
                return true;
            } else {
                System.out.println("Not enough stock for this product");
                return false;
            }
        }
        System.out.println("Item not found");
        return false;
    }


}
