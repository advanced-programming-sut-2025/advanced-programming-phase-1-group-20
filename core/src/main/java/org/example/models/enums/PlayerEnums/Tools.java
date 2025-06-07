package org.example.models.enums.PlayerEnums;

public enum Tools {
    HOE("hoe", 5),
    PICK_AXE("pick axe", 5),
    AXE("axe", 5),
    SCYTHE("Scythe", 2),
    SHEAR("Shear", 4),
    MILK_PAIL("milk pail", 4),
    FISHING_ROD("fishing rod", 8),
    WATERING_CAN("watering can", 5);

    private final String name;
    private final int energyCost;

    Tools(String name, int energyCost) {
        this.name = name;
        this.energyCost = energyCost;
    }

//    public Tool createTool() {
//        return new Tool(name, energyCost);
//    }
}
