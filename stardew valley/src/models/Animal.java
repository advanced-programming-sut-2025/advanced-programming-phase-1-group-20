package models;

public class Animal extends Item{
    private String name;
    private int price;
    public Animal(String name , int price) {
        super(name);
        this.price = price;
    }

}
