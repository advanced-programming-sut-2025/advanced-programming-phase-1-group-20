package models.enums.Types;

public enum ToolType {
    Normal(5),
    Copper(4),
    Iron(3),
    Gold(2),
    Iridium(1);

    private final int energyDamage;

    ToolType(int energyDamage) {
        this.energyDamage = energyDamage;
    }

    public int getEnergyDamage() {
        return energyDamage;
    }
}
