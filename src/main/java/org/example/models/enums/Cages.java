package org.example.models.enums;

import org.example.models.Cage;

public enum Cages {
    NORMAL_CAGE(4, 1),
    ; // other cages...
    // TODO: add other cages
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
