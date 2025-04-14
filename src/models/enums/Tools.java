package models.enums;

import models.Tool;

public enum Tools {
    HOE_NORMAL("hoe" , 5),
    PICK_AXE_NORMAL("pick axe" , 5),
    AXE_NORMAL("axe" , 5),
    SEYTHE("Seythe" , 2),
    SHEAR("Shear" , 4),
    MILK_PAIL("milk pail" , 4),
    FISHING_POLE_NORMAL("fishing pole" , 8),
    WATERING_CAN_NORMAL("watering can" , 5); //other tools

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
