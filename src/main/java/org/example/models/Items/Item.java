package org.example.models.Items;

import org.example.models.enums.Types.Quality;

public class Item {
    private String name;
    private int baseSellPrice;
    private int price;
    private String description;
    private boolean placable;
    private boolean giantable;
    private Quality quality;

    public boolean isGiantable() {
        return giantable;
    }

    public void setGiantable(boolean giantable) {
        this.giantable = giantable;
    }

    public Item(String name, int baseSellPrice) {
        this.name = name;
        this.baseSellPrice = baseSellPrice;
        this.price = baseSellPrice;
        this.placable = false;
        this.giantable = false;
        this.quality = Quality.Normal;
    }

    public Item(String name, int baseSellPrice, String description) {
        this.name = name;
        this.baseSellPrice = baseSellPrice;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBaseSellPrice() {
        return baseSellPrice;
    }

    public void setBaseSellPrice(int baseSellPrice) {
        this.baseSellPrice = baseSellPrice;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void showInfo() {

    }

    public void updateItem() {
    }

    public boolean getFinished() {
        return false;
    }

    public boolean isPlacable() {
        return placable;
    }

    public void setPlacable(boolean placable) {
        this.placable = placable;
    }

    public Quality getQuality() {
        return quality;
    }

    public void setQuality(Quality quality) {
        this.quality = quality;
    }
}
