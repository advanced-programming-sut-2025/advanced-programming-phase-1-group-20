package org.example.client.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import org.example.common.models.enums.Types.CoopAnimalTypes;

public class AnimalSpriteController {
    private final Texture textureSheet;
    private final int FRAME_W;
    private final int FRAME_H;
    private static final float FRAME_DURATION = 0.2f;


    // Animations
    private final Animation<TextureRegion> moveUp;
    private final Animation<TextureRegion> moveDown;
    private final Animation<TextureRegion> moveLeft;
    private final Animation<TextureRegion> moveRight;
    private final Animation<TextureRegion> doSomething;

    private final Animation<TextureRegion> currentAnimation;
    private float stateTime = 0f;

    private enum Dir {DOWN, LEFT, RIGHT, UP}
    private Dir facing = Dir.DOWN;

    AnimalSpriteController(String animalName) {
        textureSheet = new Texture(Gdx.files.internal("content/Animals/" + animalName + ".png"));
        CoopAnimalTypes coopAnimalType = CoopAnimalTypes.fromName(animalName);
        int numOfHeight;
        if (coopAnimalType != null) {
            numOfHeight = 7;
        } else {
            numOfHeight = 5;
        }

        FRAME_H = textureSheet.getHeight() / numOfHeight;
        FRAME_W = textureSheet.getWidth() / 4;

        TextureRegion[][] grid = TextureRegion.split(textureSheet, FRAME_W, FRAME_H);
        moveDown = buildAnim(grid[0]);
        moveRight = buildAnim(grid[1]);
        moveUp = buildAnim(grid[2]);
        moveLeft = buildAnim (grid[3]);
        doSomething = buildAnim(grid[5]);

        currentAnimation = doSomething;
    }

    private static Animation<TextureRegion> buildAnim(TextureRegion[] row) {
        Array<TextureRegion> frames = new Array<>(3);
        for (int i = 0; i < 3; i++) {
            frames.add(row[i]);
        }
        return new Animation<>(FRAME_DURATION, frames, Animation.PlayMode.LOOP_PINGPONG);
    }
}
