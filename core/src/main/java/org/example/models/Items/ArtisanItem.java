package org.example.models.Items;

import org.example.models.enums.Types.ArtisanType;

public class ArtisanItem extends Item {
    private ArtisanType type;

    public ArtisanItem(ArtisanType type) {
        super(type.getName(), type.getBaseSellPrice());
        this.type = type;
        this.setPlacable(true);
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

    public int getProcessingTime() {
        return type.getProcessingTime();
    }

    public void setProccessingTime(int proccessingTime) {
        type.setProccessingTime(proccessingTime);
    }

    public String getIngredient() {
        return type.getIngredient();
    }

    public int getBaseSellPrice() {
        return type.getBaseSellPrice();
    }

    public void setBaseSellPrice(int baseSellPrice) {
        type.setBaseSellPrice(baseSellPrice);
    }
}
