package org.example.models.enums;

import models.Fish;

public enum Fishes {
    SALAMON("salamon", 75, Seasons.AUTUMN),
    ;//other types
    private String name;
    private int price;
    private Seasons season;

    Fishes(String name, int price, Seasons season) {
        this.name = name;
        this.price = price;
        this.season = season;
    }

    public Fish createFish() {
        return new Fish(name, price, season);
    }

}
