package org.example.models;

import org.example.models.enums.Types.ForagingType;

public class Foraging extends Item {
    private ForagingType type;

    public Foraging(ForagingType type) {
        super(type.getName(), type.getSellPrice());
        this.type = type;
    }
}
