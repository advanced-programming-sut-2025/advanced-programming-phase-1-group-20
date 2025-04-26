package org.example.models.enums;

import org.example.models.App;
import org.example.models.Items.Seed;

import java.util.Arrays;
import java.util.List;

public enum Seasons {
    SPRING(new String[]{"Cauliflower", "Parsnip", "Potato", "Blue Jazz", "Tulip"}),
    SUMMER(new String[]{"Corn", "Hot Pepper" , "Radish" , "Wheat", "Poppy", "Sunflower" , "Summer Spangle"}),
    AUTUMN(new String[]{"Artichoke" , "Corn" , "Eggplant" , "Pumpkin", "Sunflower" , "Fairy Rose"}),
    WINTER(new String[]{"Powdermelon"});
    private final String[] mixedSeeds;

    Seasons(String[] seeds) {
        this.mixedSeeds = seeds;
    }


    public String[] getSeeds() {
        return mixedSeeds;
    }

}
