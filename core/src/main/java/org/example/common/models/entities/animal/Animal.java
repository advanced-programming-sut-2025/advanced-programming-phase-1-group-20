package org.example.common.models.entities.animal;

import org.example.common.models.Items.Item;

public class Animal extends Item {
    private String name;
    private int price;

    public Animal(String name, int price) {
        //TODO : adding correct file path
        super(name , price , "");
        this.name = name;
        this.price = price;
    }
}
