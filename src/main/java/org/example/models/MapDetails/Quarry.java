package org.example.models.MapDetails;

public class Quarry {
    private int x;
    private int y;
    private static final int height = 4;
    private static final int width = 4;

    public Quarry(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
