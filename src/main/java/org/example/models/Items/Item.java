package org.example.models.Items;

public class Item {
    private String name;
    private int baseSellPrice;
    private int price;
    private String description;


    public Item(String name, int baseSellPrice) {
        this.name = name;
        this.baseSellPrice = baseSellPrice;
        this.price = baseSellPrice;
    }

    public Item(String name, int baseSellPrice, String description) {
        this.name = name;
        this.baseSellPrice = baseSellPrice;
        this.description = description;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
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
    public void updateItem(){
    }
    public boolean getFinished(){
        return false;
    }
}
