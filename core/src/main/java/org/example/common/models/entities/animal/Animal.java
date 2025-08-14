// main/java/org/example/common/models/entities/animal/Animal.java
package org.example.common.models.entities.animal;

import org.example.common.models.Items.Item;

public class Animal extends Item {
    private String name;
    private int price;

    private boolean isOutSide = false;

    private float posX = 0;
    private float posY = 0;
    private float speed = 50f;


    private boolean isMoving = false;
    private float targetX;
    private float targetY;
    private float stateTimer = 0f;
    public enum Direction { DOWN, LEFT, RIGHT, UP }
    private Direction facing = Direction.DOWN;

    // Current sprite sheet name in assets/content/Animals (e.g., "Sheep" or "Sheep_sheared")
    private String spriteName;

    public Animal(String name, int price) {
        super(name, price, "");
        this.name = name;
        this.price = price;
        this.stateTimer = (float) (Math.random() * 5);
        this.targetX = posX;
        this.targetY = posY;
        this.spriteName = name;
    }

    public float getPosX() {
        return posX;
    }

    public boolean isOutSide() {
        return isOutSide;
    }

    public void setOutSide(boolean outSide) {
        isOutSide = outSide;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void setMoving(boolean moving) {
        isMoving = moving;
    }

    public float getTargetX() {
        return targetX;
    }

    public void setTargetX(float targetX) {
        this.targetX = targetX;
    }

    public float getTargetY() {
        return targetY;
    }

    public void setTargetY(float targetY) {
        this.targetY = targetY;
    }

    public float getStateTimer() {
        return stateTimer;
    }

    public void setStateTimer(float stateTimer) {
        this.stateTimer = stateTimer;
    }

    public Direction getFacing() {
        return facing;
    }

    public void setFacing(Direction facing) {
        this.facing = facing;
    }

    public String getSpriteName() {
        return spriteName != null ? spriteName : name;
    }

    public void setSpriteName(String spriteName) {
        this.spriteName = spriteName;
    }


}
