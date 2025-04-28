package org.example.models.Items;

import org.example.models.enums.Seasons;
import org.example.models.enums.Types.CropType;

import java.util.Arrays;

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
    public void showInfo(){
        System.out.println("Name: " + this.getName());
        System.out.println("Base Sell Price: " + this.getBaseSellPrice());
        String season = Arrays.toString(getSeasons()).replace("[", "").replace("]", "")
                .replace(" " , "");
        System.out.println("Season: " + season);
        System.out.println("Energy: " + getEnergy());
    }
}
