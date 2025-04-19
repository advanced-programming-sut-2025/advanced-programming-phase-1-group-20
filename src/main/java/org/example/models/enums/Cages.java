package org.example.models.enums;

import models.Cage;

public enum Cages {
    NORMAL_CAGE(4, 1),
    ; // other cages...
    private final int capacity;
    private final int productPerDay;

    Cages(int capacity, int productPerDay) {
        this.capacity = capacity;
        this.productPerDay = productPerDay;
    }

    public Cage createCage() {
        return new Cage(capacity, productPerDay);
    }
}
