package models;

import models.enums.ArtisanItems;

import java.util.HashMap;
import java.util.List;

public class ArtisanItem extends Item{
    private ArtisanItems type;
    public ArtisanItem(ArtisanItems type) {
        super(type.getName(), type.getSellPrice());
        this.type = type;
    }
}
