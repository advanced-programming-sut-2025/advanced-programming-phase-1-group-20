package models;

import models.enums.Seasons;
import models.enums.Types.PlantType;

public class Plant extends Item{
    private PlantType type;

    public Plant(PlantType type) {
        super(type.getName(), type.getBaseSell());
        this.type = type;
    }
}
