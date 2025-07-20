package org.example.client.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import org.example.Main;
import org.example.common.models.MapDetails.Farm;
import org.example.common.models.Player.Player;
import org.example.common.models.common.Location;

public class PlayerController {

    private static final int FRAME_W = 16;
    private static final int FRAME_H = 32;
    private static final int RENDER_W = 48;
    private static final int RENDER_H = 96;
    private static final float FRAME_DURATION = 0.15f;

    private final Player player;
    private final Farm farm;

    private final Animation<TextureRegion> walkDown;
    private final Animation<TextureRegion> walkLeft;
    private final Animation<TextureRegion> walkRight;
    private final Animation<TextureRegion> walkUp;

    private Animation<TextureRegion> currentAnim;
    private float stateTime = 0f;

    private enum Dir {DOWN, LEFT, RIGHT, UP}

    private Dir facing = Dir.DOWN;

    private float toolAnimTime = 0f;
    private boolean toolSwinging = false;
    private String lastToolDirection = "down";
    private float lastMouseX = 0f;
    private float lastMouseY = 0f;

    public void triggerToolSwing(String direction, float mouseX, float mouseY) {
        toolAnimTime = 0f;
        toolSwinging = true;
        lastToolDirection = direction;
        lastMouseX = mouseX;
        lastMouseY = mouseY;
    }

    public PlayerController(Player player, Farm farm) {
        this.player = player;
        this.farm = farm;

        Texture sheet = player.getTextureSheet();
        TextureRegion[][] grid = TextureRegion.split(sheet, FRAME_W, FRAME_H);

        walkDown = buildAnim(grid[0]);
        walkLeft = buildAnim(grid[3]);
        walkRight = buildAnim(grid[1]);
        walkUp = buildAnim(grid[2]);

        currentAnim = walkDown;
    }

    private static Animation<TextureRegion> buildAnim(TextureRegion[] row) {
        Array<TextureRegion> frames = new Array<>(3);
        for (int i = 0; i < 3; i++) {
            frames.add(row[i]);
        }
        return new Animation<>(FRAME_DURATION, frames, Animation.PlayMode.LOOP_PINGPONG);
    }

    public void update() {
        float delta = Gdx.graphics.getDeltaTime();
        handlePlayerInput(delta);

        stateTime += delta;

        TextureRegion frame = currentAnim.getKeyFrame(stateTime, true);
        Main.getBatch().draw(
            frame,
            player.getPosX(),
            player.getPosY(),
            RENDER_W,
            RENDER_H
        );

        // Draw tool if equipped
        if (player.getCurrentTool() != null) {
            Texture toolTexture = new Texture(player.getCurrentTool().getImageFilepath());
            float playerX = player.getPosX();
            float playerY = player.getPosY();
            float centerX = playerX + RENDER_W / 2f;
            float centerY = playerY + RENDER_H / 2f;
            float toolW = 32, toolH = 32;
            // Set origin to handle (bottom-middle)
            float originX = toolW / 2f;
            float originY = toolH * 0.85f;
            // Calculate angle to mouse
            float dx = lastMouseX - centerX;
            float dy = lastMouseY - centerY;
            float angle = (float)Math.toDegrees(Math.atan2(dy, dx));
            // Swing effect
            float swingDuration = 0.18f;
            float swingArc = 60f; // degrees
            float finalAngle = angle;
            if (toolSwinging) {
                float swingProgress = Math.min(toolAnimTime / swingDuration, 1f);
                // Animate from -swingArc to +swingArc around the mouse direction
                float swingOffset = (float)Math.sin(Math.PI * swingProgress - Math.PI/2) * swingArc;
                finalAngle = angle + swingOffset;
                toolAnimTime += Gdx.graphics.getDeltaTime();
                if (toolAnimTime > swingDuration) toolSwinging = false;
            }
            // Mirror if mouse is to the left
            boolean flip = (angle > 90f || angle < -90f);
            Main.getBatch().draw(
                toolTexture,
                centerX - originX, centerY - originY,
                originX, originY,
                toolW, toolH,
                1f, 1f,
                finalAngle,
                0, 0,
                toolTexture.getWidth(), toolTexture.getHeight(),
                flip, false
            );
        }
    }

    private void handlePlayerInput(float delta) {
        float newX = player.getPosX();
        float newY = player.getPosY();
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            newX -= player.getSpeed();
            if (isWalkable(newX / 60, newY / 60)) {
                player.setPosX(newX);
                facing = Dir.LEFT;
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            newX += player.getSpeed();
            if (isWalkable(newX / 60, newY / 60)) {
                player.setPosX(newX);
                facing = Dir.RIGHT;
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            newY += player.getSpeed();
            if (isWalkable(newX /60, newY / 60)) {
                player.setPosY(newY);
                facing = Dir.UP;
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            newY -= player.getSpeed();
            if (isWalkable(newX / 60, newY / 60)) {
                player.setPosY(newY);
                facing = Dir.DOWN;
            }
        }

        // Update current animation based on facing direction
        switch (facing) {
            case UP -> currentAnim = walkUp;
            case DOWN -> currentAnim = walkDown;
            case LEFT -> currentAnim = walkLeft;
            case RIGHT -> currentAnim = walkRight;
        }
    }

    private boolean isWalkable(float x, float y) {
        if(!farm.contains((int) x, (int) y)) {
            return false;
        }
        Location loc = farm.getItem((int) x, (int) y);
        if(!loc.getTile().isWalkable()){
            return false;
        }
        return true;
    }

    public Player getPlayer() {
        return player;
    }

    public TextureRegion getCurrentFrame() {
        return currentAnim.getKeyFrame(stateTime, true);
    }

    public Dir getFacing() {
        return facing;
    }
}
