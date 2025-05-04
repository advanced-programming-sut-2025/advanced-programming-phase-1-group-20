package org.example.models.Items;

import org.example.models.enums.Seasons;
import org.example.models.enums.Types.CropType;

public class Crop extends Item {
    private CropType type;

    public Crop(CropType type) {
        super(type.getName(), type.getBaseSellPrice());
        this.type = type;
    }

    public Seasons[] getSeasons() {
        return type.getSeasons();
    }

    public int getEnergy() {
        return type.getEnergy();
    }


    @Override
    public void showInfo() {
        type.showInfo();
    }
}
