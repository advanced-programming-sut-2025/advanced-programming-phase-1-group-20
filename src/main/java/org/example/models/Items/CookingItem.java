package org.example.models.Items;

import org.example.models.Player.Skill;
import org.example.models.enums.PlayerEnums.Skills;

import java.util.HashMap;

public class CookingItem extends Item {
    private String ingredients;
    private int energy;
    private String buffer;
    private String source;

    public CookingItem(String name, String ingredients, String buffer, String source, int baseSellPrice) {
        super(name, baseSellPrice);
        this.ingredients = ingredients;
        this.buffer = buffer;
        this.source = source;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public String getBuffer() {
        return buffer;
    }

    public void setBuffer(String buffer) {
        this.buffer = buffer;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void showInfo(){
        System.out.println("Name: " + this.getName());
        System.out.println("Ingredients: " + ingredients);
        System.out.println("Base Sell Price: " + this.getBaseSellPrice());
        System.out.println("Energy: " + this.getEnergy());
        System.out.println("Buffer: " + this.getBuffer());
        System.out.println("Source: " + this.getSource());
    }
}
