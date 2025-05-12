package org.example.models.MapDetails;

import org.example.models.Player.Player;
import org.example.models.Player.Refrigerator;

public class Building {
    private int x;
    private int y;
    private static final int height = 5;
    private static final int width = 5;
    private final String name;
    private final String type;
    private Player owner;
    private boolean isEnterable;
    private String interiorMapName;
    private final Refrigerator refrigerator = new Refrigerator();

    public Building(int x, int y, String name, String type) {
        this.x = x;
        this.y = y;
        this.name = name;
        this.type = type;
        this.isEnterable = true;
        this.interiorMapName = name.toLowerCase().replace(" ", "_") + "_interior";
    }

    public Building(int x, int y, String name, String type, Player owner) {
        this(x, y, name, type);
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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public static int getWidth() {
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

    public Refrigerator getRefrigerator() {
        return refrigerator;
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