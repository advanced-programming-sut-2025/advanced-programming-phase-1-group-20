package models.Items;

import models.Item;
import models.enums.ArtisanItems;

public class ArtisanItem extends Item {
    private ArtisanItems type;
    public ArtisanItem(ArtisanItems type) {
        super(type.getName(), type.getSellPrice());
        this.type = type;
    }
}
