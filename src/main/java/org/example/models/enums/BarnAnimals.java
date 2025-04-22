package org.example.models.enums;

import org.example.models.entities.animal.BarnAnimal;

public enum BarnAnimals {
    SHEEP("sheep", false, 1, null, 500),
    ;//other animals
    private final String name;
    private final boolean barn;
    private final int speed;
    private final Cages cage;
    private final int price;

    BarnAnimals(String name, boolean barn, int speed, Cages cage, int price) {
        this.name = name;
        this.barn = barn;
        this.speed = speed;
        this.cage = cage;
        this.price = price;
    }

    public BarnAnimal createAnimal() {
        return new BarnAnimal(name, barn, speed, cage, price);
    }
}
