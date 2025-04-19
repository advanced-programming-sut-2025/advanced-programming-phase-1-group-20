package models;

public class Item {
    String name;
    int price;
    public Item(String name, int price){
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
