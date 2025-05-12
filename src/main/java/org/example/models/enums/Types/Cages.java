package org.example.models.enums.Types;

import org.example.models.Coop;

public enum Cages {
    NORMAL_COOP(4, 1, "Coop", 4000),
    BIG_CAGE(8, 1, "Big Coop", 10000),
    DELUXE_CAGE(12, 2, "Deluxe Coop", 20000);

    private final int capacity;
    private final int productPerDay;
    private final String displayName;
    private final int buildCost;

    Cages(int capacity, int productPerDay, String displayName, int buildCost) {
        this.capacity = capacity;
        this.productPerDay = productPerDay;
        this.displayName = displayName;
        this.buildCost = buildCost;
    }

//    public Coop createCage() {
//        return new Coop(capacity, productPerDay, buildCost);
//    }
    //TODO: کامنتش کردم بعدا باید درستش کرد همینه که هست :)

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