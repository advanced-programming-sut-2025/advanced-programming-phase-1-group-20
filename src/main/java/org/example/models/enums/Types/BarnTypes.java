package org.example.models.enums.Types;

import org.example.models.Barn;

public enum BarnTypes {
    NORMAL_BARN(4, 1, "Barn", 6000),
    BIG_BARN(8, 1, "Big Barn", 12000),
    DELUXE_BARN(12, 2, "Deluxe Barn", 25000);

    private final int capacity;
    private final int productPerDay;
    private final String displayName;
    private final int buildCost;

    BarnTypes(int capacity, int productPerDay, String displayName, int buildCost) {
        this.capacity = capacity;
        this.productPerDay = productPerDay;
        this.displayName = displayName;
        this.buildCost = buildCost;
    }

//    public Barn createBarn() {
//        return new Barn(capacity, productPerDay, buildCost);
//    }
c
    public int getCapacity() {
        return capacity;
    }

    public int getProductPerDay() {
        return productPerDay;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getBuildCost() {
        return buildCost;
    }
}