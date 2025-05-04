package org.example.models.Items;


import org.example.models.enums.Seasons;
import org.example.models.enums.Types.SeedType;

public class Seed extends Item {
    private SeedType type;

    public Seed(SeedType type) {
        super(type.getName(), type.getBaseSellPrice());
        this.type = type;
    }

    public Seasons[] getSeason() {
        return type.getSeasons();
    }

    public int getPrice() {
        return super.getPrice();
    }

    public void setPrice(int price) {
        super.setPrice(price);
    }

    @Override
    public void showInfo() {
        type.showInfo();
    }
}
