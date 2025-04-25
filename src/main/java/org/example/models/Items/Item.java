package org.example.models.Items;

public class Item {
    private String name;
    private int baseSellPrice;
    private int price;


    public Item(String name, int baseSellPrice) {
        this.name = name;
        this.baseSellPrice = baseSellPrice;
        this.price = baseSellPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBaseSellPrice() {
        return baseSellPrice;
    }

    public void setBaseSellPrice(int baseSellPrice) {
        this.baseSellPrice = baseSellPrice;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public void showInfo(){

    }
}
