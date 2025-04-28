package org.example.models.Items;


import org.example.models.enums.Types.MineralType;

public class Mineral extends Item {
    private MineralType type;
    public Mineral(MineralType type) {
        super(type.getName(), type.getBaseSellPrice());
        this.type = type;
    }

    public String getDescription() {
        return type.getDescription();
    }

    @Override
    public void showInfo(){
        System.out.println("Name: " + this.getName());
        System.out.println("Base Sell Price: " + this.getPrice());
        System.out.println("Description: " + getDescription());
    }
}
