package org.example.models;

import models.Animal;
import models.enums.Seasons;

public class Fish extends Animal {
    private Seasons season;
    private String name;
    private int price;

    public Fish(String name, int price, Seasons season) {
        super(name, price);
        this.season = season;
    }

}
