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


    public void showAllProducts() {
        System.out.println("Permanent Stock");
        int c = 1;
        for(Item item : permanentStock.keySet()) {
            System.out.println("Item Code " + c + " : ");
            System.out.println("Name        : " + item.getName());
            System.out.println("Description : " + item.getDescription());
            System.out.println("Price       : " + item.getPrice());
            String formatedStock = String.format("%.0f", permanentStock.get(item));
            System.out.println("Stock       : " + formatedStock);
            c++;
        }

        System.out.println("Spring Stock");
        if(!springStock.isEmpty()) {
            for(Item item : springStock.keySet()) {
                System.out.println();
                System.out.println("--------------------------------");
                System.out.println("Item Code " + c + " : ");
                System.out.println("Name        : " + item.getName());
                System.out.println("Description : " + item.getDescription());
                System.out.println("Price       : " + item.getPrice());
                String formatedStock = String.format("%.0f", springStock.get(item));
                System.out.println("Stock       : " + formatedStock);
                System.out.println("--------------------------------");
                System.out.println();
                c++;
            }
        }else{
            System.out.println("--------------------------------");
            System.out.println();
            System.out.println("--------------------------------");
        }

        System.out.println("Summer Stock");
        if(!summerStock.isEmpty()) {
            for(Item item : summerStock.keySet()) {
                System.out.println();
                System.out.println("--------------------------------");
                System.out.println("Item Code " + c + " : ");
                System.out.println("Name        : " + item.getName());
                System.out.println("Description : " + item.getDescription());
                System.out.println("Price       : " + item.getPrice());
                String formatedStock = String.format("%.0f", summerStock.get(item));
                System.out.println("Stock       : " + formatedStock);
                System.out.println("--------------------------------");
                System.out.println();
                c++;
            }
        }else{
            System.out.println("--------------------------------");
            System.out.println();
            System.out.println("--------------------------------");
        }

        System.out.println("Autumn Stock");
        if(!autumnStock.isEmpty()){
            for(Item item : autumnStock.keySet()) {
                System.out.println();
                System.out.println("--------------------------------");
                System.out.println("Item Code " + c + " : ");
                System.out.println("Name        : " + item.getName());
                System.out.println("Description : " + item.getDescription());
                System.out.println("Price       : " + item.getPrice());
                String formatedStock = String.format("%.0f", autumnStock.get(item));
                System.out.println("Stock       : " + formatedStock);
                System.out.println("--------------------------------");
                System.out.println();
                c++;
            }
        }else{
            System.out.println("--------------------------------");
            System.out.println();
            System.out.println("--------------------------------");
        }

        System.out.println("Winter Stock");
        if(!winterStock.isEmpty()) {
            for(Item item : winterStock.keySet()) {
                System.out.println();
                System.out.println("--------------------------------");
                System.out.println("Item Code " + c + " : ");
                System.out.println("Name        : " + item.getName());
                System.out.println("Description : " + item.getDescription());
                System.out.println("Price       : " + item.getPrice());
                String formatedStock = String.format("%.0f", winterStock.get(item));
                System.out.println("Stock       : " + formatedStock);
                System.out.println("--------------------------------");
                System.out.println();
                c++;
            }
        }else{
            System.out.println("--------------------------------");
            System.out.println();
            System.out.println("--------------------------------");
        }
    }
}
