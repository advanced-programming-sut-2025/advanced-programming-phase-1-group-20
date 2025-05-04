package org.example.models.Items;

import org.example.models.enums.Types.ArtisanType;

public class ArtisanItem extends Item {
    private ArtisanType type;

    public ArtisanItem(ArtisanType type) {
        super(type.getName(), type.getBaseSellPrice());
        this.type = type;
    }


    public String getName() {
        return type.getName();
    }

    public String getDescription() {
        return type.getDescription();
    }

    public int getEnergy() {
        return type.getEnergy();
    }

    public void setEnergy(int energy) {
        type.setEnergy(energy);
    }

    public int getProccessingTime() {
        return type.getProcessingTime();
    }

    public String getIngridient() {
        return type.getIngredient();
    }

    public int getBaseSellPrice() {
        return type.getBaseSellPrice();
    }

    public void setBaseSellPrice(int baseSellPrice) {
        type.setBaseSellPrice(baseSellPrice);
    }


    public void setProccessingTime(int proccessingTime) {
        type.setProccessingTime(proccessingTime);
    }
}
