package org.example.models.Items;

import org.example.models.enums.Types.SeedType;

public class Seed extends Item {
    private SeedType seedType;

    public Seed(SeedType seedType) {
        super(seedType.getName(), seedType.getPrice());
        this.seedType = seedType;
    }
}
