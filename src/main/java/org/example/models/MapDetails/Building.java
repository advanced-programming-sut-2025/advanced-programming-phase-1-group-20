package org.example.models.MapDetails;

import org.example.models.Player.Player;

public class Building {
    private int x;
    private int y;
    private int width;
    private int height;
    private String name;
    private String type; // "house", "shop", "public", etc.
    private Player owner; // null for public buildings
    private boolean isEnterable;
    private String interiorMapName;

    public Building(int x, int y, int width, int height, String name, String type) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.name = name;
        this.type = type;
        this.isEnterable = true;
        this.interiorMapName = name.toLowerCase().replace(" ", "_") + "_interior";
    }

    public Building(int x, int y, int width, int height, String name, String type, Player owner) {
        this(x, y, width, height, name, type);
        this.owner = owner;
    }

    public boolean contains(int targetX, int targetY) {
        return targetX >= x && targetX < x + width && targetY >= y && targetY < y + height;
    }

    public boolean canEnter(Player player) {
        if (!isEnterable) return false;
        if (owner == null) return true;
        return owner.equals(player);
    }

    public String getInteriorMapName() {
        return interiorMapName;
    }

    public void setInteriorMapName(String name) {
        this.interiorMapName = name;
    }

    // Getters
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public boolean isEnterable() {
        return isEnterable;
    }

    public void setEnterable(boolean enterable) {
        isEnterable = enterable;
    }

    public void printBuildingInfo() {
        //..
    }
}