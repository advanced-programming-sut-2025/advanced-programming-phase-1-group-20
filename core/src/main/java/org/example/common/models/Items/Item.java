package org.example.common.models.Items;

import org.example.common.models.enums.Types.Quality;

public class Item {
    private String name;
    private String imageFilepath;
    private int baseSellPrice;
    private int price;
    private String description;
    private boolean placable;
    private boolean giantable;
    private Quality quality;

    public Item(String name, int baseSellPrice , String imageFilepath) {
        this.name = name;
        this.baseSellPrice = baseSellPrice;
        this.price = baseSellPrice;
        this.placable = false;
        this.giantable = false;
        this.quality = Quality.Normal;
        this.imageFilepath = imageFilepath;
    }


    public Item(String name, int baseSellPrice, String imageFilepath , String description) {
        this.name = name;
        this.baseSellPrice = baseSellPrice;
        this.imageFilepath = imageFilepath;
        this.description = description;
    }

    public boolean isGiantable() {
        return giantable;
    }

    public void setGiantable(boolean giantable) {
        this.giantable = giantable;
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

    public String getImageFilepath() {
        return imageFilepath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return name.equalsIgnoreCase(item.name);
    }

    @Override
    public int hashCode() {
        return name.toLowerCase().hashCode();
    }
}
