package org.example.models.enums;

public enum Fishes {
    SALMON("salmon", 75, Seasons.AUTUMN),
    ;//other types
    private String name;
    private int price;
    private Seasons season;

    Fishes(String name, int price, Seasons season) {
        this.name = name;
        this.price = price;
        this.season = season;
    }
}
