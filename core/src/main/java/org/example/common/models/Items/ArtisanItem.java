package org.example.common.models.Items;

import org.example.common.models.enums.Ingredients;
import org.example.common.models.enums.Types.ArtisanType;

import java.util.Map;

public class ArtisanItem extends Item {
    private ArtisanType type;
    private int proccessingTimeFinal;

    public ArtisanItem(ArtisanType type) {
        super(type.getName(), type.getBaseSellPrice() , type.getImageFilepath());
        this.type = type;
        proccessingTimeFinal = this.getProcessingTime();
        this.setPlacable(true);
        super.setDescription(type.getDescription());
        super.setDescription(type.getDescription());
    }

    public ArtisanType getType() {
        return type;
    }

    public void setType(ArtisanType type) {
        this.type = type;
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

    public Ingredients getIngredient() {
        return type.getIngredients();
    }

    public int getBaseSellPrice() {
        return type.getBaseSellPrice();
    }

    public void setBaseSellPrice(int baseSellPrice) {
        type.setBaseSellPrice(baseSellPrice);
    }

    public String getImage(){
        return "content/Artisanitems/" + type.getImageFilepath() + ".png";
    }

    @Override
    public String getImageFilepath() {
        return "content/Artisanitems/" + type.getImageFilepath() + ".png";
    }

    public int getProccessingTimeFinal() {
        return proccessingTimeFinal;
    }

    public void setProccessingTimeFinal(int proccessingTimeFinal) {
        this.proccessingTimeFinal = proccessingTimeFinal;
    }
}
