package org.example.common.models.entities.animal;// main/java/org/example/common/models/entities/animal/Animal.java
// ... (imports)

import org.example.common.models.Items.Item;

public class Animal extends Item {
    private String name;
    private int price;

    // NEW FIELDS FOR GRAPHICS
    private float posX = 0;
    private float posY = 0;
    private float speed = 50f; // 0.8 tiles per second
    private boolean isMoving = false;
    private String currentAnimation = "idle"; // idle, walk

    public Animal(String name, int price) {
        //TODO : adding correct file path
        super(name , price , "");
        this.name = name;
        this.price = price;
    }

    // NEW GETTERS AND SETTERS
    public float getPosX() { return posX; }
    public void setPosX(float posX) { this.posX = posX; }
    public float getPosY() { return posY; }
    public void setPosY(float posY) { this.posY = posY; }
    public float getSpeed() { return speed; }
    public void setSpeed(float speed) { this.speed = speed; }
    public boolean isMoving() { return isMoving; }
    public void setMoving(boolean moving) { isMoving = moving; }
    public String getCurrentAnimation() { return currentAnimation; }
    public void setCurrentAnimation(String currentAnimation) { this.currentAnimation = currentAnimation; }
}
