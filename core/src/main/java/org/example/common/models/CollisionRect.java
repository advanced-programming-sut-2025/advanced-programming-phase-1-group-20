package org.example.common.models;

public class CollisionRect {
    float x, y;
    float width, height;
    public CollisionRect(float x, float y, float width, float height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void move(float x, float y){
        this.x = x;
        this.y = y;
    }

    public boolean collidesWith(CollisionRect rect) {
        return x < rect.x + rect.width &&
                x + width > rect.x &&
                y < rect.y + rect.height &&
                y + height > rect.y;
    }
}
