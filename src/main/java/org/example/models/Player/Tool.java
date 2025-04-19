package org.example.models.Player;

import org.example.models.Item;
import org.example.models.enums.Types.ToolType;


public class Tool extends Item {
    int level = 0;
    int energyCost;
    private ToolType type;
    private int health;

    public Tool(String name, int energyCost) {
        super(name, 10);
        this.energyCost = energyCost;
        this.health = 100;
    }

    public void upgrade() {
        if (this.type == ToolType.Iridium) {
            System.out.println("This tool is already at max level");
        } else {
            this.level++;
            this.type = ToolType.values()[this.level];
        }
    }

    public void use() {
//        App.getCurrentPlayer().getEnergy().decreaseEnergy(energyCost);
        this.health -= type.getEnergyDamage();
    }
}
