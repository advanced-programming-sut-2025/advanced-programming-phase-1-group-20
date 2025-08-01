package org.example.common.models.Items;

import org.example.common.models.Player.Backpack;
import org.example.common.models.entities.animal.Fish;
import org.example.common.models.enums.Ingredients;
import org.example.common.models.enums.Types.CookingType;
import org.example.common.models.enums.Types.ItemBuilder;

import java.util.HashSet;
import java.util.Map;

public class CookingItem extends Item {
    private CookingType type;

    public CookingItem(CookingType type) {
        super(type.getName(), type.getBaseSellPrice() , type.getImageFilepath());
        this.type = type;
    }

    public Ingredients getIngredients() {
        return type.getIngredient();
    }

    public int getEnergy() {
        return type.getEnergy();
    }


    public String getBuffer() {
        return type.getBuffer();
    }


    public String getSource() {
        return type.getSource();
    }


    public boolean canCook(Backpack inventory) {
        return type.getIngredient().checkRecipe(inventory);
    }

    public Food cook(Backpack inventory) {
        return new Food(getName(), getBaseSellPrice(), getEnergy(), getBuffer() , getImageFilepath());
    }

    public Food getFood() {
        return new Food(getName(), getBaseSellPrice(), getEnergy(), getBuffer() , getImageFilepath());
    }




    public void showInfo() {
        type.showInfo();
    }

    public String getImage(){
        return "content/Cookingitems/" + type.getImageFilepath() + ".png";
    }
    @Override
    public String getImageFilepath() {
        return "content/Cookingitems/" + type.getImageFilepath() + ".png";
    }
}
