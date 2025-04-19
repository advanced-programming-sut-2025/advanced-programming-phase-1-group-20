package org.example.models.enums.PlayerEnums;

import models.Player.Tool;

public enum Tools {
    HOE("hoe", 5),
    PICK_AXE("pick axe", 5),
    AXE("axe", 5),
    SEYTHE("Seythe", 2),
    SHEAR("Shear", 4),
    MILK_PAIL("milk pail", 4),
    FISHING_ROD("fishing rod", 8),
    WATERING_CAN("watering can", 5); //other tools

    private final String name;
    private final int energyCost;

    Tools(String name, int energyCost) {
        this.name = name;
        this.energyCost = energyCost;
    }

    public Tool createTool() {
        return new Tool(name, energyCost);
    }
}
