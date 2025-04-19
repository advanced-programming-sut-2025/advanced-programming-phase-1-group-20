package org.example.models;

import org.example.models.enums.Types.PlantType;

public class Plant extends Item {
    private PlantType type;

    public Plant(PlantType type) {
        super(type.getName(), type.getBaseSell());
        this.type = type;
    }
}
