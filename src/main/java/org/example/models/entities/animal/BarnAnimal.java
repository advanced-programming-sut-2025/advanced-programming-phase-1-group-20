package org.example.models.entities.animal;

import org.example.models.enums.Types.BarnAnimalTypes;

public class BarnAnimal extends Animal {
    private BarnAnimalTypes type;
    private int happinessLevel;

    public BarnAnimal(BarnAnimalTypes type) {
        super(type.getName(), type.getPrice());
        this.type = type;
    }

    public int getHappiness() {
        return happinessLevel;
    }

    public String getSecondaryProduct() {
        return type.getSecondaryProduct();
    }

    public String getPrimaryProduct() {
        return type.getPrimaryProduct();
    }

    public int getSecondaryProductPrice() {
        return type.getSecondaryProductPrice();
    }

    public int getPrimaryProductPrice() {
        return type.getPrimaryProductPrice();
    }
}
