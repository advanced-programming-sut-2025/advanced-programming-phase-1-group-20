package org.example.models.Items;

import org.example.models.enums.Seasons;

public class Crop extends Item {
    private Seasons[] seasons;
    private int energy;
    public Crop(String name, int baseSellPrice , Seasons[] seasons, int energy) {
        super(name, baseSellPrice);
        this.seasons = seasons;
        this.energy = energy;
    }

    public Seasons[] getSeasons() {
        return seasons;
    }

    public void setSeasons(Seasons[] seasons) {
        this.seasons = seasons;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }
}
