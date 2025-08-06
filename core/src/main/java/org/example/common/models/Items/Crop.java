package org.example.common.models.Items;

import com.badlogic.gdx.graphics.Texture;
import org.example.common.models.enums.Seasons;
import org.example.common.models.enums.Types.CropType;

public class Crop extends Item {
    private CropType type;

    public Crop(CropType type) {
        super(type.getName(), type.getBaseSellPrice() , type.getImageFilePath());
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

    public boolean getFinished() {
        return true;
    }

    public Fruit getFruit() {
        return new Fruit(getName() , getPrice() , getEnergy() , getImageFilepath());
    }

    public String getImage() {
        return "content/Crops/" + getImageFilepath() + ".png";
    }

    public CropType getType() {
        return type;
    }

    public Texture getTexture() {
        return new Texture(getImage());
    }
}
