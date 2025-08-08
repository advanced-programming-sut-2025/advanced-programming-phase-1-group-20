package org.example.common.models.Items;


import com.badlogic.gdx.graphics.Texture;
import org.example.common.models.Player.Backpack;
import org.example.common.models.entities.animal.Fish;
import org.example.common.models.enums.Ingredients;
import org.example.common.models.enums.Seasons;
import org.example.common.models.enums.Types.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CraftingItem extends Item {
    private CraftingType type;
    private ArtisanItem proccessingItem;
    private ArtisanItem finishedItem;
    private double progressBar;
    private int posX;
    private int posY;


    public CraftingItem(CraftingType type) {
        super(type.getName(), type.getBaseSellPrice() , type.getImageFilepath());
        this.type = type;
        this.proccessingItem = null;
        this.setPlacable(true);
        progressBar = 0;
        posX = 0;
        posY = 0;
    }

    public Ingredients getIngredients() {
        return type.getIngredients();
    }

    public String getSource() {
        return type.getSource();
    }


    public boolean canCraft(Backpack inventory) {
        return type.getIngredients().checkRecipe(inventory);
    }

    public ArtisanItem createArtisanItem(Backpack inventory , ArtisanItem artisanItem) {
        if(type.getArtisanItems().contains(artisanItem)) {
            if(artisanItem.getIngredient().checkRecipe(inventory)){
                return artisanItem;
            }
        }
        return null;
    }


    public boolean processItem(ArtisanItem item) {
        if(proccessingItem == null) {
            proccessingItem = item;
            return true;
        }
        return false;
    }

    public void updateArtisan() {
        if (proccessingItem != null) {
            int proccessingTime = proccessingItem.getProcessingTime();
            if (proccessingTime > 1) {
                proccessingItem.setProccessingTime(proccessingTime - 1);
                progressBar = 1 - (double) proccessingItem.getProcessingTime() / proccessingItem.getProccessingTimeFinal();
            } else {
                finishedItem = proccessingItem;
                proccessingItem = null;
                progressBar = 0;
            }
        }
    }

    public void fastFinishArtisan() {
        if (proccessingItem != null) {
            finishedItem = proccessingItem;
            proccessingItem = null;
            progressBar = 0;
        }
    }

    public void setProccessingItem(ArtisanItem proccessingItem) {
        this.proccessingItem = proccessingItem;
    }

    public void setFinishedItem(ArtisanItem finishedItem) {
        this.finishedItem = finishedItem;
    }

    public void cancelArtisan() {
        if (proccessingItem != null) {
            proccessingItem = null;
        }
    }

    public Item getFinishedItem() {
        if (finishedItem != null) {
            Item clone = finishedItem;
            finishedItem = null;
            return clone;
        }
        return null;
    }

    public Item getProccessingItem() {
        return proccessingItem;
    }


    @Override
    public void showInfo() {
        type.showInfo();
    }

    public String getImage(){
        return type.getImageFilepath();
    }

    @Override
    public String getImageFilepath() {
        return "content/CraftingItems/" + type.getImageFilepath() + ".png";
    }

    public Texture getTexture() {
        return new Texture(getImage());
    }

    public double getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(double progressBar) {
        this.progressBar = progressBar;
    }

    public CraftingType getType() {
        return type;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }
}
