package org.example.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import org.example.Main;
import org.example.models.MapDetails.Farm;
import org.example.models.Player.Player;

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
    }

    private void handlePlayerInput(float delta) {
        float newX = player.getPosX();
        float newY = player.getPosY();

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            newX -= player.getSpeed();
            if (isWalkable(newX, newY)) {
                player.setPosX(newX);
                facing = Dir.LEFT;
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            newX += player.getSpeed();
            if (isWalkable(newX, newY)) {
                player.setPosX(newX);
                facing = Dir.RIGHT;
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            newY += player.getSpeed();
            if (isWalkable(newX, newY)) {
                player.setPosY(newY);
                facing = Dir.UP;
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            newY -= player.getSpeed();
            if (isWalkable(newX, newY)) {
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
