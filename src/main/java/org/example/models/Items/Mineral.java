package org.example.models.Items;



public class Mineral extends Item {
    private String description;
    public Mineral(String name , int price , String description) {
        super(name, price);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
