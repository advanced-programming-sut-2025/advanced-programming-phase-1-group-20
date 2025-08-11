package org.example.common.models.Items;

import org.example.common.models.enums.Types.SpecialItemType;

public class SpecialItem extends Item {
    private SpecialItemType type;

    public SpecialItem(SpecialItemType type) {
        super(type.getName(), type.getBaseSellPrice(), type.getImageFilepath());
        this.type = type;
        this.setDescription(type.getDescription());
    }

    public SpecialItemType getType() {
        return type;
    }

    public void setType(SpecialItemType type) {
        this.type = type;
    }

    public String getName() {
        return type.getName();
    }

    public String getDescription() {
        return type.getDescription();
    }

    public int getBaseSellPrice() {
        return type.getBaseSellPrice();
    }

    public String getImageFilepath() {
        return type.getImageFilepath();
    }
}
