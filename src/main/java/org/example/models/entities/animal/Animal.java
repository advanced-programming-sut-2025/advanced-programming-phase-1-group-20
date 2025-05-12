package org.example.models.entities.animal;

import org.example.models.Items.Item;

public class Animal extends Item {
    private String name;
    private int price;

    public Animal(String name, int price) {
        super(name , price);
        this.name = name;
        this.price = price;
    }

}